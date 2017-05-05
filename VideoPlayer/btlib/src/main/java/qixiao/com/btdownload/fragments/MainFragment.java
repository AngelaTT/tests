/*
 * Copyright (C) 2016 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of LibreTorrent.
 *
 * LibreTorrent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibreTorrent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibreTorrent.  If not, see <http://www.gnu.org/licenses/>.
 */

package qixiao.com.btdownload.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import qixiao.com.btdownload.*;

import qixiao.com.btdownload.AddTorrentActivity;
import qixiao.com.btdownload.DetailTorrentActivity;
import qixiao.com.btdownload.adapters.ToolbarSpinnerAdapter;
import qixiao.com.btdownload.adapters.TorrentListAdapter;
import qixiao.com.btdownload.constants.btConstant;
import qixiao.com.btdownload.core.Torrent;
import qixiao.com.btdownload.core.TorrentStateCode;
import qixiao.com.btdownload.core.sorting.TorrentSorting;
import qixiao.com.btdownload.core.sorting.TorrentSortingComparator;
import qixiao.com.btdownload.core.stateparcel.TorrentStateParcel;
import qixiao.com.btdownload.core.TorrentTaskServiceIPC;
import qixiao.com.btdownload.core.exceptions.FileAlreadyExistsException;
import qixiao.com.btdownload.core.utils.DialogUtil;
import qixiao.com.btdownload.core.utils.RecyclerDistanceUtil;
import qixiao.com.btdownload.core.utils.SPUtils;
import qixiao.com.btdownload.core.utils.Utils;
import qixiao.com.btdownload.customviews.EmptyRecyclerView;
import qixiao.com.btdownload.customviews.RecyclerViewDividerDecoration;
import qixiao.com.btdownload.customviews.speedBottomHelpView;
import qixiao.com.btdownload.dialogs.BaseAlertDialog;
import qixiao.com.btdownload.dialogs.ErrorReportAlertDialog;
import qixiao.com.btdownload.dialogs.filemanager.FileManagerConfig;
import qixiao.com.btdownload.dialogs.filemanager.FileManagerDialog;
import qixiao.com.btdownload.receivers.NotificationReceiver;
import qixiao.com.btdownload.services.TorrentTaskService;
import qixiao.com.btdownload.settings.BTSettingActivity;
import qixiao.com.btdownload.settings.SettingConstant;
import qixiao.com.btdownload.settings.SettingsManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/*
 * The list of torrents.
 */

public class MainFragment extends Fragment
        implements
        TorrentListAdapter.ViewHolder.ClickListener,
        BaseAlertDialog.OnClickListener,
        BaseAlertDialog.OnDialogShowListener
{

    private static MainFragment instance = null;
    public static MainFragment getInstance(){
        if (instance == null){
            instance = new MainFragment();
        }
        return instance;
    }
    @SuppressWarnings("unused")
    private static final String TAG = MainFragment.class.getSimpleName();

    private static final String TAG_PREV_IMPL_INTENT = "prev_impl_intent";
    private static final String TAG_SELECTABLE_ADAPTER = "selectable_adapter";
    private static final String TAG_SELECTED_TORRENTS = "selected_torrents";
    private static final String TAG_IN_ACTION_MODE = "in_action_mode";
    private static final String TAG_DELETE_TORRENT_DIALOG = "delete_torrent_dialog";
    private static final String TAG_ADD_LINK_DIALOG = "add_link_dialog";
    private static final String TAG_SAVE_ERROR_DIALOG = "save_error_dialog";
    private static final String TAG_TORRENTS_LIST_STATE = "torrents_list_state";
    private static final String TAG_ABOUT_DIALOG = "about_dialog";
    private static final String TAG_TORRENT_SORTING = "torrent_sorting";

    private AppCompatActivity activity;
    private Toolbar toolbar;
    private FloatingActionMenu addTorrentButton;
    private FloatingActionButton openFileButton;
    private FloatingActionButton addLinkButton;
    private SearchView searchView;
    private CoordinatorLayout coordinatorLayout;
    private LinearLayoutManager layoutManager;
    private EmptyRecyclerView torrentsList;
    /* Save state scrolling */
    private Parcelable torrentsListState;
    private Map<String, TorrentStateParcel> torrentStates = new HashMap<>();
    private TorrentListAdapter adapter;
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private boolean inActionMode = false;
    private ArrayList<String> selectedTorrents = new ArrayList<>();
    private ToolbarSpinnerAdapter spinnerAdapter;
    private RelativeLayout spinner;
    private boolean addTorrentMenu = false;

    /* Prevents re-adding the torrent, obtained through implicit intent */
    private Intent prevImplIntent;
    /* Messenger for communicating with the service. */
    private Messenger serviceCallback = null;
    private Messenger clientCallback = new Messenger(new CallbackHandler(this));
    private TorrentTaskServiceIPC ipc = new TorrentTaskServiceIPC();
    /* Flag indicating whether we have called bind on the service. */
    private boolean bound;
    private ReentrantLock sync;
    /*
     * Torrents are added to the queue, if the client is not bounded to service.
     * Trying to add torrents will be made at the first connect.
     */
    private HashSet<Torrent> torrentsQueue = new HashSet<>();

    private Throwable sentError;
    private DrawerLayout drawerLayout;
    private LinearLayout ll_drawer;
    private ImageView iv_all;
    private TextView tv_all;
    private ImageView iv_downloadering;
    private TextView tv_downloadering;
    private ImageView iv_finshed;
    private TextView tv_finshed;
    private ImageView iv_paused;
    private TextView tv_paused;
    private ImageView iv_resource;
    private TextView tv_resource;
    private ImageView iv_setting;
    private TextView tv_setting;
    private TextView tv_fenlei;
    private ImageView iv_fenlei;
    private ImageView iv_genduo;
    private DialogUtil dialogUtil;
    private LinearLayout ll_add;
    private LinearLayout none;
    private TextView tv_none;
    private RelativeLayout rl_all_down;
    private speedBottomHelpView speedBottomHelpView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.main_coordinator_layout);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        drawerLayout = ((DrawerLayout) v.findViewById(R.id.drawerLayout));
        ll_drawer = ((LinearLayout) v.findViewById(R.id.ll_drawer));
        iv_all = ((ImageView) v.findViewById(R.id.iv_all));
        tv_all = ((TextView) v.findViewById(R.id.tv_all));
        iv_downloadering = ((ImageView) v.findViewById(R.id.iv_downloadering));
        tv_downloadering = ((TextView) v.findViewById(R.id.tv_downloadering));
        iv_finshed = ((ImageView) v.findViewById(R.id.iv_finshed));
        tv_finshed = ((TextView) v.findViewById(R.id.tv_finshed));
        iv_paused = ((ImageView) v.findViewById(R.id.iv_paused));
        tv_paused = ((TextView) v.findViewById(R.id.tv_paused));
        iv_resource = ((ImageView) v.findViewById(R.id.iv_resource));
        tv_resource = ((TextView)v. findViewById(R.id.tv_resource));
        iv_setting = ((ImageView)v. findViewById(R.id.iv_setting));
        tv_setting = ((TextView) v.findViewById(R.id.tv_setting));

        ((LinearLayout)v.findViewById(R.id.ll_all)).setOnClickListener(onClickListener);
        ((LinearLayout) v.findViewById(R.id.ll_downloadering)).setOnClickListener(onClickListener);
        ((LinearLayout) v.findViewById(R.id.ll_finshed)).setOnClickListener(onClickListener);
        ((LinearLayout) v.findViewById(R.id.ll_paused)).setOnClickListener(onClickListener);
        ((LinearLayout) v.findViewById(R.id.ll_resource)).setOnClickListener(onClickListener);
        ((LinearLayout) v.findViewById(R.id.ll_setting)).setOnClickListener(onClickListener);

        return v;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if (context instanceof AppCompatActivity) {
            activity = (AppCompatActivity) context;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (activity == null) {
            activity = (AppCompatActivity) getActivity();
        }

        showBlankFragment();

        sync = new ReentrantLock();

        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
        }

        dialogUtil = new DialogUtil(activity);
        rl_all_down = ((RelativeLayout) activity.findViewById(R.id.rl_all_down));
        speedBottomHelpView = new speedBottomHelpView(activity,rl_all_down);//底部速率

        /**
         * 初始化头部侧滑菜单的开关
         */
        View spinnerContainer = LayoutInflater.from(activity).inflate(R.layout.toolbar_spinner,
                toolbar, false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar.addView(spinnerContainer, lp);
        spinnerAdapter = new ToolbarSpinnerAdapter(activity);
        spinnerAdapter.addItems(getSpinnerList());
        spinner = (RelativeLayout) spinnerContainer.findViewById(R.id.toolbar_spinner);

        tv_fenlei = ((TextView) spinnerContainer.findViewById(R.id.tv_fenlei));
        iv_fenlei = ((ImageView) spinnerContainer.findViewById(R.id.iv_fenlei));
        iv_genduo = ((ImageView) spinnerContainer.findViewById(R.id.iv_genduo));
        iv_genduo.setOnClickListener(onClickListener);
        iv_fenlei.setOnClickListener(onClickListener);
        activity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        /**
         * 添加种子或磁力链接 功能
         */
        addTorrentButton = (FloatingActionMenu) activity.findViewById(R.id.add_torrent_button);
        addTorrentButton.setClosedOnTouchOutside(true);
        openFileButton = (FloatingActionButton) activity.findViewById(R.id.open_file_button);

        openFileButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addTorrentButton.close(true);
                    torrentFileChooserDialog();
            }
        });

        addLinkButton = (FloatingActionButton) activity.findViewById(R.id.add_link_button);
        addLinkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addTorrentButton.close(true);
                    addLinkDialog();
            }
        });
        //初始化添加的开关
       /* addTorrentButton = (ImageView) activity.findViewById(R.id.add_torrent_button);
        ll_add = ((LinearLayout) activity.findViewById(R.id.ll_add));
        openFileButton = (TextView) activity.findViewById(R.id.open_file_button);
        addLinkButton = (TextView) activity.findViewById(R.id.add_link_button);*/
        none = ((LinearLayout) activity.findViewById(R.id.empty_view_torrent_list));
        tv_none = ((TextView) none.findViewById(R.id.tv_none));

       /* openFileButton.setOnClickListener(onClickListener);
        addLinkButton.setOnClickListener(onClickListener);
        addTorrentButton.setOnClickListener(onClickListener);*/

        if (savedInstanceState != null) {
            prevImplIntent = savedInstanceState.getParcelable(TAG_PREV_IMPL_INTENT);
        }
        //绑定服务,更新下载信息
        activity.bindService(new Intent(activity.getApplicationContext(), TorrentTaskService.class),
                connection, Context.BIND_AUTO_CREATE);
        //初始化Recyclerview
        torrentsList = (EmptyRecyclerView) activity.findViewById(R.id.torrent_list);
        torrentsList.addItemDecoration(new RecyclerDistanceUtil(18));//设置条目间距
        layoutManager = new LinearLayoutManager(activity);
        torrentsList.setLayoutManager(layoutManager);

        /*
         * A RecyclerView by default creates another copy of the ViewHolder in order to
         * fade the views into each other. This causes the problem because the old ViewHolder gets
         * the payload but then the new one doesn't. So needs to explicitly tell it to reuse the old one.
         */
        DefaultItemAnimator animator = new DefaultItemAnimator()
        {
            @Override
            public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder)
            {
                return true;
            }
        };

        TypedArray a = activity.obtainStyledAttributes(new TypedValue().data, new int[]{ R.attr.divider });

        torrentsList.setItemAnimator(animator);
        torrentsList.addItemDecoration(
                new RecyclerViewDividerDecoration(a.getDrawable(0)));
        tv_none.setText("没有下载任务");
        torrentsList.setEmptyView(activity.findViewById(R.id.empty_view_torrent_list),rl_all_down);

        a.recycle();

        adapter = new TorrentListAdapter(
                new ArrayList<TorrentStateParcel>(),
                activity, R.layout.all_recycler_item, this,
                new TorrentSortingComparator(Utils.getTorrentSorting(activity.getApplicationContext())));
//        setTorrentListFilter((String) spinner.getSelectedItem());

        torrentsList.setAdapter(adapter);

        Intent i = activity.getIntent();
        /* If add torrent dialog has been called by an implicit intent */
        if (i != null && i.hasExtra(AddTorrentActivity.TAG_RESULT_TORRENT)) {
            if (prevImplIntent == null || !prevImplIntent.equals(i)) {
                prevImplIntent = i;
                Torrent torrent = i.getParcelableExtra(AddTorrentActivity.TAG_RESULT_TORRENT);

                if (torrent != null) {
                    ArrayList<Torrent> list = new ArrayList<>();
                    list.add(torrent);
                    addTorrentsRequest(list);
                }
            }

        } else if (i != null && i.getAction() != null) {
            switch (i.getAction()) {
                case NotificationReceiver.NOTIFY_ACTION_ADD_TORRENT:
                    addTorrentMenu = true;
                    /* Prevents re-reading action after device configuration changes */
                    i.setAction(null);
                    break;
            }
        }

        /* Show add torrent menu (called from service) after window is displayed */
        activity.getWindow().findViewById(android.R.id.content).post(new Runnable()
        {
            @Override
            public void run()
            {
                if (addTorrentMenu) {
                    /* Hide notification bar */
                    activity.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
                    addTorrentMenu = false;

                    View v = activity.getWindow().findViewById(android.R.id.content);
                    registerForContextMenu(v);
                    activity.openContextMenu(v);
                    unregisterForContextMenu(v);
                }
            }
        });

        if (savedInstanceState != null) {
            selectedTorrents = savedInstanceState.getStringArrayList(TAG_SELECTED_TORRENTS);
            if (savedInstanceState.getBoolean(TAG_IN_ACTION_MODE, false)) {
                actionMode = activity.startActionMode(actionModeCallback);
                adapter.setSelectedItems(savedInstanceState.getIntegerArrayList(TAG_SELECTABLE_ADAPTER));
                actionMode.setTitle(String.valueOf(adapter.getSelectedItemCount()));
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.iv_fenlei) {
                if (drawerLayout.isDrawerOpen(ll_drawer)) {
                    drawerLayout.closeDrawer(ll_drawer);
                } else {
                    drawerLayout.openDrawer(ll_drawer);
                }

            } else if (i == R.id.iv_genduo) {
                dialogUtil.createDialogVersions(onClickListener);

            } else if (i == R.id.ll_all) {
                tv_fenlei.setText(getString(R.string.spinner_all_torrents));
                setTorrentListFilter(getString(R.string.spinner_all_torrents));
                tv_none.setText("没有下载任务");
                torrentsList.setEmptyView(activity.findViewById(R.id.empty_view_torrent_list), rl_all_down);
                drawerLayout.closeDrawer(ll_drawer);

            } else if (i == R.id.ll_downloadering) {
                tv_fenlei.setText(getString(R.string.spinner_downloading_torrents));
                setTorrentListFilter(getString(R.string.spinner_downloading_torrents));
//                    addTorrentButton.setVisibility(View.GONE);
                tv_none.setText(getString(R.string.main_tv9));
                torrentsList.setEmptyView(activity.findViewById(R.id.empty_view_torrent_list), rl_all_down);
                drawerLayout.closeDrawer(ll_drawer);

            } else if (i == R.id.ll_finshed) {
                tv_fenlei.setText(getString(R.string.spinner_downloaded_torrents));
                setTorrentListFilter(getString(R.string.spinner_downloaded_torrents));

//                    addTorrentButton.setVisibility(View.GONE);
                tv_none.setText(getString(R.string.main_tv10));
                torrentsList.setEmptyView(activity.findViewById(R.id.empty_view_torrent_list), rl_all_down);
                drawerLayout.closeDrawer(ll_drawer);

            } else if (i == R.id.ll_paused) {
                tv_fenlei.setText(getString(R.string.spinner_paused_torrents));
                setTorrentListFilter(getString(R.string.spinner_paused_torrents));
//                    addTorrentButton.setVisibility(View.GONE);
                tv_none.setText(getString(R.string.main_tv11));
                torrentsList.setEmptyView(activity.findViewById(R.id.empty_view_torrent_list), rl_all_down);
                drawerLayout.closeDrawer(ll_drawer);

            } else if (i == R.id.ll_resource) {
                torrentFileChooserDialog();
                drawerLayout.closeDrawer(ll_drawer);

            } else if (i == R.id.ll_setting) {
                startActivity(new Intent(getActivity(), BTSettingActivity.class));
                drawerLayout.closeDrawer(ll_drawer);

/*
                case R.id.open_file_button://打开文件
                    ll_add.setVisibility(View.GONE);
                    torrentFileChooserDialog();
                    break;

                case R.id.add_link_button://输入链接
                    ll_add.setVisibility(View.GONE);
                    addLinkDialog();
                    break;

                case R.id.add_torrent_button://添加下载任务
                    ll_add.setVisibility(View.VISIBLE);
                    break;*/

                /**
                 * dialog的点击事件
                 */
            } else if (i == R.id.ll_all_start) {
                allDowning();
                dialogUtil.cancelDailog();

            } else if (i == R.id.ll_all_pause) {
                allpause();
                dialogUtil.cancelDailog();

            }
        }
    };

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        speedBottomHelpView = null;
        if (bound) {
            try {
                ipc.sendClientDisconnect(serviceCallback, clientCallback);

            } catch (RemoteException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

            getActivity().unbindService(connection);
            bound = false;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (torrentsListState != null) {
            layoutManager.onRestoreInstanceState(torrentsListState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable(TAG_PREV_IMPL_INTENT, prevImplIntent);
        outState.putIntegerArrayList(TAG_SELECTABLE_ADAPTER, adapter.getSelectedItems());
        outState.putBoolean(TAG_IN_ACTION_MODE, inActionMode);
        outState.putStringArrayList(TAG_SELECTED_TORRENTS, selectedTorrents);
        torrentsListState = layoutManager.onSaveInstanceState();
        outState.putParcelable(TAG_TORRENTS_LIST_STATE, torrentsListState);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            torrentsListState = savedInstanceState.getParcelable(TAG_TORRENTS_LIST_STATE);
        }
    }

    private ServiceConnection connection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service) {
            serviceCallback = new Messenger(service);
            bound = true;

            if (!torrentsQueue.isEmpty()) {
                addTorrentsRequest(torrentsQueue);
                torrentsQueue.clear();
            }

            try {
                ipc.sendClientConnect(serviceCallback, clientCallback);

            } catch (RemoteException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            serviceCallback = null;
            bound = false;
        }
    };

    static class CallbackHandler extends Handler
    {
        WeakReference<MainFragment> fragment;

        public CallbackHandler(MainFragment fragment) {
            this.fragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (fragment.get() == null) {
                return;
            }

            Bundle b;
            TorrentStateParcel state;

            switch (msg.what) {
                case TorrentTaskServiceIPC.UPDATE_STATES_ONESHOT: {
                    b = msg.getData();
                    b.setClassLoader(TorrentStateParcel.class.getClassLoader());

                    Bundle states = b.getParcelable(TorrentTaskServiceIPC.TAG_STATES_LIST);
                    if (states != null) {
                        fragment.get().torrentStates.clear();

                        for (String key : states.keySet()) {
                            state = states.getParcelable(key);
                            if (state != null) {
                                fragment.get().torrentStates.put(state.torrentId, state);
                            }
                        }

                        fragment.get().reloadAdapter();
                    }
                    break;
                }
                case TorrentTaskServiceIPC.UPDATE_STATE:
                    b = msg.getData();
                    b.setClassLoader(TorrentStateParcel.class.getClassLoader());
                    state = b.getParcelable(TorrentTaskServiceIPC.TAG_STATE);

                    if (state != null) {
                        fragment.get().torrentStates.put(state.torrentId, state);
                        fragment.get().reloadAdapterItem(state);
                    }
                    break;
                case TorrentTaskServiceIPC.TERMINATE_ALL_CLIENTS:
                    fragment.get().finish(new Intent(), FragmentCallback.ResultCode.SHUTDOWN);
                    break;
                case TorrentTaskServiceIPC.TORRENTS_ADDED: {
                    b = msg.getData();
                    b.setClassLoader(TorrentStateParcel.class.getClassLoader());

                    List<TorrentStateParcel> states =
                            b.getParcelableArrayList(TorrentTaskServiceIPC.TAG_STATES_LIST);

                    if (states != null && !states.isEmpty()) {
                        for (TorrentStateParcel s : states) {
                            fragment.get().torrentStates.put(s.torrentId, s);
                        }

                        fragment.get().reloadAdapter();
                    }

                    Object o = b.getSerializable(TorrentTaskServiceIPC.TAG_EXCEPTIONS_LIST);
                    if (o != null) {
                        ArrayList<Throwable> exceptions = (ArrayList<Throwable>) o;
                        for (Throwable e : exceptions) {
                            fragment.get().saveTorrentError(e);
                        }
                    }
                    break;
                }
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private class ActionModeCallback implements ActionMode.Callback
    {
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu)
        {
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            inActionMode = true;
            mode.getMenuInflater().inflate(R.menu.main_action_mode, menu);
            Utils.showActionModeStatusBar(activity, true);

            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
        {
            ArrayList<Integer> indexes = adapter.getSelectedItems();

            int i1 = item.getItemId();
            if (i1 == R.id.delete_torrent_menu) {
                mode.finish();

                if (getFragmentManager().findFragmentByTag(TAG_DELETE_TORRENT_DIALOG) == null) {
                    BaseAlertDialog deleteTorrentDialog = BaseAlertDialog.newInstance(
                            getString(R.string.deleting),
                            (indexes.size() > 1 ? getString(R.string.delete_selected_torrents) : getString(R.string.delete_selected_torrent)),
                            R.layout.dialog_delete_torrent,
                            getString(R.string.ok),
                            getString(R.string.cancel),
                            null,
                            MainFragment.this);

                    deleteTorrentDialog.show(getFragmentManager(), TAG_DELETE_TORRENT_DIALOG);
                }


            } else if (i1 == R.id.select_all_torrent_menu) {
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    if (adapter.isSelected(i)) {
                        continue;
                    }

                    onItemSelected(adapter.getItem(i).torrentId, i);
                }


            } else if (i1 == R.id.force_recheck_torrent_menu) {
                mode.finish();

                forceRecheckRequest();

               /* case R.id.force_announce_torrent_menu:
                    mode.finish();

                    forceAnnounceRequest();
                    break;*/
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode)
        {
            adapter.clearSelection();
            actionMode = null;
            inActionMode = false;
            Utils.showActionModeStatusBar(activity, false);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        activity.getMenuInflater().inflate(R.menu.main_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        int i = item.getItemId();
        if (i == R.id.add_link_menu) {
            addLinkDialog();

        } else if (i == R.id.open_file_menu) {
            torrentFileChooserDialog();

        }

        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        return true;
    }

    private void addLinkDialog()
    {
        if (getFragmentManager().findFragmentByTag(TAG_ADD_LINK_DIALOG) == null) {
            BaseAlertDialog addLinkDialog = BaseAlertDialog.newInstance(
                    getString(R.string.dialog_add_link_title),
                    null,
                    R.layout.dialog_text_input,
                    getString(R.string.ok),
                    getString(R.string.cancel),
                    null,
                    this);

            addLinkDialog.show(getFragmentManager(), TAG_ADD_LINK_DIALOG);
        }
    }

    private boolean checkEditTextField(String s, TextInputLayout layout)
    {
        if (s == null || layout == null) {
            return false;
        }

        if (TextUtils.isEmpty(s)) {
            layout.setErrorEnabled(true);
            layout.setError(getString(R.string.error_empty_link));
            layout.requestFocus();

            return false;
        }

        if (s.startsWith(Utils.MAGNET_PREFIX)) {
            layout.setErrorEnabled(false);
            layout.setError(null);

            return true;
        }

        if (!Patterns.WEB_URL.matcher(s).matches()) {
            layout.setErrorEnabled(true);
            layout.setError(getString(R.string.error_invalid_link));
            layout.requestFocus();

            return false;
        }

        layout.setErrorEnabled(false);
        layout.setError(null);

        return true;
    }

    /*
     * Returns a list of torrents sorting categories for spinner.
     */

    private List<String> getSpinnerList()
    {
        List<String> categories = new ArrayList<String>();
        categories.add(getString(R.string.spinner_all_torrents));
        categories.add(getString(R.string.spinner_downloading_torrents));
        categories.add(getString(R.string.spinner_downloaded_torrents));

        return categories;
    }

    private void setTorrentListFilter(String filter)
    {
        if (filter == null) {
            return;
        }

        if (filter.equals(getString(R.string.spinner_downloading_torrents))) {
            adapter.setDisplayFilter(new TorrentListAdapter.DisplayFilter(TorrentStateCode.DOWNLOADING));

        } else if (filter.equals(getString(R.string.spinner_downloaded_torrents))) {
            adapter.setDisplayFilter(new TorrentListAdapter.DisplayFilter(TorrentStateCode.SEEDING));

        } else if (filter.equals(getString(R.string.spinner_paused_torrents))){
            adapter.setDisplayFilter(new TorrentListAdapter.DisplayFilter(TorrentStateCode.PAUSED));
        }else {
            adapter.setDisplayFilter(new TorrentListAdapter.DisplayFilter());
        }
    }

    @Override
    public void onPauseButtonClicked(int position, TorrentStateParcel torrentState)
    {
        pauseResumeTorrentRequest(torrentState.torrentId);
    }

    @Override
    public void onItemClicked(View view,int position, TorrentStateParcel torrentState)
    {
        if (view.getId() == R.id.iv_delete){
            if (getFragmentManager().findFragmentByTag(TAG_DELETE_TORRENT_DIALOG) == null) {
                selectedTorrents.add(torrentState.torrentId);
                BaseAlertDialog deleteTorrentDialog = BaseAlertDialog.newInstance(
                        getString(R.string.deleting),
                        getString(R.string.delete_selected_torrent),
                        R.layout.dialog_delete_torrent,
                        getString(R.string.ok),
                        getString(R.string.cancel),
                        null,
                        MainFragment.this);

                deleteTorrentDialog.show(getFragmentManager(), TAG_DELETE_TORRENT_DIALOG);
            }


        }else {
            if (actionMode == null) {
            /* Mark this torrent as pause in the list */
                adapter.markAsOpen(torrentState);

                showDetailTorrent(torrentState.torrentId);
            } else {
                onItemSelected(torrentState.torrentId, position);
            }
        }
    }

    @Override
    public boolean onItemLongClicked(int position, TorrentStateParcel torrentState)
    {
        //跳转到详情页
        /*if (actionMode == null) {
            *//* Mark this torrent as pause in the list *//*
            adapter.markAsOpen(torrentState);

            showDetailTorrent(torrentState.torrentId);
        } else {
            onItemSelected(torrentState.torrentId, position);
        }*/

        //
        if (actionMode == null) {
            actionMode = activity.startActionMode(actionModeCallback);
        }

        onItemSelected(torrentState.torrentId, position);
        return true;
    }

    private void onItemSelected(String id, int position)
    {
        toggleSelection(position);

        if (selectedTorrents.contains(id)) {
            selectedTorrents.remove(id);
        } else {
            selectedTorrents.add(id);
        }
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    /*
     * Uncheck current torrent from the list and set blank fragment.
     */

    public void resetCurOpenTorrent()
    {
        adapter.markAsOpen(null);
        showBlankFragment();
    }

    @Override
    public void onShow(final AlertDialog dialog)
    {
        if (dialog != null) {
            if (getFragmentManager().findFragmentByTag(TAG_ADD_LINK_DIALOG) != null) {
                initAddDialog(dialog);

            } else if (getFragmentManager().findFragmentByTag(TAG_ABOUT_DIALOG) != null) {
                initAboutDialog(dialog);

            } else if (getFragmentManager().findFragmentByTag(TAG_TORRENT_SORTING) != null) {
                initTorrentSortingDialog(dialog);
            }
        }
    }

    private void initAddDialog(final AlertDialog dialog)
    {
        final TextInputEditText field =
                (TextInputEditText) dialog.findViewById(R.id.text_input_dialog);
        final TextInputLayout fieldLayout =
                (TextInputLayout) dialog.findViewById(R.id.layout_text_input_dialog);

        /* Dismiss error label if user has changed the text */
        if (field != null && fieldLayout != null) {
            field.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {
                    /* Nothing */
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    fieldLayout.setErrorEnabled(false);
                    fieldLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s)
                {
                    /* Nothing */
                }
            });
        }

        /*
         * It is necessary in order to the dialog is not closed by
         * pressing positive button if the text checker gave a false result
         */
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (field != null && fieldLayout != null) {
                    String link = field.getText().toString();

                    if (checkEditTextField(link, fieldLayout)) {
                        String url;

                        if (link.startsWith(Utils.MAGNET_PREFIX)) {
                            url = link;
                        } else {
                            url = Utils.normalizeURL(link);
                        }

                        if (url != null) {
                            addTorrentDialog(Uri.parse(url));
                        }

                        dialog.dismiss();
                    }
                }
            }
        });

        /* Inserting a link from the clipboard */
        String clipboard = Utils.getClipboard(activity.getApplicationContext());
        String url;

        if (clipboard != null) {
            if (!clipboard.startsWith(Utils.MAGNET_PREFIX)) {
                url = Utils.normalizeURL(clipboard);
            } else {
                url = clipboard;
            }

            if (field != null && url != null) {
                field.setText(url);
            }
        }
    }

    private void initAboutDialog(final AlertDialog dialog)
    {
        TextView version = (TextView) dialog.findViewById(R.id.about_version);

        if (version != null) {
            try {
                PackageInfo info = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                version.setText(info.versionName);

            } catch (PackageManager.NameNotFoundException e) {
                /* Ignore */
            }
        }
    }

    private void initTorrentSortingDialog(final AlertDialog dialog)
    {
        Spinner sp = (Spinner) dialog.findViewById(R.id.dialog_sort_by);
        RadioGroup group = (RadioGroup) dialog.findViewById(R.id.dialog_sort_direction);

        if (sp != null && group != null) {
            SettingsManager pref = new SettingsManager(activity);

            String[] columns = activity.getResources().getStringArray(R.array.sort_torrent_values);

            String column = pref.getString(activity.getString(R.string.pref_key_sort_torrent_by),
                    TorrentSorting.SortingColumns.name.name());
            String direction = pref.getString(activity.getString(R.string.pref_key_sort_torrent_direction),
                    TorrentSorting.Direction.ASC.name());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                    R.layout.spinner_item_dropdown,
                    getResources().getStringArray(R.array.sort_torrent_by));
            sp.setAdapter(adapter);
            sp.setSelection(Arrays.asList(columns).indexOf(column));

            if (TorrentSorting.Direction.fromValue(direction) == TorrentSorting.Direction.ASC) {
                group.check(R.id.dialog_sort_by_ascending);
            } else {
                group.check(R.id.dialog_sort_by_descending);
            }
        }
    }

    @Override
    public void onPositiveClicked(@Nullable View v)
    {
        if (v != null) {
            if (getFragmentManager().findFragmentByTag(TAG_DELETE_TORRENT_DIALOG) != null) {
                CheckBox withFiles = (CheckBox) v.findViewById(R.id.dialog_delete_torrent_with_downloaded_files);
                deleteTorrentsRequest(withFiles.isChecked());
                selectedTorrents.clear();

//                finish(new Intent(), FragmentCallback.ResultCode.CANCEL);
                /*CheckBox withFiles = (CheckBox) v.findViewById(R.id.dialog_delete_torrent_with_downloaded_files);

                DetailTorrentFragment f = getCurrentDetailFragment();
                if (f != null) {
                    String id = f.getTorrentId();
                    if (selectedTorrents.contains(id)) {
                        resetCurOpenTorrent();
                    }
                }

                deleteTorrentsRequest(withFiles.isChecked());

                selectedTorrents.clear();*/
            } else if (getFragmentManager().findFragmentByTag(btConstant.TAG_ERROR_OPEN_TORRENT_FILE_DIALOG) != null ||
                    getFragmentManager().findFragmentByTag(TAG_SAVE_ERROR_DIALOG) != null) {
                if (sentError != null) {
                    EditText editText = (EditText) v.findViewById(R.id.comment);
                    String comment = editText.getText().toString();

                    Utils.reportError(sentError, comment);
                }

            } else if (getFragmentManager().findFragmentByTag(TAG_TORRENT_SORTING) != null) {
                Spinner sp = (Spinner) v.findViewById(R.id.dialog_sort_by);
                RadioGroup group = (RadioGroup) v.findViewById(R.id.dialog_sort_direction);
                SettingsManager pref = new SettingsManager(activity);

                String[] columns = activity.getResources().getStringArray(R.array.sort_torrent_values);
                int position = sp.getSelectedItemPosition();

                if (position != -1 && position < columns.length) {
                    String column = columns[position];
                    pref.put(activity.getString(R.string.pref_key_sort_torrent_by), column);
                }

                int radioButtonId = group.getCheckedRadioButtonId();
                String direction = TorrentSorting.Direction.ASC.name();

                if (radioButtonId == R.id.dialog_sort_by_descending) {
                    direction = TorrentSorting.Direction.DESC.name();
                }
                pref.put(activity.getString(R.string.pref_key_sort_torrent_direction), direction);

                if (adapter != null) {
                    adapter.setSorting(new TorrentSortingComparator(
                            Utils.getTorrentSorting(activity.getApplicationContext())));
                }
            }
        }
    }

    @Override
    public void onNegativeClicked(@Nullable View v)
    {
        if (getFragmentManager().findFragmentByTag(TAG_DELETE_TORRENT_DIALOG) != null) {
            selectedTorrents.clear();

        } else if (getFragmentManager().findFragmentByTag(TAG_ABOUT_DIALOG) != null) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getString(R.string.about_changelog_link)));
            startActivity(i);
        }
    }

    @Override
    public void onNeutralClicked(@Nullable View v)
    {
        /* Nothing */
    }

    private void reloadAdapterItem(TorrentStateParcel state)
    {
        sync.lock();

        try {
            adapter.updateItem(state);

        } finally {
            sync.unlock();
        }
    }

    final synchronized void reloadAdapter()
    {
        adapter.clearAll();

        if (torrentStates == null || torrentStates.size() == 0) {
            adapter.notifyDataSetChanged();
        } else {
            adapter.addItems(torrentStates.values());
        }
    }

    private void torrentFileChooserDialog()
    {
        Intent i = new Intent(activity, FileManagerDialog.class);

        List<String> fileType = new ArrayList<>();
        fileType.add("torrent");
        FileManagerConfig config = new FileManagerConfig(null,
                getString(R.string.torrent_file_chooser_title),//选择 torrent 文件
                fileType,//torrent
                FileManagerConfig.FILE_CHOOSER_MODE);

        i.putExtra(FileManagerDialog.TAG_CONFIG, config);

        startActivityForResult(i, btConstant.TORRENT_FILE_CHOOSE_REQUEST);
    }

    private void addTorrentDialog(Uri uri)
    {
        if (uri == null) {
            return;
        }

        Intent i = new Intent(activity, AddTorrentActivity.class);
        i.putExtra(AddTorrentActivity.TAG_URI, uri);
        startActivityForResult(i, btConstant.ADD_TORRENT_REQUEST);
    }

    private void showDetailTorrent(String id)
    {
        if (Utils.isTwoPane(activity.getApplicationContext())) {
            FragmentManager fm = getFragmentManager();

            DetailTorrentFragment detail = DetailTorrentFragment.newInstance(id);

            Fragment fragment = fm.findFragmentById(R.id.detail_torrent_fragmentContainer);
            if (fragment != null && fragment instanceof DetailTorrentFragment) {
                String oldId = ((DetailTorrentFragment) fragment).getTorrentId();

                if (oldId != null && id.equals(oldId)) {
                    return;
                }
            }

            fm.beginTransaction()
                    .replace(R.id.detail_torrent_fragmentContainer, detail)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();

        } else {
            Intent i = new Intent(activity, DetailTorrentActivity.class);
            i.putExtra(DetailTorrentActivity.TAG_TORRENT_ID, id);
            startActivity(i);
        }
    }

    private void showBlankFragment()
    {
        if (Utils.isTablet(activity.getApplicationContext())) {
            FragmentManager fm = getFragmentManager();

            BlankFragment blank = BlankFragment.newInstance();

            fm.beginTransaction()
                    .replace(R.id.detail_torrent_fragmentContainer, blank)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .commitAllowingStateLoss();
        }
    }

    public DetailTorrentFragment getCurrentDetailFragment()
    {
        if (!Utils.isTwoPane(activity.getApplicationContext())) {
            return null;
        }

        Fragment fragment = getFragmentManager()
                .findFragmentById(R.id.detail_torrent_fragmentContainer);

        return (fragment instanceof DetailTorrentFragment ? (DetailTorrentFragment) fragment : null);
    }

    private void deleteTorrentsRequest(boolean withFiles)
    {
        if (!bound || serviceCallback == null) {
            return;
        }

        try {
            ipc.sendDeleteTorrents(serviceCallback, selectedTorrents, withFiles);

        } catch (RemoteException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private void forceRecheckRequest()
    {
        if (!bound || serviceCallback == null) {
            return;
        }

        try {
            ipc.sendForceRecheck(serviceCallback, selectedTorrents);

        } catch (RemoteException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        selectedTorrents.clear();
    }

    private void forceAnnounceRequest()
    {
        if (!bound || serviceCallback == null) {
            return;
        }

        try {
            ipc.sendForceAnnounce(serviceCallback, selectedTorrents);

        } catch (RemoteException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        selectedTorrents.clear();
    }

    private void addTorrentsRequest(Collection<Torrent> torrents)
    {
        if (!bound || serviceCallback == null) {
            torrentsQueue.addAll(torrents);

            return;
        }

        try {
            ipc.sendAddTorrents(serviceCallback, new ArrayList<>(torrents));

        } catch (RemoteException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private void saveTorrentError(Throwable e)
    {
        if (e == null || !isAdded()) {
            return;
        }

        sentError = e;

        if (e instanceof FileNotFoundException) {
            ErrorReportAlertDialog errDialog = ErrorReportAlertDialog.newInstance(
                    activity.getApplicationContext(),
                    getString(R.string.error),
                    getString(R.string.error_file_not_found_add_torrent),
                    Log.getStackTraceString(e),
                    this);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(errDialog, TAG_SAVE_ERROR_DIALOG);
            ft.commitAllowingStateLoss();

        } else if (e instanceof IOException) {
            ErrorReportAlertDialog errDialog = ErrorReportAlertDialog.newInstance(
                    activity.getApplicationContext(),
                    getString(R.string.error),
                    getString(R.string.error_io_add_torrent),
                    Log.getStackTraceString(e),
                    this);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(errDialog, TAG_SAVE_ERROR_DIALOG);
            ft.commitAllowingStateLoss();

        } else if (e instanceof FileAlreadyExistsException) {
            Snackbar.make(coordinatorLayout,
                    R.string.torrent_exist,
                    Snackbar.LENGTH_LONG)
                    .show();
        } else {
            ErrorReportAlertDialog errDialog = ErrorReportAlertDialog.newInstance(
                    activity.getApplicationContext(),
                    getString(R.string.error),
                    getString(R.string.error_add_torrent),
                    Log.getStackTraceString(e),
                    this);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(errDialog, TAG_SAVE_ERROR_DIALOG);
            ft.commitAllowingStateLoss();
        }
    }

    private void pauseResumeTorrentRequest(String id)
    {
        if (!bound || serviceCallback == null) {
            return;
        }

        ArrayList<String> list = new ArrayList<>();
        list.add(id);

        try {
            ipc.sendPauseResumeTorrents(serviceCallback, list);

        } catch (RemoteException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode) {
            case btConstant.TORRENT_FILE_CHOOSE_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.hasExtra(FileManagerDialog.TAG_RETURNED_PATH)) {
                        String path = data.getStringExtra(FileManagerDialog.TAG_RETURNED_PATH);

                        try {
                            addTorrentDialog(Uri.fromFile(new File(path)));

                        } catch (Exception e) {
                            sentError = e;

                            Log.e(TAG, Log.getStackTraceString(e));

                            if (getFragmentManager()
                                    .findFragmentByTag(btConstant.TAG_ERROR_OPEN_TORRENT_FILE_DIALOG) == null) {
                                ErrorReportAlertDialog errDialog = ErrorReportAlertDialog.newInstance(
                                        activity.getApplicationContext(),
                                        getString(R.string.error),
                                        getString(R.string.error_open_torrent_file),//无法打开目标种子文件
                                        Log.getStackTraceString(e),
                                        this);

                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.add(errDialog, btConstant.TAG_ERROR_OPEN_TORRENT_FILE_DIALOG);
                                ft.commitAllowingStateLoss();
                            }
                        }
                    }
                }
                break;
            case btConstant.ADD_TORRENT_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.hasExtra(AddTorrentActivity.TAG_RESULT_TORRENT)) {
                        Torrent torrent = data.getParcelableExtra(AddTorrentActivity.TAG_RESULT_TORRENT);
                        if (torrent != null) {
                            ArrayList<Torrent> list = new ArrayList<>();
                            list.add(torrent);
                            addTorrentsRequest(list);
                        }
                    }
                }
                break;
        }
    }



    public void allDowning(){
        ArrayList<String> list=new ArrayList<>();
        for(int i=0;i<adapter.getAllItems().size();i++){
            list.add(adapter.getAllItems().get(i).torrentId);
        }
        try {
            ipc.sendResumeTorrents(serviceCallback, list);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void allpause(){
        ArrayList<String> list=new ArrayList<>();
        for(int i=0;i<adapter.getAllItems().size();i++){
            list.add(adapter.getAllItems().get(i).torrentId);
        }
        try {
            ipc.sendPauseTorrents(serviceCallback, list);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void finish(Intent intent, FragmentCallback.ResultCode code)
    {
        ((FragmentCallback) activity).fragmentFinished(intent, code);
    }
}

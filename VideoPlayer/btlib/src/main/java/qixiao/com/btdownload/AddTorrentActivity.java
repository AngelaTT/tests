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

package qixiao.com.btdownload;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import qixiao.com.btdownload.R;

import qixiao.com.btdownload.core.exceptions.DecodeException;
import qixiao.com.btdownload.core.exceptions.FetchLinkException;
import qixiao.com.btdownload.core.utils.Utils;
import qixiao.com.btdownload.dialogs.BaseAlertDialog;
import qixiao.com.btdownload.dialogs.ErrorReportAlertDialog;
import qixiao.com.btdownload.dialogs.SpinnerProgressDialog;
import qixiao.com.btdownload.fragments.AddTorrentFragment;
import qixiao.com.btdownload.fragments.FragmentCallback;

import java.io.IOException;

/*
 * The dialog for adding torrent. The parent window.
 */

public class AddTorrentActivity extends AppCompatActivity
        implements
        FragmentCallback,
        AddTorrentFragment.Callback,
        BaseAlertDialog.OnClickListener
{

    @SuppressWarnings("unused")
    private static final String TAG = AddTorrentActivity.class.getSimpleName();

    private static final String TAG_FRAGMENT = "fragment";
    private static final String TAG_SPINNER_PROGRESS = "spinner_progress";
    private static final String TAG_IO_EXCEPT_DIALOG = "io_except_dialog";
    private static final String TAG_DECODE_EXCEPT_DIALOG = "decode_except_dialog";
    private static final String TAG_FETCH_EXCEPT_DIALOG = "fetch_except_dialog";
    private static final String TAG_ILLEGAL_ARGUMENT = "illegal_argument";

    public static final String TAG_URI = "uri";
    public static final String TAG_RESULT_TORRENT = "result_bundle";

    private AddTorrentFragment addTorrentFragment;
    private SpinnerProgressDialog progress;
    private Exception sentError;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (Utils.isDarkTheme(getApplicationContext())) {
            setTheme(R.style.AppTheme_Dark);
        }

        setContentView(R.layout.activity_add_torrent);

        FragmentManager fm = getFragmentManager();
        addTorrentFragment = (AddTorrentFragment) fm.findFragmentByTag(TAG_FRAGMENT);

        Intent intent = getIntent();
        Uri uri;
        if (intent.getData() != null) {
            /* Implicit intent with path to torrent file, http or magnet link */
            uri = intent.getData();
        } else {
            uri = intent.getParcelableExtra(TAG_URI);
        }

        if (uri != null) {
            addTorrentFragment =
                    (AddTorrentFragment) fm.findFragmentById(R.id.add_torrent_fragmentContainer);
            addTorrentFragment.setUri(uri);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        progress = (SpinnerProgressDialog) getFragmentManager().findFragmentByTag(TAG_SPINNER_PROGRESS);
    }

    @Override
    public void onPreExecute(String progressDialogText)
    {
        showProgress(progressDialogText);
    }

    @Override
    public void onPostExecute(Exception e)
    {
        dismissProgress();

        if (e != null) {
            if (e instanceof DecodeException) {
                if (getFragmentManager().findFragmentByTag(TAG_DECODE_EXCEPT_DIALOG) == null) {
                    BaseAlertDialog errDialog = BaseAlertDialog.newInstance(
                            getString(R.string.error),
                            getString(R.string.error_decode_torrent),//torrent 文件解码失败。可能文件已损坏或格式不正确。
                            0,
                            getString(R.string.ok),
                            null,
                            null,
                            this);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.add(errDialog, TAG_DECODE_EXCEPT_DIALOG);
                    ft.commitAllowingStateLoss();
                }

            } else if (e instanceof FetchLinkException) {
                if (getFragmentManager().findFragmentByTag(TAG_FETCH_EXCEPT_DIALOG) == null) {
                    BaseAlertDialog errDialog = BaseAlertDialog.newInstance(
                            getString(R.string.error),
                            getString(R.string.error_fetch_link),
                            0,
                            getString(R.string.ok),
                            null,
                            null,
                            this);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.add(errDialog, TAG_FETCH_EXCEPT_DIALOG);
                    ft.commitAllowingStateLoss();
                }

            } else if (e instanceof IllegalArgumentException) {
                if (getFragmentManager().findFragmentByTag(TAG_ILLEGAL_ARGUMENT) == null) {
                    BaseAlertDialog errDialog = BaseAlertDialog.newInstance(
                            getString(R.string.error),
                            getString(R.string.error_invalid_link_or_path),
                            0,
                            getString(R.string.ok),
                            null,
                            null,
                            this);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.add(errDialog, TAG_ILLEGAL_ARGUMENT);
                    ft.commitAllowingStateLoss();
                }

            } else if (e instanceof IOException) {
                sentError = e;
                if (getFragmentManager().findFragmentByTag(TAG_IO_EXCEPT_DIALOG) == null) {
                    ErrorReportAlertDialog errDialog = ErrorReportAlertDialog.newInstance(
                            getApplicationContext(),
                            getString(R.string.error),
                            getString(R.string.error_io_torrent),
                            Log.getStackTraceString(e),
                            this);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.add(errDialog, TAG_IO_EXCEPT_DIALOG);
                    ft.commitAllowingStateLoss();
                }
            }
        }
    }

    @Override
    public void onPositiveClicked(@Nullable View v)
    {
        if (sentError != null) {
            String comment = null;

            if (v != null) {
                EditText editText = (EditText) v.findViewById(R.id.comment);
                comment = editText.getText().toString();
            }

            Utils.reportError(sentError, comment);
        }

        finish();
    }

    @Override
    public void onNegativeClicked(@Nullable View v)
    {
        finish();
    }

    @Override
    public void onNeutralClicked(@Nullable View v)
    {
        /* Nothing */
    }

    private void showProgress(String progressDialogText)
    {
        progress = SpinnerProgressDialog.newInstance(
                R.string.decode_torrent_progress_title,
                progressDialogText,
                0,
                true,
                true);

        progress.show(getFragmentManager(), TAG_SPINNER_PROGRESS);
    }

    private void dismissProgress()
    {
        if (progress != null) {
            try {
                progress.dismiss();
            } catch (Exception e) {
                /* Ignore */
            }
        }

        progress = null;
    }

    @Override
    public void fragmentFinished(Intent intent, ResultCode code)
    {
        if (code == ResultCode.OK) {
            /* If add torrent dialog has been called by an implicit intent */
            if (getIntent().getData() != null && intent.hasExtra(TAG_RESULT_TORRENT)) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(TAG_RESULT_TORRENT, intent.getParcelableExtra(TAG_RESULT_TORRENT));
                startActivity(i);
            } else {
                setResult(RESULT_OK, intent);
            }

        } else if (code == ResultCode.BACK) {
            /* For correctly finishing activity, if it was called by implicit intent */
            if (getIntent().getData() != null) {
                finish();
            } else {
                setResult(RESULT_CANCELED, intent);
            }

        } else if (code == ResultCode.CANCEL) {
            setResult(RESULT_CANCELED, intent);
        }

        finish();
    }

    @Override
    public void onBackPressed()
    {
        addTorrentFragment.onBackPressed();
    }
}

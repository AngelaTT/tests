package qixiao.com.btdownload.core.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import qixiao.com.btdownload.R;

/**
 * Created by Administrator on 2017/3/20.
 */

public class DialogUtil implements View.OnClickListener{

    private final Context context;
    public Dialog myDialog;
    private LinearLayout ll_all_start;
    private LinearLayout ll_all_pause;

    public DialogUtil(Context context)
    {
        this.context = context;
    }

    public void createDialogVersions(View.OnClickListener onClickListener){
        myDialog = new Dialog(context, R.style.mainDialog);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_main,null);
        ll_all_start = ((LinearLayout) view.findViewById(R.id.ll_all_start));
        ll_all_pause = ((LinearLayout) view.findViewById(R.id.ll_all_pause));

        ll_all_pause.setOnClickListener(onClickListener);
        ll_all_start.setOnClickListener(onClickListener);

        Window dialogWindow = myDialog.getWindow();
        dialogWindow.setGravity(Gravity.TOP| Gravity.END);
        int pad = context.getResources().getDimensionPixelOffset(R.dimen.height_50dp);
        int pad2 = context.getResources().getDimensionPixelOffset(R.dimen.margin_20dp);
        dialogWindow.getDecorView().setPadding(0, pad, pad2, 0);//左上右下
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = context.getResources().getDimensionPixelOffset(R.dimen.height_200dp);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);


        myDialog.setContentView(view);
        myDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
           /* case R.id.ll_all_start:
//                FileDownloader.start(getUrls());
                myDialog.dismiss();
                break;

            case R.id.ll_all_pause:
//                FileDownloader.pauseAll();
                myDialog.dismiss();
                break;*/

        }
    }


    /**
     * <ul>
     *     <li>创建 下排 带2个按钮的 dialog</li>
     * </ul>
     * @param title
     * @param content
     * @param cacel
     * @param ok
     * @param onClickListener
     */
    public void createDialogWith2Btn(String title, String content, String cacel, String ok, View.OnClickListener onClickListener)
    {
        if(myDialog != null && myDialog.isShowing())
        {
            myDialog.cancel();
        }
        myDialog = new Dialog(context, R.style.mainDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_layout_2btn,null);
        Button btn_cancel  = (Button)view.findViewById(R.id.dialog_btn_cancel);
        Button btn_ok = (Button)view.findViewById(R.id.dialog_btn_sure);

        TextView tv_title = (TextView) view.findViewById(R.id.dialog_tv_title);
        tv_title.setText(title);

        TextView tv_content = (TextView) view.findViewById(R.id.dialog_tv_content);
        tv_content.setText(content);

        if(!TextUtils.isEmpty(cacel))
        {
            btn_cancel.setText(cacel);
        }

        if(!TextUtils.isEmpty(ok))
        {
            btn_ok.setText(ok);
        }

        btn_cancel.setOnClickListener(onClickListener);
        btn_ok.setOnClickListener(onClickListener);
        myDialog.setContentView(view);
        myDialog.show();
    }

    /**
     * <ul>
     *     <li>创建 下排 带2个按钮的,带输入内容 dialog</li>
     * </ul>
     * @param title
     * @param content
     * @param cacel
     * @param ok
     * @param onClickListener
     */
    public EditText createDialogEit(String title, String content, String cacel, String ok, View.OnClickListener onClickListener)
    {
        if(myDialog != null && myDialog.isShowing())
        {
            myDialog.cancel();
        }
        myDialog = new Dialog(context, R.style.mainDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_layout_eit,null);
        Button btn_cancel  = (Button)view.findViewById(R.id.dialog_btn_cancel);
        Button btn_ok = (Button)view.findViewById(R.id.dialog_btn_sure);

        TextView tv_title = (TextView) view.findViewById(R.id.dialog_tv_title);
        tv_title.setText(title);

        TextView tv_content = (TextView) view.findViewById(R.id.dialog_tv_content);
        tv_content.setText(content);

        EditText et_name = (EditText) view.findViewById(R.id.et_name);

        if(!TextUtils.isEmpty(cacel))
        {
            btn_cancel.setText(cacel);
        }

        if(!TextUtils.isEmpty(ok))
        {
            btn_ok.setText(ok);
        }

        btn_cancel.setOnClickListener(onClickListener);
        btn_ok.setOnClickListener(onClickListener);
        myDialog.setContentView(view);
        myDialog.show();

        return et_name;
    }


    /**
     * <p>取消Dialog</p>
     */
    public void cancelDailog()
    {
        if(myDialog != null && myDialog.isShowing() )
        {
            myDialog.cancel();
        }
    }
}

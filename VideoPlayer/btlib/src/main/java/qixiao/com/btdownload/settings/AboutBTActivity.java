package qixiao.com.btdownload.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import qixiao.com.btdownload.R;
import qixiao.com.btdownload.adapters.AboutBTrecycleAdapter;
import qixiao.com.btdownload.bean.AboutBean;
import qixiao.com.btdownload.core.utils.BitmapUtil;
import qixiao.com.btdownload.core.utils.RecyclerDistanceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/20.
 */

public class AboutBTActivity extends Activity {
    private RecyclerView mRecyclerView;
    private AboutBTrecycleAdapter recyclerAdapter;
    private List<AboutBean> listBean;
    private RelativeLayout rl_about_title;
    private ImageView iv_back;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_bt);
        FindViewById();
        initView();
        initOnClickListner();
    }

    private void initView() {
        AddListData();//添加数据
        mRecyclerView.addItemDecoration(new RecyclerDistanceUtil(2));//设置条目间距
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AboutBTActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        recyclerAdapter = new AboutBTrecycleAdapter(AboutBTActivity.this,listBean);
        mRecyclerView.setAdapter(recyclerAdapter);
        //初始化标题
        iv_back.setImageBitmap(BitmapUtil.getBitmap(this,R.drawable.fanhui));
        tv_title.setText("关于BT下载器");
    }

    private void initOnClickListner() {
        recyclerAdapter.setOnItemClickListener(OnRecyclerViewItemClickListener);
        iv_back.setOnClickListener(onClickListener);
    }

    private void AddListData() {
        listBean = new ArrayList<AboutBean>();
        listBean.add(new AboutBean(BitmapUtil.getBitmap(this,R.drawable.weipinhui),
                "唯品会","一家专门做特卖的网站！"));
        listBean.add(new AboutBean(BitmapUtil.getBitmap(this,R.drawable.baoyou),
                "9.9元包邮","9.9包邮！"));
        listBean.add(new AboutBean(BitmapUtil.getBitmap(this,R.drawable.youku),
                "优酷","优酷，各类视频尽收眼底！"));
        listBean.add(new AboutBean(BitmapUtil.getBitmap(this,R.drawable.aiqiyi),
                "爱奇艺","爱奇艺，你的随身电视！"));
    }

    private void FindViewById() {
        mRecyclerView = ((RecyclerView)findViewById(R.id.recyclerView));
        rl_about_title = ((RelativeLayout) findViewById(R.id.rl_about_title));
        rl_about_title.findViewById(R.id.iv_unfold).setVisibility(View.GONE);
        iv_back = ((ImageView) rl_about_title.findViewById(R.id.iv_back));
        tv_title = ((TextView) rl_about_title.findViewById(R.id.tv_title));
    }

    //点击返回箭头事件
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    //item点击事件
    public AboutBTrecycleAdapter.OnRecyclerViewItemClickListener OnRecyclerViewItemClickListener = new AboutBTrecycleAdapter.OnRecyclerViewItemClickListener(){
        @Override
        public void onItemClick(View view, int i) {
            Toast.makeText(AboutBTActivity.this,"点击了item"+ i +"的下载键", Toast.LENGTH_SHORT).show();
        }
    };

}

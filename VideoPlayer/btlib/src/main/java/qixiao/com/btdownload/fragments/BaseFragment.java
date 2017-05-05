package qixiao.com.btdownload.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ron on 2016/5/13.
 */
public abstract class BaseFragment extends Fragment
{

    protected  boolean ViewHadDestory;
    private View v;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        v = inflater.inflate(getContentView(),null);
        findViews(v);
        initViews();
        initLisenter();
        ViewHadDestory = false;
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * <p>使用findViewById方法，找到所有的Views</p>
     * <hr/>
     * <p>主要是为了方便优化维护</p>
     */
    public abstract  void findViews(View view);

    /**
     * <p>初始化Views</p>
     * <hr/>
     * <p>主要是为了方便优化维护</p>
     */
    public abstract  void initViews();

    /**
     * <p>设置Views的监听事件</p>
     * <hr/>
     * <p>主要是为了方便优化维护</p>
     */
    public abstract  void initLisenter();

    /**
     * <p>设置setContentView的Res文件ID号</p>
     * @return
     */
    public abstract int getContentView();

    @Override
    public void onDestroyView()
    {
        ViewHadDestory = true;
        super.onDestroyView();
    }
}

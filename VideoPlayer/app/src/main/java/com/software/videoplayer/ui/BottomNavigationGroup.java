package com.software.videoplayer.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.RadioGroup;

/**
 * Created by moon on 2017/2/13.
 */

public class BottomNavigationGroup extends RadioGroup implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;

    public BottomNavigationGroup(Context context) {
        super(context);
    }

    public BottomNavigationGroup(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        for (int i = 0; i < getChildCount(); i++) {
            final int position = i;
            getChildAt(position).setOnClickListener(view -> {
                setClickedViewChecked(position);
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(position, false);
                }
            });
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        updateGradient(position, positionOffset);
    }

    private void updateGradient(int position, float positionOffset) {
        if (positionOffset > 0) {
            ((BottomNavigationButton) getChildAt(position)).updateAlpha(255 * (1 - positionOffset));
            ((BottomNavigationButton) getChildAt(position+1)).updateAlpha(255 * positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {
        setSelectedViewChecked(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setSelectedViewChecked(int position) {
        for (int i=0;i<getChildCount();i++) {
            ((BottomNavigationButton) getChildAt(i)).setRadioButtonChecked(false);
        }
        ((BottomNavigationButton)getChildAt(position)).setRadioButtonChecked(true);
    }

    public void setClickedViewChecked(int position) {
        for (int i=0;i<getChildCount();i++) {
            ((BottomNavigationButton) getChildAt(i)).setRadioButtonChecked(false);
        }
        ((BottomNavigationButton)getChildAt(position)).setRadioButtonChecked(true);
    }
}

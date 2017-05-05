package com.software.videoplayer.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.software.videoplayer.R;

/**
 * Created by moon on 2017/2/13.
 */
public class BottomNavigationButton extends RadioButton {

    private Paint mFocusPaint;
    private Paint mTextPaint;
    private Paint mDeFocusPaint;

    private int iconWidth;
    private int iconPadding;
    private int iconHeight;

    private Bitmap mDeFocusBitmap;
    private Bitmap mFocusBitmap;
    private Drawable mFocusDrawable;
    private Drawable mDeFocusDrawable;

    private Rect mRect;
    private int mAlpha;
    private int mColor;
    private float mFountHeight;
    private float mTextWidth;

    public BottomNavigationButton(Context context) {
        super(context,null);
    }


    public BottomNavigationButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mFocusPaint = new Paint();
        mTextPaint = new Paint();
        mDeFocusPaint = new Paint();
        mFocusPaint.setAntiAlias(true);
        mTextPaint.setAntiAlias(true);
        mDeFocusPaint.setAntiAlias(true);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BottomNavigationButton);
        mFocusDrawable = ta.getDrawable(R.styleable.BottomNavigationButton_focus_icon);
        mDeFocusDrawable = ta.getDrawable(R.styleable.BottomNavigationButton_de_focus_icon);
        mColor = ta.getColor(R.styleable.BottomNavigationButton_focus_color, Color.YELLOW);
        ta.recycle();

        setButtonDrawable(null);
        if (mDeFocusDrawable != null) {
            setCompoundDrawablesWithIntrinsicBounds(null, mDeFocusDrawable, null, null);
            mDeFocusDrawable = getCompoundDrawables()[1];
        }

        if (mFocusDrawable==null || mDeFocusDrawable == null) {
            throw new RuntimeException("icon attribute should be defined");
        }


        mRect = mDeFocusDrawable.getBounds();
        iconWidth = mRect.width();
        iconHeight = mRect.height();
        iconPadding = getCompoundDrawablePadding();

        Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
        mFountHeight = (float) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
        mTextWidth = StaticLayout.getDesiredWidth(getText(), getPaint());

        mDeFocusBitmap = getBitmapFromDrawable(mDeFocusDrawable);
        mFocusBitmap = getBitmapFromDrawable(mFocusDrawable);

        if (isChecked()) {
            mAlpha = 255;
        }

    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            throw new RuntimeException("The Drawable must be an instance of BitmapDrawable");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawDeFocusIcon(canvas);
        drawFocusIcon(canvas);
        drawDeFocusText(canvas);
        drawFocusText(canvas);
    }
    private void drawDeFocusIcon(Canvas canvas) {
        mDeFocusPaint.setAlpha(255 - mAlpha);
        canvas.drawBitmap(mDeFocusBitmap, (getWidth() - iconWidth) / 2, iconPadding, mDeFocusPaint);
    }
    private void drawFocusIcon(Canvas canvas) {
        mFocusPaint.setAlpha(mAlpha);
        canvas.drawBitmap(mFocusBitmap, (getWidth() - iconWidth) / 2, iconPadding, mFocusPaint);
    }
    private void drawDeFocusText(Canvas canvas) {
        mTextPaint.setColor(getCurrentTextColor());
        mTextPaint.setAlpha(255 - mAlpha);
        mTextPaint.setTextSize(getTextSize());
        canvas.drawText(getText().toString(), getWidth() / 2 - mTextWidth / 2, iconHeight + iconPadding + getPaddingTop() + mFountHeight+getPaddingBottom(), mTextPaint);

    }
    private void drawFocusText(Canvas canvas) {
        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(mAlpha);
        canvas.drawText(getText().toString(),getWidth()/2-mTextWidth/2,iconHeight+iconPadding+getPaddingTop()+mFountHeight+getPaddingBottom(),mTextPaint);
    }
    public void updateAlpha(float alpha) {
        mAlpha = (int) alpha;
        invalidate();
    }

    public void setRadioButtonChecked(boolean isChecked) {
        setChecked(isChecked);
        if (isChecked) {
            mAlpha = 255;
        } else {
            mAlpha = 0;
        }
        invalidate();
    }
}

package com.coco3g.daishu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.coco3g.daishu.R;


public class BannerSelectPointView extends View {
    Context mContext;
    int mViewNum = 0;
    int mCurrSelectIndex = 0;

    public BannerSelectPointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    public BannerSelectPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    public BannerSelectPointView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    public void setPointNum(int num) {
        mViewNum = num;
    }

    public void setSelectIndex(int index) {
        mCurrSelectIndex = index;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        Bitmap b = null;
        for (int i = 0; i < mViewNum; i++) {
            if (i == mCurrSelectIndex) {
                b = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_banner_selected_icon);
            } else {
                b = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_banner_unselected_icon);
            }
//            float vl = (float) getMeasuredWidth() / 2 - (float) (mViewNum * b.getWidth() + (mViewNum - 1) * b.getWidth()) / 2;
            float vl = 130 - (float) (mViewNum * b.getWidth() + (mViewNum - 1) * b.getWidth()/2);
            vl = vl + (float) i * 3 * b.getWidth();
            canvas.drawBitmap(b, vl, getMeasuredHeight() - 2 * b.getHeight(), null);
        }
    }

}

package com.coco3g.daishu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.coco3g.daishu.R;

/**
 * Created by coco3g on 17/5/17.
 */

public class BusinessMapView extends RelativeLayout {
    private Context mContext;
    private View mView;


    public BusinessMapView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public BusinessMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public BusinessMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }


    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.view_business_map, null);


    }


}

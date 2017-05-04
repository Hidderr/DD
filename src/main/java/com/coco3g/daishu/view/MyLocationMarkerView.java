package com.coco3g.daishu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Global;

/**
 * Created by lisen on 16/4/5.
 */
public class MyLocationMarkerView extends RelativeLayout implements View.OnClickListener {
    Context mContext;
    View mView;
    private RelativeLayout mRelativeRoot;
    private TextView mTxtStoreName;
    private ImageView mImageLocation;
    private LayoutParams marker_lp;

    public MyLocationMarkerView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public MyLocationMarkerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public MyLocationMarkerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater lay = LayoutInflater.from(mContext);
        mView = lay.inflate(R.layout.view_my_location_marker, this);
        mRelativeRoot = (RelativeLayout) mView.findViewById(R.id.relative_my_location_marker_root);
        mTxtStoreName = (TextView) mView.findViewById(R.id.tv_my_location_marker_name);
        mImageLocation = (ImageView) mView.findViewById(R.id.image_my_location_marker_location);
        //
        marker_lp = new LayoutParams(Global.screenWidth / 5, Global.screenWidth / 6);
        mRelativeRoot.setLayoutParams(marker_lp);
        //
//        mImageLocation.setOnClickListener(this);
//        mTxtStoreName.setOnClickListener(this);


    }


//    public void setInfo(String title) {
//        mTxtStoreName.setText(title);
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_my_marker_location:  //定位图标

                break;

            case R.id.tv_my_marker_store_name:   //文字

                break;
        }

    }

//    public interface OnMarkerClickListener {
//        void markerClick();
//    }


}

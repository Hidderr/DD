package com.coco3g.daishu.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.DriveRouteActivity;
import com.coco3g.daishu.bean.RepairStoreBean;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by coco3g on 17/5/17.
 */

public class BusinessMapView extends RelativeLayout implements View.OnClickListener {
    private Context mContext;
    private View mView;

    public MyMapView myMapView;
    private RelativeLayout mRelativeStore;
    private RelativeLayout.LayoutParams store_lp, thumb_lp;
    private AMap aMap;
    private TextView mTxtName, mTxtAddress, mTxtPhone;
    private ImageView mImageThumb, mImageRoute;
    //

    private String typeid = "2";  //获取的地点类型  	门店类型：1=洗车店，2=维修点，3=附近门店 4=维修养护

    private Marker detailMarker;
    private LatLonPoint mCurrLatLonPoint = null;

    public boolean isMapInit = false;//地图是否初始化了
    private Bundle savedInstanceState;


    public BusinessMapView(Context context, Bundle savedInstanceState) {
        super(context);
        mContext = context;
        this.savedInstanceState = savedInstanceState;
        initView(savedInstanceState);
    }

    public BusinessMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        mContext = context;
//        initView();
    }

    public BusinessMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        mContext = context;
//        initView();
    }


    private void initView(Bundle savedInstanceState) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.view_business_map, this);
        myMapView = (MyMapView) mView.findViewById(R.id.map_business_map);
        mTxtName = (TextView) mView.findViewById(R.id.tv_business_map_store_name);
        mTxtAddress = (TextView) mView.findViewById(R.id.tv_business_map_store_address);
        mTxtPhone = (TextView) mView.findViewById(R.id.tv_business_map_store_phone);
        mImageThumb = (ImageView) mView.findViewById(R.id.image_business_map_store_thumb);
        mImageRoute = (ImageView) mView.findViewById(R.id.image_business_map_store_route);
        mRelativeStore = (RelativeLayout) mView.findViewById(R.id.relative_business_map_store);
        store_lp = new RelativeLayout.LayoutParams(Global.screenWidth, Global.screenHeight / 5);
        store_lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        int margin_10 = Global.dipTopx(mContext, 10f);
        mRelativeStore.setLayoutParams(store_lp);
        thumb_lp = new RelativeLayout.LayoutParams(Global.screenHeight / 5, Global.screenHeight / 5);
        thumb_lp.addRule(RelativeLayout.CENTER_VERTICAL);
        thumb_lp.setMargins(margin_10, margin_10, Global.dipTopx(mContext, 5f), Global.dipTopx(mContext, 5f));
        mImageThumb.setLayoutParams(thumb_lp);
        //
//        myMapView.setTypeid(typeid);
//        myMapView.init(savedInstanceState, true, false);
//        //
//        mImageRoute.setOnClickListener(this);
//        //
//        myMapView.setOnLocationSuccessedListener(new MyMapView.OnLocationSuccessedListener() {
//            @Override
//            public void locationSuccessed(LatLng latLng) {
//                mCurrLatLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
//            }
//        });
//        myMapView.setOnShowStoreListener(new MyMapView.OnShowStoreListener() {
//            @Override
//            public void showStore(boolean visible, PoiItem mCurrentPoi, Marker detailMarker1) {
//                if (visible) {
//                    mRelativeStore.setVisibility(View.VISIBLE);
//                    if (mCurrentPoi != null) {
//                        detailMarker = detailMarker1;
//                        setCarStoreInfo(mCurrentPoi);
//                    }
//                } else {
//                    mRelativeStore.setVisibility(View.GONE);
//                }
//            }
//        });


    }

    public void mapInit() {
        isMapInit = true;
        //

        myMapView.setTypeid(typeid);
        myMapView.init(savedInstanceState, true, false);
        //
        mImageRoute.setOnClickListener(this);
        //
        myMapView.setOnLocationSuccessedListener(new MyMapView.OnLocationSuccessedListener() {
            @Override
            public void locationSuccessed(LatLng latLng) {
                mCurrLatLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
            }
        });
        myMapView.setOnShowStoreListener(new MyMapView.OnShowStoreListener() {
            @Override
            public void showStore(boolean visible, PoiItem mCurrentPoi, Marker detailMarker1) {
                if (visible) {
                    mRelativeStore.setVisibility(View.VISIBLE);
                    if (mCurrentPoi != null) {
                        detailMarker = detailMarker1;
                        setCarStoreInfo(mCurrentPoi);
                    }
                } else {
                    mRelativeStore.setVisibility(View.GONE);
                }
            }
        });


    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.image_business_map_store_route:
                intent = new Intent(mContext, DriveRouteActivity.class);
                PoiItem mCurrentPoi = (PoiItem) detailMarker.getObject();
                RepairStoreBean bean = new RepairStoreBean();
                bean.lat = mCurrentPoi.getLatLonPoint().getLatitude();
                bean.lng = mCurrentPoi.getLatLonPoint().getLongitude();
                bean.photos = mCurrentPoi.getPhotos().get(0).getUrl();
                bean.title = mCurrentPoi.getTitle();
                bean.address = mCurrentPoi.getSnippet();
                bean.phone = mCurrentPoi.getTel();
                //
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", bean);
                intent.putExtras(bundle);
                intent.putExtra("startlat", mCurrLatLonPoint.getLatitude());
                intent.putExtra("startlng", mCurrLatLonPoint.getLongitude());
                mContext.startActivity(intent);

                break;
        }

    }


    //设置修理店信息
    private void setCarStoreInfo(final PoiItem mCurrentPoi) {
        mTxtName.setText(mCurrentPoi.getTitle());
        mTxtAddress.setText("地址： " + mCurrentPoi.getSnippet());
        mTxtPhone.setText("电话： " + mCurrentPoi.getTel());
        //
        String thumb = mCurrentPoi.getPhotos().get(0).getUrl();
        if (TextUtils.isEmpty(thumb)) {
            return;
        }
        ImageLoader.getInstance().displayImage(mCurrentPoi.getPhotos().get(0).getUrl(), mImageThumb, new DisplayImageOptionsUtils().init());
    }

}

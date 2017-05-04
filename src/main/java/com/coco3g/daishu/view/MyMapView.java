package com.coco3g.daishu.view;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.coco3g.daishu.R;
import com.coco3g.daishu.utils.LocationUtil;
import com.coco3g.daishu.utils.RequestPermissionUtils;

/**
 * Created by coco3g on 17/3/29.
 */

public class MyMapView extends RelativeLayout {
    private Context mContext;
    private View mView;
    public TextureMapView mMapView;
    public AMap aMap;
    private UiSettings mUiSettings;
    //当前位置的经纬度
    private double mCurrLat, mCurrLng;

    private OnLocationSuccessedListener onLocationSuccessedListener;


    public MyMapView(Context context) {
        super(context);
        mContext = context;
        initView();
    }


    public MyMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public MyMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();

    }

    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.view_mymap, this);
        mMapView = (TextureMapView) mView.findViewById(R.id.txtture_mapview);
    }

    //初始化地图的配置
    public void init(Bundle savedInstanceState, boolean startLocation) {
        mMapView.onCreate(savedInstanceState);// 此方法必须重写

        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        mUiSettings = aMap.getUiSettings();
        //
        mUiSettings.setZoomControlsEnabled(false);  //缩放按钮设置不可见
        mUiSettings.setTiltGesturesEnabled(false);  //设置不可倾斜
        mUiSettings.setRotateGesturesEnabled(false);  //设置不可旋转
//        aMap.animateCamera(CameraUpdateFactory.zoomTo(18f));  //缩放级别

//        aMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
//            @Override
//            public View getInfoWindow(Marker marker) {
//                return null;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker) {
//                MyMarkerView view = new MyMarkerView(mContext);
//                view.setInfo(marker.getTitle());
//
//                return view;
//            }
//        });

//        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                if (marker.isInfoWindowShown()) {
//                    marker.hideInfoWindow();
//                } else {
//                    marker.showInfoWindow();
//                }
//                return true;
//            }
//        });

        if (startLocation) {
            startLocation();
        }
    }


    public void setLatLng(double lat, double lng) {
        this.mCurrLat = lat;
        this.mCurrLng = lng;
    }


    //定位添加marker
    public void startLocation() {
        new RequestPermissionUtils(mContext).aleraPermission(Manifest.permission.ACCESS_FINE_LOCATION, 1);
        //定位
        new LocationUtil(mContext).initLocationAndStart(true, 1000, false, null).setAMapLocationChanged(new LocationUtil.AMapLocationChanged() {
            @Override
            public void aMapLocation(AMapLocation aMapLocation) {
                String city = aMapLocation.getCity();
                mCurrLat = aMapLocation.getLatitude();
                mCurrLng = aMapLocation.getLongitude();
                Log.e("定位结果", "city " + city + "  mCurrLat   " + mCurrLat + "  mCurrLng" + mCurrLng);
                locationSuccessed(new LatLng(mCurrLat, mCurrLng));
            }
        });
    }

//    //地图视图移动到我的位置
//    public void moveToMyLocation() {
//        if (mCurrLat == 0 || mCurrLng == 0) {
//            startLocation();
//            return;
//        }
//        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrLat, mCurrLng), 16));
//        //
//        MarkerOptions markerOptions = new MarkerOptions();
//        LatLng latLng = new LatLng(mCurrLat, mCurrLng);
//        markerOptions.position(latLng);
//        markerOptions.title("定位点");
//        markerOptions.visible(true);
//        //
//        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.pic_location_icon);
//        markerOptions.icon(bitmapDescriptor);
//        aMap.addMarker(markerOptions);
//
//    }


    /**
     * 方法必须重写
     */
    public void onResume() {
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    public void onPause() {
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    public void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    public void onDestroy() {
        mMapView.onDestroy();
    }


    public interface OnLocationSuccessedListener {
        void locationSuccessed(LatLng latLng);
    }

    public void setOnLocationSuccessedListener(OnLocationSuccessedListener onLocationSuccessedListener) {
        this.onLocationSuccessedListener = onLocationSuccessedListener;
    }

    public void locationSuccessed(LatLng latLng) {
        if (onLocationSuccessedListener != null) {
            onLocationSuccessedListener.locationSuccessed(latLng);
        }

    }


}

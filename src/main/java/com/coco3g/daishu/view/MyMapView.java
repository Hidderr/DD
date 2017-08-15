package com.coco3g.daishu.view;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.Photo;
import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.CarDetailTypeActivity;
import com.coco3g.daishu.amap.MyPoiOverlay;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.LocationUtil;
import com.coco3g.daishu.utils.RequestPermissionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by coco3g on 17/3/29.
 */

public class MyMapView extends RelativeLayout implements AMap.OnMarkerClickListener {
    private Context mContext;
    private View mView;
    public TextureMapView mMapView;
    private ImageView mImageStartLocation;
    public AMap aMap;
    private UiSettings mUiSettings;
    private MyProgressDialog myProgressDialog;

    private OnLocationSuccessedListener onLocationSuccessedListener;
    private OnShowStoreListener onShowStoreListener;

    //
    private LatLonPoint mCurrLatLonPoint = null;
    private double mCurrLat = 0, mCurrLng = 0;
    private Marker detailMarker;
    private Marker mlastMarker;
    private MyPoiOverlay poiOverlay;// poi图层
    private ArrayList<PoiItem> poiItems = new ArrayList<>();// poi数据
    private float mBaseDistance = 10000;  //距离定位点最小的半径

    //
    boolean showDialog;

    //
    private String typeid = "";  //获取的地点类型  	2=洗车店，1=维修养护和维修救援 ,-1 :某个汽车类型的某个配置的车型的最新报价
    private String carid = "";  //汽车最新报价的id


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
        mImageStartLocation = (ImageView) mView.findViewById(R.id.image_mymap_start_location);
        //
        mImageStartLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaseDistance = 10000;
                startLocation();
            }
        });
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    public void setCarNewsOfferId(String carid) {
        this.carid = carid;
    }

    //初始化地图的配置
    public void init(Bundle savedInstanceState, boolean startLocation, boolean showDialog) {
        this.showDialog = showDialog;
        mMapView.onCreate(savedInstanceState);// 此方法必须重写

        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        mUiSettings = aMap.getUiSettings();
        //
        mUiSettings.setZoomControlsEnabled(false);  //缩放按钮设置不可见
        mUiSettings.setTiltGesturesEnabled(false);  //设置不可倾斜
        mUiSettings.setRotateGesturesEnabled(false);  //设置不可旋转
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT); //设置高德Logo在右下角
        aMap.setOnMarkerClickListener(this);
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


        //监听地图的移动
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
//                ClusterOverlay.currentMarker.hideInfoWindow();
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                float currDistance = getLargeDistance();
                if (currDistance > mBaseDistance) {   //距离默认是10000米，最大不能超过
                    mBaseDistance = currDistance;
                    if (typeid.equals("-1")) {
                        getNewestOffer(false);
                    } else {
                        getRepairStoreList(null, false);
                    }
                }
            }
        });


        if (startLocation) {
            if (showDialog) {
                myProgressDialog = MyProgressDialog.show(mContext, "定位中...", false, true);
            }
            startLocation();
        }
    }


    //定位添加marker
    public void startLocation() {
        new RequestPermissionUtils(mContext).aleraPermission(Manifest.permission.ACCESS_FINE_LOCATION, 1);
        //定位
        new LocationUtil(mContext).initLocationAndStart(true, 1000, false, null).setAMapLocationChanged(new LocationUtil.AMapLocationChanged() {
            @Override
            public void aMapLocation(AMapLocation aMapLocation) {
                if (myProgressDialog != null) {
                    myProgressDialog.dismiss();
                }
                //
                String city = aMapLocation.getCity();
                Global.mCurrLat = mCurrLat = aMapLocation.getLatitude();
                Global.mCurrLng = mCurrLng = aMapLocation.getLongitude();
                Log.e("定位结果", "city " + city + "  mCurrLat   " + mCurrLat + "  mCurrLng" + mCurrLng);

                mCurrLatLonPoint = new LatLonPoint(mCurrLat, mCurrLng);
                locationSuccessed(new LatLng(mCurrLat, mCurrLng));

                if (mCurrLat != 0 && mCurrLng != 0) {
                    //
                    if (mlastMarker != null) {
                        resetlastmarker();
                    }
                    //清理之前搜索结果的marker
                    if (poiOverlay != null) {
                        poiOverlay.removeFromMap();
                        poiOverlay = null;
                    }
                    aMap.clear();
                    //
                    if (showDialog) {
                        if (typeid.equals("-1")) {  //汽车最新报价
                            getNewestOffer(true);
                        } else {
                            getRepairStoreList(getResources().getString(R.string.loading), true);  //维修养护
                        }

                    } else {
                        if (typeid.equals("-1")) {   //汽车最新报价
                            getNewestOffer(true);
                        } else {
                            getRepairStoreList(null, true);  //维修养护
                        }
                    }
                }


            }
        });
    }


    //计算地图可见范围内，当前定位点和可见范围内任一点的最远距离
    public float getLargeDistance() {
        //位置改变以后的地图西南角的经纬度
        LatLngBounds visibleBounds = aMap.getProjection().getVisibleRegion().latLngBounds;
        //西南角经纬度
        double southwest_lat = visibleBounds.southwest.latitude;
        double southwest_lng = visibleBounds.southwest.longitude;
        LatLng southwest_latLng = new LatLng(southwest_lat, southwest_lng);
//        Log.e("经纬度，西南", visibleBounds.southwest.longitude + "," + visibleBounds.southwest.latitude);
        //东北角经纬度
        double northeast_lat = visibleBounds.northeast.latitude;
        double northeast_lng = visibleBounds.northeast.longitude;
        LatLng northeast_latLng = new LatLng(northeast_lat, northeast_lng);
//        Log.e("经纬度，东北", visibleBounds.northeast.longitude + "," + visibleBounds.northeast.latitude);

        //自己定位位置的经纬度
        LatLng currLatLng = new LatLng(mCurrLat, mCurrLng);
        float southwest_distance = AMapUtils.calculateLineDistance(southwest_latLng, currLatLng);
        float northeast_distance = AMapUtils.calculateLineDistance(northeast_latLng, currLatLng);
        //
        float distance = southwest_distance > northeast_distance ? southwest_distance : northeast_distance;
        Log.e("地图视野距离：", distance + "$$$$");
        return distance;
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


    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getObject() != null) {

            PoiItem mCurrentPoi = (PoiItem) marker.getObject();
            if (mCurrentPoi.getPoiId().equals("#")) {  //代表我的位置
                marker.showInfoWindow();
                whetherToShowDetailInfo(false);
                return true;
            }

            whetherToShowDetailInfo(true);
            try {

                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    // 将之前被点击的marker置为原来的状态
                    resetlastmarker();
                    mlastMarker = marker;
                }
                detailMarker = marker;
                detailMarker.showInfoWindow();

//                setRepairStoreInfo(mCurrentPoi);
                onShowStore(true, mCurrentPoi, detailMarker);
            } catch (Exception e) {
                // TODO: handle exception
            }
        } else {
            whetherToShowDetailInfo(false);
            resetlastmarker();
        }


        return true;
    }


    //筛选的时候
    public void refreshData(String typeid) {
        poiItems.clear();
        this.typeid = typeid;
        if (typeid.equals("-1")) {
            getNewestOffer(true);
        } else {
            getRepairStoreList(mContext.getResources().getString(R.string.loading), true);
        }
    }


    //获取附近的汽车修理店的信息
    public void getRepairStoreList(String loadMsg, final boolean isZoomToSpan) {
        HashMap<String, String> params = new HashMap<>();
        params.put("lat", mCurrLatLonPoint.getLatitude() + "");
        params.put("lng", mCurrLatLonPoint.getLongitude() + "");
        params.put("distance", mBaseDistance + "");
        if (!TextUtils.isEmpty(typeid)) {
            params.put("joinid", typeid);  //2=洗车店，1=维修养护和维修救援，附近门店(不传参)，汽修厂、爱车保姆快修店（根据获取的维修类型id）
        }
        Log.e("地图传参", "distance " + mBaseDistance + "   joinid " + typeid);
        new BaseDataPresenter(mContext).loadData(DataUrl.GET_REPAIR_STORE, params, loadMsg, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {

                ArrayList<Map<String, String>> repairList = (ArrayList<Map<String, String>>) data.response;
                if (repairList == null) {
                    repairList = new ArrayList<Map<String, String>>();
                }
                Log.e("门店数量", repairList.size() + "");
                showRepairStore(repairList, isZoomToSpan);

            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, mContext);
            }

            @Override
            public void onError() {
            }
        });
    }


    //获取汽车最新报价的店
    public void getNewestOffer(final boolean isZoomToSpan) {
        HashMap<String, String> params = new HashMap<>();
        params.put("carid", carid);
        params.put("lat", mCurrLatLonPoint.getLatitude() + "");
        params.put("lng", mCurrLatLonPoint.getLongitude() + "");
        params.put("distance", mBaseDistance + "");
        new BaseDataPresenter(mContext).loadData(DataUrl.GET_CAR_NEWEST_OFFER, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, String>> newsOfferStoreList = (ArrayList<Map<String, String>>) data.response;
                //
                if (newsOfferStoreList == null || newsOfferStoreList.size() <= 0) {
//                    Global.showToast(, mContext);
                    return;
                }
                showRepairStore(newsOfferStoreList, isZoomToSpan);

            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, mContext);
            }

            @Override
            public void onError() {
            }
        });
    }


    public void clearMarker() {
        //清理之前搜索结果的marker
        if (poiOverlay != null) {
            poiOverlay.removeFromMap();
        }
        aMap.clear();
    }


    //将获取的店铺在地图上显示出来
    public void showRepairStore(ArrayList<Map<String, String>> repairList, boolean isZoomToSpan) {

//        ArrayList<PoiItem> poiItemList = new ArrayList<PoiItem>();
        int size = repairList.size();
        for (int i = 0; i < size + 1; i++) {

            PoiItem poiItem = null;

            if (i == size) {   //添加自己的位置坐标
                LatLonPoint latLonPoint = new LatLonPoint(mCurrLat, mCurrLng);
                poiItem = new PoiItem("#", latLonPoint, "我的位置", null);
            } else {
                Map<String, String> itemMap = repairList.get(i);
                double lat = Double.parseDouble(itemMap.get("lat"));
                double lng = Double.parseDouble(itemMap.get("lng"));
                LatLonPoint latLonPoint = new LatLonPoint(lat, lng);
                poiItem = new PoiItem(itemMap.get("id"), latLonPoint, itemMap.get("title"), itemMap.get("address"));
                poiItem.setTel(itemMap.get("phone"));
                //
                ArrayList<Photo> photos = new ArrayList<Photo>();
                Photo photo = new Photo();
                photo.setUrl(itemMap.get("thumb"));
                photos.add(photo);
                poiItem.setPhotos(photos);
            }
            //
            poiItems.add(poiItem);
        }

        //清除POI信息显示
        whetherToShowDetailInfo(false);
        //并还原点击marker样式
        if (mlastMarker != null) {
            resetlastmarker();
        }
        //清理之前搜索结果的marker
        if (poiOverlay != null) {
            poiOverlay.removeFromMap();
            poiOverlay = null;
        }
        aMap.clear();
        poiOverlay = new MyPoiOverlay(mContext, aMap, poiItems);
        poiOverlay.addToMap();
        if (isZoomToSpan) {
            poiOverlay.zoomToSpan();
        }

    }

    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            onShowStore(true, null, detailMarker);

        } else {
            onShowStore(false, null, detailMarker);
        }
    }

    // 将之前被点击的marker置为原来的状态
    private void resetlastmarker() {
        int index = poiOverlay.getPoiIndex(mlastMarker);

//        MyMarkerView markerView = new MyMarkerView(mContext);
//        markerView.setInfo(mlastMarker.getTitle());
        mlastMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.pic_location_red_icon));
        mlastMarker = null;

    }


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

    //
    public interface OnShowStoreListener {
        void showStore(boolean visible, PoiItem mCurrentPoi, Marker detailMarker);
    }

    public void setOnShowStoreListener(OnShowStoreListener onShowStoreListener) {
        this.onShowStoreListener = onShowStoreListener;
    }

    public void onShowStore(boolean visible, PoiItem mCurrentPoi, Marker detailMarker) {
        if (onShowStoreListener != null) {
            onShowStoreListener.showStore(visible, mCurrentPoi, detailMarker);
        }
    }


}

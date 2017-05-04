package com.coco3g.daishu.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.coco3g.daishu.R;
import com.coco3g.daishu.amap.AMapUtil;
import com.coco3g.daishu.amap.DrivingRouteOverlay;
import com.coco3g.daishu.bean.RepairStoreBean;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.coco3g.daishu.view.TopBarView;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * 驾车出行路线规划 实现
 */
public class DriveRouteActivity extends BaseActivity implements OnMapClickListener,
        OnMarkerClickListener, OnInfoWindowClickListener, InfoWindowAdapter, OnRouteSearchListener {
    private TopBarView mTopbar;
    private AMap aMap;
    private MapView mapView;
    private Context mContext;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private LatLonPoint mStartPoint = null;//起点，39.942295,116.335891
    private LatLonPoint mEndPoint = null;//终点，39.995576,116.481288

    private final int ROUTE_TYPE_DRIVE = 2;

    private ProgressDialog progDialog = null;// 搜索时进度条

    private RepairStoreBean currStoreBean = new RepairStoreBean();
    private double startLat, startLng;


    //
    private TextView mTxtName, mTxtAddress, mTxtPhone;
    private ImageView mImageThumb, mImageNavigation;
    private RelativeLayout mRelativeStore;
    private RelativeLayout.LayoutParams store_lp, thumb_lp;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_drive_route);

        mContext = this;
        mapView = (MapView) findViewById(R.id.map_dirve_route);
        mapView.onCreate(bundle);// 此方法必须重写
        //
        init();
        //
        Bundle bundle1 = getIntent().getExtras();
        currStoreBean = (RepairStoreBean) bundle1.getSerializable("data");
        startLat = getIntent().getDoubleExtra("startlat", 0);
        startLng = getIntent().getDoubleExtra("startlng", 0);
        mStartPoint = new LatLonPoint(startLat, startLng);
        mEndPoint = new LatLonPoint(currStoreBean.lat, currStoreBean.lng);
        //
        setRepairStoreInfo();

        //开始规划路径
        searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        registerListener();
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        mTopbar = (TopBarView) findViewById(R.id.topbar_dirve_route);
        mTopbar.setTitle("导航中");
        mTxtName = (TextView) findViewById(R.id.tv_drive_route_store_name);
        mTxtAddress = (TextView) findViewById(R.id.tv_repair_route_store_address);
        mTxtPhone = (TextView) findViewById(R.id.tv_drive_route_store_phone);
        mImageThumb = (ImageView) findViewById(R.id.image_drive_route_store_thumb);
        mImageNavigation = (ImageView) findViewById(R.id.image_drive_route_store_navigation);
        //
        mRelativeStore = (RelativeLayout) findViewById(R.id.relative_drive_route_repair_store);
        store_lp = new RelativeLayout.LayoutParams(Global.screenWidth, Global.screenHeight / 5);
        store_lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        int margin_10 = Global.dipTopx(this, 10f);
        mRelativeStore.setLayoutParams(store_lp);
        thumb_lp = new RelativeLayout.LayoutParams(Global.screenHeight / 5, Global.screenHeight / 5);
        thumb_lp.addRule(RelativeLayout.CENTER_VERTICAL);
        thumb_lp.setMargins(margin_10, margin_10, Global.dipTopx(this, 5f), Global.dipTopx(this, 5f));
        mImageThumb.setLayoutParams(thumb_lp);
        //
        mImageNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("androidamap://navi?sourceApplication=softname&mCurrLat=" + currStoreBean.lat
//                            + "&lon=" + currStoreBean.lng + "&dev=0&style=0"));
//                    intent.setPackage("com.autonavi.minimap");
//                    startActivity(intent);
//                } catch (ActivityNotFoundException e) {
//                    e.printStackTrace();
//                    Global.showToast("请安装高德地图", DriveRouteActivity.this);
//                }

                Intent intent = new Intent(DriveRouteActivity.this, DriveRouteNavActivity.class);
                intent.putExtra("startlat", mStartPoint.getLatitude());
                intent.putExtra("startlng", mStartPoint.getLongitude());
                intent.putExtra("endlat", mEndPoint.getLatitude());
                intent.putExtra("endlng", mEndPoint.getLongitude());
                startActivity(intent);
            }
        });

    }

    /**
     * 注册监听
     */
    private void registerListener() {
        aMap.setOnMapClickListener(DriveRouteActivity.this);
        aMap.setOnMarkerClickListener(DriveRouteActivity.this);
        aMap.setOnInfoWindowClickListener(DriveRouteActivity.this);
        aMap.setInfoWindowAdapter(DriveRouteActivity.this);

    }

    //设置修理店信息
    private void setRepairStoreInfo() {
        mTxtName.setText(currStoreBean.title);
        mTxtAddress.setText("地址： " + currStoreBean.address);
        mTxtPhone.setText("电话： " + currStoreBean.phone);
        //
        ImageLoader.getInstance().displayImage(currStoreBean.photos, mImageThumb, new DisplayImageOptionsUtils().init());
    }

    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onMapClick(LatLng arg0) {
        // TODO Auto-generated method stub

    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            Global.showToast("定位中，稍后再试...", mContext);
            return;
        }
        if (mEndPoint == null) {
            Global.showToast("终点未设置", mContext);
        }
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            DriveRouteQuery query = new DriveRouteQuery(fromAndTo, mode, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            mContext, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();

                } else if (result != null && result.getPaths() == null) {
                    Global.showToast(getResources().getString(R.string.no_result), mContext);
                }

            } else {
                Global.showToast(getResources().getString(R.string.no_result), mContext);
            }
        } else {
//            Global.showToast(errorCode,mContext);
        }


    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {

    }


    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onRideRouteSearched(RideRouteResult arg0, int arg1) {
        // TODO Auto-generated method stub

    }

}


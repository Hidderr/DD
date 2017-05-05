package com.coco3g.daishu.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.Photo;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.coco3g.daishu.R;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.bean.RepairStoreBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.coco3g.daishu.view.MyMapView;
import com.coco3g.daishu.view.MyMarkerView;
import com.coco3g.daishu.view.MyProgressDialog;
import com.coco3g.daishu.view.TopBarView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepairWebsiteActivity extends BaseActivity implements AMap.OnMarkerClickListener, View.OnClickListener {
    private MyMapView myMapView;
    private TopBarView mTopbar;
    private RelativeLayout mRelativeStore;
    private RelativeLayout.LayoutParams store_lp, thumb_lp;
    private AMap aMap;
    private TextView mTxtName, mTxtAddress, mTxtPhone;
    private ImageView mImageThumb, mImageRoute;
    //
    private String keyWord = "汽车修理店";
    private MyProgressDialog myProgressDialog = null;
    private float mBaseDistance = 10000;  //距离定位点最小的半径
    //
    private String typeid = "2";  //获取的地点类型  	门店类型：1=洗车店，2=维修点，3=附近门店

    //

    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private LatLonPoint mCurrLatLonPoint = null;
    private double mCurrLat = 0, mCurrLng = 0;
    private Marker locationMarker; // 选择的点
    private Marker detailMarker;
    private Marker mlastMarker;
    private PoiSearch poiSearch;
    private MyPoiOverlay poiOverlay;// poi图层
    private ArrayList<PoiItem> poiItems = new ArrayList<>();// poi数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_website);
        myProgressDialog = MyProgressDialog.show(this, "定位中...", false, true);
        typeid = getIntent().getStringExtra("typeid");

        init(savedInstanceState);

    }

    private void init(Bundle savedInstanceState) {
        mTopbar = (TopBarView) findViewById(R.id.topbar_repair_website);
        mTopbar.setTitle("网点维修");
        //
        mTxtName = (TextView) findViewById(R.id.tv_repair_website_store_name);
        mTxtAddress = (TextView) findViewById(R.id.tv_repair_website_store_address);
        mTxtPhone = (TextView) findViewById(R.id.tv_repair_website_store_phone);
        mImageThumb = (ImageView) findViewById(R.id.image_repair_website_store_thumb);
        mImageRoute = (ImageView) findViewById(R.id.image_repair_website_store_route);
        myMapView = (MyMapView) findViewById(R.id.map_repair_website);
        myMapView.init(savedInstanceState, true);
        aMap = myMapView.aMap;
        aMap.setOnMarkerClickListener(this);
        mRelativeStore = (RelativeLayout) findViewById(R.id.relative_repair_website_repair_store);
        store_lp = new RelativeLayout.LayoutParams(Global.screenWidth, Global.screenHeight / 5);
        store_lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        int margin_10 = Global.dipTopx(this, 10f);
        mRelativeStore.setLayoutParams(store_lp);
        thumb_lp = new RelativeLayout.LayoutParams(Global.screenHeight / 5, Global.screenHeight / 5);
        thumb_lp.addRule(RelativeLayout.CENTER_VERTICAL);
        thumb_lp.setMargins(margin_10, margin_10, Global.dipTopx(this, 5f), Global.dipTopx(this, 5f));
        mImageThumb.setLayoutParams(thumb_lp);
        //
        myMapView.setOnLocationSuccessedListener(new MyMapView.OnLocationSuccessedListener() {
            @Override
            public void locationSuccessed(LatLng latLng) {
                mCurrLatLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
                mCurrLat = latLng.latitude;
                mCurrLng = latLng.longitude;
//                doSearchQuery();   //高德地图搜索附近的汽车修理店信息
                myProgressDialog.cancel();

                if (mCurrLat != 0 && mCurrLng != 0) {
                    getRepairStoreList(getResources().getString(R.string.loading), true);  //接口获取信息
                }

            }
        });
        //
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
                    getRepairStoreList(null, false);
                }
            }
        });

        //
        mImageRoute.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.image_repair_website_store_route:
                intent = new Intent(this, DriveRouteActivity.class);
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
                startActivity(intent);

                break;


        }

    }


//    /**
//     * 开始进行poi搜索
//     */
//    /**
//     * 开始进行poi搜索
//     */
//    protected void doSearchQuery() {
//        currentPage = 0;
//        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
//        query.setPageSize(20);// 设置每页最多返回多少条poiitem
//        query.setPageNum(currentPage);// 设置查第一页
//
//        if (mCurrLatLonPoint != null) {
//            poiSearch = new PoiSearch(this, query);
//            poiSearch.setOnPoiSearchListener(this);
//            poiSearch.setBound(new PoiSearch.SearchBound(mCurrLatLonPoint, 5000, true));//
//            // 设置搜索区域为以lp点为圆心，其周围5000米范围
//            poiSearch.searchPOIAsyn();// 异步搜索
//        }
//    }
//
//
//    @Override
//    public void onPoiSearched(PoiResult result, int rcode) {
//        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
//            if (result != null && result.getQuery() != null) {// 搜索poi的结果
//                if (result.getQuery().equals(query)) {// 是否是同一条
//                    poiResult = result;
//                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
//                    List<SuggestionCity> suggestionCities = poiResult
//                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
//                    if (poiItems != null && poiItems.size() > 0) {
//                        //清除POI信息显示
//                        whetherToShowDetailInfo(false);
//                        //并还原点击marker样式
//                        if (mlastMarker != null) {
//                            resetlastmarker();
//                        }
//                        //清理之前搜索结果的marker
//                        if (poiOverlay != null) {
//                            poiOverlay.removeFromMap();
//                        }
//                        aMap.clear();
//                        poiOverlay = new MyPoiOverlay(aMap, poiItems);
//                        poiOverlay.addToMap();
//                        poiOverlay.zoomToSpan();
//
//                        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(
//                                getResources(), R.mipmap.pic_location_arrow_icon)))
//                                .position(new LatLng(mCurrLatLonPoint.getLatitude(), mCurrLatLonPoint.getLongitude())));
//
//
//                    } else if (suggestionCities != null
//                            && suggestionCities.size() > 0) {
//                    } else {
//                        Global.showToast(getResources().getString(R.string.no_result), this);
//                    }
//                }
//            } else {
//                Global.showToast(getResources().getString(R.string.no_result), this);
//            }
//        } else {
//            Global.showToast(rcode + "", this);
//        }
//    }
//
//    @Override
//    public void onPoiItemSearched(com.amap.api.services.core.PoiItem poiItem, int i) {
//
//    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getObject() != null) {
            whetherToShowDetailInfo(true);
            try {
                PoiItem mCurrentPoi = (PoiItem) marker.getObject();
                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    // 将之前被点击的marker置为原来的状态
                    resetlastmarker();
                    mlastMarker = marker;
                }
                detailMarker = marker;
                detailMarker.showInfoWindow();
//                detailMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.pic_location_icon)));

                setRepairStoreInfo(mCurrentPoi);
            } catch (Exception e) {
                // TODO: handle exception
            }
        } else {
            whetherToShowDetailInfo(false);
            resetlastmarker();
        }


        return true;
    }

    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            mRelativeStore.setVisibility(View.VISIBLE);

        } else {
            mRelativeStore.setVisibility(View.GONE);

        }
    }

    // 将之前被点击的marker置为原来的状态
    private void resetlastmarker() {
        int index = poiOverlay.getPoiIndex(mlastMarker);

        MyMarkerView markerView = new MyMarkerView(RepairWebsiteActivity.this);
        markerView.setInfo(mlastMarker.getTitle());
        mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(markerView, Global.screenWidth / 5, Global.screenWidth / 6)));
        mlastMarker = null;

    }

    //设置修理店信息
    private void setRepairStoreInfo(final PoiItem mCurrentPoi) {
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


    @Override
    public void onResume() {
        super.onResume();
        myMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        myMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        myMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myMapView.onDestroy();
    }


    /**
     * 自定义PoiOverlay
     */

    private class MyPoiOverlay {
        private AMap mamap;
        private List<com.amap.api.services.core.PoiItem> mPois;
        private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();

        public MyPoiOverlay(AMap amap, List<com.amap.api.services.core.PoiItem> pois) {
            mamap = amap;
            mPois = pois;
        }

        /**
         * 添加Marker到地图中。
         *
         * @since V2.1.0
         */
        public void addToMap() {
            for (int i = 0; i < mPois.size(); i++) {

                com.amap.api.services.core.PoiItem item = mPois.get(i);
                Marker marker = mamap.addMarker(getMarkerOptions(i, item.getTitle()));
                marker.setObject(item);
                mPoiMarks.add(marker);
            }
        }

        /**
         * 去掉PoiOverlay上所有的Marker。
         *
         * @since V2.1.0
         */
        public void removeFromMap() {
            for (Marker mark : mPoiMarks) {
                mark.remove();
            }
        }

        /**
         * 移动镜头到当前的视角。
         *
         * @since V2.1.0
         */
        public void zoomToSpan() {
            if (mPois != null && mPois.size() > 0) {
                if (mamap == null)
                    return;
                LatLngBounds bounds = getLatLngBounds();
                mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }

        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            for (int i = 0; i < mPois.size(); i++) {
                b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
                        mPois.get(i).getLatLonPoint().getLongitude()));
            }
            return b.build();
        }

        //添加一个marker信息（到地图上）
        private MarkerOptions getMarkerOptions(int index, String storeName) {
            return new MarkerOptions().position(new LatLng(mPois.get(index).getLatLonPoint()
                    .getLatitude(), mPois.get(index).getLatLonPoint().getLongitude()))
                    .title(getTitle(index)).snippet(getSnippet(index))
                    .icon(getBitmapDescriptor(index, storeName));
        }

        protected String getTitle(int index) {
            return mPois.get(index).getTitle();
        }

        protected String getSnippet(int index) {
            return mPois.get(index).getSnippet();
        }

        /**
         * 从marker中得到poi在list的位置。
         *
         * @param marker 一个标记的对象。
         * @return 返回该marker对应的poi在list的位置。
         * @since V2.1.0
         */
        public int getPoiIndex(Marker marker) {
            for (int i = 0; i < mPoiMarks.size(); i++) {
                if (mPoiMarks.get(i).equals(marker)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 返回第index的poi的信息。
         *
         * @param index 第几个poi。
         * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
         * @since V2.1.0
         */
        public com.amap.api.services.core.PoiItem getPoiItem(int index) {
            if (index < 0 || index >= mPois.size()) {
                return null;
            }
            return mPois.get(index);
        }


        //添加marker到地图上时候的bitmap
        protected BitmapDescriptor getBitmapDescriptor(int arg0, String storeName) {

            MyMarkerView markerView = new MyMarkerView(RepairWebsiteActivity.this);
            markerView.setInfo(storeName);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(getViewBitmap(markerView, Global.screenWidth / 5, Global.screenWidth / 6));
            return icon;
        }

    }

    //将加载的ma头像转换为bitmap
    public Bitmap getViewBitmap(View comBitmap, int width, int height) {
        Bitmap bitmap = null;
        if (comBitmap != null) {
            comBitmap.clearFocus();
            comBitmap.setPressed(false);

            boolean willNotCache = comBitmap.willNotCacheDrawing();
            comBitmap.setWillNotCacheDrawing(false);

            // Reset the drawing cache background color to fully transparent
            // for the duration of this operation
            int color = comBitmap.getDrawingCacheBackgroundColor();
            comBitmap.setDrawingCacheBackgroundColor(0);
            float alpha = comBitmap.getAlpha();
            comBitmap.setAlpha(1.0f);

            if (color != 0) {
                comBitmap.destroyDrawingCache();
            }

            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            comBitmap.measure(widthSpec, heightSpec);
            comBitmap.layout(0, 0, width, height);

            comBitmap.buildDrawingCache();
            Bitmap cacheBitmap = comBitmap.getDrawingCache();
            if (cacheBitmap == null) {
                Log.e("view.ProcessImageToBlur", "failed getViewBitmap(" + comBitmap + ")",
                        new RuntimeException());
                return null;
            }
            bitmap = Bitmap.createBitmap(cacheBitmap);
            // Restore the view
            comBitmap.setAlpha(alpha);
            comBitmap.destroyDrawingCache();
            comBitmap.setWillNotCacheDrawing(willNotCache);
            comBitmap.setDrawingCacheBackgroundColor(color);
        }
        mImageThumb.setImageBitmap(bitmap);
        return bitmap;
    }


    //计算地图可见范围内，当前定位点和可见范围内任一点的最远距离
    public float getLargeDistance() {
        //位置改变以后的地图西南角的经纬度
        LatLngBounds visibleBounds = aMap.getProjection().getVisibleRegion().latLngBounds;
        //西南角经纬度
        double southwest_lat = visibleBounds.southwest.latitude;
        double southwest_lng = visibleBounds.southwest.longitude;
        LatLng southwest_latLng = new LatLng(southwest_lat, southwest_lng);
        //东北角经纬度
        double northeast_lat = visibleBounds.northeast.latitude;
        double northeast_lng = visibleBounds.northeast.longitude;
        LatLng northeast_latLng = new LatLng(northeast_lat, northeast_lng);

        //自己定位位置的经纬度
        LatLng currLatLng = new LatLng(mCurrLat, mCurrLng);
        float southwest_distance = AMapUtils.calculateLineDistance(southwest_latLng, currLatLng);
        float northeast_distance = AMapUtils.calculateLineDistance(northeast_latLng, currLatLng);
        //
        float distance = southwest_distance > northeast_distance ? southwest_distance : northeast_distance;
        Log.e("地图视野距离：", distance + "$$$$");
        return distance;
    }


    //获取附近的汽车修理店的信息
    public void getRepairStoreList(String loadMsg, final boolean isZoomToSpan) {
        HashMap<String, String> params = new HashMap<>();
        params.put("lat", mCurrLatLonPoint.getLatitude() + "");
        params.put("lng", mCurrLatLonPoint.getLongitude() + "");
        params.put("distance", mBaseDistance + "");
        params.put("typeid", typeid);        //门店类型：1=洗车店，2=维修点，3=附近门店

        new BaseDataPresenter(this).loadData(DataUrl.GET_REPAIR_STORE, params, loadMsg, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {

                ArrayList<Map<String, String>> repairList = (ArrayList<Map<String, String>>) data.response;
                if (repairList == null || repairList.size() <= 0) {
                    return;
                }
                Log.e("门店数量", repairList.size() + "");
                showRepairStore(repairList, isZoomToSpan);

            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, RepairWebsiteActivity.this);
            }

            @Override
            public void onError() {
            }
        });
    }


    public void showRepairStore(ArrayList<Map<String, String>> repairList, boolean isZoomToSpan) {

//        ArrayList<PoiItem> poiItemList = new ArrayList<PoiItem>();
        for (int i = 0; i < repairList.size(); i++) {
            Map<String, String> itemMap = repairList.get(i);
            double lat = Double.parseDouble(itemMap.get("lat"));
            double lng = Double.parseDouble(itemMap.get("lng"));
            LatLonPoint latLonPoint = new LatLonPoint(lat, lng);
            PoiItem poiItem = new PoiItem(itemMap.get("id"), latLonPoint, itemMap.get("name"), itemMap.get("address"));
            poiItem.setTel(itemMap.get("phone"));
            //
            ArrayList<Photo> photos = new ArrayList<Photo>();
            Photo photo = new Photo();
            photo.setUrl(itemMap.get("thumb"));
            photos.add(photo);
            poiItem.setPhotos(photos);
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
        }
        aMap.clear();
        poiOverlay = new MyPoiOverlay(aMap, poiItems);
        poiOverlay.addToMap();
        if (isZoomToSpan) {
            poiOverlay.zoomToSpan();
        }


        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(
                getResources(), R.mipmap.pic_location_arrow_icon))).position(new LatLng(mCurrLng, mCurrLng)).title("我的位置"));


//        new MarkerOptions().position(new LatLng(mPois.get(index).getLatLonPoint()
//                .getLatitude(), mPois.get(index).getLatLonPoint().getLongitude()))
//                .title(getTitle(index)).snippet(getSnippet(index))
//                .icon(getBitmapDescriptor(index, storeName));

    }


}

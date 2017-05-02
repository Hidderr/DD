package com.coco3g.daishu.activity;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.coco3g.daishu.view.MyMapView;
import com.coco3g.daishu.view.TopBarView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import u.aly.au;

public class RepairWebsiteActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener, AMap.OnMarkerClickListener {
    private MyMapView myMapView;
    private TopBarView mTopbar;
    private RelativeLayout mRelativeStore;
    private RelativeLayout.LayoutParams store_lp;
    private AMap aMap;
    private TextView mTxtName, mTxtAddress, mTxtPhone;
    private ImageView mImageThumb;
    //
    private String keyWord = "汽车修理店";

    //

    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private LatLonPoint mCurrLatLonPoint = null;
    private Marker locationMarker; // 选择的点
    private Marker detailMarker;
    private Marker mlastMarker;
    private PoiSearch poiSearch;
    private RepairWebsiteActivity.myPoiOverlay poiOverlay;// poi图层
    private List<PoiItem> poiItems;// poi数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_website);

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
        myMapView = (MyMapView) findViewById(R.id.map_repair_website);
        myMapView.init(savedInstanceState, true);
        aMap = myMapView.aMap;
        aMap.setOnMarkerClickListener(this);
        mRelativeStore = (RelativeLayout) findViewById(R.id.relative_repair_website_repair_store);
        store_lp = new RelativeLayout.LayoutParams(Global.screenWidth, Global.screenHeight / 5);
        store_lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mRelativeStore.setLayoutParams(store_lp);
        //
        myMapView.setOnLocationSuccessedListener(new MyMapView.OnLocationSuccessedListener() {
            @Override
            public void locationSuccessed(LatLng latLng) {
                mCurrLatLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
                doSearchQuery();
            }
        });


    }


    /**
     * 开始进行poi搜索
     */
    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (mCurrLatLonPoint != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(mCurrLatLonPoint, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }


    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
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
                        poiOverlay = new RepairWebsiteActivity.myPoiOverlay(aMap, poiItems);
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();

                        aMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(BitmapFactory.decodeResource(
                                                getResources(), R.mipmap.pic_location_icon)))
                                .position(new LatLng(mCurrLatLonPoint.getLatitude(), mCurrLatLonPoint.getLongitude())));

//                        aMap.addCircle(new CircleOptions()
//                                .center(new LatLng(mCurrLatLonPoint.getLatitude(),
//                                        mCurrLatLonPoint.getLongitude())).radius(5000)
//                                .strokeColor(Color.BLUE)
//                                .fillColor(Color.argb(50, 1, 1, 1))
//                                .strokeWidth(2));

                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
//                        showSuggestCity(suggestionCities);
                    } else {
                        Global.showToast(getResources().getString(R.string.no_result), this);
                    }
                }
            } else {
                Global.showToast(getResources().getString(R.string.no_result), this);
            }
        } else {
            Global.showToast(rcode + "", this);
        }
    }

    @Override
    public void onPoiItemSearched(com.amap.api.services.core.PoiItem poiItem, int i) {

    }


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
                detailMarker.setIcon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                getResources(),
                                R.mipmap.pic_location_red_icon)));

                setPoiItemDisplayContent(mCurrentPoi);
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
        if (index < 10) {
            mlastMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            getResources(), R.mipmap.pic_location_icon)));
        } else {
            mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(), R.mipmap.pic_location_icon)));
        }
        mlastMarker = null;

    }

    private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
        mTxtName.setText(mCurrentPoi.getTitle());
        mTxtAddress.setText(mCurrentPoi.getSnippet());
        mTxtPhone.setText(mCurrentPoi.getTel());
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

    private class myPoiOverlay {
        private AMap mamap;
        private List<com.amap.api.services.core.PoiItem> mPois;
        private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();

        public myPoiOverlay(AMap amap, List<com.amap.api.services.core.PoiItem> pois) {
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
                Marker marker = mamap.addMarker(getMarkerOptions(i));
                com.amap.api.services.core.PoiItem item = mPois.get(i);
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

        private MarkerOptions getMarkerOptions(int index) {
            return new MarkerOptions()
                    .position(
                            new LatLng(mPois.get(index).getLatLonPoint()
                                    .getLatitude(), mPois.get(index)
                                    .getLatLonPoint().getLongitude()))
                    .title(getTitle(index)).snippet(getSnippet(index))
                    .icon(getBitmapDescriptor(index));
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

        protected BitmapDescriptor getBitmapDescriptor(int arg0) {
            if (arg0 < 10) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.mipmap.pic_location_icon));
                return icon;
            } else {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.mipmap.pic_location_icon));
                return icon;
            }
        }
    }


}

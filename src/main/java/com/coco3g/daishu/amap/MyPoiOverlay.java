package com.coco3g.daishu.amap;

import android.content.Context;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.coco3g.daishu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by coco3g on 17/5/18.
 */

public class MyPoiOverlay {
    private Context mContext;
    private AMap mamap;
    private List<PoiItem> mPois;
    private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();

    public MyPoiOverlay(Context context, AMap amap, List<com.amap.api.services.core.PoiItem> pois) {
        mContext = context;
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
            Marker marker = mamap.addMarker(getMarkerOptions(i, item.getTitle(), item.getPoiId()));
            marker.setObject(item);
//            if (item.getPoiId().equals("#")) {  //我的位置
//                marker.setAnchor(0.5f, 0.17f);
//            } else {
//                marker.setAnchor(0.2f, 0.39f);
//            }
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
        if (mPois != null && mPois.size() == 1) {  //一条数据就是自己的位置
            if (mamap == null)
                return;
            PoiItem poiItem = mPois.get(0);
            LatLonPoint latLonPoint = poiItem.getLatLonPoint();
            mamap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()), 15), 1000, null);
        } else if (mPois.size() > 1) {
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
    private MarkerOptions getMarkerOptions(int index, String storeName, String markerId) {
        return new MarkerOptions().position(new LatLng(mPois.get(index).getLatLonPoint()
                .getLatitude(), mPois.get(index).getLatLonPoint().getLongitude()))
                .title(getTitle(index)).snippet(getSnippet(index))
                .icon(getBitmapDescriptor(index, storeName, markerId));
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


//    //添加marker到地图上时候的bitmap
//    protected BitmapDescriptor getBitmapDescriptor(int arg0, String storeName, String markerId) {
//        View view = null;
//        if (markerId.equals("#")) {
//            MyLocationMarkerView myLocationMarkerView = new MyLocationMarkerView(mContext);
//            view = myLocationMarkerView;
//
//        } else {
//            MyMarkerView markerView = new MyMarkerView(mContext);
//            markerView.setInfo(storeName);
//            view = markerView;
//        }
//        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(Global.getViewBitmap(view, Global.screenWidth / 5, Global.screenWidth / 6));
//        return icon;
//    }

    //添加marker到地图上时候的bitmap
    protected BitmapDescriptor getBitmapDescriptor(int arg0, String storeName, String markerId) {
        View view = null;
        if (markerId.equals("#")) {
            return BitmapDescriptorFactory.fromResource(R.mipmap.pic_location_arrow_icon);
        } else {
            return BitmapDescriptorFactory.fromResource(R.mipmap.pic_location_red_icon);
        }


    }


}

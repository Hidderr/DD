package com.coco3g.daishu.amap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.view.MyEndMarkerView;

import java.util.ArrayList;
import java.util.List;

public class RouteOverlay {
    protected List<Marker> stationMarkers = new ArrayList<Marker>();
    protected List<Polyline> allPolyLines = new ArrayList<Polyline>();
    protected Marker startMarker;
    protected Marker endMarker;
    protected LatLng startPoint;
    protected LatLng endPoint;
    protected AMap mAMap;
    private Context mContext;
    private Bitmap startBit, endBit, busBit, walkBit, driveBit;
    protected boolean nodeIconVisible = true;

    public RouteOverlay(Context context) {
        mContext = context;
    }

    /**
     * 去掉BusRouteOverlay上所有的Marker。
     *
     * @since V2.1.0
     */
    public void removeFromMap() {
        if (startMarker != null) {
            startMarker.remove();

        }
        if (endMarker != null) {
            endMarker.remove();
        }
        for (Marker marker : stationMarkers) {
            marker.remove();
        }
        for (Polyline line : allPolyLines) {
            line.remove();
        }
        destroyBit();
    }

    private void destroyBit() {
        if (startBit != null) {
            startBit.recycle();
            startBit = null;
        }
        if (endBit != null) {
            endBit.recycle();
            endBit = null;
        }
        if (busBit != null) {
            busBit.recycle();
            busBit = null;
        }
        if (walkBit != null) {
            walkBit.recycle();
            walkBit = null;
        }
        if (driveBit != null) {
            driveBit.recycle();
            driveBit = null;
        }
    }

    /**
     * 给起点Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     *
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected BitmapDescriptor getStartBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.pic_location_arrow_icon);


//        MyLocationMarkerView myLocationMarkerView = new MyLocationMarkerView(mContext);
//        return BitmapDescriptorFactory.fromBitmap(Global.getViewBitmap(myLocationMarkerView, Global.screenWidth / 5, Global.screenWidth / 6));
    }

    /**
     * 给终点Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     *
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected BitmapDescriptor getEndBitmapDescriptor(String title) {
//        return BitmapDescriptorFactory.fromResource(R.mipmap.pic_location_red_icon);

        MyEndMarkerView myEndMarkerView = new MyEndMarkerView(mContext);
        myEndMarkerView.setInfo(title);

        return BitmapDescriptorFactory.fromBitmap(Global.getViewBitmap(myEndMarkerView, Global.screenWidth / 5, Global.screenWidth / 6));
    }

    /**
     * 给公交Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     *
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected BitmapDescriptor getBusBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.pic_amap_end_icon);
    }

    /**
     * 给步行Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     *
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected BitmapDescriptor getWalkBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.pic_amap_end_icon);
    }

    protected BitmapDescriptor getDriveBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.pic_amap_end_icon);
    }

    protected void addStartAndEndMarker(String endTitle) {
        startMarker = mAMap.addMarker((new MarkerOptions())
                .position(startPoint).icon(getStartBitmapDescriptor())
                .title("\u8D77\u70B9").anchor(0.5f, 0.3f));
        // startMarker.showInfoWindow();

        endMarker = mAMap.addMarker((new MarkerOptions()).position(endPoint)
                .icon(getEndBitmapDescriptor(endTitle)).title(endTitle).anchor(0.3f, 1f));
//                .icon(getEndBitmapDescriptor(endTitle)).title("\u7EC8\u70B9"));
        // mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint,
        // getShowRouteZoom()));
    }

    /**
     * 移动镜头到当前的视角。
     *
     * @since V2.1.0
     */
    public void zoomToSpan() {
        if (startPoint != null) {
            if (mAMap == null)
                return;
            try {
                LatLngBounds bounds = getLatLngBounds();
                mAMap.animateCamera(CameraUpdateFactory
                        .newLatLngBounds(bounds, 50));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    protected LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        b.include(new LatLng(startPoint.latitude, startPoint.longitude));
        b.include(new LatLng(endPoint.latitude, endPoint.longitude));
        return b.build();
    }

    /**
     * 路段节点图标控制显示接口。
     *
     * @param visible true为显示节点图标，false为不显示。
     * @since V2.3.1
     */
    public void setNodeIconVisibility(boolean visible) {
        try {
            nodeIconVisible = visible;
            if (this.stationMarkers != null && this.stationMarkers.size() > 0) {
                for (int i = 0; i < this.stationMarkers.size(); i++) {
                    this.stationMarkers.get(i).setVisible(visible);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected void addStationMarker(MarkerOptions options) {
        if (options == null) {
            return;
        }
        Marker marker = mAMap.addMarker(options);
        if (marker != null) {
            stationMarkers.add(marker);
        }

    }

    protected void addPolyLine(PolylineOptions options) {
        if (options == null) {
            return;
        }
        Polyline polyline = mAMap.addPolyline(options);
        if (polyline != null) {
            allPolyLines.add(polyline);
        }
    }

    protected float getRouteWidth() {
        return 18f;
    }

    protected int getWalkColor() {
        return Color.parseColor("#6db74d");
    }

    /**
     * 自定义路线颜色。
     * return 自定义路线颜色。
     *
     * @since V2.2.1
     */
    protected int getBusColor() {
        return Color.parseColor("#537edc");
    }

    protected int getDriveColor() {
        return Color.parseColor("#537edc");
    }

    // protected int getShowRouteZoom() {
    // return 15;
    // }
}

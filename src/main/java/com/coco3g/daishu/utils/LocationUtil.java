package com.coco3g.daishu.utils;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.coco3g.daishu.data.Global;

public class LocationUtil implements AMapLocationListener {
    Context mContext = null;
    AMapLocation am = null;
    AMapLocationClient mLocationClient = null;
    LocationComplete locationcomplete; // 定位完毕的监听
    AMapLocationChanged aMapLocationChanged; //定位完毕的监听
    AMapLocationClientOption mLocationOption = null;
    boolean onceLocation = true; // 是否只定位一次
    long locationDur = 2000; // 定位间隔
    boolean commitLatLng = false; // 是否提交定位经纬度到服务器
    String manyou = "0"; // 是否漫游；0：不漫游；1：漫游

    public LocationUtil(Context context) {
        mContext = context;
    }

    /**
     * 初始化定位并开始定位
     *
     * @param oncelocation 是否只定位一次
     * @param locationdur  定位间隔
     * @param commit       是否提交定位结果到服务器
     * @param manyou       是否漫游
     * @return
     */
    public LocationUtil initLocationAndStart(boolean oncelocation, long locationdur, boolean commit, String manyou) {
        this.onceLocation = oncelocation;
        this.locationDur = locationdur;
        this.commitLatLng = commit;
        this.manyou = manyou;
        //
        mLocationClient = new AMapLocationClient(mContext);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        // 初始化定位参数
        mLocationOption = new AMapLocationClientOption();

        /*判断是否有网络，有就使用高精度定位模式，没有就使用GPS定位模式*/
        if (Global.isNetworkConnected(mContext)) {  //网络可用时候
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        } else {
            //设置定位模式为AMapLocationMode.Device_Sensors，仅设备模式（GPS）。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
        }

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(onceLocation);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(locationdur);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
        return this;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        // TODO Auto-generated method stub
        int code = amapLocation.getErrorCode();
        Log.e("定位结果", amapLocation.getErrorInfo() + "**" + amapLocation.getErrorCode());
        if (amapLocation != null && code == 0) {
            //定位成功回调信息，设置相关消息
//            amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//            amapLocation.getAccuracy();//获取精度信息
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date date = new Date(amapLocation.getTime());
//            df.format(date);//定位时间
//            amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//            amapLocation.getCountry();//国家信息
//            amapLocation.getProvince();//省信息
//            amapLocation.getCity();//城市信息
//            amapLocation.getStreetNum();//街道门牌号信息
//            amapLocation.getCityCode();//城市编码
//            amapLocation.getAdCode();//地区编码
//            amapLocation.getAoiName();//获取当前定位点的AOI信息
            String lat = amapLocation.getLatitude() + "";//获取纬度
            String lng = amapLocation.getLongitude() + "";//获取经度
            String address = amapLocation.getProvince() + amapLocation.getCity() + amapLocation.getDistrict() + amapLocation.getStreet() + amapLocation.getStreetNum();
//            Log.e("location", address + "--" + lat + "--" + lng);
            locationComplete(address, lat, lng);
            aMapLocationChanged(amapLocation);
            if (commitLatLng) {
//                commitLatLng(lat, lng, manyou); // 上传定位结果
            }
            if (onceLocation) { // 如果只定位一次，则定位完毕后关闭定位服务
                stopLocation(); // 停止定位
                destroyLocation(); // 销毁定位
            }
        } else if (code != 0) {
//            Log.e("Error", code + "-" + amapLocation.getErrorCode());
            locationComplete("", "", "");
            aMapLocationChanged(null);
        }
    }

    private void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();//停止定位
        }
    }

    private void destroyLocation() {
        if (mLocationClient != null) {
            mLocationClient.onDestroy(); // 销毁定位
        }
    }

    public interface LocationComplete {
        void locationcomplete(String address, String lat, String lng);
    }

    public void setLocationComplete(LocationComplete locationcomplete) {
        this.locationcomplete = locationcomplete;
    }

    private void locationComplete(String address, String lat, String lng) {
        if (locationcomplete != null) {
            locationcomplete.locationcomplete(address, lat, lng);
        }
    }

    //新添加的接口回调
    public interface AMapLocationChanged {
        void aMapLocation(AMapLocation aMapLocation);
    }

    public void setAMapLocationChanged(AMapLocationChanged aMapLocationChanged) {
        this.aMapLocationChanged = aMapLocationChanged;
    }

    public void aMapLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocationChanged != null) {
            aMapLocationChanged.aMapLocation(aMapLocation);
        }
    }

//    private void commitLatLng(String lat, String lng, String manyou) {
//        new CommitLatLngPresenter(mContext).commitLatLng(lat, lng, manyou, new ICommitLatLngListener() {
//            @Override
//            public void onSuccess(BaseDataBean data) {
//
//            }
//
//            @Override
//            public void onFailure(BaseDataBean data) {
//
//            }
//
//            @Override
//            public void onError() {
//
//            }
//
//        });
//    }
}

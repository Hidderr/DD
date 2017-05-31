package com.coco3g.daishu.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.StoreShaiXuanAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.bean.RepairStoreBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.coco3g.daishu.utils.LocationUtil;
import com.coco3g.daishu.utils.RequestPermissionUtils;
import com.coco3g.daishu.view.ChoosePopupwindow;
import com.coco3g.daishu.view.MyMapView;
import com.coco3g.daishu.view.SuperRefreshLayout;
import com.coco3g.daishu.view.TopBarView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShaiXuanListActivity extends BaseActivity implements View.OnClickListener {
    private TopBarView mTopbar;
    private String title = "", typeid = "";
    private ListView mListView;
    private StoreShaiXuanAdapter mAdapter;
    private SuperRefreshLayout mSuperRefresh;
    private RelativeLayout mRelativeAddress, mRelativeStoreType, mRelativeMoRenOrder, mRelativeShaiXuan, mRelativeCurrLocation;
    private TextView mTxtAddress, mTxtStoreType, mTxtMoRenOrder, mTxtShaiXuan, mTxtCurrLocation;
    private ImageView mImageLocation;

    private float mBaseDistance = 10000;  //距离定位点最小的半径

    private double currLat, currLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shai_xuan_list);
        typeid = getIntent().getStringExtra("typeid");
        title = getIntent().getStringExtra("title");

        init();

    }

    private void init() {
        mTopbar = (TopBarView) findViewById(R.id.tobbar_shai_xuan_list);
        mTopbar.setTitle(title);
        ImageView rightView = new ImageView(this);
        int margin_5 = Global.dipTopx(this, 5f);
        int margin_15 = Global.dipTopx(this, 15f);
        rightView.setPadding(margin_5, margin_5, margin_15, margin_5);
        rightView.setImageResource(R.mipmap.pic_store_map_location_icon);
        mTopbar.setRightView(rightView);
        mTopbar.setOnClickRightListener(new TopBarView.OnClickRightView() {
            @Override
            public void onClickTopbarView() {
                Intent intent = new Intent(ShaiXuanListActivity.this, RepairWebsiteActivity.class);
                if (!TextUtils.isEmpty(typeid)) {
                    intent.putExtra("typeid", typeid);
                }
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });
        //
        mListView = (ListView) findViewById(R.id.listview_shai_xuan_list);
        mSuperRefresh = (SuperRefreshLayout) findViewById(R.id.sr_shai_xuan_list);
        mRelativeAddress = (RelativeLayout) findViewById(R.id.relative_shai_xuan_list_address);
        mRelativeStoreType = (RelativeLayout) findViewById(R.id.relative_shai_xuan_list_store_type);
        mRelativeMoRenOrder = (RelativeLayout) findViewById(R.id.relative_shai_xuan_list_moren_order);
        mRelativeShaiXuan = (RelativeLayout) findViewById(R.id.relative_shai_xuan_list_shaixuan);
        mRelativeCurrLocation = (RelativeLayout) findViewById(R.id.relative_shai_xuan_list_curr_address);
        mTxtAddress = (TextView) findViewById(R.id.tv_shai_xuan_list_address);
        mTxtStoreType = (TextView) findViewById(R.id.tv_shai_xuan_list_store_type);
        mTxtMoRenOrder = (TextView) findViewById(R.id.tv_shai_xuan_list_moren_order);
        mTxtShaiXuan = (TextView) findViewById(R.id.tv_shai_xuan_list_shaixuan);
        mTxtCurrLocation = (TextView) findViewById(R.id.tv_shai_xuan_list_curr_address);
        mImageLocation = (ImageView) findViewById(R.id.image_shai_xuan_list_curr_location);
        //
        mRelativeAddress.setOnClickListener(this);
        mRelativeStoreType.setOnClickListener(this);
        mRelativeMoRenOrder.setOnClickListener(this);
        mRelativeShaiXuan.setOnClickListener(this);
        mImageLocation.setOnClickListener(this);
        //
        mAdapter = new StoreShaiXuanAdapter(ShaiXuanListActivity.this);
        mListView.setAdapter(mAdapter);
        //
        mSuperRefresh.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                if (currLat == 0 || currLng == 0) {
                    startLocation(true);
                } else {
                    mAdapter.clearList();
                    getStoreList();
                }
            }

            @Override
            public void onLoadMore() {

            }
        });

        //
        mSuperRefresh.setRefreshingLoad();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relative_shai_xuan_list_address:  //地址

                break;

            case R.id.relative_shai_xuan_list_store_type:   //门店种类

                break;

            case R.id.relative_shai_xuan_list_moren_order:  //默认排序

                break;

            case R.id.relative_shai_xuan_list_shaixuan:   //筛选

                break;

            case R.id.image_shai_xuan_list_curr_location:   //当前位置定位
                Intent intent = new Intent(ShaiXuanListActivity.this, RepairWebsiteActivity.class);
                if (!TextUtils.isEmpty(typeid)) {
                    intent.putExtra("typeid", typeid);
                }
                intent.putExtra("title", title);
                startActivity(intent);

                break;
        }


    }


    //定位
    public void startLocation(boolean isGetList) {
        new RequestPermissionUtils(this).aleraPermission(Manifest.permission.ACCESS_FINE_LOCATION, 1);
        //定位
        new LocationUtil(this).initLocationAndStart(true, 1000, false, null).setAMapLocationChanged(new LocationUtil.AMapLocationChanged() {
            @Override
            public void aMapLocation(AMapLocation aMapLocation) {
                Global.locationCity = aMapLocation.getCity();
                Global.mCurrLat = currLat = aMapLocation.getLatitude();
                Global.mCurrLng = currLng = aMapLocation.getLongitude();
                //
                mTopbar.setLocationCity(Global.locationCity);
                Log.e("定位结果", "city " + Global.locationCity + "  mCurrLat   " + Global.mCurrLat + "  mCurrLng" + Global.mCurrLng);
                //
                mRelativeCurrLocation.setVisibility(View.VISIBLE);
                mTxtCurrLocation.setText(aMapLocation.getCity() + aMapLocation.getAoiName() + aMapLocation.getStreet() + aMapLocation.getStreetNum());

                //
                getStoreList();

            }
        });
    }


    public void showPopupWidnow(View view) {
//        final ChoosePopupwindow popupwindow = new ChoosePopupwindow(this, Global.screenWidth / 4 - 70, 0, gradeList, currChooseIndex);
//        popupwindow.showAsDropDown(view, -5, 35);
//        popupwindow.setOnTextSeclectedListener(new ChoosePopupwindow.OnTextSeclectedListener() {
//            @Override
//            public void textSelected(int position) {
//                currChooseIndex = position;
//                if (gradeList != null) {
//                    String typeid = gradeList.get(position).get("id");
//                    myMapView.refreshData(typeid);
//                }
//
////                myMapView.clearMarker();
//
//            }
//        });
//        popupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//
//            }
//        });

    }


    //获取维修等级列表
    public void getSto() {
        HashMap<String, String> params = new HashMap<>();
        new BaseDataPresenter(this).loadData(DataUrl.GET_REPAIR_GRAGE_LIST, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
//                gradeList = (ArrayList<Map<String, String>>) data.response;
            }

            @Override
            public void onFailure(BaseDataBean data) {
            }

            @Override
            public void onError() {
            }
        });
    }

    //获取附近的汽车修理店的信息
    public void getStoreList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("lat", Global.mCurrLat + "");
        params.put("lng", Global.mCurrLng + "");
        params.put("distance", mBaseDistance + "");
        if (!TextUtils.isEmpty(typeid)) {
            params.put("joinid", typeid);  //-1=洗车店，1=维修养护和维修救援，附近门店(不传参)，汽修厂、爱车保姆快修店（根据获取的维修类型id）
        }
        Log.e("地图传参", "distance " + mBaseDistance + "   joinid " + typeid);
        new BaseDataPresenter(ShaiXuanListActivity.this).loadData(DataUrl.GET_REPAIR_STORE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {

                ArrayList<Map<String, String>> repairList = (ArrayList<Map<String, String>>) data.response;
                if (repairList == null || repairList.size() <= 0) {
                    mSuperRefresh.onLoadComplete();
                    return;
                }
                Log.e("门店数量", repairList.size() + "");
                mAdapter.setList(repairList);
                //
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, ShaiXuanListActivity.this);
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefresh.onLoadComplete();
            }
        });
    }


}

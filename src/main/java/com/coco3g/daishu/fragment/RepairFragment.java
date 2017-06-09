package com.coco3g.daishu.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.RepairWebsiteActivity;
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.data.TypevauleGotoDictionary;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.LocationUtil;
import com.coco3g.daishu.utils.RequestPermissionUtils;
import com.coco3g.daishu.view.BannerView;
import com.coco3g.daishu.view.HomeMenuImageView;
import com.coco3g.daishu.view.LoginRegisterView;
import com.coco3g.daishu.view.SuperRefreshLayout;
import com.sunfusheng.marqueeview.MarqueeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RepairFragment extends Fragment implements View.OnClickListener {
    SuperRefreshLayout mSuperRefreshLayout;
    private View mRepairView;
    BannerView mBanner;
    LinearLayout mLinearMenu, mLinearRoot;
    MarqueeView mTxtRepairBoradcast;
    //
    HomeMenuImageView[] mMenuRes;
    HomeMenuImageView mRepairMenu1, mRepairMenu2, mRepairMenu3, mRepairMenu4, mRepairMenu5;
    int[] mNavIconResID = new int[]{R.mipmap.pic_help_phone, R.mipmap.pic_car_account, R.mipmap.pic_server_confirm, R.mipmap.pic_history_record,
            R.mipmap.pic_car_repair};
    String[] mTitles = new String[]{"救援电话", "我的账单", "服务确认", "历史记录", "袋鼠好车维修点"};
    //

    private ArrayList<Map<String, String>> mBroadCastList;  //跑马灯

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRepairView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_repair, null);
        init();
        return mRepairView;
    }

    private void init() {
        mSuperRefreshLayout = (SuperRefreshLayout) mRepairView.findViewById(R.id.superrefresh_repair);
        mBanner = (BannerView) mRepairView.findViewById(R.id.banner_repair_frag);
        mLinearRoot = (LinearLayout) mRepairView.findViewById(R.id.linear_repair_root);
        mLinearRoot.setVisibility(View.GONE);
        mLinearMenu = (LinearLayout) mRepairView.findViewById(R.id.linear_repair_menu);
        mTxtRepairBoradcast = (MarqueeView) mRepairView.findViewById(R.id.tv_repair_boardcast);
        mRepairMenu1 = (HomeMenuImageView) mRepairView.findViewById(R.id.view_repair_menu_1);
        mRepairMenu2 = (HomeMenuImageView) mRepairView.findViewById(R.id.view_repair_menu_2);
        mRepairMenu3 = (HomeMenuImageView) mRepairView.findViewById(R.id.view_repair_menu_3);
        mRepairMenu4 = (HomeMenuImageView) mRepairView.findViewById(R.id.view_repair_menu_4);
        mRepairMenu5 = (HomeMenuImageView) mRepairView.findViewById(R.id.view_repair_menu_5);
        //
        mBanner.setScreenRatio(2);
        mMenuRes = new HomeMenuImageView[]{mRepairMenu1, mRepairMenu2, mRepairMenu3, mRepairMenu4, mRepairMenu5};
        for (int i = 0; i < mNavIconResID.length; i++) {
            mMenuRes[i].setIcon(mNavIconResID[i], mTitles[i]);
        }
        //
        mSuperRefreshLayout.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                mBanner.clearList();
                getBanner();
            }

            @Override
            public void onLoadMore() {
                mSuperRefreshLayout.onLoadComplete();
            }
        });
        //
        mRepairMenu1.setOnClickListener(this);
        mRepairMenu2.setOnClickListener(this);
        mRepairMenu3.setOnClickListener(this);
        mRepairMenu4.setOnClickListener(this);
        mRepairMenu5.setOnClickListener(this);
        //跑马灯
        mTxtRepairBoradcast.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TextView textView) {
                String url = mBroadCastList.get(position).get("linkurl");
                if (!TextUtils.isEmpty(url)) {
                    Intent intent = new Intent(getActivity(), WebActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            }
        });
        //
        startLocation(false);
        //
        mSuperRefreshLayout.setRefreshingLoad();


    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.view_repair_menu_1:  //救援电话
                if (!Global.checkoutLogin(getActivity())) {
                    return;
                }
                takePhoneUploadLatLng();

                break;

            case R.id.view_repair_menu_2:  //我的账单
                intentToWeb(Global.H5Map.get("mybill"));

                break;

            case R.id.view_repair_menu_3:  //服务确认
                intentToWeb(Global.H5Map.get("serice_confirm"));

                break;

            case R.id.view_repair_menu_4:  //历史记录
                intentToWeb(Global.H5Map.get("history"));

                break;

            case R.id.view_repair_menu_5:  //袋鼠大师维修点
                if (!Global.checkoutLogin(getActivity())) {
                    return;
                }
                intent = new Intent(getActivity(), RepairWebsiteActivity.class);
                intent.putExtra("typeid", "1");  //门店类型：2=洗车店，1=维修养护和维修救援，附近门店(不传参)，汽修厂、爱车保姆快修店（根据获取的维修类型id）
                intent.putExtra("title", "袋鼠好车维修点");
                startActivity(intent);

                break;

        }
    }

    public void intentToWeb(String url) {
        if (!Global.checkoutLogin(getActivity())) {
            return;
        }
        if (url.equals("#")) {
            return;
        }
        if (url.startsWith("http://coco3g-app/open_tabview")) {
            TypevauleGotoDictionary typevauleGotoDictionary = new TypevauleGotoDictionary(getActivity());
            typevauleGotoDictionary.gotoViewChoose(url);
            return;
        }
        Intent intent = new Intent(getActivity(), WebActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    //定位
    public void startLocation(final boolean takePhone) {
        new RequestPermissionUtils(getActivity()).aleraPermission(Manifest.permission.ACCESS_FINE_LOCATION, 1);
        //定位
        new LocationUtil(getActivity()).initLocationAndStart(true, 1000, false, null).setAMapLocationChanged(new LocationUtil.AMapLocationChanged() {
            @Override
            public void aMapLocation(AMapLocation aMapLocation) {
                Global.locationCity = aMapLocation.getCity();
                Global.mCurrLat = aMapLocation.getLatitude();
                Global.mCurrLng = aMapLocation.getLongitude();
                //
                Log.e("定位结果", "city " + Global.locationCity + "  mCurrLat   " + Global.mCurrLat + "  mCurrLng" + Global.mCurrLng);
                if (takePhone) {
                    takePhoneUploadLatLng();
                }
            }
        });
    }


    //获取banner图片
    private void getBanner() {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "3");    //1:首页轮播图， 2:商品汇， 3:维修救援，
        new BaseDataPresenter(getActivity()).loadData(DataUrl.GET_BANNER_IMAGE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {

                ArrayList<Map<String, String>> bannerList = (ArrayList<Map<String, String>>) data.response;
                mBanner.loadData(bannerList);
                //
                getBroadCastData();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, getActivity());
                mSuperRefreshLayout.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefreshLayout.onLoadComplete();
            }
        });
    }


    //获取跑马灯
    private void getBroadCastData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "7");    //1:首页轮播图， 2:商品汇， 3:维修救援， 4:汽车商城， 5:首页广播， 6:商城头条， 7:维修通告，
        new BaseDataPresenter(getActivity()).loadData(DataUrl.GET_BANNER_IMAGE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                mBroadCastList = (ArrayList<Map<String, String>>) data.response;
                if (mBroadCastList != null && mBroadCastList.size() > 0) {
                    ArrayList<String> list = new ArrayList<String>();
                    for (int i = 0; i < mBroadCastList.size(); i++) {
                        list.add(mBroadCastList.get(i).get("title"));
                    }
                    mTxtRepairBoradcast.startWithList(list);
                }
                mLinearRoot.setVisibility(View.VISIBLE);
                mSuperRefreshLayout.onLoadComplete();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, getActivity());
                mSuperRefreshLayout.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefreshLayout.onLoadComplete();
            }
        });
    }


    //拨打电话上传经纬度
    private void takePhoneUploadLatLng() {
        if (Global.mCurrLat == 0 || Global.mCurrLng == 0) {
            startLocation(true);
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("lat", Global.mCurrLat + "");
        params.put("lng", Global.mCurrLng + "");
        new BaseDataPresenter(getActivity()).loadData(DataUrl.TAKE_PHONE_UPLOAD_LATLNG, params, getResources().getString(R.string.loading), new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Map<String, String> phoneMap = (Map<String, String>) data.response;
                takePhoneDialog(phoneMap.get("phone"));
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, getActivity());
                mSuperRefreshLayout.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefreshLayout.onLoadComplete();
            }
        });
    }

    private void takePhoneDialog(final String phone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("拨打救援电话");
        builder.setMessage("确定拨打救援电话？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Global.callPhone(getActivity(), phone);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();


    }


}

package com.coco3g.daishu.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.adapter.HomeAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.data.TypevauleGotoDictionary;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.view.BannerView;
import com.coco3g.daishu.view.BottomNavImageView;
import com.coco3g.daishu.view.HomeMenuImageView;
import com.coco3g.daishu.view.SuperRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private Context mContext;
    SuperRefreshLayout mSuperRefreshLayout;
    ListView mListView;
    private View mHomeView, mHeadView;
    BannerView mBanner;
    TextView mTxtBoardcast;
    HomeMenuImageView[] mMenuRes;
    HomeMenuImageView mMenu1, mMenu2, mMenu3, mMenu4, mMenu5, mMenu6, mMenu7, mMenu8;
    ImageView mImageMiddleBanner;
    HomeAdapter mAdapter;
    //
    int[] mNavIconResID = new int[]{R.mipmap.pic_menu_mime_car, R.mipmap.pic_menu_repair_car, R.mipmap.pic_menu_wash_car, R.mipmap.pic_menu_nearby_carshop,
            R.mipmap.pic_menu_buy_car, R.mipmap.pic_menu_car_goodsing, R.mipmap.pic_menu_gasoline, R.mipmap.pic_menu_car_insurance};
    String[] mNavTitles = new String[]{"我的汽车", "维护养修", "洗车", "附近门店", "我要买车", "车载用品", "打折油卡", "机动车险"};

    //
    OnRepairClickListener onRepairClickListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mAdapter = new HomeAdapter(getActivity());
        mHomeView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
        init();
        mSuperRefreshLayout.setRefreshingLoad();
        return mHomeView;
    }

    private void init() {
        mSuperRefreshLayout = (SuperRefreshLayout) mHomeView.findViewById(R.id.superrefresh_home);
        mListView = (ListView) mHomeView.findViewById(R.id.listview_home);
        /* 头部数据 */
        mHeadView = LayoutInflater.from(getActivity()).inflate(R.layout.view_home_head, null);
        mBanner = (BannerView) mHeadView.findViewById(R.id.banner_home_frag);
        mTxtBoardcast = (TextView) mHeadView.findViewById(R.id.tv_home_boardcast);
        mMenu1 = (HomeMenuImageView) mHeadView.findViewById(R.id.view_home_menu_1);
        mMenu2 = (HomeMenuImageView) mHeadView.findViewById(R.id.view_home_menu_2);
        mMenu3 = (HomeMenuImageView) mHeadView.findViewById(R.id.view_home_menu_3);
        mMenu4 = (HomeMenuImageView) mHeadView.findViewById(R.id.view_home_menu_4);
        mMenu5 = (HomeMenuImageView) mHeadView.findViewById(R.id.view_home_menu_5);
        mMenu6 = (HomeMenuImageView) mHeadView.findViewById(R.id.view_home_menu_6);
        mMenu7 = (HomeMenuImageView) mHeadView.findViewById(R.id.view_home_menu_7);
        mMenu8 = (HomeMenuImageView) mHeadView.findViewById(R.id.view_home_menu_8);
        mImageMiddleBanner = (ImageView) mHeadView.findViewById(R.id.image_home_middle_banner);
        mBanner.setScreenRatio(2);
        mMenuRes = new HomeMenuImageView[]{mMenu1, mMenu2, mMenu3, mMenu4, mMenu5, mMenu6, mMenu7, mMenu8};
        for (int i = 0; i < mNavIconResID.length; i++) {
            mMenuRes[i].setIcon(mNavIconResID[i], mNavTitles[i]);
        }
        mListView.addHeaderView(mHeadView);
        //
        mSuperRefreshLayout.setCanLoadMore();
        mSuperRefreshLayout.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                getBanner();
            }

            @Override
            public void onLoadMore() {
                mSuperRefreshLayout.onLoadComplete();
            }
        });


        //
        mMenu1.setOnClickListener(this);
        mMenu2.setOnClickListener(this);
        mMenu3.setOnClickListener(this);
        mMenu4.setOnClickListener(this);
        mMenu5.setOnClickListener(this);
        mMenu6.setOnClickListener(this);
        mMenu7.setOnClickListener(this);
        mMenu8.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.view_home_menu_1:  //我的汽车
                intentToWeb(Global.H5Map.get("mycar"));

                break;

            case R.id.view_home_menu_2:  //维护养修
                onRepairClick();

                break;

            case R.id.view_home_menu_3:  //洗车
//                intentToWeb(Global.H5Map.get("mycar"));

                break;

            case R.id.view_home_menu_4:  //附近门店
//                intentToWeb(Global.H5Map.get("mycar"));

                break;

            case R.id.view_home_menu_5:  //我要买车
//                intentToWeb(Global.H5Map.get("mycar"));

                break;

            case R.id.view_home_menu_6:  //车载用品
//                intentToWeb(Global.H5Map.get("mycar"));

                break;

            case R.id.view_home_menu_7:  //打折油卡
                intentToWeb(Global.H5Map.get("youka"));

                break;

            case R.id.view_home_menu_8:  //  机动车险
                intentToWeb(Global.H5Map.get("baoxian"));

                break;

        }
    }


    public void intentToWeb(String url) {

        if (url.equals("#")) {
            return;
        }

        if (url.startsWith("http://coco3g-app/open_tabview")) {
            TypevauleGotoDictionary typevauleGotoDictionary = new TypevauleGotoDictionary(mContext);
            typevauleGotoDictionary.gotoViewChoose(url);
            return;
        }

        Intent intent = new Intent(mContext, WebActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }


    //获取banner图片
    public void getBanner() {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "1");    //1:首页轮播图， 2:商品汇， 3:维修救援，
        new BaseDataPresenter(mContext).loadData(DataUrl.GET_BANNER_IMAGE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, String>> bannerList = (ArrayList<Map<String, String>>) data.response;
                mBanner.loadData(bannerList);
                //
                mListView.setAdapter(mAdapter);
                getBroadCastReceiver();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, mContext);
                mSuperRefreshLayout.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefreshLayout.onLoadComplete();
            }
        });
    }

    //获取banner图片
    public void getBroadCastReceiver() {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "5");    //1:首页轮播图， 2:商品汇， 3:维修救援， 4:汽车商城， 5:首页广播， 6:商城头条， 7:维修通告，
        new BaseDataPresenter(mContext).loadData(DataUrl.GET_BANNER_IMAGE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, String>> broadCastList = (ArrayList<Map<String, String>>) data.response;
//                mTxtBoardcast.setText();
                getH5URL();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, mContext);
                mSuperRefreshLayout.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefreshLayout.onLoadComplete();
            }
        });
    }

    //获取banner图片
    public void getH5URL() {
        HashMap<String, String> params = new HashMap<>();
        new BaseDataPresenter(mContext).loadData(DataUrl.GET_H5, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Global.H5Map = (Map<String, String>) data.response;


                mSuperRefreshLayout.onLoadComplete();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, mContext);
                mSuperRefreshLayout.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefreshLayout.onLoadComplete();
            }
        });
    }


    public interface OnRepairClickListener {
        void repairClick();
    }

    public void setOnRepairClickListener(OnRepairClickListener onRepairClickListener) {
        this.onRepairClickListener = onRepairClickListener;
    }

    public void onRepairClick() {
        if (onRepairClickListener != null) {
            onRepairClickListener.repairClick();
        }
    }

}

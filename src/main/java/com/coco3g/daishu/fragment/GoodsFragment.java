package com.coco3g.daishu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.CarShopActivity;
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.data.TypevauleGotoDictionary;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.view.BannerView;
import com.coco3g.daishu.view.HomeMenuImageView;
import com.coco3g.daishu.view.SuperRefreshLayout;
import com.sunfusheng.marqueeview.MarqueeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GoodsFragment extends Fragment implements View.OnClickListener {
    SuperRefreshLayout mSuperRefreshLayout;
    private View mReadView;
    BannerView mBanner;
    LinearLayout mLinearMenu, mLinearRoot;
    MarqueeView mTxtReadBoradcast;
    //
    LinearLayout.LayoutParams lpLinear;
    HomeMenuImageView[] mMenuRes;
    HomeMenuImageView mReadMenu1, mReadMenu2, mReadMenu3, mReadMenu4, mReadMenu5;
    int[] mNavIconResID = new int[]{R.mipmap.pic_car_shop, R.mipmap.pic_car_insurance, R.mipmap.pic_car_tyre, R.mipmap.pic_car_gasoline,
            R.mipmap.pic_car_other};
    String[] mNavTitles = new String[]{"汽车商城", "汽车保险", "车载用品", "油品区", "其他产品"};

    private ArrayList<Map<String, String>> mBroadCastList;  //跑马灯

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mReadView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_goods, null);
        lpLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpLinear.topMargin = Global.dipTopx(getActivity(), 10);
        init();
        mSuperRefreshLayout.setRefreshingLoad();
        return mReadView;
    }

    private void init() {
        mSuperRefreshLayout = (SuperRefreshLayout) mReadView.findViewById(R.id.superrefresh_read);
        mBanner = (BannerView) mReadView.findViewById(R.id.banner_read_frag);
        mLinearRoot = (LinearLayout) mReadView.findViewById(R.id.linear_goods_frag);
        mLinearRoot.setVisibility(View.GONE);
        mLinearMenu = (LinearLayout) mReadView.findViewById(R.id.linear_read_menu);
        mLinearMenu.setLayoutParams(lpLinear);
        mTxtReadBoradcast = (MarqueeView) mReadView.findViewById(R.id.tv_read_boardcast);
        mReadMenu1 = (HomeMenuImageView) mReadView.findViewById(R.id.view_read_menu_1);
        mReadMenu2 = (HomeMenuImageView) mReadView.findViewById(R.id.view_read_menu_2);
        mReadMenu3 = (HomeMenuImageView) mReadView.findViewById(R.id.view_read_menu_3);
        mReadMenu4 = (HomeMenuImageView) mReadView.findViewById(R.id.view_read_menu_4);
        mReadMenu5 = (HomeMenuImageView) mReadView.findViewById(R.id.view_read_menu_5);
        //
        mBanner.setScreenRatio(2);
        mMenuRes = new HomeMenuImageView[]{mReadMenu1, mReadMenu2, mReadMenu3, mReadMenu4, mReadMenu5};
        for (int i = 0; i < mNavIconResID.length; i++) {
            mMenuRes[i].setIcon(mNavIconResID[i], mNavTitles[i]);
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
        mReadMenu1.setOnClickListener(this);
        mReadMenu2.setOnClickListener(this);
        mReadMenu3.setOnClickListener(this);
        mReadMenu4.setOnClickListener(this);
        mReadMenu5.setOnClickListener(this);
        //跑马灯
        mTxtReadBoradcast.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
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


    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.view_read_menu_1:  //汽车商城
                intent = new Intent(getActivity(), CarShopActivity.class);
                startActivity(intent);

                break;

            case R.id.view_read_menu_2:  //汽车保险
                intentToWeb(Global.H5Map.get("baoxian"));


                break;

            case R.id.view_read_menu_3:  //车载用品
//                intentToWeb(Global.H5Map.get("baoxian"));

                break;

            case R.id.view_read_menu_4:  //油品区
                intentToWeb(Global.H5Map.get("youpinqu"));

                break;

            case R.id.view_read_menu_5:  // 其他产品
//                intentToWeb(Global.H5Map.get("youka"));

                break;
        }
    }


    public void intentToWeb(String url) {

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


    //获取banner图片
    public void getBanner() {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "2");    //1:首页轮播图， 2:商品汇， 3:维修救援，
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
    public void getBroadCastData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "6");    //1:首页轮播图， 2:商品汇， 3:维修救援， 4:汽车商城， 5:首页广播， 6:商城头条， 7:维修通告，
        new BaseDataPresenter(getActivity()).loadData(DataUrl.GET_BANNER_IMAGE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                mBroadCastList = (ArrayList<Map<String, String>>) data.response;
                if (mBroadCastList != null && mBroadCastList.size() > 0) {
                    ArrayList<String> list = new ArrayList<String>();
                    for (int i = 0; i < mBroadCastList.size(); i++) {
                        list.add(mBroadCastList.get(i).get("title"));
                    }
                    mTxtReadBoradcast.startWithList(list);
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


}

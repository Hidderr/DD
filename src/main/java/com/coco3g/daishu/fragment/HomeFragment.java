package com.coco3g.daishu.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.CarCategoryListActivity;
import com.coco3g.daishu.activity.CarShopActivity;
import com.coco3g.daishu.activity.CategoryActivity;
import com.coco3g.daishu.activity.DiscountOilActivity;
import com.coco3g.daishu.activity.MainActivity;
import com.coco3g.daishu.activity.MemberServiceActivity;
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.adapter.HomeAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.data.TypevauleGotoDictionary;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.coco3g.daishu.view.BannerView;
import com.coco3g.daishu.view.HomeMenuImageView;
import com.coco3g.daishu.view.MarqueeText;
import com.coco3g.daishu.view.SuperRefreshLayout;
import com.coco3g.daishu.view.UPMarqueeView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private Context mContext;
    SuperRefreshLayout mSuperRefreshLayout;
    ListView mListView;
    private View mHomeView, mHeadView;
    BannerView mBanner;
    //    MarqueeView mTxtBoardcast;
    UPMarqueeView mTxtBroadcast;
    List<View> mMarqueeViews = new ArrayList<>();
    HomeMenuImageView[] mMenuRes;
    HomeMenuImageView mMenu1, mMenu2, mMenu3, mMenu4, mMenu5, mMenu6, mMenu7, mMenu8;
    ImageView mImageMycar;
    RelativeLayout mRelativeMycar, mRelativeBroadcast;
    public TextView mTxtMycarTitle, mTxtMycarSubTitle1, mTxtLocation, mEditSearch;
    MarqueeText mTxtMycarSubTitle2;
    ImageView mImageMiddleBanner;
    HomeAdapter mAdapter;
    RelativeLayout.LayoutParams mMycarThumb_lp;
    //
    int[] mNavIconResID = new int[]{R.mipmap.pic_menu_my_car, R.mipmap.pic_menu_vip_icon, R.mipmap.pic_menu_buy_car, R.mipmap.pic_menu_car_insurance,
            R.mipmap.pic_car_gai_zhuang_icon, R.mipmap.pic_menu_gasoline, R.mipmap.pic_menu_second_hand_car, R.mipmap.pic_menu_borrow_money_service};
    String[] mNavTitles = new String[]{"我的汽车", "会员服务", "我要买车", "低价车险", "爱车改装", "折扣油站", "二手车", "借款服务"};
    //
    OnRepairClickListener onRepairClickListener = null;
    private ArrayList<Map<String, String>> mBroadCastList;  //跑马灯

    //广告的页面
    int currPage = 1;
    private ArrayList<Map<String, String>> mOneGuangGaoList;


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
        mTxtLocation = (TextView) mHomeView.findViewById(R.id.tv_home_frag_location);
        mEditSearch = (TextView) mHomeView.findViewById(R.id.edit_home_frag_search);
        /* 头部数据 */
        mHeadView = LayoutInflater.from(getActivity()).inflate(R.layout.view_home_head, null);
        mBanner = (BannerView) mHeadView.findViewById(R.id.banner_home_frag);
//        mTxtBoardcast = (MarqueeView) mHeadView.findViewById(R.id.tv_home_boardcast);
        mTxtBroadcast = (UPMarqueeView) mHeadView.findViewById(R.id.upmarquee_home_head);
        mMenu1 = (HomeMenuImageView) mHeadView.findViewById(R.id.view_home_menu_1);
        mMenu2 = (HomeMenuImageView) mHeadView.findViewById(R.id.view_home_menu_2);
        mMenu3 = (HomeMenuImageView) mHeadView.findViewById(R.id.view_home_menu_3);
        mMenu4 = (HomeMenuImageView) mHeadView.findViewById(R.id.view_home_menu_4);
        mMenu5 = (HomeMenuImageView) mHeadView.findViewById(R.id.view_home_menu_5);
        mMenu6 = (HomeMenuImageView) mHeadView.findViewById(R.id.view_home_menu_6);
        mMenu7 = (HomeMenuImageView) mHeadView.findViewById(R.id.view_home_menu_7);
        mMenu8 = (HomeMenuImageView) mHeadView.findViewById(R.id.view_home_menu_8);
        mImageMycar = (ImageView) mHeadView.findViewById(R.id.image_home_item_mycar_thumb);
        mTxtMycarTitle = (TextView) mHeadView.findViewById(R.id.tv_home_mycar_title);
        mTxtMycarSubTitle1 = (TextView) mHeadView.findViewById(R.id.tv_home_mycar_subtitle1);
        mTxtMycarSubTitle2 = (MarqueeText) mHeadView.findViewById(R.id.tv_home_mycar_subtitle2);
        mRelativeMycar = (RelativeLayout) mHeadView.findViewById(R.id.relative_home_frag_mycar);
        mRelativeBroadcast = (RelativeLayout) mHeadView.findViewById(R.id.relative_home_head_broadcast);
        mMycarThumb_lp = new RelativeLayout.LayoutParams(Global.screenWidth / 5, Global.screenWidth / 5);
        mMycarThumb_lp.addRule(RelativeLayout.CENTER_VERTICAL);
        mImageMycar.setLayoutParams(mMycarThumb_lp);

        mImageMiddleBanner = (ImageView) mHeadView.findViewById(R.id.image_home_middle_banner);
        LinearLayout.LayoutParams banner_lp = new LinearLayout.LayoutParams(Global.screenWidth, Global.screenWidth * 5 / 18);
        mImageMiddleBanner.setLayoutParams(banner_lp);

        mBanner.setScreenRatio(4);
        mMenuRes = new HomeMenuImageView[]{mMenu1, mMenu2, mMenu3, mMenu4, mMenu5, mMenu6, mMenu7, mMenu8};
        for (int i = 0; i < mNavIconResID.length; i++) {
            mMenuRes[i].setIcon(mNavIconResID[i], mNavTitles[i]);
            if (i <= 3) {
                mMenuRes[i].setImagePadding(5, 5, 5, 5);
            } else {
                mMenuRes[i].setImagePadding(5, 0, 5, 5);
            }
        }

        mListView.addHeaderView(mHeadView);
        //
        mSuperRefreshLayout.setCanLoadMore();
        mSuperRefreshLayout.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                mBanner.clearList();
                mAdapter.clearList();
                currPage = 1;
                getBanner();
            }

            @Override
            public void onLoadMore() {
                currPage++;
                getGuangGaoList();
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
        mImageMiddleBanner.setOnClickListener(this);
        mRelativeMycar.setOnClickListener(this);
        //
        mEditSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 先隐藏键盘
                    ((InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(((Activity) mContext).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //进行搜索操作的方法，在该方法中可以加入mEditSearchUser的非空判断
                    String searchKey = mEditSearch.getText().toString().trim();
                    if (TextUtils.isEmpty(searchKey)) {

                    } else {
                        if (TextUtils.isEmpty(searchKey)) {
                            Global.showToast("搜索内容为空", mContext);
                        } else {
                            Intent intent = new Intent(mContext, CarCategoryListActivity.class);
                            intent.putExtra("fromType", 1);
                            intent.putExtra("searchKey", searchKey);
                            startActivity(intent);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
//        //跑马灯
//        mTxtBoardcast.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position, TextView textView) {
//                String url = mBroadCastList.get(position).get("linkurl");
//                if (!TextUtils.isEmpty(url)) {
//                    Intent intent = new Intent(getActivity(), WebActivity.class);
//                    intent.putExtra("url", url);
//                    startActivity(intent);
//                }
//            }
//        });

    }

    @Override
    public void onResume() {
        super.onResume();
        setMyCarInfo();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.relative_home_frag_mycar:     //首页显示的我的汽车的信息
                intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url", Global.H5Map.get("mycar"));
                startActivity(intent);

                break;

            case R.id.view_home_menu_1:  //我的汽车
//                intentToWeb(Global.H5Map.get("mycar"), false);
                intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url", Global.H5Map.get("mycar"));
                startActivity(intent);

                break;

            case R.id.view_home_menu_2:  //会员服务
                intent = new Intent(getActivity(), MemberServiceActivity.class);
                startActivity(intent);

                break;

            case R.id.view_home_menu_3:  //我要买车
                intent = new Intent(getActivity(), CarShopActivity.class);
                startActivity(intent);

                break;

            case R.id.view_home_menu_4: //低价车险
                intentToWeb(Global.H5Map.get("baoxian"), true);

                break;

            case R.id.view_home_menu_5:  //爱车改装

                intent = new Intent(getActivity(), CategoryActivity.class);
                startActivity(intent);

                break;

            case R.id.view_home_menu_6:  //折扣油站
//                intentToWeb(Global.H5Map.get("youka"));
                intent = new Intent(getActivity(), DiscountOilActivity.class);
                startActivity(intent);

                break;

            case R.id.view_home_menu_7:  //二手车
                intentToWeb(Global.H5Map.get("used_car"), false);

                break;

            case R.id.view_home_menu_8:  //  借款服务
                intentToWeb(Global.H5Map.get("daikuan"), false);

                break;

            case R.id.image_home_middle_banner:  //单个广告
                intent = new Intent(mContext, WebActivity.class);
                if (!TextUtils.isEmpty(mOneGuangGaoList.get(0).get("url"))) {
                    intent.putExtra("url", mOneGuangGaoList.get(0).get("url"));
                    mContext.startActivity(intent);
                }

                break;

        }
    }


    public void intentToWeb(String url, boolean hideTopbar) {
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
        if (hideTopbar) {
            intent.putExtra("hidetopbar", true);
        }
        startActivity(intent);
    }


    //设置我的汽车的状态的情况
    public void setMyCarInfo() {
        if (Global.USERINFOMAP != null) {
            ArrayList<Map<String, String>> myCarList = (ArrayList<Map<String, String>>) Global.USERINFOMAP.get("mycars");
            if (myCarList == null || myCarList.size() <= 0) {
                mRelativeMycar.setVisibility(View.GONE);
            } else {
                mRelativeMycar.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(myCarList.get(0).get("brandthumb"), mImageMycar, new DisplayImageOptionsUtils().init(R.mipmap.pic_item_test));
                mTxtMycarTitle.setText(myCarList.get(0).get("cartype") + "    " + myCarList.get(0).get("chepai"));
                //
                Map<String, String> myCarCondition = (Map<String, String>) Global.USERINFOMAP.get("mycar_record");
                mTxtMycarSubTitle1.setText(myCarCondition.get("type"));
                mTxtMycarSubTitle2.setText(myCarCondition.get("text"));
                //
                Map<String, String> myCarMap = new HashMap<>();
                myCarMap.put("brandthumb", myCarList.get(0).get("brandthumb"));
                myCarMap.put("cartype", myCarList.get(0).get("cartype"));
                myCarMap.put("chepai", myCarList.get(0).get("chepai"));
                myCarMap.put("type", myCarCondition.get("type"));
                myCarMap.put("text", myCarCondition.get("text"));
                Global.serializeData(mContext, myCarMap, Global.MY_CAR);
            }

        } else if (Global.readSerializeData(mContext, Global.MY_CAR) != null) {
            Map<String, String> myCarMap = (Map<String, String>) Global.readSerializeData(mContext, Global.MY_CAR);
            ImageLoader.getInstance().displayImage(myCarMap.get("brandthumb"), mImageMycar, new DisplayImageOptionsUtils().init(R.mipmap.pic_item_test));
            String carType = myCarMap.get("cartype");
            String chePai = myCarMap.get("chepai");
            if (!TextUtils.isEmpty(carType) && !TextUtils.isEmpty(chePai)) {
                mTxtMycarTitle.setText(carType + "    " + chePai);
            } else {
                mTxtMycarTitle.setText("请登录后查看");
            }
            //
            mTxtMycarSubTitle1.setText(myCarMap.get("type"));
            mTxtMycarSubTitle2.setText(myCarMap.get("text"));
        }
    }

    //设置推送信息来的车的状况
    public void setMycarTuiSongInfo(String subtitle1, String subtitle2) {

        mTxtMycarSubTitle1.setText(subtitle1);
        mTxtMycarSubTitle2.setText(subtitle2);
        Map<String, String> myCarMap = (Map<String, String>) Global.readSerializeData(mContext, Global.MY_CAR);
        if (myCarMap == null) {
            myCarMap = new HashMap<>();
        }
        myCarMap.put("type", subtitle1);
        myCarMap.put("text", subtitle2);
        Global.serializeData(mContext, myCarMap, Global.MY_CAR);

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
                getBroadCastData();
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

    //获取跑马灯
    public void getBroadCastData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "5");    //1:首页轮播图， 2:商品汇， 3:维修救援， 4:汽车商城， 5:首页广播， 6:商城头条， 7:维修通告，
        new BaseDataPresenter(mContext).loadData(DataUrl.GET_BANNER_IMAGE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                mBroadCastList = (ArrayList<Map<String, String>>) data.response;
                if (mBroadCastList != null && mBroadCastList.size() > 0) {
//                    ArrayList<String> list = new ArrayList<String>();
//                    for (int i = 0; i < mBroadCastList.size(); i++) {
//                        list.add(mBroadCastList.get(i).get("title"));
//                    }
//                    mTxtBoardcast.startWithList(list);
                    mRelativeBroadcast.setVisibility(View.VISIBLE);
                    setBroadcastInfo();
                } else {
                    mRelativeBroadcast.setVisibility(View.GONE);
                }
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

    //获取所有的H5
    public void getH5URL() {
        HashMap<String, String> params = new HashMap<>();
        new BaseDataPresenter(mContext).loadData(DataUrl.GET_H5, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Global.H5Map = (Map<String, String>) data.response;
                //
                getOneGuangGao();
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


    //获取首页单个广告
    public void getOneGuangGao() {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("catid", "4");
        new BaseDataPresenter(mContext).loadData(DataUrl.GET_HOME_GUANG_GAO_LIST, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                mOneGuangGaoList = (ArrayList<Map<String, String>>) data.response;
                if (mOneGuangGaoList == null || mOneGuangGaoList.size() <= 0) {
                    mImageMiddleBanner.setVisibility(View.GONE);
                } else {
                    mImageMiddleBanner.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(mOneGuangGaoList.get(0).get("image"), mImageMiddleBanner, new DisplayImageOptionsUtils().init());
                }
                //
                getGuangGaoList();
                //
                setMyCarInfo();

            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, mContext);
                mSuperRefreshLayout.onLoadComplete();
                currPage--;
            }

            @Override
            public void onError() {
                mSuperRefreshLayout.onLoadComplete();
                currPage--;
            }
        });
    }

    //获取广告列表
    public void getGuangGaoList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", currPage + "");
        params.put("catid", "1");
        new BaseDataPresenter(mContext).loadData(DataUrl.GET_HOME_GUANG_GAO_LIST, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, String>> homeList = (ArrayList<Map<String, String>>) data.response;
                if (homeList == null || homeList.size() <= 0) {
                    currPage--;
                    mSuperRefreshLayout.onLoadComplete();
                    return;
                }

                //添加我的爱车信息
                if (mAdapter.getList() == null || mAdapter.getList().size() <= 0) {
//                    Map<String, String> myCarMap = new HashMap<String, String>();
//                    myCarMap.put("type", "mycar");
//                    homeList.add(0, myCarMap);
                    //
                    mAdapter.setList(homeList);
                } else {
                    mAdapter.addList(homeList);
                }
                //
                mSuperRefreshLayout.onLoadComplete();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, mContext);
                mSuperRefreshLayout.onLoadComplete();
                currPage--;
            }

            @Override
            public void onError() {
                mSuperRefreshLayout.onLoadComplete();
                currPage--;
            }
        });
    }


    /**
     * 初始化需要循环的View
     * 为了灵活的使用滚动的View，所以把滚动的内容让用户自定义
     * 假如滚动的是三条或者一条，或者是其他，只需要把对应的布局，和这个方法稍微改改就可以了，
     */
    private void setBroadcastInfo() {
        for (int i = 0; i < mBroadCastList.size(); i = i + 2) {
            final int position = i;
            //设置滚动的单个布局
            LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.view_marquee, null);
            //初始化布局的控件
            TextView tv1 = (TextView) moreView.findViewById(R.id.tv_marquee_title1);
            TextView tv2 = (TextView) moreView.findViewById(R.id.tv_marquee_title2);

            /**
             * 设置监听
             */
            moreView.findViewById(R.id.rl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = mBroadCastList.get(position).get("linkurl");
                    if (!TextUtils.isEmpty(url)) {
                        Intent intent = new Intent(getActivity(), WebActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                }
            });
            /**
             * 设置监听
             */
            moreView.findViewById(R.id.rl2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = mBroadCastList.get(position + 1).get("linkurl");
                    if (!TextUtils.isEmpty(url)) {
                        Intent intent = new Intent(getActivity(), WebActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                }
            });
            //进行对控件赋值
            tv1.setText(mBroadCastList.get(i).get("title"));
            if (mBroadCastList.size() > i + 1) {
                //因为淘宝那儿是两条数据，但是当数据是奇数时就不需要赋值第二个，所以加了一个判断，还应该把第二个布局给隐藏掉
                tv2.setText(mBroadCastList.get(i + 1).get("title"));
            } else {
                moreView.findViewById(R.id.rl2).setVisibility(View.GONE);
            }

            //添加到循环滚动数组里面去
            mMarqueeViews.add(moreView);
        }
        mTxtBroadcast.setViews(mMarqueeViews);
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

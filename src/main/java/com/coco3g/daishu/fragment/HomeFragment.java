package com.coco3g.daishu.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.coco3g.daishu.R;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.view.BannerView;
import com.coco3g.daishu.view.BottomNavImageView;
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
    BottomNavImageView[] mMenuRes;
    BottomNavImageView mMenu1, mMenu2, mMenu3, mMenu4, mMenu5, mMenu6, mMenu7, mMenu8;
    ImageView mImageMiddleBanner;
    //
    int[] mNavIconResID = new int[]{R.mipmap.pic_menu_mime_car, R.mipmap.pic_menu_repair_car, R.mipmap.pic_menu_wash_car, R.mipmap.pic_menu_nearby_carshop,
            R.mipmap.pic_menu_buy_car, R.mipmap.pic_menu_car_goodsing, R.mipmap.pic_menu_gasoline, R.mipmap.pic_menu_car_insurance};
    String[] mNavTitles = new String[]{"我的汽车", "维护养修", "洗车", "附近门店", "我要买车", "车载用品", "打折油卡", "机动车险"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mHomeView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
        init();
        getBanner();
        return mHomeView;
    }

    private void init() {
        mSuperRefreshLayout = (SuperRefreshLayout) mHomeView.findViewById(R.id.superrefresh_home);
        mListView = (ListView) mHomeView.findViewById(R.id.listview_home);
        /* 头部数据 */
        mHeadView = LayoutInflater.from(getActivity()).inflate(R.layout.view_home_head, null);
        mBanner = (BannerView) mHeadView.findViewById(R.id.banner_home_frag);
        mTxtBoardcast = (TextView) mHeadView.findViewById(R.id.tv_home_boardcast);
        mMenu1 = (BottomNavImageView) mHeadView.findViewById(R.id.view_home_menu_1);
        mMenu2 = (BottomNavImageView) mHeadView.findViewById(R.id.view_home_menu_2);
        mMenu3 = (BottomNavImageView) mHeadView.findViewById(R.id.view_home_menu_3);
        mMenu4 = (BottomNavImageView) mHeadView.findViewById(R.id.view_home_menu_4);
        mMenu5 = (BottomNavImageView) mHeadView.findViewById(R.id.view_home_menu_5);
        mMenu6 = (BottomNavImageView) mHeadView.findViewById(R.id.view_home_menu_6);
        mMenu7 = (BottomNavImageView) mHeadView.findViewById(R.id.view_home_menu_7);
        mMenu8 = (BottomNavImageView) mHeadView.findViewById(R.id.view_home_menu_8);
        mImageMiddleBanner = (ImageView) mHeadView.findViewById(R.id.image_home_middle_banner);
        mBanner.setScreenRatio(2);
        mMenuRes = new BottomNavImageView[]{mMenu1, mMenu2, mMenu3, mMenu4, mMenu5, mMenu6, mMenu7, mMenu8};
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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.tv_go_frag_mission:  //任务书
//                Intent intent1 = new Intent(mContext, MissionBookActivity.class);
//                startActivity(intent1);
////                checkIfHasMission();
//
//                break;
//            case R.id.tv_go_frag_saoma:  //扫码
//                new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.START_LOCATION, null);
//                Intent intent = new Intent(mContext, CaptureActivity.class);
//                ((Activity) mContext).startActivityForResult(intent, Constants.RESULT_SCAN);
//                break;
//        }
    }

    //获取banner图片
    public void getBanner() {
        HashMap<String, String> params = new HashMap<>();
        new BaseDataPresenter(mContext).loadData(DataUrl.GET_BANNER_IMAGE, params, mContext.getResources().getString(R.string.loading), new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Map<String, Object> dataMap = (Map<String, Object>) data.response;
                ArrayList<Map<String, String>> bannerImageList = (ArrayList<Map<String, String>>) dataMap.get("banner");
                mBanner.loadData(bannerImageList);
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

}

package com.coco3g.daishu.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.view.BannerView;
import com.coco3g.daishu.view.HomeMenuImageView;
import com.coco3g.daishu.view.SuperRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ReadFragment extends Fragment implements View.OnClickListener {
    SuperRefreshLayout mSuperRefreshLayout;
    private View mReadView;
    BannerView mBanner;
    LinearLayout mLinearMenu;
    TextView mTxtReadBoradcast;
    //
    LinearLayout.LayoutParams lpLinear;
    HomeMenuImageView[] mMenuRes;
    HomeMenuImageView mReadMenu1, mReadMenu2, mReadMenu3, mReadMenu4, mReadMenu5;
    int[] mNavIconResID = new int[]{R.mipmap.pic_car_shop, R.mipmap.pic_car_insurance, R.mipmap.pic_car_tyre, R.mipmap.pic_car_gasoline,
            R.mipmap.pic_car_other};
    String[] mNavTitles = new String[]{"汽车商城", "汽车保险", "车载用品", "油品区", "其他产品"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mReadView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_read, null);
        lpLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Global.screenWidth * 3 / 2 + Global.dipTopx(getActivity(), 20));
        lpLinear.topMargin = Global.dipTopx(getActivity(), 10);
        init();
        getBanner();
        return mReadView;
    }

    private void init() {
        mSuperRefreshLayout = (SuperRefreshLayout) mReadView.findViewById(R.id.superrefresh_read);
        mBanner = (BannerView) mReadView.findViewById(R.id.banner_read_frag);
        mLinearMenu = (LinearLayout) mReadView.findViewById(R.id.linear_read_menu);
        mLinearMenu.setLayoutParams(lpLinear);
        mTxtReadBoradcast = (TextView) mReadView.findViewById(R.id.tv_read_boardcast);
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


    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.view_read_menu_1:  //

                break;

            case R.id.view_read_menu_2:  //汽车保险
                intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url", DataUrl.QI_CHE_BAO_XIAN);
                startActivity(intent);


                break;

            case R.id.view_read_menu_3:  //

                break;

            case R.id.view_read_menu_4:  //油品区
                intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url", DataUrl.YOU_PIN_QU);
                startActivity(intent);

                break;

            case R.id.view_read_menu_5:  //

                break;
        }
    }

    //获取banner图片
    private void getBanner() {
        HashMap<String, String> params = new HashMap<>();
        new BaseDataPresenter(getActivity()).loadData(DataUrl.GET_BANNER_IMAGE, params, getActivity().getResources().getString(R.string.loading), new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Map<String, Object> dataMap = (Map<String, Object>) data.response;
                ArrayList<Map<String, String>> bannerImageList = (ArrayList<Map<String, String>>) dataMap.get("banner");
                mBanner.loadData(bannerImageList);
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

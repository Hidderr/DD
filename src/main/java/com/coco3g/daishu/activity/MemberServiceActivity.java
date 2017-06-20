package com.coco3g.daishu.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.Constants;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.view.BannerView;
import com.coco3g.daishu.view.SuperRefreshLayout;
import com.coco3g.daishu.view.TopBarView;
import com.sunfusheng.marqueeview.MarqueeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by lisen on 16/2/4.
 */
public class MemberServiceActivity extends BaseActivity implements View.OnClickListener {
    private TopBarView mTopBar;
    private BannerView mBanner;
    private SuperRefreshLayout mSuperRefresh;
    private MarqueeView marqueeView;
    private TextView mTxtRepair, mTxtNearbyStore, mTxtVisitingService;
    //
    private ArrayList<Map<String, String>> mBroadCastList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_service);
        init();
        mSuperRefresh.setRefreshingLoad();
    }


    private void init() {
        mTopBar = (TopBarView) findViewById(R.id.topbar_membar_service);
        mTopBar.setTitle("会员服务");
        mBanner = (BannerView) findViewById(R.id.banner_member_service);
        mBanner.setScreenRatio(2);
        marqueeView = (MarqueeView) findViewById(R.id.tv_member_service_boardcast);
        mTxtRepair = (TextView) findViewById(R.id.tv_member_service_repair);
        mTxtNearbyStore = (TextView) findViewById(R.id.tv_member_service_nearby_store);
        mTxtVisitingService = (TextView) findViewById(R.id.tv_member_service_income_visiting_service);
        mSuperRefresh = (SuperRefreshLayout) findViewById(R.id.sr_member_service);
        //
        mSuperRefresh.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                getBanner();
            }

            @Override
            public void onLoadMore() {

            }
        });
        //

        mTxtRepair.setOnClickListener(this);
        mTxtNearbyStore.setOnClickListener(this);
        mTxtVisitingService.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_start_come_in:  //维修保养


                break;

            case R.id.tv_member_service_nearby_store:  //附近门店


                break;

            case R.id.tv_member_service_income_visiting_service:  //上门服务


                break;
        }
    }


    //获取banner图片
    public void getBanner() {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "12");
        new BaseDataPresenter(this).loadData(DataUrl.GET_BANNER_IMAGE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, String>> bannerList = (ArrayList<Map<String, String>>) data.response;
                mBanner.loadData(bannerList);
                //
                getBroadCastData();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, MemberServiceActivity.this);
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefresh.onLoadComplete();
            }
        });
    }

    //获取跑马灯
    public void getBroadCastData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "13");
        new BaseDataPresenter(this).loadData(DataUrl.GET_BANNER_IMAGE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                mBroadCastList = (ArrayList<Map<String, String>>) data.response;
                if (mBroadCastList != null && mBroadCastList.size() > 0) {
                    ArrayList<String> list = new ArrayList<String>();
                    for (int i = 0; i < mBroadCastList.size(); i++) {
                        list.add(mBroadCastList.get(i).get("title"));
                    }
                    marqueeView.startWithList(list);
                }
                //
                mSuperRefresh.onLoadComplete();
                mSuperRefresh.setEnabled(false);
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, MemberServiceActivity.this);
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefresh.onLoadComplete();
            }
        });
    }

}

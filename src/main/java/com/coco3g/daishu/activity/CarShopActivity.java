package com.coco3g.daishu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.CarDetailTypeAdapter;
import com.coco3g.daishu.adapter.CarShopAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.Constants;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.view.BannerView;
import com.coco3g.daishu.view.SuperRefreshLayout;
import com.coco3g.daishu.view.TopBarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CarShopActivity extends BaseActivity implements View.OnClickListener {
    private TopBarView mTopbar;
    private ListView mListView, mListViewRight;
    private View mHeadView;
    private SuperRefreshLayout mSuperRefresh;
    private CarShopAdapter mAdapter, mCarTypeAdapter;
    private DrawerLayout mDrawerLayout;


    private TextView mTxtHot1, mTxtHot2, mTxtHot3, mTxtHot4;
    private BannerView mBannerView;
    private ImageView mImageRecomd1, mImageRecomd2, mImageRecomd3, mImageRecomd4;

    private DrawerLayout.LayoutParams listView_lp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_shop);
        Global.getScreenWH(this);

        initView();
        mSuperRefresh.setRefreshingLoad();

    }

    private void initView() {
        mTopbar = (TopBarView) findViewById(R.id.topbar_car_shop);
        mTopbar.setTitle(getResources().getString(R.string.car_shop));
        //
        mListView = (ListView) findViewById(R.id.listview_car_shop);
        mListViewRight = (ListView) findViewById(R.id.listview_car_shop_slide_right);
        mSuperRefresh = (SuperRefreshLayout) findViewById(R.id.sr_car_shop);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_main);
        mAdapter = new CarShopAdapter(this);
        mCarTypeAdapter = new CarShopAdapter(this);
        //
//        listView_lp = new DrawerLayout.LayoutParams(Global.screenWidth * 2 / 3, DrawerLayout.LayoutParams.MATCH_PARENT);
//        mListViewRight.setLayoutParams(listView_lp);
        //
        mHeadView = LayoutInflater.from(this).inflate(R.layout.view_car_shop_headview, null);
        mBannerView = (BannerView) mHeadView.findViewById(R.id.banner_car_shop);
        mBannerView.setScreenRatio(2);
        mImageRecomd1 = (ImageView) mHeadView.findViewById(R.id.tv_car_shop_recommend_1);
        mImageRecomd2 = (ImageView) mHeadView.findViewById(R.id.tv_car_shop_recommend_2);
        mImageRecomd3 = (ImageView) mHeadView.findViewById(R.id.tv_car_shop_recommend_3);
        mImageRecomd4 = (ImageView) mHeadView.findViewById(R.id.tv_car_shop_recommend_4);
        mTxtHot1 = (TextView) mHeadView.findViewById(R.id.tv_car_shop_hotsale_1);
        mTxtHot2 = (TextView) mHeadView.findViewById(R.id.tv_car_shop_hotsale_2);
        mTxtHot3 = (TextView) mHeadView.findViewById(R.id.tv_car_shop_hotsale_3);
        mTxtHot4 = (TextView) mHeadView.findViewById(R.id.tv_car_shop_hotsale_4);
        //
        mImageRecomd1.setOnClickListener(this);
        mImageRecomd2.setOnClickListener(this);
        mImageRecomd3.setOnClickListener(this);
        mImageRecomd4.setOnClickListener(this);
        mTxtHot1.setOnClickListener(this);
        mTxtHot2.setOnClickListener(this);
        mTxtHot3.setOnClickListener(this);
        mTxtHot4.setOnClickListener(this);
        //
        mSuperRefresh.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                mAdapter.clearList();
                getBanner();
            }

            @Override
            public void onLoadMore() {

            }
        });
        //
        mListView.addHeaderView(mHeadView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openDrawerLayout();
            }
        });
        //
        mDrawerLayout.setDrawerLockMode(mDrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        //
        mListViewRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CarShopActivity.this, CarDetailTypeActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.tv_car_shop_recommend_1:  //

                break;

            case R.id.tv_car_shop_recommend_2:  //

                break;

            case R.id.tv_car_shop_recommend_3:  //

                break;

            case R.id.tv_car_shop_recommend_4:  //

                break;

            case R.id.tv_car_shop_hotsale_1:  //

                break;

            case R.id.tv_car_shop_hotsale_2:  //

                break;

            case R.id.tv_car_shop_hotsale_3:  //

                break;

            case R.id.tv_car_shop_hotsale_4:  //

                break;

        }
    }


    //开启侧滑
    public void openDrawerLayout() {
        mDrawerLayout.setDrawerLockMode(mDrawerLayout.LOCK_MODE_UNLOCKED);
        mDrawerLayout.openDrawer(mListViewRight);
    }

    //关闭侧滑
    public void closeDrawerLayout() {
        mDrawerLayout.closeDrawer(mListViewRight);
        mDrawerLayout.setDrawerLockMode(mDrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }


    //获取banner图片
    public void getBanner() {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "4");
        new BaseDataPresenter(this).loadData(DataUrl.GET_BANNER_IMAGE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, String>> bannerList = (ArrayList<Map<String, String>>) data.response;
                mBannerView.loadData(bannerList);
                //
                mListView.setAdapter(mAdapter);
                mListViewRight.setAdapter(mCarTypeAdapter);
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, CarShopActivity.this);
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefresh.onLoadComplete();
            }
        });
    }

}

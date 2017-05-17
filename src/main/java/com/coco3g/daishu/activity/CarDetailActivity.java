package com.coco3g.daishu.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.ViewPagerAdapter;
import com.coco3g.daishu.view.BusinessMapView;
import com.coco3g.daishu.view.CanShuDetailView;
import com.coco3g.daishu.view.NewestOfferView;
import com.coco3g.daishu.view.TopBarView;

import java.util.ArrayList;

public class CarDetailActivity extends BaseActivity {
    private TopBarView mTopbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private ArrayList<View> mViewList = new ArrayList<>();

    String[] mViewPagerTitles = new String[]{"最新报价", "商家地图", "参数详情"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_detail);
        initView();
    }

    private void initView() {
        mTopbar = (TopBarView) findViewById(R.id.topbar_car_detail);
        mTopbar.setTitle("奥迪A1详情");
        //
        mTabLayout = (TabLayout) findViewById(R.id.tablayout_car_detail);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_car_detail);
        //
        NewestOfferView newestOfferView = new NewestOfferView(this);
        BusinessMapView businessMapView = new BusinessMapView(this);
        CanShuDetailView canShuDetailView = new CanShuDetailView(this);

        mViewList.add(newestOfferView);
        mViewList.add(businessMapView);
        mViewList.add(canShuDetailView);
        //
        mAdapter = new ViewPagerAdapter(mViewList, mViewPagerTitles);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mAdapter);


    }
}

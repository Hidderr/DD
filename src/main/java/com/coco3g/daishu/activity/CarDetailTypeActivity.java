package com.coco3g.daishu.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.CarDetailTypeAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
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

public class CarDetailTypeActivity extends BaseActivity {
    private View mHeadView;
    private BannerView mBanner;
    private MarqueeView marqueeView;
    private SuperRefreshLayout mSuperRefresh;
    private ListView mListView;
    private CarDetailTypeAdapter mAdapter;
    private TopBarView mTopbar;

    private ArrayList<Map<String, String>> mBroadCastList;  //跑马灯

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail_type);
        init();
    }

    private void init() {
        mTopbar = (TopBarView) findViewById(R.id.topbar_cat_detail_type);
        mTopbar.setTitle("奥迪A1详情");
        mHeadView = LayoutInflater.from(this).inflate(R.layout.view_car_detail_type_head, null);
        mBanner = (BannerView) mHeadView.findViewById(R.id.banner_car_detail_type);
        mBanner.setScreenRatio(3);
        marqueeView = (MarqueeView) mHeadView.findViewById(R.id.tv_car_detail_type_boardcast);
        mListView = (ListView) findViewById(R.id.listview_car_detail_type);
        mSuperRefresh = (SuperRefreshLayout) findViewById(R.id.sr_car_detail_type);
        //
        //跑马灯
        marqueeView.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TextView textView) {
                String url = mBroadCastList.get(position).get("linkurl");
                if (!TextUtils.isEmpty(url)) {
                    Intent intent = new Intent(CarDetailTypeActivity.this, WebActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            }
        });
        mSuperRefresh.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                mBanner.clearList();
                getBanner();
            }

            @Override
            public void onLoadMore() {

            }
        });
        //
        mAdapter = new CarDetailTypeAdapter(this);
        //
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CarDetailTypeActivity.this, CarDetailActivity.class);
                startActivity(intent);
            }
        });
        //
        mSuperRefresh.setRefreshingLoad();
    }


    //获取banner图片
    public void getBanner() {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "1");    //1:首页轮播图， 2:商品汇， 3:维修救援，
        new BaseDataPresenter(this).loadData(DataUrl.GET_BANNER_IMAGE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, String>> bannerList = (ArrayList<Map<String, String>>) data.response;
                mBanner.loadData(bannerList);
                if (mListView.getHeaderViewsCount() <= 0) {
                    mListView.addHeaderView(mHeadView);
                }
                //
                getBroadCastData();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, CarDetailTypeActivity.this);
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefresh.onLoadComplete();
            }
        });
    }


    //获取跑马灯
    private void getBroadCastData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "5");    //1:首页轮播图， 2:商品汇， 3:维修救援， 4:汽车商城， 5:首页广播， 6:商城头条， 7:维修通告，
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
                mListView.setAdapter(mAdapter);
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, CarDetailTypeActivity.this);
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefresh.onLoadComplete();
            }
        });
    }

}

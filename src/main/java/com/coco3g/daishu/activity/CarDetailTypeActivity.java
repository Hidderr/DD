package com.coco3g.daishu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.coco3g.daishu.view.UPMarqueeView;
import com.sunfusheng.marqueeview.MarqueeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarDetailTypeActivity extends BaseActivity {
    private View mHeadView;
    private BannerView mBanner;
    //    private MarqueeView marqueeView;
    private UPMarqueeView upMarqueeView;
    private SuperRefreshLayout mSuperRefresh;
    private ListView mListView;
    private CarDetailTypeAdapter mAdapter;
    private TopBarView mTopbar;
    private RelativeLayout mRelativeBroadcast;
    List<View> mMarqueeViews = new ArrayList<>();

    private ArrayList<Map<String, String>> mBroadCastList;  //跑马灯

    private String title = "", carTypeId = "";
    //
    private int currPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail_type);
        title = getIntent().getStringExtra("title");
        carTypeId = getIntent().getStringExtra("carTypeId");
        init();
    }

    private void init() {
        mTopbar = (TopBarView) findViewById(R.id.topbar_cat_detail_type);
        mTopbar.setTitle(title);
        mHeadView = LayoutInflater.from(this).inflate(R.layout.view_car_detail_type_head, null);
        mBanner = (BannerView) mHeadView.findViewById(R.id.banner_car_detail_type);
        mBanner.setScreenRatio(2);
        mRelativeBroadcast = (RelativeLayout) mHeadView.findViewById(R.id.relative_car_detail_type_broadcast);
//        marqueeView = (MarqueeView) mHeadView.findViewById(R.id.tv_car_detail_type_boardcast);
        upMarqueeView = (UPMarqueeView) mHeadView.findViewById(R.id.upmarquee_car_detail_type_head);
        mListView = (ListView) findViewById(R.id.listview_car_detail_type);
        mSuperRefresh = (SuperRefreshLayout) findViewById(R.id.sr_car_detail_type);
        //
        mAdapter = new CarDetailTypeAdapter(CarDetailTypeActivity.this);
//        mListView.setAdapter(mAdapter);
        //跑马灯
//        marqueeView.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position, TextView textView) {
//                String url = mBroadCastList.get(position).get("linkurl");
//                if (!TextUtils.isEmpty(url)) {
//                    Intent intent = new Intent(CarDetailTypeActivity.this, WebActivity.class);
//                    intent.putExtra("url", url);
//                    startActivity(intent);
//                }
//            }
//        });
        mSuperRefresh.setCanLoadMore();
        mSuperRefresh.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
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
                getCarTypeDetail();

            }
        });
        //
        mAdapter = new CarDetailTypeAdapter(this);
        //
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(CarDetailTypeActivity.this, CarDetailActivity.class);
//                startActivity(intent);
//            }
//        });
        //跑马灯
//        marqueeView.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position, TextView textView) {
//                String url = mBroadCastList.get(position).get("linkurl");
//                if (!TextUtils.isEmpty(url)) {
//                    Intent intent = new Intent(CarDetailTypeActivity.this, WebActivity.class);
//                    intent.putExtra("url", url);
//                    startActivity(intent);
//                }
//            }
//        });
        //
        //
        mSuperRefresh.setRefreshingLoad();
    }


    //获取banner图片
    public void getBanner() {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "10");
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
        params.put("type", "11");
        new BaseDataPresenter(this).loadData(DataUrl.GET_BANNER_IMAGE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                mBroadCastList = (ArrayList<Map<String, String>>) data.response;
                if (mBroadCastList != null && mBroadCastList.size() > 0) {
//                    ArrayList<String> list = new ArrayList<String>();
//                    for (int i = 0; i < mBroadCastList.size(); i++) {
//                        list.add(mBroadCastList.get(i).get("title"));
//                    }
//                    marqueeView.startWithList(list);
                    setBroadcastInfo();
                    mRelativeBroadcast.setVisibility(View.VISIBLE);
                } else {
                    mRelativeBroadcast.setVisibility(View.GONE);
                }

                getCarTypeDetail();
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

    //获取某个车型的不同的配置
    private void getCarTypeDetail() {
        HashMap<String, String> params = new HashMap<>();
        params.put("pid", carTypeId);
        params.put("page", currPage + "");
        new BaseDataPresenter(this).loadData(DataUrl.GET_CAR_TYPE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) data.response;
                //
                if (list == null || list.size() <= 0) {
                    currPage--;
                    mSuperRefresh.onLoadComplete();
                    return;
                }
                if (mAdapter.getList() == null || mAdapter.getList().size() <= 0) {
                    mAdapter.setList(list);
                    mListView.setAdapter(mAdapter);
                } else {
                    mAdapter.addList(list);
                }
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, CarDetailTypeActivity.this);
                mSuperRefresh.onLoadComplete();
                currPage--;
            }

            @Override
            public void onError() {
                mSuperRefresh.onLoadComplete();
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
            LinearLayout moreView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_marquee, null);
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
                        Intent intent = new Intent(CarDetailTypeActivity.this, WebActivity.class);
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
                        Intent intent = new Intent(CarDetailTypeActivity.this, WebActivity.class);
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
        upMarqueeView.setViews(mMarqueeViews);
    }


}

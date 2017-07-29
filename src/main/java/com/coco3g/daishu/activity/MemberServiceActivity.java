package com.coco3g.daishu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.MemberServiceAdapter;
import com.coco3g.daishu.adapter.StoreShaiXuanAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.Constants;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.data.TypevauleGotoDictionary;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.view.BannerView;
import com.coco3g.daishu.view.MyListView;
import com.coco3g.daishu.view.SuperRefreshLayout;
import com.coco3g.daishu.view.TopBarView;
import com.sunfusheng.marqueeview.MarqueeView;

import java.lang.reflect.Member;
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
    //
    private ListView mListView;
    //    private MemberServiceAdapter mAdapter;
    private StoreShaiXuanAdapter mAdapter;

    private int currPage = 1;  //门店信息

    private View mHeadView;


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
        mListView = (ListView) findViewById(R.id.listview_member_service);
        mSuperRefresh = (SuperRefreshLayout) findViewById(R.id.sr_member_service);
        //
        mHeadView = LayoutInflater.from(this).inflate(R.layout.view_member_service_header, null);
        mBanner = (BannerView) mHeadView.findViewById(R.id.banner_member_service);
        mBanner.setScreenRatio(2);
        marqueeView = (MarqueeView) mHeadView.findViewById(R.id.tv_member_service_boardcast);
        mTxtRepair = (TextView) mHeadView.findViewById(R.id.tv_member_service_repair);
        mTxtNearbyStore = (TextView) mHeadView.findViewById(R.id.tv_member_service_nearby_store);
        mTxtVisitingService = (TextView) mHeadView.findViewById(R.id.tv_member_service_income_visiting_service);
        //
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
                getStoreList();
            }
        });
        //

        mTxtRepair.setOnClickListener(this);
        mTxtNearbyStore.setOnClickListener(this);
        mTxtVisitingService.setOnClickListener(this);
        //
        mAdapter = new StoreShaiXuanAdapter(this);
        mListView.setAdapter(mAdapter);

    }


    @Override
    public void onClick(View view) {
        Intent intent = null;

        switch (view.getId()) {
            case R.id.tv_member_service_repair:  //维修保养
                if (!Global.checkoutLogin(MemberServiceActivity.this)) {
                    return;
                }
//                intent = new Intent(mContext, RepairWebsiteActivity.class);
                intent = new Intent(MemberServiceActivity.this, ShaiXuanListActivity.class);
                intent.putExtra("typeid", "1");   //2=洗车店，1=维修养护和维修救援，附近门店(不传参)，汽修厂、爱车保姆快修店（根据获取的维修类型id）
                intent.putExtra("title", "快修门店");
                startActivity(intent);

                break;

            case R.id.tv_member_service_nearby_store:  //洗车
                //洗车
                if (!Global.checkoutLogin(MemberServiceActivity.this)) {
                    return;
                }
                intent = new Intent(MemberServiceActivity.this, ShaiXuanListActivity.class);
                intent.putExtra("typeid", "2");   //2=洗车店，1=维修养护和维修救援，附近门店(不传参)，汽修厂、爱车保姆快修店（根据获取的维修类型id）
                intent.putExtra("title", "优惠洗车");
                startActivity(intent);

                break;

            case R.id.tv_member_service_income_visiting_service:  //上门服务
                intentToWeb(Global.H5Map.get("home_service"));

                break;
        }
    }

    public void intentToWeb(String url) {
        if (!Global.checkoutLogin(MemberServiceActivity.this)) {
            return;
        }
        if (url.equals("#")) {
            return;
        }

        if (url.startsWith("http://coco3g-app/open_tabview")) {
            TypevauleGotoDictionary typevauleGotoDictionary = new TypevauleGotoDictionary(MemberServiceActivity.this);
            typevauleGotoDictionary.gotoViewChoose(url);
            return;
        }

        Intent intent = new Intent(MemberServiceActivity.this, WebActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
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
                getStoreList();
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

    //获取附近的汽车店的信息
    public void getStoreList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("lat", Global.mCurrLat + "");
        params.put("lng", Global.mCurrLng + "");
        params.put("order", "1");
        params.put("page", currPage + "");
        Log.e("地图传参", "area " + params.get("area") + "   qualific " + params.get("qualific") + "   order" + params.get("order") + "    joinid" + params.get("joinid"));
        new BaseDataPresenter(MemberServiceActivity.this).loadData(DataUrl.GET_REPAIR_STORE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {

                if (mListView.getHeaderViewsCount() <= 0) {
                    mListView.addHeaderView(mHeadView);
                }
                ///
                ArrayList<Map<String, String>> repairList = (ArrayList<Map<String, String>>) data.response;
                if (repairList == null || repairList.size() <= 0) {
                    mSuperRefresh.onLoadComplete();
                    currPage--;
                    return;
                }
                Log.e("门店数量", repairList.size() + "");
                if (mAdapter.getList() == null || mAdapter.getList().size() <= 0) {
                    mAdapter.setList(repairList);
                } else {
                    mAdapter.addList(repairList);
                }
                //
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, MemberServiceActivity.this);
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


//    //获取广告列表
//    public void getGuangGaoList() {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("page", "1");
//        params.put("catid", "3");
//        new BaseDataPresenter(MemberServiceActivity.this).loadData(DataUrl.GET_HOME_GUANG_GAO_LIST, params, null, new IBaseDataListener() {
//            @Override
//            public void onSuccess(BaseDataBean data) {
//
//                ArrayList<Map<String, String>> mList = (ArrayList<Map<String, String>>) data.response;
//                if (mList == null || mList.size() <= 0) {
//                    mSuperRefresh.onLoadComplete();
//                    return;
//                }
//                mAdapter.setList(mList);
//                //
//                mLinearRoot.setVisibility(View.VISIBLE);
//                mSuperRefresh.onLoadComplete();
//            }
//
//            @Override
//            public void onFailure(BaseDataBean data) {
//                Global.showToast(data.msg, MemberServiceActivity.this);
//                mSuperRefresh.onLoadComplete();
//            }
//
//            @Override
//            public void onError() {
//                mSuperRefresh.onLoadComplete();
//            }
//        });
//    }

}

package com.coco3g.daishu.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;


import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.CarCategoryAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by lisen on 2017/5/3.
 */

public class CategoryListView extends RelativeLayout {
    Context mContext;
    View mView;
    public SuperRefreshLayout mSuperRefresh;
    private RecyclerView mRecyclerView;
    private CarCategoryAdapter mAdapter;

    boolean isLoading;
    //是否在刚刚加载更多后的一秒内
    private boolean inLoadMoreOneSecond = false;

    String order = "", price = "", catid = "";

    //数据加载的第几页
    int currPager = 1;
    //
    private boolean forceRefresh = false;  //是否是整体下拉强制刷新


    public CategoryListView(Context context) {
        super(context);
        mContext = context;
        mAdapter = new CarCategoryAdapter(mContext);
        initView();
    }

    public CategoryListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mAdapter = new CarCategoryAdapter(mContext);
        initView();
    }

    public CategoryListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView() {
        LayoutInflater lay = LayoutInflater.from(mContext);
        mView = lay.inflate(R.layout.view_car_category_listview, this);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.rv_demand_frag);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //
        mSuperRefresh = (SuperRefreshLayout) mView.findViewById(R.id.sr_home_listview);
        mSuperRefresh.setEnabled(true);
        mSuperRefresh.setCanLoadMore();
        mSuperRefresh.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                mAdapter.clearList();
                currPager = 1;
                getDemandList();

            }

            @Override
            public void onLoadMore() {
                currPager++;
                getDemandList();

            }
        });
        //

//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                Log.d("test", "StateChanged = " + newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                Log.d("test", "onScrolled");
//
//                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
//                if (lastVisibleItemPosition + 1 == mAdapter.getItemCount()) {
//                    Log.d("test", "loading executed");
//
//                    boolean isRefreshing = mSuperRefresh.isRefreshing();
//                    if (isRefreshing) {
//                        mAdapter.notifyItemRemoved(mAdapter.getItemCount());
//                        return;
//                    }
//                    if (!isLoading) {
//                        if (inLoadMoreOneSecond) {  //加载更多以后的2秒内不能再次加载更多
//                            return;
//                        }
//
//                        isLoading = true;
//                        mAdapter.startLoadMore();
//                        //
//                        currPager++;
//                        getDemandList();
//                        Log.e("加载更多", "加载更多");
//                    }
//                }
//            }
//        });


    }

    public String getOrder() {
        return order;
    }

    public void setForceRefresh(boolean forceRefresh) {
        this.forceRefresh = forceRefresh;
    }

    public boolean isForceRefresh() {
        return forceRefresh;
    }


    public void refreshData(String order, String price, String catid) {
        this.order = order;
//        this.price = price;
//        this.carid = carid;
        if (mAdapter.getList() == null || mAdapter.getList().size() <= 0) {
            mSuperRefresh.setEnabled(true);
            mSuperRefresh.setRefreshingLoad();
            //
            if (forceRefresh) {
                forceRefresh = false;
            }
        } else {  //有数据的时候，看看是否是强制刷新
            if (forceRefresh) {
                mSuperRefresh.setEnabled(true);
                mSuperRefresh.setRefreshingLoad();
                //
                forceRefresh = false;
            }
        }
    }

    public void forceRefreshData(String order, String price, String catid) {
        this.order = order;
        this.price = price;
        this.catid = catid;
        mSuperRefresh.setEnabled(true);
        mSuperRefresh.setRefreshingLoad();
    }

    //强制上一次刷新
    public void forceRefreshData() {
        mSuperRefresh.setEnabled(true);
        mSuperRefresh.setRefreshingLoad();
        //
        forceRefresh = false;
    }


    //获取需求列表
    public void getDemandList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", currPager + "");

        if (!TextUtils.isEmpty(order)) {
            params.put("order", order);
        }

        if (!TextUtils.isEmpty(price)) {
            params.put("price", price);
        }

        if (!TextUtils.isEmpty(catid)) {
            params.put("carid", catid);
        }
        Log.e("刷新数据类型", " order " + order + "    price " + price + "     carid " + catid);
        new BaseDataPresenter(mContext).loadData(DataUrl.GET_BANNER_IMAGE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, Object>> demandList = (ArrayList<Map<String, Object>>) data.response;
                if (demandList == null || demandList.size() <= 0) {
                    currPager--;
                    mSuperRefresh.onLoadComplete();
                    isLoading = false;
                    return;
                }
                if (mAdapter.getList() == null || mAdapter.getList().size() <= 0) {
                    mAdapter.setList(demandList);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.addList(demandList);
                }
                //
                mSuperRefresh.onLoadComplete();
                //
                isLoading = false;

            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, mContext);
                currPager--;
                mSuperRefresh.onLoadComplete();
                isLoading = false;
//                mSuperRefresh.setEnabled(false);
            }

            @Override
            public void onError() {
                currPager--;
                mSuperRefresh.onLoadComplete();
                isLoading = false;
//                mSuperRefresh.setEnabled(false);
            }
        });
    }



}

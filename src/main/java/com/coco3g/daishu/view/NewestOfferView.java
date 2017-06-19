package com.coco3g.daishu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.NewestOfferAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by coco3g on 17/5/17.
 */

public class NewestOfferView extends RelativeLayout {
    private Context mContext;
    private View mView;
    private ListView mListView;
    private SuperRefreshLayout mSuperRefresh;
    private NewestOfferAdapter mAdapter;
    //
    String carid = "";

    int currPage = 1;


    public NewestOfferView(Context context, String carid) {
        super(context);
        mContext = context;
        this.carid = carid;
        initView();
    }

    public NewestOfferView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public NewestOfferView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }


    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.view_newest_offer, this);
        mListView = (ListView) mView.findViewById(R.id.listview_newest_offer);
        mSuperRefresh = (SuperRefreshLayout) mView.findViewById(R.id.sr_newest_offer);
        mAdapter = new NewestOfferAdapter(mContext);
        mListView.setAdapter(mAdapter);
        //
        mSuperRefresh.setCanLoadMore();
        mSuperRefresh.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                mAdapter.clearList();
                currPage = 1;
                getNewestOffer();

            }

            @Override
            public void onLoadMore() {
                currPage++;
                getNewestOffer();

            }
        });
        //
        mSuperRefresh.setRefreshingLoad();

    }


    //获取跑马灯
    public void getNewestOffer() {
        HashMap<String, String> params = new HashMap<>();
        params.put("carid", carid);
        params.put("page", currPage + "");
        new BaseDataPresenter(mContext).loadData(DataUrl.GET_CAR_NEWEST_OFFER, params, null, new IBaseDataListener() {
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
                } else {
                    mAdapter.addList(list);
                }


                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, mContext);
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


}

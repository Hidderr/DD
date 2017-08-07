package com.coco3g.daishu.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.IncomeAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.Coco3gBroadcastUtils;
import com.coco3g.daishu.view.MyWebView;
import com.coco3g.daishu.view.SuperRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by jason on 2017/4/26.
 */

public class IncomeFragment extends Fragment {
    private View mIncomeView;
    private Context mContext;
    private ListView mListView;
    private IncomeAdapter mAdapter;
    private MyWebView myWebView;
    private SuperRefreshLayout mSuperRefresh;
    private Coco3gBroadcastUtils mCoco3gShouyi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mIncomeView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_income, null);
        intview();
        mCoco3gShouyi = new Coco3gBroadcastUtils(getActivity());
        mCoco3gShouyi.receiveBroadcast(Coco3gBroadcastUtils.SEND_SHOUYI_REFRESH_FLAG)
                .setOnReceivebroadcastListener(new Coco3gBroadcastUtils.OnReceiveBroadcastListener() {
                    @Override
                    public void receiveReturn(Intent intent) {
                        mAdapter.clearList();
                        getIncomeList();
                    }
                });
        return mIncomeView;
    }

    private void intview() {
        mListView = (ListView) mIncomeView.findViewById(R.id.listview_income_frag);
        mSuperRefresh = (SuperRefreshLayout) mIncomeView.findViewById(R.id.sr_income_frag);
//        mSuperRefreshWebview = (SuperRefreshLayout) mIncomeView.findViewById(R.id.sr_income_frag_webview);
        myWebView = (MyWebView) mIncomeView.findViewById(R.id.webview_income_fragment);
        mAdapter = new IncomeAdapter(mContext);
        mListView.setAdapter(mAdapter);
        myWebView.setRefreshEnable(true);
        //
        mSuperRefresh.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                mAdapter.clearList();
                getIncomeList();
            }

            @Override
            public void onLoadMore() {

            }
        });
        //
//        mSuperRefreshWebview.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
//            @Override
//            public void onRefreshing() {
//                mAdapter.clearList();
//                getIncomeList();
//            }
//
//            @Override
//            public void onLoadMore() {
//
//            }
//        });
        //
        mSuperRefresh.setRefreshingLoad();

    }


    @Override
    public void onResume() {
        super.onResume();

    }


    //收益列表
    public void getIncomeList() {

        HashMap<String, String> params = new HashMap<>();
        new BaseDataPresenter(getActivity()).loadData(DataUrl.GET_INCOME_LIST, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, String>> incomeList = (ArrayList<Map<String, String>>) data.response;
                mAdapter.setList(incomeList);
                //
                if (incomeList == null || incomeList.size() <= 0) {
                    mSuperRefresh.setVisibility(View.GONE);
                    myWebView.setVisibility(View.VISIBLE);
                    myWebView.loadUrl(Global.H5Map.get("jiangli"));
                } else {
                    mSuperRefresh.setVisibility(View.VISIBLE);
                    myWebView.setVisibility(View.GONE);
                }
                //
                mSuperRefresh.onLoadComplete();
//                mSuperRefreshWebview.onLoadComplete();
            }

            @Override
            public void onFailure(BaseDataBean data) {
//                Global.showToast(data.msg, mContext);
                mSuperRefresh.onLoadComplete();
//                mSuperRefreshWebview.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefresh.onLoadComplete();
//                mSuperRefreshWebview.onLoadComplete();

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCoco3gShouyi != null) {
            mCoco3gShouyi.unregisterBroadcast();
        }
    }
}

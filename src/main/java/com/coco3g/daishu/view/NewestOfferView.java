package com.coco3g.daishu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.CarDetailTypeActivity;
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


    public NewestOfferView(Context context) {
        super(context);
        mContext = context;
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
        mSuperRefresh.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {

            }

            @Override
            public void onLoadMore() {

            }
        });

    }

//    //获取跑马灯
//    private void getCarTypeDetail(String id) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("id", id);
//        new BaseDataPresenter(mContext).loadData(DataUrl.GET_CAR_TYPE_DETIL, params, null, new IBaseDataListener() {
//            @Override
//            public void onSuccess(BaseDataBean data) {
//                ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) data.response;
//                //
//                if (list == null || list.size() <= 0) {
//                    return;
//                }
////
//                mListView.setAdapter(mAdapter);
//                mAdapter.setList(list);
//                mSuperRefresh.onLoadComplete();
//            }
//
//            @Override
//            public void onFailure(BaseDataBean data) {
//                Global.showToast(data.msg, mContext);
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

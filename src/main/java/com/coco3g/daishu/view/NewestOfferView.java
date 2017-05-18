package com.coco3g.daishu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.NewestOfferAdapter;

/**
 * Created by coco3g on 17/5/17.
 */

public class NewestOfferView extends RelativeLayout {
    private Context mContext;
    private View mView;
    private ListView mListView;
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
        mAdapter = new NewestOfferAdapter(mContext);
        mListView.setAdapter(mAdapter);


    }


}

package com.coco3g.daishu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.coco3g.daishu.R;

/**
 * Created by coco3g on 17/5/17.
 */

public class CanShuDetailView extends RelativeLayout {
    private Context mContext;
    private View mView;
    private MyWebView myWebView;
    //
    public String url = "";


    public CanShuDetailView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public CanShuDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public CanShuDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.view_canshu_detail, this);
        myWebView = (MyWebView) mView.findViewById(R.id.webview_canshu_detail);
    }

    public void loadUrl(String url) {
        this.url = url;
        myWebView.loadUrl(url);
    }


}

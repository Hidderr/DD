package com.coco3g.daishu.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.view.MyWebView;


public class GoodsFragment1 extends Fragment {
    private View mReadView;
    private MyWebView myWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mReadView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_goods1, null);
        myWebView = (MyWebView) mReadView.findViewById(R.id.webview_gooods_fragment);
        //
        loadUrl();

        return mReadView;
    }

    public void loadUrl() {
        if (myWebView != null && TextUtils.isEmpty(myWebView.getCurrentUrl())) {
            String url = Global.H5Map.get("partner");
            if (!TextUtils.isEmpty(url) && !url.startsWith("#")) {
                myWebView.loadUrl(url);
            }
        }
    }


}

package com.coco3g.daishu.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.data.TypevauleGotoDictionary;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.net.utils.ShareAppUtils;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.coco3g.daishu.utils.ImageSelectUtils;
import com.coco3g.daishu.view.MyWebView;
import com.coco3g.daishu.view.TopBarView;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lisen on 16/3/3 16:34.
 */
public class WebActivity extends BaseActivity {
    TopBarView mTopBar;
    //    XRefreshView mXRefreshView;
    MyWebView mWebView;
    RelativeLayout relativeRoot;
    String mTitle = "";
    String action = "", id = "", url = "";
    boolean showRightView = true;
    //
    private ImageSelectUtils mImageSelectUtils;
    //
    TypevauleGotoDictionary typevauleGotoDictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mTitle = getIntent().getStringExtra("title");
        action = getIntent().getStringExtra("action");
        id = getIntent().getStringExtra("id");
        url = getIntent().getStringExtra("url");
        showRightView = getIntent().getBooleanExtra("showrightview", false);
        initView();
        if (!TextUtils.isEmpty(action)) {
            getUrl(action);
            return;
        }
        if (!TextUtils.isEmpty(url)) {
            if (url.startsWith("http://coco3g-app/")) {
                typevauleGotoDictionary = new TypevauleGotoDictionary(WebActivity.this);
                typevauleGotoDictionary.setWebview(mWebView.getCurrentWebview());
                typevauleGotoDictionary.setMyWebview(mWebView);
                typevauleGotoDictionary.gotoViewChoose(url);
                finish();
            } else {
                mWebView.loadUrl(url);
            }
            return;
        }
    }

    private void initView() {
        //
        mTopBar = (TopBarView) findViewById(R.id.topbar_my_webview);
        mTopBar.setTitle(mTitle);
        TextView tv = new TextView(WebActivity.this);
        tv.setGravity(Gravity.START | Gravity.CENTER);
        tv.setText(getString(R.string.save));
        tv.setTextSize(14f);
        int padding = Global.dipTopx(this, 10);
        tv.setPadding(padding, padding, padding, padding);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mWebView.save();
                setResult(Global.REFRESH_DATA);
//                finish();
            }
        });
        mTopBar.setRightView(tv);
        if (!showRightView) {
            mTopBar.hideRightView();
        }
        //
        relativeRoot = (RelativeLayout) findViewById(R.id.relative_webview_root);
        mWebView = (MyWebView) findViewById(R.id.view_my_webview);
        mWebView.setRootView(relativeRoot);
        mWebView.setOnTitleListener(new MyWebView.SetTitleListener() {
            @Override
            public void setTitle(String title1) {
                if (!TextUtils.isEmpty(title1)) {
                    return;
                } else if (!TextUtils.isEmpty(title1) && !title1.startsWith("qym.test.coco3g.com")) {
                    mTopBar.setTitle(title1);
                }
            }
        });
//        mWebView.setOnRefreshFinished(new MyWebView.OnRefreshFinished() {
//            @Override
//            public void refreshFinished() {
//                mXRefreshView.stopRefresh();
//            }
//        });
//        mXRefreshView = (XRefreshView) findViewById(R.id.xrefresh_webview);
//        mXRefreshView.setPullRefreshEnable(true);
//        mXRefreshView.setPullLoadEnable(false);
//        mXRefreshView.setPinnedTime(100);
//        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
//
//            @Override
//            public void onRefresh() {
//                mWebView.reLoadUrl();
//            }
//
//            @Override
//            public void onLoadMore(boolean isSlience) {
////                mWebView.reLoadUrl("javascript: pullup()");
//            }
//        });

        /*下面是上传图片的监听*/
        //压缩处理完成后的监听回调
        mImageSelectUtils = new ImageSelectUtils(this);
        mImageSelectUtils.setOnImageCompressFinishedListener(new ImageSelectUtils.OnImageCompressFinishedListener() {
            @Override
            public void imageCompressFinished(final ArrayList<String> list) {
                uploadImage(list.get(0));
            }
        });
        //
//        mCurrBoardCast = new Coco3gBroadcastUtils(this);
//        mCurrBoardCast.receiveBroadcast(Coco3gBroadcastUtils.RETURN_UPDATE_FLAG)
//                .setOnReceivebroadcastListener(new Coco3gBroadcastUtils.OnReceiveBroadcastListener() {
//                    @Override
//                    public void receiveReturn(Intent intent) {
////                        mScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//                        mXRefreshView.setPullRefreshEnable(true);
//                        mXRefreshView.setPullLoadEnable(false);
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
////                                mScrollView.setRefreshing();
//                                mXRefreshView.startRefresh();
//                            }
//                        }, 500);
//                    }
//                });
    }

    /**
     * 获取url并打开
     *
     * @param action
     */
    public void getUrl(String action) {
        HashMap<String, String> params = new HashMap<>();
        params.put("linkname", action);
        new BaseDataPresenter(this).loadData(DataUrl.GET_H5, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Map<String, String> urlMap = (Map<String, String>) data.response;
                String beginUrl = urlMap.get("url");
                Log.e("获取action网址", beginUrl);
                if (!TextUtils.isEmpty(beginUrl)) {
                    try {
                        if (beginUrl.startsWith("http://coco3g-app")) {
                            typevauleGotoDictionary = new TypevauleGotoDictionary(WebActivity.this);
                            typevauleGotoDictionary.setWebview(mWebView.getCurrentWebview());
                            typevauleGotoDictionary.setMyWebview(mWebView);
                            typevauleGotoDictionary.setOnWebConfigurationListener(new TypevauleGotoDictionary.OnWebConfigurationListener() {
                                @Override
                                public void configuration(String pullrefresh, ArrayList<Map<String, String>> topbarRight) {
                                    mWebView.setPullLoadEnable(pullrefresh);  //设置是否可以下拉
                                    setTopbarRightView(topbarRight);
                                }
                            });
                            typevauleGotoDictionary.gotoViewChoose(beginUrl);
                        } else {

                            beginUrl = URLDecoder.decode(beginUrl, "utf-8");
//                        int lastposition = beginUrl.lastIndexOf("?");
//                        String newurl = beginUrl.substring(lastposition + 1);
//                            Map<String, String> urlDecodeMap = Global.parseCustomUrl(beginUrl);
                            url = beginUrl;
                            mWebView.loadUrl(url);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(BaseDataBean data) {

            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {//返回头像照片
            if (data != null) {
                ArrayList<ImageItem> avatarImages = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                mImageSelectUtils.compressImage(avatarImages);
            }
        }
        if (null != ShareAppUtils.TENCENT) {
            ShareAppUtils.TENCENT.onActivityResultData(requestCode, resultCode, data, ShareAppUtils.qqShareListener);
        }
    }

    //上传图片ath
    public void uploadImage(final String imagePath) {
        HashMap<String, String> params = new HashMap<>();
        params.put("uptype", "1");
        new BaseDataPresenter(this).uploadFiles(DataUrl.UPLOAD_IMAGES, params, imagePath, "上传", new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Map<String, String> ImagePathMap = (Map<String, String>) data.response;
                String imageBackPath = ImagePathMap.get("fileurl");
                String js = "javascript:c3_navtive_user.callback('" + TypevauleGotoDictionary.CALLBACKTAG.get("callbackTag") + "','" + imageBackPath + "');";
                mWebView.getCurrentWebview().loadUrl(js);
            }

            @Override
            public void onFailure(BaseDataBean data) {

            }

            @Override
            public void onError() {

            }
        });
    }


    //配置topbar右上角的view
    public void setTopbarRightView(ArrayList<Map<String, String>> topbarRightList) {
        if (topbarRightList == null) {
            return;
        }
        for (int i = 0; i < topbarRightList.size(); i++) {
            Map<String, String> oneView = topbarRightList.get(i);
            String title = oneView.get("title");
            final String url = oneView.get("url");
            if (title.startsWith("http://") || title.startsWith("https://")) {
                ImageView imageView = new ImageView(this);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(WebActivity.this, WebActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });
                ImageLoader.getInstance().displayImage(title, imageView, new DisplayImageOptionsUtils().init());
                mTopBar.setRightView(imageView);

            } else {

                TextView rightView = new TextView(this);
                rightView.setText(title);
                rightView.setTextSize(14);
                rightView.setTextColor(Color.WHITE);
                rightView.setPadding(0, Global.dipTopx(this, 3f), Global.dipTopx(this, 10f), Global.dipTopx(this, 3f));
                rightView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(WebActivity.this, WebActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });
                mTopBar.setRightView(rightView);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mCurrBoardCast.unregisterBroadcast();
        mWebView.unRegisterBroadcast();
        if (typevauleGotoDictionary != null) {
            typevauleGotoDictionary.unregister();
        }
    }
}
package com.coco3g.daishu.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.andview.refreshview.XRefreshView;
import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.data.TypevauleGotoDictionary;
import com.coco3g.daishu.utils.Coco3gBroadcastUtils;
import com.coco3g.daishu.utils.JsCallMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by lisen on 16/2/29 16:16.
 */
public class MyWebView extends RelativeLayout {
    Context mContext;
    View mView;
    WebView webView, mForbidRefreshWebview;
    XRefreshView mXRefreshView;
    String mUrl = "";
    String mUserID = "";
    //    MyProgressDialog mProgress;
    SetTitleListener settitlelistener;
    OnRefreshFinished onRefreshFinished;
    private ProgressBar mProgressBar;
    //
    boolean hideTopbar = false;  //是否隐藏topbar
    RelativeLayout mRelativeRoot;
    //
    Coco3gBroadcastUtils mCurrBoardCast;
    ConfigTopBarMenu configtopbarmenu;
    public TypevauleGotoDictionary typevauleGotoDictionary;

    private String coco3g_url = "";//协议Url,判断的时候用的到

    public MyWebView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater lay = LayoutInflater.from(mContext);
        mView = lay.inflate(R.layout.view_webview, this);
        webView = (WebView) mView.findViewById(R.id.view_webview);
        mForbidRefreshWebview = (WebView) mView.findViewById(R.id.view_webview_forbid_refresh);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_webview);
        mProgressBar.setMax(100);
        //
        mXRefreshView = (XRefreshView) findViewById(R.id.xrefresh_webview_view);
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(false);
        mXRefreshView.setPinnedTime(100);
        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh() {
                webView.reload();
            }

            @Override
            public void onLoadMore(boolean isSlience) {
//                mWebView.reLoadUrl("javascript: pullup()");
            }
        });
        //
        mCurrBoardCast = new Coco3gBroadcastUtils(mContext);
        mCurrBoardCast.receiveBroadcast(Coco3gBroadcastUtils.RETURN_UPDATE_FLAG)
                .setOnReceivebroadcastListener(new Coco3gBroadcastUtils.OnReceiveBroadcastListener() {
                    @Override
                    public void receiveReturn(Intent intent) {
//                        mScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        mXRefreshView.setPullRefreshEnable(true);
                        mXRefreshView.setPullLoadEnable(false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                mScrollView.setRefreshing();
                                mXRefreshView.startRefresh();
                            }
                        }, 500);
                    }
                });
    }

    public void setRootView(RelativeLayout mRelativeRoot) {
        this.mRelativeRoot = mRelativeRoot;
    }


    public void loadUrl(String url) {
        this.mUrl = url;
        //
//        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setTitle(title);    //目前先禁用获取自动获取H5页的标题，现在是自己传递过来的
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(GONE);
                } else {
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
//        webView.setWebChromeClient(new WebChromeClient());

        webView.addJavascriptInterface(new getHtmlObject(), "CocoObj");
        //
        webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString() + ";Coco3gAppAndroid");
        //
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // 设置缓存模式
        // 开启DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);
        // 开启database storage API功能
        webView.getSettings().setDatabaseEnabled(true);
        String cacheDirPath = mContext.getFilesDir().getAbsolutePath() + "webview";
        // 设置数据库缓存路径
        webView.getSettings().setDatabasePath(cacheDirPath); // API 19 deprecated
        // 设置Application caches缓存目录
        webView.getSettings().setAppCachePath(cacheDirPath);
        // 开启Application Cache功能
        webView.getSettings().setAppCacheEnabled(true);
        //
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                Log.e("coco3g协议", url);

                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mContext.startActivity(intent);
                    return true;
                }
                if (!TextUtils.isEmpty(url) && url.startsWith("http://coco3g-app")) {
                    typevauleGotoDictionary = new TypevauleGotoDictionary(mContext);
                    typevauleGotoDictionary.setWebview(webView);
                    typevauleGotoDictionary.setRootView(mRelativeRoot);
                    typevauleGotoDictionary.gotoViewChoose(url);
                    return true;
                } else if (url.contains("http://www.zhixunchelian.com/bxtx/index.do?")) {
                    webView.loadUrl(url);

                } else if (url.contains("http://open.iauto360.cn")) {  //车保姆
                    if (!TextUtils.isEmpty(url) && url.startsWith("http://open.iauto360.cn/html/main.html?")) {  //排除跳转两次
                        webView.loadUrl(url);
                    } else {
                        mUrl = url;
                        Intent intent = new Intent(mContext, WebActivity.class);
                        intent.putExtra("url", url);
                        intent.putExtra("hidetopbar", hideTopbar);
                        mContext.startActivity(intent);
//                    ((Activity) mContext).finish();
                        return true;
                    }
                } else {
                    mUrl = url;
                    Intent intent = new Intent(mContext, WebActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("hidetopbar", hideTopbar);
                    mContext.startActivity(intent);
//                    ((Activity) mContext).finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                mProgress = MyProgressDialog.show(mContext, mContext.getString(R.string.jia_zai_zhong), false, true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);

                mXRefreshView.stopRefresh();

                if (onRefreshFinished != null) {
                    onRefreshFinished.refreshFinished();
                }

//                if (!mUrl.contains("pull_down")) {
//                    mXRefreshView.setPullLoadEnable(false);
//                    mXRefreshView.setPullRefreshEnable(false);
//                }
//                if (mProgress != null) {
//                    try {
//                        mProgress.cancel();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
            }


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // handler.cancel(); // Android默认的处理方式
                handler.proceed(); // 接受所有网站的证书
                // handleMessage(Message msg); // 进行其他处理
            }

        });
        webView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
        if (Global.USERINFOMAP != null && !TextUtils.isEmpty(Global.USERINFOMAP.get("id") + "")) {
            CookieSyncManager.createInstance(mContext);
            CookieManager cookieManager = CookieManager.getInstance();
            String cookie = cookieManager.getCookie("cookie");
//            cookieManager.setAcceptCookie(true);
//            cookieManager.removeSessionCookie();// 移除
            cookieManager.setCookie(mUrl, cookie);// cookies是在HttpClient中获得的cookie
            CookieSyncManager.getInstance().sync();
        }
        //
        webView.loadUrl(mUrl, Global.getTokenTimeStampHeader(mContext));
    }


    public void reLoadUrl() {
        webView.reload();
    }


    public void execJsUrl(String jsStr) {
        webView.loadUrl(jsStr);
    }
//    public void reLoadUrl(String url) {
//        webView.loadUrl(url);
//    }

    public void goBack() {
        webView.goBack();
    }

    public boolean canBack() {
        return webView.canGoBack();
    }

    public WebView getCurrentWebview() {
        return webView;
    }

    public String getCurrentUrl() {
        return mUrl;
    }

    public void setHideTopbar(boolean hideTopbar) {
        this.hideTopbar = hideTopbar;
    }

    //禁止下拉
    public void forbidRefresh(boolean forbidRefresh) {
        if (forbidRefresh) {
            mXRefreshView.setVisibility(GONE);
            mForbidRefreshWebview.setVisibility(VISIBLE);
            webView = mForbidRefreshWebview;
        } else {

        }
    }

    public void setRefreshEnable(boolean canRefresh) {
        if (canRefresh) {
            mXRefreshView.setEnabled(true);
        } else {
            mXRefreshView.setEnabled(false);
        }

    }


    public class getHtmlObject {
        @JavascriptInterface
        public void AppAction(String action, String json, String callback) {
            try {
                Class classes = Class.forName("com.coco3g.daishu.utils.JsCallMethod");
                Method method = classes.getDeclaredMethod(action, String.class, String.class);
                JsCallMethod jsCallMethod = new JsCallMethod();
                jsCallMethod.setOnJsReturnDataListener(onJsReturnInterface);
                method.invoke(jsCallMethod, json, callback);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

    }

    public void save() {
//        webView.loadUrl("javascript:edit_info()");
        webView.loadUrl("javascript:$('#dosubmit').click();");
//        webView.destroy();
    }

//    public void update() {
//        webView.reload();
//    }

    public void setPullLoadEnable(String pullrefresh) {
        if (!TextUtils.isEmpty(pullrefresh)) {
            if (pullrefresh.equals("1")) {
                mXRefreshView.setPullRefreshEnable(true);
            } else {
                mXRefreshView.setPullRefreshEnable(false);
            }
        }
    }


    public void setOnTitleListener(SetTitleListener settitlelistener) {
        this.settitlelistener = settitlelistener;
    }

    public interface SetTitleListener {
        void setTitle(String title);
    }

    private void setTitle(String title) {
        Log.e("标题", title);
        if (settitlelistener != null && !title.endsWith(".html")) {
            settitlelistener.setTitle(title);
        }
    }

    public interface OnRefreshFinished {
        void refreshFinished();
    }

    public void setOnRefreshFinished(OnRefreshFinished onRefreshFinished) {
        this.onRefreshFinished = onRefreshFinished;
    }


    public void unRegisterBroadcast() {
        if (mCurrBoardCast != null) {
            mCurrBoardCast.unregisterBroadcast();
        }
    }

    public void setOnConfigMenuListener(ConfigTopBarMenu configtopbarmenu) {
        this.configtopbarmenu = configtopbarmenu;
    }

    public interface ConfigTopBarMenu {
        void configmenu(String json, String callback);
    }

    private void configTopMenu(String json, String callback) {
        if (configtopbarmenu != null) {
            configtopbarmenu.configmenu(json, callback);
        }
    }

    JsCallMethod.OnJsReturnInterface onJsReturnInterface = new JsCallMethod.OnJsReturnInterface() {
        @Override
        public void returnData(final String json, final String callback) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    configTopMenu(json, callback);
                }
            });

        }
    };
}

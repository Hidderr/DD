package com.coco3g.daishu.data;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.webkit.WebView;
import android.widget.DatePicker;
import android.widget.RelativeLayout;

import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.BrowseImageActivity;
import com.coco3g.daishu.activity.DiscountOilActivity;
import com.coco3g.daishu.activity.DriveRouteActivity;
import com.coco3g.daishu.activity.LoginActivity;
import com.coco3g.daishu.activity.MainActivity;
import com.coco3g.daishu.activity.MemberServiceActivity;
import com.coco3g.daishu.activity.ShaiXuanListActivity;
import com.coco3g.daishu.activity.TabViewWebActivity;
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.alipay.AliPayUtils;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.bean.LocationBean;
import com.coco3g.daishu.bean.RepairStoreBean;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.net.utils.RongUtils;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.Coco3gBroadcastUtils;
import com.coco3g.daishu.utils.DateTime;
import com.coco3g.daishu.utils.LocationUtil;
import com.coco3g.daishu.utils.OpenWXPayUtils;
import com.coco3g.daishu.utils.RequestPermissionUtils;
import com.coco3g.daishu.view.EditTextItemView;
import com.coco3g.daishu.view.MyProgressDialog;
import com.coco3g.daishu.view.MyWebView;
import com.coco3g.daishu.view.SharePopupWindow;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.view.CropImageView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by lisen on 16/3/29.
 */
public class TypevauleGotoDictionary {
    Context mContext;
    private String indexNativeKey = "http://coco3g-app/";
    private String indexNativeKey_gotopage = indexNativeKey + "gotopage?";   //H5跳转
    //
    private final String CLOSE_WINDOW = indexNativeKey + "close_window?";  //关闭当前页面
    private final String REFRESH_WINDOW = indexNativeKey + "refresh_window?";  //刷新当前页面
    private final String CLOSE_OPEN_NEW_WINDOW = indexNativeKey + "close_open_new_window?";  //关闭当前界面，打开指定界面(newtag代表指定界面的标识)
    private final String OPEN_NEW_WINDOW = indexNativeKey + "open_new_window?";  //打开指定界面(newtag代表指定界面的标识)
    private final String SHARE = indexNativeKey + "share?";  //分享
    private final String OPEN_GALLERY = indexNativeKey + "open_gallery?";  //打开本地相册并上传图片
    private final String LOGOUT = indexNativeKey + "logout?";  //注销登录
    private final String GET_LOCATION = indexNativeKey + "getlocation?";  //定位并获取经纬度
    private final String LOAD_ALBUM = indexNativeKey + "load_album?";  //浏览H5页面上的图片
    private final String OPEN_LOADING = indexNativeKey + "open_loading?";  //打开loading进度条
    private final String CLOSE_LOADING = indexNativeKey + "close_loading?";  //关闭loading进度条
    private final String OPEN_ALERT = indexNativeKey + "open_alert?";  //打开浮动提示框(可自动消失)
    private final String OPEN_CONFIRM_ALERT = indexNativeKey + "confirm?";  //打开确认提示框
    private final String OPEN_ALERT_DIALOG = indexNativeKey + "open_alert_dialog?";  //在当前界面打开提示对话框
    private final String OPEN_CONFIRM_DIALOG = indexNativeKey + "open_confirm_dialog?";  //在当前界面打开确认对话框
    private final String PULL_REFRESH_WINDOW = indexNativeKey + "pull_refresh_window?"; // 下拉刷新当前页
    private final String CALL_PAY = indexNativeKey + "payonline?";
    private final String CALL_EDIT_DIALOG = indexNativeKey + "prompt?";
    private final String OPEN_TAB_VIEW = indexNativeKey + "open_tabview?";
    private final String GET_URL = indexNativeKey + "call_interface?";  //调用接口获取url
    private final String FORBID_REFRESH = indexNativeKey + "ban_refresh?";  //禁止刷新
    private final String START_CHAT = indexNativeKey + "start_chat?";   //打开聊天

    //
    public static HashMap<String, String> CALLBACKTAG = new HashMap<>();
    private MyProgressDialog myProgressDialog;
    //
    SharePopupWindow mSharePopupWindow;
    public Coco3gBroadcastUtils mWXShareSuccessBroadcast;  //微信分享成功监听
    OnWebConfigurationListener onWebConfigurationListener;  //配置是否下拉刷新和topbar右上角是否有视图
    //
    WebView mWebview;
    MyWebView myWebView = null;
    RelativeLayout mRelativeRoot;
    Calendar calendar = Calendar.getInstance(Locale.CHINA);
    //

    public TypevauleGotoDictionary(Context context) {
        mContext = context;
        mWXShareSuccessBroadcast = new Coco3gBroadcastUtils(mContext);
        mWXShareSuccessBroadcast.receiveBroadcast(Coco3gBroadcastUtils.SHARE_SUCCESS).setOnReceivebroadcastListener(new Coco3gBroadcastUtils.OnReceiveBroadcastListener() {
            @Override
            public void receiveReturn(Intent intent) {     //typeid  1：qq   2:微信    3：朋友圈  4：微博
                if (mSharePopupWindow != null) {
                    String shareType = mSharePopupWindow.getmShareType() + "";
                    String js = "javascript:c3_navtive_user.callback('" + TypevauleGotoDictionary.CALLBACKTAG.get("callbackTag") + "','" + shareType + "');";
                    mWebview.loadUrl(js);
                    //
                    mSharePopupWindow.dismiss();
                    mSharePopupWindow = null;
                }
            }
        });
    }

    public void setWebview(WebView webview) {
        this.mWebview = webview;
    }

    public void setMyWebview(MyWebView myWebview) {
        this.myWebView = myWebview;
    }

    public void setRootView(RelativeLayout relativeLayout) {
        this.mRelativeRoot = relativeLayout;
    }

    public void gotoViewChoose(String value) {
        Intent intent = null;
        try {
            value = URLDecoder.decode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final HashMap<String, String> hashMap = Global.parseCustomUrl(value);
        CALLBACKTAG = hashMap;
        if (value.startsWith(indexNativeKey_gotopage)) {   /***h5页面跳转***/
            Map<String, String> urlDecodeMap = Global.parseCustomUrl(value);
            Log.e("是否在当前页面打开", urlDecodeMap.get("target") + " 到底那一页");
            if (urlDecodeMap.get("target").equals("self")) {  //当前webview加载url,不重新跳转到新的页面
                //myWebview是从WebActivity传递过来的，mWebView是从MyWebview传递过来的
                if (myWebView != null && TextUtils.isEmpty(myWebView.getCurrentUrl())) {
                    myWebView.loadUrl(urlDecodeMap.get("url"));
                    Log.e("gotopager加载的网址111", "MyWebview" + urlDecodeMap.get("url"));
                } else {
                    mWebview.loadUrl(urlDecodeMap.get("url"), Global.getTokenTimeStampHeader(mContext));
                    Log.e("gotopager加载的网址222", "mWebview" + urlDecodeMap.get("url"));
                }

            } else if (urlDecodeMap.get("target").equals("blank")) {   //跳转到新的页面
                intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("url", urlDecodeMap.get("url"));
                ((Activity) mContext).startActivityForResult(intent, Global.REFRESH_DATA);
            } else {  //当没有传递"self"时候，默认当前页面打开
                if (myWebView != null && TextUtils.isEmpty(myWebView.getCurrentUrl())) {
                    myWebView.loadUrl(urlDecodeMap.get("url"));
                } else {
                    mWebview.loadUrl(urlDecodeMap.get("url"), Global.getTokenTimeStampHeader(mContext));
                }
            }


        } else if (value.startsWith(OPEN_GALLERY)) {  /***打开相册，上传图片***/
            new RequestPermissionUtils(mContext).aleraPermission(Manifest.permission.CAMERA, 1);  //首先检查权限
            openGallery(hashMap.get("uptype"));
            CALLBACKTAG = Global.parseCustomUrl(value);


        } else if (value.startsWith(SHARE)) {  /***分享***/
            if (mSharePopupWindow == null) {
                mSharePopupWindow = new SharePopupWindow(mContext, hashMap);
            }
            mSharePopupWindow.showAtLocation(mRelativeRoot, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        } else if (value.startsWith(CLOSE_WINDOW)) {  /***关闭当前页面***/
            ((Activity) mContext).finish();


        } else if (value.startsWith(REFRESH_WINDOW)) {  /***刷新当前页面***/
            mWebview.reload();

        } else if (value.startsWith(CLOSE_OPEN_NEW_WINDOW)) {  /***关闭当前界面，打开指定界面(newtag代表指定界面的标识)***/
            ((Activity) mContext).finish();
            try {
                String tag = hashMap.get("newtag");
                if ("login".equalsIgnoreCase(tag)) {
                    Intent intent_login = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(intent_login);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (value.startsWith(OPEN_NEW_WINDOW)) {  /***git push -u origin master打开指定界面(newtag代表指定界面的标识)***/
//            Global.showToast("打开指定界面还未做好", mContext);


            if (hashMap.get("newtag").equals("navigation")) {  //维修点导航
                String id = hashMap.get("data");
                String store_id = new String(Base64.decode(id, Base64.DEFAULT));
                Gson gson = new Gson();
                StoreId storeId = gson.fromJson(store_id, StoreId.class);
                Log.e("解码", storeId.id);
                getStoreDetail(storeId.id);

            } else if (hashMap.get("newtag").equals("home")) {
                ((Activity) mContext).finish();
                new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.LOG_OUT, null);
            } else if (hashMap.get("newtag").equals("login")) {
                intent = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(intent);
            } else if (hashMap.get("newtag").equals("disyouika")) {
                intent = new Intent(mContext, DiscountOilActivity.class);
                mContext.startActivity(intent);
                ((Activity) mContext).finish();
            } else if (hashMap.get("newtag").equals("dis_wash_car")) {  //跳转到优惠洗车
                if (!Global.checkoutLogin(mContext)) {
                    return;
                }
                intent = new Intent(mContext, ShaiXuanListActivity.class);
                intent.putExtra("typeid", "2");   //2=洗车店，1=维修养护和维修救援，附近门店(不传参)，汽修厂、爱车保姆快修店（根据获取的维修类型id）
                intent.putExtra("title", "优惠洗车");
                mContext.startActivity(intent);
            }


        } else if (value.startsWith(LOGOUT)) {  /***注销登录***/
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            builder.setMessage("确认注销吗？");
            builder.setTitle("提示");
            builder.setPositiveButton(mContext.getString(R.string.confirm), new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.LOG_OUT, null);
                    Global.logout(mContext);
                    ((Activity) mContext).finish();
                    //
                    mWebview.loadUrl("javascript:c3_navtive_user.callback('" + hashMap.get("callbackTag") + "')");
                }

            });
            builder.setNegativeButton("取消", null);
            builder.create().show();


        } else if (value.startsWith(PULL_REFRESH_WINDOW)) {
            new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.RETURN_UPDATE_FLAG, null);
        } else if (value.startsWith(GET_LOCATION)) {  /***定位并获取经纬度***/
            Global.showToast("获取经纬度还未做好", mContext);
            new LocationUtil(mContext).initLocationAndStart(true, 10000, false, "0").setLocationComplete(new LocationUtil.LocationComplete() {
                @Override
                public void locationcomplete(String address, String mCurrLat, String mCurrLng) {
                    LocationBean locationBean = new LocationBean();
                    locationBean.mCurrLat = mCurrLat;
                    locationBean.mCurrLng = mCurrLng;
                    String json = new Gson().toJson(locationBean);
                    //Log.e("tag", "javascript:c3_navtive_user.callback('" + hm.get("callbackTag") + "','" + json + "');");
                    mWebview.loadUrl("javascript:c3_navtive_user.callback('" + hashMap.get("callbackTag") + "','" + json + "');");
                }
            });


        } else if (value.startsWith(LOAD_ALBUM)) {  /***浏览H5页面上的图片***/
            String images = hashMap.get("pic");
            if (TextUtils.isEmpty(images)) {
                return;
            }
            String[] pictures = images.split(",");
            ArrayList<String> imageList = new ArrayList<>();
            for (int i = 0; i < pictures.length; i++) {
                imageList.add(pictures[i]);
            }
            //点击的哪张图片
            int position = Integer.parseInt(hashMap.get("index"));
            intent = new Intent(mContext, BrowseImageActivity.class);
            intent.putExtra("image_list", imageList);
            intent.putExtra("pagenum", position - 1);
            mContext.startActivity(intent);

        } else if (value.startsWith(OPEN_LOADING)) {  /***打开loading进度条***/
            String title = hashMap.get("title");
            if (!TextUtils.isEmpty(title)) {
                showProgressDia(hashMap.get(title));
            }

        } else if (value.startsWith(CLOSE_LOADING)) {  /***关闭loading进度条***/
            closeProgressDia();

        } else if (value.startsWith(OPEN_CONFIRM_ALERT)) {  /***打开确认提示框***/
            openAlertDialog(hashMap.get("title"), hashMap.get("message"), hashMap.get("callbackTag"), true);

        } else if (value.startsWith(OPEN_ALERT)) {  /***打开浮动提示框(可自动消失)***/
            Global.showToast(hashMap.get("message"), mContext);

        } else if (value.startsWith(OPEN_ALERT_DIALOG)) {  /***在当前界面打开提示对话框***/
            openAlertDialog(hashMap.get("title"), hashMap.get("message"), hashMap.get("callbackTag"), false);

        } else if (value.startsWith(OPEN_CONFIRM_DIALOG)) {  /***在当前界面打开确认对话框***/
            openAlertDialog(hashMap.get("title"), hashMap.get("message"), hashMap.get("callbackTag"), true);
        } else if (value.startsWith(CALL_PAY)) {
//            getOrderDetail(hashMap.get("orderid"));
//            try {
//                new AliPayUtils(mContext).payV2(hashMap.get("orderid"), hashMap.get("goodsname"), hashMap.get("goodsdetail"), Float.parseFloat(hashMap.get("price")));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            try {
                String type = hashMap.get("type");
                if (type.equalsIgnoreCase("alipay")) {
                    new AliPayUtils(mContext).payV2(hashMap.get("orderid"), hashMap.get("goodsname"), hashMap.get("goodsdetail"),
                            Float.parseFloat(hashMap.get("price")));
                } else if (type.equalsIgnoreCase("weixin")) {
                    new OpenWXPayUtils(mContext, hashMap.get("orderid"), hashMap.get("goodsname"), hashMap.get("goodsdetail"),
                            Float.parseFloat(hashMap.get("price"))).setOnPayCompleteListener(new OpenWXPayUtils.OnPayCompleteListener() {
                        @Override
                        public void payComplete(int paystate) {
                            ((Activity) mContext).finish();
                        }
                    }).pay();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (value.startsWith(CALL_EDIT_DIALOG)) {
            callEditDialog(hashMap);
//            try {
//                new AliPayUtils(mContext).payV2(hashMap.get("orderid"), hashMap.get("goodsname"), hashMap.get("goodsdetail"), Float.parseFloat(hashMap.get("price")));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } else if (value.startsWith(OPEN_TAB_VIEW)) {
            String title = hashMap.get("title");
            String content = hashMap.get("content");
            if (!TextUtils.isEmpty(content)) {
                Gson gson = new Gson();
                ArrayList<HashMap<String, String>> listdata = gson.fromJson(content, ArrayList.class);
                intent = new Intent(mContext, TabViewWebActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("data", listdata);
                mContext.startActivity(intent);
            }
        } else if (value.startsWith(GET_URL)) {
            getUrl(hashMap.get("url"), hashMap.get("target"));

        } else if (value.startsWith(FORBID_REFRESH)) {  //禁止下拉刷新

            if (hashMap.get("target").equals("self")) {  //当前webview加载url,不重新跳转到新的页面
                if (myWebView != null && TextUtils.isEmpty(myWebView.getCurrentUrl())) {
                    myWebView.forbidRefresh(true);
                    myWebView.loadUrl(hashMap.get("url"));
                } else {
                    mWebview.loadUrl(hashMap.get("url"), Global.getTokenTimeStampHeader(mContext));
                }
            } else if (hashMap.get("target").equals("blank")) {   //跳转到新的页面
                intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("url", hashMap.get("url"));
                intent.putExtra("pulldown", true);
                ((Activity) mContext).startActivityForResult(intent, Global.REFRESH_DATA);
            } else {  //当没有传递"self"时候，默认当前页面打开
                if (myWebView != null && TextUtils.isEmpty(myWebView.getCurrentUrl())) {
                    myWebView.forbidRefresh(true);
                    myWebView.loadUrl(hashMap.get("url"));
                } else {
                    mWebview.loadUrl(hashMap.get("url"), Global.getTokenTimeStampHeader(mContext));
                }
            }
        } else if (value.startsWith(START_CHAT)) {
            new RongUtils(mContext).startConversation(hashMap.get("nickname"), hashMap.get("touserid"));
        }

    }


    //打开进度条
    private void showProgressDia(String title) {
        //显示ProgressDialog
        myProgressDialog = MyProgressDialog.show(mContext, title, false, true);
    }

    //关闭进度条
    private void closeProgressDia() {
        try {
            if (myProgressDialog != null || myProgressDialog.isShowing()) {
                myProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // //打开相册
    public void openGallery(String uptype) {
        ImagePicker.getInstance().destory();
        if (uptype.equals("8")) {  //二手车上传图片
            ImagePicker.getInstance()
                    .setShowCamera(true)
                    .setMultiMode(false)
                    .setCrop(false).start((Activity) mContext);
        } else if (uptype.equals("1")) {   //上传头像
            int cropWidth = Global.dipTopx(mContext, 150f);
            ImagePicker.getInstance()
                    .setOutPutX(cropWidth)
                    .setOutPutY(cropWidth)
                    .setFocusWidth(cropWidth)
                    .setFocusHeight(cropWidth)
                    .setShowCamera(true)
                    .setMultiMode(false)
                    .setCrop(true)
                    .setStyle(CropImageView.Style.CIRCLE);
            ImagePicker.getInstance().start((Activity) mContext);
        } else if (uptype.equals("7")) {  //实名认证
            int cropWidth = Global.screenWidth * 3 / 4;
            int cropHeight = cropWidth * 2 / 3;
            ImagePicker.getInstance().setSelectLimit(4)
                    .setOutPutX(cropWidth)
                    .setOutPutY(cropHeight)
                    .setFocusWidth(cropWidth)
                    .setFocusHeight(cropHeight)
                    .setShowCamera(true)
                    .setMultiMode(false)
                    .setCrop(true)
                    .start((Activity) mContext);
        }

    }

    //提示对话框
    public void openAlertDialog(String title, String message, final String callBackTag, boolean hasCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String js = "javascript:c3_navtive_user.callback('" + callBackTag + "');";
                mWebview.loadUrl(js);
            }
        });
        if (hasCancel) {
            builder.setNegativeButton("取消", null);
        }
        builder.create().show();
    }


    /**
     * 打开对话框
     *
     * @param hashmap
     */
    private void callEditDialog(HashMap<String, String> hashmap) {
        String defaultValue = hashmap.get("defaultValue");
        String format = hashmap.get("format");
        if ("radio".equalsIgnoreCase(format)) { // 单选
            String primaryValues = hashmap.get("primaryValues");
            if (!TextUtils.isEmpty(primaryValues)) {
                openRadioDialog(defaultValue, primaryValues, hashmap);
            }
        } else if ("text".equalsIgnoreCase(format)) {
            String type = hashmap.get("type");
            if (TextUtils.isEmpty(type)) {
                openTextDialog(defaultValue, hashmap);
            } else {
                openDateTimeDialog(defaultValue, type, hashmap);
            }
        }
    }

    /**
     * 打开下拉列表单选框
     *
     * @param primaryValues
     * @param hashmap
     */
    private void openRadioDialog(String defaultid, String primaryValues, final HashMap<String, String> hashmap) {
        Gson gson = new Gson();
        ArrayList<Map> list = gson.fromJson(primaryValues, ArrayList.class);
        if (list != null && list.size() > 0) {
            String[] names = new String[list.size()];
            final String[] ids = new String[list.size()];
            int defaultcheckindex = 0;
            for (int i = 0; i < list.size(); i++) {
                LinkedTreeMap<String, String> temphm = (LinkedTreeMap<String, String>) list.get(i);
                names[i] = temphm.get("name");
                ids[i] = temphm.get("id") + "";
                if (defaultid.equalsIgnoreCase(ids[i])) {
                    defaultcheckindex = i;
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            builder.setTitle("单选").setSingleChoiceItems(names, defaultcheckindex, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String js = "javascript:c3_navtive_user.callback('" + hashmap.get("callbackTag") + "','" + ids[which] + "');";
                    mWebview.loadUrl(js);
                    dialog.dismiss();
                    new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.RETURN_UPDATE_FLAG, null);
                }
            });
            builder.setCancelable(true);
            builder.create().show();
        }
    }

    /**
     * 打开可编辑内容对话框
     *
     * @param defaultvalue
     * @param hashmap
     */
    private void openTextDialog(String defaultvalue, final HashMap<String, String> hashmap) {
        final EditTextItemView edit = new EditTextItemView(mContext, null);
        edit.setHintText(defaultvalue);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle("信息").setView(edit).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = edit.getText();
                String js = "javascript:c3_navtive_user.callback('" + hashmap.get("callbackTag") + "','" + content + "');";
                mWebview.loadUrl(js);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setCancelable(true);
        builder.create().show();
    }

    /**
     * 打开日期选择对话框
     *
     * @param defaultvalue
     * @param timetype
     * @param hashmap
     */
    private void openDateTimeDialog(String defaultvalue, final String timetype, final HashMap<String, String> hashmap) {
        int year = 2000;
        int month = 01;
        int day = 01;
        try {
            if (!TextUtils.isEmpty(defaultvalue)) {
                String date[] = defaultvalue.split("-");
                year = Integer.parseInt(date[0]);
                month = Integer.parseInt(date[1]);
                day = Integer.parseInt(date[2]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new DatePickerDialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //
                String newDatetime = DateTime.getDateFormated(timetype, calendar.getTimeInMillis());
                String js = "javascript:c3_navtive_user.callback('" + hashmap.get("callbackTag") + "','" + newDatetime + "');";
                mWebview.loadUrl(js);
            }
        }, year, month - 1, day).show();
    }


    /**
     * 获取url并打开
     */
    public void getUrl(String url, final String target) {
        HashMap<String, String> params = new HashMap<>();
        new BaseDataPresenter(mContext).loadData(url, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Map<String, String> map = (Map<String, String>) data.response;
                Intent intent = null;
                if (target.equals("self")) {  //当前webview加载url,不重新跳转到新的页面
                    //myWebview是从WebActivity传递过来的，mWebView是从MyWebview传递过来的
                    if (myWebView != null && TextUtils.isEmpty(myWebView.getCurrentUrl())) {
                        myWebView.loadUrl(map.get("url"));
                        Log.e("gotopager加载的网址111", "MyWebview" + map.get("url"));
                    } else {
                        mWebview.loadUrl(map.get("url"), Global.getTokenTimeStampHeader(mContext));
                        Log.e("gotopager加载的网址222", "mWebview" + map.get("url"));
                    }

                } else if (target.equals("blank")) {   //跳转到新的页面
                    intent = new Intent(mContext, WebActivity.class);
                    intent.putExtra("url", map.get("url"));
                    ((Activity) mContext).startActivityForResult(intent, Global.REFRESH_DATA);
                } else {  //当没有传递"self"时候，默认当前页面打开
                    if (myWebView != null && TextUtils.isEmpty(myWebView.getCurrentUrl())) {
                        myWebView.loadUrl(map.get("url"));
                    } else {
                        mWebview.loadUrl(map.get("url"), Global.getTokenTimeStampHeader(mContext));
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


    class StoreId {
        public String id;
    }

    /**
     * 获取未修点详情
     */
    public void getStoreDetail(String id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("id", id);
        new BaseDataPresenter(mContext).loadData(DataUrl.GET_STORE_DETAIL, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Map<String, String> storeMap = (Map<String, String>) data.response;
                Intent intent = new Intent(mContext, DriveRouteActivity.class);
                RepairStoreBean bean = new RepairStoreBean();
                bean.lat = Double.parseDouble(storeMap.get("lat"));
                bean.lng = Double.parseDouble(storeMap.get("lng"));
                bean.photos = storeMap.get("thumb");
                bean.title = storeMap.get("name");
                bean.address = storeMap.get("address");
                bean.phone = storeMap.get("phone");
                //
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", bean);
                intent.putExtras(bundle);
                intent.putExtra("startlat", Global.mCurrLat);
                intent.putExtra("startlng", Global.mCurrLng);
                mContext.startActivity(intent);

            }

            @Override
            public void onFailure(BaseDataBean data) {

            }

            @Override
            public void onError() {

            }
        });
    }


    public interface OnWebConfigurationListener {
        void configuration(String pullrefresh, ArrayList<Map<String, String>> topbarRight);
    }

    public void setOnWebConfigurationListener(OnWebConfigurationListener onWebConfigurationListener) {
        this.onWebConfigurationListener = onWebConfigurationListener;
    }

    public void configurationData(String pullrefresh, ArrayList<Map<String, String>> topbarRight) {
        if (onWebConfigurationListener != null) {
            onWebConfigurationListener.configuration(pullrefresh, topbarRight);
        }
    }


}




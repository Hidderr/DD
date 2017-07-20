package com.coco3g.daishu.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.coco3g.daishu.R;
import com.coco3g.daishu.bean.WeiXinLoginReturnBean;
import com.coco3g.daishu.data.Constants;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.net.utils.Coco3gNetRequest;
import com.coco3g.daishu.utils.Coco3gBroadcastUtils;
import com.coco3g.daishu.view.MyProgressDialog;
import com.coco3g.daishu.view.TopBarView;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    TopBarView mTopBar;
    private IWXAPI api;
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;
    MyProgressDialog mProgress;
    //微信登录后的
    private String openID = "", nickname = "", avatar_url = "", sex = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_callback);
        mTopBar = (TopBarView) findViewById(R.id.topbar_wx_callback);
        mTopBar.setTitle("微信");
        //
        api = WXAPIFactory.createWXAPI(this, Constants.WEIXIN_APP_ID, true);
        api.handleIntent(getIntent(), this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    @Override
    public void onReq(BaseReq req) {
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
//        switch (resp.errCode) {
//            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                Global.showToast("用户取消", WXEntryActivity.this);
////                new Coco3gBroadcastUtils(WXEntryActivity.this).sendBroadcast(Coco3gBroadcastUtils.WEIXIN_LOGIN_FAILURE, null);
//                finish();
//                break;
//            case BaseResp.ErrCode.ERR_OK:
//                switch (resp.getType()) {
//                    case RETURN_MSG_TYPE_LOGIN:
//                        break;
//                    case RETURN_MSG_TYPE_SHARE:
//                        Global.showToast("分享成功", WXEntryActivity.this);
//                        finish();
//                        break;
//                }
//                break;
//        }

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:  //用户拒绝授权
                Global.showToast("登录失败", WXEntryActivity.this);
            case BaseResp.ErrCode.ERR_USER_CANCEL:  //取消
                Global.showToast("用户取消", WXEntryActivity.this);
                finish();
                break;
            case BaseResp.ErrCode.ERR_OK: //成功，用户同意
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        String code = ((SendAuth.Resp) resp).code;
                        Log.e("code", code + "--");
                        getWxAccessToken(code);
                        break;
                    case RETURN_MSG_TYPE_SHARE:
                        Global.showToast("微信分享成功", WXEntryActivity.this);
                        new Coco3gBroadcastUtils(WXEntryActivity.this).sendBroadcast(Coco3gBroadcastUtils.SHARE_SUCCESS, null);
                        finish();
                        break;
                }
                break;
        }

    }

    /**
     * 获取微信access_token
     *
     * @param code
     */
    private void getWxAccessToken(String code) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("appid=", Constants.WEIXIN_APP_ID));
        params.add(new BasicNameValuePair("secret=", Constants.WEIXIN_APP_SECRET));
        params.add(new BasicNameValuePair("code=", code));
        params.add(new BasicNameValuePair("grant_type=", "authorization_code"));
        String values = "";
        for (NameValuePair nvp : params) {
            values = values + nvp.getName() + nvp.getValue() + "&";
        }
        new Coco3gNetRequest(WXEntryActivity.this, "get", DataUrl.GET_WX_ACCESS_TOKEN + values, true, "正在登录微信").addRequestModel(new HashMap<String, Object>())
                .setHandler(mHandler).run();
//        new DownLoadDataLib("get", new BindignLoginData()).setHandler(mHandler).getWxAccessToken(values);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.obj == null) {
                Global.showToast("数据返回错误，请检查网络重试...", WXEntryActivity.this);
                return;
            }
//            BaseDataBean data = (BaseDataBean) msg.obj;
            Map<String, Object> access_tokenMap = (Map<String, Object>) msg.obj;
            if (access_tokenMap != null) {
                getWxUserInfo(access_tokenMap.get("access_token") + "", access_tokenMap.get("openid") + "");
            } else {
                // 微信登录失败，发送广播
                new Coco3gBroadcastUtils(WXEntryActivity.this).sendBroadcast(Coco3gBroadcastUtils.WEIXIN_LOGIN_FAILURE, null);
//                Intent intent = new Intent("wx_login");
//                sendBroadcast(intent);
                finish();
            }
        }

    };

    /**
     * 获取微信用户信息
     *
     * @param access_token
     * @param openid
     */
    private void getWxUserInfo(String access_token, String openid) {
//        mProgress = MyProgressDialog.show(this, "正在绑定微信登录，请稍候...", false, true);
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("access_token=", access_token));
        params.add(new BasicNameValuePair("openid=", openid));
        String values = "";
        for (NameValuePair nvp : params) {
            values = values + nvp.getName() + nvp.getValue() + "&";
        }
//        new DownLoadDataLib("get", new BindignLoginData()).setHandler(mHandlerUserInfo).getWxUserInfo(values);
        new Coco3gNetRequest(WXEntryActivity.this, "get", DataUrl.GET_WX_USERINFO + values, true, "正在获取微信信息").addRequestModel(new HashMap<String, Object>()).setHandler(mHandlerUserInfo).run();
    }

    Handler mHandlerUserInfo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
//            mProgress.cancel();
            if (msg.obj == null) {
                Global.showToast("数据返回错误，请检查网络重试...", WXEntryActivity.this);
                return;
            }
            Map<String, Object> userInfoMap = (Map<String, Object>) msg.obj;
            if (userInfoMap != null) {
                openID = userInfoMap.get("openid") + "";
                nickname = userInfoMap.get("nickname") + "";
                avatar_url = userInfoMap.get("headimgurl") + "";
                sex = userInfoMap.get("sex") + "";
                //
                WeiXinLoginReturnBean data = new WeiXinLoginReturnBean();
                data.openid = openID;
                data.headimgurl = avatar_url;
                data.nickname = nickname;
                data.sex = sex;
                // 微信登录成功，发送广播
                Bundle b = new Bundle();
                b.putSerializable("weixindata", data);
                new Coco3gBroadcastUtils(WXEntryActivity.this).sendBroadcast(Coco3gBroadcastUtils.WEIXIN_LOGIN_SUCCESS, b);
            } else {
                // 微信登录失败，发送广播
                new Coco3gBroadcastUtils(WXEntryActivity.this).sendBroadcast(Coco3gBroadcastUtils.WEIXIN_LOGIN_FAILURE, null);
            }
            finish();


        }

    };
}
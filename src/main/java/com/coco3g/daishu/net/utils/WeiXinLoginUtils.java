package com.coco3g.daishu.net.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.coco3g.daishu.bean.WeiXinLoginReturnBean;
import com.coco3g.daishu.data.Constants;
import com.coco3g.daishu.utils.Coco3gBroadcastUtils;
import com.coco3g.daishu.view.MyProgressDialog;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WeiXinLoginUtils {
    Context mContext;
    private IWXAPI api;
    WXLoginCompleteListener wxlogincompletelistener;
    WXLoginFailureListener wxloginfailurelistener;
    Coco3gBroadcastUtils mCurrBroadcast, mCurrBroadcastFailure;
    MyProgressDialog mProgress;

    public WeiXinLoginUtils(Context context) {
        mContext = context;
        api = WXAPIFactory.createWXAPI(mContext, Constants.WEIXIN_APP_ID, true);
        api.registerApp(Constants.WEIXIN_APP_ID);
//		// 注册广播接收
//		receiveBroadCast = new ReceiveBroadCast();
//		IntentFilter filter = new IntentFilter("flag");
//		filter.addAction("wx_login");
//		mContext.registerReceiver(receiveBroadCast, filter);
        mCurrBroadcast = new Coco3gBroadcastUtils(mContext);
        mCurrBroadcast.receiveBroadcast(Coco3gBroadcastUtils.WEIXIN_LOGIN_SUCCESS).setOnReceivebroadcastListener(new Coco3gBroadcastUtils.OnReceiveBroadcastListener() {
            @Override
            public void receiveReturn(Intent intent) {
                Bundle bundle = intent.getBundleExtra("data");
                WeiXinLoginReturnBean data = (WeiXinLoginReturnBean) bundle.getSerializable("weixindata");
                logincomplete(data);
            }
        });
        mCurrBroadcastFailure = new Coco3gBroadcastUtils(mContext);
        mCurrBroadcastFailure.receiveBroadcast(Coco3gBroadcastUtils.WEIXIN_LOGIN_FAILURE).setOnReceivebroadcastListener(new Coco3gBroadcastUtils.OnReceiveBroadcastListener() {
            @Override
            public void receiveReturn(Intent intent) {
                loginfailure();
            }
        });
    }

    public WeiXinLoginUtils login() {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        api.sendReq(req);
        return this;
    }

    //	private class ReceiveBroadCast extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// 得到广播中得到的数据，并显示出来
//			if ("wx_login".equals(intent.getAction())) {
//				BindignLoginData logindata = (BindignLoginData) intent.getSerializableExtra("logindata");
//				logincomplete(logindata);
//			}
//		}
//	}
//
    public WeiXinLoginUtils setWXLoginCompleteListener(WXLoginCompleteListener wxlogincompletelistener) {
        this.wxlogincompletelistener = wxlogincompletelistener;
        return this;
    }

    public interface WXLoginCompleteListener {
        void logincomplete(WeiXinLoginReturnBean logindata);
    }

    private void logincomplete(WeiXinLoginReturnBean logindata) {
        if (wxlogincompletelistener != null) {
            wxlogincompletelistener.logincomplete(logindata);
        }
    }

    public void setWXLoginFailureListener(WXLoginFailureListener wxloginfailurelistener) {
        this.wxloginfailurelistener = wxloginfailurelistener;
    }

    public interface WXLoginFailureListener {
        void loginfailure();
    }

    private void loginfailure() {
        if (wxloginfailurelistener != null) {
            wxloginfailurelistener.loginfailure();
        }
    }

    public void unregisterReceiver() {
        if (mCurrBroadcast != null) {
            mCurrBroadcast.unregisterBroadcast();
        }
        if (mCurrBroadcastFailure != null) {
            mCurrBroadcastFailure.unregisterBroadcast();
        }
    }
}

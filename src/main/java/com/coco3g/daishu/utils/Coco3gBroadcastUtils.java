package com.coco3g.daishu.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * Created by lisen on 16/5/13.
 */
public class Coco3gBroadcastUtils {
    Context mContext;
    public static final String RETURN_GOTO_TAB = "return_goto_tab"; // 跳转到某个tab页
    public static final String WEIXIN_LOGIN_SUCCESS = "weixin_login_success"; // 微信登录成功
    public static final String WEIXIN_LOGIN_FAILURE = "weixin_login_failure"; // 微信登录失败
    public static final String SHARE_SUCCESS = "share_success"; // 微信、qq分享成功
    public static final String RETURN_UPDATE_FLAG = "return_update_flag"; // 刷新当前
    public static final String START_LOCATION = "start_location"; //开始定位
    public static final String RONG_UNREAD_MSG = "rong_unread_msg"; //设置融云未读消息
    public static final String RONG_UNREAD_MSG_SYSTEM = "rong_unread_msg_system"; //设置融云未读的系统消息
    public static final String PERSONAL_RONG_UNREAD_MSG = "personal_rong_unread_msg"; //刷新与某人的融云未读消息
    public static final String RONG_CONNECT_STATE_FLAG = "rong_connect_state_flag"; //融云网络状态监听
    //
    private ReceiveBroadCast receiveBroadCast; // 广播实例
    OnReceiveBroadcastListener onreceivebroadcastlistener = null;

    public Coco3gBroadcastUtils(Context mContext) {
        this.mContext = mContext;
        receiveBroadCast = new ReceiveBroadCast();
    }

    /**
     * 发送广播
     *
     * @param broadcastflag
     * @param bundle        携带数据
     */
    public void sendBroadcast(String broadcastflag, Bundle bundle) {
        Intent intent = new Intent(broadcastflag);
        if (bundle != null) {
            intent.putExtra("data", bundle);
        }
        mContext.sendBroadcast(intent);
    }

    public Coco3gBroadcastUtils receiveBroadcast(String broadcastflag) {
        IntentFilter filter = new IntentFilter("coco3gbroadcast");
        filter.addAction(broadcastflag);
        mContext.registerReceiver(receiveBroadCast, filter);
        return this;
    }

    private class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 得到广播中得到的数据，并显示出来
//            if ("paycomplete".equals(intent.getAction())) {
//                onPayComplete(1);
//            }
            onReceiveReturn(intent);
        }
    }

    public void setOnReceivebroadcastListener(OnReceiveBroadcastListener onreceivebroadcastlistener) {
        this.onreceivebroadcastlistener = onreceivebroadcastlistener;
    }

    public interface OnReceiveBroadcastListener {
        void receiveReturn(Intent intent);
    }

    private void onReceiveReturn(Intent intent) {
        if (onreceivebroadcastlistener != null) {
            onreceivebroadcastlistener.receiveReturn(intent);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mContext.unregisterReceiver(receiveBroadCast);
//                }
//            }, 1000);
        }
    }

    /**
     * 注销广播
     */
    public void unregisterBroadcast() {
        if (receiveBroadCast != null) {
            mContext.unregisterReceiver(receiveBroadCast);
        }
    }
}

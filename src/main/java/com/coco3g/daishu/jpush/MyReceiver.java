package com.coco3g.daishu.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.coco3g.daishu.activity.MainActivity;
import com.coco3g.daishu.activity.StartActivity;
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.bean.JPushData;
import com.coco3g.daishu.data.Constants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p/>
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "QianQu";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.e(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            Constants.JPUSH_REGISTERID = regId;
            // send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.e(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            // processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.e(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.e(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
//            processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.e(TAG, "[MyReceiver] 用户点击打开了通知");
            processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.e(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            // 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
            // 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.e(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.e(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.e(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" + myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    // send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        JPushInterface.clearAllNotifications(context);
        try {
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Gson gson = new Gson();
            JPushData jpdata = gson.fromJson(extras, new JPushData().getClass());
            String msgtype = jpdata.msgtype;
            String value = "";
            if ("1".equals(msgtype)) { // 订单号
                value = jpdata.txt;
            } else if ("2".equals(msgtype)) { // 商品ID
                value = jpdata.txt;
            } else if ("3".equals(msgtype)) { // url
                value = jpdata.txt;
            }
            if (MainActivity.isForeground) { // 应用内
                if ("1".equals(msgtype)) {
//                    Intent intent = new Intent(context, WebActivity.class);
//                    intent.putExtra("title", "订单详情");
//                    intent.putExtra("url", DataUrl.ORDER_DETAIL_WAP_URL + value);
//                    context.startActivity(intent);
                } else if ("2".equals(msgtype)) { // 商品ID
//                    Intent intent = new Intent(context, GoodsDetailActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra("title", "");
//                    intent.putExtra("goodsid", Integer.parseInt(value));
//                    context.startActivity(intent);
                } else if ("3".equals(msgtype)) { // url
                    Intent intent = new Intent(context, WebActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    value = URLDecoder.decode(value, "utf-8");
                    intent.putExtra("url", value);
                    intent.putExtra("title", "");
                    context.startActivity(intent);
                }
            } else { // 应用外
                Intent intent = new Intent(context, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("msgtype", msgtype);
                if ("2".equals(msgtype)) {
                    intent.putExtra("value", Integer.parseInt(value));
                } else {
                    value = URLDecoder.decode(value, "utf-8");
                    intent.putExtra("value", value);
                }
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Intent intent = new Intent(context, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }

    }
}

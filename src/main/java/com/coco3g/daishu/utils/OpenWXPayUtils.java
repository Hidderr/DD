package com.coco3g.daishu.utils;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;


import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.wxapi.WXPayUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付工具类
 *
 * @author lisen
 * @date 2015年12月7日下午1:54:57
 */
public class OpenWXPayUtils {
    Context mContext;
    public static String CURRENT_ORDER_SN = ""; // 当前支付订单号
    OnPayCompleteListener onpaycompletelistener;
    private ReceiveBroadCast receiveBroadCast; // 广播实例
    String mGoodsName = "", mGoodsDescription;
    float mPrice = 0f;

    public OpenWXPayUtils(Context context, String ordersn, String goodsname, String goodsdescription, float price) {
        mContext = context;
        CURRENT_ORDER_SN = ordersn;
        mGoodsName = goodsname;
        mGoodsDescription = goodsdescription;
        mPrice = price;
        // 注册广播接收
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter("flag");
        filter.addAction("paycomplete");
        mContext.registerReceiver(receiveBroadCast, filter);
    }

    public OpenWXPayUtils pay() {
        wxPay();
        return this;
    }

    private class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 得到广播中得到的数据，并显示出来
            if ("paycomplete".equals(intent.getAction())) {
                onPayComplete(1);
            }
        }
    }

    /**
     * 微信支付
     */
    private void wxPay() {
        HashMap<String, String> params = new HashMap<>();
        params.put("ordersn", CURRENT_ORDER_SN);
        new BaseDataPresenter(mContext).loadData(DataUrl.WEIXIN_PAY_GET_PREPAYID, params, "正在支付", new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Map<String, String> datamap = (Map<String, String>) data.response;
                String prepay_id = datamap.get("prepay_id");
                if (TextUtils.isEmpty(prepay_id)) {
                    Global.showToast("网络错误，请重试...", mContext);
                    return;
                }
                WXPayUtils payutils = new WXPayUtils(mContext, prepay_id);
                payutils.genPayReq();
                payutils.sendPayReq();
            }

            @Override
            public void onFailure(BaseDataBean data) {
            }

            @Override
            public void onError() {
            }
        });
    }

//
//    Handler mHandlerPay = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            super.handleMessage(msg);
//            mLoadingProgress.cancel();
//            if (msg.obj == null) {
//                Global.showToast("数据返回错误，请检查网络重试...", mContext);
//                return;
//            }
//            BaseData data = (BaseData) msg.obj;
//            Global.showToast(data.msg, mContext);
//            int paytype = msg.arg1;
//            switch (paytype) {
//                case 1: // 余额支付完成
//                    mPayDialog.cancel();
//                    onPayComplete(data.code);
//                    break;
//                case 2: // 支付宝支付
//                    break;
//                case 3: // 微信支付
//                    break;
//            }
//        }
//
//    };

    public OpenWXPayUtils setOnPayCompleteListener(OnPayCompleteListener onpaycompletelistener) {
        this.onpaycompletelistener = onpaycompletelistener;
        return this;
    }

    public interface OnPayCompleteListener {
        public void payComplete(int paystate);
    }

    private void onPayComplete(int paystate) {
        if (onpaycompletelistener != null) {
            onpaycompletelistener.payComplete(paystate);
        }
    }

    public void unregisterBoardcast() {
        if (receiveBroadCast != null) {
            mContext.unregisterReceiver(receiveBroadCast);
        }
    }

}

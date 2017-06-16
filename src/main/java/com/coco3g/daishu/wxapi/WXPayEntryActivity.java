package com.coco3g.daishu.wxapi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.BaseActivity;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.Constants;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.OpenWXPayUtils;
import com.coco3g.daishu.view.TopBarView;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.HashMap;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    TopBarView mTopBar = null;
    private IWXAPI api;
    TextView mTxtPayState;
    Button mBtnComplete, mBtnCheck = null;
    ProgressDialog mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_payresult);
        mTopBar = (TopBarView) findViewById(R.id.wx_payresult_topbar);
        mTopBar.setTitle("微信支付");
        mTopBar.hideLeftView();
        mTxtPayState = (TextView) findViewById(R.id.tv_wx_payresult_state);
        mBtnComplete = (Button) findViewById(R.id.btn_wx_payresult_complete);
        mBtnCheck = (Button) findViewById(R.id.btn_wx_payresult_check);
        api = WXAPIFactory.createWXAPI(this, Constants.WEIXIN_APP_ID);
        api.handleIntent(getIntent(), this);
        //
        mBtnComplete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        mBtnCheck.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                getOrderInfo(OpenWXPayUtils.CURRENT_ORDER_SN);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            // if ("0".equals(OrderActivity.PAYSOURCE)) {
            // ((Activity) OrderActivity.mOrderContext).finish();
            // } else if ("1".equals(OrderActivity.PAYSOURCE)) {
            // // ((Activity) PreviewActivity.mPreViewContext).finish();
            // } else if ("3".equals(OrderActivity.PAYSOURCE)) {
            // ((Activity) UserChargeActivity.mChargeContext).finish();
            // }
            int code = resp.errCode;
            switch (code) {
                case 0: // 支付成功
                    mTxtPayState.setText(String.format(getResources().getString(R.string.weixin_pay_state), "支付成功"));
                    getOrderInfo(OpenWXPayUtils.CURRENT_ORDER_SN);
                    break;
                case -1:
                    mTxtPayState.setText(String.format(getResources().getString(R.string.weixin_pay_state), "签名错误、未注册APPID" +
                            "、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。"));
                    break;
                case -2:
                    mTxtPayState.setText(String.format(getResources().getString(R.string.weixin_pay_state), "取消支付"));
                    break;
            }
            // AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // builder.setTitle("提示");
            // builder.setMessage(getString(R.string.pay_result_callback_msg,
            // resp.errStr + ";code=" + String.valueOf(resp.errCode)));
            // builder.show();
        }
    }

    /**
     * 获取订单详情
     */
    private void getOrderInfo(String ordersn) {
        HashMap<String, String> params = new HashMap<>();
        params.put("ordersn", ordersn);
        new BaseDataPresenter(this).loadData(DataUrl.GET_ORDER_PAY_STATE, params, "正在查询支付状态", new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                int code = data.code;
//                OrderInfoDataBean.OrderBaseInfo orderinfo = data.response;
                if (code == 200) { // 支付成功
                    mBtnComplete.setVisibility(View.VISIBLE);
                    mBtnCheck.setVisibility(View.GONE);
                    Intent intent = new Intent("paycomplete");
                    sendBroadcast(intent);
                } else {
                    mBtnComplete.setVisibility(View.GONE);
                    mBtnCheck.setVisibility(View.VISIBLE);
                    Global.showToast("订单状态查询失败...", WXPayEntryActivity.this);
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

}
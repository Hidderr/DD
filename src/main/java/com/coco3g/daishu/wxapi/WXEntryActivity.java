package com.coco3g.daishu.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Constants;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.view.MyProgressDialog;
import com.coco3g.daishu.view.TopBarView;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    TopBarView mTopBar;
    private IWXAPI api;
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;
    MyProgressDialog mProgress;

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
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Global.showToast("用户取消", WXEntryActivity.this);
//                new Coco3gBroadcastUtils(WXEntryActivity.this).sendBroadcast(Coco3gBroadcastUtils.WEIXIN_LOGIN_FAILURE, null);
                finish();
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        break;
                    case RETURN_MSG_TYPE_SHARE:
                        Global.showToast("分享成功", WXEntryActivity.this);
                        finish();
                        break;
                }
                break;
        }

    }
}
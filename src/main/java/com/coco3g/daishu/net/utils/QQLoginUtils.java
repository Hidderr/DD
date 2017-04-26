package com.coco3g.daishu.net.utils;

import android.app.Activity;
import android.content.Context;

import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Constants;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.view.MyProgressDialog;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * qq绑定登录工具
 *
 * @author lisen
 * @date 2015年12月22日下午4:19:18
 */
public class QQLoginUtils {
    Context mContext;
    Tencent mTencent = null;
    public qqLoginUiListener qqLoginUiListener;
    String openID = "", nickName = "", avatar_url = "";
    MyProgressDialog myProgressDialog;

    QQLoginCompleteListener qqlogincompletelistener = null;

    public QQLoginUtils(Context context) {
        mContext = context;
        myProgressDialog = new MyProgressDialog(mContext);
    }

    /**
     * QQ登录
     */
    public QQLoginUtils login() {
        myProgressDialog = MyProgressDialog.show(mContext, mContext.getResources().getString(R.string.bangding_qq), false, false);
        mTencent = Tencent.createInstance(Constants.QQ_APP_ID, mContext.getApplicationContext());
        qqLoginUiListener = new qqLoginUiListener();
        mTencent.login((Activity) mContext, "all", qqLoginUiListener);
        return this;
    }

    class qqLoginUiListener implements IUiListener {
        @Override
        public void onError(UiError error) {
            // TODO Auto-generated method stub
            if (error.errorCode == 110406) {
                Global.showToast("测试阶段，该qq号未获得授权，请联系管理员", mContext);
                logincomplete(null, null, null);
            }
        }

        @Override
        public void onComplete(Object jsonObject) {
            // TODO Auto-generated method stub
//				String str = jsonObject.toString();
//				Gson gson = new Gson();
            myProgressDialog.cancel();
            JSONObject object = (JSONObject) jsonObject;
            try {
                openID = object.getString("openid");
                String accessToken = object.getString("access_token");
                String expires = object.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //
            if (mTencent != null && mTencent.isSessionValid() && jsonObject != null) {   //获取nickname和avatar
                UserInfo mInfo = new UserInfo(mContext, mTencent.getQQToken());
                mInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onError(UiError e) {
                        // TODO Auto-generated method stub
                        logincomplete(null, null, null);
                    }

                    @Override
                    public void onComplete(final Object response) {
                        JSONObject json = (JSONObject) response;
                        try {
                            nickName = ((JSONObject) response).getString("nickname");
                            avatar_url = json.getString("figureurl_qq_2");
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        logincomplete(openID, nickName, avatar_url);
                        myProgressDialog.cancel();
                    }

                    @Override
                    public void onCancel() {
                        // TODO Auto-generated method stub
                        logincomplete(null, null, null);
                    }
                });

            }
        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
            myProgressDialog.cancel();
            logincomplete(null, null, null);
        }
    }


    public IUiListener getIUiListener() {
        return qqLoginUiListener;
    }


    public void setQQLoginCompleteListener(QQLoginCompleteListener qqlogincompletelistener) {
        this.qqlogincompletelistener = qqlogincompletelistener;
    }

    public interface QQLoginCompleteListener {
        public void logincomplete(String openID, String nickName, String avatarUrl);
    }

    private void logincomplete(String openID, String nickName, String avatarUrl) {
        if (qqlogincompletelistener != null) {
            qqlogincompletelistener.logincomplete(openID, nickName, avatarUrl);
        }
    }

}

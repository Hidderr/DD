package com.coco3g.daishu.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.bean.WeiXinLoginReturnBean;
import com.coco3g.daishu.data.Constants;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.net.utils.QQLoginUtils;
import com.coco3g.daishu.net.utils.WeiXinLoginUtils;
import com.coco3g.daishu.presenter.BaseDataPresenter;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity implements View.OnClickListener {
    private ImageView mImageWXLogin, mImageQQLogin;
    private EditText mEditPhone, mEditPassWord;
    private TextView mTxtLogin, mTxtRegister, mTxtForgetPassWord, mTxtKanKan;
    //
    private RelativeLayout.LayoutParams avatar_lp;
    private String mPhone = "", mPassWord = "";

    //
    //qq登录utils
    QQLoginUtils qqLoginUtils;
    //微信登录utils
    WeiXinLoginUtils weiXinLoginUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        Global.getScreenWH(this);

        initView();

    }

    private void initView() {
        //
        mEditPhone = (EditText) findViewById(R.id.edit_login_phone);
        mEditPassWord = (EditText) findViewById(R.id.edit_login_password);
        mTxtLogin = (TextView) findViewById(R.id.tv_login_start);
        mTxtRegister = (TextView) findViewById(R.id.tv_login_fast_register);
        mTxtRegister.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mTxtForgetPassWord = (TextView) findViewById(R.id.tv_login_forgetpwd);
        mTxtKanKan = (TextView) findViewById(R.id.tv_login_kankan);
        mImageWXLogin = (ImageView) findViewById(R.id.image_login_weixin);
        mImageQQLogin = (ImageView) findViewById(R.id.image_login_qq);
        //输入用户名字的监听(控制删除按钮的显示与否)
        mEditPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String mPhone = mEditPhone.getText().toString().trim();
//                setAvatar(mPhone);
            }
        });
//        mEditPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus && !TextUtils.isEmpty(mEditPhone.getText().toString().trim())) {
//                    mImageDel.setVisibility(View.VISIBLE);
//                } else {
//                    mImageDel.setVisibility(View.GONE);
//                }
//
//            }
//        });
        //
        mTxtLogin.setOnClickListener(this);
        mTxtForgetPassWord.setOnClickListener(this);
        mTxtRegister.setOnClickListener(this);
        mImageWXLogin.setOnClickListener(this);
        mImageQQLogin.setOnClickListener(this);
        mTxtKanKan.setOnClickListener(this);
        //
        final HashMap<String, String> loginMap = Global.readLoginInfo(LoginActivity.this, Global.LOGIN_INFO_LAST);
        if (loginMap != null) {
            mEditPhone.setText(loginMap.get("phone"));
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.tv_login_start:  //登录
                mPhone = mEditPhone.getText().toString().trim();
                if (TextUtils.isEmpty(mPhone)) {
                    Global.showToast("请输入手机号或账号", this);
                    return;
                }
                mPassWord = mEditPassWord.getText().toString().trim();
                if (TextUtils.isEmpty(mPassWord)) {
                    Global.showToast("密码不能为空", this);
                    return;
                }
                if (mPassWord.length() < 6) {
                    Global.showToast("密码长度不能小于6个字符", this);
                    return;
                }

                login(mPhone, mPassWord);

                break;

            case R.id.tv_login_fast_register:   //注册
                intent = new Intent(this, RegisterActivity.class);
                startActivityForResult(intent, Constants.REGISTER_REQUEST_CODE);
                overridePendingTransition(R.anim.acivity_in, R.anim.acivity_out);
                break;

            case R.id.tv_login_kankan:  //随便看看
                finish();

                break;

            case R.id.tv_login_forgetpwd:   //忘记密码
                intent = new Intent(this, ForgetPassWordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.acivity_in, R.anim.acivity_out);
                break;

            case R.id.image_login_weixin:   //微信登录
//                wxLogin();

                break;

            case R.id.image_login_qq:   //qq登录
//                qqLogin();

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Constants.REGISTER_SUCCESS_RETURN_CODE) {
            finish();
        }
    }


    //登录
    public void login(final String mLoginPhone, final String mLoginPassWord) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", mLoginPhone);
        params.put("password", mLoginPassWord);
        new BaseDataPresenter(this).loadData(DataUrl.LOGIN, params, getResources().getString(R.string.logining), new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                if (data.code == 200) {
                    Global.USERINFOMAP = (Map<String, String>) data.response;
                    Global.savePassWord(LoginActivity.this, mLoginPassWord);
                    Global.saveLoginInfo(LoginActivity.this, mLoginPhone, Global.USERINFOMAP.get("nickname"), mLoginPassWord, Global.LOGIN_INFO);
                    Global.saveLoginInfo(LoginActivity.this, mLoginPhone, Global.USERINFOMAP.get("nickname"), mLoginPassWord, Global.LOGIN_INFO_LAST);
                    //
                    if (Global.MAINACTIVITY_CONTEXT != null) {
                        ((Activity) Global.MAINACTIVITY_CONTEXT).finish();
                        Global.MAINACTIVITY_CONTEXT = null;
                    }
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Global.showToast(data.msg, LoginActivity.this);
                }
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, LoginActivity.this);
            }

            @Override
            public void onError() {

            }
        });
    }

    //qq登录
    public void qqLogin() {
        qqLoginUtils = new QQLoginUtils(this);
        qqLoginUtils.login().setQQLoginCompleteListener(new QQLoginUtils.QQLoginCompleteListener() {
            @Override
            public void logincomplete(String openID, String nickName, String avatarUrl) {
                Log.e("QQ登录", "信息：" + openID + "***" + nickName + "***" + avatarUrl);
                threeOtherLogin("qq", nickName, avatarUrl, openID);
            }
        });

    }


    //微信登录
    public void wxLogin() {
        weiXinLoginUtils.login().setWXLoginCompleteListener(new WeiXinLoginUtils.WXLoginCompleteListener() {
            @Override
            public void logincomplete(WeiXinLoginReturnBean logindata) {
                Log.e("登录成功", logindata.openid + "--" + logindata.nickname);
                threeOtherLogin("weixin", logindata.nickname, logindata.headimgurl, logindata.openid);
            }
        }).setWXLoginFailureListener(new WeiXinLoginUtils.WXLoginFailureListener() {
            @Override
            public void loginfailure() {
                Log.e("登录失败", "ssssss");
            }
        });
    }


    //第三方登录与后台进行绑定
    public void threeOtherLogin(final String loginType, final String nickname, final String avatar, final String openID) {
        HashMap<String, String> params = new HashMap<>();
        params.put("logintype", loginType);
        params.put("nickname", nickname);
        params.put("avatar", avatar);
        params.put("qqkey", openID);
        params.put("uuid", Constants.JPUSH_REGISTERID);
        new BaseDataPresenter(this).loadData(DataUrl.REGISTER, params, getResources().getString(R.string.login_loading), new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                if (data.response == null) {
                    return;
                }
                Global.USERINFOMAP = (Map<String, String>) data.response;
                Global.saveLoginInfo(LoginActivity.this, Global.USERINFOMAP.get("phone"), mPassWord, avatar, Global.LOGIN_INFO);
                Global.saveLoginInfo(LoginActivity.this, Global.USERINFOMAP.get("phone"), mPassWord, avatar, Global.LOGIN_INFO_LAST);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onFailure(BaseDataBean data) {
//                Global.showToast(data.msg, LoginActivity.this);
                if (data.code == 400) {  //表示没有绑定第三方
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    intent.putExtra("mode", 1);
                    intent.putExtra("logintype", loginType);
                    intent.putExtra("nickname", nickname);
                    intent.putExtra("avatar", avatar);
                    intent.putExtra("qqkey", openID);
                    startActivity(intent);
                }
            }

            @Override
            public void onError() {

            }
        });
    }


}

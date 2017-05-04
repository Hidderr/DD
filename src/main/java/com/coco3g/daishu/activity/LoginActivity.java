package com.coco3g.daishu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.Constants;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;

import java.util.HashMap;
import java.util.Map;



public class LoginActivity extends Activity implements View.OnClickListener {
    private ImageView mImageDel;
    private EditText mEditPhone, mEditPassWord;
    private TextView mTxtLogin, mTxtRegister, mTxtForgetPassWord;
    //
    private RelativeLayout.LayoutParams avatar_lp;
    private String mPhone = "", mPassWord = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Global.getScreenWH(this);

        initView();

    }

    private void initView() {
        //
        mImageDel = (ImageView) findViewById(R.id.image_login_username_del);
        mEditPhone = (EditText) findViewById(R.id.edit_login_phone);
        mEditPassWord = (EditText) findViewById(R.id.edit_login_password);
        mTxtLogin = (TextView) findViewById(R.id.tv_login_start);
        mTxtRegister = (TextView) findViewById(R.id.tv_login_fast_register);
        mTxtForgetPassWord = (TextView) findViewById(R.id.tv_login_forgetpwd);
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
                if (TextUtils.isEmpty(mPhone)) {
                    mImageDel.setVisibility(View.GONE);
                } else {
                    mImageDel.setVisibility(View.VISIBLE);
                }
//                setAvatar(mPhone);
            }
        });
        mEditPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !TextUtils.isEmpty(mEditPhone.getText().toString().trim())) {
                    mImageDel.setVisibility(View.VISIBLE);
                } else {
                    mImageDel.setVisibility(View.GONE);
                }

            }
        });
        //
        mImageDel.setOnClickListener(this);
        mTxtLogin.setOnClickListener(this);
        mTxtForgetPassWord.setOnClickListener(this);
        mTxtRegister.setOnClickListener(this);
        //
        final HashMap<String, String> loginMap = Global.readLoginInfo(LoginActivity.this, Global.LOGIN_INFO_LAST);
        if (loginMap != null) {
            mEditPhone.setText(loginMap.get("phone"));
//            mEditPassWord.setText("123456");
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.image_login_username_del:  //清除手机号/账号
                mEditPhone.setText("");
                break;

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
            case R.id.tv_login_forgetpwd:   //忘记密码
                intent = new Intent(this, ForgetPassWordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.acivity_in, R.anim.acivity_out);
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
                    Global.saveLoginInfo(LoginActivity.this, mLoginPhone, Global.USERINFOMAP.get("realname"), Global.USERINFOMAP.get("id"), mLoginPassWord, Global.USERINFOMAP.get("avatar"), Global.LOGIN_INFO);
                    Global.saveLoginInfo(LoginActivity.this, mLoginPhone, Global.USERINFOMAP.get("realname"), Global.USERINFOMAP.get("id"), mLoginPassWord, Global.USERINFOMAP.get("avatar"), Global.LOGIN_INFO_LAST);
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


}

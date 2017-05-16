package com.coco3g.daishu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.Constants;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.view.TimingView;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends Activity implements View.OnClickListener {
    private EditText mEditPhone, mEditPassWord, mEditVeriCode;
    private TextView mTxtLogin, mTxtRegister, mTxtXieYi;
    private TimingView mTimingView;
    //
    private String mPhone = "", mPassWord = "", mVeriCode = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Global.getScreenWH(this);

        initView();

    }

    private void initView() {
        //
        mEditPhone = (EditText) findViewById(R.id.edit_register_phone);
        mEditPassWord = (EditText) findViewById(R.id.edit_register_password);
        mEditVeriCode = (EditText) findViewById(R.id.edit_register_verification);
        mTxtLogin = (TextView) findViewById(R.id.tv_register_go_login);
        mTxtRegister = (TextView) findViewById(R.id.tv_register_start);
        mTimingView = (TimingView) findViewById(R.id.timing_register_get_certificate_code);
        mTxtXieYi = (TextView) findViewById(R.id.tv_register_xieyi_2);
        //
        mTimingView.setTimeInterval(1000);
        mTimingView.setMaxSecond(60);
        mTimingView.setOnClickListener(this);
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
            }
        });
        //
        mTxtLogin.setOnClickListener(this);
        mTxtRegister.setOnClickListener(this);
        mTimingView.setOnClickListener(this);
        mTxtXieYi.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.tv_register_start:  //注册
                mPhone = mEditPhone.getText().toString().trim();
                if (TextUtils.isEmpty(mPhone)) {
                    Global.showToast("请输入手机号或账号", this);
                    return;
                }
                mVeriCode = mEditVeriCode.getText().toString().trim();
                if (TextUtils.isEmpty(mVeriCode)) {
                    Global.showToast("请输入验证码", this);
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

                register();

                break;

            case R.id.timing_register_get_certificate_code:  //验证码
                mPhone = mEditPhone.getText().toString().trim();
                if (TextUtils.isEmpty(mPhone)) {
                    Global.showToast("请输入手机号或账号", this);
                    return;
                }
                getVeriCode();

                break;

            case R.id.tv_register_go_login:  //去登陆
                finish();

                break;

            case R.id.tv_register_xieyi_2:  //注册协议
                intent = new Intent(this, WebActivity.class);
                intent.putExtra("url", DataUrl.REGISTER_XIEYI);
                startActivity(intent);

                break;

        }
    }

    //获取验证码
    public void getVeriCode() {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", mPhone);
        params.put("check_phone_unique", "1");
        new BaseDataPresenter(this).loadData(DataUrl.GET_PHONE_CODE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Global.showToast(data.msg, RegisterActivity.this);
                mTimingView.startTiming();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, RegisterActivity.this);
            }

            @Override
            public void onError() {

            }
        });
    }


    //    //注册
    public void register() {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", mPhone);
        params.put("password", mPassWord);
        params.put("code", mVeriCode);
        new BaseDataPresenter(this).loadData(DataUrl.REGISTER, params, getResources().getString(R.string.registering), new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Global.USERINFOMAP = (Map<String, String>) data.response;
                Global.saveLoginInfo(RegisterActivity.this, mPhone, Global.USERINFOMAP.get("nickname"), mPassWord, Global.LOGIN_INFO);
                Global.saveLoginInfo(RegisterActivity.this, mPhone, Global.USERINFOMAP.get("nickname"), mPassWord, Global.LOGIN_INFO_LAST);
                //
                if (Global.MAINACTIVITY_CONTEXT != null) {
                    ((Activity) Global.MAINACTIVITY_CONTEXT).finish();
                    Global.MAINACTIVITY_CONTEXT = null;
                }
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                setResult(Constants.REGISTER_SUCCESS_RETURN_CODE);
                finish();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, RegisterActivity.this);
            }

            @Override
            public void onError() {

            }
        });
    }


}

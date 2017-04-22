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
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.view.TimingView;


public class RegisterActivity extends Activity implements View.OnClickListener {
    private EditText mEditPhone, mEditPassWord;
    private TextView mTxtLogin, mTxtRegister;
    private TimingView mTimingView;
    //
    private String mPhone = "", mPassWord = "";


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
        mTxtLogin = (TextView) findViewById(R.id.tv_register_go_login);
        mTxtRegister = (TextView) findViewById(R.id.tv_register_start);
        mTimingView = (TimingView) findViewById(R.id.timing_register_get_certificate_code);
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
                mPassWord = mEditPassWord.getText().toString().trim();
                if (TextUtils.isEmpty(mPassWord)) {
                    Global.showToast("密码不能为空", this);
                    return;
                }
                if (mPassWord.length() < 6) {
                    Global.showToast("密码长度不能小于6个字符", this);
                    return;
                }
//                login(mPhone, mPassWord);

                break;

            case R.id.timing_register_get_certificate_code:  //验证码
                mTimingView.startTiming();

                break;

            case R.id.tv_register_go_login:  //去登陆
                finish();

                break;

        }
    }



//    //登录
//    public void login(final String mLoginPhone, final String mLoginPassWord) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("phone", mLoginPhone);
//        params.put("password", mLoginPassWord);
//        new BaseDataPresenter(this).loadData(DataUrl.LOGIN, params, getResources().getString(R.string.logining), new IBaseDataListener() {
//            @Override
//            public void onSuccess(BaseDataBean data) {
//                Global.USERINFOMAP = (Map<String, String>) data.response;
//                Global.savePassWord(LoginActivity.this, mLoginPassWord);
//                Global.saveLoginInfo(LoginActivity.this, mLoginPhone, mLoginPassWord, Global.USERINFOMAP.get("avatar"), Global.LOGIN_INFO);
//                Global.saveLoginInfo(LoginActivity.this, mLoginPhone, mLoginPassWord, Global.USERINFOMAP.get("avatar"), Global.LOGIN_INFO_LAST);
//                //
//                ((Activity) Global.MAINACTIVITY_CONTEXT).finish();
//                Global.MAINACTIVITY_CONTEXT = null;
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
//                //连接融云
//                new RongUtils(LoginActivity.this).init();
//                finish();
//            }
//
//            @Override
//            public void onFailure(BaseDataBean data) {
//                Global.showToast(data.msg, LoginActivity.this);
//            }
//
//            @Override
//            public void onError() {
//
//            }
//        });
//    }


}

package com.coco3g.daishu.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.view.TimingView;
import com.coco3g.daishu.view.TopBarView;


public class ForgetPassWordActivity extends BaseActivity implements View.OnClickListener {
    private TopBarView mTopBar;
    private EditText  mEditPhone, mEditVerifiCode,mEditPassWord,mEditConfirmPwd;
    private TextView mTxtChangePassword;
    private TimingView mTimeCountDown;
    private String mPassWord = "", mPhone = "", mVerifiCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initView();
    }

    private void initView() {
        mTopBar = (TopBarView) findViewById(R.id.topbar_forget_password);
        mTopBar.setTitle(getResources().getString(R.string.forget_password_1));
        //
        mEditPassWord = (EditText) findViewById(R.id.edit_forget_password_password);
        mEditConfirmPwd = (EditText) findViewById(R.id.edit_forget_password_confirm_password);
        mEditPhone = (EditText) findViewById(R.id.edit_forget_password_phone);
        mEditVerifiCode = (EditText) findViewById(R.id.edit_forget_password_verification);
        mTimeCountDown = (TimingView) findViewById(R.id.time_forget_password_get_verification);
        mTxtChangePassword = (TextView) findViewById(R.id.tv_forget_password_change_password);
        //
        mTimeCountDown.setTimeInterval(1000);
        mTimeCountDown.setMaxSecond(60);
        mTimeCountDown.setOnClickListener(this);
        mTxtChangePassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.time_forget_password_get_verification:  //获取验证码
                mPhone = mEditPhone.getText().toString().trim();
                if (TextUtils.isEmpty(mPhone)) {
                    Global.showToast(getResources().getString(R.string.register_phone), this);
                    return;
                }
//                getCertificationCode();
                break;
            case R.id.tv_forget_password_change_password:  //修改密码
                mPhone = mEditPhone.getText().toString().trim();
                if (TextUtils.isEmpty(mPhone)) {
                    Global.showToast(getResources().getString(R.string.register_phone), this);
                    return;
                }
                mVerifiCode = mEditVerifiCode.getText().toString().trim();
                if (TextUtils.isEmpty(mVerifiCode)) {
                    Global.showToast(getResources().getString(R.string.register_verification), this);
                    return;
                }
                mPassWord = mEditPassWord.getText().toString().trim();
                if (TextUtils.isEmpty(mPassWord)) {
                    Global.showToast(getResources().getString(R.string.new_password), this);
                    return;
                }
                if (mPassWord.length() < 6) {
                    Global.showToast(getResources().getString(R.string.register_password_limit), this);
                    return;
                }
//                changePassword();

                break;
        }
    }


//    //获取验证码
//    public void getCertificationCode() {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("phone", mPhone);
//        new BaseDataPresenter(this).loadData(DataUrl.GET_PHONE_CODE, params, null, new IBaseDataListener() {
//            @Override
//            public void onSuccess(BaseDataBean data) {
//                Global.showToast(data.msg, ForgetPassWordActivity.this);
//                mTimeCountDown.startTiming();
//            }
//
//            @Override
//            public void onFailure(BaseDataBean data) {
//                Global.showToast(data.msg, ForgetPassWordActivity.this);
//            }
//
//            @Override
//            public void onError() {
//
//            }
//        });
//    }
//
//
//    //登录
//    public void changePassword() {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("phone", mPhone);
//        params.put("code", mVerifiCode);
//        params.put("password", mPassWord);
//        new BaseDataPresenter(this).loadData(DataUrl.FORGET_PASSWORD, params, getResources().getString(R.string.update_password), new IBaseDataListener() {
//            @Override
//            public void onSuccess(BaseDataBean data) {
//                Global.showToast(data.msg, ForgetPassWordActivity.this);
//                finish();
//            }
//
//            @Override
//            public void onFailure(BaseDataBean data) {
//                Global.showToast(data.msg, ForgetPassWordActivity.this);
//            }
//
//            @Override
//            public void onError() {
//
//            }
//        });
//    }


}

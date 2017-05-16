package com.coco3g.daishu.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.LoginActivity;


/**
 * Created by lisen on 16/2/16 12:11.
 */
public class LoginRegisterView extends LinearLayout implements View.OnClickListener {
    View mView;
    Context mContext;
    TextView mTxtLogin;

    public LoginRegisterView(Context context) {
        super(context);
        mContext = context;
    }

    public LoginRegisterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public LoginRegisterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void initView() {
        LayoutInflater lay = LayoutInflater.from(mContext);
        mView = lay.inflate(R.layout.view_loginregister, this);
        mTxtLogin = (TextView) mView.findViewById(R.id.tv_loginregister_view_login);
        mTxtLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_loginregister_view_login: // 登录/注册
                Intent intent = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(intent);
                break;
        }
    }
}

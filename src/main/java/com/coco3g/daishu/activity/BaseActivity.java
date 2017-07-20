package com.coco3g.daishu.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


import com.coco3g.daishu.R;
import com.coco3g.daishu.utils.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

public class BaseActivity extends Activity {

    public static ArrayList<Context> CONTEXTLIST = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        if (CONTEXTLIST == null) {
            CONTEXTLIST = new ArrayList<>();
        }
        CONTEXTLIST.add(this);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.white);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (CONTEXTLIST != null) {
            for (int i = 0; i < CONTEXTLIST.size(); i++) {
                ((Activity) CONTEXTLIST.get(i)).finish();
            }
            CONTEXTLIST.clear();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            CONTEXTLIST.remove(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

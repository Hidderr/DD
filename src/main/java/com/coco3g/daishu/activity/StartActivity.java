package com.coco3g.daishu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.webkit.CookieSyncManager;

import com.coco3g.daishu.R;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.Constants;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by jason on 2017/4/26.
 */

public class StartActivity extends BaseActivity {
    public String SHARE_APP_TAG = "first";
    boolean isFirst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if (!isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;//finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
            }
        }
        //
        SharedPreferences setting = getSharedPreferences(SHARE_APP_TAG, 0);
        Boolean user_first = setting.getBoolean("FIRST", true);
        if (user_first) {//第一次
            setting.edit().putBoolean("FIRST", false).commit();
            isFirst = false;
        } else {
            isFirst = false;
        }
        //
        CookieSyncManager.createInstance(this);
        Global.LOGIN_INFO_MAP = Global.readLoginInfo(this, Global.LOGIN_INFO);
        //
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    private void init() {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(StartActivity.this);
                config.threadPriority(Thread.NORM_PRIORITY - 2);
                config.denyCacheImageMultipleSizesInMemory();
                config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
                config.memoryCacheSize(2 * 1024 * 1024);
                config.diskCacheSize(50 * 1024 * 1024);
                config.memoryCache(new WeakMemoryCache());
                config.imageDownloader(new BaseImageDownloader(StartActivity.this, 5 * 1000, 10 * 1000));
                config.tasksProcessingOrder(QueueProcessingType.LIFO);
                config.writeDebugLogs();
                config.threadPoolSize(3);
                ImageLoader.getInstance().init(config.build());
                // 获取极光推送的ID
                Constants.JPUSH_REGISTERID = JPushInterface.getRegistrationID(StartActivity.this);
                // 获取当前应用版本号
                Global.SDK_VERSION = Global.getAndroidSDKVersion();
                // 获取屏幕尺寸
                Global.getScreenWH(StartActivity.this);
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Message mess = new Message();
                mHandler.sendMessage(mess);
            }

        }.start();

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (Global.LOGIN_INFO_MAP != null) {
                login(Global.LOGIN_INFO_MAP.get("phone"), Global.LOGIN_INFO_MAP.get("password"));
            } else {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
//                if (isFirst) {
//                    Intent intent = new Intent(StartActivity.this, GuideActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
            }
        }
    };

    /**
     * 登录
     *
     * @param phone
     * @param password
     */
    public void login(final String phone, final String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("password", password);
        params.put("uuid", Constants.JPUSH_REGISTERID);
        new BaseDataPresenter(this).loadData(DataUrl.LOGIN, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                if (data.code == 200) {
                    Global.USERINFOMAP = (Map<String, String>) data.response;
                    Global.saveLoginInfo(StartActivity.this, phone, password, Global.USERINFOMAP.get("avatar"), Global.LOGIN_INFO);
                    Global.saveLoginInfo(StartActivity.this, phone, password, Global.USERINFOMAP.get("avatar"), Global.LOGIN_INFO_LAST);
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    intent.putExtra("msgtype", getIntent().getStringExtra("msgtype"));
                    if ("2".equals(getIntent().getStringExtra("msgtype"))) {
                        intent.putExtra("value", getIntent().getIntExtra("value", 0));
                    } else {
                        intent.putExtra("value", getIntent().getStringExtra("value"));
                    }
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError() {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }
}

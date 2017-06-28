package com.coco3g.daishu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.Constants;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.net.utils.RongUtils;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by jason on 2017/4/26.
 */

public class StartActivity2 extends BaseActivity implements View.OnClickListener {
    public String SHARE_APP_TAG = "first";
    boolean isFirst = false;
    private String password;
    private TextView mTxtComeIn;
    private Timer timer;
    //
    boolean isClickComeIn;  //点击了"键入袋鼠好车"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start2);
        mTxtComeIn = (TextView) findViewById(R.id.tv_start_come_in);
        mTxtComeIn.setOnClickListener(this);
        //
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
        password = Global.readPassWord(StartActivity2.this);
        //
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    private void init() {
//        new Thread() {
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                super.run();
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(StartActivity2.this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.memoryCacheSize(2 * 1024 * 1024);
        config.diskCacheSize(50 * 1024 * 1024);
        config.memoryCache(new WeakMemoryCache());
        config.imageDownloader(new BaseImageDownloader(StartActivity2.this, 5 * 1000, 10 * 1000));
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();
        config.threadPoolSize(3);
        ImageLoader.getInstance().init(config.build());
        // 获取极光推送的ID
        Constants.JPUSH_REGISTERID = JPushInterface.getRegistrationID(StartActivity2.this);
        // 获取当前应用版本号
        Global.SDK_VERSION = Global.getAndroidSDKVersion();
        // 获取屏幕尺寸
        Global.getScreenWH(StartActivity2.this);
//        try {
//            sleep(1500);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                Message mess = new Message();
                mHandler.sendMessage(mess);
            }
        }, 2000, 2000);


//            }
//        }.start();

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (Global.LOGIN_INFO_MAP != null && !TextUtils.isEmpty(password)) {
                login(Global.LOGIN_INFO_MAP.get("phone"), password);
            } else {

                Intent intent = new Intent(StartActivity2.this, MainActivity.class);
                startActivity(intent);
                finish();
//                if (isFirst) {
//                    Intent intent = new Intent(StartActivity2.this, GuideActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Intent intent = new Intent(StartActivity2.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start_come_in:  //进入袋鼠好车
                isClickComeIn = true;
                timer.cancel();
                Intent intent = new Intent(StartActivity2.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

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
                    Global.USERINFOMAP = (Map<String, Object>) data.response;
                    Global.savePassWord(StartActivity2.this, password);
                    Global.saveLoginInfo(StartActivity2.this, phone, Global.USERINFOMAP.get("nickname") + "", password, Global.LOGIN_INFO);
                    Global.saveLoginInfo(StartActivity2.this, phone, Global.USERINFOMAP.get("nickname") + "", password, Global.LOGIN_INFO_LAST);
                    Intent intent = new Intent(StartActivity2.this, MainActivity.class);
                    startActivity(intent);
                    new RongUtils(StartActivity2.this).init();
                } else {

                    if (!isClickComeIn) {
                        Intent intent = new Intent(StartActivity2.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
                finish();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Intent intent = new Intent(StartActivity2.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError() {
                Intent intent = new Intent(StartActivity2.this, MainActivity.class);
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

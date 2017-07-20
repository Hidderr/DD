package com.coco3g.daishu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.Constants;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.net.utils.RongUtils;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.view.MySurfaceHolder;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
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
 * Created by lisen on 16/2/4.
 */
@SuppressWarnings("ALL")
public class StartActivity extends Activity {
    RelativeLayout mRelativeVideoBg;
    SurfaceView mSurfaceVideo;
    MySurfaceHolder mSurefaceHolder;
    //
    boolean intoMain = false;
    DisplayImageOptions options;
    //
    int mTimeCount = 5; // 倒计时5s
    Timer mTimer;
    TimerTask mTask;
    private Timer timer;

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
//        Global.LOGIN_INFO_MAP = Global.readLoginInfo(this,Global.LOGIN_INFO_LAST); // 获取保存的登录信息
//        SharedPreferences setting = getSharedPreferences(SHARE_APP_TAG, 0);
//        Boolean user_first = setting.getBoolean("FIRST", true);
//        if (user_first) {//第一次
//            setting.edit().putBoolean("FIRST", false).commit();
//            isFirst = true;
//        } else {
//            isFirst = false;
//        }
        //
        mRelativeVideoBg = (RelativeLayout) findViewById(R.id.relative_start_video_bg);
        mSurfaceVideo = (SurfaceView) findViewById(R.id.surface_start);
        //
        mSurefaceHolder = new MySurfaceHolder(this, mSurfaceVideo);
        mSurfaceVideo.getHolder().setKeepScreenOn(true);
        mSurfaceVideo.getHolder().addCallback(mSurefaceHolder);
        //
        CookieSyncManager.createInstance(this);
        Global.LOGIN_INFO_MAP = (HashMap<String, String>) Global.readSerializeData(this, Global.LOGIN_INFO);
        Global.LOGIN_OPENID = (String) Global.readSerializeData(this, Global.LOGIN_INFO_OPENID);
        //
        init();
    }

    private void init() {
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
        //
        if (Global.LOGIN_INFO_MAP != null && !TextUtils.isEmpty(Global.LOGIN_INFO_MAP.get("password"))) {
            login(Global.LOGIN_INFO_MAP.get("phone"), Global.LOGIN_INFO_MAP.get("password"));
        } else if (!TextUtils.isEmpty(Global.LOGIN_OPENID)) {
            threeOtherLogin();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                Message mess = new Message();
                mHandler.sendMessage(mess);
            }
        }, 3000, 3000);

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
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
                    Global.USERINFOMAP = (Map<String, Object>) data.response;
//                    Global.serializeData(StartActivity.this, Global.USERINFOMAP, Global.LOGIN_INFO);
//                    Global.saveLoginInfo(StartActivity.this, phone, Global.USERINFOMAP.get("nickname") + "", password, Global.LOGIN_INFO);
//                    Global.saveLoginInfo(StartActivity.this, phone, Global.USERINFOMAP.get("nickname") + "", password, Global.LOGIN_INFO_LAST);
                    //
                    new RongUtils(StartActivity.this).init();
//                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
//                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(BaseDataBean data) {
//                Intent intent = new Intent(StartActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
            }

            @Override
            public void onError() {
//                Intent intent = new Intent(StartActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
            }
        });

    }

    //第三方登录与后台进行绑定
    public void threeOtherLogin() {
        HashMap<String, String> params = new HashMap<>();
        params.put("qqkey", Global.LOGIN_OPENID);
        params.put("uuid", Constants.JPUSH_REGISTERID);
        new BaseDataPresenter(this).loadData(DataUrl.REGISTER, params, getResources().getString(R.string.login_loading), new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                if (data.response == null) {
                    return;
                }
                Global.USERINFOMAP = (Map<String, Object>) data.response;
                Global.serializeData(StartActivity.this, Global.LOGIN_OPENID, Global.LOGIN_INFO_OPENID);
//                Intent intent = new Intent(StartActivity.this, MainActivity.class);
//                startActivity(intent);
                //连接融云
                new RongUtils(StartActivity.this).init();
//                finish();

            }

            @Override
            public void onFailure(BaseDataBean data) {
//                Intent intent = new Intent(StartActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
            }

            @Override
            public void onError() {
//                Intent intent = new Intent(StartActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}

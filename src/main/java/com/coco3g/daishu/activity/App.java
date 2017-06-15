package com.coco3g.daishu.activity;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.coco3g.daishu.bean.ChatItemDataBean;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.widget.provider.TextMessageItemProvider;
import io.rong.message.TextMessage;


/**
 * Created by lisen on 16/2/18 18:56.
 */
public class App extends MultiDexApplication {

    private ChatItemDataBean mChatItemDataBean;

    public ChatItemDataBean getmChatItemDataBean() {
        return mChatItemDataBean;
    }

    public void setmChatItemDataBean(ChatItemDataBean mChatItemDataBean) {
        this.mChatItemDataBean = mChatItemDataBean;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 1);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.memoryCacheSize(10 * 1024 * 1024);
        config.diskCacheSize(100 * 1024 * 1024);
        config.diskCacheFileCount(100);
        config.memoryCache(new WeakMemoryCache());
        config.imageDownloader(new BaseImageDownloader(this));
        config.tasksProcessingOrder(QueueProcessingType.FIFO);
        config.writeDebugLogs();
        config.threadPoolSize(5);
        ImageLoader.getInstance().init(config.build());
        //

       /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第一步 初始化
             */
            RongIM.init(this);

        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    @ProviderTag(messageContent = TextMessage.class, showPortrait = true, showSummaryWithName = true)
    public class MyTextMessageItemProvider extends TextMessageItemProvider {

    }


}

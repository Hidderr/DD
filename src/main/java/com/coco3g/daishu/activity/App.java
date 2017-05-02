package com.coco3g.daishu.activity;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;


/**
 * Created by lisen on 16/2/18 18:56.
 */
public class App extends Application {

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
        Log.e("打印SHA1", sHA1(this));

//        /**
//         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
//         * io.rong.push 为融云 push 进程名称，不可修改。
//         */
//        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
//
//            /**
//             * IMKit SDK调用第一步 初始化
//             */
//            RongIM.init(this);
//            RongIM.getInstance().registerMessageTemplate(new MyTextMessageItemProvider());
//            new RongUtils(this).setRongConnectStateListener();
//
//        }
    }


//    /**
//     * 获得当前进程的名字
//     *
//     * @param context
//     * @return 进程号
//     */
//    public static String getCurProcessName(Context context) {
//
//        int pid = android.os.Process.myPid();
//
//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//
//        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
//
//            if (appProcess.pid == pid) {
//                return appProcess.processName;
//            }
//        }
//        return null;
//    }
//
//    @ProviderTag(messageContent = TextMessage.class, showPortrait = true, showSummaryWithName = true)
//    public class MyTextMessageItemProvider extends TextMessageItemProvider {
//
//    }
//





    //获取高德的sha1值 ，没用的可以删去
    public String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }





}

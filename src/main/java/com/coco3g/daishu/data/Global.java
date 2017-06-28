package com.coco3g.daishu.data;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.BaseActivity;
import com.coco3g.daishu.activity.LoginActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Global {
    public static final String rootPath = "com_app_daishudashi";
    public static final String APP_CACHE = rootPath + "data"; // 用户信息
    public static final String LOGIN_INFO = rootPath + "login"; // 用户登录信息
    public static final String LOGIN_INFO_LAST = rootPath + "loginlastperson"; // 最后一位用户登录信息
    public static final String LOGIN_PASSWORD = rootPath + "loginpassword"; // 登录密码
    public static final String RONGTOKEN_INFO = rootPath + "rongtokeninfo"; // 融云token保存

    public static final String localThumbPath = "thumbnail"; // 应用的图片存放目录
    public static final String DOWNLOAD = "download"; // 应用的图片下载目录
    public static final String START_PIC = "qymtemp"; // 启动界面存储目录
    public static final String localVideoPath = "video"; // 应用的视频存放目录
    public static final String localAudioPath = "audio"; // 应用的音频存放目录
    public static String NOTICE_MSG_TYPE = ""; // 通知下发，携带的数据
    public static Map<String, Object> USERINFOMAP = null;  //用户信息
    public static Map<String, String> H5Map = null;  //所有的h5
    //当前定位的数据
    public static String locationCity = ""; // 定位城市
    public static double mCurrLat = 0; // 定位城市纬度
    public static double mCurrLng = 0; // 定位城市经度
    //
    public static Context MAINACTIVITY_CONTEXT = null;  //当前MainActivity的上下文

    /**
     * 融云token
     **/
    public static String RONG_TOKEN = "";

    //
    /**
     * height为高的一边，width为低的一边
     **/
    public static int screenHeight, screenWidth, topbarHeight;
    static FileOutputStream fos;
    static ObjectOutputStream oos;
    static FileInputStream fis = null;
    static ObjectInputStream ois = null;
    public static String MODEL = "";
    public static String IMEI = "";
    /**
     * SDK版本号
     **/
    public static int SDK_VERSION = 0;
    public static HashMap<String, String> LOGIN_INFO_MAP = null;
    //
    public final static int RESULT_CHECK_GALLERY_CODE = 1; // 系统相册中，选择的图片后，返回的code
    public static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 10; //相册返回code
    public static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 11; // 相机拍照返回code
    public static final int REQUEST_CODE_GETIMAGE_BYCROP = 12; //图片裁剪返回code
    public static final int REFRESH_DATA = 103; // 返回后数据刷新操作
    public static final int REFRESH_PHOTOWALL_BG = 104; // 返回刷新照片墙背景
    public static final int RESULT_LOCATION = 105; // 返回定位位置
    public static final int RESULT_RECHARGE_COMPLETE = 106; // 充值成功后返回
    public static final int RESULT_DELETE_PARTY = 107; // 取消活动后返回
    public static final int RESULT_SEND_DIY_LANGUAGE = 108; // 发送特色语言
    //

    /**
     * 获取当前SDK版本号
     *
     * @return
     */
    public static int getAndroidSDKVersion() {
        int version = 0;
        try {
            version = Build.VERSION.SDK_INT;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     *
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    /**
     * 获取屏幕长宽
     *
     * @param context
     */
    public static void getScreenWH(Context context) {
        WindowManager w = ((Activity) context).getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) try {
            screenHeight = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        else if (Build.VERSION.SDK_INT >= 17) {
            try {
                android.graphics.Point realSize = new android.graphics.Point();
                Display.class.getMethod("getRealSize", android.graphics.Point.class).invoke(d, realSize);
                screenHeight = realSize.y;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.e("w-h", screenWidth + "--" + screenHeight);
    }

    /**
     * 获取通知栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 获取当前屏幕的长宽比
     *
     * @param a
     * @param b
     * @return
     */
    public static String getScreenRatio(int a, int b) {
        int min;
        int temp = 1;
        min = (a < b) ? a : b;
        for (int i = 1; i <= min; i++) {
            if (a % i == 0 && b % i == 0) temp = i;
        }
        int a_ = a / temp;
        int b_ = b / temp;
        return a_ + ":" + b_;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int pxTodip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dipTopx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

//    /**
//     * 获取IMEI号，IESI号，手机型号
//     */
//    public static void getInfo(Context context) {
//        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        String imei = mTm.getDeviceId();
//        String imsi = mTm.getSubscriberId();
//        String mtype = Build.MODEL; // 手机型号
//        String mtyb = Build.BRAND;// 手机品牌
//        String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
//        IMEI = imei;
//        Log.i("text", "手机IMEI号:" + imei + " 手机IESI号:" + imsi + " 手机型号:" + mtype + " 手机品牌:" + mtyb + " 手机号码:" + numer);
//        MODEL = "手机IMEI号:" + imei + " 手机IESI号:" + imsi + " 手机型号:" + mtype + " 手机品牌:" + mtyb + " 手机号码:" + numer + " MAC:" +
//                getMacAddress(context);
//    }

//    /**
//     * 获取手机MAC地址 只有手机开启wifi才能获取到mac地址
//     */
//    public static String getMacAddress(Context context) {
//        String result = "";
//        try {
//            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//            result = wifiInfo.getMacAddress();
//            Log.i("text", " MAC:" + result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
//        return result;
//    }

    /**
     * 判断是否连接网络
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 获取应用根目录
     *
     * @param c
     * @return
     */
    public static String getPath(Context c) {
        String path = null;
        if (c == null) return null;
        File file = null;
        if (isSdcardInsert()) {
            File sdDir = Environment.getExternalStorageDirectory();
            path = sdDir.toString() + File.separator + rootPath;
        } else {
            path = c.getFilesDir().getPath() + File.separator + rootPath;
        }
        file = new File(path);
        if (!file.exists()) file.mkdirs();
        return path;
    }

    /**
     * 判断是否有SD卡
     *
     * @return
     */
    public static boolean isSdcardInsert() {
        boolean isInsert = false;
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            isInsert = false;
        } else isInsert = true;
        return isInsert;
    }

    /**
     * 获取应用版本号
     *
     * @return
     * @throws Exception
     */
    public static int getAppVersionCode(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (packInfo == null) {
            return Integer.MAX_VALUE;
        }
        return packInfo.versionCode;
    }

    /**
     * 获取应用版本号
     *
     * @return
     * @throws Exception
     */
    public static String getAppVersion(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (packInfo == null) {
            return "";
        }
        return "v" + packInfo.versionName;
    }

    /**
     * 从SD卡中删除文件
     *
     * @param path
     * @return
     */
    public static boolean deleteFileFromSD(String path) {
        if (path == null) {
            return false;
        }
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        return file.delete();
    }

    @SuppressLint("NewApi")
    public static int getRealScreenSize(boolean isWidth, Activity act) {
        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = act.getWindowManager().getDefaultDisplay();
        if (hasPermanentMenuKey(act)) {
            act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels; // 屏幕宽度（像素）
            int height = metrics.heightPixels; // 屏幕高度（像素）

            if (isWidth) return width;
            else return height;
        } else {
            Method mGetRawH = null, mGetRawW = null;

            // Not real dimensions
            display.getMetrics(metrics);
            int width = metrics.heightPixels;
            int height = metrics.widthPixels;

            try {
                // For JellyBeans and onward
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    display.getRealMetrics(metrics);

                    // Real dimensions
                    width = metrics.heightPixels;
                    height = metrics.widthPixels;
                } else {
                    mGetRawH = Display.class.getMethod("getRawHeight");
                    mGetRawW = Display.class.getMethod("getRawWidth");

                    try {
                        width = (Integer) mGetRawW.invoke(display);
                        height = (Integer) mGetRawH.invoke(display);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            } catch (NoSuchMethodException e3) {
                e3.printStackTrace();
            }

            if (isWidth) {
                return width;
            } else {
                return height;
            }
        }
    }

    public static boolean hasPermanentMenuKey(Context con) {
        ViewConfiguration configuration = ViewConfiguration.get(con);
        try {
            Object result = configuration.getClass().getMethod("hasPermanentMenuKey", new Class[]{}).invoke(configuration, new
                    Object[]{});

            if (result != null && result instanceof Boolean)
                return Boolean.parseBoolean(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 将数据序列化到本地
     *
     * @param context
     * @param hm
     */
    public static void serializeData(Context context, Object hm, String dir) {
        String path = context.getFilesDir().getPath() + File.separator + dir;
        File f = new File(path);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(hm);
            oos.flush();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取序列化数据
     *
     * @param context
     * @return
     */
    public static Object readSerializeData(Context context, String dir) {
        String path = context.getFilesDir().getPath() + File.separator + dir;
        Object o = new Object();
        File f = new File(path);
        if (f.exists()) {
            try {
                fis = new FileInputStream(f);
                ois = new ObjectInputStream(fis);
                o = ois.readObject();
                fis.close();
                ois.close();
                return o;
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (StreamCorruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    /**
     * 保存应用中的轻量级数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveTempData(Context context, String key, String value) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences("daling_temp_data", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 读取应用中的轻量级数据
     *
     * @param context
     * @param key
     * @return
     */
    public static String readTempData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("daling_temp_data",
                Activity.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, "");
        return value;
    }

    /**
     * 验证登录
     *
     * @param
     */
    public static boolean verifyLogin() {
        if (Global.USERINFOMAP == null) { // 还未登录
            return false;
        } else {
            return true;
        }
    }

    /**
     * 验证手机号的合法性
     *
     * @param phone
     * @return
     */
    public static boolean verifyPhone(String phone) {
        String rule = "^1(3|5|7|8|4)\\d{9}";
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);
        if (!m.matches()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 保存登录信息
     *
     * @param context
     * @param username
     * @param password
     */
    public static void saveLoginInfo(Context context, String phone, String username, String password, String dir) {
        HashMap<String, String> loginmap = new HashMap<String, String>();
        loginmap.put("phone", phone);
        loginmap.put("password", password);
        loginmap.put("name", username);
        serializeData(context, loginmap, dir);
    }

    /**
     * 读取登录信息
     *
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")

    public static HashMap<String, String> readLoginInfo(Context context, String dir) {
        return (HashMap<String, String>) readSerializeData(context, dir);
    }

    /*
    * 保存登录密码
    * */
    public static void savePassWord(Context context, String password) {
        serializeData(context, password, LOGIN_PASSWORD);
    }

    /*
    * 读取登录密码
    * */
    public static String readPassWord(Context context) {
        return (String) readSerializeData(context, LOGIN_PASSWORD);
    }


    /**
     * 删除序列化
     *
     * @param context
     * @param dir
     */
    public static void deleteSerializeData(Context context, String dir) {
        String path = context.getFilesDir().getPath() + File.separator + dir;
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
    }

    /**
     * 清空文件夹
     *
     * @param file
     */
    public static void deleteDir(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteDir(files[i]);
                }
            }
            file.delete();
        } else {

        }
    }

    /**
     * 获取文件夹大小
     *
     * @param f
     * @return
     */
    public static long getFileSize(File f) {
        if (!f.exists()) {
            return 0;
        }
        long size = 0;
        File flist[] = f.listFiles();
        if (flist != null && flist.length > 0) {
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFileSize(flist[i]);
                } else {
                    size = size + flist[i].length();
                }
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 获取多媒体目录
     *
     * @param context
     * @param filedir
     * @param filename
     * @return
     */
    public static String getDirPath(Context context, String filedir, String filename) {
        File file1 = new File(getPath(context) + File.separator + filedir);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        File file2 = new File(file1.getAbsolutePath() + File.separator + filename);
        if (!file2.exists()) {
            try {
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file2.getAbsolutePath();
    }

    public static String durationToTime(long duration) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm′ss″");
        String hms = formatter.format(duration - 8 * 60 * 60 * 1000);
        return hms;
    }

    static Toast toast;

    public static void showToast(String content, Context con) {
//        if (!TextUtils.isEmpty(text)) {
//            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
//        }
        if (con == null) {
            return;
        }
        if (content == null) {
            content = "null";
        }
        if (toast == null) {
            toast = new Toast(con);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        RelativeLayout relative = new RelativeLayout(con);
        relative.setBackgroundResource(R.mipmap.pic_toast_bg);
        TextView textView = new TextView(con);
        textView.setText(content);
        textView.setTextSize(15f);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        relative.addView(textView, lp);

        toast.setView(relative);
        toast.show();
    }

    /**
     * 根据文件绝对路径获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName
     * @return
     */
    public static String getFileFormat(String fileName) {
        if (TextUtils.isEmpty(fileName)) return "";

        int point = fileName.lastIndexOf('.');
        return fileName.substring(point + 1);
    }

    public static String convertToThumb(String url) {
        if (url == null) {
            return "";
        }
        String newurl;
        if (url.contains(".")) {
            try {
                int index = url.lastIndexOf(".");
                newurl = url.substring(0, index) + "_1" + url.substring(index);
                return newurl;
            } catch (Exception e) {
                e.printStackTrace();
                return url;
            }

        }
        return url;
    }

    /**
     * 元转换成分
     *
     * @param source
     * @return
     */
    public static int currencyConvert(String source) {
        float sourceF = Float.parseFloat(source);
        DecimalFormat df = new DecimalFormat("#.00");
        float sourceS = Float.parseFloat(df.format(sourceF));
        int target = (int) (sourceS * 100);
        return target;
    }

    /**
     * MD5加密
     *
     * @param info
     * @return
     */
    public static String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 清除cookie
     *
     * @param context
     */
    public static void clearCookie(Context context) {
        CookieSyncManager.createInstance(context.getApplicationContext());
        CookieManager.getInstance().removeAllCookie();
    }

    public static HashMap<String, String> parseCustomUrl(String value) {
        //未经过解码的网址  http://coco3g-app/confirm?title=%E7%B3%BB%E7%BB%9F%E6%8F%90%E7%A4%BA&message=%E7%A1%AE%E5%AE%9A%E8%A6%81%E5%8F%91%E5%87%BA%E6%B1%82%E8%81%8C%E5%90%97%EF%BC%9F&callbackTag=callback_1489061410511&
        //经过解码的网址  http://coco3g-app/confirm?title=系统提示&message=确定要发出求职吗？&callbackTag=callback_1489060445226&
        int lastposition = value.lastIndexOf("?");
        String url = value.substring(lastposition + 1);   //此处的url是截取后的所有的键值对

        HashMap<String, String> hashmap = new HashMap<String, String>();
        if (!TextUtils.isEmpty(url)) {
            String[] str1 = url.split("&");
            if (str1 != null && str1.length > 0) {
                for (String keyvalue : str1) {
                    if (keyvalue.contains("=")) {
                        String[] str2 = keyvalue.split("=");
                        if (str2 != null && str2.length == 2) {
                            hashmap.put(str2[0], str2[1]);
                        }
                    }
                }
            }
        }
        return hashmap;
    }


    public static void playAudio(Context context) {
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getAssets().openFd("ring_1.mp3");
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 半角转全角
     *
     * @param input
     * @return
     */
    public static String toSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    //判断是否有网络连接
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 拨打电话
     *
     * @param context
     * @param number
     */
    public static void callPhone(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + number);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Global.showToast("没有相关权限", context);
            return;
        }
        context.startActivity(intent);
    }


    //将view转换为bitmap
    public static Bitmap getViewBitmap(View comBitmap, int width, int height) {
        Bitmap bitmap = null;
        if (comBitmap != null) {
            comBitmap.clearFocus();
            comBitmap.setPressed(false);

            boolean willNotCache = comBitmap.willNotCacheDrawing();
            comBitmap.setWillNotCacheDrawing(false);

            // Reset the drawing cache background color to fully transparent
            // for the duration of this operation
            int color = comBitmap.getDrawingCacheBackgroundColor();
            comBitmap.setDrawingCacheBackgroundColor(0);
            float alpha = comBitmap.getAlpha();
            comBitmap.setAlpha(1.0f);

            if (color != 0) {
                comBitmap.destroyDrawingCache();
            }

            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            comBitmap.measure(widthSpec, heightSpec);
            comBitmap.layout(0, 0, width, height);

            comBitmap.buildDrawingCache();
            Bitmap cacheBitmap = comBitmap.getDrawingCache();
            if (cacheBitmap == null) {
                Log.e("view.ProcessImageToBlur", "failed getViewBitmap(" + comBitmap + ")",
                        new RuntimeException());
                return null;
            }
            bitmap = Bitmap.createBitmap(cacheBitmap);
            // Restore the view
            comBitmap.setAlpha(alpha);
            comBitmap.destroyDrawingCache();
            comBitmap.setWillNotCacheDrawing(willNotCache);
            comBitmap.setDrawingCacheBackgroundColor(color);
        }
        return bitmap;
    }


    public static void realeaseData(Context context) {
        try {
            Global.screenWidth = 0;
            Global.screenHeight = 0;
            Global.USERINFOMAP = null;
            Global.IMEI = null;
            Global.MODEL = null;
            BaseActivity.CONTEXTLIST.clear();
            BaseActivity.CONTEXTLIST = null;
            Global.deleteSerializeData(context, Global.APP_CACHE); //清除token
            Global.deleteSerializeData(context, Global.LOGIN_INFO); //清除token
            Global.deleteSerializeData(context, Global.LOGIN_PASSWORD); //清除token
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //获取token和时间戳
    public static HashMap<String, String> getTokenTimeStampHeader(Context context) {
        HashMap<String, String> header = new HashMap<>();
        header.put("token", (String) (Global.readSerializeData(context, Global.APP_CACHE)));
        header.put("timestamp", System.currentTimeMillis() / 1000 + "");
        return header;
    }

    public static boolean checkoutLogin(Context context) {
        if (Global.USERINFOMAP == null) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            return false;
        }
        return true;
    }


    public static void logout(Context mContext) {
        Global.USERINFOMAP = null;
        Global.deleteSerializeData(mContext, Global.APP_CACHE);
        Global.deleteSerializeData(mContext, Global.LOGIN_PASSWORD);  //删除登录密码
        Global.deleteSerializeData(mContext, Global.LOGIN_INFO);  //删除个人信息
        Global.deleteSerializeData(mContext, Global.RONGTOKEN_INFO);  //删除融云的token
//        new RongUtils(mContext).disConnect();
        //
        // 除cookie
        Global.clearCookie(mContext);
//        ((Activity) mContext).finish();
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);
    }


}

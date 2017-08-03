package com.coco3g.daishu.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.fragment.GoodsFragment;
import com.coco3g.daishu.fragment.HomeFragment;
import com.coco3g.daishu.fragment.IncomeFragment;
import com.coco3g.daishu.fragment.MeFragment;
import com.coco3g.daishu.fragment.RepairFragment;
import com.coco3g.daishu.net.utils.VersionUpdateUtils;
import com.coco3g.daishu.utils.Coco3gBroadcastUtils;
import com.coco3g.daishu.utils.LocationUtil;
import com.coco3g.daishu.utils.RequestPermissionUtils;
import com.coco3g.daishu.view.BottomNavImageView;
import com.coco3g.daishu.view.TopBarView;

import java.util.Timer;
import java.util.TimerTask;

import io.rong.imlib.RongIMClient;


public class MainActivity extends BaseFragmentActivity implements View.OnClickListener {
    private TopBarView mTopbar;
    //底部导航标题
    BottomNavImageView[] mImageRes;
    BottomNavImageView mImageHome, mImageRead, mImageIncome, mImageShop, mImageMe;
    String[] mNavTitles;
    int[] mNavIconResID = new int[]{R.drawable.nav_home_icon, R.drawable.nav_goods_icon, R.drawable.nav_income_icon, R.drawable.nav_repair_icon,
            R.drawable.nav_me_icon};
    private static FragmentManager mFragManager = null;
    //
    private static HomeFragment mHomeFrag;
    private GoodsFragment mGoodsFrag;
    private IncomeFragment mIncomeFrag;
    private RepairFragment mRepairFrag;
    private MeFragment mMeFrag;
    //
    public static boolean isForeground = false;
    int mCurrNavIndex = -1;  //目前选中的地步导航的哪一个
    //点击返回键退出app
    private static Boolean isExit = false;
    Coco3gBroadcastUtils mLogoutBroadcast, CurrBroadcast, CurrUnreadBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Global.MAINACTIVITY_CONTEXT != null) {
//            finish();
//        }
        setContentView(R.layout.activity_main);
//        Global.MAINACTIVITY_CONTEXT = this;
        initView();
        startLocation();
    }

    private void initView() {
        mTopbar = (TopBarView) findViewById(R.id.topbar_main);
        mTopbar.hideLeftView();
        mTopbar.setTitle(getResources().getString(R.string.app_name));
        mTopbar.setOnHomeSearchListener(new TopBarView.OnHomeSearchListener() {
            @Override
            public void homeSearch(String searchKey) {
//                Log.e("searchkey", searchKey);
                if (TextUtils.isEmpty(searchKey)) {
                    Global.showToast("搜索内容为空", MainActivity.this);
                } else {
                    Intent intent = new Intent(MainActivity.this, CarCategoryListActivity.class);
                    intent.putExtra("fromType", 1);
                    intent.putExtra("searchKey", searchKey);
                    startActivity(intent);
                }
            }
        });
        //
        mImageHome = (BottomNavImageView) findViewById(R.id.view_nav_home);
        mImageRead = (BottomNavImageView) findViewById(R.id.view_nav_read);
        mImageIncome = (BottomNavImageView) findViewById(R.id.view_nav_income);
        mImageShop = (BottomNavImageView) findViewById(R.id.view_nav_shop);
        mImageMe = (BottomNavImageView) findViewById(R.id.view_nav_me);
        //
        mNavTitles = new String[]{getString(R.string.nav_title_home), getString(R.string.nav_title_city_partner), getString(R.string.nav_title_income), getString(R.string.nav_title_shop),
                getString(R.string.nav_title_me)};
        mImageRes = new BottomNavImageView[]{mImageHome, mImageRead, mImageIncome, mImageShop, mImageMe};
        for (int i = 0; i < mImageRes.length; i++) {
            mImageRes[i].setIcon(mNavIconResID[i], mNavTitles[i]);
        }
        //
        mImageHome.setOnClickListener(this);
        mImageRead.setOnClickListener(this);
        mImageIncome.setOnClickListener(this);
        mImageShop.setOnClickListener(this);
        mImageMe.setOnClickListener(this);
        //
        mFragManager = getSupportFragmentManager();
        setTabSelection(0);
        // 退出登录后返回到首页
        mLogoutBroadcast = new Coco3gBroadcastUtils(MainActivity.this);
        mLogoutBroadcast.receiveBroadcast(Coco3gBroadcastUtils.LOG_OUT).setOnReceivebroadcastListener(new Coco3gBroadcastUtils.OnReceiveBroadcastListener() {
            @Override
            public void receiveReturn(Intent intent) {
                setTabSelection(0);
            }
        });

        // 接收融云未读消息（私聊）的广播
        CurrUnreadBroadcast = new Coco3gBroadcastUtils(MainActivity.this);
        CurrUnreadBroadcast.receiveBroadcast(Coco3gBroadcastUtils.RONG_UNREAD_MSG).setOnReceivebroadcastListener(new Coco3gBroadcastUtils.OnReceiveBroadcastListener() {
            @Override
            public void receiveReturn(Intent intent) {
                Bundle bundle = intent.getBundleExtra("data");
                int count = bundle.getInt("unreadcount");
                mTopbar.setUnReadCount(count);
            }
        });
        //
        CurrBroadcast = new Coco3gBroadcastUtils(this);
        CurrBroadcast.receiveBroadcast(Coco3gBroadcastUtils.RONGIM_DISCONNECTION_FLAG).setOnReceivebroadcastListener(new Coco3gBroadcastUtils.OnReceiveBroadcastListener() {
            @Override
            public void receiveReturn(Intent intent) {
                Bundle b = intent.getBundleExtra("data");
                if (b == null) {
                    return;
                }
                int connectionState = b.getInt("connection_state");
                if (connectionState == RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT.getValue()) { // 用户账户在其他设备登录，被踢掉线
//                    showState(true, "用户账户在其他设备登录，点击重连");
                    Global.showToast("用户账户在其他设备登录", MainActivity.this);
                    Global.logout(MainActivity.this);
                    mTopbar.setUnReadCount(0);
                    setTabSelection(0);
                } else if (connectionState == RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE.getValue()) { // 网络不可用
//                    if (mMessageFragment != null) {
////                        mMessageFragment.updateChatList();
//                    }
                } else if (connectionState == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED.getValue()) { // 成功
//                    if (mMessageFragment != null) {
////                        mMessageFragment.updateChatList();
//                    }
                }
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        isForeground = true;
        // 检查新版本
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new VersionUpdateUtils(MainActivity.this).checkUpdate(false, ""); // 检查最新版本
            }
        }, 500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_nav_home: //首页
                setTabSelection(0);
                break;
            case R.id.view_nav_read://阅读
                setTabSelection(1);
                break;
            case R.id.view_nav_income:// 收益
                setTabSelection(2);
                break;
            case R.id.view_nav_shop:// 商城
                setTabSelection(3);
                break;
            case R.id.view_nav_me://我
                setTabSelection(4);
                break;
        }
    }


    private void setTabSelection(int index) {
        if (mCurrNavIndex == index) {
            return;
        }
        FragmentTransaction transaction = mFragManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        Bundle b = new Bundle();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_in_right);
        switch (index) {
            case 0: // 首页
//                mTopbar.showHomeTopbar();
                mTopbar.setSettingVisible(false);
                mTopbar.showJiangLi(false);
                mTopbar.setVisibility(View.GONE);
                if (mHomeFrag == null) {
                    mHomeFrag = (HomeFragment) new HomeFragment();
                    transaction.add(R.id.frame_main_content, mHomeFrag);
                    mHomeFrag.setOnRepairClickListener(new HomeFragment.OnRepairClickListener() {
                        @Override
                        public void repairClick() {
                            setTabSelection(3);
                        }
                    });
                } else {
                    transaction.show(mHomeFrag);
                }
                break;
            case 1: // 商品汇
                mTopbar.showNomalTopbar();
                mTopbar.setTitle(getResources().getString(R.string.nav_title_city_partner));
                mTopbar.setSettingVisible(false);
                mTopbar.showJiangLi(false);
                mTopbar.setVisibility(View.VISIBLE);
                if (mGoodsFrag == null) {
                    mGoodsFrag = new GoodsFragment();
                    transaction.add(R.id.frame_main_content, mGoodsFrag);
                } else {
                    transaction.show(mGoodsFrag);
                }
                break;
            case 2: // 收益
                if (!checkoutIfLogin()) {
                    return;
                }
                mTopbar.showNomalTopbar();
                mTopbar.setTitle(getResources().getString(R.string.nav_title_income));
                mTopbar.setSettingVisible(false);
                mTopbar.showJiangLi(true);
                mTopbar.setVisibility(View.VISIBLE);
                if (mIncomeFrag == null) {
                    mIncomeFrag = new IncomeFragment();
                    transaction.add(R.id.frame_main_content, mIncomeFrag);
                } else {
                    transaction.show(mIncomeFrag);
                }
                break;
            case 3: // 维修救援
//                if (!checkoutIfLogin()) {
//                    return;
//                }
                mTopbar.showNomalTopbar();
                mTopbar.setTitle(getResources().getString(R.string.nav_title_shop));
                mTopbar.setSettingVisible(false);
                mTopbar.showJiangLi(false);
                mTopbar.setVisibility(View.VISIBLE);
                if (mRepairFrag == null) {
                    mRepairFrag = new RepairFragment();
                    transaction.add(R.id.frame_main_content, mRepairFrag);
                } else {
                    transaction.show(mRepairFrag);
                    mRepairFrag.startLocation(false);
                }
                break;
            case 4: // 我的
//                if (!checkoutIfLogin()) {
//                    return;
//                }
                mTopbar.showNomalTopbar();
                mTopbar.setTitle(getResources().getString(R.string.personal_center));
                mTopbar.setSettingVisible(true);
                mTopbar.showJiangLi(false);
                mTopbar.setVisibility(View.VISIBLE);
                if (mMeFrag == null) {
                    mMeFrag = new MeFragment();
                    transaction.add(R.id.frame_main_content, mMeFrag);
                } else {
                    transaction.show(mMeFrag);
                }
                break;
        }
        transaction.commitAllowingStateLoss();
        //
        mCurrNavIndex = index;
        int count = mImageRes.length;
        for (int i = 0; i < count; i++) {
            if (i == index) {
                mImageRes[i].setSelected(i, true);
            } else {
                mImageRes[i].setSelected(i, false);
            }
        }
    }

    //隐藏所有fragment
    private void hideFragments(FragmentTransaction transaction) {
        if (mHomeFrag != null) {
            transaction.hide(mHomeFrag);
        }
        if (mGoodsFrag != null) {
            transaction.hide(mGoodsFrag);
        }
        if (mIncomeFrag != null) {
            transaction.hide(mIncomeFrag);
        }
        if (mRepairFrag != null) {
            transaction.hide(mRepairFrag);
        }
        if (mMeFrag != null) {
            transaction.hide(mMeFrag);
        }
    }


    //检查是否登录
    public boolean checkoutIfLogin() {
        if (Global.USERINFOMAP == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return false;
        }
        return true;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        requestPermission(); // 申请相关权限
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 申请相关权限
     */
    private void requestPermission() {
        new RequestPermissionUtils(this).aleraPermission(Manifest.permission.CAMERA, 1);
        new RequestPermissionUtils(this).aleraPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 1);
        new RequestPermissionUtils(this).aleraPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
        new RequestPermissionUtils(this).aleraPermission(Manifest.permission.ACCESS_FINE_LOCATION, 1);
        new RequestPermissionUtils(this).aleraPermission(Manifest.permission.READ_PHONE_STATE, 1);
        new RequestPermissionUtils(this).aleraPermission(Manifest.permission.CALL_PHONE, 1);
    }


    //定位
    public void startLocation() {
        new RequestPermissionUtils(this).aleraPermission(Manifest.permission.ACCESS_FINE_LOCATION, 1);
        //定位
        new LocationUtil(this).initLocationAndStart(true, 1000, false, null).setAMapLocationChanged(new LocationUtil.AMapLocationChanged() {
            @Override
            public void aMapLocation(AMapLocation aMapLocation) {
                Global.locationCity = aMapLocation.getCity();
                Global.mCurrLat = aMapLocation.getLatitude();
                Global.mCurrLng = aMapLocation.getLongitude();
                //
//                mTopbar.setLocationCity(Global.locationCity);
                mHomeFrag.mTxtLocation.setText(Global.locationCity);
                Log.e("定位结果", "city " + Global.locationCity + "  mCurrLat   " + Global.mCurrLat + "  mCurrLng" + Global.mCurrLng);
            }
        });
    }

    public static void setMycarTuiSongInfo(String subtitle1, String subtitle2) {
        if (mHomeFrag != null) {
            mHomeFrag.setMycarTuiSongInfo(subtitle1, subtitle2);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitAPPFromBack();
        }
        return true;
    }

    /**
     * 双击退出函数
     */
    private void exitAPPFromBack() {
        Timer timer = null;
        TimerTask timerTask = null;
        if (isExit == false) {
            isExit = true;
            Toast.makeText(this, getResources().getString(R.string.exit_app_tip), Toast.LENGTH_SHORT).show();
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            };
            timer.schedule(timerTask, 2000);
        } else {
            realeaseData();
            finish();
        }
    }

    private void realeaseData() {
        try {
            Global.screenWidth = 0;
            Global.screenHeight = 0;
            Global.USERINFOMAP = null;
            Global.IMEI = null;
            Global.MODEL = null;
            BaseActivity.CONTEXTLIST.clear();
            BaseActivity.CONTEXTLIST = null;
//            Global.MAINACTIVITY_CONTEXT = null;
            Global.deleteSerializeData(this, Global.APP_CACHE); //清除token
            mHomeFrag.onDestroy();
            mHomeFrag = null;
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isForeground = false;
        if (mLogoutBroadcast != null) {
            mLogoutBroadcast.unregisterBroadcast();
        }
        if (CurrBroadcast != null) {
            CurrBroadcast.unregisterBroadcast();
        }
        if (CurrUnreadBroadcast != null) {
            CurrUnreadBroadcast.unregisterBroadcast();
        }

//        mLogoutBroadcast.unregisterBroadcast();
//        systemBroadcast.unregisterBroadcast();
    }
}

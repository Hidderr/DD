package com.coco3g.daishu.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.fragment.ReadFragment;
import com.coco3g.daishu.fragment.HomeFragment;
import com.coco3g.daishu.fragment.IncomeFragment;
import com.coco3g.daishu.fragment.MeFragment;
import com.coco3g.daishu.fragment.RepairFragment;
import com.coco3g.daishu.utils.Coco3gBroadcastUtils;
import com.coco3g.daishu.utils.RequestPermissionUtils;
import com.coco3g.daishu.view.BottomNavImageView;
import com.coco3g.daishu.view.TopBarView;

import java.util.Timer;
import java.util.TimerTask;


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
    private HomeFragment mHomeFrag;
    private ReadFragment mReadFrag;
    private IncomeFragment mIncomeFrag;
    private RepairFragment mRepairFrag;
    private MeFragment mMeFrag;
    //
    public static boolean isForeground = false;
    int mCurrNavIndex = -1;  //目前选中的地步导航的哪一个
    //点击返回键退出app
    private static Boolean isExit = false;
    Coco3gBroadcastUtils CurrUnreadBroadcast, systemBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Global.MAINACTIVITY_CONTEXT = this;
        initView();
    }

    private void initView() {
        mTopbar = (TopBarView) findViewById(R.id.topbar_main);
        mTopbar.hideLeftView();
        mTopbar.setTitle(getResources().getString(R.string.app_name));
        mTopbar.setMsgRemindVisible();
        //
        mImageHome = (BottomNavImageView) findViewById(R.id.view_nav_home);
        mImageRead = (BottomNavImageView) findViewById(R.id.view_nav_read);
        mImageIncome = (BottomNavImageView) findViewById(R.id.view_nav_income);
        mImageShop = (BottomNavImageView) findViewById(R.id.view_nav_shop);
        mImageMe = (BottomNavImageView) findViewById(R.id.view_nav_me);
        //
        mNavTitles = new String[]{getString(R.string.nav_title_home), getString(R.string.nav_title_goods_hui), getString(R.string.nav_title_income), getString(R.string.nav_title_shop),
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
        // 接收融云未读消息（私聊）的广播
        CurrUnreadBroadcast = new Coco3gBroadcastUtils(MainActivity.this);
        CurrUnreadBroadcast.receiveBroadcast(Coco3gBroadcastUtils.RONG_UNREAD_MSG).setOnReceivebroadcastListener(new Coco3gBroadcastUtils.OnReceiveBroadcastListener() {
            @Override
            public void receiveReturn(Intent intent) {
                Bundle bundle = intent.getBundleExtra("data");
                int count = bundle.getInt("unreadcount");

                Log.e("未读消息", count + "***");
                mImageRead.setUnReadCount(count);
            }
        });
        //接收融云未读消息（系统消息）的广播
        systemBroadcast = new Coco3gBroadcastUtils(MainActivity.this);
        systemBroadcast.receiveBroadcast(Coco3gBroadcastUtils.RONG_UNREAD_MSG_SYSTEM).setOnReceivebroadcastListener(new Coco3gBroadcastUtils.OnReceiveBroadcastListener() {
            @Override
            public void receiveReturn(Intent intent) {
//                mImageMe.setSystemRemind(true);
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        isForeground = true;
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
                mTopbar.setVisibility(View.VISIBLE);
                mTopbar.setMsgVisible();
                mTopbar.setTitle(getResources().getString(R.string.nav_title_home));
                if (mHomeFrag == null) {
                    mHomeFrag = (HomeFragment) new HomeFragment();
                    transaction.add(R.id.frame_main_content, mHomeFrag);
                } else {
                    transaction.show(mHomeFrag);
                }
                break;
            case 1: // 阅读
                mTopbar.setVisibility(View.VISIBLE);
                mTopbar.setMsgVisible();
                mTopbar.setTitle(getResources().getString(R.string.nav_title_goods_hui));
                if (mReadFrag == null) {
                    mReadFrag = new ReadFragment();
                    transaction.add(R.id.frame_main_content, mReadFrag);
                } else {
                    transaction.show(mReadFrag);
                }
                break;
            case 2: // 收益
                mTopbar.setVisibility(View.VISIBLE);
                mTopbar.setMsgVisible();
                mTopbar.setTitle(getResources().getString(R.string.nav_title_income));
                if (mIncomeFrag == null) {
                    mIncomeFrag = new IncomeFragment();
                    transaction.add(R.id.frame_main_content, mIncomeFrag);
                } else {
                    transaction.show(mIncomeFrag);
                }
                break;
            case 3: // 维修救援
                mTopbar.setVisibility(View.VISIBLE);
                mTopbar.setMsgVisible();
                mTopbar.setTitle(getResources().getString(R.string.nav_title_shop));
                if (mRepairFrag == null) {
                    mRepairFrag = new RepairFragment();
                    transaction.add(R.id.frame_main_content, mRepairFrag);
                } else {
                    transaction.show(mRepairFrag);
                }
                break;
            case 4: // 我的
                mTopbar.setVisibility(View.GONE);
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
        if (mReadFrag != null) {
            transaction.hide(mReadFrag);
        }
        if (mRepairFrag != null) {
            transaction.hide(mRepairFrag);
        }
        if (mMeFrag != null) {
            transaction.hide(mMeFrag);
        }
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
            Global.deleteSerializeData(this, Global.APP_CACHE); //清除token
            Global.deleteSerializeData(this, Global.LOGIN_PASSWORD); //清除密码
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isForeground = false;
        CurrUnreadBroadcast.unregisterBroadcast();
        systemBroadcast.unregisterBroadcast();
    }
}

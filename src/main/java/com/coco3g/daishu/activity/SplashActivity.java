package com.coco3g.daishu.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.SplashAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.bean.SplashPicListBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.RequestPermissionUtils;
import com.coco3g.daishu.view.CircleIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lisen on 2016/10/27.
 */

public class SplashActivity extends Activity {
    ViewPager mViewpager;
    TextView mTxtComplete;
    CircleIndicator indicator;
    SplashAdapter mAdapter;
    //
    int mTimerDuration = 2 * 1000;
    Timer mTimer = null;
    TimerTask mTask = null;
    boolean mIsViewPagerScroll = true; // 控制viewpager是否停止滚动
    int mCurrPagerItemPosition = 0;
    private final static int MSG_PAGER_SCROLL_CONTROLL = 0; // viewpager
    float dx, dy;
    float startX = 0, startY = 0;
    float endX, endY;
    ArrayList<SplashPicListBean.SplashPicData> mCurrSplashList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Global.getScreenWH(this);
        //
        if (!Global.isShowSplash(SplashActivity.this, false)) {
            Intent intent = new Intent(SplashActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        }
        mAdapter = new SplashAdapter(this);
        initView();
        getStartPicList();
    }

    private void initView() {
        mViewpager = (ViewPager) findViewById(R.id.viewpager_splash);
        indicator = (CircleIndicator) findViewById(R.id.indicator_splash);
        mTxtComplete = (TextView) findViewById(R.id.tv_splash_complete);
        //
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrPagerItemPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mViewpager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = Math.abs(event.getX());
                        startY = Math.abs(event.getY());
                        mIsViewPagerScroll = false;
                        cancelTimer();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        endX = Math.abs(event.getX());
                        endY = Math.abs(event.getY());
                        dx = Math.abs(startX - endX);
                        dy = Math.abs(startY - endY);
                        if (dx <= 8) { // 点击事件
                            if (mCurrSplashList != null && mCurrSplashList.size() > 0) {
                                String linkurl = mCurrSplashList.get(mCurrPagerItemPosition).linkurl;
                                if (TextUtils.isEmpty(linkurl)) { // 不跳转
                                    return false;
                                }
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                if (linkurl.startsWith("coco3g:")) {
                                    int lastposition = linkurl.lastIndexOf("?");
                                    String newurl = linkurl.substring(lastposition + 1);
                                    HashMap<String, String> hm = Global.parseCustomUrl(newurl);
                                    if (linkurl.startsWith("coco3g://goods_detail?")) { // 商品详情
                                        intent.putExtra("skip", "goodsdetail");
                                        intent.putExtra("title", hm.get("title"));
                                        intent.putExtra("goodsid", Integer.parseInt(hm.get("goodsid")));
                                        startActivity(intent);
                                    } else if (linkurl.startsWith("coco3g://tab?")) { // 跳转会员中心
                                        intent.putExtra("skip", "member");
                                        intent.putExtra("page", Integer.parseInt(hm.get("page")));
                                        startActivity(intent);
                                    } else if (linkurl.startsWith("coco3g://entershop?")) { // 进入基地
                                        intent.putExtra("skip", "baseshop");
                                        intent.putExtra("shopid", hm.get("shopid"));
                                        startActivity(intent);
                                    } else if (linkurl.startsWith("coco3g://topaike")) { // 进入拍客首页
                                        intent.putExtra("skip", "videolist");
                                        startActivity(intent);
                                    }
                                } else if (linkurl.startsWith("http://")) {
                                    intent.putExtra("skip", "web");
                                    intent.putExtra("url", linkurl);
                                    startActivity(intent);
                                }
                                finish();
                            }
                        } else {
                            if (mCurrPagerItemPosition == mCurrSplashList.size() - 1 && endX < startX) {
                                Intent intent = new Intent(SplashActivity.this, StartActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            Log.e("mCurrPagerItemPosition", mCurrPagerItemPosition + "--");
                        }
                        mIsViewPagerScroll = true;
                        initTimer();
                        break;
                }
                return false;
            }
        });
        mTxtComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }





    /**
     * 获取启动界面列表
     */
    public void getStartPicList() {
        Log.e("启动页", "启动页");
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "15");
        new BaseDataPresenter(this).loadData(DataUrl.GET_BANNER_IMAGE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, String>> imagelist = (ArrayList<Map<String, String>>) data.response;

                for (int i = 0; i < imagelist.size(); i++) {
                    SplashPicListBean.SplashPicData splashPicData = new SplashPicListBean.SplashPicData();
                    splashPicData.thumb = imagelist.get(i).get("thumb");
                    splashPicData.linkurl = imagelist.get(i).get("likurl");
                    mCurrSplashList.add(splashPicData);
                }
                //
                mAdapter.setList(mCurrSplashList);
                mViewpager.setAdapter(mAdapter);
                indicator.setViewPager(mViewpager);
                mViewpager.setCurrentItem(0);
                //
                Global.isShowSplash(SplashActivity.this, true);
//                initTimer();

            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, SplashActivity.this);
            }

            @Override
            public void onError() {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        initTimer();
        new RequestPermissionUtils(this).aleraPermission(Manifest.permission.ACCESS_FINE_LOCATION, 1);
    }

    /**
     * 计时器--控制banner显示周期
     */
    public void initTimer() {
        mTimer = new Timer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mIsViewPagerScroll) {
                    mCurrPagerItemPosition++;
                    if (mCurrPagerItemPosition == mAdapter.getCount()) {
                        mTask.cancel();
                        mTimer.cancel();
                        mCurrPagerItemPosition--;
                        return;
                    } else {
                        Message mess = new Message();
                        mess.what = MSG_PAGER_SCROLL_CONTROLL;
                        mHandlerMain.sendMessage(mess);
                    }
                } else {
                    return;
                }
            }
        };
        mTimer.schedule(mTask, mTimerDuration, mTimerDuration);
    }

    Handler mHandlerMain = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_PAGER_SCROLL_CONTROLL:
                    mViewpager.setCurrentItem(mCurrPagerItemPosition++);
                    break;
            }

        }

    };

    /**
     * 暂停banner滚动
     */
    public void cancelTimer() {
        if (mTimer != null && mTask != null) {
            mTimer.cancel();
            mTask.cancel();
            mTask = null;
            mTimer = null;
        }
    }
}

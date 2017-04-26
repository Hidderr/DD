package com.coco3g.daishu.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.data.Global;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BannerView extends RelativeLayout implements OnPageChangeListener {
    Context mContext;
    View mView = null;
    //    ArrayList<View> mPageViewList = new ArrayList<>();
    LoopViewPager mViewpagerBanner;
    MyViewPagerAdapter mPagerAdapter;
    BannerSelectPointView mPoints = null;
    public DisplayImageOptions options;
    //
    Timer mTimer = null;
    TimerTask mTask = null;
    int mTimerDuration = 4 * 1000;
    boolean mIsBannerScroll = true; // 控制banner是否停止滚动
    int mCurrPagerItemPosition = 0;
    int mScreenRatio = 4; // 占据的屏幕比例
    private final static int MSG_PAGER_SCROLL_CONTROLL = 0; // viewpager
    float dx, dy;
    float startX = 0, startY = 0;
    float endX, endY;
    private final static int BannerRequestCode = 0;
    ArrayList<Map<String, String>> mCurrBannerList = new ArrayList<Map<String, String>>();
    PageChangeListener pagechangelistener;

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.pic_default_icon).showImageForEmptyUri(R.mipmap.pic_default_icon)
                .showImageOnFail(R.mipmap.pic_default_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).resetViewBeforeLoading(false).build();
        initView();
    }

    public BannerView setScreenRatio(int ratio) {
        mScreenRatio = ratio;
        LayoutParams lp;
        lp = new LayoutParams(Global.screenWidth, Global.screenWidth / mScreenRatio);
        addView(mView, lp);
        return this;
    }

    public void initView() {
        // TODO Auto-generated method stub
        LayoutInflater lay = LayoutInflater.from(mContext);
        mView = lay.inflate(R.layout.view_banner, null);
        mViewpagerBanner = (LoopViewPager) mView.findViewById(R.id.viewpager_banner);
        mPoints = (BannerSelectPointView) mView.findViewById(R.id.viewpage_banner_point);
        mViewpagerBanner.addOnPageChangeListener(this);
//        mViewpagerBanner.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // TODO Auto-generated method stub
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        startX = Math.abs(event.getX());
//                        startY = Math.abs(event.getY());
//                        mIsBannerScroll = false;
//                        cancelTimer();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        endX = Math.abs(event.getX());
//                        endY = Math.abs(event.getY());
//                        dx = Math.abs(startX - endX);
//                        dy = Math.abs(startY - endY);
//                        if (dx <= 8) { // 点击事件
//                            if (mCurrBannerList != null && mCurrBannerList.size() > 0) {
//                                String linkurl = mCurrBannerList.get(mCurrPagerItemPosition).linkurl;
//                                if (TextUtils.isEmpty(linkurl)) {
//                                    return false;
//                                } else {
//                                    Intent intent = null;
//                                    if (linkurl.startsWith("coco3g:")) {
//                                        int lastposition = linkurl.lastIndexOf("?");
//                                        String newurl = linkurl.substring(lastposition + 1);
//                                        HashMap<String, String> hm = Global.parseCustomUrl(newurl);
//                                        if (linkurl.startsWith("coco3g://goods_detail?")) { // 商品详情
////                                            intent = new Intent(mContext, GoodsDetailActivity.class);
////                                            intent.putExtra("title", hm.get("title"));
////                                            intent.putExtra("goodsid", Integer.parseInt(hm.get("goodsid")));
////                                            mContext.startActivity(intent);
//                                        } else if (linkurl.startsWith("coco3g://tab?")) { //  跳转会员中心
////                                            Bundle bundle = new Bundle();
////                                            bundle.putInt("pagenum", Integer.parseInt(hm.get("page")));
////                                            new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.RETURN_GOTO_TAB, bundle);
//                                        } else if (linkurl.startsWith("coco3g://entershop?")) { // 基地
////                                            intent = new Intent(mContext, GoodsDetailActivity.class);
////                                            intent.putExtra("shopid", hm.get("shopid"));
////                                            mContext.startActivity(intent);
//                                        } else if (linkurl.startsWith("coco3g://topaike")) { // 进入拍客列表
////                                            intent = new Intent(mContext, VideoListActivity.class);
////                                            mContext.startActivity(intent);
//                                        }
//                                    } else if (linkurl.startsWith("http://")) {
////                                        intent = new Intent(mContext, WebActivity.class);
////                                        intent.putExtra("url", linkurl);
////                                        mContext.startActivity(intent);
//                                    }
//                                }
//                            } else {
//                                if (mCurrPagerItemPosition == 0 && mSurfaceVideo != null && mSurfaceHolder != null) {
//
//                                }
//                            }
//                        }
//                        mIsBannerScroll = true;
//                        initTimer();
//                        break;
//                }
//                return false;
//            }
//        });
    }

    /**
     * 禁止滚动
     */
    public void stopScroll() {
        mViewpagerBanner.setOnTouchListener(null);
        mIsBannerScroll = false;
    }

    public void loadData(ArrayList<Map<String, String>> list) {
        // TODO Auto-generated method stub
        mCurrBannerList = list;
        fillData();
    }

    public void setList(final ArrayList<String> videoimg, final ArrayList<String> videourl, final ArrayList<String> list) {
        //
        mPoints.setPointNum(list.size());
        mPoints.setSelectIndex(mCurrPagerItemPosition);
        mIsBannerScroll = true;
        initTimer();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                    if (jcVideoView != null) {
//                        jcVideoView.startPlayLogic();
//                    }
                mPagerAdapter = new MyViewPagerAdapter(videoimg, videourl, list);
                mViewpagerBanner.setAdapter(mPagerAdapter);
            }
        }, 200);
    }

//    public int getListSize() {
//        return mPageViewList.size();
//    }

    public void clearList() {
        if (mPagerAdapter != null) {
            mPagerAdapter.clearList();
        }
        mIsBannerScroll = false;
        cancelTimer();
        mCurrPagerItemPosition = 0;
    }

    class MyViewPagerAdapter extends PagerAdapter {
        private List<String> mListDatas;
        ArrayList<String> videoImageList, videoUrlList;

        public MyViewPagerAdapter(ArrayList<String> videoImageList, ArrayList<String> videoUrlList, List<String> listdate) {
            this.mListDatas = listdate;
            this.videoImageList = videoImageList;
            this.videoUrlList = videoUrlList;
        }

        public void clearList() {
            if (mListDatas != null) {
                mListDatas.clear();
                notifyDataSetChanged();
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView(mListViews.get(position));
            if (object instanceof SurfaceView) {
//                JCVideoPlayer.releaseAllVideos();
//                stopPlayVideo();
//                JCMediaManager.instance().mediaPlayer.pause();
            } else {

            }
//            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView image = new ImageView(mContext);
            LayoutParams lp = new LayoutParams(Global.screenWidth, Global.screenWidth / mScreenRatio);
            image.setLayoutParams(lp);
            image.setScaleType(ScaleType.CENTER_CROP);
            image.setImageResource(R.mipmap.pic_default_icon);
            ImageLoader.getInstance().displayImage(mListDatas.get(position), image, options);
            container.addView(image, 0);
            //点击事件
            image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = mCurrBannerList.get(position).get("linkurl");
                    if (!TextUtils.isEmpty(url) && !"#".equals(url)) {
                        Intent intent = new Intent(mContext, WebActivity.class);
                        intent.putExtra("url", url);
                        Log.e("banner图片链接", url);
                        mContext.startActivity(intent);
                    }
                }
            });
            return image;

        }

        @Override
        public int getCount() {
            return mListDatas.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    @Override
    public void onPageScrollStateChanged(int position) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int position) {
        // TODO Auto-generated method stub
        if (position > 0) {
//            if (JCMediaManager.instance().mediaPlayer.isPlaying()) {
//                JCMediaManager.instance().mediaPlayer.pause();
//            }
        }
        mCurrPagerItemPosition = position;
        if (mPoints != null) {
            mPoints.setSelectIndex(mCurrPagerItemPosition);
            pageChange(mCurrPagerItemPosition);
        }
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
                if (mIsBannerScroll) {
                    Message mess = new Message();
                    mess.what = MSG_PAGER_SCROLL_CONTROLL;
                    mHandlerMain.sendMessage(mess);
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
//                    if (!mIsVideo && mSurfaceHolder != null && !mSurfaceHolder.isPlaying()) {
                    mCurrPagerItemPosition++;
                    mCurrPagerItemPosition = mCurrPagerItemPosition % mPagerAdapter.getCount();
                    mViewpagerBanner.setCurrentItem(mCurrPagerItemPosition++);
//                    }
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

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            if (mTimer == null) {
                initTimer();
            }
        } else {
            cancelTimer();
        }
    }


    private void fillData() {
        ArrayList<String> dataList = new ArrayList<>();
        for (int i = 0; i < mCurrBannerList.size(); i++) {
            dataList.add(mCurrBannerList.get(i).get("thumb"));
        }
        //
        mPagerAdapter = new MyViewPagerAdapter(null, null, dataList);
        mViewpagerBanner.setAdapter(mPagerAdapter);
        //
        mPoints.setPointNum(dataList.size());
        mPoints.setSelectIndex(mCurrPagerItemPosition);
        mIsBannerScroll = true;
        initTimer();
    }

    public void setOnPageChangeListener(PageChangeListener pagechangelistener) {
        this.pagechangelistener = pagechangelistener;
    }

    public interface PageChangeListener {
        void pageChangeListener(int index);
    }

    private void pageChange(int index) {
        if (pagechangelistener != null) {
            pagechangelistener.pageChangeListener(index);
        }
    }

}

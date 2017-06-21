package com.coco3g.daishu.activity;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.utils.ImageUtils;
import com.coco3g.daishu.view.TopBarView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.polites.android.GestureImageView;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by lisen on 16/5/5.
 */
public class BrowseImageActivity extends BaseActivity {
    View mainView;
    TopBarView mTopBar;
    RelativeLayout mRelativeTopbar;
    TextView mTxtCurrPage;
    ImageView mImageBack;
    Button mBtnFinish;
    //    ImageView mImageView;
    ViewPager mViewPager;
    RelativeLayout mRelativeDownLoad;
    ArrayList<String> mImageList;
    int mCurrPageNum = 0;
    DisplayImageOptions options;
    RelativeLayout.LayoutParams lp = null;
    private ImageView[] mImageViews;
    private ArrayList<String> mCurrPathList = new ArrayList<String>();
    private ArrayList<Bitmap> mCurrBitmapList = new ArrayList<Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater lay = LayoutInflater.from(this);
        mainView = lay.inflate(R.layout.activity_browser_image, null);
        setContentView(mainView);
        mImageList = (ArrayList<String>) getIntent().getSerializableExtra("image_list");
        mCurrPageNum = getIntent().getIntExtra("pagenum", 0);
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.pic_default_icon).showImageForEmptyUri(R
                .mipmap.pic_default_icon).showImageOnFail(R.mipmap.pic_default_icon).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.ARGB_8888).imageScaleType(ImageScaleType
                        .IN_SAMPLE_INT).resetViewBeforeLoading(true).build();
        lp = new RelativeLayout.LayoutParams(Global.screenWidth, ActionBar.LayoutParams.WRAP_CONTENT);
        initView();
    }

    private void initView() {
        mTopBar = (TopBarView) findViewById(R.id.topbar_browser_image);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_browser_image);
        mTxtCurrPage = (TextView) findViewById(R.id.tv_des);
        mImageBack = (ImageView) findViewById(R.id.btn_back);
        mBtnFinish = (Button) findViewById(R.id.btn_ok);
        mRelativeTopbar = (RelativeLayout) findViewById(R.id.browser_image_topbar);
        mBtnFinish.setVisibility(View.GONE);
        mRelativeDownLoad = (RelativeLayout) findViewById(R.id.relative_browser_download);
        //
        mImageViews = new ImageView[mImageList.size()];
        for (int i = 0; i < mImageViews.length; i++) {
            final GestureImageView imageView = new GestureImageView(this);
            imageView.setLayoutParams(lp);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            ImageLoader.getInstance().displayImage(mImageList.get(i), imageView, options);
            mImageViews[i] = imageView;
            if (mImageList.get(i).startsWith("http://") || mImageList.get(i).startsWith("https://")) {
                ImageLoader.getInstance().loadImage(mImageList.get(i), options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        try {
                            imageView.setImageBitmap(bitmap);
                            mCurrBitmapList.add(bitmap);
                            mCurrPathList.add(s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            } else if (mImageList.get(i).startsWith("R")) {
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.pic_default_icon, imageView, options);
            } else {
                ImageLoader.getInstance().displayImage("file://" + mImageList.get(i), imageView, options);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onImageSingleTap();
//                    v.setVisibility(View.GONE);
//                    finish();
                }
            });
//            imageView.setBackgroundResource(imgIdArray[i]);
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrPageNum = position;
                mTxtCurrPage.setText(mCurrPageNum + 1 + "/" + mImageList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(new MyAdapter());
        mViewPager.setCurrentItem(mCurrPageNum);
        //
        mRelativeDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePic();
            }
        });
        mImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //
        mTxtCurrPage.setText(mCurrPageNum + 1 + "/" + mImageList.size());
    }

    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageViews.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(mImageViews[position]);

        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(mImageViews[position], 0);
            return mImageViews[position];
        }

    }

    private void savePic() {
        String temppath = Global.getPath(BrowseImageActivity.this) + File.separator + Global.DOWNLOAD;
        File f = new File(temppath);
        if (!f.exists()) {
            f.mkdirs();
        }
        temppath = temppath + File.separator + Global.getFileName(mCurrPathList.get(mCurrPageNum));
        String savePath = ImageUtils.saveImageToSD(BrowseImageActivity.this, temppath, mCurrBitmapList.get(mCurrPageNum), 100);
        if (!TextUtils.isEmpty(savePath)) {
            Global.showToast("图片已保存到：" + savePath, BrowseImageActivity.this);
        } else {
            Global.showToast("图片保存失败", BrowseImageActivity.this);
        }
    }



    /**
     * 单击时，隐藏头和尾
     */
    public void onImageSingleTap() {
        if (mRelativeTopbar.getVisibility() == View.VISIBLE) {
            mRelativeTopbar.setAnimation(AnimationUtils.loadAnimation(this, com.lqr.imagepicker.R.anim.top_out));
            mRelativeTopbar.setVisibility(View.GONE);
        } else {
            mRelativeTopbar.setAnimation(AnimationUtils.loadAnimation(this, com.lqr.imagepicker.R.anim.top_in));
            mRelativeTopbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCurrBitmapList != null && mCurrBitmapList.size() > 0) {
//            for (Bitmap bitmap : mCurrBitmapList) {
//                if (bitmap != null && !bitmap.isRecycled()) {
//                    bitmap.recycle();
//                    bitmap = null;
//                }
//            }
            mCurrBitmapList.clear();
            mCurrBitmapList = null;
            mCurrPathList.clear();
            mCurrPathList = null;
        }
    }
}

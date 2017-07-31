package com.coco3g.daishu.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.bean.SplashPicListBean;
import com.coco3g.daishu.data.Global;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

/**
 * Created by lisen on 16/8/12.
 */
public class SplashAdapter extends PagerAdapter {
    Context mContext;
    ArrayList<SplashPicListBean.SplashPicData> mList = new ArrayList<>();
    DisplayImageOptions options;
    RelativeLayout.LayoutParams lp = null;

    public SplashAdapter(Context context) {
        mContext = context;
        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .resetViewBeforeLoading(true).displayer(new RoundedBitmapDisplayer(20)).displayer(new FadeInBitmapDisplayer(500)).build();
        lp = new RelativeLayout.LayoutParams(Global.screenWidth, RelativeLayout.LayoutParams.MATCH_PARENT);
    }

    public void setList(ArrayList<SplashPicListBean.SplashPicData> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    ViewHolder viewHolder;

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        viewHolder = new ViewHolder();
        LayoutInflater lay = LayoutInflater.from(mContext);
        View v = lay.inflate(R.layout.a_splash_item, null);
        viewHolder.mImagePic = (ImageView) v.findViewById(R.id.image_splash_info);
        viewHolder.mImagePic.setLayoutParams(lp);
        ImageLoader.getInstance().displayImage(mList.get(position).thumb, viewHolder.mImagePic, options);
        view.addView(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return v;
    }

    private class ViewHolder {
        public ImageView mImagePic;
    }

}

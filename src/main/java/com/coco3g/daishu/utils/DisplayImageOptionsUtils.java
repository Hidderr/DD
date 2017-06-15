package com.coco3g.daishu.utils;

import android.graphics.Bitmap;

import com.coco3g.daishu.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class DisplayImageOptionsUtils {

    //正常图片
    public DisplayImageOptions init() {
        return new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.pic_default_icon).showImageForEmptyUri(R.mipmap.pic_default_icon)
                .showImageOnFail(R.mipmap.pic_default_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).resetViewBeforeLoading(false).build();
    }

    //正常图片
    public DisplayImageOptions init(int resId) {
        return new DisplayImageOptions.Builder().showImageOnLoading(resId).showImageForEmptyUri(resId)
                .showImageOnFail(resId).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).resetViewBeforeLoading(false).build();
    }

    //圆形头像
    public DisplayImageOptions circleImageInit() {
        return new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.pic_default_avatar_icon).showImageForEmptyUri(R.mipmap.pic_default_avatar_icon)
                .showImageOnFail(R.mipmap.pic_default_avatar_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).resetViewBeforeLoading(true).displayer(new RoundedBitmapDisplayer(200)).build();
    }


}

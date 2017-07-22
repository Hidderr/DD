package com.coco3g.daishu.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by jason on 2017/4/26.
 */

public class CarShopAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Map<String, String>> mList = new ArrayList<>();
    RelativeLayout.LayoutParams lp = null;
    DisplayImageOptions options, mImageOptions;

    public CarShopAdapter(Context mContext) {
        this.mContext = mContext;
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.pic_default_car_icon).showImageForEmptyUri(R.mipmap.pic_default_car_icon)
                .showImageOnFail(R.mipmap.pic_default_car_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .resetViewBeforeLoading(true).displayer(new FadeInBitmapDisplayer(500)).build();
        mImageOptions = new DisplayImageOptionsUtils().init(R.mipmap.pic_default_car_icon);
        lp = new RelativeLayout.LayoutParams(Global.screenWidth / 8, Global.screenWidth / 8);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
    }

    public void setList(ArrayList<Map<String, String>> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void clearList() {
        if (mList != null && mList.size() > 0) {
            mList.clear();
        }
        notifyDataSetChanged();
    }

    public void addList(ArrayList<Map<String, String>> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public ArrayList<Map<String, String>> getList() {
        return mList;
    }


    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewHolder viewHolder;

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.a_car_shop_item, null);
            viewHolder.mImageThumb = (ImageView) view.findViewById(R.id.image_car_shop_item_thumb);
            viewHolder.mImageThumb.setLayoutParams(lp);
            viewHolder.mImageThumb.setScaleType(ImageView.ScaleType.FIT_CENTER);
            viewHolder.mTxtName = (TextView) view.findViewById(R.id.image_car_shop_item_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Map<String, String> map = mList.get(position);

        if (!TextUtils.isEmpty(map.get("title")) && !TextUtils.isEmpty(map.get("thumb"))) {
            ImageLoader.getInstance().displayImage(map.get("thumb"), viewHolder.mImageThumb, options);
            //
            viewHolder.mTxtName.setText(map.get("title"));
        } else {   //适配器在个人信息里面还用到过
            ImageLoader.getInstance().displayImage(map.get("brandthumb"), viewHolder.mImageThumb, options);
            //
            viewHolder.mTxtName.setText(map.get("chepai"));
        }
        return view;
    }

    private class ViewHolder {
        public ImageView mImageThumb;
        public TextView mTxtName;
    }
}

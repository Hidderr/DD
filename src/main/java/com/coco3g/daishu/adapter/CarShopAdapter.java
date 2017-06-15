package com.coco3g.daishu.adapter;

import android.content.Context;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by jason on 2017/4/26.
 */

public class CarShopAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Map<String, String>> mList = new ArrayList<>();
    RelativeLayout.LayoutParams lp = null;

    public CarShopAdapter(Context mContext) {
        this.mContext = mContext;
        lp = new RelativeLayout.LayoutParams(Global.screenWidth / 6, Global.screenWidth / 6);
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
            viewHolder.mImageThumb.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            viewHolder.mTxtName = (TextView) view.findViewById(R.id.image_car_shop_item_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Map<String, String> map = mList.get(position);

        ImageLoader.getInstance().displayImage(map.get("thumb"), viewHolder.mImageThumb, new DisplayImageOptionsUtils().init(R.mipmap.pic_default_car_icon));
        //
        viewHolder.mTxtName.setText(map.get("title"));


        return view;
    }

    private class ViewHolder {
        public ImageView mImageThumb;
        public TextView mTxtName;
    }
}

package com.coco3g.daishu.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Global;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by jason on 2017/4/26.
 */

public class HomeAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Map<String, String>> mList = new ArrayList<>();
    RelativeLayout.LayoutParams lp = null;

    public HomeAdapter(Context mContext) {
        this.mContext = mContext;
        lp = new RelativeLayout.LayoutParams(Global.screenWidth / 4, Global.screenWidth / 4);
        lp.addRule(RelativeLayout.ALIGN_TOP, R.id.relative_home_item_content);
        lp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.relative_home_item_content);
        lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
    }

    public void setList(ArrayList<Map<String, String>> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 5;
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
            view = LayoutInflater.from(mContext).inflate(R.layout.a_home_item, null);
            viewHolder.mImageThumb = (ImageView) view.findViewById(R.id.image_home_item_thumb);
            viewHolder.mImageThumb.setLayoutParams(lp);
            viewHolder.mImageThumb.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            viewHolder.mTxtTitle = (TextView) view.findViewById(R.id.tv_home_item_title);
            viewHolder.mTxtSubTitle1 = (TextView) view.findViewById(R.id.tv_home_item_subtitle1);
            viewHolder.mTxtSubTitle2 = (TextView) view.findViewById(R.id.tv_home_item_subtitle2);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        return view;
    }

    private class ViewHolder {
        public ImageView mImageThumb;
        public TextView mTxtTitle, mTxtSubTitle1, mTxtSubTitle2;
    }
}

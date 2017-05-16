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

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by jason on 2017/4/26.
 */

public class IncomeAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Map<String, String>> mList = new ArrayList<>();
    RelativeLayout.LayoutParams lp = null;

    public IncomeAdapter(Context mContext) {
        this.mContext = mContext;
        lp = new RelativeLayout.LayoutParams(Global.screenWidth / 4, Global.screenWidth / 4);
    }

    public void setList(ArrayList<Map<String, String>> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 3;
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
            view = LayoutInflater.from(mContext).inflate(R.layout.a_income_item, null);
            viewHolder.mImageThumb = (ImageView) view.findViewById(R.id.image_home_item_thumb);
//            viewHolder.mImageThumb.setLayoutParams(lp);
//            viewHolder.mImageThumb.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            viewHolder.mTxtName = (TextView) view.findViewById(R.id.tv_income_item_nickname);
            viewHolder.mTxtMoney = (TextView) view.findViewById(R.id.tv_income_item_money);
            viewHolder.mTxtPhone = (TextView) view.findViewById(R.id.tv_income_item_phone);
            viewHolder.mTxtTime = (TextView) view.findViewById(R.id.tv_income_item_time);
            viewHolder.mImageBottomLine1 = (ImageView) view.findViewById(R.id.image_income_item_bottom_line1);
            viewHolder.mImageBottomLine2 = (ImageView) view.findViewById(R.id.image_income_item_bottom_line2);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (position == 2) {
            viewHolder.mImageBottomLine1.setVisibility(View.GONE);
            viewHolder.mImageBottomLine2.setVisibility(View.VISIBLE);
        }


        return view;
    }

    private class ViewHolder {
        public ImageView mImageThumb, mImageBottomLine1, mImageBottomLine2;
        public TextView mTxtName, mTxtMoney, mTxtPhone, mTxtTime;
    }
}

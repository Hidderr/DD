package com.coco3g.daishu.adapter;

import android.content.Context;
import android.graphics.Paint;
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

public class NewestOfferAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Map<String, String>> mList = new ArrayList<>();
    RelativeLayout.LayoutParams lp = null;

    public NewestOfferAdapter(Context mContext) {
        this.mContext = mContext;
        lp = new RelativeLayout.LayoutParams(Global.screenWidth / 4, Global.screenWidth / 4);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.a_newest_offer_item, null);
            viewHolder.mTxtStoreName = (TextView) view.findViewById(R.id.tv_newest_offer_item_name);
            viewHolder.mTxtLowPrice = (TextView) view.findViewById(R.id.tv_newest_offer_item_low_price);
            viewHolder.mTxtHighPrice = (TextView) view.findViewById(R.id.tv_newest_offer_item_high_price);
            viewHolder.mTxtAddress = (TextView) view.findViewById(R.id.tv_newest_offer_item_address);
            viewHolder.mTxtPhone = (TextView) view.findViewById(R.id.tv_newest_offer_item_phone);
            viewHolder.mTxtContact = (TextView) view.findViewById(R.id.tv_newest_offer_item_contact);
            viewHolder.mImageThumb = (ImageView) view.findViewById(R.id.image_newest_offer_item_thumb);
            //
            viewHolder.mImageThumb.setLayoutParams(lp);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.mTxtHighPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线


        return view;
    }

    private class ViewHolder {
        public TextView mTxtStoreName, mTxtLowPrice, mTxtHighPrice, mTxtAddress, mTxtPhone, mTxtContact;
        public ImageView mImageThumb;
    }
}

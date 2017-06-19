package com.coco3g.daishu.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
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

    public void addList(ArrayList<Map<String, String>> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public ArrayList<Map<String, String>> getList() {
        return mList;
    }

    public void clearList() {
        if (mList != null) {
            mList.clear();
        }
        notifyDataSetChanged();
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


        final Map<String, String> map = mList.get(position);
        //
        viewHolder.mTxtStoreName.setText(map.get("title"));
        //
        ImageLoader.getInstance().displayImage(map.get("thumb"), viewHolder.mImageThumb, new DisplayImageOptionsUtils().init(R.mipmap.pic_default_car_icon));
        //
        viewHolder.mTxtHighPrice.setText(map.get("price"));
        viewHolder.mTxtHighPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
        //
        viewHolder.mTxtLowPrice.setText(map.get("shopprice"));
        //
        viewHolder.mTxtAddress.setText("地址：" + map.get("location"));
        //
        viewHolder.mTxtPhone.setText("电话：" + map.get("phone"));
        //立即联系
        viewHolder.mTxtContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + map.get("phone")));
                mContext.startActivity(intent);
            }
        });


        return view;
    }

    private class ViewHolder {
        public TextView mTxtStoreName, mTxtLowPrice, mTxtHighPrice, mTxtAddress, mTxtPhone, mTxtContact;
        public ImageView mImageThumb;
    }
}

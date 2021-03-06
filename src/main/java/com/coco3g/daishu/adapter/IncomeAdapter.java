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

    public void addList(ArrayList<Map<String, String>> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearList() {
        if (mList != null || mList.size() > 0) {
            mList.clear();
            notifyDataSetChanged();
        }
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
            view = LayoutInflater.from(mContext).inflate(R.layout.a_income_item, null);
            viewHolder.mImageThumb = (ImageView) view.findViewById(R.id.image_income_item_thumb);
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

        Map<String, String> incomeMap = mList.get(position);

        //昵称
        viewHolder.mTxtName.setText(incomeMap.get("nickname"));
        //手机号码
        viewHolder.mTxtPhone.setText(incomeMap.get("phone"));
        //会员推荐奖励
        viewHolder.mTxtMoney.setText("会员推荐奖励：" + incomeMap.get("price") + "元");
        //时间
        viewHolder.mTxtTime.setText(incomeMap.get("addtime"));
        //
        String vipId = incomeMap.get("groupid");
        if (vipId.equals("1")) {  //铜
            viewHolder.mImageThumb.setImageResource(R.mipmap.pic_income_red_icon);
        } else if (vipId.equals("2")) {   //银
            viewHolder.mImageThumb.setImageResource(R.mipmap.pic_income_grey_icon);
        } else if (vipId.equals("3")) {   //金
            viewHolder.mImageThumb.setImageResource(R.mipmap.pic_income_yellow_icon);
        }


        return view;
    }

    private class ViewHolder {
        public ImageView mImageThumb, mImageBottomLine1, mImageBottomLine2;
        public TextView mTxtName, mTxtMoney, mTxtPhone, mTxtTime;
    }
}

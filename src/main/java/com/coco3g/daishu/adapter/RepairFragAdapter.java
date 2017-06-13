package com.coco3g.daishu.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by jason on 2017/4/26.
 */

public class RepairFragAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Map<String, String>> mList = new ArrayList<>();

    public RepairFragAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setList(ArrayList<Map<String, String>> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public ArrayList<Map<String, String>> getList() {
        return mList;
    }

    public void addList(ArrayList<Map<String, String>> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearList() {
        if (mList != null && mList.size() > 0) {
            mList.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 1;
//        if (mList == null) {
//            return 0;
//        }
//        return mList.size();
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
            view = LayoutInflater.from(mContext).inflate(R.layout.a_repair_frag_item, null);
            viewHolder.mLinearRoot = (LinearLayout) view.findViewById(R.id.linear_a_repair_frag_item_root);
            viewHolder.mTxtCar = (TextView) view.findViewById(R.id.tv_a_repair_frag_item_car_name);
            viewHolder.mTxtContent = (TextView) view.findViewById(R.id.tv_a_repair_frag_item_content);
            //
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

//        final Map<String, String> map = mList.get(position);

        viewHolder.mLinearRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return view;
    }

    private class ViewHolder {
        public TextView mTxtCar, mTxtContent;
        public LinearLayout mLinearRoot;
    }
}

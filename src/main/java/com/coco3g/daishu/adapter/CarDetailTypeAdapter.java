package com.coco3g.daishu.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.CarDetailActivity;
import com.coco3g.daishu.activity.CarDetailTypeActivity;
import com.coco3g.daishu.data.Global;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by jason on 2017/4/26.
 */

public class CarDetailTypeAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Map<String, String>> mList = null;
    RelativeLayout.LayoutParams lp = null;

    public CarDetailTypeAdapter(Context mContext) {
        this.mContext = mContext;
        lp = new RelativeLayout.LayoutParams(Global.screenWidth / 4, Global.screenWidth / 4);
    }

    public void setList(ArrayList<Map<String, String>> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void addList(ArrayList<Map<String, String>> list) {
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    public ArrayList<Map<String, String>> getList() {
        return this.mList;
    }

    public void clearList() {
        if (this.mList != null) {
            this.mList.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.mList == null ? 0 : this.mList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewHolder viewHolder;

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.a_car_detail_type_item, null);
            viewHolder.mTxtTitle = (TextView) view.findViewById(R.id.tv_car_detail_type_title);
            viewHolder.mTxtContent = (TextView) view.findViewById(R.id.tv_car_detail_type_content);
            viewHolder.mRelativeRoot = (RelativeLayout) view.findViewById(R.id.relative_car_detail_type_content);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Map<String, String> map = mList.get(position);

        viewHolder.mTxtTitle.setText(map.get("title"));
        //
        viewHolder.mTxtContent.setText(map.get("super"));
        //
        viewHolder.mRelativeRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CarDetailActivity.class);
                intent.putExtra("id", mList.get(position).get("id"));
                intent.putExtra("title", map.get("title"));
                mContext.startActivity(intent);
            }
        });


        return view;
    }

    private class ViewHolder {
        public TextView mTxtTitle, mTxtContent;
        public RelativeLayout mRelativeRoot;
    }
}

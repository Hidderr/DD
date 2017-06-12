package com.coco3g.daishu.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Global;


public class CategoryOneAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Map<String, Object>> mList = new ArrayList<>();
    private int selectItem = -1;

    public CategoryOneAdapter(Context context) {
        mContext = context;
    }

    public void setList(ArrayList<Map<String, Object>> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public ArrayList<Map<String, Object>> getList() {
        return mList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Map<String, Object> map = mList.get(position);


        TextView tv = new TextView(mContext);
        tv.setText(map.get("name") + "");
        tv.setTag(map.get("catid") + "");
        tv.setTextSize(14f);
        tv.setPadding(10, Global.dipTopx(mContext, 20), 10, Global.dipTopx(mContext, 20));
        tv.setGravity(Gravity.CENTER);
        if (position == selectItem) {
            tv.setTextColor(mContext.getResources().getColor(R.color.text_color_1));
            tv.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        } else {
            tv.setTextColor(mContext.getResources().getColor(R.color.text_color_2));
            tv.setBackgroundColor(mContext.getResources().getColor(R.color.text_color_8));
        }
        return tv;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }
}

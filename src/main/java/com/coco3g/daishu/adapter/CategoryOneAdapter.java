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

    OnItemSelectedListener onItemSelectedListener;

    public CategoryOneAdapter(Context context) {
        mContext = context;
    }

    public void setList(ArrayList<Map<String, Object>> list) {
        mList = list;

        for (int i = 0; i < mList.size(); i++) {
            if (i == 0) {
                mList.get(i).put("selected", true);
            } else {
                mList.get(i).put("selected", false);
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<Map<String, Object>> getList() {
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
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Map<String, Object> map = mList.get(position);


        TextView tv = new TextView(mContext);
        tv.setText(map.get("title") + "");
        tv.setTag(map.get("id") + "");
        tv.setTextSize(14f);
        tv.setPadding(10, Global.dipTopx(mContext, 20), 10, Global.dipTopx(mContext, 20));
        tv.setGravity(Gravity.CENTER);
        if ((Boolean) map.get("selected")) {
            tv.setTextColor(mContext.getResources().getColor(R.color.text_color_1));
            tv.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        } else {
            tv.setTextColor(mContext.getResources().getColor(R.color.text_color_2));
            tv.setBackgroundColor(mContext.getResources().getColor(R.color.text_color_14));
        }
        //
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemOnClick(position, map.get("id") + "");
            }
        });
        return tv;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public interface OnItemSelectedListener {
        void OnItemClick(int position, String id);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public void itemOnClick(int position, String id) {

        int size = mList.size();
        for (int i = 0; i < size; i++) {
            if (position == i) {
                mList.get(i).put("selected", true);
            } else {
                mList.get(i).put("selected", false);
            }
        }

        if (onItemSelectedListener != null) {
            onItemSelectedListener.OnItemClick(position, id);
        }
    }


}

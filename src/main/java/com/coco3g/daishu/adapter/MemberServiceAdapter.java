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
import com.coco3g.daishu.activity.StartActivity;
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by jason on 2017/4/26.
 */

public class MemberServiceAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Map<String, String>> mList = new ArrayList<>();
    LinearLayout.LayoutParams thumb_lp = null;

    public MemberServiceAdapter(Context mContext) {
        this.mContext = mContext;
        thumb_lp = new LinearLayout.LayoutParams(Global.screenWidth, Global.screenWidth * 2 / 3);
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
//        return 3;
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
            view = LayoutInflater.from(mContext).inflate(R.layout.a_member_service_item, null);
            viewHolder.mImageThumb = (ImageView) view.findViewById(R.id.image_member_service_item);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Map<String, String> map = mList.get(position);

        viewHolder.mImageThumb.setLayoutParams(thumb_lp);
        ImageLoader.getInstance().displayImage(map.get("image"), viewHolder.mImageThumb, new DisplayImageOptionsUtils().init(R.mipmap.pic_default_car_icon));
        viewHolder.mImageThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(map.get("url"))) {
                    return;
                }
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("url", map.get("url"));
                mContext.startActivity(intent);
            }
        });
        return view;
    }

    private class ViewHolder {
        public ImageView mImageThumb;
    }
}

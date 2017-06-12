package com.coco3g.daishu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Map;

public class CategoryTwoAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Map<String, Object>> mList = new ArrayList<>();
    public DisplayImageOptions options;
    LinearLayout.LayoutParams lp = null;

    public CategoryTwoAdapter(Context context) {
        mContext = context;
        options = new DisplayImageOptionsUtils().init();
        lp = new LinearLayout.LayoutParams(Global.screenWidth / 5, Global.screenWidth / 5);
    }

    public void setList(ArrayList<Map<String, Object>> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public ArrayList<Map<String, Object>> getList() {
        return mList;
    }

    public void clearList() {
        if (mList != null) {
            mList.clear();
            notifyDataSetChanged();
        }
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

    ViewHodler viewHodler;

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (view == null) {
            viewHodler = new ViewHodler();
            LayoutInflater lay = LayoutInflater.from(mContext);
            view = lay.inflate(R.layout.a_two_category_item, null);
            viewHodler.mImageThumb = (ImageView) view.findViewById(R.id.image_two_category_thumb);
            viewHodler.mTxtName = (TextView) view.findViewById(R.id.tv_two_category_name);
            view.setTag(viewHodler);
        } else {
            viewHodler = (ViewHodler) view.getTag();
        }

        Map<String, Object> map = mList.get(position);
        //
        viewHodler.mTxtName.setText(map.get("name")+"");
        viewHodler.mImageThumb.setLayoutParams(lp);
        ImageLoader.getInstance().displayImage(map.get("thumb")+"", viewHodler.mImageThumb, options);
        return view;
    }

    public class ViewHodler {
        public ImageView mImageThumb;
        public TextView mTxtName;
    }

}

package com.coco3g.daishu.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.ShaiXuanListActivity;
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by jason on 2017/4/26.
 */

public class StoreShaiXuanAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Map<String, String>> mList = new ArrayList<>();
    RelativeLayout.LayoutParams lp = null;
    String typeid = "1";  //  1;快修门店   2；洗车店

    public StoreShaiXuanAdapter(Context mContext, String typeid) {
        this.mContext = mContext;
        this.typeid = typeid;
        lp = new RelativeLayout.LayoutParams(Global.screenWidth / 4, Global.screenWidth / 4);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.a_store_shai_xuan_item, null);
            viewHolder.mImageThumb = (ImageView) view.findViewById(R.id.image_store_shai_xuan_item_thumb);
            viewHolder.mImageThumb.setLayoutParams(lp);
            viewHolder.mImageThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHolder.mTxtName = (TextView) view.findViewById(R.id.tv_store_shai_xuan_item_name);
            viewHolder.mTxtType = (TextView) view.findViewById(R.id.tv_store_shai_xuan_item_type);
            viewHolder.mTxtPingFen = (TextView) view.findViewById(R.id.tv_store_shai_xuan_item_pingfen);
            viewHolder.mTxtOrders = (TextView) view.findViewById(R.id.tv_store_shai_xuan_item_orders);
            viewHolder.mTxtAddress = (TextView) view.findViewById(R.id.tv_store_shai_xuan_item_address);
            viewHolder.mTxtDistance = (TextView) view.findViewById(R.id.tv_store_shai_xuan_item_distance);
            viewHolder.mTxtDisPrice = (TextView) view.findViewById(R.id.tv_store_shai_xuan_item_disprice);
            viewHolder.mTxtGroupPrice = (TextView) view.findViewById(R.id.tv_store_shai_xuan_item_groprice);
            viewHolder.mRelativeRoot = (RelativeLayout) view.findViewById(R.id.relative_store_shai_xuan_item_root);
            viewHolder.mRelativePrice = (RelativeLayout) view.findViewById(R.id.relative_store_shai_xuan_item_price);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Map<String, String> storeMap = mList.get(position);
        ImageLoader.getInstance().displayImage(storeMap.get("thumb"), viewHolder.mImageThumb, new DisplayImageOptionsUtils().init());
        //
        viewHolder.mTxtName.setText(storeMap.get("name"));
        //
        viewHolder.mTxtAddress.setText(storeMap.get("address"));
        //
        viewHolder.mTxtDistance.setText(storeMap.get("juli") + "Km");
        //汽车店的类别
        String typename = storeMap.get("quaname");
        if (!TextUtils.isEmpty(typename)) {
            viewHolder.mTxtType.setVisibility(View.VISIBLE);
            viewHolder.mTxtType.setText(typename);
        } else {
            viewHolder.mTxtType.setVisibility(View.GONE);
        }

        //是洗车店的话显示价格
        if (typeid.equals("2")) {
            viewHolder.mRelativePrice.setVisibility(View.VISIBLE);
            String disprice = storeMap.get("disprice");
            if (!TextUtils.isEmpty(disprice)) {
                viewHolder.mTxtDisPrice.setText("优惠价    " + disprice);
            } else {
                viewHolder.mTxtDisPrice.setText("优惠价     暂无");
            }
            String grouprice = storeMap.get("groprice");
            if (!TextUtils.isEmpty(grouprice)) {
                viewHolder.mTxtGroupPrice.setText("团购价    " + grouprice);
            } else {
                viewHolder.mTxtGroupPrice.setText("团购价     暂无");
            }

        } else {
            viewHolder.mRelativePrice.setVisibility(View.INVISIBLE);
        }

        //完成单数
        viewHolder.mTxtOrders.setText(storeMap.get("ordnums") + "单");
        //评分
        viewHolder.mTxtPingFen.setText(storeMap.get("evaluate"));
        //
        viewHolder.mRelativeRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(mContext, WebActivity.class);
                String url = DataUrl.BASE_REPAIR_URL + storeMap.get("id");
                intent1.putExtra("url", url);
                mContext.startActivity(intent1);
            }
        });


        return view;
    }

    private class ViewHolder {
        public ImageView mImageThumb;
        public RelativeLayout mRelativeRoot, mRelativePrice;
        public TextView mTxtName, mTxtType, mTxtPingFen, mTxtOrders, mTxtAddress, mTxtDistance, mTxtDisPrice, mTxtGroupPrice;

    }
}

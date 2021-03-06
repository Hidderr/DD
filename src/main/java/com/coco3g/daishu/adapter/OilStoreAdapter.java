package com.coco3g.daishu.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.DriveRouteActivity;
import com.coco3g.daishu.activity.DriveRouteNavActivity;
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

public class OilStoreAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Map<String, Object>> mList = new ArrayList<>();
    RelativeLayout.LayoutParams lp = null;

    public OilStoreAdapter(Context mContext) {
        this.mContext = mContext;
        lp = new RelativeLayout.LayoutParams(Global.screenWidth / 4, Global.screenWidth / 4);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
    }

    public void setList(ArrayList<Map<String, Object>> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void clearList() {
        if (mList != null && mList.size() > 0) {
            mList.clear();
        }
        notifyDataSetChanged();
    }

    public void addList(ArrayList<Map<String, Object>> list) {
        mList.addAll(list);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.a_oil_store_item, null);
            viewHolder.mImageThumb = (ImageView) view.findViewById(R.id.image_oil_store_item_thumb);
            viewHolder.mImageTakePhone = (ImageView) view.findViewById(R.id.image_store_shai_xuan_item_phone);
            viewHolder.mImageThumb.setLayoutParams(lp);
            viewHolder.mImageThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHolder.mTxtName = (TextView) view.findViewById(R.id.tv_store_shai_xuan_item_name);
            viewHolder.mTxtOil92 = (TextView) view.findViewById(R.id.tv_oil_store_item_oil_type1);
            viewHolder.mTxtOil95 = (TextView) view.findViewById(R.id.tv_oil_store_item_oil_type2);
            viewHolder.mTxtPrice92 = (TextView) view.findViewById(R.id.tv_oil_store_item_price_92);
            viewHolder.mTxtPrice95 = (TextView) view.findViewById(R.id.tv_oil_store_item_price_95);
            viewHolder.mTxtAddress = (TextView) view.findViewById(R.id.tv_oil_tore_item_address);
            viewHolder.mTxtDistance = (TextView) view.findViewById(R.id.tv_oil_store_item_distance);
            viewHolder.mRelativeRoot = (RelativeLayout) view.findViewById(R.id.relative_oil_store_item_root);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Map<String, Object> storeMap = mList.get(position);
        ArrayList<Map<String, String>> oilList = (ArrayList<Map<String, String>>) storeMap.get("gaslist");
        //
        ImageLoader.getInstance().displayImage(storeMap.get("thumb") + "", viewHolder.mImageThumb, new DisplayImageOptionsUtils().init());
        //
        viewHolder.mTxtName.setText(storeMap.get("title") + "");
        //
        viewHolder.mTxtAddress.setText(storeMap.get("address") + "");
        //
        viewHolder.mTxtDistance.setText(storeMap.get("juli") + "Km");
        //
        viewHolder.mTxtOil92.setText(oilList.get(0).get("title"));
        viewHolder.mTxtPrice92.setText(oilList.get(0).get("price") + "/L");
        //
        viewHolder.mTxtOil95.setText(oilList.get(1).get("title"));
        viewHolder.mTxtPrice95.setText(oilList.get(1).get("price") + "/L");
        //
        viewHolder.mRelativeRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DriveRouteActivity.class);
                intent.putExtra("startlat", Global.mCurrLat);
                intent.putExtra("startlng", Global.mCurrLng);

                double endLat = Double.parseDouble(storeMap.get("lat") + "");
                double endLng = Double.parseDouble(storeMap.get("lng") + "");
                intent.putExtra("endlat", endLat);
                intent.putExtra("endlng", endLng);
                //
                intent.putExtra("storeName", storeMap.get("title") + "");
                mContext.startActivity(intent);
            }
        });
        viewHolder.mImageTakePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + storeMap.get("phone")));
                mContext.startActivity(intent);
            }
        });


        return view;
    }

    private class ViewHolder {
        public ImageView mImageThumb, mImageTakePhone;
        public RelativeLayout mRelativeRoot;
        public TextView mTxtName, mTxtOil92, mTxtOil95, mTxtPrice92, mTxtPrice95, mTxtAddress, mTxtDistance;
    }
}

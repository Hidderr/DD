package com.coco3g.daishu.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
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

public class HomeAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Map<String, String>> mList = new ArrayList<>();
    RelativeLayout.LayoutParams lp, guanggao_lp;

    public HomeAdapter(Context mContext) {
        this.mContext = mContext;
        lp = new RelativeLayout.LayoutParams(Global.screenWidth / 4, Global.screenWidth / 4);
        lp.addRule(RelativeLayout.ALIGN_TOP, R.id.relative_home_item_content);
        lp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.relative_home_item_content);
        lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        //
        guanggao_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Global.screenWidth / 2);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.a_home_item, null);
            viewHolder.mRelativeMyCar = (RelativeLayout) view.findViewById(R.id.relative_home_frag_mycar);
            viewHolder.mRelativeGuangGao = (RelativeLayout) view.findViewById(R.id.relative_home_frag_guang_gao_item);
            viewHolder.mImageThumb = (ImageView) view.findViewById(R.id.image_home_item_thumb);
            viewHolder.mImageThumb.setLayoutParams(lp);
            viewHolder.mImageThumb.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            viewHolder.mTxtTitle = (TextView) view.findViewById(R.id.tv_home_item_title);
            viewHolder.mTxtSubTitle1 = (TextView) view.findViewById(R.id.tv_home_item_subtitle1);
            viewHolder.mTxtSubTitle2 = (TextView) view.findViewById(R.id.tv_home_item_subtitle2);
            viewHolder.mTxtGGTitle = (TextView) view.findViewById(R.id.tv_home_guang_gao_title);
            viewHolder.mTxtGGContent = (TextView) view.findViewById(R.id.tv_home_guang_gao_content);
            //
            viewHolder.mImageGuangGao = (ImageView) view.findViewById(R.id.image_home_frag_image);
            viewHolder.mImageGuangGao.setLayoutParams(guanggao_lp);
            //
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Map<String, String> map = mList.get(position);

        if (!TextUtils.isEmpty(map.get("type"))) {
            viewHolder.mRelativeMyCar.setVisibility(View.VISIBLE);
            viewHolder.mRelativeGuangGao.setVisibility(View.GONE);

        } else {
            viewHolder.mRelativeMyCar.setVisibility(View.GONE);
            viewHolder.mRelativeGuangGao.setVisibility(View.VISIBLE);
            //
            ImageLoader.getInstance().displayImage(map.get("image"), viewHolder.mImageGuangGao, new DisplayImageOptionsUtils().init());
            viewHolder.mTxtGGTitle.setText(map.get("title"));
            viewHolder.mTxtGGContent.setText(map.get("description"));
            //
            viewHolder.mRelativeGuangGao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, WebActivity.class);
                    intent.putExtra("url", map.get("url"));
                    mContext.startActivity(intent);
                }
            });

        }


        return view;
    }

    private class ViewHolder {
        public RelativeLayout mRelativeMyCar, mRelativeGuangGao;
        public ImageView mImageThumb;
        public TextView mTxtTitle, mTxtSubTitle1, mTxtSubTitle2, mTxtGGTitle, mTxtGGContent;
        //
        public ImageView mImageGuangGao;
    }
}

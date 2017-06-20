package com.coco3g.daishu.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class OilTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Map<String, String>> mOrderList = new ArrayList<>();
    private RelativeLayout.LayoutParams mRoot_lp;
    private LinearLayout.LayoutParams mThumb_lp;
    //
    private OnItemClickListener mOnItemClickListener;

    public OilTypeAdapter(Context context) {
        this.context = context;
        mRoot_lp = new RelativeLayout.LayoutParams(Global.screenWidth * 2 / 5, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mRoot_lp.setMargins(Global.dipTopx(context, 5f), 0, Global.dipTopx(context, 5f), 0);
        //
        mThumb_lp = new LinearLayout.LayoutParams(Global.screenWidth / 2, Global.screenWidth / 3);
        mThumb_lp.setMargins(Global.dipTopx(context, 5f), 0, Global.dipTopx(context, 5f), 0);
    }

    public void setList(ArrayList<Map<String, String>> mGoodsList) {
        this.mOrderList = mGoodsList;
        notifyDataSetChanged();
    }

    public void addList(ArrayList<Map<String, String>> mGoodsList) {
        this.mOrderList.addAll(mGoodsList);
        notifyDataSetChanged();
    }

    public void clearList() {
        if (mOrderList != null || mOrderList.size() > 0) {
            mOrderList.clear();
            notifyDataSetChanged();
        }
    }

    public ArrayList<Map<String, String>> getList() {
        return mOrderList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.a_oil_type_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof RecyclerViewHolder) {
            final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) viewHolder;


            final Map<String, String> mapInfo = mOrderList.get(position);
//            //
            ImageLoader.getInstance().displayImage(mapInfo.get("thumb"), recyclerViewHolder.mImageThumb, new DisplayImageOptionsUtils().init());
            //订单类型
            recyclerViewHolder.mTxtTitle.setText(mapInfo.get("title"));

//            //设置点击item后的效果
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //在5.0以上才有的这种水波纹的效果
//                recyclerViewHolder.mRelativeRoot.setBackground(ContextCompat.getDrawable(context, R.drawable.selector_recyclerview_item_bg));
//            } else {
//                recyclerViewHolder.mRelativeRoot.setBackground(ContextCompat.getDrawable(context, R.drawable.selector_txt_grey_bg));
//            }

            //点击Item的监听
            recyclerViewHolder.mLinearRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mOnItemClickListener.ItemClick(position);

                    Intent intent = new Intent(context, WebActivity.class);
                    intent.putExtra("url", mapInfo.get("linkurl"));
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mOrderList == null ? 0 : mOrderList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView mTxtTitle;
        public ImageView mImageThumb;
        public LinearLayout mLinearRoot;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mImageThumb = (ImageView) itemView.findViewById(R.id.image_oil_type_thumb);
            mTxtTitle = (TextView) itemView.findViewById(R.id.tv_oil_type_title);
            mLinearRoot = (LinearLayout) itemView.findViewById(R.id.linear_oil_type_root);
            mImageThumb.setLayoutParams(mThumb_lp);
        }
    }

    public interface OnItemClickListener {
        void ItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


}

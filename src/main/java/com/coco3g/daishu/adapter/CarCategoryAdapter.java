package com.coco3g.daishu.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CarCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private Context mContext;
    private ArrayList<Map<String, Object>> mOrderList = new ArrayList<>();

    RelativeLayout.LayoutParams avatar_lp;
    //
//    private OnItemClickListener mOnItemClickListener;

    int padding_5, padding_2;
    //
    private FooterViewHolder footerViewHolder;  //加载更多的

    public CarCategoryAdapter(Context context) {
        this.mContext = context;
        padding_5 = Global.dipTopx(mContext, 5f);
        padding_2 = Global.dipTopx(mContext, 2f);
        avatar_lp = new RelativeLayout.LayoutParams(Global.screenWidth / 7, Global.screenWidth / 7);
    }

    public void setList(ArrayList<Map<String, Object>> mOrderList) {
        this.mOrderList = mOrderList;
        notifyDataSetChanged();
    }

    public void addList(ArrayList<Map<String, Object>> mOrderList) {
        this.mOrderList.addAll(mOrderList);
        notifyDataSetChanged();
    }

    public void clearList() {
        if (mOrderList != null || mOrderList.size() > 0) {
            mOrderList.clear();
            notifyDataSetChanged();
        }
    }

    public ArrayList<Map<String, Object>> getList() {
        return mOrderList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.a_home_item, parent, false);
        View view = null;

        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.a_home_item, parent, false);
            return new RecyclerViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
//            view = LayoutInflater.from(mContext).inflate(R.layout.view_recycler_footer, parent, false);
//            //
//            footerViewHolder = new FooterViewHolder(view);
//            footerViewHolder.mProgressBar.setVisibility(View.VISIBLE);
//            footerViewHolder.mTxtState.setText(mContext.getResources().getString(R.string.loading));
            return footerViewHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof RecyclerViewHolder) {
            final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) viewHolder;


            final Map<String, Object> demandMap = mOrderList.get(position);
            ArrayList<String> imagesList = (ArrayList<String>) demandMap.get("acce");
        }
    }

    @Override
    public int getItemCount() {
        return mOrderList == null ? 0 : mOrderList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageAvatar;
        public LinearLayout mLinearImages;
        public RelativeLayout mRelativeType, mRelativeRoot;
        public TextView mTxtName, mTxtTimeAndType, mTxtMoney, mTxtType, mTxtTitle, mTxtDesc, mTxtFav, mTxtZan, mTxtComment;

        public RecyclerViewHolder(View itemView) {
            super(itemView);


//            mImageAvatar = (ImageView) itemView.findViewById(R.id.image_home_item_avatar);

        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar mProgressBar;
        public TextView mTxtState;

        public FooterViewHolder(View itemView) {
            super(itemView);

//            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar_recycler_footer);
//            mTxtState = (TextView) itemView.findViewById(R.id.tv_recycler_footer);

        }
    }



//
//    // 收藏订单
//    public void collectOrder(final int position) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("contentid", (String) mOrderList.get(position).get("id"));
//        params.put("typeid", "2");    //	1:对用户的关注，2：需求收藏
//        new BaseDataPresenter(mContext).loadData(DataUrl.COLLECT_ORDER, params, null, new IBaseDataListener() {
//            @Override
//            public void onSuccess(BaseDataBean data) {
//                Map<String, String> collectMap = (Map<String, String>) data.response;
//                //
//
//                Map<String, Object> orderMap = mOrderList.get(position);
//                orderMap.put("isfav", "1");
//                if (collectMap != null) {
//                    orderMap.put("favs", collectMap.get("favs"));
//                }
//                notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onFailure(BaseDataBean data) {
//                Global.showToast(data.msg, mContext);
//            }
//
//            @Override
//            public void onError() {
//
//            }
//        });
//    }


//    // 点赞
//    public void zanOrder(final int position) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("contentid", (String) mOrderList.get(position).get("id"));
//        params.put("typeid", "2");
//        new BaseDataPresenter(mContext).loadData(DataUrl.ORDER_ZAN, params, null, new IBaseDataListener() {
//            @Override
//            public void onSuccess(BaseDataBean data) {
//                Map<String, String> zanMap = (Map<String, String>) data.response;
//
//                Map<String, Object> orderMap = mOrderList.get(position);
//                orderMap.put("iszan", "1");
//                if (zanMap != null) {
//                    orderMap.put("zans", zanMap.get("zans"));
//                }
//                notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onFailure(BaseDataBean data) {
//                Global.showToast(data.msg, mContext);
//            }
//
//            @Override
//            public void onError() {
//
//            }
//        });
//    }


//    public void startRefreshing() {
//        if (footerViewHolder != null) {
//            footerViewHolder.mTxtState.setText("");
//            footerViewHolder.mProgressBar.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    public void startLoadMore() {
//        if (footerViewHolder != null) {
//            footerViewHolder.mTxtState.setText(mContext.getResources().getString(R.string.loading));
//            footerViewHolder.mProgressBar.setVisibility(View.VISIBLE);
//        }
//    }
//
//    public void onLoadMoreComplete() {
//        if (footerViewHolder != null) {
//            footerViewHolder.mTxtState.setText(mContext.getResources().getString(R.string.no_more_remind));
//            footerViewHolder.mProgressBar.setVisibility(View.GONE);
//        }
//    }
//
//
//    public interface OnItemClickListener {
//        void ItemClick(int position);
//    }
//
//    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//        mOnItemClickListener = onItemClickListener;
//    }


}

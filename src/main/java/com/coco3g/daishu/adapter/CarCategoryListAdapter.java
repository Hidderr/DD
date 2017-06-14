package com.coco3g.daishu.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.CarShopActivity;
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CarCategoryListAdapter extends BaseRecyclerAdapter<CarCategoryListAdapter.SimpleAdapterViewHolder> {
    private Context mContext;
    private int largeCardHeight, smallCardHeight;

    private LinearLayout.LayoutParams image_lp;

    private ArrayList<Map<String, String>> mList = new ArrayList<>();

    public CarCategoryListAdapter(Context context) {
        mContext = context;
        image_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Global.screenWidth / 2);
    }

    public void setList(ArrayList<Map<String, String>> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void addList(ArrayList<Map<String, String>> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public ArrayList<Map<String, String>> getList() {
        return mList;
    }

    public void clearList() {
        if (mList != null && mList.size() > 0) {
            mList.clear();
        }
        notifyDataSetChanged();
    }


    @Override
    public int getAdapterItemViewType(int position) {
        return 0;
    }

    @Override
    public int getAdapterItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public SimpleAdapterViewHolder getViewHolder(View view) {
        return new SimpleAdapterViewHolder(view, false);
    }


    @Override
    public SimpleAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_car_category_list_item, parent, false);
        SimpleAdapterViewHolder vh = new SimpleAdapterViewHolder(v, true);
        return vh;
    }

    @Override
    public void onBindViewHolder(SimpleAdapterViewHolder holder, int position, boolean isItem) {
        if (holder instanceof SimpleAdapterViewHolder) {

            final Map<String, String> map = mList.get(position);

            //图片
            ImageLoader.getInstance().displayImage(map.get("thumb"), holder.mImageThumb, new DisplayImageOptionsUtils().init());
            //标题
            holder.mTxtTitle.setText(map.get("title"));
            //
            holder.mTxtContent.setText(map.get("description"));
            //价格
            holder.mTxtPrice.setText("￥" + map.get("price"));
            //
            holder.mLinearRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, WebActivity.class);
                    intent.putExtra("url", map.get("linkurl"));
                    mContext.startActivity(intent);
                }
            });
            //加入购物车
            holder.mImageShoppingCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addShoppingCart(map.get("id"));
                }
            });
        }

    }


    public class SimpleAdapterViewHolder extends RecyclerView.ViewHolder {


        private LinearLayout mLinearRoot;
        private ImageView mImageThumb, mImageShoppingCart;
        private TextView mTxtTitle, mTxtContent, mTxtPrice;


        public SimpleAdapterViewHolder(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                mLinearRoot = (LinearLayout) itemView.findViewById(R.id.linear_car_category_list_itme_root);
                mImageThumb = (ImageView) itemView.findViewById(R.id.image_car_category_list_item_thumb);
                mImageShoppingCart = (ImageView) itemView.findViewById(R.id.image_car_category_list_item_shopping_cart);
                mTxtTitle = (TextView) itemView.findViewById(R.id.tv_car_category_list_item_name);
                mTxtContent = (TextView) itemView.findViewById(R.id.tv_car_category_list_item_desc);
                mTxtPrice = (TextView) itemView.findViewById(R.id.tv_car_category_list_item_money);
                //
                mImageThumb.setLayoutParams(image_lp);
            }

        }
    }

    //添加购物车
    public void addShoppingCart(String id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("contentid", id);
        Log.e("购物车", id + "");
        new BaseDataPresenter(mContext).loadData(DataUrl.ADD_SHOPPING_CART, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Global.showToast("添加购物车成功", mContext);
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, mContext);
            }

            @Override
            public void onError() {
            }
        });
    }


}
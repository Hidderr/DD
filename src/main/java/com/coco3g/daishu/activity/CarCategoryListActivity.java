package com.coco3g.daishu.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.CarCategoryListAdapter;
import com.coco3g.daishu.adapter.ViewPagerAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.view.CategoryListView;
import com.coco3g.daishu.view.CustomFooterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CarCategoryListActivity extends BaseActivity implements View.OnClickListener {

    private TabLayout mTabLayout;
    private TextView mEditSearch;
    private ImageView mImageBack;
    private TextView mTxtAddMyCar;
    //
    int currPager = 1;  //当前第几个页面

    RecyclerView recyclerView;
    CarCategoryListAdapter mAdapter;
    XRefreshView xRefreshView;
    View mHeadView;
    ProgressBar mProgressBar;


    HashMap<Integer, TextView> mTabLayoutItemMap = new HashMap<>();
    String[] mViewPagerTitles = new String[]{"综合", "销量", "价格", "筛选"};
    //
    int currTab = 0;

    String typeName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_category_list);
        typeName = getIntent().getStringExtra("typename");
        initView();
    }

    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress_car_category_list);
        mImageBack = (ImageView) findViewById(R.id.image_car_category_list_back);
        mTabLayout = (TabLayout) findViewById(R.id.tablayout_car_category_list);
        mEditSearch = (TextView) findViewById(R.id.edit_car_category_list_search);
        mEditSearch.setHint(typeName);
        //
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLayout.getLayoutParams();
        lp.height = Global.screenHeight / 18;
        mTabLayout.setLayoutParams(lp);
        // 添加tablayout的分割线
        LinearLayout linearLayout = (LinearLayout) mTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerPadding(Global.dipTopx(this, 5));
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this, R.drawable.shape_tablayout_divider));
        //

        xRefreshView = (XRefreshView) findViewById(R.id.xrefreshview_car_category_list);
        xRefreshView.setPullLoadEnable(true);
        recyclerView = (RecyclerView) findViewById(R.id.rv_car_category_list);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        //
        mAdapter = new CarCategoryListAdapter(this);
        mHeadView = mAdapter.setHeaderView(R.layout.view_category_list_header, recyclerView);
        mTxtAddMyCar = (TextView) mHeadView.findViewById(R.id.tv_category_list_header);
        // 静默加载模式不能设置footerview
        recyclerView.setAdapter(mAdapter);
        //
        //设置刷新完成以后，headerview固定的时间
        xRefreshView.setPinnedTime(500);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setMoveForHorizontal(true);
        xRefreshView.setAutoLoadMore(true);
        xRefreshView.setPullRefreshEnable(false);
        //
        if (mAdapter.getCustomLoadMoreView() == null) {
            mAdapter.setCustomLoadMoreView(new CustomFooterView(CarCategoryListActivity.this));
        }
        //
        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                super.onRefresh();

                Log.e("刷新", "刷新");
                xRefreshView.stopRefresh();

            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
                Log.e("刷新", "加载更多****");
                currPager++;
                getCarGoodsList(false);

            }
        });
        for (int i = 0; i < mViewPagerTitles.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText("tab"));
        }
        for (int i = 0; i < mViewPagerTitles.length; i++) {
            mTabLayout.getTabAt(i).setCustomView(getTabView(i));
        }
        //
        mEditSearch.setOnClickListener(this);
        mImageBack.setOnClickListener(this);
        mTxtAddMyCar.setOnClickListener(this);

        //
        getCarGoodsList(true);
    }

    private View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.a_home_tablayout_item_icon, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_home_tablayout_item_icon);
        tv.setText(mViewPagerTitles[position]);
        view.setTag(position + "");
        //
        if (position == 0) {
            tv.setCompoundDrawables(null, null, null, null);
        } else if (position == 1 || position == 2) {
            setDrawable(tv, R.mipmap.pic_order_nomal, 0);
        } else if (position == 3) {
            setDrawable(tv, R.mipmap.pic_order_nomal, 1);
        }
        //
        if (mTabLayoutItemMap.get(position) == null) {
            mTabLayoutItemMap.put(position, tv);
        }
        view.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.image_car_category_list_back:  //返回键
                finish();

                break;
            case R.id.tv_category_list_header:  //添加爱车信息

                break;
        }


        String tag = (String) v.getTag();
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        int tag_int = Integer.parseInt(tag);

        switch (tag_int) {
            case 0:  //综合
                setTabTitle(0);
                mAdapter.clearList();
                getCarGoodsList(true);

                break;

            case 1:  //销量
                setTabTitle(1);
                mAdapter.clearList();
                getCarGoodsList(true);

                break;

            case 2:  //价格
                setTabTitle(2);
                mAdapter.clearList();
                getCarGoodsList(true);

                break;

            case 3:  //筛选
                setTabTitle(3);
                mAdapter.clearList();
                getCarGoodsList(true);


                break;
        }
    }

    //设置tablayout标题
    public void setTabTitle(int selectPosition) {

        for (int i = 0; i < mTabLayoutItemMap.size(); i++) {
            mTabLayoutItemMap.get(i).setSelected(false);
        }

        switch (selectPosition) {
            case 0:  //综合
                mTabLayoutItemMap.get(0).setSelected(true);
                setDrawable(mTabLayoutItemMap.get(1), R.mipmap.pic_order_nomal, 0);
                setDrawable(mTabLayoutItemMap.get(2), R.mipmap.pic_order_nomal, 0);
                setDrawable(mTabLayoutItemMap.get(3), R.mipmap.pic_order_nomal, 1);
                break;
            case 1:  //销量
                mTabLayoutItemMap.get(1).setSelected(true);
                if (currTab == selectPosition) {
                    setDrawable(mTabLayoutItemMap.get(1), R.mipmap.pic_order_asc, 0);
                } else {
                    setDrawable(mTabLayoutItemMap.get(1), R.mipmap.pic_order_dec, 0);
                }
                setDrawable(mTabLayoutItemMap.get(2), R.mipmap.pic_order_nomal, 0);
                setDrawable(mTabLayoutItemMap.get(3), R.mipmap.pic_order_nomal, 1);
                break;

            case 2:  //价格
                mTabLayoutItemMap.get(2).setSelected(true);
                if (currTab == selectPosition) {
                    setDrawable(mTabLayoutItemMap.get(2), R.mipmap.pic_order_asc, 0);
                } else {
                    setDrawable(mTabLayoutItemMap.get(2), R.mipmap.pic_order_dec, 0);
                }
                setDrawable(mTabLayoutItemMap.get(1), R.mipmap.pic_order_nomal, 0);
                setDrawable(mTabLayoutItemMap.get(3), R.mipmap.pic_order_nomal, 1);
                break;
            case 3:  //筛选
                mTabLayoutItemMap.get(3).setSelected(true);
                setDrawable(mTabLayoutItemMap.get(1), R.mipmap.pic_order_nomal, 0);
                setDrawable(mTabLayoutItemMap.get(2), R.mipmap.pic_order_nomal, 0);
                setDrawable(mTabLayoutItemMap.get(3), R.mipmap.pic_order_nomal, 1);
                break;
        }
        currTab = selectPosition;
    }

    public void setDrawable(TextView textView, int resId, int driection) {
        Drawable drawable = ContextCompat.getDrawable(this, resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        if (driection == 0) {  //右
            textView.setCompoundDrawables(null, null, drawable, null);
        } else if (driection == 1) {  //左
            textView.setCompoundDrawables(drawable, null, null, null);
        }
    }


    //获取一级分类
    public void getCarGoodsList(boolean isRefresh) {
        if (isRefresh) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("catid", "27");
        params.put("page", currPager + "");
        new BaseDataPresenter(this).loadData(DataUrl.GET_CAR_GOODS_LIST, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                mProgressBar.setVisibility(View.GONE);
                //
                ArrayList<Map<String, String>> mList = (ArrayList<Map<String, String>>) data.response;
                if (mList == null || mList.size() <= 0) {
                    currPager--;
                    onLoadComplete();
                    return;
                }

                if (mAdapter.getList() == null || mAdapter.getList().size() <= 0) {
                    mAdapter.setList(mList);
                } else {
                    mAdapter.addList(mList);
                }

                onLoadComplete();


            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, CarCategoryListActivity.this);
                currPager--;
                onLoadComplete();
            }

            @Override
            public void onError() {
                onLoadComplete();
                currPager--;
            }
        });
    }


    public void onLoadComplete() {
        xRefreshView.stopRefresh();
        xRefreshView.stopLoadMore();
    }


}

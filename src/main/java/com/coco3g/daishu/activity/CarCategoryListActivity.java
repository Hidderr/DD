package com.coco3g.daishu.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.CarCategoryListAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.view.CustomFooterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CarCategoryListActivity extends BaseActivity implements View.OnClickListener {

    private TabLayout mTabLayout;
    private EditText mEditSearch;
    private ImageView mImageBack;
    private TextView mTxtSearch;
    //    private TextView mTxtAddMyCar;
    //
    int currPager = 1;  //当前第几个页面

    RecyclerView recyclerView;
    CarCategoryListAdapter mAdapter;
    XRefreshView xRefreshView;
    //    View mHeadView;
    ProgressBar mProgressBar;

    HashMap<Integer, TextView> mTabLayoutItemMap = new HashMap<>();
    String[] mViewPagerTitles = new String[]{"综合", "销量", "价格"};
    //
    int currTab = 0;

    String typeName = "", catid = "", searchKey = "";
    String saleOrder = "market_dec";  //销量market_inc：销量（低到高），market_dec：销量（高到低）
    String priceOrder = "price_inc";  //price_inc：价格（低到高），price_dec：价格（高到低）

    //来自搜索
    int fromType = 0;  //来自哪里   0：默认点击的是车载用品，   1：点击搜索按钮进来的


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_category_list);
        typeName = getIntent().getStringExtra("typename");
        catid = getIntent().getStringExtra("catid");
        searchKey = getIntent().getStringExtra("searchKey");
        fromType = getIntent().getIntExtra("fromType", 0);
        initView();
    }

    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress_car_category_list);
        mImageBack = (ImageView) findViewById(R.id.image_car_category_list_back);
        mTabLayout = (TabLayout) findViewById(R.id.tablayout_car_category_list);
        mEditSearch = (EditText) findViewById(R.id.edit_car_category_list_search);
        mTxtSearch = (TextView) findViewById(R.id.tv_car_category_list_search);
        //
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLayout.getLayoutParams();
        lp.height = Global.screenHeight / 18;
        mTabLayout.setLayoutParams(lp);
        xRefreshView = (XRefreshView) findViewById(R.id.xrefreshview_car_category_list);
        xRefreshView.setPullLoadEnable(true);
        recyclerView = (RecyclerView) findViewById(R.id.rv_car_category_list);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        //
        mAdapter = new CarCategoryListAdapter(this);
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
                xRefreshView.stopRefresh();
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
                currPager++;
                if (fromType == 0) {   //来自点击车载用品
                    getCarGoodsList(false);
                } else if (fromType == 1) {  //搜索(进来不做处理)
                    getSearchList(false);
                }

            }
        });
        for (int i = 0; i < mViewPagerTitles.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText("tab"));
        }
        for (int i = 0; i < mViewPagerTitles.length; i++) {
            mTabLayout.getTabAt(i).setCustomView(getTabView(i));
            View tabView = (View) mTabLayout.getTabAt(i).getCustomView().getParent();
            tabView.setTag(i + "");
            tabView.setOnClickListener(this);
        }
        //
        mEditSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //进行搜索操作的方法，在该方法中可以加入mEditSearchUser的非空判断
                    searchKey = mEditSearch.getText().toString().trim();
                    if (TextUtils.isEmpty(searchKey)) {
                        Global.showToast("搜索内容为空", CarCategoryListActivity.this);
                    } else {
                        fromType = 1;//只要是点击了搜索，就成了搜索页面
                        currPager = 1;
                        mAdapter.clearList();
                        getSearchList(true);
                        return true;
                    }
                }
                return false;
            }
        });
        //
        mTxtSearch.setOnClickListener(this);
        mImageBack.setOnClickListener(this);
        //
        if (fromType == 0) {   //来自点击车载用品
            mEditSearch.setHint(typeName);
            getCarGoodsList(true);
        } else if (fromType == 1 && !TextUtils.isEmpty(searchKey)) {  //搜索(进来不做处理)
            mEditSearch.setText(searchKey);
            getSearchList(true);
        }

    }

    private View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.a_home_tablayout_item_icon, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_home_tablayout_item_icon);
        LinearLayout linearRoot = (LinearLayout) view.findViewById(R.id.linear_a_home_tablayout_item_root);
        tv.setText(mViewPagerTitles[position]);
        linearRoot.setTag(position + "");
        //
        if (position == 0) {
            tv.setCompoundDrawables(null, null, null, null);
        } else if (position == 1 || position == 2) {
            setDrawable(tv, R.mipmap.pic_order_nomal, 0);
        } else if (position == 3) {
            setDrawable(tv, R.mipmap.pic_shai_xuan_unselected_icon, 1);
        }
        //
        if (mTabLayoutItemMap.get(position) == null) {
            mTabLayoutItemMap.put(position, tv);
        }
        return view;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.image_car_category_list_back:  //返回键
                finish();

                break;

            case R.id.tv_car_category_list_search:  //搜索
                searchKey = mEditSearch.getText().toString().trim();
                if (TextUtils.isEmpty(searchKey)) {
                    Global.showToast("搜索内容为空", CarCategoryListActivity.this);
                    return;
                }
                fromType = 1;//只要是点击了搜索，就成了搜索页面
                currPager = 1;
                mAdapter.clearList();
                getSearchList(true);

                break;
        }

        String tag = (String) v.getTag();
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        int tag_int = Integer.parseInt(tag);
//        Global.showToast(tag, this);

        switch (tag_int) {
            case 0:  //综合
                setTabTitle(0);
                mAdapter.clearList();

                if (fromType == 0) {   //来自点击车载用品
                    getCarGoodsList(true);
                } else if (fromType == 1) {  //搜索
                    getSearchList(true);
                }

                break;

            case 1:  //销量
                setTabTitle(1);
                mAdapter.clearList();
                if (fromType == 0) {   //来自点击车载用品
                    getCarGoodsList(true);
                } else if (fromType == 1) {  //搜索
                    getSearchList(true);
                }


                break;

            case 2:  //价格
                setTabTitle(2);
                mAdapter.clearList();
                if (fromType == 0) {   //来自点击车载用品
                    getCarGoodsList(true);
                } else if (fromType == 1) {  //搜索
                    getSearchList(true);
                }


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
                break;
            case 1:  //销量
                mTabLayoutItemMap.get(1).setSelected(true);
                if (currTab == selectPosition) {
                    if (saleOrder.equals("market_dec")) {
                        saleOrder = "market_inc";
                        setDrawable(mTabLayoutItemMap.get(1), R.mipmap.pic_order_asc, 0);
                    } else {
                        saleOrder = "market_dec";
                        setDrawable(mTabLayoutItemMap.get(1), R.mipmap.pic_order_dec, 0);
                    }
                } else {
                    setDrawable(mTabLayoutItemMap.get(1), R.mipmap.pic_order_dec, 0);
                    saleOrder = "market_dec";
                }
                setDrawable(mTabLayoutItemMap.get(2), R.mipmap.pic_order_nomal, 0);

                break;

            case 2:  //价格
                mTabLayoutItemMap.get(2).setSelected(true);
                if (currTab == selectPosition) {
                    if (priceOrder.equals("price_dec")) {
                        priceOrder = "price_inc";
                        setDrawable(mTabLayoutItemMap.get(2), R.mipmap.pic_order_asc, 0);
                    } else {
                        priceOrder = "price_dec";
                        setDrawable(mTabLayoutItemMap.get(2), R.mipmap.pic_order_dec, 0);
                    }
                } else {
                    setDrawable(mTabLayoutItemMap.get(2), R.mipmap.pic_order_asc, 0);
                    priceOrder = "price_inc";
                }
                setDrawable(mTabLayoutItemMap.get(1), R.mipmap.pic_order_nomal, 0);
                break;
        }
        currTab = selectPosition;
    }

    public void setDrawable(TextView textView, int resId, int driection) {
        Drawable drawable = ContextCompat.getDrawable(this, resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        if (driection == 0) {  //右
            textView.setCompoundDrawables(null, null, drawable, null);
        }
    }


    //获取一级分类（点击车载用品进来的，不是搜索）
    public void getCarGoodsList(boolean isRefresh) {
        if (isRefresh) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("gcatid", catid);
        params.put("page", currPager + "");
        //
        if (currTab == 0) { //综合
            params.put("order", "synth");
            Log.e("参数：：：", " catid " + catid + " | order " + params.get("order"));

        } else if (currTab == 1) {  //销量
            params.put("order", saleOrder);
            Log.e("参数：：：", " catid " + catid + " | order " + params.get("order"));

        } else if (currTab == 2) {  //价格
            params.put("order", priceOrder);
            Log.e("参数：：：", " catid " + catid + " | order " + params.get("order"));

        }

        new BaseDataPresenter(this).loadData(DataUrl.GET_CAR_GOODS_LIST, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                mProgressBar.setVisibility(View.GONE);
                //
                ArrayList<Map<String, String>> mList = (ArrayList<Map<String, String>>) data.response;
                if (mList == null || mList.size() <= 0) {
                    if (currPager > 1) {
                        currPager--;
                    }
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
                mProgressBar.setVisibility(View.GONE);
                onLoadComplete();
            }

            @Override
            public void onError() {
                onLoadComplete();
                mProgressBar.setVisibility(View.GONE);
                currPager--;
            }
        });
    }


    //获取一级分类（点击车载用品进来的，不是搜索）
    public void getSearchList(boolean isRefresh) {
        if (isRefresh) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("page", currPager + "");
        params.put("keywords", searchKey);
        //
        if (currTab == 0) { //综合
            params.put("order", "synth");
            Log.e("参数：：：", " | order " + params.get("order"));

        } else if (currTab == 1) {  //销量
            params.put("order", saleOrder);
            Log.e("参数：：：", " | order " + params.get("order"));

        } else if (currTab == 2) {  //价格
            params.put("order", priceOrder);
            Log.e("参数：：：", " | order " + params.get("order"));

        }

        new BaseDataPresenter(this).loadData(DataUrl.GET_CAR_GOODS_SEARCH_LIST, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                mProgressBar.setVisibility(View.GONE);
                //
                ArrayList<Map<String, String>> mList = (ArrayList<Map<String, String>>) data.response;
                if (mList == null || mList.size() <= 0) {
                    if (currPager == 1) {
                        Global.showToast("搜索内容为空", CarCategoryListActivity.this);
                    } else if (currPager > 1) {
                        currPager--;
                    }
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
                mProgressBar.setVisibility(View.GONE);
                onLoadComplete();
            }

            @Override
            public void onError() {
                onLoadComplete();
                mProgressBar.setVisibility(View.GONE);
                currPager--;
            }
        });
    }

    public void onLoadComplete() {
        xRefreshView.stopRefresh();
        xRefreshView.stopLoadMore();
    }


}

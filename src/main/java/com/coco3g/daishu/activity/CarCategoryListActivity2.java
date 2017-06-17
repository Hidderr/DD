package com.coco3g.daishu.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.RelativeLayout;
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
import com.coco3g.daishu.view.ShaiXuanTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class CarCategoryListActivity2 extends BaseActivity implements View.OnClickListener {

    private TabLayout mTabLayout;
    private TextView mEditSearch;
    private ImageView mImageBack;
    //    private TextView mTxtAddMyCar;
    //
    int currPager = 1;  //当前第几个页面

    RecyclerView recyclerView;
    CarCategoryListAdapter mAdapter;
    XRefreshView xRefreshView;
    //    View mHeadView;
    ProgressBar mProgressBar;

    HashMap<Integer, TextView> mTabLayoutItemMap = new HashMap<>();
    String[] mViewPagerTitles = new String[]{"综合", "销量", "价格", "筛选"};
    //
    int currTab = 0;

    String typeName = "", catid = "";
    String saleOrder = "market_dec";  //销量market_inc：销量（低到高），market_dec：销量（高到低）
    String priceOrder = "price_inc";  //price_inc：价格（低到高），price_dec：价格（高到低）

    ///*右侧滑页面*/
    Map<String, Object> shaixuanMap = null;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mRelativeAddress, mRelativeRightView;
    private LinearLayout mLinearPinPai, mLinearAddress, mLinearCityList, mLinearProvinceList, mLinearGoodsType;
    private EditText mEditLowPrice, mEditHighPrice;
    private TextView mTxtAddress, mTxtReset, mTxtConfirm;
    private ArrayList<Map<String, String>> pinPaiList = new ArrayList<>();
    private ArrayList<Map<String, String>> cityList = new ArrayList<>();
    private ArrayList<Map<String, String>> provinceList = new ArrayList<>();
    private ArrayList<Map<String, String>> goodsTypeList = new ArrayList<>();
    //
    private HashMap<ShaiXuanTextView, Boolean> pinPaiMap = new HashMap<>();
    private HashMap<ShaiXuanTextView, Boolean> cityMap = new HashMap<>();
    private HashMap<ShaiXuanTextView, Boolean> provinceMap = new HashMap<>();
    private HashMap<ShaiXuanTextView, Boolean> goodsTypeMap = new HashMap<>();
    //筛选的条件
    String brand = "", minprice = "", maxprice = "", areaid = "", type = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_category_list);
        typeName = getIntent().getStringExtra("typename");
        catid = getIntent().getStringExtra("catid");
        initView();
    }

    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress_car_category_list);
        mImageBack = (ImageView) findViewById(R.id.image_car_category_list_back);
        mTabLayout = (TabLayout) findViewById(R.id.tablayout_car_category_list);
        mEditSearch = (TextView) findViewById(R.id.edit_car_category_list_search);
        mRelativeRightView = (RelativeLayout) findViewById(R.id.relative_category_list_rightview);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_car_category_list);
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
//        mHeadView = mAdapter.setHeaderView(R.layout.view_category_list_header, recyclerView);
//        mTxtAddMyCar = (TextView) mHeadView.findViewById(R.id.tv_category_list_header);
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
            mAdapter.setCustomLoadMoreView(new CustomFooterView(CarCategoryListActivity2.this));
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
                getCarGoodsList(false);

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
        /*右侧滑*/
        mRelativeAddress = (RelativeLayout) findViewById(R.id.relative_category_list_address);
        mLinearPinPai = (LinearLayout) findViewById(R.id.linear_category_list_pin_pai);
        mLinearAddress = (LinearLayout) findViewById(R.id.linear_category_list_address);
        mLinearCityList = (LinearLayout) findViewById(R.id.linear_category_list_address_city);
        mLinearProvinceList = (LinearLayout) findViewById(R.id.linear_category_list_address_province);
        mLinearGoodsType = (LinearLayout) findViewById(R.id.linear_category_list_goods_type);
        mEditLowPrice = (EditText) findViewById(R.id.edit_category_list_low);
        mEditHighPrice = (EditText) findViewById(R.id.edit_category_list_high);
        mTxtAddress = (TextView) findViewById(R.id.tv_category_list_address);
        mTxtReset = (TextView) findViewById(R.id.tv_category_list_reset);
        mTxtConfirm = (TextView) findViewById(R.id.tv_category_list_confirm);
        //
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        //
        mEditSearch.setOnClickListener(this);
        mImageBack.setOnClickListener(this);
//        mTxtAddMyCar.setOnClickListener(this);
        mRelativeAddress.setOnClickListener(this);
        mTxtReset.setOnClickListener(this);
        mTxtConfirm.setOnClickListener(this);
        //
        getCarGoodsList(true);
    }

    private View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.a_home_tablayout_item_icon, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_home_tablayout_item_icon);
        LinearLayout linearRoot = (LinearLayout) view.findViewById(R.id.linear_a_home_tablayout_item_root);
        tv.setText(mViewPagerTitles[position]);
//        view.setTag(position + "");
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
//        linearRoot.setOnClickListener(this);
        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shaixuanMap == null) {
            getShaiXuanType();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.image_car_category_list_back:  //返回键
                finish();

                break;

            case R.id.tv_category_list_reset:  //筛选重置
                resetShaiXuan();

                break;

            case R.id.tv_category_list_confirm:  //筛选确定
                getShaiXuanResult();

                break;

            case R.id.relative_category_list_address:  //展开地址
                if (mLinearAddress.getVisibility() == View.VISIBLE) {
                    mLinearAddress.setVisibility(View.GONE);
                    Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.pic_arrow_down);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mTxtAddress.setCompoundDrawables(null, null, drawable, null);
                } else {
                    mLinearAddress.setVisibility(View.VISIBLE);
                    Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.pic_arrow_up);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mTxtAddress.setCompoundDrawables(null, null, drawable, null);
                }

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
                openDrawerLayout(mRelativeRightView);

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
                setDrawable(mTabLayoutItemMap.get(3), R.mipmap.pic_shai_xuan_unselected_icon, 1);
                break;
            case 1:  //销量
                mTabLayoutItemMap.get(1).setSelected(true);
                if (currTab == selectPosition) {
                    setDrawable(mTabLayoutItemMap.get(1), R.mipmap.pic_order_asc, 0);
                    if (saleOrder.equals("market_dec")) {
                        saleOrder = "market_inc";
                    } else {
                        saleOrder = "market_dec";
                    }
                } else {
                    setDrawable(mTabLayoutItemMap.get(1), R.mipmap.pic_order_dec, 0);
                    saleOrder = "market_dec";
                }
                setDrawable(mTabLayoutItemMap.get(2), R.mipmap.pic_order_nomal, 0);
                setDrawable(mTabLayoutItemMap.get(3), R.mipmap.pic_shai_xuan_unselected_icon, 1);

                break;

            case 2:  //价格
                mTabLayoutItemMap.get(2).setSelected(true);
                if (currTab == selectPosition) {
                    setDrawable(mTabLayoutItemMap.get(2), R.mipmap.pic_order_dec, 0);
                    if (priceOrder.equals("price_dec")) {
                        priceOrder = "price_inc";
                    } else {
                        priceOrder = "price_dec";
                    }
                } else {
                    setDrawable(mTabLayoutItemMap.get(2), R.mipmap.pic_order_asc, 0);
                    priceOrder = "price_inc";
                }
                setDrawable(mTabLayoutItemMap.get(1), R.mipmap.pic_order_nomal, 0);
                setDrawable(mTabLayoutItemMap.get(3), R.mipmap.pic_shai_xuan_unselected_icon, 1);
                break;
            case 3:  //筛选
                mTabLayoutItemMap.get(3).setSelected(true);
                setDrawable(mTabLayoutItemMap.get(1), R.mipmap.pic_order_nomal, 0);
                setDrawable(mTabLayoutItemMap.get(2), R.mipmap.pic_order_nomal, 0);
                setDrawable(mTabLayoutItemMap.get(3), R.mipmap.pic_shai_xuan_selected_icon, 1);
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

    /**************右边侧滑代码
     * @param content
     * @param hashMap
     * @param addLinear
     * @param type****************/   //筛选的类型

    //添加textview
    public void addTextView(final ArrayList<Map<String, String>> content, final HashMap<ShaiXuanTextView, Boolean> hashMap, LinearLayout addLinear, final int type) {
        if (content == null || content.size() <= 0) {
            addLinear.setVisibility(View.GONE);
            return;
        }
        LinearLayout[] linears = new LinearLayout[(content.size() / 3) + 1];
        for (int i = 0; i < linears.length; i++) {
            LinearLayout linearLayout = new LinearLayout(this);
            LinearLayout.LayoutParams linearLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearLP.setMargins(0, Global.dipTopx(this, 5f), 0, 0);
            linearLayout.setLayoutParams(linearLP);
            linearLayout.setWeightSum(3);
            linears[i] = linearLayout;
        }
        //textview的layoutparams
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < content.size(); i++) {
            final ShaiXuanTextView textView = new ShaiXuanTextView(this);
            lp.weight = 1;
            textView.setLayoutParams(lp);
            textView.setTag(i);
            textView.setText(content.get(i).get("title"));
//            content.get(i).put("selected", "0");
            hashMap.put(textView, false);
            textView.setOnClickTextViewListener(new ShaiXuanTextView.OnClickTextViewListener() {
                @Override
                public void textViewChoosed() {
                    setOnlyOneChoosed(textView, hashMap);
                    if (type == 1) {  //城市
                        setOnlyOneChoosed(null, provinceMap);
                    } else if (type == 2) {  //省
                        setOnlyOneChoosed(null, cityMap);
                    }
                }
            });
            linears[i / 4].addView(textView);
        }
        for (int j = 0; j < linears.length; j++) {
            addLinear.addView(linears[j]);
        }

    }

    //设置筛选的是时候,点击其中一个textview后,设置其他不选中
    public void setOnlyOneChoosed(ShaiXuanTextView textView, HashMap<ShaiXuanTextView, Boolean> hashMap) {

        if (hashMap == null || hashMap.size() == 0) {
            return;
        }
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            hashMap.put(((ShaiXuanTextView) entry.getKey()), false);
            ((ShaiXuanTextView) entry.getKey()).setSelected(false);
        }
        if (textView != null) {
            hashMap.put(textView, true);
            textView.setSelected(true);
        }
    }


    //筛选重置
    public void resetShaiXuan() {
        minprice = maxprice = brand = areaid = type = "";
        mEditLowPrice.setText("");
        mEditHighPrice.setText("");
        resetOneShaiXuan(pinPaiMap);
        resetOneShaiXuan(cityMap);
        resetOneShaiXuan(provinceMap);
        resetOneShaiXuan(goodsTypeMap);

    }

    //筛选重置
    public void resetOneShaiXuan(HashMap<ShaiXuanTextView, Boolean> hashMap) {
        if (hashMap == null || hashMap.size() == 0) {
            return;
        }
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            hashMap.put(((ShaiXuanTextView) entry.getKey()), false);
            ((ShaiXuanTextView) entry.getKey()).setSelected(false);
        }
    }

    public void getShaiXuanResult() {
        //
        minprice = mEditLowPrice.getText().toString().trim();
        maxprice = mEditHighPrice.getText().toString().trim();
        if (!TextUtils.isEmpty(minprice) && !TextUtils.isEmpty(maxprice)) {
            int lowPrice = Integer.parseInt(minprice);
            int highPrice = Integer.parseInt(maxprice);
            if (lowPrice >= highPrice) {
                Global.showToast("价格输入有误", this);
                return;
            }
        }
        getSelectedShaiXuanTypeId(pinPaiMap, 0, pinPaiList);
        getSelectedShaiXuanTypeId(cityMap, 1, cityList);
        getSelectedShaiXuanTypeId(provinceMap, 2, provinceList);
        getSelectedShaiXuanTypeId(goodsTypeMap, 3, goodsTypeList);
        //
        closeDrawerLayout(mRelativeRightView);
        // 先隐藏键盘
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        //
        mAdapter.clearList();
        getCarGoodsList(true);

    }

    //遍历Map,得到选中的id
    private void getSelectedShaiXuanTypeId(HashMap map, int selected_type, ArrayList<Map<String, String>> typeList) {
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if ((Boolean) entry.getValue()) {
                ShaiXuanTextView textView = (ShaiXuanTextView) entry.getKey();
                int position = (int) textView.getTag();
                if (selected_type == 0) {
                    brand = typeList.get(position).get("id");
                } else if (selected_type == 1) {
                    areaid = typeList.get(position).get("id");
                } else if (selected_type == 2) {
                    areaid = typeList.get(position).get("id");
                } else if (selected_type == 3) {
                    type = typeList.get(position).get("id");
                }
            }
        }
    }

    //开启侧滑
    public void openDrawerLayout(RelativeLayout relativeDrawerLayout) {
        mDrawerLayout.setDrawerLockMode(mDrawerLayout.LOCK_MODE_UNLOCKED);
        mDrawerLayout.openDrawer(relativeDrawerLayout);
    }

    //关闭侧滑
    public void closeDrawerLayout(RelativeLayout relativeDrawerLayout) {
        mDrawerLayout.closeDrawer(relativeDrawerLayout);
        mDrawerLayout.setDrawerLockMode(mDrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }


    //获取筛选的类型
    public void getShaiXuanType() {
        HashMap<String, String> params = new HashMap<>();
        params.put("gcatid", catid);

        new BaseDataPresenter(this).loadData(DataUrl.GET_SHAI_XUAN_TYPE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                shaixuanMap = (Map<String, Object>) data.response;
                pinPaiList = (ArrayList<Map<String, String>>) shaixuanMap.get("brand");
                cityList = (ArrayList<Map<String, String>>) shaixuanMap.get("recom_city");
                provinceList = (ArrayList<Map<String, String>>) shaixuanMap.get("province");
                goodsTypeList = (ArrayList<Map<String, String>>) shaixuanMap.get("goodstype");
                //
                addTextView(pinPaiList, pinPaiMap, mLinearPinPai, 0);
                addTextView(cityList, cityMap, mLinearCityList, 1);
                addTextView(provinceList, provinceMap, mLinearProvinceList, 2);
                addTextView(goodsTypeList, goodsTypeMap, mLinearGoodsType, 3);

            }

            @Override
            public void onFailure(BaseDataBean data) {
            }

            @Override
            public void onError() {
            }
        });
    }

    //获取一级分类
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

        } else if (currTab == 3) {  //筛选
            if (!TextUtils.isEmpty(brand)) {
                params.put("brand", brand);
            }
            if (!TextUtils.isEmpty(minprice)) {
                params.put("minprice", minprice);
            }
            if (!TextUtils.isEmpty(maxprice)) {
                params.put("maxprice", maxprice);
            }
            if (!TextUtils.isEmpty(areaid)) {
                params.put("areaid", areaid);
            }
            if (!TextUtils.isEmpty(type)) {
                params.put("type", type);
            }
            Log.e("参数：：：", " catid " + catid + " | brand " + brand + " | minprice " + minprice + " | maxprice " + maxprice + " | areaid " + areaid + " | type " + type);
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
                Global.showToast(data.msg, CarCategoryListActivity2.this);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDrawerLayout.isDrawerOpen(mRelativeRightView)) {
                mDrawerLayout.closeDrawer(mRelativeRightView);
//                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                return true;
            } else {
                finish();
            }
        }
        return false;
    }
}

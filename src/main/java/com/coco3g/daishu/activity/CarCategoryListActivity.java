package com.coco3g.daishu.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.CategoryOneAdapter;
import com.coco3g.daishu.adapter.CategoryTwoAdapter;
import com.coco3g.daishu.adapter.ViewPagerAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.view.CategoryListView;
import com.coco3g.daishu.view.MyGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CarCategoryListActivity extends BaseActivity implements View.OnClickListener {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeVPBg;
    private TextView mEditSearch;
    //
    int currPager = 0;  //当前第几个页面

    HashMap<Integer, TextView> mTabLayoutItemMap = new HashMap<>();
    String[] mViewPagerTitles = new String[]{"推荐", "最新", "价格", "筛选"};
    ArrayList<View> mViewList = new ArrayList<>();
    CategoryListView mListView1, mListView2, mListView3, mListView4;
    private ViewPagerAdapter mAdapter;
    //
    ArrayList<Map<String, String>> priceTitleList = new ArrayList<>();
    String[] priceTitles = new String[]{"0-100", "100-500", "500-1000", "1000以上"};
    int priceSelectedIndex = -1;  //价格选中的第几个
    int shaixuanSelectedIndex = -1;  //筛选页面选中的第几个
    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_demand);
        initView();
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.tablayout_car_category_list);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_car_category_list);
        mEditSearch = (TextView) findViewById(R.id.edit_car_category_list_search);
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
        mListView1 = new CategoryListView(this);
        mListView2 = new CategoryListView(this);
        mListView3 = new CategoryListView(this);
        mListView4 = new CategoryListView(this);
        mViewList.add(mListView1);
        mViewList.add(mListView2);
        mViewList.add(mListView3);
        mViewList.add(mListView4);
        //
        mAdapter = new ViewPagerAdapter(mViewList, mViewPagerTitles);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mAdapter);
        //
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currPager = position;
                //
                changeTxtDrawable(position);
                //
//                DemandListView homeListView = (DemandListView) mViewList.get(position);
//                if (position + 1 == 3 || position + 1 == 4) {  //价格和筛选 默认不传参数，默认加载全部的
//                    homeListView.refreshData("", "", "");
//                } else {   //价格的时候，默认是从低到高
//                    homeListView.refreshData((position + 1) + "", "", "");
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        for (int i = 0; i < mViewPagerTitles.length; i++) {
            mTabLayout.getTabAt(i).setCustomView(getTabView(i));
        }
        //
        for (int i = 0; i < 4; i++) {
            Map<String, String> priceMap = new HashMap<>();
            priceMap.put("title", priceTitles[i]);
            priceTitleList.add(priceMap);
        }
        //
        mEditSearch.setOnClickListener(this);
    }

    private View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.a_home_tablayout_item_icon, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_home_tablayout_item_icon);
        tv.setText(mViewPagerTitles[position]);
//        tv.setTag(position);
        view.setTag(position);
        //
        if (position == 0 || position == 1) {
            tv.setCompoundDrawables(null, null, null, null);
        }
        //
        if (mTabLayoutItemMap.get(position) == null) {
            mTabLayoutItemMap.put(position, tv);
        }
        view.setOnClickListener(this);
        return view;
    }

    public void changeTxtDrawable(int position) {
        TextView txtPrice = mTabLayoutItemMap.get(2);
        TextView txtShaiXuan = mTabLayoutItemMap.get(3);
        Drawable selectDrawable = null;
        Drawable unselectDrawable = null;
        selectDrawable = ContextCompat.getDrawable(this, R.mipmap.pic_menu_downdrag_select);
        selectDrawable.setBounds(0, 0, selectDrawable.getMinimumWidth(), selectDrawable.getMinimumHeight());
        unselectDrawable = ContextCompat.getDrawable(this, R.mipmap.pic_menu_downdrag_unselect);
        unselectDrawable.setBounds(0, 0, unselectDrawable.getMinimumWidth(), unselectDrawable.getMinimumHeight());
        if (position == 2) {
            txtPrice.setCompoundDrawables(null, null, selectDrawable, null);
            txtShaiXuan.setCompoundDrawables(null, null, unselectDrawable, null);
        } else if (position == 3) {
            txtPrice.setCompoundDrawables(null, null, unselectDrawable, null);
            txtShaiXuan.setCompoundDrawables(null, null, selectDrawable, null);
        } else {
            txtPrice.setCompoundDrawables(null, null, unselectDrawable, null);
            txtShaiXuan.setCompoundDrawables(null, null, unselectDrawable, null);
        }
    }


    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.time_forget_password_get_verification:  //获取验证码
//
//                break;
//            case R.id.tv_forget_password_change_password:  //修改密码
//
//                break;
//        }
    }


//    //获取一级分类
//    public void getOneCategoryList() {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("all", "0");
//        params.put("catid", "0");
//        new BaseDataPresenter(this).loadData(DataUrl.GET_CAR_ONE_CATEGORY, params, null, new IBaseDataListener() {
//            @Override
//            public void onSuccess(BaseDataBean data) {
//                ArrayList<Map<String, Object>> mList = (ArrayList<Map<String, Object>>) data.data;
//                mOneAdapter.setList(mList);
//                //
//                getTwoCategoryList(mList.get(0).get("catid") + "");
//
//            }
//
//            @Override
//            public void onFailure(BaseDataBean data) {
//                Global.showToast(data.msg, CarCategoryListActivity.this);
//            }
//
//            @Override
//            public void onError() {
//
//            }
//        });
//    }
//
//
//    //获取一级分类
//    public void getTwoCategoryList(String catid) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("all", "0");
//        params.put("catid", catid);
//        new BaseDataPresenter(this).loadData(DataUrl.GET_CAR_TWO_CATEGORY, params, null, new IBaseDataListener() {
//            @Override
//            public void onSuccess(BaseDataBean data) {
//                ArrayList<Map<String, Object>> mList = (ArrayList<Map<String, Object>>) data.data;
//                mTwoAdapter.setList(mList);
//                //
//                mProgress.setVisibility(View.GONE);
//
//            }
//
//            @Override
//            public void onFailure(BaseDataBean data) {
//                Global.showToast(data.msg, CarCategoryListActivity.this);
//                mProgress.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onError() {
//                mProgress.setVisibility(View.GONE);
//
//            }
//        });
//    }
}

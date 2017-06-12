package com.coco3g.daishu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.CategoryOneAdapter;
import com.coco3g.daishu.adapter.CategoryTwoAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.view.MyGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CategoryActivity extends BaseActivity implements View.OnClickListener {

    ListView mListCategory;
    MyGridView mGridCategory;
    ProgressBar mProgress;
    private final int mGridDataRequestCode = 1;
    CategoryOneAdapter mOneAdapter = null;
    CategoryTwoAdapter mTwoAdapter = null;
    int mCurrOneCategoryIndex = 0;
    String mCurrOneCategoryID = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_category);
        initView();
    }

    private void initView() {
        mOneAdapter = new CategoryOneAdapter(this);
        mTwoAdapter = new CategoryTwoAdapter(this);
        //
        mListCategory = (ListView) findViewById(R.id.list_category_one);
        mGridCategory = (MyGridView) findViewById(R.id.grid_category_two);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        //
        mListCategory.setAdapter(mOneAdapter);
        mGridCategory.setAdapter(mTwoAdapter);
        // mGridCategory.setVerticalSpacing(40);
        // mGridCategory.setHorizontalSpacing(40);
        //
        mListCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (mCurrOneCategoryIndex == position) {
                    return;
                }
                mCurrOneCategoryIndex = position;
                mTwoAdapter.clearList();
                mOneAdapter.setSelectItem(position);
                mListCategory.setItemChecked(position, true);
                //
                mCurrOneCategoryID = mOneAdapter.getList().get(position).get("catid") + "";
                mProgress.setVisibility(View.VISIBLE);
                getTwoCategoryList(mCurrOneCategoryID);
//                if (mCurrOneCategoryID == 1) {
//                    getTwoCategoryList(1, mCurrOneCategoryID);
//                } else {
//                    getTwoCategoryList(0, mCurrOneCategoryID);
//                }

            }
        });
        //
        mGridCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CategoryActivity.this, CarCategoryListActivity.class);
                intent.putExtra("cateid", mTwoAdapter.getList().get(position).get("catid") + "");
                intent.putExtra("title", mTwoAdapter.getList().get(position).get("name") + "");
                startActivityForResult(intent, 1);
            }
        });
        //
        mProgress.setVisibility(View.VISIBLE);
        getOneCategoryList();
        mOneAdapter.setSelectItem(0);
        mListCategory.setItemChecked(0, true);

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


    //获取一级分类
    public void getOneCategoryList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("all", "0");
        params.put("catid", "0");
        new BaseDataPresenter(this).loadData(DataUrl.GET_CAR_ONE_CATEGORY, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, Object>> mList = (ArrayList<Map<String, Object>>) data.data;
                mOneAdapter.setList(mList);
                //
                getTwoCategoryList(mList.get(0).get("catid") + "");

            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, CategoryActivity.this);
            }

            @Override
            public void onError() {

            }
        });
    }


    //获取一级分类
    public void getTwoCategoryList(String catid) {
        HashMap<String, String> params = new HashMap<>();
        params.put("all", "0");
        params.put("catid", catid);
        new BaseDataPresenter(this).loadData(DataUrl.GET_CAR_TWO_CATEGORY, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, Object>> mList = (ArrayList<Map<String, Object>>) data.data;
                mTwoAdapter.setList(mList);
                //
                mProgress.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, CategoryActivity.this);
                mProgress.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                mProgress.setVisibility(View.GONE);

            }
        });
    }
}

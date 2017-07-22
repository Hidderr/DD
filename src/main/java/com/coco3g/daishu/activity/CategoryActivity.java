package com.coco3g.daishu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.CategoryOneAdapter;
import com.coco3g.daishu.adapter.CategoryTwoAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.coco3g.daishu.view.MyGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CategoryActivity extends BaseActivity implements View.OnClickListener {

    ImageView mImageBack;
    EditText mEditSearch;
    ListView mListCategory;
    MyGridView mGridCategory;
    ProgressBar mProgress;
    private final int mGridDataRequestCode = 1;
    CategoryOneAdapter mOneAdapter = null;
    CategoryTwoAdapter mTwoAdapter = null;
    int mCurrOneCategoryIndex = 0;
    String mCurrOneCategoryID = "0";

    ImageView mImageThumb;
    LinearLayout.LayoutParams image_lp;

    String searchKey = "";

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
        mImageBack = (ImageView) findViewById(R.id.image_car_category_back);
        mEditSearch = (EditText) findViewById(R.id.edit_car_category_search);
        mListCategory = (ListView) findViewById(R.id.list_category_one);
        mGridCategory = (MyGridView) findViewById(R.id.grid_category_two);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        mImageThumb = (ImageView) findViewById(R.id.image_car_category_thumb);
        int imageWidth = Global.screenWidth * 3 / 4 - 30;
        image_lp = new LinearLayout.LayoutParams(imageWidth, imageWidth / 3);
        mImageThumb.setLayoutParams(image_lp);
        //
        mListCategory.setAdapter(mOneAdapter);
        mGridCategory.setAdapter(mTwoAdapter);
        //
        mOneAdapter.setOnItemSelectedListener(new CategoryOneAdapter.OnItemSelectedListener() {
            @Override
            public void OnItemClick(int position, String id) {
                if (mCurrOneCategoryIndex == position) {
                    return;
                }
                mCurrOneCategoryIndex = position;
                mTwoAdapter.clearList();
                //
                mOneAdapter.setSelectItem(position);
                mListCategory.setItemChecked(position, true);
                //
                mCurrOneCategoryID = mOneAdapter.getList().get(position).get("id") + "";
                mProgress.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(mOneAdapter.getList().get(position).get("thumb") + "", mImageThumb, new DisplayImageOptionsUtils().init());
                getTwoCategoryList(mCurrOneCategoryID);
            }
        });
        //
        mGridCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CategoryActivity.this, CarCategoryListActivity.class);
                intent.putExtra("typename", mOneAdapter.getList().get(mCurrOneCategoryIndex).get("title") + "");
                intent.putExtra("catid", mTwoAdapter.getList().get(position).get("id") + "");
                startActivityForResult(intent, 1);
            }
        });
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
                        Global.showToast("搜索内容为空", CategoryActivity.this);
                    } else {
                        Intent intent = new Intent(CategoryActivity.this, CarCategoryListActivity.class);
                        intent.putExtra("fromType", 1);
                        intent.putExtra("searchKey", searchKey);
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }
        });
        //
        mImageThumb.setOnClickListener(this);
        mImageBack.setOnClickListener(this);
        mProgress.setVisibility(View.VISIBLE);
        getOneCategoryList();

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.image_car_category_thumb:  //图片
                intent = new Intent(this, WebActivity.class);
                intent.putExtra("url", mOneAdapter.getList().get(mCurrOneCategoryIndex).get("url") + "");
                startActivity(intent);

                break;
            case R.id.image_car_category_back:  //
                finish();
                break;
        }
    }


    //获取一级分类
    public void getOneCategoryList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("gcatid", "9");
        new BaseDataPresenter(this).loadData(DataUrl.GET_CAR_CATEGORY_LIST, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, Object>> mList = (ArrayList<Map<String, Object>>) data.response;
                mOneAdapter.setList(mList);
                mOneAdapter.setSelectItem(0);
                mListCategory.setItemChecked(0, true);
                //
                ImageLoader.getInstance().displayImage(mList.get(0).get("thumb") + "", mImageThumb, new DisplayImageOptionsUtils().init());
                getTwoCategoryList(mList.get(0).get("id") + "");

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
        params.put("page", "1");
        params.put("gcatid", catid);
        new BaseDataPresenter(this).loadData(DataUrl.GET_CAR_CATEGORY_LIST, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, Object>> mList = (ArrayList<Map<String, Object>>) data.response;
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

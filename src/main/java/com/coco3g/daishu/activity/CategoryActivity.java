package com.coco3g.daishu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.coco3g.daishu.view.MyGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

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

    ImageView mImageThumb;

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
        mImageThumb = (ImageView) findViewById(R.id.image_car_category_thumb);
        //
        mListCategory.setAdapter(mOneAdapter);
        mGridCategory.setAdapter(mTwoAdapter);
        // mGridCategory.setVerticalSpacing(40);
        // mGridCategory.setHorizontalSpacing(40);
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
//        mListCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // TODO Auto-generated method stub
//                if (mCurrOneCategoryIndex == position) {
//                    return;
//                }
//                mCurrOneCategoryIndex = position;
//                mTwoAdapter.clearList();
//                //
//                mOneAdapter.setSelectItem(position);
//                mListCategory.setItemChecked(position, true);
//                //
//                mCurrOneCategoryID = mOneAdapter.getList().get(position).get("id") + "";
//                mProgress.setVisibility(View.VISIBLE);
//                ImageLoader.getInstance().displayImage(mOneAdapter.getList().get(position).get("thumb") + "", mImageThumb, new DisplayImageOptionsUtils().init());
//                getTwoCategoryList(mCurrOneCategoryID);
//
//            }
//        });
        //
        mGridCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CategoryActivity.this, CarCategoryListActivity.class);
                intent.putExtra("typename", mOneAdapter.getList().get(mCurrOneCategoryIndex).get("title") + "");
                startActivityForResult(intent, 1);
            }
        });
        //
        mProgress.setVisibility(View.VISIBLE);
        getOneCategoryList();

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

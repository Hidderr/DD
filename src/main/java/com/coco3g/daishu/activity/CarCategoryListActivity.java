package com.coco3g.daishu.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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


public class CarCategoryListActivity extends BaseActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_category_list);
        initView();
    }

    private void initView() {

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

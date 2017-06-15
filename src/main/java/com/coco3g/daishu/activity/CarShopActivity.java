package com.coco3g.daishu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.CarShopAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.coco3g.daishu.view.BannerView;
import com.coco3g.daishu.view.SuperRefreshLayout;
import com.coco3g.daishu.view.TopBarView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CarShopActivity extends BaseActivity  {
    private TopBarView mTopbar;
    private ListView mListView, mListViewRight;
    private View mHeadView;
    private SuperRefreshLayout mSuperRefresh, mSuperRefreshRight;
    private CarShopAdapter mAdapter, mCarTypeAdapter;
    private DrawerLayout mDrawerLayout;

    private ProgressBar progressBar;

    private TextView mTxtHot1, mTxtHot2, mTxtHot3, mTxtHot4;
    private TextView[] mTxtHots;
    private BannerView mBannerView;
    private ImageView mImageRecomd1, mImageRecomd2, mImageRecomd3, mImageRecomd4;
    private ImageView[] mImageRecomds;
    private LinearLayout.LayoutParams thumb_lp;


    private int currPage = 1;//品牌分页
    private int currPageType = 1;//某个品牌的车型分页

    private ArrayList<Map<String, String>> recomdBrandList = new ArrayList<>();
    private ArrayList<Map<String, String>> hotBrandList = new ArrayList<>();
    //
    private String currBrandId = "";  //当前选中的哪个品牌的车


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_shop);

        initView();
        mSuperRefresh.setRefreshingLoad();

    }

    private void initView() {
        mTopbar = (TopBarView) findViewById(R.id.topbar_car_shop);
        mTopbar.setTitle(getResources().getString(R.string.car_shop));
        //
        mListView = (ListView) findViewById(R.id.listview_car_shop);
        mListViewRight = (ListView) findViewById(R.id.listview_car_shop_slide_right);
        mSuperRefresh = (SuperRefreshLayout) findViewById(R.id.sr_car_shop);
        mSuperRefreshRight = (SuperRefreshLayout) findViewById(R.id.sr_car_shop_right_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_main);
        progressBar = (ProgressBar) findViewById(R.id.progress_car_shop);
        mAdapter = new CarShopAdapter(this);
        mCarTypeAdapter = new CarShopAdapter(this);
        //
//        listView_lp = new DrawerLayout.LayoutParams(Global.screenWidth * 2 / 3, DrawerLayout.LayoutParams.MATCH_PARENT);
//        mListViewRight.setLayoutParams(listView_lp);
        //
        mHeadView = LayoutInflater.from(this).inflate(R.layout.view_car_shop_headview, null);
        mBannerView = (BannerView) mHeadView.findViewById(R.id.banner_car_shop);
        mBannerView.setScreenRatio(2);
        mImageRecomd1 = (ImageView) mHeadView.findViewById(R.id.tv_car_shop_recommend_1);
        mImageRecomd2 = (ImageView) mHeadView.findViewById(R.id.tv_car_shop_recommend_2);
        mImageRecomd3 = (ImageView) mHeadView.findViewById(R.id.tv_car_shop_recommend_3);
        mImageRecomd4 = (ImageView) mHeadView.findViewById(R.id.tv_car_shop_recommend_4);
        thumb_lp = new LinearLayout.LayoutParams(Global.screenWidth / 8, Global.screenWidth / 8);
        thumb_lp.gravity = Gravity.CENTER_VERTICAL;
        thumb_lp.weight = 0.25f;
        mImageRecomd1.setLayoutParams(thumb_lp);
        mImageRecomd2.setLayoutParams(thumb_lp);
        mImageRecomd3.setLayoutParams(thumb_lp);
        mImageRecomd4.setLayoutParams(thumb_lp);
        mTxtHot1 = (TextView) mHeadView.findViewById(R.id.tv_car_shop_hotsale_1);
        mTxtHot2 = (TextView) mHeadView.findViewById(R.id.tv_car_shop_hotsale_2);
        mTxtHot3 = (TextView) mHeadView.findViewById(R.id.tv_car_shop_hotsale_3);
        mTxtHot4 = (TextView) mHeadView.findViewById(R.id.tv_car_shop_hotsale_4);
        mTxtHots = new TextView[]{mTxtHot1, mTxtHot2, mTxtHot3, mTxtHot4};
        mImageRecomds = new ImageView[]{mImageRecomd1, mImageRecomd2, mImageRecomd3, mImageRecomd4};
        //
        mSuperRefresh.setCanLoadMore();
        mSuperRefresh.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                mAdapter.clearList();
                getBanner();
            }

            @Override
            public void onLoadMore() {
                Log.e("加载更多", " page " + currPage);
                currPage++;
                getCarBrand();

            }
        });
        mSuperRefreshRight.setCanLoadMore();
        mSuperRefreshRight.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                mCarTypeAdapter.clearList();
                currPageType = 1;
                getOneBrandTypeList();
            }

            @Override
            public void onLoadMore() {
                Log.e("加载更多", " currPageType " + currPageType);
                currPageType++;
                getOneBrandTypeList();
            }
        });
        //
        mListView.addHeaderView(mHeadView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currBrandId = mAdapter.getList().get(position - 1).get("id");
                progressBar.setVisibility(View.VISIBLE);
                //
                currPageType = 1;
                mCarTypeAdapter.clearList();
                getOneBrandTypeList();
            }
        });
        //
        mDrawerLayout.setDrawerLockMode(mDrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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
        mListViewRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                closeDrawerLayout();
                Intent intent = new Intent(CarShopActivity.this, CarDetailTypeActivity.class);
                intent.putExtra("title", mCarTypeAdapter.getList().get(position).get("title"));
                intent.putExtra("carTypeId", mCarTypeAdapter.getList().get(position).get("id"));
                startActivity(intent);
            }
        });
        //设置侧滑的宽度
        ViewGroup.LayoutParams para = mSuperRefreshRight.getLayoutParams();//获取drawerlayout的布局
        para.width = Global.screenWidth * 5 / 9;//修改宽度
        mSuperRefreshRight.setLayoutParams(para); //设置修改后的布局。


    }

//    @Override
//    public void onClick(View v) {
//        Intent intent = null;
//        switch (v.getId()) {
//
//            case R.id.tv_car_shop_hotsale_4:  //
//
//                break;
//
//        }
//    }


    //开启侧滑
    public void openDrawerLayout() {
        mDrawerLayout.setDrawerLockMode(mDrawerLayout.LOCK_MODE_UNLOCKED);
        mDrawerLayout.openDrawer(mSuperRefreshRight);
//        mSuperRefreshRight.setRefreshingLoad();
    }

    //关闭侧滑
    public void closeDrawerLayout() {
        mDrawerLayout.closeDrawer(mSuperRefreshRight);
        mDrawerLayout.setDrawerLockMode(mDrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }


    //获取banner图片
    public void getBanner() {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "4");
        new BaseDataPresenter(this).loadData(DataUrl.GET_BANNER_IMAGE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, String>> bannerList = (ArrayList<Map<String, String>>) data.response;
                mBannerView.loadData(bannerList);
                //
//                mListView.setAdapter(mAdapter);
//                mListViewRight.setAdapter(mCarTypeAdapter);
                //
                getRecomdAndHotBrand();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, CarShopActivity.this);
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefresh.onLoadComplete();
            }
        });
    }

    //获取推荐和热销品牌
    public void getRecomdAndHotBrand() {
        HashMap<String, String> params = new HashMap<>();
        new BaseDataPresenter(this).loadData(DataUrl.GET_CAR_BRAND, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Map<String, Object> map = (Map<String, Object>) data.response;

                recomdBrandList = (ArrayList<Map<String, String>>) map.get("recom");
                hotBrandList = (ArrayList<Map<String, String>>) map.get("hot");
                showRecomdAndHotBrand();
                //
                getCarBrand();

            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, CarShopActivity.this);
                currPage--;
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefresh.onLoadComplete();
                currPage--;
            }
        });
    }

    //获取汽车品牌
    public void getCarBrand() {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", currPage + "");
        new BaseDataPresenter(this).loadData(DataUrl.GET_ONE_BRAND_TYPE_LIST, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, String>> brand = (ArrayList<Map<String, String>>) data.response;
                if (brand == null || brand.size() <= 0) {
                    currPage--;
                    mSuperRefresh.onLoadComplete();
                    return;
                }

                if (mAdapter.getList() == null || mAdapter.getList().size() <= 0) {
                    mAdapter.setList(brand);
                    mListView.setAdapter(mAdapter);
                } else {
                    mAdapter.addList(brand);
                }

                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, CarShopActivity.this);
                currPage--;
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefresh.onLoadComplete();
                currPage--;
            }
        });
    }

    public void showRecomdAndHotBrand() {

        for (int i = 0; i < mImageRecomds.length; i++) {
            ImageLoader.getInstance().displayImage(recomdBrandList.get(i).get("thumb"), mImageRecomds[i], new DisplayImageOptionsUtils().init(R.mipmap.pic_default_car_icon));
            final int finalI = i;
            mImageRecomds[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currBrandId = recomdBrandList.get(finalI).get("id");
                    progressBar.setVisibility(View.VISIBLE);
                    //
                    currPageType = 1;
                    mCarTypeAdapter.clearList();
                    getOneBrandTypeList();

                }
            });
        }

        for (int j = 0; j < mTxtHots.length; j++) {
            mTxtHots[j].setText(hotBrandList.get(j).get("title"));
            final int finalJ = j;
            mTxtHots[j].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currBrandId = hotBrandList.get(finalJ).get("id");
                    progressBar.setVisibility(View.VISIBLE);
                    //
                    currPageType = 1;
                    mCarTypeAdapter.clearList();
                    getOneBrandTypeList();
                }
            });
        }
    }


    //获取某个汽车品牌的车型
    public void getOneBrandTypeList() {
        if (TextUtils.isEmpty(currBrandId)) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("pid", currBrandId);
        params.put("page", currPageType + "");
        new BaseDataPresenter(this).loadData(DataUrl.GET_ONE_BRAND_TYPE_LIST, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, String>> carTypeList = (ArrayList<Map<String, String>>) data.response;

                if (carTypeList == null || carTypeList.size() <= 0) {
                    currPageType--;
                    progressBar.setVisibility(View.GONE);
                    mSuperRefreshRight.onLoadComplete();
                    return;
                }

                if (mCarTypeAdapter.getList() == null || mCarTypeAdapter.getList().size() <= 0) {
                    mCarTypeAdapter.setList(carTypeList);
                    mListViewRight.setAdapter(mCarTypeAdapter);
                    openDrawerLayout();
                } else {
                    mCarTypeAdapter.addList(carTypeList);
                }
                progressBar.setVisibility(View.GONE);
                mSuperRefreshRight.onLoadComplete();

            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, CarShopActivity.this);
                currPageType--;
                mSuperRefreshRight.onLoadComplete();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                currPageType--;
                mSuperRefreshRight.onLoadComplete();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDrawerLayout.isDrawerOpen(mSuperRefreshRight)) {
                mDrawerLayout.closeDrawer(mSuperRefreshRight);
                return true;
            } else {
                finish();
            }
        }
        return false;
    }
}

package com.coco3g.daishu.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.OilStoreAdapter;
import com.coco3g.daishu.adapter.OilTypeAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.coco3g.daishu.utils.LocationUtil;
import com.coco3g.daishu.utils.RequestPermissionUtils;
import com.coco3g.daishu.view.ChoosePopupwindow;
import com.coco3g.daishu.view.SuperRefreshLayout;
import com.coco3g.daishu.view.TopBarView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by lisen on 16/2/4.
 */
public class DiscountOilActivity extends BaseActivity implements View.OnClickListener {
    private TopBarView mTopbar;
    private TextView mTxtAddress, mTxtOilType, mTxtChargeYouKa;
    private RelativeLayout mRelativeAddress, mRelativeMoRen, mRelativeShaiXuan;
    private SuperRefreshLayout mSuperRefresh;
    private ListView mListView;
    private OilStoreAdapter mAdapter;
    //    private RecyclerView mRecyclerView;
//    private OilTypeAdapter mOilAdapter;
    private TextView mTxtOilType1, mTxtOilType2;
    private ImageView mImageOilType1, mImageOilType2;
    private LinearLayout mLinearOilType1, mLinearOilType2;
    //
    private View mHeadView;
    //
    private ArrayList<Map<String, String>> addressList = new ArrayList<>();
    private ArrayList<Map<String, String>> orderList = new ArrayList<>();
    private ArrayList<Map<String, String>> shaiXuanList = new ArrayList<>();
    private ArrayList<Map<String, String>> oilList = new ArrayList<>();


    private float mBaseDistance = 10000;  //距离定位点最小的半径

    private double currLat, currLng;


    private ArrayList<Map<String, String>>[] types;
    private int currSelected[] = new int[]{-1, -1, -1};
    private String currLocation = "";//当前所在的区的地址

    private int currPosition;
    private ChoosePopupwindow popupwindow;

    private int currPage = 1;
    //油卡的选项
    String[] oilTypes;
    private int currOilIndex = 0;


    private LinearLayout.LayoutParams mOilThumb_lp;
    private ArrayList<Map<String, String>> mOilTypeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_oil);
        init();
        mSuperRefresh.setRefreshingLoad();
    }


    private void init() {
        mTopbar = (TopBarView) findViewById(R.id.tobbar_discount_oil);
        mTopbar.setTitle("折扣油站");
        TextView textView = new TextView(this);
        textView.setText("历史记录");
        int margin_5 = Global.dipTopx(this, 5f);
        int margin_10 = Global.dipTopx(this, 10f);
        textView.setPadding(margin_5, margin_5, margin_10, margin_5);
        textView.setTextSize(14f);
        textView.setTextColor(getResources().getColor(R.color.text_color_1));
        mTopbar.setRightView(textView);
        mTopbar.setOnClickRightListener(new TopBarView.OnClickRightView() {
            @Override
            public void onClickTopbarView() {
                Intent intent = new Intent(DiscountOilActivity.this, WebActivity.class);
                intent.putExtra("url", Global.H5Map.get("youka"));
                startActivity(intent);
            }
        });
        //
        mTxtAddress = (TextView) findViewById(R.id.tv_discount_oil_address);
        mRelativeAddress = (RelativeLayout) findViewById(R.id.relative_discount_oil_address);
        mRelativeMoRen = (RelativeLayout) findViewById(R.id.relative_discount_oil_moren_order);
        mRelativeShaiXuan = (RelativeLayout) findViewById(R.id.relative_discount_oil_shaixuan);
        mSuperRefresh = (SuperRefreshLayout) findViewById(R.id.sr_discount_oil);
        mListView = (ListView) findViewById(R.id.listview_discount_oil);
        mAdapter = new OilStoreAdapter(this);
        mListView.setAdapter(mAdapter);
        //
        mHeadView = LayoutInflater.from(this).inflate(R.layout.view_discount_oil_header, null);
        mTxtOilType = (TextView) mHeadView.findViewById(R.id.tv_oil_tore_card_type);
        mTxtChargeYouKa = (TextView) mHeadView.findViewById(R.id.tv_dicount_oil_header_charge_youka);
        mTxtOilType1 = (TextView) mHeadView.findViewById(R.id.tv_oil_type_title_1);
        mTxtOilType2 = (TextView) mHeadView.findViewById(R.id.tv_oil_type_title_2);
        mLinearOilType1 = (LinearLayout) mHeadView.findViewById(R.id.linear_discount_oil_1);
        mLinearOilType2 = (LinearLayout) mHeadView.findViewById(R.id.linear_discount_oil_2);
        mImageOilType1 = (ImageView) mHeadView.findViewById(R.id.image_oil_type_thumb_1);
        mImageOilType2 = (ImageView) mHeadView.findViewById(R.id.image_oil_type_thumb_2);
        mOilThumb_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Global.screenWidth / 3 - Global.dipTopx(this, 15f));
        mImageOilType1.setLayoutParams(mOilThumb_lp);
        mImageOilType2.setLayoutParams(mOilThumb_lp);
//        mRecyclerView = (RecyclerView) mHeadView.findViewById(R.id.rv_discount_oil_header_image);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        mRecyclerView.setLayoutManager(linearLayoutManager);
//        mOilAdapter = new OilTypeAdapter(this);
//        mRecyclerView.setAdapter(mOilAdapter);
        //
        mRelativeAddress.setOnClickListener(this);
        mRelativeMoRen.setOnClickListener(this);
        mRelativeShaiXuan.setOnClickListener(this);
        mTxtOilType.setOnClickListener(this);
        mTxtChargeYouKa.setOnClickListener(this);
        mLinearOilType1.setOnClickListener(this);
        mLinearOilType2.setOnClickListener(this);
        //
        mSuperRefresh.setCanLoadMore();
        mSuperRefresh.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                if (currLat == 0 || currLng == 0) {
                    startLocation(true);
                } else {
                    mAdapter.clearList();
                    currPage = 1;
                    getStoreList();
                }
            }

            @Override
            public void onLoadMore() {
                currPage++;
                Log.e("当前页面", currPage + "&&&");
                getStoreList();
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relative_discount_oil_address:  //地址
                currPosition = 0;
                showPopupWidnow(mRelativeAddress, currPosition);

                break;

            case R.id.relative_discount_oil_moren_order:  //默认
                currPosition = 1;
                showPopupWidnow(mRelativeMoRen, currPosition);

                break;

            case R.id.relative_discount_oil_shaixuan:  //筛选
                currPosition = 2;
                showPopupWidnow(mRelativeShaiXuan, currPosition);

                break;

            case R.id.tv_oil_tore_card_type:  //油卡类型
                chooseOilType();

                break;

            case R.id.tv_dicount_oil_header_charge_youka:  //充值油卡

                if (Global.H5Map.get("youka_topup").equals("#")) {
                    return;
                }
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra("url", Global.H5Map.get("youka_topup"));
                startActivity(intent);

                break;

            case R.id.linear_discount_oil_1:  //中石油油卡
                if (mOilTypeList != null && mOilTypeList.size() >= 1) {
                    intent = new Intent(this, WebActivity.class);
                    intent.putExtra("url", mOilTypeList.get(0).get("linkurl"));
                    startActivity(intent);
                }

                break;

            case R.id.linear_discount_oil_2:  //中石化油卡
                if (mOilTypeList != null && mOilTypeList.size() >= 2) {
                    intent = new Intent(this, WebActivity.class);
                    intent.putExtra("url", mOilTypeList.get(1).get("linkurl"));
                    startActivity(intent);
                }

                break;


        }
    }


    //定位
    public void startLocation(boolean isGetList) {
        new RequestPermissionUtils(this).aleraPermission(Manifest.permission.ACCESS_FINE_LOCATION, 1);
        //定位
        new LocationUtil(this).initLocationAndStart(true, 1000, false, null).setAMapLocationChanged(new LocationUtil.AMapLocationChanged() {
            @Override
            public void aMapLocation(AMapLocation aMapLocation) {
                Global.locationCity = aMapLocation.getCity();
                Global.mCurrLat = currLat = aMapLocation.getLatitude();
                Global.mCurrLng = currLng = aMapLocation.getLongitude();
                //
//                mTopbar.setLocationCity(Global.locationCity);
                mTxtAddress.setText(aMapLocation.getDistrict());
                currLocation = aMapLocation.getDistrict();
                Log.e("定位结果", "city " + Global.locationCity + "  mCurrLat   " + Global.mCurrLat + "  mCurrLng" + Global.mCurrLng);
                //
                getShaiXuanList(aMapLocation.getCity());
            }
        });
    }

    private void showPopupWidnow(View view, final int typePosition) {
        if (popupwindow != null) {
            popupwindow.dismiss();
        }
        ArrayList<Map<String, String>> typeList = new ArrayList<>();
        typeList = types[typePosition];
        popupwindow = new ChoosePopupwindow(this, Global.screenWidth, 0, typeList, currSelected[typePosition], null);
        popupwindow.showAsDropDown(view, -5, 0);
        popupwindow.setOnTextSeclectedListener(new ChoosePopupwindow.OnTextSeclectedListener() {
            @Override
            public void textSelected(int position) {
                if (typePosition == 0 || typePosition == 2) {
                    if (typePosition == 0) {
                        currLocation = "";
                    }
                    currSelected[1] = -1;
                } else if (typePosition == 1) {
                    currSelected[0] = -1;
                    currSelected[2] = -1;
                }

                currSelected[typePosition] = position;
                Log.e("当前选中的id", types[typePosition].get(position).get("id"));

                if (typePosition == 0) {
                    mTxtAddress.setText(addressList.get(position).get("title"));
                }

                mSuperRefresh.setRefreshingLoad();
            }
        });
        popupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupwindow = null;
            }
        });
    }


    public void chooseOilType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setSingleChoiceItems(oilTypes, currOilIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (which == currOilIndex) {
                    return;
                } else {
                    currOilIndex = which;
                    mTxtOilType.setText(oilTypes[currOilIndex]);
                    getOilCardList(oilList.get(currOilIndex).get("id"), true);
                }
            }
        });
        builder.setCancelable(true);
        builder.create().show();
    }

    //获取筛选的条件
    public void getShaiXuanList(String cityName) {
        HashMap<String, String> params = new HashMap<>();
        params.put("city", cityName);
        new BaseDataPresenter(this).loadData(DataUrl.GET_DISCOUNT_OIL_TYPE, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Map<String, Object> map = (Map<String, Object>) data.response;
                addressList = (ArrayList<Map<String, String>>) map.get("citylist");
                orderList = (ArrayList<Map<String, String>>) map.get("ordlist");
                shaiXuanList = (ArrayList<Map<String, String>>) map.get("gastype");
                oilList = (ArrayList<Map<String, String>>) map.get("yklist");
                //
                Map<String, String> city = new HashMap<String, String>();
//                city.put("title", Global.locationCity);
                city.put("title", "全部");
                city.put("id", "0");
                addressList.add(0, city);
                //查找当前定位的区
                for (int i = 0; i < addressList.size(); i++) {
                    if (currLocation.equals(addressList.get(i).get("title"))) {
                        currSelected[0] = i;
                        break;
                    }
                }
                //
                types = new ArrayList[]{addressList, orderList, shaiXuanList};
                oilTypes = new String[oilList.size()];
                for (int i = 0; i < oilList.size(); i++) {
                    oilTypes[i] = oilList.get(i).get("title");
                }
                //
                mTxtOilType.setText(oilTypes[0]);
                //
                getOilCardList(oilList.get(0).get("id"), false);
            }

            @Override
            public void onFailure(BaseDataBean data) {
            }

            @Override
            public void onError() {
            }
        });
    }


    //获取油卡类型
    public void getOilCardList(String id, final boolean onlyGetCard) {
        HashMap<String, String> params = new HashMap<>();
//        params.put("gcatid", id);
//        params.put("page", "1");
        new BaseDataPresenter(this).loadData(DataUrl.GET_YOUKA_LIST, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                mOilTypeList = (ArrayList<Map<String, String>>) data.response;
                if (mOilTypeList != null && mOilTypeList.size() > 0) {
                    for (int i = 0; i < mOilTypeList.size(); i++) {
                        if (i == 0) {
                            ImageLoader.getInstance().displayImage(mOilTypeList.get(i).get("thumb"), mImageOilType1, new DisplayImageOptionsUtils().init());
                            mTxtOilType1.setText(mOilTypeList.get(i).get("title"));
                        } else if (i == 1) {
                            ImageLoader.getInstance().displayImage(mOilTypeList.get(i).get("thumb"), mImageOilType2, new DisplayImageOptionsUtils().init());
                            mTxtOilType2.setText(mOilTypeList.get(i).get("title"));
                        }
                    }
//                    mOilAdapter.clearList();
//                    mOilAdapter.setList(mList);
                    if (mListView.getHeaderViewsCount() < 1) {
                        mListView.addHeaderView(mHeadView);
                    }
                }
                if (!onlyGetCard) {
                    getStoreList();
                }
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, DiscountOilActivity.this);
            }

            @Override
            public void onError() {

            }
        });
    }


    //获取附近的加油站列表
    public void getStoreList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("lat", Global.mCurrLat + "");
        params.put("lng", Global.mCurrLng + "");
        params.put("distance", mBaseDistance + "");
        params.put("page", currPage + "");

        //地区选择
        if (currSelected[0] != -1) {
            params.put("area", addressList.get(currSelected[0]).get("id"));
        }

        //默认排序
        if (currSelected[1] != -1) {
            params.put("order", orderList.get(currSelected[1]).get("id"));
        }

        //筛选
        if (currSelected[2] != -1) {
            params.put("gastypeid", shaiXuanList.get(currSelected[2]).get("id"));
        }


        Log.e("地图传参", "area " + params.get("area") + "   order" + params.get("order") + "    gastypeid" + params.get("gastypeid"));
        new BaseDataPresenter(DiscountOilActivity.this).loadData(DataUrl.GET_OIL_STORE_LIST, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {

                ArrayList<Map<String, Object>> oilStoreList = (ArrayList<Map<String, Object>>) data.response;
                if (oilStoreList == null || oilStoreList.size() <= 0) {
                    mSuperRefresh.onLoadComplete();
                    currPage--;
                    return;
                }
                Log.e("门店数量", oilStoreList.size() + "");
                if (mAdapter.getList() == null || mAdapter.getList().size() <= 0) {
                    mAdapter.setList(oilStoreList);
                } else {
                    mAdapter.addList(oilStoreList);
                }
                //
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, DiscountOilActivity.this);
                mSuperRefresh.onLoadComplete();
                currPage--;
            }

            @Override
            public void onError() {
                mSuperRefresh.onLoadComplete();
                currPage--;
            }
        });
    }


}

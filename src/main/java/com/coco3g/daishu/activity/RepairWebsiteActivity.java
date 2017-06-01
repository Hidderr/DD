package com.coco3g.daishu.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.coco3g.daishu.R;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.bean.RepairStoreBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.coco3g.daishu.view.ChoosePopupwindow;
import com.coco3g.daishu.view.MyMapView;
import com.coco3g.daishu.view.TopBarView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RepairWebsiteActivity extends BaseActivity implements View.OnClickListener {
    private MyMapView myMapView;
    private TopBarView mTopbar;
    private RelativeLayout mRelativeStore;
    private RelativeLayout.LayoutParams store_lp, thumb_lp;
    private TextView mTxtName, mTxtAddress, mTxtPhone;
    private ImageView mImageThumb, mImageRoute;
    //
    private String typeid = "";  //获取的地点类型  -1=洗车店，1=维修养护和维修救援，附近门店(不传参)，汽修厂、爱车保姆快修店（根据获取的维修类型id）
    private String title = "";

    private TextView rightView;

    private ArrayList<Map<String, String>> gradeList;  //维修等级
    private int currChooseIndex = -1;

    private Marker detailMarker;
    private LatLonPoint mCurrLatLonPoint = null;
    private PoiItem currentPoi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_website);
        typeid = getIntent().getStringExtra("typeid");
        title = getIntent().getStringExtra("title");

        init(savedInstanceState);

    }

    private void init(Bundle savedInstanceState) {
        mTopbar = (TopBarView) findViewById(R.id.topbar_repair_website);
        mTopbar.setTitle(title);
        rightView = new TextView(this);
        rightView.setText("维修等级");
        rightView.setTextSize(14);
        rightView.setPadding(0, 0, Global.dipTopx(this, 5f), 0);
        rightView.setTextColor(ContextCompat.getColor(this, R.color.text_color_1));
        Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.pic_arrow_down_icon);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        rightView.setCompoundDrawables(null, null, drawable, null);
        if (!TextUtils.isEmpty(typeid) && typeid.equals("1")) {  // -1=洗车店，1=维修养护和维修救援，附近门店(不传参)，汽修厂、爱车保姆快修店（根据获取的维修类型id）
            mTopbar.setRightView(rightView);
        }
        mTopbar.setOnClickRightListener(new TopBarView.OnClickRightView() {
            @Override
            public void onClickTopbarView() {
                if (gradeList == null || gradeList.size() <= 0) {
                    getRepairGradeList();
                } else {
                    showPopupWidnow();
                }
            }
        });
        //
        mTxtName = (TextView) findViewById(R.id.tv_repair_website_store_name);
        mTxtAddress = (TextView) findViewById(R.id.tv_repair_website_store_address);
        mTxtPhone = (TextView) findViewById(R.id.tv_repair_website_store_phone);
        mImageThumb = (ImageView) findViewById(R.id.image_repair_website_store_thumb);
        mImageRoute = (ImageView) findViewById(R.id.image_repair_website_store_route);
        myMapView = (MyMapView) findViewById(R.id.map_repair_website);
        if (!TextUtils.isEmpty(typeid)) {
            myMapView.setTypeid(typeid);
        }
        myMapView.init(savedInstanceState, true, true);
        //
        mRelativeStore = (RelativeLayout) findViewById(R.id.relative_repair_website_repair_store);
        store_lp = new RelativeLayout.LayoutParams(Global.screenWidth, Global.screenHeight / 5);
        store_lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        int margin_10 = Global.dipTopx(this, 10f);
        mRelativeStore.setLayoutParams(store_lp);
        thumb_lp = new RelativeLayout.LayoutParams(Global.screenHeight / 5, Global.screenHeight / 5);
        thumb_lp.addRule(RelativeLayout.CENTER_VERTICAL);
        thumb_lp.setMargins(margin_10, margin_10, Global.dipTopx(this, 5f), Global.dipTopx(this, 5f));
        mImageThumb.setLayoutParams(thumb_lp);
        //
        myMapView.setOnLocationSuccessedListener(new MyMapView.OnLocationSuccessedListener() {
            @Override
            public void locationSuccessed(LatLng latLng) {
                mCurrLatLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
            }
        });
        myMapView.setOnShowStoreListener(new MyMapView.OnShowStoreListener() {
            @Override
            public void showStore(boolean visible, PoiItem mCurrentPoi, Marker detailMarker1) {
                if (visible) {
                    mRelativeStore.setVisibility(View.VISIBLE);
                    if (mCurrentPoi != null) {
                        detailMarker = detailMarker1;
                        currentPoi = mCurrentPoi;
                        setRepairStoreInfo(mCurrentPoi);
                    }
                } else {
                    mRelativeStore.setVisibility(View.GONE);
                }
            }
        });
        //
        mImageRoute.setOnClickListener(this);
        mRelativeStore.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.image_repair_website_store_route:
                intent = new Intent(this, DriveRouteActivity.class);
                PoiItem mCurrentPoi = (PoiItem) detailMarker.getObject();
                RepairStoreBean bean = new RepairStoreBean();
                bean.lat = mCurrentPoi.getLatLonPoint().getLatitude();
                bean.lng = mCurrentPoi.getLatLonPoint().getLongitude();
                bean.photos = mCurrentPoi.getPhotos().get(0).getUrl();
                bean.title = mCurrentPoi.getTitle();
                bean.address = mCurrentPoi.getSnippet();
                bean.phone = mCurrentPoi.getTel();
                //
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", bean);
                intent.putExtras(bundle);
                intent.putExtra("startlat", mCurrLatLonPoint.getLatitude());
                intent.putExtra("startlng", mCurrLatLonPoint.getLongitude());
                startActivity(intent);
                break;

            case R.id.relative_repair_website_repair_store:  //维修点详情
                Intent intent1 = new Intent(this, WebActivity.class);
                String url = DataUrl.BASE_REPAIR_URL + currentPoi.getPoiId();
                intent1.putExtra("url", url);
                startActivity(intent1);
                break;
        }

    }

    public void showPopupWidnow() {
        final ChoosePopupwindow popupwindow = new ChoosePopupwindow(this, Global.screenWidth / 4 - 70, 0, gradeList, currChooseIndex);
        popupwindow.showAsDropDown(rightView, -5, 35);
        popupwindow.setOnTextSeclectedListener(new ChoosePopupwindow.OnTextSeclectedListener() {
            @Override
            public void textSelected(int position) {
                currChooseIndex = position;
                if (gradeList != null) {
                    String typeid = gradeList.get(position).get("id");
                    myMapView.refreshData(typeid);
                }

//                myMapView.clearMarker();

            }
        });
        popupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

    }


    //设置修理店信息
    private void setRepairStoreInfo(final PoiItem mCurrentPoi) {
        mTxtName.setText(mCurrentPoi.getTitle());
        mTxtAddress.setText("地址： " + mCurrentPoi.getSnippet());
        mTxtPhone.setText("电话： " + mCurrentPoi.getTel());
        //
        String thumb = mCurrentPoi.getPhotos().get(0).getUrl();
        if (TextUtils.isEmpty(thumb)) {
            return;
        }
        ImageLoader.getInstance().displayImage(mCurrentPoi.getPhotos().get(0).getUrl(), mImageThumb, new DisplayImageOptionsUtils().init());
    }


    //获取维修等级列表
    public void getRepairGradeList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("pid", "1");
        new BaseDataPresenter(this).loadData(DataUrl.GET_REPAIR_GRAGE_LIST, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Map<String,Object> map = (Map<String, Object>) data.response;
                gradeList = (ArrayList<Map<String, String>>) map.get("joinlist");
            }

            @Override
            public void onFailure(BaseDataBean data) {
            }

            @Override
            public void onError() {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        myMapView.onResume();
        if (gradeList == null) {
            getRepairGradeList();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        myMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        myMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myMapView.onDestroy();
    }

}

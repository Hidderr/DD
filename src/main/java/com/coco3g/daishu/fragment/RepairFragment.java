package com.coco3g.daishu.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.view.BannerView;
import com.coco3g.daishu.view.HomeMenuImageView;
import com.coco3g.daishu.view.SuperRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RepairFragment extends Fragment implements View.OnClickListener {
    SuperRefreshLayout mSuperRefreshLayout;
    private View mRepairView;
    BannerView mBanner;
    LinearLayout mLinearMenu;
    TextView mTxtRepairBoradcast;
    //
    HomeMenuImageView[] mMenuRes;
    HomeMenuImageView mRepairMenu1, mRepairMenu2, mRepairMenu3, mRepairMenu4, mRepairMenu5;
    int[] mNavIconResID = new int[]{R.mipmap.pic_help_phone, R.mipmap.pic_car_repair, R.mipmap.pic_server_confirm, R.mipmap.pic_car_account,
            R.mipmap.pic_history_record};
    String[] mTitles = new String[]{"救援电话", "车辆维修", "服务确认", "我的账单", "历史记录"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRepairView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_repair, null);
        init();
        getBanner();
        return mRepairView;
    }

    private void init() {
        mSuperRefreshLayout = (SuperRefreshLayout) mRepairView.findViewById(R.id.superrefresh_repair);
        mBanner = (BannerView) mRepairView.findViewById(R.id.banner_repair_frag);
        mLinearMenu = (LinearLayout) mRepairView.findViewById(R.id.linear_repair_menu);
        mTxtRepairBoradcast = (TextView) mRepairView.findViewById(R.id.tv_repair_boardcast);
        mRepairMenu1 = (HomeMenuImageView) mRepairView.findViewById(R.id.view_repair_menu_1);
        mRepairMenu2 = (HomeMenuImageView) mRepairView.findViewById(R.id.view_repair_menu_2);
        mRepairMenu3 = (HomeMenuImageView) mRepairView.findViewById(R.id.view_repair_menu_3);
        mRepairMenu4 = (HomeMenuImageView) mRepairView.findViewById(R.id.view_repair_menu_4);
        mRepairMenu5 = (HomeMenuImageView) mRepairView.findViewById(R.id.view_repair_menu_5);
        //
        mBanner.setScreenRatio(2);
        mMenuRes = new HomeMenuImageView[]{mRepairMenu1, mRepairMenu2, mRepairMenu3, mRepairMenu4, mRepairMenu5};
        for (int i = 0; i < mNavIconResID.length; i++) {
            mMenuRes[i].setIcon(mNavIconResID[i], mTitles[i]);
        }
        //
        mSuperRefreshLayout.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                getBanner();
            }

            @Override
            public void onLoadMore() {
                mSuperRefreshLayout.onLoadComplete();
            }
        });
    }
    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.tv_go_frag_mission:  //任务书
//                Intent intent1 = new Intent(mContext, MissionBookActivity.class);
//                startActivity(intent1);
////                checkIfHasMission();
//
//                break;
//            case R.id.tv_go_frag_saoma:  //扫码
//                new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.START_LOCATION, null);
//                Intent intent = new Intent(mContext, CaptureActivity.class);
//                ((Activity) mContext).startActivityForResult(intent, Constants.RESULT_SCAN);
//                break;
//        }
    }

    //获取banner图片
    private void getBanner() {
        HashMap<String, String> params = new HashMap<>();
        new BaseDataPresenter(getActivity()).loadData(DataUrl.GET_BANNER_IMAGE, params, getActivity().getResources().getString(R.string.loading), new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Map<String, Object> dataMap = (Map<String, Object>) data.response;
                ArrayList<Map<String, String>> bannerImageList = (ArrayList<Map<String, String>>) dataMap.get("banner");
                mBanner.loadData(bannerImageList);
                mSuperRefreshLayout.onLoadComplete();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, getActivity());
                mSuperRefreshLayout.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefreshLayout.onLoadComplete();
            }
        });
    }


}

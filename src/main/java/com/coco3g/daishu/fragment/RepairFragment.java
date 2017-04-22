package com.coco3g.daishu.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coco3g.daishu.R;


public class RepairFragment extends Fragment implements View.OnClickListener {
    private View mHomeView;
    private Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mHomeView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_repair, null);

        return mHomeView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
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

//    //获取banner图片
//    public void getLunBoImage() {
//        HashMap<String, String> params = new HashMap<>();
//        new BaseDataPresenter(mContext).loadData(DataUrl.GET_BANNER_IMAGE, params, mContext.getResources().getString(R.string.loading), new IBaseDataListener() {
//            @Override
//            public void onSuccess(BaseDataBean data) {
//                Log.e("获取到了banner", "banner");
//                Map<String, Object> dataMap = (Map<String, Object>) data.response;
//
//                ArrayList<Map<String, String>> bannerImageList = (ArrayList<Map<String, String>>) dataMap.get("banner");
//                mBannerImage.loadData(bannerImageList);
//
//            }
//
//            @Override
//            public void onFailure(BaseDataBean data) {
//                Global.showToast(data.msg, mContext);
//            }
//
//            @Override
//            public void onError() {
//
//            }
//        });
//    }


}

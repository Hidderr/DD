package com.coco3g.daishu.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.coco3g.daishu.R;


public class MeFragment extends Fragment implements View.OnClickListener {
    private View mMeView;
    ImageView mImageSetting, mImageAvatar, mImageQRCode, mImageRightArrow;
    HorizontalScrollView mHorizontalScroll;
    TextView mTxtCarNurse, mTxtAccount, mTxtShoppingAccount, mTxtBalance, mTxtCompact, mTxtMemberRecommend, mTxtUpdateMember;
    //
    Drawable drawableRight, drawableDown;
    boolean isNurseExpands = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMeView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_me, null);
        initView();
        //
        drawableRight = getResources().getDrawable(R.mipmap.pic_arrow_grey_right);
        drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(), drawableRight.getMinimumHeight());
        drawableDown = getResources().getDrawable(R.mipmap.pic_arrow_grey_down);
        drawableDown.setBounds(0, 0, drawableDown.getMinimumWidth(), drawableDown.getMinimumHeight());
        return mMeView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView() {
        mImageSetting = (ImageView) mMeView.findViewById(R.id.image_me_top_setting);
        mImageAvatar = (ImageView) mMeView.findViewById(R.id.image_me_top_avatar);
        mImageQRCode = (ImageView) mMeView.findViewById(R.id.image_me_top_qr);
        mImageRightArrow = (ImageView) mMeView.findViewById(R.id.image_me_top_arrow);
        //
        mHorizontalScroll = (HorizontalScrollView) mMeView.findViewById(R.id.horizontal_scroll_me_car_binding);
        //
        mTxtCarNurse = (TextView) mMeView.findViewById(R.id.tv_me_mime_car_nurse);
        mTxtAccount = (TextView) mMeView.findViewById(R.id.tv_me_mime_account);
        mTxtShoppingAccount = (TextView) mMeView.findViewById(R.id.tv_me_shopping_account);
        mTxtBalance = (TextView) mMeView.findViewById(R.id.tv_me_account_balance);
        mTxtCompact = (TextView) mMeView.findViewById(R.id.tv_me_compact);
        mTxtMemberRecommend = (TextView) mMeView.findViewById(R.id.tv_me_member_recommend);
        mTxtUpdateMember = (TextView) mMeView.findViewById(R.id.tv_me_update_member);
        //
        mImageRightArrow.setOnClickListener(this);
        mTxtCarNurse.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_me_top_arrow:
                break;
            case R.id.tv_me_mime_car_nurse: // 车保姆面板
                if (isNurseExpands) { // 隐藏
                    isNurseExpands = false;
                    mTxtCarNurse.setCompoundDrawables(null, null, drawableRight, null);
                    mTxtCarNurse.setCompoundDrawablePadding(10);
                    mHorizontalScroll.setVisibility(View.GONE);
                } else { // 展开
                    isNurseExpands = true;
                    mTxtCarNurse.setCompoundDrawables(null, null, drawableDown, null);
                    mTxtCarNurse.setCompoundDrawablePadding(10);
                    mHorizontalScroll.setVisibility(View.VISIBLE);
                }
                break;
        }
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

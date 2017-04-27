package com.coco3g.daishu.fragment;

import android.content.Context;
import android.content.Intent;
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
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.data.DataUrl;


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
        mTxtAccount.setOnClickListener(this);
        mTxtShoppingAccount.setOnClickListener(this);
        mTxtBalance.setOnClickListener(this);
        mTxtCompact.setOnClickListener(this);
        mTxtMemberRecommend.setOnClickListener(this);
        mTxtUpdateMember.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
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

            case R.id.tv_me_mime_account:  //个人账户
                intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url", DataUrl.GE_REN_SETTING);
                startActivity(intent);

                break;
            case R.id.tv_me_shopping_account:  //购物账单
                intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url", DataUrl.GOU_WU_ZHANG_DAN);
                startActivity(intent);

                break;
            case R.id.tv_me_account_balance:  //账户余额
                intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url", DataUrl.ZHANG_HU_YU_E);
                startActivity(intent);

                break;
            case R.id.tv_me_compact:  //合同摘要
                intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url", DataUrl.HE_TONG_ZHAI_YAO);
                startActivity(intent);

                break;
            case R.id.tv_me_member_recommend:  //会员推荐
                intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url", DataUrl.HUI_YUAN_TUI_JIAN);
                startActivity(intent);

                break;
            case R.id.tv_me_update_member:  //升级会员
                intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url", DataUrl.SHENG_JI_HUI_YUAN);
                startActivity(intent);

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

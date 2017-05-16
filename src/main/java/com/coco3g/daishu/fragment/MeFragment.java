package com.coco3g.daishu.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.data.TypevauleGotoDictionary;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.coco3g.daishu.view.MyQRcodeView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MeFragment extends Fragment implements View.OnClickListener {
    private View mMeView;
    ImageView mImageSetting, mImageAvatar, mImageQRCode, mImageRightArrow;
    HorizontalScrollView mHorizontalScroll;
    TextView mTxtCarNurse, mTxtAccount, mTxtShoppingAccount, mTxtBalance, mTxtCompact, mTxtMemberRecommend, mTxtUpdateMember,
            mTxtName, mTxtVipID, mTxtLogout;
    //
    Drawable drawableRight, drawableDown;
    boolean isNurseExpands = false;

    private RelativeLayout.LayoutParams avatar_lp;

    //
    OnLogoutListener onLogoutListener;


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
        mTxtName = (TextView) mMeView.findViewById(R.id.tv_me_top_username);
        mTxtVipID = (TextView) mMeView.findViewById(R.id.tv_me_top_id);
        mTxtLogout = (TextView) mMeView.findViewById(R.id.tv_me_frag_logout);
        //
        avatar_lp = new RelativeLayout.LayoutParams(Global.screenWidth / 6, Global.screenWidth / 6);
        avatar_lp.addRule(RelativeLayout.CENTER_VERTICAL);
        mImageAvatar.setLayoutParams(avatar_lp);
        //
        mImageRightArrow.setOnClickListener(this);
        mTxtCarNurse.setOnClickListener(this);
        mTxtShoppingAccount.setOnClickListener(this);
        mTxtBalance.setOnClickListener(this);
        mTxtCompact.setOnClickListener(this);
        mTxtMemberRecommend.setOnClickListener(this);
        mTxtUpdateMember.setOnClickListener(this);
        mImageSetting.setOnClickListener(this);
        mImageQRCode.setOnClickListener(this);
        mTxtLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
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

            case R.id.image_me_top_setting:  //个人账户
                intentToWeb(Global.H5Map.get("myinfo"));

                break;
            case R.id.tv_me_shopping_account:  //购物账单
                intentToWeb(Global.H5Map.get("goodsorder"));

                break;
            case R.id.tv_me_account_balance:  //账户余额
                intentToWeb(Global.H5Map.get("amount"));

                break;
            case R.id.tv_me_compact:  //合同摘要
                intentToWeb(Global.H5Map.get("hetong"));

                break;
            case R.id.tv_me_member_recommend:  //会员推荐
                intentToWeb(Global.H5Map.get("tuijian"));

                break;
            case R.id.tv_me_update_member:  //升级会员
                intentToWeb(Global.H5Map.get("vip"));

                break;
            case R.id.image_me_top_qr:  //二维码
                myQRcodeDialog();

                break;
            case R.id.image_me_top_arrow:  //二维码箭头
                myQRcodeDialog();

                break;
            case R.id.tv_me_frag_logout:  //退出登录
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity(), android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                builder.setMessage("退出账号将删除历史数据，下次登录可以继续使用本账号");
                builder.setTitle("提示");
                builder.setPositiveButton("退出", new android.app.AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Global.logout(getActivity());
                        OnLogout();
                    }

                });
                builder.setNegativeButton(getString(R.string.cancel), new android.app.AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                });
                builder.create().show();

                break;

        }
    }


    public void intentToWeb(String url) {

        if (url.equals("#")) {
            return;
        }

        if (url.startsWith("http://coco3g-app/open_tabview")) {
            TypevauleGotoDictionary typevauleGotoDictionary = new TypevauleGotoDictionary(getActivity());
            typevauleGotoDictionary.gotoViewChoose(url);
            return;
        }

        Intent intent = new Intent(getActivity(), WebActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (Global.USERINFOMAP != null) {
            getUserInfo();
        }
    }


    //我的专属二维码
    public void myQRcodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        MyQRcodeView view = new MyQRcodeView(getActivity());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.show();
        //
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = Global.screenWidth * 3 / 4;
//        params.height = p ;
        dialog.getWindow().setAttributes(params);
    }


    //登录
    public void getUserInfo() {

        HashMap<String, String> params = new HashMap<>();
//        params.put("id", Global.USERINFOMAP.get("id"));
        new BaseDataPresenter(getActivity()).loadData(DataUrl.GET_USERINFO, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                if (data.response instanceof List) { //有时候返回的是空数组
                    return;
                } else {
                    Global.USERINFOMAP = (Map<String, String>) data.response;
                    Global.saveLoginInfo(getActivity(), Global.USERINFOMAP.get("phone"), Global.USERINFOMAP.get("nickname"), Global.USERINFOMAP.get("password"), Global.LOGIN_INFO);
                    Global.saveLoginInfo(getActivity(), Global.USERINFOMAP.get("phone"), Global.USERINFOMAP.get("nickname"), Global.USERINFOMAP.get("password"), Global.LOGIN_INFO_LAST);
                    //
                }
                setMyInfo();
            }

            @Override
            public void onFailure(BaseDataBean data) {
//                Global.showToast(data.msg, mContext);
            }

            @Override
            public void onError() {

            }
        });
    }


    public void setMyInfo() {
        //头像
        if (Global.USERINFOMAP != null) {
            ImageLoader.getInstance().displayImage(Global.USERINFOMAP.get("avatar"), mImageAvatar, new DisplayImageOptionsUtils().circleImageInit());
            //名字
            mTxtName.setText(Global.USERINFOMAP.get("nickname"));
            mTxtVipID.setText("会员ID号：" + Global.USERINFOMAP.get("vipno"));
//            //会员专属二维码
//            String vip = Global.USERINFOMAP.get("groupid");
//            if (!TextUtils.isEmpty(vip) && vip.equals("1")) {
//                mRelativeQRcode.setVisibility(View.VISIBLE);
//                mCollectManager.setVisibility(View.VISIBLE);
//            } else {
//                mRelativeQRcode.setVisibility(View.GONE);
//                mCollectManager.setVisibility(View.GONE);
//            }
            //
//            mSettingItemList.get(0).setEFen(Global.USERINFOMAP.get("ecoin"));
        }

    }


    public interface OnLogoutListener {
        void logout();
    }

    public void setOnLogoutListener(OnLogoutListener onLogoutListener) {
        this.onLogoutListener = onLogoutListener;
    }

    public void OnLogout() {
        if (onLogoutListener != null) {
            onLogoutListener.logout();
            Global.USERINFOMAP = null;
        }
    }


}

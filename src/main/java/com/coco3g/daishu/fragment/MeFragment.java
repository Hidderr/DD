package com.coco3g.daishu.fragment;

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
    ImageView mImageAvatar, mImageQRCode, mImageRightArrow;
    HorizontalScrollView mHorizontalScroll;
    RelativeLayout mRelativeInfo;
    TextView mTxtCarNurse, mTxtAccount, mTxtShoppingAccount, mTxtBalance, mTxtCompact, mTxtUpdateMember,
            mTxtName, mTxtMemberID, mTxtMemberType, mTxtYouHuiQuan, mTxtAddress;
    //
    Drawable drawableRight, drawableDown;
    boolean isNurseExpands = false;

    private RelativeLayout.LayoutParams avatar_lp;
    //
    boolean meFragIsVisible = true; //meFragment在MainActivity中是否可见


    //
//    OnLogoutListener onLogoutListener;


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
        mImageAvatar = (ImageView) mMeView.findViewById(R.id.image_me_top_avatar);
        mImageQRCode = (ImageView) mMeView.findViewById(R.id.image_me_top_qr);
        mImageRightArrow = (ImageView) mMeView.findViewById(R.id.image_me_top_arrow);
        mRelativeInfo = (RelativeLayout) mMeView.findViewById(R.id.relative_me_frag_my_info);
        //
        mHorizontalScroll = (HorizontalScrollView) mMeView.findViewById(R.id.horizontal_scroll_me_car_binding);
        //
        mTxtCarNurse = (TextView) mMeView.findViewById(R.id.tv_me_mime_car_nurse);
        mTxtAccount = (TextView) mMeView.findViewById(R.id.tv_me_mime_account);
        mTxtShoppingAccount = (TextView) mMeView.findViewById(R.id.tv_me_shopping_account);
        mTxtBalance = (TextView) mMeView.findViewById(R.id.tv_me_account_balance);
        mTxtCompact = (TextView) mMeView.findViewById(R.id.tv_me_compact);
        mTxtUpdateMember = (TextView) mMeView.findViewById(R.id.tv_me_update_member);
        mTxtName = (TextView) mMeView.findViewById(R.id.tv_me_top_username);
        mTxtMemberID = (TextView) mMeView.findViewById(R.id.tv_me_top_member_id);
//        mTxtLogout = (TextView) mMeView.findViewById(R.id.tv_me_frag_logout);
        mTxtMemberType = (TextView) mMeView.findViewById(R.id.tv_me_top_member_type);
        mTxtYouHuiQuan = (TextView) mMeView.findViewById(R.id.tv_me_shopping_youhui_quan);
        mTxtAddress = (TextView) mMeView.findViewById(R.id.tv_me_update_address);
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
        mTxtUpdateMember.setOnClickListener(this);
        mImageQRCode.setOnClickListener(this);
//        mTxtLogout.setOnClickListener(this);
        mTxtYouHuiQuan.setOnClickListener(this);
        mRelativeInfo.setOnClickListener(this);
        mTxtAddress.setOnClickListener(this);
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

            case R.id.relative_me_frag_my_info:  //个人信息
                intentToWeb(Global.H5Map.get("myinfo"));

                break;
            case R.id.tv_me_shopping_youhui_quan:  //优惠券
                intentToWeb(Global.H5Map.get("youhuiquan"));

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
            case R.id.tv_me_update_member:  //升级会员
                intentToWeb(Global.H5Map.get("vip"));

                break;
            case R.id.tv_me_update_address:  //收货地址管理
                intentToWeb(Global.H5Map.get("address"));

                break;
            case R.id.image_me_top_qr:  //二维码
                if (!Global.checkoutLogin(getActivity())) {
                    return;
                }
                myQRcodeDialog();

                break;
            case R.id.image_me_top_arrow:  //二维码箭头
                if (!Global.checkoutLogin(getActivity())) {
                    return;
                }
                myQRcodeDialog();

                break;

        }
    }


    public void intentToWeb(String url) {
        if (!Global.checkoutLogin(getActivity())) {
            return;
        }
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
        if (Global.USERINFOMAP != null && meFragIsVisible) {
            getUserInfo();
        } else {
            setMyInfo();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        meFragIsVisible = !hidden;
        if (Global.USERINFOMAP != null && meFragIsVisible) {
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


    //获取个人信息
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
            mTxtMemberID.setText("会员ID号：" + Global.USERINFOMAP.get("vipno"));
            //会员类型
            mTxtMemberType.setText(Global.USERINFOMAP.get("vip_level"));
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
        } else {
            ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.pic_default_avatar_icon, mImageAvatar, new DisplayImageOptionsUtils().circleImageInit());
            //名字
            mTxtName.setText("登录/注册");
            mTxtMemberID.setText("");
            //会员类型
            mTxtMemberType.setText("");
        }

    }


//    public interface OnLogoutListener {
//        void logout();
//    }
//
//    public void setOnLogoutListener(OnLogoutListener onLogoutListener) {
//        this.onLogoutListener = onLogoutListener;
//    }
//
//    public void OnLogout() {
//        if (onLogoutListener != null) {
//            onLogoutListener.logout();
//            Global.USERINFOMAP = null;
//        }
//    }


}

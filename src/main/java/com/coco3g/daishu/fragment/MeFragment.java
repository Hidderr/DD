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
import android.widget.LinearLayout;
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
import com.coco3g.daishu.view.HomeMenuImageView;
import com.coco3g.daishu.view.MeMenuImageView;
import com.coco3g.daishu.view.MyQRcodeView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MeFragment extends Fragment implements View.OnClickListener {
    private View mMeView;
    ImageView mImageAvatar, mImageQRCode, mImageRightArrow;
    HorizontalScrollView mHorizontalScroll;
    LinearLayout mLinearGuangGao;
    RelativeLayout mRelativeInfo, mRelativeShopping;
    TextView mTxtCarNurse, mTxtAccount, mTxtName, mTxtMemberID, mTxtMemberType;
    //
    MeMenuImageView meMenu1, meMenu2, meMenu3, meMenu4;
    MeMenuImageView[] meMenus;
    int[] mShoppingResID = new int[]{R.mipmap.pic_dai_pay_money_icon, R.mipmap.pic_dai_shou_huo_icon, R.mipmap.pic_dai_comment_icon, R.mipmap.pic_shou_hou_icon};
    String[] mShoppingTitles = new String[]{"待付款", "待收货", "待评价", "售后"};
    //
    HomeMenuImageView mHomeMenu1, mHomeMenu2, mHomeMenu3, mHomeMenu4, mHomeMenu5, mHomeMenu6, mHomeMenu7, mHomeMenu8;
    HomeMenuImageView[] mHomeMenus;
    int[] mMeInfoResID = new int[]{R.mipmap.pic_shopping_cart_icon, R.mipmap.pic_collection_icon, R.mipmap.pic_you_hui_quan_icon, R.mipmap.pic_address_manager_icon,
            R.mipmap.pic_yu_e_icon, R.mipmap.pic_zhang_dan_icon, R.mipmap.pic_he_tong_icon, R.mipmap.pic_update_vip_icon};
    String[] mMeInfoTitles = new String[]{"购物车", "收藏", "优惠券", "地址管理", "账户余额", "服务账单", "合同摘要", "会员升级"};
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
        mRelativeShopping = (RelativeLayout) mMeView.findViewById(R.id.relative_me_frag_shopping_zhangdan);
        //
        mHorizontalScroll = (HorizontalScrollView) mMeView.findViewById(R.id.horizontal_scroll_me_car_binding);
        //
        mTxtCarNurse = (TextView) mMeView.findViewById(R.id.tv_me_mime_car_nurse);
        mTxtAccount = (TextView) mMeView.findViewById(R.id.tv_me_mime_account);
        mTxtName = (TextView) mMeView.findViewById(R.id.tv_me_top_username);
        mTxtMemberID = (TextView) mMeView.findViewById(R.id.tv_me_top_member_id);
        mTxtMemberType = (TextView) mMeView.findViewById(R.id.tv_me_top_member_type);
        mLinearGuangGao = (LinearLayout) mMeView.findViewById(R.id.linear_me_frag_guang_gao_List);
        //
        avatar_lp = new RelativeLayout.LayoutParams(Global.screenWidth / 6, Global.screenWidth / 6);
        avatar_lp.addRule(RelativeLayout.CENTER_VERTICAL);
        mImageAvatar.setLayoutParams(avatar_lp);
        //
        meMenu1 = (MeMenuImageView) mMeView.findViewById(R.id.view_me_menu_1);
        meMenu2 = (MeMenuImageView) mMeView.findViewById(R.id.view_me_menu_2);
        meMenu3 = (MeMenuImageView) mMeView.findViewById(R.id.view_me_menu_3);
        meMenu4 = (MeMenuImageView) mMeView.findViewById(R.id.view_me_menu_4);
        meMenus = new MeMenuImageView[]{meMenu1, meMenu2, meMenu3, meMenu4};
        mHomeMenu1 = (HomeMenuImageView) mMeView.findViewById(R.id.view_me_menu_5);
        mHomeMenu2 = (HomeMenuImageView) mMeView.findViewById(R.id.view_me_menu_6);
        mHomeMenu3 = (HomeMenuImageView) mMeView.findViewById(R.id.view_me_menu_7);
        mHomeMenu4 = (HomeMenuImageView) mMeView.findViewById(R.id.view_me_menu_8);
        mHomeMenu5 = (HomeMenuImageView) mMeView.findViewById(R.id.view_me_menu_9);
        mHomeMenu6 = (HomeMenuImageView) mMeView.findViewById(R.id.view_me_menu_10);
        mHomeMenu7 = (HomeMenuImageView) mMeView.findViewById(R.id.view_me_menu_11);
        mHomeMenu8 = (HomeMenuImageView) mMeView.findViewById(R.id.view_me_menu_12);
        mHomeMenus = new HomeMenuImageView[]{mHomeMenu1, mHomeMenu2, mHomeMenu3, mHomeMenu4, mHomeMenu5, mHomeMenu6, mHomeMenu7, mHomeMenu8};
        //
        for (int i = 0; i < mShoppingResID.length; i++) {
            meMenus[i].setIcon(mShoppingResID[i], mShoppingTitles[i]);
            meMenus[i].setUnReadCount(2);
        }
        for (int i = 0; i < mMeInfoResID.length; i++) {
            mHomeMenus[i].setIcon(mMeInfoResID[i], mMeInfoTitles[i]);
            mHomeMenus[i].setTextSize(14f);
        }
        //
        mImageRightArrow.setOnClickListener(this);
        mTxtCarNurse.setOnClickListener(this);
        mImageQRCode.setOnClickListener(this);
//        mTxtLogout.setOnClickListener(this);
        mRelativeInfo.setOnClickListener(this);


        //
        meMenu1.setOnClickListener(this);
        meMenu2.setOnClickListener(this);
        meMenu3.setOnClickListener(this);
        meMenu4.setOnClickListener(this);
        mHomeMenu1.setOnClickListener(this);
        mHomeMenu2.setOnClickListener(this);
        mHomeMenu3.setOnClickListener(this);
        mHomeMenu4.setOnClickListener(this);
        mHomeMenu5.setOnClickListener(this);
        mHomeMenu6.setOnClickListener(this);
        mHomeMenu7.setOnClickListener(this);
        mHomeMenu8.setOnClickListener(this);
        mRelativeShopping.setOnClickListener(this);

        //
        getGuangGaoList();


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

            case R.id.relative_me_frag_shopping_zhangdan:  //购物账单
                intentToWeb(Global.H5Map.get("shoporder"));

                break;

            case R.id.relative_me_frag_my_info:  //个人信息
                intentToWeb(Global.H5Map.get("myinfo"));

                break;
            case R.id.view_me_menu_1:  //代付款
                intentToWeb(Global.H5Map.get("notpayment"));

                break;
            case R.id.view_me_menu_2:  //待收货
                intentToWeb(Global.H5Map.get("notreciev"));

                break;
            case R.id.view_me_menu_3:  //待评价
                intentToWeb(Global.H5Map.get("notevas"));

                break;
            case R.id.view_me_menu_4:  //售后
                intentToWeb(Global.H5Map.get("saleafter"));

                break;
            case R.id.view_me_menu_5:  //购物车
                intentToWeb(Global.H5Map.get("shopcar"));

                break;
            case R.id.view_me_menu_6:  //收藏
                intentToWeb(Global.H5Map.get("favorites"));

                break;
            case R.id.view_me_menu_7:  //优惠券
                intentToWeb(Global.H5Map.get("youhuiquan"));

                break;
            case R.id.view_me_menu_8:  //收货地址管理
                intentToWeb(Global.H5Map.get("address"));

                break;
            case R.id.view_me_menu_9:  //账户余额
                intentToWeb(Global.H5Map.get("amount"));

                break;
            case R.id.view_me_menu_10:  //服务账单
                intentToWeb(Global.H5Map.get("mybill"));

                break;
            case R.id.view_me_menu_11:  //合同摘要
                intentToWeb(Global.H5Map.get("hetong"));

                break;
            case R.id.view_me_menu_12:  //升级会员
                intentToWeb(Global.H5Map.get("vip"));

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
            //
            mTxtMemberID.setVisibility(View.VISIBLE);
            mTxtMemberID.setText("会员ID号：" + Global.USERINFOMAP.get("vipno"));
            //会员类型
            mTxtMemberType.setVisibility(View.VISIBLE);
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
            mTxtMemberID.setVisibility(View.GONE);
            //会员类型
            mTxtMemberType.setVisibility(View.GONE);
        }

    }


    //获取广告列表
    public void getGuangGaoList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("catid", "2");
        new BaseDataPresenter(getActivity()).loadData(DataUrl.GET_HOME_GUANG_GAO_LIST, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, String>> guangGaoList = (ArrayList<Map<String, String>>) data.response;
                addGuangGaoImage(guangGaoList);
            }

            @Override
            public void onFailure(BaseDataBean data) {
            }

            @Override
            public void onError() {
            }
        });
    }


    public void addGuangGaoImage(final ArrayList<Map<String, String>> guangGaoList) {
        LinearLayout.LayoutParams image_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Global.screenWidth / 2);
        image_lp.setMargins(0, Global.dipTopx(getActivity(), 5f), 0, 0);
        for (int i = 0; i < guangGaoList.size(); i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(image_lp);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageLoader.getInstance().displayImage(guangGaoList.get(i).get("image"), imageView, new DisplayImageOptionsUtils().init());
            //
            mLinearGuangGao.addView(imageView);
            //
            final int finalI = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WebActivity.class);
                    intent.putExtra("url", guangGaoList.get(finalI).get("url"));
                    getActivity().startActivity(intent);
                }
            });
        }

    }


}

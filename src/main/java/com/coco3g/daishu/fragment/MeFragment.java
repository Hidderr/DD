package com.coco3g.daishu.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.adapter.CarShopAdapter;
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
    LinearLayout mLinearGuangGao, mLinearMyCar;
    RelativeLayout mRelativeInfo, mRelativeShopping;
    TextView mTxtCarNurse, mTxtAccount, mTxtName, mTxtMemberID, mTxtMemberType;
    //
    MeMenuImageView meMenu1, meMenuDaiFaHuo, meMenu2, meMenu3, meMenu4;
    MeMenuImageView[] meMenus;
    int[] mShoppingResID = new int[]{R.mipmap.pic_dai_pay_money_icon, R.mipmap.pic_dai_fahuo_icon, R.mipmap.pic_dai_shou_huo_icon, R.mipmap.pic_dai_comment_icon, R.mipmap.pic_shou_hou_icon};
    String[] mShoppingTitles = new String[]{"待付款", "待发货", "待收货", "待评价", "售后"};
    //
    HomeMenuImageView mHomeMenu1, mHomeMenu2, mHomeMenu3, mHomeMenu4, mHomeMenu5, mHomeMenu6, mHomeMenu7, mHomeMenu8;
    HomeMenuImageView[] mHomeMenus;
    int[] mMeInfoResID = new int[]{R.mipmap.pic_shopping_cart_icon, R.mipmap.pic_collection_icon, R.mipmap.pic_you_hui_quan_icon, R.mipmap.pic_address_manager_icon,
            R.mipmap.pic_yu_e_icon, R.mipmap.pic_zhang_dan_icon, R.mipmap.pic_he_tong_icon, R.mipmap.pic_update_vip_icon};

    String[] mMeInfoTitles = new String[]{"购物车", "收藏", "优惠券", "地址管理", "账户余额", "服务账单", "合同摘要", "会员升级"};
    //
    Drawable drawableLeft, drawableRight, drawableDown;
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
        drawableLeft = getResources().getDrawable(R.mipmap.pic_my_car_icon);
        drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
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
        mLinearMyCar = (LinearLayout) mMeView.findViewById(R.id.linear_me_car_binding);
        //
        avatar_lp = new RelativeLayout.LayoutParams(Global.screenWidth / 6, Global.screenWidth / 6);
        avatar_lp.addRule(RelativeLayout.CENTER_VERTICAL);
        mImageAvatar.setLayoutParams(avatar_lp);
        //
        meMenu1 = (MeMenuImageView) mMeView.findViewById(R.id.view_me_menu_1);
        meMenuDaiFaHuo = (MeMenuImageView) mMeView.findViewById(R.id.view_me_menu_fa_huo);
        meMenu2 = (MeMenuImageView) mMeView.findViewById(R.id.view_me_menu_2);
        meMenu3 = (MeMenuImageView) mMeView.findViewById(R.id.view_me_menu_3);
        meMenu4 = (MeMenuImageView) mMeView.findViewById(R.id.view_me_menu_4);
        meMenus = new MeMenuImageView[]{meMenu1, meMenuDaiFaHuo, meMenu2, meMenu3, meMenu4};
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
        }
        for (int i = 0; i < mMeInfoResID.length; i++) {
            mHomeMenus[i].setIcon(mMeInfoResID[i], mMeInfoTitles[i]);
            mHomeMenus[i].setImagePadding();
//            mHomeMenus[i].setTextSize(13f);
        }
        //
        mImageRightArrow.setOnClickListener(this);
        mTxtCarNurse.setOnClickListener(this);
        mImageQRCode.setOnClickListener(this);
//        mTxtLogout.setOnClickListener(this);
        mRelativeInfo.setOnClickListener(this);


        //
        meMenu1.setOnClickListener(this);
        meMenuDaiFaHuo.setOnClickListener(this);
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
                    mTxtCarNurse.setCompoundDrawables(drawableLeft, null, drawableRight, null);
                    mTxtCarNurse.setCompoundDrawablePadding(10);
                    mHorizontalScroll.setVisibility(View.GONE);
                } else { // 展开
                    isNurseExpands = true;
                    mTxtCarNurse.setCompoundDrawables(drawableLeft, null, drawableDown, null);
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

            case R.id.view_me_menu_fa_huo:  //代发货
                intentToWeb(Global.H5Map.get("notsend"));

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
                    Global.USERINFOMAP = (Map<String, Object>) data.response;
                    Global.saveLoginInfo(getActivity(), Global.USERINFOMAP.get("phone") + "", Global.USERINFOMAP.get("nickname") + "", Global.USERINFOMAP.get("password") + "", Global.LOGIN_INFO);
                    Global.saveLoginInfo(getActivity(), Global.USERINFOMAP.get("phone") + "", Global.USERINFOMAP.get("nickname") + "", Global.USERINFOMAP.get("password") + "", Global.LOGIN_INFO_LAST);
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


    //展示个人信息
    public void setMyInfo() {
        //头像
        if (Global.USERINFOMAP != null) {
            ImageLoader.getInstance().displayImage(Global.USERINFOMAP.get("avatar") + "", mImageAvatar, new DisplayImageOptionsUtils().circleImageInit());
            //名字
            mTxtName.setText(Global.USERINFOMAP.get("nickname") + "");
            //
            mTxtMemberID.setVisibility(View.VISIBLE);
            mTxtMemberID.setText("会员ID号：" + Global.USERINFOMAP.get("vipno"));
            //会员类型
            mTxtMemberType.setVisibility(View.VISIBLE);
            mTxtMemberType.setText(Global.USERINFOMAP.get("vip_level") + "");
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


            Map<String, String> shopInfoMap = (Map<String, String>) Global.USERINFOMAP.get("bill");
            //代付款
            meMenu1.setUnReadCount(Integer.parseInt(shopInfoMap.get("notpayment")));
            //代付款
            meMenuDaiFaHuo.setUnReadCount(Integer.parseInt(shopInfoMap.get("notsend")));
            //代付款
            meMenu2.setUnReadCount(Integer.parseInt(shopInfoMap.get("notreciev")));
            //代付款
            meMenu3.setUnReadCount(Integer.parseInt(shopInfoMap.get("notevas")));
            //代付款
            meMenu4.setUnReadCount(Integer.parseInt(shopInfoMap.get("saleafter")));


            //设置爱车保姆
            addMyCar();

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


    //添加汽车保姆
    public void addMyCar() {
//        int childNum = mLinearMyCar.getChildCount();
//        if (childNum > 1) {
//            mLinearMyCar.removeViews(0, childNum - 1);
//        }

        mLinearMyCar.removeAllViews();

        final ArrayList<Map<String, String>> myCarList = (ArrayList<Map<String, String>>) Global.USERINFOMAP.get("mycars");
        if (myCarList != null && myCarList.size() > 0) {
            //添加汽车保姆
            for (int i = 0; i < myCarList.size(); i++) {
                MeMenuImageView meMenuImageView = new MeMenuImageView(getActivity());
                meMenuImageView.setTextColor(R.color.text_color_1);
                meMenuImageView.setIcon(myCarList.get(i).get("brandthumb"), myCarList.get(i).get("chepai"));
//                meMenuImageView.setDrawableSize();
                final int finalI = i;
                meMenuImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), WebActivity.class);
                        intent.putExtra("url", myCarList.get(finalI).get("url"));
                        startActivity(intent);
                    }
                });
                mLinearMyCar.addView(meMenuImageView);
            }
        } else {
            //添加新增绑定接口
            MeMenuImageView meMenuImageView = new MeMenuImageView(getActivity());
            meMenuImageView.setIcon(R.mipmap.pic_me_car_add, "新增绑定");
//            meMenuImageView.setDrawableSize();
            meMenuImageView.setTextColor(R.color.text_color_1);
            meMenuImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Global.checkoutLogin(getActivity())) {
                        getMyCar();
                    }
                }
            });
            mLinearMyCar.addView(meMenuImageView);
        }
    }

    //获取我的汽车
    public void getMyCar() {
        HashMap<String, String> params = new HashMap<>();
        new BaseDataPresenter(getActivity()).loadData(DataUrl.GET_MY_CAR, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                ArrayList<Map<String, String>> myCarList = (ArrayList<Map<String, String>>) data.response;
                if (myCarList == null || myCarList.size() <= 0) {
                    remindDialog();
                } else if (myCarList.size() == 1) {
                    getBandingCarBaoMuUrl(myCarList.get(0).get("id"));
                } else {
                    chooseMyCarDialog(myCarList);
                }
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, getActivity());
            }

            @Override
            public void onError() {
            }
        });
    }

    //没有添加汽车信息的时候，提醒信息
    public void remindDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("请先在\"首页——我的汽车\"添加车辆信息");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //选择我的汽车
    public void chooseMyCarDialog(ArrayList<Map<String, String>> myCarList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_me_frag_my_car_list, null);
        ListView mListView = (ListView) view.findViewById(R.id.listview_me_frag_my_car_list);
        final CarShopAdapter mAdapter = new CarShopAdapter(getActivity());
        mAdapter.setList(myCarList);
        mListView.setAdapter(mAdapter);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        //
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                getBandingCarBaoMuUrl(mAdapter.getList().get(position).get("id"));
            }
        });

    }


    //获取我的汽车
    public void getBandingCarBaoMuUrl(String carid) {
        HashMap<String, String> params = new HashMap<>();
        params.put("id", carid);
        new BaseDataPresenter(getActivity()).loadData(DataUrl.GET_MY_CAR_BANGDING_BAOMU_URL, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Map<String, String> map = (Map<String, String>) data.response;
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url", map.get("url"));
                startActivity(intent);

            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, getActivity());
            }

            @Override
            public void onError() {
            }
        });
    }


}

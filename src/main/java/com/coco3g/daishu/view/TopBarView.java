package com.coco3g.daishu.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.autonavi.ae.search.log.GLog;
import com.coco3g.daishu.R;
import com.coco3g.daishu.activity.MessageActivity;
import com.coco3g.daishu.activity.WebActivity;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.data.TypevauleGotoDictionary;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class TopBarView extends RelativeLayout implements OnClickListener {
    View mView = null;
    Context mContext = null;
    ImageView mImageLeft = null;
    RelativeLayout mRelativeLeft, mRelativeRight, mRelativeMeSetting;
    TextView mTxtTitle = null;

    //
    OnClickLeftView onclickleftview;
    OnClickRightView onclickrightview = null;
    boolean mOverrideClick = false;

    //r
    RelativeLayout mRelativeNormal, mRelativeHome, mRelativeUnRead;
    ImageView mImageSetting, mImageMsg;

    //首页里
    TextView mTxtLocation, mTxtIncomeZhiDu, mTxtUnreadCount;
    EditText mEditSearch;
    OnHomeSearchListener onHomeSearchListener;


    public TopBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        initView();
    }

    void initView() {
        LayoutInflater lay = LayoutInflater.from(mContext);
        mView = lay.inflate(R.layout.view_topbar, this);
        mImageLeft = (ImageView) mView.findViewById(R.id.image_topbar_left);
        mRelativeLeft = (RelativeLayout) mView.findViewById(R.id.relative_topbar_left);
        mRelativeRight = (RelativeLayout) mView.findViewById(R.id.relative_topbar_right);
        mRelativeMeSetting = (RelativeLayout) mView.findViewById(R.id.relatvie_me_frag_setting);
        mRelativeNormal = (RelativeLayout) mView.findViewById(R.id.relative_topbar_nomal);
        mRelativeHome = (RelativeLayout) mView.findViewById(R.id.relative_main_topbar);
        mTxtTitle = (TextView) mView.findViewById(R.id.tv_topbar_title);
        mTxtLocation = (TextView) mView.findViewById(R.id.tv_topbar_location);
        mTxtIncomeZhiDu = (TextView) mView.findViewById(R.id.tv_main_income_zhidu);
        mEditSearch = (EditText) mView.findViewById(R.id.edit_topbar_search);
        mImageSetting = (ImageView) mView.findViewById(R.id.image_topbar_setting);
        mImageMsg = (ImageView) mView.findViewById(R.id.image_topbar_system_msg);
        mRelativeUnRead = (RelativeLayout) mView.findViewById(R.id.relative_topbar_unread);
        mTxtUnreadCount = (TextView) mView.findViewById(R.id.tv_topbar_unread_count);
        mRelativeLeft.setOnClickListener(this);
        mRelativeRight.setOnClickListener(this);
        mImageSetting.setOnClickListener(this);
        mTxtIncomeZhiDu.setOnClickListener(this);
        mImageMsg.setOnClickListener(this);
        //
        mEditSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 先隐藏键盘
                    ((InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(((Activity) mContext).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //进行搜索操作的方法，在该方法中可以加入mEditSearchUser的非空判断
                    String searchKey = mEditSearch.getText().toString().trim();
                    if (TextUtils.isEmpty(searchKey)) {

                    } else {
                        onHomeSearch(searchKey);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /* 设置顶部栏左侧view */
    public void setLeftView(View view) {
        mRelativeLeft.removeAllViews();
        mRelativeLeft.setVisibility(View.VISIBLE);
        mImageLeft.setVisibility(View.GONE);
        mRelativeLeft.addView(view);
        mRelativeLeft.setFocusableInTouchMode(false);
        mRelativeLeft.setClickable(true);
    }

    /* 隐藏左侧view */
    public void hideLeftView() {
        mRelativeLeft.setVisibility(View.INVISIBLE);
    }

    /* 设置标题 */
    public void setTitle(String title) {
        mTxtTitle.setText(title);
    }


    /* 设置顶部栏右侧view */
    public void setRightView(View view) {
        mRelativeRight.removeAllViews();
        mRelativeRight.setVisibility(View.VISIBLE);
        mRelativeRight.addView(view);
    }


    /* 隐藏右侧view */
    public void hideRightView() {
        mRelativeRight.setVisibility(View.INVISIBLE);
    }


    //显示主页面的
    public void showHomeTopbar() {
        mRelativeNormal.setVisibility(GONE);
        mRelativeHome.setVisibility(VISIBLE);
    }

    //显示正常的
    public void showNomalTopbar() {
        mRelativeNormal.setVisibility(VISIBLE);
        mRelativeHome.setVisibility(View.GONE);
    }

    //显示正常的
    public void setLocationCity(String city) {
        mTxtLocation.setText(city);
    }


    public void showJiangLi(boolean show) {
        if (show) {
            mTxtIncomeZhiDu.setVisibility(VISIBLE);
        } else {
            mTxtIncomeZhiDu.setVisibility(GONE);
        }
    }


    public void setSettingVisible(boolean visible) {
        if (visible) {
            mRelativeMeSetting.setVisibility(VISIBLE);
        } else {
            mRelativeMeSetting.setVisibility(GONE);
        }
    }

    public void setUnReadCount(int count) {
        if (count > 0 && count <= 99) {
            mRelativeUnRead.setVisibility(View.VISIBLE);
            mTxtUnreadCount.setVisibility(View.VISIBLE);
            mTxtUnreadCount.setText(count + "");
        } else if (count > 99) {
            mRelativeUnRead.setVisibility(View.VISIBLE);
            mTxtUnreadCount.setVisibility(View.VISIBLE);
            mTxtUnreadCount.setText("99+");
        } else {
            mRelativeUnRead.setVisibility(View.GONE);
            mTxtUnreadCount.setVisibility(View.GONE);
            mTxtUnreadCount.setText("0");
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.relative_topbar_left:
                if (mOverrideClick) {
                    onClickLeftView();
                } else {
                    ((Activity) mContext).finish();
                }
                break;
            case R.id.relative_topbar_right:
                onClickRightView();
                break;

            case R.id.tv_main_income_zhidu:  //奖励制度
                intent = new Intent(mContext, WebActivity.class);
                if (!TextUtils.isEmpty(Global.H5Map.get("jiangli"))) {
                    intent.putExtra("url", Global.H5Map.get("jiangli"));
                    mContext.startActivity(intent);
                }
                break;

            case R.id.image_topbar_setting:  //个人设置
                if (!Global.checkoutLogin(mContext)) {
                    return;
                }
                String settingUrl = Global.H5Map.get("setting");
                if (settingUrl.equals("#")) {
                    return;
                }
                intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("url", settingUrl);
                mContext.startActivity(intent);
                break;

            case R.id.image_topbar_system_msg:  //消息提醒
                if (!Global.checkoutLogin(mContext)) {
                    return;
                }
                intent = new Intent(mContext, MessageActivity.class);
                mContext.startActivity(intent);

                break;
        }
    }


    public void setOnClickLeftListener(OnClickLeftView onclickleftview) {
        mOverrideClick = true;
        this.onclickleftview = onclickleftview;
    }

    public interface OnClickLeftView {
        void onClickLeftView();
    }

    private void onClickLeftView() {
        if (onclickleftview != null) {
            onclickleftview.onClickLeftView();
        }
    }

    //
    public void setOnClickRightListener(OnClickRightView onclicktopbarview) {
        this.onclickrightview = onclicktopbarview;
    }

    private void onClickRightView() {
        if (onclickrightview != null) {
            onclickrightview.onClickTopbarView();
        }
    }

    public interface OnClickRightView {
        void onClickTopbarView();
    }

    public interface OnHomeSearchListener {
        void homeSearch(String searchKey);
    }

    public void setOnHomeSearchListener(OnHomeSearchListener onHomeSearchListener) {
        this.onHomeSearchListener = onHomeSearchListener;
    }

    public void onHomeSearch(String searchKey) {
        if (onHomeSearchListener != null) {
            onHomeSearchListener.homeSearch(searchKey);
        }
    }


}

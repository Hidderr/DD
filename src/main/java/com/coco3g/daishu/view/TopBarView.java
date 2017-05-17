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

import com.coco3g.daishu.R;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class TopBarView extends RelativeLayout implements OnClickListener {
    View mView = null;
    Context mContext = null;
    ImageView mImageLeft = null;
    RelativeLayout mRelativeLeft, mRelativeRight = null;
    TextView mTxtTitle = null;

    //
    OnClickLeftView onclickleftview;
    OnClickRightView onclickrightview = null;
    boolean mOverrideClick = false;

    //r
    RelativeLayout mRelativeNormal, mRelativeHome;
    ImageView mImageSetting;

    //首页里
    TextView mTxtLocation;
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
        mRelativeNormal = (RelativeLayout) mView.findViewById(R.id.relative_topbar_nomal);
        mRelativeHome = (RelativeLayout) mView.findViewById(R.id.relative_main_topbar);
        mTxtTitle = (TextView) mView.findViewById(R.id.tv_topbar_title);
        mTxtLocation = (TextView) mView.findViewById(R.id.tv_topbar_location);
        mEditSearch = (EditText) mView.findViewById(R.id.edit_topbar_search);
        mImageSetting = (ImageView) mView.findViewById(R.id.image_topbar_setting);
        mRelativeLeft.setOnClickListener(this);
        mRelativeRight.setOnClickListener(this);
        mImageSetting.setOnClickListener(this);
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


    public void setSettingVisible(boolean visible) {
        if (visible) {
            mImageSetting.setVisibility(VISIBLE);
        } else {
            mImageSetting.setVisibility(GONE);
        }
    }


    @Override
    public void onClick(View v) {
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
            case R.id.image_topbar_setting:  //个人设置

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

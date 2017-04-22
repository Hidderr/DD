package com.coco3g.daishu.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;


public class TopBarView extends RelativeLayout implements OnClickListener {
    View mView = null;
    Context mContext = null;
    ImageView mImageLeft = null;
    RelativeLayout mRelativeLeft, mRelativeRight = null;
    TextView mTxtTitle = null;
    EditText mEditSearch = null;
    //
    OnClickLeftView onclickleftview;
    OnClickRightView onclickrightview = null;
    boolean mOverrideClick = false;

    //
    RelativeLayout mRelativeMainRightView;
    ImageView mImageMsg, mImageMsgRemind;


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
        mImageMsg = (ImageView) mView.findViewById(R.id.image_topbar_main_home_msg);
        mImageMsgRemind = (ImageView) mView.findViewById(R.id.image_topbar_system_msg_remind);
        mRelativeLeft = (RelativeLayout) mView.findViewById(R.id.relative_topbar_left);
        mRelativeRight = (RelativeLayout) mView.findViewById(R.id.relative_topbar_right);
        mRelativeMainRightView = (RelativeLayout) mView.findViewById(R.id.relative_topbar_right_main_view);
        mTxtTitle = (TextView) mView.findViewById(R.id.tv_topbar_title);
        mRelativeLeft.setOnClickListener(this);
        mRelativeRight.setOnClickListener(this);
        mImageMsg.setOnClickListener(this);
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

    //设置系统消息图标可见
    public void setMsgVisible() {
        mRelativeMainRightView.setVisibility(VISIBLE);
        mImageMsg.setVisibility(VISIBLE);
    }

    //设置消息提醒
    public void setMsgRemindVisible() {
        mRelativeMainRightView.setVisibility(VISIBLE);
        mImageMsg.setVisibility(VISIBLE);
        mImageMsgRemind.setVisibility(VISIBLE);
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
            case R.id.image_topbar_main_home_msg:
//                new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.START_LOCATION, null);
//                Intent intent = new Intent(mContext, CaptureActivity.class);
//                ((Activity) mContext).startActivityForResult(intent, Constants.RESULT_SCAN);
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

}

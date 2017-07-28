package com.coco3g.daishu.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.coco3g.daishu.R;
import com.coco3g.daishu.net.utils.ShareAppUtils;

import java.util.HashMap;

/**
 * Created by MIN on 16/5/18.
 */
public class SharePopupWindow extends PopupWindow implements View.OnClickListener {
    Context mContext;
    private View mView;
    private ImageView mQQ, mWeiXin, mWeiBo, mPengYouQuan;
    private Button mCancle;
    ShareAppUtils mShareAppUtils;
    String mShareUrl = "", mShareTitle = "", mShareDesc = "", mShareImage = "";
    //
    private int mShareType = -1;  //1：qq   2:微信    3：朋友圈  4：微博

    public SharePopupWindow(Context context, HashMap<String, String> shareinfo) {
        super(context);
        this.mContext = context;
        mShareUrl = shareinfo.get("url");
        mShareTitle = shareinfo.get("title");
        mShareDesc = shareinfo.get("content");
        if (TextUtils.isEmpty(mShareDesc)) {
            mShareDesc = shareinfo.get("resume");
        }
        mShareImage = shareinfo.get("thumb");
        //
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.view_share, null);
        mQQ = (ImageView) mView.findViewById(R.id.share_iv_qq);
        mWeiXin = (ImageView) mView.findViewById(R.id.share_iv_weixin);
        mWeiBo = (ImageView) mView.findViewById(R.id.share_iv_weibo);
        mPengYouQuan = (ImageView) mView.findViewById(R.id.share_iv_pengyouquan);
        mCancle = (Button) mView.findViewById(R.id.share_btn_cancle);
        //
        mQQ.setOnClickListener(this);
        mWeiXin.setOnClickListener(this);
        mWeiBo.setOnClickListener(this);
        mPengYouQuan.setOnClickListener(this);
        //
        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();//销毁弹框
            }
        });
        //设置view
        this.setContentView(mView);
        //设置弹框的宽和高
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置窗体可点击
        this.setFocusable(true);
        //设置弹出动画
        this.setAnimationStyle(R.style.PopupWindowAnim);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable cd = new ColorDrawable(0xb0000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(cd);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_iv_qq: // qq分享
                onShareClick();
                mShareType = 1;
                mShareAppUtils = new ShareAppUtils(mContext, 1, mShareUrl, mShareTitle, mShareDesc, mShareImage);
                break;
            case R.id.share_iv_weixin: // 微信分享
                onShareClick();
                mShareType = 2;
                mShareAppUtils = new ShareAppUtils(mContext, 2, mShareUrl, mShareTitle, mShareDesc, mShareImage);
                break;
            case R.id.share_iv_pengyouquan: // 朋友圈分享
                onShareClick();
                mShareType = 3;
                mShareAppUtils = new ShareAppUtils(mContext, 3, mShareUrl, mShareTitle, mShareDesc, mShareImage);
                break;
            case R.id.share_iv_weibo: // 新浪微博分享
                onShareClick();
                mShareType = 4;
                mShareAppUtils = new ShareAppUtils(mContext, 4, mShareUrl, mShareTitle, mShareDesc, mShareImage);
                break;

        }
    }

    //获取那种分享方式
    public int getmShareType() {
        return mShareType;
    }


    private OnShareClickListener onShareClickListener;

    public interface OnShareClickListener {
        void onShareClick();
    }

    public void setOnShareClickListener(OnShareClickListener onShareClickListener) {
        this.onShareClickListener = onShareClickListener;
    }

    public void onShareClick() {
        if (onShareClickListener != null) {
            onShareClickListener.onShareClick();
        }
    }


}


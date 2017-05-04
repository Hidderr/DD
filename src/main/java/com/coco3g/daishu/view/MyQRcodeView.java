package com.coco3g.daishu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Map;

/**
 * Created by coco3g on 17/4/27.
 */

public class MyQRcodeView extends RelativeLayout {


    private Context mContext;
    private View mView;
    private ImageView mImageAvatar, mImageQRcode;
    private TextView mTxtName;
    private LinearLayout mLinearRoot;

    private LayoutParams avatar_lp, qrcode_lp;

    //
    private Map<String, String> infoMap;


    public MyQRcodeView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MyQRcodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MyQRcodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.view_my_qrcode, this);
        mImageAvatar = (ImageView) mView.findViewById(R.id.image_my_qrcode_avatar);
        mImageQRcode = (ImageView) mView.findViewById(R.id.image_my_qrcode);
        mTxtName = (TextView) mView.findViewById(R.id.tv_my_qrcode_name);
        //
        avatar_lp = new LayoutParams(Global.screenWidth / 6, Global.screenWidth / 6);
        mImageAvatar.setLayoutParams(avatar_lp);
        qrcode_lp = new LayoutParams(Global.screenWidth / 2, Global.screenWidth / 2);
        mImageQRcode.setLayoutParams(qrcode_lp);
        //
        setInfo();
    }

    public void setInfo() {

        if (Global.USERINFOMAP != null) {

            //头像
            ImageLoader.getInstance().displayImage(Global.USERINFOMAP.get("avatar"), mImageAvatar, new DisplayImageOptionsUtils().circleImageInit());
            //二维码
//        ImageLoader.getInstance().displayImage(Global.USERINFOMAP.get("avatar"),mImageAvatar,new DisplayImageOptionsUtils().circleImageInit());
            ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.pic_qrcode_default, mImageQRcode, new DisplayImageOptionsUtils().init());
            //名字
            mTxtName.setText(Global.USERINFOMAP.get("nickname"));

        }

    }


}

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


public class HomeMenuImageView extends LinearLayout {
    Context mContext;
    View view;
    ImageView mImageIcon;
    TextView mTxtTitle, mTxtUnreadCount;
    RelativeLayout mRelativeUnRead;

    public HomeMenuImageView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public HomeMenuImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater lay = LayoutInflater.from(mContext);
        view = lay.inflate(R.layout.a_home_menu_image, this);
        mImageIcon = (ImageView) view.findViewById(R.id.image_home_menu_item_icon);
        mTxtTitle = (TextView) view.findViewById(R.id.tv_home_menu_item_title);
        mTxtUnreadCount = (TextView) view.findViewById(R.id.tv_unread_count);
        mRelativeUnRead = (RelativeLayout) view.findViewById(R.id.relative_home_menu_unread);
    }

    public void setIcon(int resID, String title) {
        mImageIcon.setImageResource(resID);
        mTxtTitle.setText(title);
    }

    public void setTextSize(float size) {
        mTxtTitle.setTextSize(size);
    }

    public void setSelected(int index, boolean selected) {
        super.setSelected(selected);
        mTxtTitle.setSelected(selected);
//        if (selected) {
//            mTxtTitle.setTextColor(getResources().getColor(R.color.text_color_blue_2));
//        } else if (!selected) {
//            mTxtTitle.setTextColor(getResources().getColor(R.color.text_color_2));
//        }
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


//    //设置体统消息提醒
//    public void setSystemRemind(boolean haveMsg) {
//        if (haveMsg) {
//            mRelativeUnRead.setVisibility(VISIBLE);
//            mRelativeUnRead.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_oveal_txt_bg));
//        } else {
//            mRelativeUnRead.setVisibility(GONE);
//        }
//    }


//
//	private void loadAnim(int index) {
//		PropertyValuesHolder pvhZ = null;
//		PropertyValuesHolder pvhX = null;
//		PropertyValuesHolder pvhY = null;
//		ObjectAnimator animator = null;
//		switch (index) {
//		case 0: // 首页
//			// pvhX = PropertyValuesHolder.ofFloat("scaleX", 0.7f, 1.0f, 0.8f,
//			// 1.0f, 0.9f, 1f);
//			pvhY = PropertyValuesHolder.ofFloat("rotationY", 0.0f, -180.0f);
//			// pvhZ = PropertyValuesHolder.ofFloat("alpha", 0.5f, 0.7f, 8f,
//			// 0.9f, 1f);
//			animator = ObjectAnimator.ofPropertyValuesHolder(mImageIcon, pvhY);
//			break;
//		case 1: // 分类
//			pvhX = PropertyValuesHolder.ofFloat("scaleX", 0.7f, 1.0f, 0.8f, 1.0f, 0.9f, 1f);
//			pvhY = PropertyValuesHolder.ofFloat("scaleY", 0.7f, 1.0f, 0.8f, 1.0f, 0.9f, 1f);
//			pvhZ = PropertyValuesHolder.ofFloat("alpha", 0.5f, 0.7f, 8f, 0.9f, 1f);
//			animator = ObjectAnimator.ofPropertyValuesHolder(mImageIcon, pvhX, pvhY, pvhZ);
//			break;
//		case 2: // 购物车
//			pvhY = PropertyValuesHolder.ofFloat("rotation", 0.0f, -20.0f, 20f, -20.0f, 20f, 0f);
//			animator = ObjectAnimator.ofPropertyValuesHolder(mImageIcon, pvhY);
//			break;
//		case 3: // 个人中心
//			pvhX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.3f, 1.5f, 1.6f, 1.7f);
//			pvhY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.3f, 1.5f, 1.6f, 1.7f);
//			pvhZ = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.7f, 0.5f, 0.3f, 0f);
//			animator = ObjectAnimator.ofPropertyValuesHolder(mImageIcon1, pvhX, pvhY, pvhZ);
//			animator.addListener(new AnimatorListenerAdapter() {
//
//				@Override
//				public void onAnimationEnd(Animator animation) {
//					// TODO Auto-generated method stub
//					super.onAnimationEnd(animation);
//					mImageIcon1.setVisibility(View.INVISIBLE);
//				}
//
//				@Override
//				public void onAnimationStart(Animator animation) {
//					// TODO Auto-generated method stub
//					super.onAnimationStart(animation);
//					mImageIcon1.setVisibility(View.VISIBLE);
//				}
//
//			});
//			break;
//		case 4: // 果实
//			pvhX = PropertyValuesHolder.ofFloat("scaleX", 0.7f, 1.0f, 0.8f, 1.0f, 0.9f, 1f);
//			pvhY = PropertyValuesHolder.ofFloat("scaleY", 0.7f, 1.0f, 0.8f, 1.0f, 0.9f, 1f);
//			pvhZ = PropertyValuesHolder.ofFloat("alpha", 0.5f, 0.7f, 8f, 0.9f, 1f);
//			animator = ObjectAnimator.ofPropertyValuesHolder(mImageIcon, pvhX, pvhY, pvhZ);
//			break;
//		}
//		animator.setDuration(500);// 动画时间
//		animator.setInterpolator(new LinearInterpolator());// 动画插值
//		animator.start();
//	}

}

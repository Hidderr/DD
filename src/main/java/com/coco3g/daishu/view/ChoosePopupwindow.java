package com.coco3g.daishu.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Global;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by coco3g on 17/5/9.
 */

public class ChoosePopupwindow extends PopupWindow {
    private Context mContext;
    OnTextSeclectedListener onTextSeclectedListener;


    public ChoosePopupwindow(final Context mContext, int width, int height, ArrayList<Map<String, String>> infoList, int selectedIndex,
                             String selectedTitle) {
        this.mContext = mContext;

        LinearLayout linearLayout;
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_choose, null);
        linearLayout = (LinearLayout) view.findViewById(R.id.linear_ddddd);
        //
        int margin_8 = Global.dipTopx(mContext, 8f);
        int size = infoList.size();
        for (int i = 0; i < size; i++) {
            final TextView textView = new TextView(mContext);
            if (selectedIndex == i) {
                textView.setTextColor(mContext.getResources().getColor(R.color.text_color_red_2));
                Drawable drawable = ContextCompat.getDrawable(mContext, R.mipmap.pic_dui_hao_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textView.setCompoundDrawables(null, null, drawable, null);
            } else if (!TextUtils.isEmpty(selectedTitle) && selectedTitle.equals(infoList.get(i).get("title"))) {
                textView.setTextColor(mContext.getResources().getColor(R.color.text_color_red_2));
                Drawable drawable = ContextCompat.getDrawable(mContext, R.mipmap.pic_dui_hao_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textView.setCompoundDrawables(null, null, drawable, null);
            } else {
                textView.setTextColor(mContext.getResources().getColor(R.color.text_color_2));
            }
//            textView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.selector_txt_yellow_color));
            textView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            textView.setText(infoList.get(i).get("title"));
            textView.setTextSize(14f);
            textView.setPadding(Global.dipTopx(mContext, 30f), margin_8, Global.dipTopx(mContext, 30f), margin_8);
            textView.setTag(i);
            textView.setGravity(Gravity.START);
            //
            LinearLayout.LayoutParams txt_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            txt_lp.setMargins(0, Global.dipTopx(mContext, 1f), 0, 0);
            textView.setLayoutParams(txt_lp);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (int) v.getTag();
                    textSelected(tag);
                    dismiss();
                }
            });
            //
            linearLayout.addView(textView);
        }
        //设置view
        this.setContentView(view);
        //设置弹框的宽和高
        this.setWidth(width);
        if (height == 0) {
            this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            this.setHeight(height);
        }
        //设置窗体可点击
        this.setFocusable(true);
        //设置弹出动画
//        this.setAnimationStyle(R.style.ChoosePopupWindowAnim);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable cd = new ColorDrawable(0xb0000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(cd);
    }


    public interface OnTextSeclectedListener {
        void textSelected(int position);
    }

    public void setOnTextSeclectedListener(OnTextSeclectedListener onTextSeclectedListener) {
        this.onTextSeclectedListener = onTextSeclectedListener;
    }

    public void textSelected(int postion) {
        if (onTextSeclectedListener != null) {
            onTextSeclectedListener.textSelected(postion);
        }
    }

}

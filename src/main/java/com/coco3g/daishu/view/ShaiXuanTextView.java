package com.coco3g.daishu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;


/**
 * Created by coco3g on 16/10/17.
 */
public class ShaiXuanTextView extends LinearLayout {
    private Context mContext;
    private View mView;
    private TextView mTextView;
    private OnClickTextViewListener mOnClickTextViewListener;

    public ShaiXuanTextView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ShaiXuanTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public ShaiXuanTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.view_shai_xuan_textview, this);
        mTextView = (TextView) mView.findViewById(R.id.tv_shai_xuan);
        mTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextView.setSelected(true);
                textViewOnClicked();
            }
        });

    }

    public void setText(String name) {
        mTextView.setText(name);
    }


    public interface OnClickTextViewListener {
        void textViewChoosed();
    }

    public void setOnClickTextViewListener(OnClickTextViewListener onClickTextViewListener) {
        this.mOnClickTextViewListener = onClickTextViewListener;
    }

    public void textViewOnClicked() {
        if (mOnClickTextViewListener != null) {
            mOnClickTextViewListener.textViewChoosed();
        }
    }


}

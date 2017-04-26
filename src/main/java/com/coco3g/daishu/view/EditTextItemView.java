package com.coco3g.daishu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.coco3g.daishu.R;

/**
 * Created by lisen on 16/4/5.
 */
public class EditTextItemView extends RelativeLayout {
    Context mContext;
    View mView;
    public EditText mEdit;

    public EditTextItemView(Context context) {
        super(context);
        mContext = context;
    }

    public EditTextItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public EditTextItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    private void initView() {
        LayoutInflater lay = LayoutInflater.from(mContext);
        mView = lay.inflate(R.layout.a_edittext_item, this);
        mEdit = (EditText) mView.findViewById(R.id.edit_item);
    }

    /**
     * 设置edittext提示信息
     *
     * @param hint
     */
    public void setHintText(String hint) {
        mEdit.setHint(hint);
    }

    /**
     * 设置行数
     *
     * @param nums
     */
    public void setLineNums(int nums) {
        mEdit.setLines(nums);
    }

    /**
     * 设置最小行数
     *
     * @param nums
     */
    public void setMinLineNums(int nums) {
        mEdit.setMinLines(nums);
    }

    /**
     * 设置最大行数
     *
     * @param nums
     */
    public void setMaxLineNums(int nums) {
        mEdit.setMaxLines(nums);
    }

    /**
     * 设置输入类型
     *
     * @param format
     */
    public void setInputFormat(int format) {
        mEdit.setInputType(format);
    }

    /**
     * 获取输入的文字
     *
     * @return
     */
    public String getText() {
        return mEdit.getText().toString();
    }

}

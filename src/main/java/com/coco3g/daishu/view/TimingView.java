package com.coco3g.daishu.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import com.coco3g.daishu.R;


/**
 * Created by lisen on 16/7/1.
 */
public class TimingView extends TextView {
    Context mContext;
    private boolean mIsStartTimer = false; // 是否已经开始计时
    int mMaxSecond = 60; // 从xx秒开始倒计时
    int mCurrSecond = 60; // 当前还剩的时间
    int mTimeInterval = 1000; // 倒计时的时间间隔（单位：毫秒）
    public TimingComplete timingcomplete;

    public TimingView(Context context) {
        super(context);
    }

    public TimingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public TimingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置倒计时的最大秒数
     *
     * @param second
     */
    public TimingView setMaxSecond(int second) {
        mMaxSecond = second;
        mCurrSecond = second;
        return this;
    }

    /**
     * 设置倒计时的时间间隔
     *
     * @param interval
     */
    public TimingView setTimeInterval(int interval) {
        mTimeInterval = interval;
        return this;
    }

    /**
     * 开始倒计时
     */
    public void startTiming() {
        mIsStartTimer = true;
        mCurrSecond = mMaxSecond;
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                while (mIsStartTimer) {
                    if (mCurrSecond < 0) {
                        return;
                    } else {
                        Message mess = new Message();
                        mHandlerUpdateUI.sendMessage(mess);
                        try {
                            sleep(mTimeInterval);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        mCurrSecond--;
                    }
                }

            }

        }.start();
    }

    Handler mHandlerUpdateUI = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mCurrSecond == 0) {
                setClickable(true);
                try {
                    String unReceive = mContext.getString(R.string.smssdk_unreceive_identify_code);
                    setText(unReceive);
                    mIsStartTimer = false;
//                    complete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                setClickable(false);
                try {
                    String unReceive = mContext.getString(R.string.smssdk_receive_msg, mCurrSecond);
                    setText(Html.fromHtml(unReceive));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 停止计时
     */
    public void stopTiming() {
        mCurrSecond = -1;
    }

    public void setOnTimingCompleteListener(TimingComplete timingcomplete) {
        this.timingcomplete = timingcomplete;
    }


    public interface TimingComplete {
        void complete();
    }

    private void complete() {
        if (timingcomplete != null) {
            timingcomplete.complete();
        }
    }
}

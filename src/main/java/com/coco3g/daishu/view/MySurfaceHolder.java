package com.coco3g.daishu.view;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lisen on 2016/9/27.
 */

public class MySurfaceHolder implements SurfaceHolder.Callback {
    Context mContext;
    SurfaceView mSurfaceVideo;
    MediaPlayer mMediaPlayer;
    //
    ShowButtonListener showbuttonlistener;
    PauseListener pauselistener;
    OnComeInListener showComeInListener;
    Timer mTimer;
    TimerTask mTask;
    private int position = 0;

    public MySurfaceHolder(final Context mContext, SurfaceView surfaceview) {
        this.mContext = mContext;
        mSurfaceVideo = surfaceview;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("创建surface", "创建surface");
        playVideo();
//        timerControl(false);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        try {
//            mMediaPlayer.seekTo(0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
//            mTask.cancel();
//            mTimer.cancel();
            position = mMediaPlayer.getCurrentPosition();
            stop();
        }
    }

//    private void timerControl(final boolean isreplay) {
//        mTimer = new Timer();
//        mTask = new TimerTask() {
//            @Override
//            public void run() {
//                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
//                    int currSec = mMediaPlayer.getCurrentPosition() / 1000;
//                    if (currSec == 1 && !isreplay) {   //显示"直接登录"
//                        Message mess = new Message();
//                        mess.what = 3;
//                        mHandler.sendMessage(mess);
//                    }
//                    if (currSec == 20 && !isreplay) {   //显示"从新播放"
//                        Message mess = new Message();
//                        mess.what = 0;
//                        mHandler.sendMessage(mess);
//                    }
//                    if (currSec == 26) {      //停止界面
//                        mMediaPlayer.pause();
//                        Message mess = new Message();
//                        mess.what = 1;
//                        mHandler.sendMessage(mess);
//                        mTask.cancel();
//                        mTimer.cancel();
//                    }
//                }
//            }
//        };
//        mTimer.schedule(mTask, 0, 500);
//    }
//
//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    showJumpInto();
//                    break;
//            }
//        }
//    };

    /**
     * 播放视频
     */
    private void playVideo() {
        try {
            mMediaPlayer = new MediaPlayer();
            AssetFileDescriptor fileDescriptor = mContext.getAssets().openFd("start.mp4");
            mMediaPlayer.reset();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            // 把视频输出到SurfaceView上
            mMediaPlayer.setDisplay(mSurfaceVideo.getHolder());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.seekTo(position);
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    replayVideo();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void replayVideo() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(0);
        } else {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            position = 0;
            playVideo();
//            timerControl(true);
        }
    }

    /**
     * 停止播放
     */
    private void stop() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public MySurfaceHolder setOnShowButtonLisntener(ShowButtonListener showbuttonlistener) {
        this.showbuttonlistener = showbuttonlistener;
        return this;
    }

    public interface ShowButtonListener {
        void showButton();
    }

    public void showButton() {
        if (showbuttonlistener != null) {
            showbuttonlistener.showButton();
        }
    }

    public MySurfaceHolder setOnPauseListener(PauseListener pauselistener) {
        this.pauselistener = pauselistener;
        return this;
    }

    public interface PauseListener {
        void videoPause();
    }

    public void videoPause() {
        if (pauselistener != null) {
            pauselistener.videoPause();
        }
    }

    public interface OnComeInListener {
        void comeIn();
    }

    public void setOnComeInListener(OnComeInListener showComeInListener) {
        this.showComeInListener = showComeInListener;
    }

    public void showJumpInto() {
        showComeInListener.comeIn();
    }


}

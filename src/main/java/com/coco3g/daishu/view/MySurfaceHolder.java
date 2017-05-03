package com.coco3g.daishu.view;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
    //    Timer mTimer;
//    TimerTask mTask;
    private int position = 0;
    String mUrl = "";

    public MySurfaceHolder(final Context mContext, SurfaceView surfaceview, String url) {
        this.mContext = mContext;
        mSurfaceVideo = surfaceview;
        mUrl = url;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        playVideo();
//        timerControl(false);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
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
////                    if (currSec == 20 && !isreplay) {
////                        Message mess = new Message();
////                        mess.what = 0;
////                        mHandler.sendMessage(mess);
////                    }
//                    if (currSec == 6) {
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

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showButton();
                    break;
                case 1:
//                    videoPause();
                    replayVideo();
                    break;
            }
        }
    };

    /**
     * 播放视频
     */
    public void playVideo() {
        try {
            mMediaPlayer = new MediaPlayer();
//            AssetFileDescriptor fileDescriptor = mContext.getAssets().openFd("start_video.mp4");
            mMediaPlayer.reset();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            mMediaPlayer.setDataSource(mContext, Uri.parse(mUrl));
            // 把视频输出到SurfaceView上
            mMediaPlayer.setDisplay(mSurfaceVideo.getHolder());
            mMediaPlayer.setVolume(0, 0);
            mMediaPlayer.prepare();
//            mMediaPlayer.start();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
//                    mMediaPlayer.seekTo(position);
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    replayVideo();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void replayVideo() {
//        mMediaPlayer.stop();
//        mMediaPlayer.release();
//        mMediaPlayer = null;
        position = 0;
//        playVideo();
        mMediaPlayer.seekTo(position);
        mMediaPlayer.start();
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        } else {
            return false;
        }
    }

    /**
     * 暂停后继续播放
     */
    public void pauseAndStart() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
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
}

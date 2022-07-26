package com.anna.lib_video.video.core.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.anna.lib_video.R;

import java.io.IOException;

public class SmartVideoView extends RelativeLayout implements View.OnClickListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        TextureView.SurfaceTextureListener {
    private Context context;
    private MediaPlayer mMediaPlayer;
    private ScreenEventReceiver mScreenReceiver;
    /**
     * Constant
     */
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_PAUSING = 2;
    private static final int STATE_COMP = 3;
    private static final int LOAD_TOTAL_COUNT = 3;

    /**
     * Status状态保护
     */
    private boolean canPlay = true;
    private boolean mIsRealPause;
    private boolean mIsComplete;
    private int mCurrentCount;
    private int playerState = STATE_IDLE;
    /**
     * UI
     */
    private TextureView mVideoView;
    private RelativeLayout mPlayerView;
    private Button mMiniPlayBtn;
    private ImageView mFullBtn;
    private ImageView mLoadingBar;
    private boolean isMute = true;

    /**
     * true is no voice
     */
    public void mute(boolean mute) {
        isMute = mute;
        if (mMediaPlayer != null) {
            float volume = isMute ? 0.0f : 1.0f;
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    /**
     * 屏幕宽高
     */
    private int mScreenWidth;
    private int mVideoHeight;

    /**
     * Data
     */
    private String mUrl;


    public SmartVideoView(Context context) {
        this(context,null);
    }

    public SmartVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SmartVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initData();
        initView();
        registerBroadcastReceiver();
    }

    /**
     * 初始化布局
     * 1.设置点击事件
     * 2.SurfaceTexture回调事件
     * 3.初始化小屏状态
     */
    private void initView() {
        mPlayerView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.video_player_layout,this);
        mVideoView = findViewById(R.id.xadsdk_player_video_textureView);
        mVideoView.setOnClickListener(this);
        mVideoView.setSurfaceTextureListener(this);
        initSmallLayout();
    }

    /**小模式状态
     * 1.设置各种View的点击事件
     */
    private void initSmallLayout() {
        setPlayViewlayoutParam();
        mMiniPlayBtn = mPlayerView.findViewById(R.id.xadsdk_small_play_btn);
        mFullBtn = mPlayerView.findViewById(R.id.xadsdk_to_full_view);
        mLoadingBar = mPlayerView.findViewById(R.id.loading_bar);
        mMiniPlayBtn.setOnClickListener(this);
        mFullBtn.setOnClickListener(this);
    }

    /**
     * 初始化宽高：16:9
     */
    private void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mVideoHeight = (int) (mScreenWidth * (9/16.0f));
    }
    /**
     * 初始化MediaPlayer
     */
    private void checkMediaPlayer(){
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }


    /**数据元加载成功，准备调用start开启播放
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        //1.显示playing状态‘
        showPlayView();
        //2.设置当前状态为Playing
        setPlayerState(STATE_PLAYING);
        //3设置静音模式
        mute(true);
        //4.开始调用start启动MediaPlayer
        mMediaPlayer.start();
        mCurrentCount = 0;
    }


    /**播放事件完成后回调：
     * 1.显示完成状态的view
     * 2.设置当前状态太为完成撞他
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (listener != null) {
            listener.onAdVideoLoadComplete();
        }
        //显示播放完成状态
        playBack();
        //设置当前状态为完成状态
        setPlayerState(STATE_COMP);

        setIsComplete(true);
        setIsRealPause(true);
    }

    /**出现异常
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //1设置当前状态为ERROR
        setPlayerState(STATE_ERROR);
        //2回调error事件给上游
        if (this.listener != null) {
            listener.onAdVideoLoadFailed();
        }
        //3调用stop方法
        stop();
        return false;
    }

    /**各种View的点击事件处理
     * @param v
     */
    @Override
    public void onClick(View v) {
        if(v == mMiniPlayBtn){
            if (this.playerState == STATE_PAUSING || this.playerState == STATE_COMP) {
                resume();
                this.listener.onClickPlay();
            }else {
                load();
            }
        }else if (v == this.mFullBtn) {
            //回调给外部调用，切换到大屏状态
            listener.onClickFullScreenBtn();
        }else if (v == mVideoView) {
            //回调给外部
            this.listener.onClickVideo();
        }
    }

    /**
     * 加载视频
     */
    public void load(){
        //1.判断状态是否是初始化状态
        if(playerState != STATE_IDLE){
            return;
        }
        //2.显示对应的Vew状态
        showLoadingView();
        //3.判断MediaPlayer是否已经初始化过
        checkMediaPlayer();
        //4.异步加载请求，回调onPrepare方法
        try {
            mMediaPlayer.setDataSource(mUrl);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停按钮
     */
    public void pause(){
        //1检测状态是否是播放状态
        if(playerState != STATE_PLAYING){
            return;
        }
        if(isPlaying()){
            //3调用MediaPlayer的pause方法
            mMediaPlayer.pause();
        }
        //2显示暂停状态
        showPauseView(true);
        //4设置当前状态为pause状态
        setPlayerState(STATE_PAUSING);

    }

    /**
     * 恢复按钮
     */
    public void resume(){
        //1检测当前状态是否是暂停状态
        if(playerState != STATE_PAUSING && this.playerState != STATE_COMP){
            return;
        }
        if(!isPlaying()){
            //3调用MediaPlayer的resume方法
            mMediaPlayer.start();
        }
        //2显示恢复状态
        showPlayView();
        //4设置当前状态为播放状态
        entryResumeState();
    }

    //跳到指定点播放视频
    public void seekAndResume(int position) {
        if (mMediaPlayer != null) {
            showPauseView(true);
            entryResumeState();
            mMediaPlayer.seekTo(position);
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        }
    }

    //跳到指定点暂停视频
    public void seekAndPause(int position) {
        if (this.playerState != STATE_PLAYING) {
            return;
        }
        showPauseView(false);
        setPlayerState(STATE_PAUSING);
        if (isPlaying()) {
            mMediaPlayer.seekTo(position);
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mMediaPlayer.pause();
                }
            });
        }
    }

    //全屏不显示暂停状态,后续可以整合，不必单独出一个方法
    public void pauseForFullScreen() {
        if (playerState != STATE_PLAYING) {
            return;
        }
        setPlayerState(STATE_PAUSING);
        if (isPlaying()) {
            mMediaPlayer.pause();
            if (!this.canPlay) {
                mMediaPlayer.seekTo(0);
            }
        }
    }



    /**
     * 停止播放，需要seek到0的位置,一般出异常才会调用这个
     */
    private void stop(){
        //1.将MediaPlayer释放掉
        if(mMediaPlayer != null){
            mMediaPlayer.reset();
            mMediaPlayer.setOnSeekCompleteListener(null);
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        //2.设置当前状态为初始化状态
        setPlayerState(STATE_IDLE);
        //3.判断是否满足重试条件，根据重试次数来
        if(mCurrentCount<=LOAD_TOTAL_COUNT){
            //满足重新加载：则调用load方法重试并将充实次数加1
            load();
            mCurrentCount+=1;
        }else{
            //不满足：则显示pause状态
            showPauseView(false);
        }
    }

    /**
     * 释放MediaPlayer状态
     */
    public void destroy(){
        //1.将MediaPlayer释放掉
        if(mMediaPlayer != null){
            mMediaPlayer.reset();
            mMediaPlayer.setOnSeekCompleteListener(null);
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        //2.设置当前状态为初始化状态
        setPlayerState(STATE_IDLE);
        //3 显示pause状态
        showPauseView(false); //除了播放和loading外其余任何状态都显示pause
        mCurrentCount = 0;
        setIsComplete(false);
        setIsRealPause(false);
        unRegisterBroadcastReceiver();
    }


    /**
     * 显示加载View
     */
    private void showLoadingView() {
        mFullBtn.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.VISIBLE);
        AnimationDrawable anim = (AnimationDrawable) mLoadingBar.getBackground();
        anim.start();
        mMiniPlayBtn.setVisibility(View.GONE);
    }

    /**
     * 显示播放View
     */
    private void showPlayView() {
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        mMiniPlayBtn.setVisibility(View.GONE);
        mFullBtn.setVisibility(VISIBLE);
    }

    /**显示状态状态
     * @param show 是否显示暂停和播放按钮
     */
    private void showPauseView(boolean show) {
        mFullBtn.setVisibility(show ? View.VISIBLE : View.GONE);
        mMiniPlayBtn.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
    }

    //播放完成后回到初始状态
    private void playBack(){
        setPlayerState(STATE_PAUSING);
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnSeekCompleteListener(null);
            mMediaPlayer.seekTo(0);
            mMediaPlayer.pause();
        }
        this.showPauseView(false);
    }

    /**
     * 进入播放状态时的状态更新
     */
    private void entryResumeState() {
        canPlay = true;
        setPlayerState(STATE_PLAYING);
        setIsRealPause(false);
        setIsComplete(false);
    }


    /**是否在播放状态
     * @return
     */
    public boolean isPlaying() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    /**
     * 设置播放器的宽高
     */
    private void setPlayViewlayoutParam() {
        LayoutParams params = new LayoutParams(mScreenWidth,mVideoHeight);
        params.addRule(CENTER_IN_PARENT);
        params.width = mScreenWidth;
        params.height = mVideoHeight;
        mPlayerView.setLayoutParams(params);
    }

    public void setIsComplete(boolean isComplete) {
        mIsComplete = isComplete;
    }

    public void setIsRealPause(boolean isRealPause) {
        this.mIsRealPause = isRealPause;
    }
    /**设置视频的url地址
     * @param url
     */
    public void setDataSource(String url) {
        this.mUrl = url;
    }

    public boolean isRealPause() {
        return mIsRealPause;
    }

    public boolean isComplete() {
        return mIsComplete;
    }
    private Surface videoSurface;

    /**给mMediaPlayer设置Surface
     * @param surface
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        videoSurface = new Surface(surface);
        checkMediaPlayer();
        mMediaPlayer.setSurface(videoSurface);
        load();
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }
    public void setPlayerState(int playerState) {
        this.playerState = playerState;
    }



    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE && playerState == STATE_PAUSING) {
            if (isRealPause() || isComplete()) {
                pause();
            } else {
                resume();
            }
        } else {
            pause();
        }
    }


    private void registerBroadcastReceiver() {
        if (mScreenReceiver == null) {
            mScreenReceiver = new ScreenEventReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            getContext().registerReceiver(mScreenReceiver, filter);
        }
    }
    private ADVideoPlayerListener listener;
    public void setListener(ADVideoPlayerListener listener){
        this.listener = listener;
    }
    private void unRegisterBroadcastReceiver() {
        if (mScreenReceiver != null) {
            getContext().unregisterReceiver(mScreenReceiver);
        }
    }

    public void isShowFullBtn(boolean isShow) {
        mFullBtn.setImageResource(isShow ? R.drawable.xadsdk_ad_mini : R.drawable.xadsdk_ad_mini_null);
        mFullBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public int getCurrentPosition() {
        if (this.mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }


    /**
     * 监听锁屏事件的广播接收器
     */
    private class ScreenEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //主动锁屏时 pause, 主动解锁屏幕时，resume
            switch (intent.getAction()) {
                case Intent.ACTION_USER_PRESENT:
                    if (playerState == STATE_PAUSING) {
                        if (mIsRealPause) {
                            //手动点的暂停，回来后还暂停
                            pause();
                        } else {
                            resume();
                        }
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    if (playerState == STATE_PLAYING) {
                        pause();
                    }
                    break;
            }
        }
    }

    /**
     * 供slot层来实现具体点击逻辑,具体逻辑还会变，
     * 如果对UI的点击没有具体监测的话可以不回调
     */
    public interface ADVideoPlayerListener {

        void onBufferUpdate(int time);

        void onClickFullScreenBtn();

        void onClickVideo();

        void onClickBackBtn();

        void onClickPlay();

        void onAdVideoLoadSuccess();

        void onAdVideoLoadFailed();

        void onAdVideoLoadComplete();
    }




}

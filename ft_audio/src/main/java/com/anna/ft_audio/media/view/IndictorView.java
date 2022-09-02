package com.anna.ft_audio.media.view;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;


import com.anna.ft_audio.R;
import com.anna.ft_audio.media.adapter.MusicPagerAdapter;
import com.anna.ft_audio.media.core.AudioController;
import com.anna.ft_audio.media.event.AudioLoadEvent;
import com.anna.ft_audio.media.event.AudioPauseEvent;
import com.anna.ft_audio.media.event.AudioStartEvent;
import com.anna.ft_audio.media.exception.AudioQueueEmptyException;
import com.anna.ft_audio.media.model.AudioBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class IndictorView extends RelativeLayout implements ViewPager.OnPageChangeListener{
    private Context mContext;

    /*
     * view相关
     */
    private ImageView mImageView;
    private ViewPager mViewPager;
    private MusicPagerAdapter mMusicPagerAdapter;
    /*
     * data
     */
    private AudioBean mAudioBean; //当前播放歌曲
    private ArrayList<AudioBean> mQueue; //播放队列
    public IndictorView(Context context) {
        this(context,null);
    }

    public IndictorView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IndictorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        EventBus.getDefault().register(this);
        initData();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }
    private void initData() {
        mQueue = AudioController.getInstance().getmQueue();
        try {
            mAudioBean = AudioController.getInstance().getNowPlaying();
        }catch (AudioQueueEmptyException e){
            e.printStackTrace();
            return;
        }
    }
    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.indictor_view, this);
        mImageView = rootView.findViewById(R.id.tip_view);
        mViewPager = rootView.findViewById(R.id.view_pager);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mMusicPagerAdapter = new MusicPagerAdapter(mQueue, mContext, null);
        mViewPager.setAdapter(mMusicPagerAdapter);
        showLoadView(false);
        //要在UI初始化完，否则会多一次listener响应
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //指定要播放的position
        AudioController.getInstance().setPlayIndex(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                showPlayView();
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                showPauseView();
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //更新viewpager为load状态
        mAudioBean = event.mAudioBean;
        showLoadView(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        //更新activity为暂停状态
        showPauseView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        //更新activity为播放状态
        showPlayView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    private void showLoadView(boolean isSmooth) {
        mViewPager.setCurrentItem(mQueue.indexOf(mAudioBean), isSmooth);
    }

    private void showPauseView() {
        Animator anim = mMusicPagerAdapter.getAnim(mViewPager.getCurrentItem());
        if (anim != null) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                anim.pause();
        }
    }

    private void showPlayView() {
        Animator anim = mMusicPagerAdapter.getAnim(mViewPager.getCurrentItem());
        if (anim != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                    &&anim.isPaused()) {
                anim.resume();
            } else {
                anim.start();
            }
        }
    }
}

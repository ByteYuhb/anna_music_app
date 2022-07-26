package com.anna.lib_video.video.core;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.anna.lib_video.video.core.view.SmartVideoView;
import com.anna.lib_video.video.core.view.VideoFullScreenDialog;
import com.anna.lib_video.utils.Utils;

public class VideoAdSlot implements SmartVideoView.ADVideoPlayerListener {
    private Context mContext;
    /**
     * UI
     */
    private SmartVideoView mVideoView;
    private ViewGroup mParentView;
    /**
     * Data
     */
    private String mXAdInstance;
    private SDKSlotListener mSlotListener;

    public VideoAdSlot(String adInstance, SDKSlotListener slotLitener) {

        mXAdInstance = adInstance;
        mSlotListener = slotLitener;
        mParentView = slotLitener.getAdParent();
        mContext = mParentView.getContext();
        initVideoView();
    }

    public void destroy() {
        mVideoView.destroy();
        mVideoView = null;
        mContext = null;
        mXAdInstance = null;
    }


    private void initVideoView() {
        mVideoView = new SmartVideoView(mContext);
        if (mXAdInstance != null) {
            mVideoView.setDataSource(mXAdInstance);
            mVideoView.setListener(this);
        }
        RelativeLayout paddingView = new RelativeLayout(mContext);
        paddingView.setBackgroundColor(mContext.getResources().getColor(android.R.color.black));
        paddingView.setLayoutParams(mVideoView.getLayoutParams());
        mParentView.addView(paddingView);
        mParentView.addView(mVideoView);
    }


    /**
     * 实现play层接口
     */
    @Override
    public void onClickFullScreenBtn() {
        //获取videoview在当前界面的属性
        Bundle bundle = Utils.getViewProperty(mParentView);
        mParentView.removeView(mVideoView);
        VideoFullScreenDialog dialog =
                new VideoFullScreenDialog(mContext, mVideoView, mXAdInstance, mVideoView.getCurrentPosition());
        dialog.setListener(new VideoFullScreenDialog.FullToSmallListener() {
            @Override
            public void getCurrentPlayPosition(int position) {
                backToSmallMode(position);
            }

            @Override
            public void playComplete() {
                bigPlayComplete();
            }
        });
        dialog.setViewBundle(bundle); //为Dialog设置播放器数据Bundle对象
        dialog.setSlotListener(mSlotListener);
        dialog.show();
    }

    private void backToSmallMode(int position) {
        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        mVideoView.setTranslationY(0); //防止动画导致偏离父容器
        mVideoView.isShowFullBtn(true);
        mVideoView.mute(true);
        mVideoView.setListener(this);
        mVideoView.seekAndResume(position);
    }

    private void bigPlayComplete() {
        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        mVideoView.setTranslationY(0); //防止动画导致偏离父容器
        mVideoView.isShowFullBtn(true);
        mVideoView.seekAndPause(0);
    }

    @Override
    public void onBufferUpdate(int time) {

    }

    @Override
    public void onClickVideo() {

    }

    @Override
    public void onClickBackBtn() {

    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void onAdVideoLoadSuccess() {
        if (mSlotListener != null) {
            mSlotListener.onVideoLoadSuccess();
        }
    }

    @Override
    public void onAdVideoLoadFailed() {
        if (mSlotListener != null) {
            mSlotListener.onVideoFailed();
        }
    }

    @Override
    public void onAdVideoLoadComplete() {
        if (mSlotListener != null) {
            mSlotListener.onVideoComplete();
        }
        mVideoView.setIsRealPause(true);
    }

    //传递消息到appcontext层
    public interface SDKSlotListener {

        ViewGroup getAdParent();

        void onVideoLoadSuccess();

        void onVideoFailed();

        void onVideoComplete();
    }
}

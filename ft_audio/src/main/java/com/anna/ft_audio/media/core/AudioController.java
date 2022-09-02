package com.anna.ft_audio.media.core;


import com.anna.ft_audio.media.db.GreenDaoHelper;
import com.anna.ft_audio.media.event.AudioCompleteEvent;
import com.anna.ft_audio.media.event.AudioErrorEvent;
import com.anna.ft_audio.media.event.AudioFavouriteEvent;
import com.anna.ft_audio.media.event.AudioPlayModeEvent;
import com.anna.ft_audio.media.exception.AudioQueueEmptyException;
import com.anna.ft_audio.media.model.AudioBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 控制播放逻辑类，注意添加一些控制方法时，要考虑是否需要增加Event,来更新UI
 */
public class AudioController {

    /**
     * 播放方式
     */
    public enum PlayMode {
        /**
         * 列表循环
         */
        LOOP,
        /**
         * 随机
         */
        RANDOM,
        /**
         * 单曲循环
         */
        REPEAT
    }
    private AudioPlayer mAudioPlayer;
    //播放队列,不能为空,不设置主动抛错
    private ArrayList<AudioBean> mQueue = new ArrayList<>();
    private int mQueueIndex = 0;
    private PlayMode mPlayMode = PlayMode.LOOP;

    private AudioController() {
        EventBus.getDefault().register(this);
        mAudioPlayer = new AudioPlayer();
    }

    /**设置歌曲列表
     * @param listBean
     */
    public void setmQueue(List<AudioBean> listBean) {
        setmQueue(listBean,0);
    }
    public void setmQueue(List<AudioBean> listBean,int index){
        if(mQueue == null) mQueue = new ArrayList<>();
        mQueue.addAll(listBean);
        mQueueIndex = index;
    }

    public ArrayList<AudioBean> getmQueue() {
        return mQueue;
    }

    public PlayMode getmPlayMode() {
        return mPlayMode;
    }

    public void setmPlayMode(PlayMode mPlayMode) {
        this.mPlayMode = mPlayMode;
        EventBus.getDefault().post(new AudioPlayModeEvent(mPlayMode));
    }



    /**
     * 队列头添加播放哥曲
     */
    public void addAudio(AudioBean bean) {
        this.addAudio(bean,0);
    }
    public void addAudio(AudioBean bean,int index){
        if (mQueue == null){
            mQueue = new ArrayList<>();
        }
        int queryId = queryBean(bean);
        if(queryId<=-1){
            //没添加过此id的歌曲，添加且直播番放
            addCustomBean(bean,index);
            setPlayIndex(index);
        }else{
            AudioBean currentBean = getNowPlaying();
            if(!currentBean.id.equals(queryId)){
                setPlayIndex(queryId);
            }
        }
    }

    /**
     * 对外提供获取当前播放时间
     */
    public int getNowPlayTime() {
        return mAudioPlayer.getCurrentPosition();
    }

    /**
     * 对外提供获取总播放时间
     */
    public int getTotalPlayTime() {
        return mAudioPlayer.getCurrentPosition();
    }

    /**
     * 对外提供的获取当前歌曲信息
     */
    public AudioBean getNowPlaying() {
        return getPlaying(mQueueIndex);
    }

    /**获取下一首AudioBean
     * @return
     */
    private AudioBean getNextPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex + 1) % mQueue.size();
                return getPlaying(mQueueIndex);
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                return getPlaying(mQueueIndex);
            case REPEAT:
                return getPlaying(mQueueIndex);
        }
        return null;
    }

    /**获取前一个AudioBean
     * @return
     */
    private AudioBean getPreviousPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex + mQueue.size() - 1) % mQueue.size();
                return getPlaying(mQueueIndex);
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                return getPlaying(mQueueIndex);
            case REPEAT:
                return getPlaying(mQueueIndex);
        }
        return null;
    }

    /**获取当前播放的AudioBean
     * @param index
     * @return
     */
    private AudioBean getPlaying(int index) {
        if (mQueue != null && !mQueue.isEmpty() && index >= 0 && index < mQueue.size()) {
            return mQueue.get(index);
        } else {
//            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
            return null;
        }
    }

    /**设置当前播放列表
     * @param index
     */
    public void setPlayIndex(int index) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
        mQueueIndex  = index;
        play();
    }

    /**
     * 加载当前index歌曲
     */
    public void play() {
        AudioBean bean = getPlaying(mQueueIndex);
        load(bean);
    }

    /**
     * 加载next index歌曲
     */
    public void next() {
        AudioBean bean = getNextPlaying();
        load(bean);
    }

    /**
     * 加载previous index歌曲
     */
    public void previous() {
        AudioBean bean = getPreviousPlaying();
        load(bean);
    }
    private void load(AudioBean bean) {
        mAudioPlayer.load(bean);
    }

    /**
     * 添加/移除到收藏
     */
    public void changeFavourite() {
        if (null != GreenDaoHelper.selectFavourite(getNowPlaying())) {
            //已收藏，移除
            GreenDaoHelper.removeFavourite(getNowPlaying());
            EventBus.getDefault().post(new AudioFavouriteEvent(false));
        } else {
            //未收藏，添加收藏
            GreenDaoHelper.addFavourite(getNowPlaying());
            EventBus.getDefault().post(new AudioFavouriteEvent(true));
        }
    }
    private void addCustomBean(AudioBean bean, int index) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
        mQueue.add(index,bean);
    }

    /**查找bean在队列中的位置
     *  -1:表示每找到，找到返回索引位置
     * @return
     */
    private int queryBean(AudioBean bean) {
        if(mQueue==null){
            throw new AudioQueueEmptyException("当前队列为空");
        }
        return mQueue.indexOf(bean);
    }

    public void resume() {
        mAudioPlayer.resume();
    }

    public void pause() {
        mAudioPlayer.pause();
    }
    /**
     * 播放/暂停切换
     */
    public void playOrPause() {
        if (isStartState()) {
            pause();
        } else if (isPauseState()) {
            resume();
        }
    }
    public void release() {
        mAudioPlayer.release();
        EventBus.getDefault().unregister(this);
    }
    /*
     * 获取播放器当前状态
     */
    private CustomMediaPlayer.Status getStatus() {
        return mAudioPlayer.getStatus();
    }
    /**
     * 对外提供是否播放中状态
     */
    public boolean isStartState() {
        return CustomMediaPlayer.Status.STARTED == getStatus();
    }

    /**
     * 对外提提供是否暂停状态
     */
    public boolean isPauseState() {
        return CustomMediaPlayer.Status.PAUSED == getStatus();
    }

    //插放完毕事件处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioCompleteEvent(
            AudioCompleteEvent event) {
        next();
    }

    //播放出错事件处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioErrorEvent(AudioErrorEvent event) {
        next();
    }


    public static AudioController getInstance() {
        return SingletonHolder.instance;
    }
    private static class SingletonHolder {
        private static AudioController instance = new AudioController();
    }

}

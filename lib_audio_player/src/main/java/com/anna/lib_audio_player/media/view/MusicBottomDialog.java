package com.anna.lib_audio_player.media.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anna.lib_audio_player.R;
import com.anna.lib_audio_player.media.adapter.MusicListAdapter;
import com.anna.lib_audio_player.media.core.AudioController;
import com.anna.lib_audio_player.media.event.AudioLoadEvent;
import com.anna.lib_audio_player.media.event.AudioPlayModeEvent;
import com.anna.lib_audio_player.media.model.AudioBean;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 底部弹出歌曲列表：
 * 1.功能：
 *  循环模式：
 *  收藏功能
 *  删除列表功能
 *  歌曲列表
 */
public class MusicBottomDialog extends BottomSheetDialog {
    public MusicBottomDialog(@NonNull Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bottom_sheet);
        EventBus.getDefault().register(this);
        initAction();
        initView();
    }

    //当前歌曲列表
    private List<AudioBean>  audioBeans;
    //当前播放歌曲
    private AudioBean mCurBean;
    //当前播放模式
    private AudioController.PlayMode mCurPlayModel;
    /**
     * 获取音乐列表和当前播放音乐以及当前循环模式
     * 使用AudioController获取
     */
    private void initAction() {
        audioBeans = AudioController.getInstance().getmQueue();
        mCurBean = AudioController.getInstance().getNowPlaying();
        mCurPlayModel = AudioController.getInstance().getmPlayMode();
    }

    private ImageView mode_image_view;
    private TextView mode_text_view;
    private ImageView tip_view;
    private RecyclerView recycler;

    /**
     * 根据initAction中获取的数据进行填充数据
     */
    private void initView() {
        mode_image_view = findViewById(R.id.mode_image_view);
        mode_text_view = findViewById(R.id.mode_text_view);
        tip_view = findViewById(R.id.tip_view);

        recycler = findViewById(R.id.recycler);

        //1.根据playModel设置对应的图标和显示对应的模式说明
        setModeImageView();
        //2.设置点击收藏功能的事件
        setFavoriteView();
        //3.显示对应的歌曲列表
        showMusicList();
    }


    /** 3.显示对应的歌曲列表
     * 这里需要创建一个Adapter，来显示歌曲列表
     */

    private void showMusicList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(new MusicListAdapter(audioBeans,mCurBean));
    }

    /**
     * 2.设置点击收藏功能的事件
     */
    private void setFavoriteView() {
        tip_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里可以将部分列表放入到收藏列表中，并显示一个红色的收藏图标
            }
        });
    }

    /**
     * 1.设置播放模式
     */
    private void setModeImageView() {
        showPlayModeView();
        mode_image_view.setOnClickListener(v -> switchPlayModel());
    }

    private void showPlayModeView(){
        switch (mCurPlayModel){
            case LOOP:
                mode_image_view.setImageResource(R.mipmap.loop);
                mode_text_view.setText("列表循环");
                break;
            case REPEAT:
                mode_image_view.setImageResource(R.mipmap.once);
                mode_text_view.setText("单曲循环");
                break;
            case RANDOM:
                mode_image_view.setImageResource(R.mipmap.random);
                mode_text_view.setText("随机播放");
                break;
        }
    }

    /**
     * 切换循环模式
     */
    private void switchPlayModel() {
        switch (mCurPlayModel){
            case LOOP:
                AudioController.getInstance().setmPlayMode(AudioController.PlayMode.REPEAT);
                break;
            case REPEAT:
                AudioController.getInstance().setmPlayMode(AudioController.PlayMode.RANDOM);
                break;
            case RANDOM:
                AudioController.getInstance().setmPlayMode(AudioController.PlayMode.LOOP);
                break;
        }
    }

    /**音乐load事件需要对列表从加载
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event){
        //这里设置当前歌曲为event的歌曲，不然歌曲显示和播放会错乱，切记
        this.mCurBean = event.mAudioBean;
        ((MusicListAdapter)recycler.getAdapter()).updateCurrentAudio(event.mAudioBean);
    }
    /**音乐load事件需要对列表从加载
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPlayModelEvent(AudioPlayModeEvent event){
        mCurPlayModel = event.mPlayMode;
        showPlayModeView();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }
}

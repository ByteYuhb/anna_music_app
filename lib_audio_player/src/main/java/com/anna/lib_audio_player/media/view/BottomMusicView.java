package com.anna.lib_audio_player.media.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.anna.lib_audio_player.R;
import com.anna.lib_audio_player.media.activity.MusicPlayerActivity;
import com.anna.lib_audio_player.media.core.AudioController;
import com.anna.lib_audio_player.media.event.AudioLoadEvent;
import com.anna.lib_audio_player.media.event.AudioPauseEvent;
import com.anna.lib_audio_player.media.event.AudioStartEvent;
import com.anna.lib_audio_player.media.model.AudioBean;
import com.anna.lib_image_loader.glide.CustomImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 一个显示在底部的MediaPlayer
 * 通过获取AudioPlayer回调的事件，对View进行处理
 * 1.start状态显示loading，专辑View选择
 * 2.pause显示pause View，且专辑View不选择
 * 3.点击List的菜单按钮，显示List的Dialog
 */
public class BottomMusicView extends RelativeLayout {
    private Context context;
    //view
    private ImageView album_view;
    private TextView audio_name_view;
    private TextView audio_album_view;
    private ImageView play_view;
    private ImageView show_list_view;

    ObjectAnimator animator;

    //data
    private AudioBean audioBean;

    public BottomMusicView(Context context) {
        this(context,null);
    }

    public BottomMusicView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BottomMusicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        EventBus.getDefault().register(this);
        initView();
        initAction();
    }

    /**
     * 初始化事件
     * 1.专辑View旋转
     * 2.设置play_view点击事件：去启动Media，并根据回调设置对应的View
     * 3.show_list_view点击事件：去显示一个List View的dialog
     */
    private void initAction() {
        //显示旋转
        showAnimation();

        play_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //去启动Media，并根据回调设置对应的View
                AudioController.getInstance().playOrPause();
            }
        });

        show_list_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //去显示一个List View的dialog
                MusicBottomDialog dialog = new MusicBottomDialog(getContext());
                dialog.show();
            }
        });
    }

    //专辑View旋转
    private void showAnimation() {
        animator = ObjectAnimator.ofFloat(album_view,View.ROTATION,0,360);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
        animator.start();

    }

    /**显示load View
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoaded(AudioLoadEvent event){
        this.audioBean = event.mAudioBean;
        showLoadingView();
    }

    /**显示Pause状态
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        showPauseView();
    }

    /**显示
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        showStartView();
    }


    private void showLoadingView() {
        if(audioBean != null){
            audio_name_view.setText(audioBean.name);
            audio_album_view.setText(audioBean.album);
            play_view.setImageResource(R.mipmap.note_btn_pause_white);
            //这里调用我们之前封装的图片加载库lib_image_loader
            CustomImageLoader.getInstance().displayImageForCircleView(album_view,audioBean.albumPic);
        }
    }

    private void showPauseView(){
        if(audioBean != null){
            play_view.setImageResource(R.mipmap.note_btn_play_white);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            animator.pause();
        }
    }

    private void showStartView() {
        if(audioBean != null){
            play_view.setImageResource(R.mipmap.note_btn_pause_white);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            animator.resume();
        }
    }



    /**
     * 初始化View
     */
    private void initView() {
        View rootView = LayoutInflater.from(context).inflate(R.layout.bottom_view,this);
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳到音乐播放Activitity
                MusicPlayerActivity.start((Activity) context);
            }
        });

        album_view = rootView.findViewById(R.id.album_view);
        audio_name_view = rootView.findViewById(R.id.audio_name_view);
        audio_album_view = rootView.findViewById(R.id.audio_album_view);
        play_view = rootView.findViewById(R.id.play_view);
        show_list_view = rootView.findViewById(R.id.show_list_view);
    }

    /**
     * 在view退出逇时候，调用unregister事件
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }
}

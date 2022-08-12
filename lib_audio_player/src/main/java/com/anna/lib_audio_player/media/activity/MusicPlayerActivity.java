package com.anna.lib_audio_player.media.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentActivity;

import com.anna.lib_audio_player.R;
import com.anna.lib_audio_player.media.core.AudioController;
import com.anna.lib_audio_player.media.core.CustomMediaPlayer;
import com.anna.lib_audio_player.media.db.GreenDaoHelper;
import com.anna.lib_audio_player.media.event.AudioFavouriteEvent;
import com.anna.lib_audio_player.media.event.AudioLoadEvent;
import com.anna.lib_audio_player.media.event.AudioPauseEvent;
import com.anna.lib_audio_player.media.event.AudioPlayModeEvent;
import com.anna.lib_audio_player.media.event.AudioProgressEvent;
import com.anna.lib_audio_player.media.event.AudioStartEvent;
import com.anna.lib_audio_player.media.model.AudioBean;
import com.anna.lib_audio_player.media.utils.Utils;
import com.anna.lib_audio_player.media.view.MusicBottomDialog;
import com.anna.lib_image_loader.glide.CustomImageLoader;
//import com.anna.lib_share.ShareDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 显示播放歌曲的界面
 */
public class MusicPlayerActivity extends FragmentActivity {
    private RelativeLayout mBgView;
    private TextView mInfoView;
    private TextView mAuthorView;

    private ImageView mFavouriteView;

    private SeekBar mProgressView;
    private TextView mStartTimeView;
    private TextView mTotalTimeView;

    private ImageView mPlayModeView;
    private ImageView mPlayView;
    private ImageView mNextView;
    private ImageView mPreViousView;

    private Animator animator;
    private AudioBean mAudioBean;
    private AudioController.PlayMode mPlayMode;

    /**对外提供的启动方式
     * @param context
     */
    public static void start(Activity context) {
        Intent intent = new Intent(context, MusicPlayerActivity.class);
        ActivityCompat.startActivity(context, intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(context).toBundle());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加入场动画
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(
                    TransitionInflater.from(this).inflateTransition(R.transition.transition_bottom2top));
        }
        setContentView(R.layout.activity_music_service_layout);
        //设置EventBus监听器
        EventBus.getDefault().register(this);
        initData();
        initView();
    }

    private void initView() {
        mBgView = findViewById(R.id.root_layout);
        CustomImageLoader.getInstance().displayImageForView(mBgView, mAudioBean.albumPic);

        findViewById(R.id.back_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.title_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.share_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMusic(mAudioBean.mUrl, mAudioBean.name);
            }
        });
        findViewById(R.id.show_list_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicBottomDialog dialog = new MusicBottomDialog(MusicPlayerActivity.this);
                dialog.show();
            }
        });
        mInfoView = findViewById(R.id.album_view);
        mInfoView.setText(mAudioBean.albumInfo);
        mInfoView.requestFocus();
        mAuthorView = findViewById(R.id.author_view);
        mAuthorView.setText(mAudioBean.author);

        mFavouriteView = findViewById(R.id.favourite_view);
        mFavouriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //收藏与否
                AudioController.getInstance().changeFavourite();
            }
        });
        changeFavouriteStatus(false);
        mStartTimeView = findViewById(R.id.start_time_view);
        mTotalTimeView = findViewById(R.id.total_time_view);
        mProgressView = findViewById(R.id.progress_view);
        mProgressView.setProgress(0);
        mProgressView.setEnabled(false);

        mPlayModeView = findViewById(R.id.play_mode_view);
        mPlayModeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放模式
                switch (mPlayMode) {
                    case LOOP:
                        AudioController.getInstance().setmPlayMode(AudioController.PlayMode.RANDOM);
                        break;
                    case RANDOM:
                        AudioController.getInstance().setmPlayMode(AudioController.PlayMode.REPEAT);
                        break;
                    case REPEAT:
                        AudioController.getInstance().setmPlayMode(AudioController.PlayMode.LOOP);
                        break;
                }
            }
        });
        updatePlayModeView();
        mPreViousView = findViewById(R.id.previous_view);
        mPreViousView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioController.getInstance().previous();
            }
        });
        mPlayView = findViewById(R.id.play_view);
        mPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioController.getInstance().playOrPause();
            }
        });
        mNextView = findViewById(R.id.next_view);
        mNextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioController.getInstance().next();
            }
        });
    }

    private void initData() {
        mAudioBean = AudioController.getInstance().getNowPlaying();
        mPlayMode = AudioController.getInstance().getmPlayMode();
    }

    //这有两个地方调用：1.刚进入的时候，去获取当前收藏状态 2.状态改变后，根据回调看状态值
    private void updatePlayModeView() {
        switch (mPlayMode) {
            case LOOP:
                mPlayModeView.setImageResource(R.mipmap.player_loop);
                break;
            case RANDOM:
                mPlayModeView.setImageResource(R.mipmap.player_random);
                break;
            case REPEAT:
                mPlayModeView.setImageResource(R.mipmap.player_once);
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //更新notifacation为load状态
        mAudioBean = event.mAudioBean;
        CustomImageLoader.getInstance().displayImageForView(mBgView, mAudioBean.albumPic);
        //可以与初始化时的封装一个方法
        mInfoView.setText(mAudioBean.albumInfo);
        mAuthorView.setText(mAudioBean.author);
        changeFavouriteStatus(false);
        mProgressView.setProgress(0);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioFavouriteEvent(AudioFavouriteEvent event) {
        //更新activity收藏状态
        changeFavouriteStatus(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPlayModeEvent(AudioPlayModeEvent event) {
        mPlayMode = event.mPlayMode;
        //更新播放模式
        updatePlayModeView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioProgessEvent(AudioProgressEvent event) {
        int totalTime = event.maxLength;
        int currentTime = event.progress;
        //更新时间
        mStartTimeView.setText(Utils.formatTime(currentTime));
        mTotalTimeView.setText(Utils.formatTime(totalTime));
        mProgressView.setProgress(currentTime);
        mProgressView.setMax(totalTime);
        if (event.mStatus == CustomMediaPlayer.Status.PAUSED) {
            showPauseView();
        } else {
            showPlayView();
        }
    }

    private void showPlayView() {
        mPlayView.setImageResource(R.mipmap.audio_aj6);
    }

    private void showPauseView() {
        mPlayView.setImageResource(R.mipmap.audio_aj7);
    }

    /**改变收藏状态，且需要设置一个动画效果
     * @param anim
     */
    private void changeFavouriteStatus(boolean anim) {
        if (GreenDaoHelper.selectFavourite(mAudioBean) != null) {
            mFavouriteView.setImageResource(R.mipmap.audio_aeh);
        } else {
            mFavouriteView.setImageResource(R.mipmap.audio_aef);
        }

        if (anim) {
            //留个作业，将动画封到view中作为一个自定义View
            if (animator != null) animator.end();
            PropertyValuesHolder animX =
                    PropertyValuesHolder.ofFloat(View.SCALE_X.getName(), 1.0f, 1.2f, 1.0f);
            PropertyValuesHolder animY =
                    PropertyValuesHolder.ofFloat(View.SCALE_Y.getName(), 1.0f, 1.2f, 1.0f);
            animator = ObjectAnimator.ofPropertyValuesHolder(mFavouriteView, animX, animY);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.setDuration(300);
            animator.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 分享慕课网给好友
     */
    private void shareMusic(String url, String name) {
//        ShareDialog dialog = new ShareDialog(this, false);
//        dialog.setShareType(5);
//        dialog.setShareTitle(name);
//        dialog.setShareTitleUrl(url);
//        dialog.setShareText("慕课网");
//        dialog.setShareSite("imooc");
//        dialog.setShareSiteUrl("http://www.imooc.com");
//        dialog.show();
    }
}

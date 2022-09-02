package com.anna.ft_audio.media.activity;

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

import com.anna.ft_audio.R;
import com.anna.ft_audio.media.core.AudioController;
import com.anna.ft_audio.media.core.CustomMediaPlayer;
import com.anna.ft_audio.media.db.GreenDaoHelper;
import com.anna.ft_audio.media.event.AudioFavouriteEvent;
import com.anna.ft_audio.media.event.AudioLoadEvent;
import com.anna.ft_audio.media.event.AudioPauseEvent;
import com.anna.ft_audio.media.event.AudioPlayModeEvent;
import com.anna.ft_audio.media.event.AudioProgressEvent;
import com.anna.ft_audio.media.event.AudioStartEvent;
import com.anna.ft_audio.media.model.AudioBean;
import com.anna.ft_audio.media.utils.Utils;
import com.anna.ft_audio.media.view.MusicBottomDialog;
import com.anna.lib_base.module.audio.AudioImpl;
import com.anna.lib_base.module.audio.model.CommonAudioBean;
import com.anna.lib_image_loader.glide.CustomImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

//import com.anna.lib_share.ShareDialog;

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

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        setList();
        initData();
        initView();
    }

    public void setList(){
        List<CommonAudioBean> mLists = new ArrayList();
        mLists.add(new CommonAudioBean("100001", "http://sp-sycdn.kuwo.cn/resource/n2/85/58/433900159.mp3",
                "我走后", "小咪", "生而为人", "为梦想打拼的每一个个体都是闪光的,为梦想打拼的每一个个体都是闪光的,为梦想打拼的每一个个体都是闪光的。",
                "http://img0.imgtn.bdimg.com/it/u=2329770966,4069416364&fm=26&gp=0.jpg",
                "4:30"));
        mLists.add(
                new CommonAudioBean("100002", "http://sq-sycdn.kuwo.cn/resource/n1/98/51/3777061809.mp3", "勇气",
                        "梁静茹", "勇气", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "http://b-ssl.duitang.com/uploads/item/201707/01/20170701182157_5eMn2.jpeg",
                        "4:40"));
        mLists.add(
                new CommonAudioBean("100003", "http://sp-sycdn.kuwo.cn/resource/n2/52/80/2933081485.mp3", "灿烂如你",
                        "汪峰", "春天里", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "http://img3.imgtn.bdimg.com/it/u=2136332768,653918923&fm=26&gp=0.jpg",
                        "3:20"));
        mLists.add(
                new CommonAudioBean("100004", "http://sr-sycdn.kuwo.cn/resource/n2/33/25/2629654819.mp3", "小情歌",
                        "五月天", "小幸运", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "http://pic1.win4000.com/mobile/2018-10-08/5bbb0343355a0.jpg",
                        "2:45"));

        AudioImpl.getInstance().setQueue(mLists);
    }

    private void initView() {
        mBgView = findViewById(R.id.root_layout);
        if(mAudioBean!=null)
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
                if(mAudioBean!=null)
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
        if(mAudioBean!=null)
             mInfoView.setText(mAudioBean.albumInfo);
        mInfoView.requestFocus();
        mAuthorView = findViewById(R.id.author_view);
        if(mAudioBean!=null)
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

        if (mAudioBean!=null&&GreenDaoHelper.selectFavourite(mAudioBean) != null) {
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

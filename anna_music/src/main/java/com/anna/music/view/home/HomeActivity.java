package com.anna.music.view.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;


import com.anna.lib_audio_player.api.AudioHelper;
import com.anna.lib_audio_player.media.model.AudioBean;
import com.anna.lib_common_ui.base.BaseActivity;
import com.anna.lib_common_ui.base.constant.Constant;
import com.anna.lib_common_ui.pager_indictor.ScaleTransitionPagerTitleView;
import com.anna.lib_image_loader.glide.CustomImageLoader;
import com.anna.music.R;
import com.anna.music.model.CHANNEL;
import com.anna.music.model.login.LoginEvent;
import com.anna.music.utils.UserManager;
import com.anna.music.view.home.adapter.HomePagerAdapter;
import com.anna.music.view.login.LoginActivity;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 首页Activity
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener {

  private static final CHANNEL[] CHANNELS =
      new CHANNEL[]{CHANNEL.MY, CHANNEL.DISCORY, CHANNEL.FRIEND};

  private UpdateReceiver mReceiver = null;
  /*
   * View
   */
  private DrawerLayout mDrawerLayout;
  private View mToggleView;
  private View mSearchView;
  private ViewPager mViewPager;
  private HomePagerAdapter mAdapter;
  private View mDrawerQrcodeView;
  private View mDrawerShareView;
  private View unLogginLayout;
  private ImageView mPhotoView;

  /*
   * data
   */
  private ArrayList<AudioBean> mLists = new ArrayList<>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    registerBroadcastReceiver();
    EventBus.getDefault().register(this);
    setContentView(R.layout.activity_main);
    initView();
    initData();

  }


  private void initData() {
    mLists.add(new AudioBean("100001", "http://sp-sycdn.kuwo.cn/resource/n2/85/58/433900159.mp3",
        "我走后", "小咪", "生而为人", "为梦想打拼的每一个个体都是闪光的,为梦想打拼的每一个个体都是闪光的,为梦想打拼的每一个个体都是闪光的。",
        "http://img0.imgtn.bdimg.com/it/u=2329770966,4069416364&fm=26&gp=0.jpg",
        "4:30"));
    mLists.add(
        new AudioBean("100002", "http://sq-sycdn.kuwo.cn/resource/n1/98/51/3777061809.mp3", "勇气",
            "梁静茹", "勇气", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
            "http://b-ssl.duitang.com/uploads/item/201707/01/20170701182157_5eMn2.jpeg",
            "4:40"));
    mLists.add(
        new AudioBean("100003", "http://sp-sycdn.kuwo.cn/resource/n2/52/80/2933081485.mp3", "灿烂如你",
            "汪峰", "春天里", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
            "http://img3.imgtn.bdimg.com/it/u=2136332768,653918923&fm=26&gp=0.jpg",
            "3:20"));
    mLists.add(
        new AudioBean("100004", "http://sr-sycdn.kuwo.cn/resource/n2/33/25/2629654819.mp3", "小情歌",
            "五月天", "小幸运", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
            "http://pic1.win4000.com/mobile/2018-10-08/5bbb0343355a0.jpg",
            "2:45"));

    try {
      AudioHelper.provider.setQueue(mLists);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  private void initView() {
    mDrawerLayout = findViewById(R.id.drawer_layout);
    mToggleView = findViewById(R.id.toggle_view);
    mToggleView.setOnClickListener(this);
    mSearchView = findViewById(R.id.search_view);
    mSearchView.setOnClickListener(this);
    //初始化adpater
    mAdapter = new HomePagerAdapter(getSupportFragmentManager(), CHANNELS);
    mViewPager = findViewById(R.id.view_pager);
    mViewPager.setAdapter(mAdapter);
    initMagicIndicator();

    mDrawerQrcodeView = findViewById(R.id.home_qrcode);
    mDrawerQrcodeView.setOnClickListener(this);
    mDrawerShareView = findViewById(R.id.home_music);
    mDrawerShareView.setOnClickListener(this);
    findViewById(R.id.online_music_view).setOnClickListener(this);
    findViewById(R.id.check_update_view).setOnClickListener(this);

    unLogginLayout = findViewById(R.id.unloggin_layout);
    unLogginLayout.setOnClickListener(this);
    mPhotoView = findViewById(R.id.avatr_view);
    findViewById(R.id.exit_layout).setOnClickListener(this);
    DisplayMetrics dm = new DisplayMetrics();
    WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
    wm.getDefaultDisplay().getMetrics(dm);
    Log.d("tag1112:","width:"+dm.widthPixels);
  }

  private void initMagicIndicator() {
    MagicIndicator magicIndicator = findViewById(R.id.magic_indicator);
    magicIndicator.setBackgroundColor(Color.WHITE);
    CommonNavigator commonNavigator = new CommonNavigator(this);
    commonNavigator.setAdjustMode(true);
    commonNavigator.setAdapter(new CommonNavigatorAdapter() {
      @Override
      public int getCount() {
        return CHANNELS == null ? 0 : CHANNELS.length;
      }

      @Override
      public IPagerTitleView getTitleView(Context context, final int index) {
        SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
        simplePagerTitleView.setText(CHANNELS[index].getKey());
        simplePagerTitleView.setTextSize(19);
        simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        simplePagerTitleView.setNormalColor(Color.parseColor("#999999"));
        simplePagerTitleView.setSelectedColor(Color.parseColor("#333333"));
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mViewPager.setCurrentItem(index);
          }
        });
        return simplePagerTitleView;
      }

      @Override
      public IPagerIndicator getIndicator(Context context) {
        return null;
      }

      @Override
      public float getTitleWeight(Context context, int index) {
        return 1.0f;
      }
    });
    magicIndicator.setNavigator(commonNavigator);
    ViewPagerHelper.bind(magicIndicator, mViewPager);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.exit_layout:
        finish();
        System.exit(0);
        break;
      case R.id.unloggin_layout:
        if (!UserManager.getInstance().hasLogined()) {
          LoginActivity.start(this);
        } else {
          mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
        break;
      case R.id.toggle_view:
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
          mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
          mDrawerLayout.openDrawer(Gravity.LEFT);
        }
        break;
      case R.id.home_qrcode:
        if (hasPermission(Constant.HARDWEAR_CAMERA_PERMISSION)) {
          doCameraPermission();
        } else {
          requestPermission(Constant.HARDWEAR_CAMERA_CODE, Constant.HARDWEAR_CAMERA_PERMISSION);
        }
        break;
      case R.id.home_music:
        //shareFriend();
        goToMusic();
        break;
      case R.id.online_music_view:
        //跳到指定webactivity
        gotoWebView("https://www.imooc.com");
        break;
      case R.id.check_update_view:
        checkUpdate();
        break;
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      //退出不销毁task中activity
      moveTaskToBack(true);
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
    unRegisterBroadcastReceiver();
  }

  @Override
  public void doCameraPermission() {
//    ARouter.getInstance().build(Constant.Router.ROUTER_CAPTURE_ACTIVIYT).navigation();
  }

  private void goToMusic() {
//    ARouter.getInstance().build(Constant.Router.ROUTER_MUSIC_ACTIVIYT).navigation();
  }

  private void gotoWebView(String url) {
//    ARouter.getInstance()
//        .build(Constant.Router.ROUTER_WEB_ACTIVIYT)
//        .withString("url", url)
//        .navigation();
  }

  //启动检查更新
  private void checkUpdate() {
//    UpdateHelper.checkUpdate(this);
  }

  private void registerBroadcastReceiver() {
//    if (mReceiver == null) {
//      mReceiver = new UpdateReceiver();
//      LocalBroadcastManager.getInstance(this)
//          .registerReceiver(mReceiver, new IntentFilter(UpdateHelper.UPDATE_ACTION));
//    }
  }

  private void unRegisterBroadcastReceiver() {
    if (mReceiver != null) {
      LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }
  }

  /**
   * 接收Update发送的广播
   */
  public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      //启动安装页面
//      context.startActivity(
//          Utils.getInstallApkIntent(context, intent.getStringExtra(UpdateHelper.UPDATE_FILE_KEY)));
    }
  }

  /**
   * 处理登陆事件
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onLoginEvent(LoginEvent event) {
    unLogginLayout.setVisibility(View.GONE);
    mPhotoView.setVisibility(View.VISIBLE);
    CustomImageLoader.getInstance()
        .displayImageForCircleView(mPhotoView, UserManager.getInstance().getUser().data.photoUrl);
  }
}
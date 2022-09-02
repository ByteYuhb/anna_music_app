package com.anna.ft_loading;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anna.ft_audio.utils.NotificationSetUtil;
import com.anna.lib_base.module.home.HomeImpl;
import com.anna.lib_common_ui.base.BaseActivity;
import com.anna.lib_common_ui.base.constant.Constant;
import com.anna.lib_keepalive.service.KeepAliveService;


public class SplashActivity extends BaseActivity {
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            HomeImpl.getInstance().startHomeActivity(SplashActivity.this);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_layout);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //音乐类直接在音乐服务中运行，这里可以使用一个JobService来保活
            KeepAliveService.start(this, KeepAliveService.AliveStrategy.JOB_SERVICE);
        }
        if(hasPermission(Constant.WRITE_READ_EXTERNAL_PERMISSION)){
            doSDCardPermission();
        }
        else {
            requestPermission(Constant.WRITE_READ_EXTERNAL_CODE,Constant.WRITE_READ_EXTERNAL_PERMISSION);
        }
        NotificationSetUtil.OpenNotificationSetting(this, () -> {

        });
    }

    @Override
    public void doSDCardPermission() {
        handler.sendEmptyMessageDelayed(0,1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}

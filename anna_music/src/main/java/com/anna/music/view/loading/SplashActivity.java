package com.anna.music.view.loading;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anna.lib_common_ui.base.BaseActivity;
import com.anna.lib_common_ui.base.constant.Constant;
import com.anna.lib_keepalive.service.KeepAliveService;
import com.anna.music.R;
import com.anna.music.view.home.HomeActivity;

public class SplashActivity extends BaseActivity {
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            Intent it = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(it);
            finish();
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
        }else {
            requestPermission(Constant.WRITE_READ_EXTERNAL_CODE,Constant.WRITE_READ_EXTERNAL_PERMISSION);
        }
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

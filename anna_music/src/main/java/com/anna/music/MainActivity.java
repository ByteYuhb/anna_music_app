package com.anna.music;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.anna.lib_audio_player.AudioProvider;
import com.anna.lib_audio_player.api.AudioHelper;
import com.anna.lib_audio_player.media.model.AudioBean;
import com.anna.lib_image_loader.listener.BitmapRequestListener;
import com.anna.lib_image_loader.glide.CustomImageLoader;
import com.anna.lib_keepalive.service.KeepAliveService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = findViewById(R.id.id_iv_1);
        findViewById(R.id.id_btn1).setOnClickListener(this);
        findViewById(R.id.id_btn2).setOnClickListener(this);
        findViewById(R.id.id_btn3).setOnClickListener(this);
        findViewById(R.id.id_btn4).setOnClickListener(this);
        findViewById(R.id.id_btn5).setOnClickListener(this);
        findViewById(R.id.id_btn6).setOnClickListener(this);
        checkPermissions();
    }
    public void checkPermissions(){
        if (hasPermission(Constant.WRITE_READ_EXTERNAL_PERMISSION)) {
            doSDCardPermission();
        } else {
            requestPermission(Constant.WRITE_READ_EXTERNAL_CODE, Constant.WRITE_READ_EXTERNAL_PERMISSION);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
//        KeepAliveService.start(this, KeepAliveService.AliveStrategy.ALL);
    }


    @Override
    public void onClick(View v) {
        String url = "";
        switch (v.getId()){
            case R.id.id_btn1:

                break;
            case R.id.id_btn2:
                try {
                    AudioHelper.provider.setQueue(getAudioList());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.id_btn3:
                try {
                    AudioHelper.provider.addAudio(new AudioBean("100005", "http://sp-sycdn.kuwo.cn/resource/n2/85/58/433900159.mp3",
                            "我走后11", "小咪11", "生而为人11", "为梦想打拼的每一个个体都是闪光的,为梦想打拼的每一个个体都是闪光的,为梦想打拼的每一个个体都是闪光的。",
                            "http://img0.imgtn.bdimg.com/it/u=2329770966,4069416364&fm=26&gp=0.jpg",
                            "4:30"));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.id_btn4:
                try {
                    AudioHelper.provider.play();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.id_btn5:
                try {
                    AudioHelper.provider.playNext();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.id_btn6:
                try {
                    AudioHelper.provider.playPre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
package com.anna.music;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.anna.lib_image_loader.listener.BitmapRequestListener;
import com.anna.lib_image_loader.glide.CustomImageLoader;
import com.anna.lib_keepalive.service.KeepAliveService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
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
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        KeepAliveService.start(this, KeepAliveService.AliveStrategy.ALL);
    }

    @Override
    public void onClick(View v) {
        String url = "";
        switch (v.getId()){
            case R.id.id_btn1:
                url = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.jj20.com%2Fup%2Fallimg%2Ftp09%2F210F2130512J47-0-lp.jpg&refer=http%3A%2F%2Fimg.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660360420&t=bcb7bcfcf6b50dee94c16f87b808b29d";
                CustomImageLoader.getInstance().displayImageForCircleView(iv,url);
                break;
            case R.id.id_btn2:
                url = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.jj20.com%2Fup%2Fallimg%2F4k%2Fs%2F02%2F2109242306111155-0-lp.jpg&refer=http%3A%2F%2Fimg.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660360420&t=67a990ce4d018f3d173ec62bec64a3f9";
                CustomImageLoader.getInstance().displayImageForCallBack(MainActivity.this, url, new BitmapRequestListener() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        Log.d("Main","bitmap:"+bitmap);
                    }

                    @Override
                    public void onFail(String errorMsg) {

                    }
                });
                break;
            case R.id.id_btn3:
                url = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.jj20.com%2Fup%2Fallimg%2F1113%2F052420110515%2F200524110515-2-1200.jpg&refer=http%3A%2F%2Fimg.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660360420&t=c61f3cf3ac994280cb0a2cd895475616";
                CustomImageLoader.getInstance().displayImageForIView(iv,url);
                break;
            case R.id.id_btn4:
                url = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.jj20.com%2Fup%2Fallimg%2Ftp02%2F1Z9191923035R0-0-lp.jpg&refer=http%3A%2F%2Fimg.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660360420&t=bcbf1c66e19de5c4a2d0cdf982d2f4b1";
                CustomImageLoader.getInstance().displayImageForView(iv,url);
                break;
            case R.id.id_btn5:
                url = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.jj20.com%2Fup%2Fallimg%2Ftp05%2F19100220062L3N-0-lp.jpg&refer=http%3A%2F%2Fimg.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660360420&t=fe7d04821e0be23f2b6f9af456b9a410";
//                CustomImageLoader.getInstance().displayImageForNotification(MainActivity.this);
                break;

        }
    }
}
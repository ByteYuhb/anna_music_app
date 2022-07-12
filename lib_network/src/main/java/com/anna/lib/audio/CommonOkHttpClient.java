package com.anna.lib.audio;

import com.anna.lib.audio.cookie.SimpleCookieJar;
import com.anna.lib.audio.https.HttpsUtils;
import com.anna.lib.audio.listener.DisposeDataHandle;
import com.anna.lib.audio.response.CommonFileCallBack;
import com.anna.lib.audio.response.CommonJsonCallback;

import java.io.IOException;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommonOkHttpClient {
    private static final int TIME_OUT = 30;
    private static OkHttpClient mOkHttpClient;
    static {
        OkHttpClient.Builder mOkHttpClientBuilder = new OkHttpClient.Builder();
        mOkHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        //添加自定义的拦截器
        mOkHttpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("User-Agent","anna-movie")
                        .build();
                return chain.proceed(request);
            }
        });
        mOkHttpClientBuilder.cookieJar(new SimpleCookieJar());
        mOkHttpClientBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        mOkHttpClientBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        mOkHttpClientBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
        //设置是否支持重定向
        mOkHttpClientBuilder.followRedirects(true);
        //设置代理
       // mOkHttpClientBuilder.proxy()
        mOkHttpClientBuilder.sslSocketFactory(HttpsUtils.initSSLSocketFactory(),
                HttpsUtils.initTrustManager());
        mOkHttpClient = mOkHttpClientBuilder.build();
    }
    public static OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 通过构造好的Request,Callback去发送请求
     */
    public static Call get(Request request, DisposeDataHandle handle) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(handle));
        return call;
    }
    public static Call post(Request request, DisposeDataHandle handle) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(handle));
        return call;
    }

    public static Call downloadFile(Request request, DisposeDataHandle handle) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonFileCallBack(handle));
        return call;
    }
}

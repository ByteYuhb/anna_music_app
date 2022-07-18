package com.anna.lib_image_loader.listener;

import android.graphics.Bitmap;

import com.bumptech.glide.request.RequestListener;

/**
 * 外部回调使用
 */
public interface BitmapRequestListener{
    void onSuccess(Bitmap bitmap);
    void onFail(String errorMsg);
}

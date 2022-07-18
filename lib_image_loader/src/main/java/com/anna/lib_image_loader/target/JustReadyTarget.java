package com.anna.lib_image_loader.target;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anna.lib_image_loader.listener.BitmapRequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class JustReadyTarget extends CustomTarget<Bitmap> {
    private BitmapRequestListener listener;
    Handler handler = new Handler(Looper.getMainLooper());
    public JustReadyTarget(BitmapRequestListener listener){
        this.listener = listener;
    }
    @Override
    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition transition) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onSuccess(resource);
            }
        });
    }

    @Override
    public void onLoadCleared(@Nullable Drawable placeholder) {

    }
}

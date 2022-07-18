package com.anna.lib_image_loader.glide;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.anna.lib_image_loader.R;
import com.anna.lib_image_loader.listener.BitmapRequestListener;
import com.anna.lib_image_loader.listener.CustomRequestListener;
import com.anna.lib_image_loader.target.JustReadyTarget;
import com.anna.lib_image_loader.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.bitmap.Rotate;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.transition.Transition;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CustomImageLoader {
    private CustomImageLoader(){

    }
    public static CustomImageLoader getInstance(){
        return SingletonHolder.instance;
    }
    private static final class SingletonHolder{
        private static CustomImageLoader  instance = new CustomImageLoader();
    }

    /**创建一个设置ImageView src的接口
     * @param imageView
     * @param url
     */
    public void displayImageForIView(ImageView imageView, String url, CustomRequestListener listener){
        Glide.with(imageView.getContext())
                .load(url)
                .apply(initCommonOptions())
                .into(imageView);
    }

    /**不带回调
     * @param imageView
     * @param url
     */
    public void displayImageForIView(ImageView imageView,String url){
        displayImageForIView(imageView,url,null);
    }


    /**创建一个设置View背景的接口
     * @param view
     * @param url
     * @param listener
     */
    public void displayImageForView(final View view, String url,CustomRequestListener listener){
        Glide.with(view.getContext())
                .asBitmap()
                .load(url)
                .listener(listener)
                .apply(initCommonOptions())
                .into(initCustomViewTarget(view));
    }
    /**创建一个设置View背景的接口,不带回调
     * @param view
     * @param url
     * @param
     */
    public void displayImageForView(final View view, String url){
       displayImageForView(view,url,null);
    }

    /**创建一个给通知栏RemoteViews设置src的接口
     * @param context
     * @param remoteViews
     * @param id
     * @param notification
     * @param notificationId
     * @param url
     */
    public void displayImageForNotification(Context context, RemoteViews remoteViews, int id,
                                            Notification notification,int notificationId,String url){
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(initCommonOptions())
                .into(new NotificationTarget(context,id,remoteViews,notification,notificationId));

    }

    /**给ImageVIew设置一个圆形的view
     * @param imageView
     * @param url
     */
    public void displayImageForCircleView(ImageView imageView,String url){
        Glide.with(imageView)
                .asBitmap()
                .load(url)
                .into(new BitmapImageViewTarget(imageView){
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory
                                .create(imageView.getResources(),resource);
                        drawable.setCircular(true);
                        imageView.setImageDrawable(drawable);
                    }
                });
    }


    /**回调一个Bitmap给上层
     */
    public void displayImageForCallBack(Context context, String url, BitmapRequestListener listener){
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(new JustReadyTarget(listener));

    }

    private BaseRequestOptions initCommonOptions(){
        return  new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .downsample(DownsampleStrategy.AT_LEAST)
                .error(R.mipmap.b4y)
                .placeholder(R.mipmap.b4y)
                .skipMemoryCache(false)
                .transform(new Rotate(90))
                .priority(Priority.NORMAL);
    }

    /**初始化一个CustomViewTarget
     * @param vg ViewGroup类型的对象
     * @param <T> input:继承ViewGroup
     * @return
     */
    private <T extends View> CustomViewTarget initCustomViewTarget(T vg){
        return new CustomViewTarget<T, Bitmap>(vg) {
            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {
                view.setBackground(placeholder);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                view.setBackground(errorDrawable);
            }
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                handleTargetByBitmap(view,resource);
            }

        };
    }

    /**处理View为Target的图片加载请求，设置View背景图片
     * 这里采用了RxJava的链式调用，将Bitmap转换为Drawable的响应，并在主线程中设置View的背景色
     * @param group
     * @param resource
     */
    private void handleTargetByBitmap(final View group, final Bitmap resource){
        Observable.just(resource)
                .map(new Function<Bitmap, Drawable>() {
                    @Override
                    public Drawable apply(Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(group.getResources(),
                                Utils.doBlur(resource, 100, true)
                        );
                        return drawable;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Drawable>() {
                    @Override
                    public void accept(Drawable drawable) throws Exception {
                        group.setBackground(drawable);
                    }
                });
    }



}

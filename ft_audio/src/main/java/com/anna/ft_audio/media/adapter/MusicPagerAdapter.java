package com.anna.ft_audio.media.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.anna.ft_audio.R;
import com.anna.ft_audio.media.core.AudioController;
import com.anna.ft_audio.media.model.AudioBean;
import com.anna.lib_image_loader.glide.CustomImageLoader;

import java.util.ArrayList;

public class MusicPagerAdapter extends PagerAdapter {
    private Context mContext;
    /*
     * data
     */
    private ArrayList<AudioBean> mAudioBeans;
    private SparseArray<ObjectAnimator> mAnims = new SparseArray<>();
    private Callback mCallback;
    public MusicPagerAdapter(ArrayList<AudioBean> audioBeans, Context context, Callback callback) {
        mAudioBeans = audioBeans;
        mContext = context;
        mCallback = callback;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.indictor_item_view,container,false);
        ImageView circle_view = rootView.findViewById(R.id.circle_view);
        container.addView(rootView);
        CustomImageLoader.getInstance().displayImageForCircleView(circle_view,
                mAudioBeans.get(position).albumPic);
        mAnims.put(position,createAnim(rootView));
        return rootView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return mAudioBeans==null?0:mAudioBeans.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**创建一个旋转动画
     * @param view
     * @return
     */
    private ObjectAnimator createAnim(View view) {
        view.setRotation(0);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ROTATION.getName(), 0, 360);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
        if (AudioController.getInstance().isStartState()) {
            animator.start();
        }
        return animator;
    }

    /**根据pos获取选择动画，外部可能需要停止他
     * @param pos
     * @return
     */
    public ObjectAnimator getAnim(int pos) {
        return mAnims.get(pos);
    }

    /**
     * 与IndictorView回调,暂时没用到
     */
    public interface Callback {
        void onPlayStatus();

        void onPauseStatus();
    }
}

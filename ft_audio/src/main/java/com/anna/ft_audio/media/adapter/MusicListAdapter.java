package com.anna.ft_audio.media.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.anna.ft_audio.R;
import com.anna.ft_audio.media.core.AudioController;
import com.anna.ft_audio.media.model.AudioBean;

import java.util.List;

/**
 * 用于显示歌曲列表
 */
public class MusicListAdapter  extends RecyclerView.Adapter<MusicListAdapter.AudioViewHolder> {
    private List<AudioBean> audioBeanList;
    private AudioBean mCurrentBean;
    public MusicListAdapter(List<AudioBean> audioBeanList,AudioBean mCurrentBean){
        this.audioBeanList = audioBeanList;
        this.mCurrentBean = mCurrentBean;
    }
    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_bottom_sheet_item,parent,false);
        return new AudioViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        AudioBean bean = audioBeanList.get(position);
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioController.getInstance().addAudio(bean);
            }
        });
        holder.author.setText(new StringBuilder().append(" ").append("-").append(" ").append(bean.author));
        holder.name.setText(bean.name);
        //对播放的歌曲特殊显示
        if (bean.id.equals(mCurrentBean.id)) {
            //为播放中歌曲，红色提醒
            holder.tip.setVisibility(View.VISIBLE);
            holder.name.setTextColor(
                    holder.itemView.getResources().getColor(android.R.color.holo_red_light));
            holder.author.setTextColor(
                    holder.itemView.getResources().getColor(android.R.color.holo_red_light));
        } else {
            holder.tip.setVisibility(View.GONE);
            holder.name.setTextColor(Color.parseColor("#333333"));
            holder.author.setTextColor(Color.parseColor("#333333"));
        }
    }

    @Override
    public int getItemCount() {
        return audioBeanList==null?0:audioBeanList.size();
    }

    /**根据新的mCurrentBean更新UI
     * @param bean
     */
    public void updateCurrentAudio(AudioBean bean){
        this.mCurrentBean = bean;
        notifyDataSetChanged();
    }

    /**根据新的菜单列表
     * @param
     */
    public void updateCurrentAudio(List<AudioBean> listBeans){
        this.audioBeanList = listBeans;
        notifyDataSetChanged();
    }


    public class AudioViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout mLayout;
        private TextView name;
        private TextView author;
        private ImageView tip;
        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = itemView.findViewById(R.id.item_layout);
            tip = itemView.findViewById(R.id.tip_view);
            name = itemView.findViewById(R.id.item_name);
            author = itemView.findViewById(R.id.item_author);
        }

    }

}

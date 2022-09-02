package com.anna.ft_audio.media.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class AudioBean implements Parcelable {

    @Id
    public String id;
    //地址
    @NotNull
    @Unique
    public String mUrl;

    //歌名
    @NotNull public String name;

    //作者
    @NotNull public String author;

    //所属专辑
    @NotNull public String album;

    @NotNull public String albumInfo;

    //专辑封面
    @NotNull public String albumPic;

    //时长
    @NotNull public String totalTime;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMUrl() {
        return this.mUrl;
    }

    public void setMUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumPic() {
        return this.albumPic;
    }

    public void setAlbumPic(String albumPic) {
        this.albumPic = albumPic;
    }

    public String getAlbumInfo() {
        return this.albumInfo;
    }

    public void setAlbumInfo(String albumInfo) {
        this.albumInfo = albumInfo;
    }

    public String getTotalTime() {
        return this.totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof AudioBean)) {
            return false;
        }
        return ((AudioBean) other).id.equals(this.id);
    }
    protected AudioBean(Parcel in) {
    }

    @Generated(hash = 1701787808)
    public AudioBean(String id, @NotNull String mUrl, @NotNull String name,
                     @NotNull String author, @NotNull String album, @NotNull String albumInfo,
                     @NotNull String albumPic, @NotNull String totalTime) {
        this.id = id;
        this.mUrl = mUrl;
        this.name = name;
        this.author = author;
        this.album = album;
        this.albumInfo = albumInfo;
        this.albumPic = albumPic;
        this.totalTime = totalTime;
    }

    @Generated(hash = 1628963493)
    public AudioBean() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(mUrl);
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(album);
        dest.writeString(albumInfo);
        dest.writeString(albumPic);
        dest.writeString(totalTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    /**
     *         dest.writeString(id);
     *         dest.writeString(mUrl);
     *         dest.writeString(name);
     *         dest.writeString(author);
     *         dest.writeString(album);
     *         dest.writeString(albumInfo);
     *         dest.writeString(albumPic);
     *         dest.writeString(totalTime);
     * */
    public static final Creator<AudioBean> CREATOR = new Creator<AudioBean>() {
        @Override
        public AudioBean createFromParcel(Parcel in) {
            AudioBean proxy = new AudioBean(in);
            proxy.setId(in.readString());
            proxy.setMUrl(in.readString());
            proxy.setName(in.readString());
            proxy.setAuthor(in.readString());
            proxy.setAlbum(in.readString());
            proxy.setAlbumInfo(in.readString());
            proxy.setAlbumPic(in.readString());
            proxy.setTotalTime(in.readString());
            return proxy;
        }

        @Override
        public AudioBean[] newArray(int size) {
            return new AudioBean[size];
        }
    };
}

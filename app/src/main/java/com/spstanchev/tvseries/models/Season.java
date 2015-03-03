package com.spstanchev.tvseries.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Stefan on 2/23/2015.
 */
public class Season implements Parcelable {
    private int showId;
    private String showName;
    private int season;
    private ArrayList<Episode> episodes;
    public boolean watchedAll;

    public Season() {
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }

    public boolean isWatchedAll() {
        return watchedAll;
    }
    
    public void setWatchedAll(boolean watchedAll) {
        this.watchedAll = watchedAll;
    }

    protected Season(Parcel in) {
        showId = in.readInt();
        showName = in.readString();
        season = in.readInt();
        episodes = new ArrayList<>();
        in.readTypedList(episodes, Episode.CREATOR);
        watchedAll = (in.readInt() != 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(showId);
        dest.writeString(showName);
        dest.writeInt(season);
        dest.writeTypedList(episodes);
        dest.writeInt(watchedAll ? 1 : 0);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Season> CREATOR = new Parcelable.Creator<Season>() {
        @Override
        public Season createFromParcel(Parcel in) {
            return new Season(in);
        }

        @Override
        public Season[] newArray(int size) {
            return new Season[size];
        }
    };
}

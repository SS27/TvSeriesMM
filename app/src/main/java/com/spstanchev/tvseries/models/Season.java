package com.spstanchev.tvseries.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stefan on 2/23/2015.
 */
public class Season implements Parcelable {
    private int season;
    public boolean watchedAll;

    public Season() {
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public boolean isWatchedAll() {
        return watchedAll;
    }
    
    public void setWatchedAll(boolean watchedAll) {
        this.watchedAll = watchedAll;
    }

    protected Season(Parcel in) {
        season = in.readInt();
        watchedAll = (in.readInt() != 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(season);
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

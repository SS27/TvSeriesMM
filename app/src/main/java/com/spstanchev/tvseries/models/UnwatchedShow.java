package com.spstanchev.tvseries.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stefan on 2/24/2015.
 */
public class UnwatchedShow implements Parcelable {
    private Show show;
    private int seasons;
    private int episodes;
    private int unwatchedEpisodes;

    public UnwatchedShow() {
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public int getSeasons() {
        return seasons;
    }

    public void setSeasons(int seasons) {
        this.seasons = seasons;
    }

    public int getEpisodes() {
        return episodes;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }

    public int getUnwatchedEpisodes() {
        return unwatchedEpisodes;
    }

    public void setUnwatchedEpisodes(int unwatchedEpisodes) {
        this.unwatchedEpisodes = unwatchedEpisodes;
    }

    protected UnwatchedShow(Parcel in) {
        show = (Show) in.readValue(Show.class.getClassLoader());
        seasons = in.readInt();
        episodes = in.readInt();
        unwatchedEpisodes = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(show);
        dest.writeInt(seasons);
        dest.writeInt(episodes);
        dest.writeInt(unwatchedEpisodes);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UnwatchedShow> CREATOR = new Parcelable.Creator<UnwatchedShow>() {
        @Override
        public UnwatchedShow createFromParcel(Parcel in) {
            return new UnwatchedShow(in);
        }

        @Override
        public UnwatchedShow[] newArray(int size) {
            return new UnwatchedShow[size];
        }
    };
}

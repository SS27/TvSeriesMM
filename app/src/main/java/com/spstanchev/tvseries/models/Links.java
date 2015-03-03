package com.spstanchev.tvseries.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stefan on 2/8/2015.
 */
public class Links implements Parcelable {

    private String self;
    private String episodes;
    private String cast;
    private String previousepisode;
    private String nextepisode;

    public Links() {
    }

    /**
     * @return The self
     */
    public String getSelf() {
        return self;
    }

    /**
     * @param self The self
     */
    public void setSelf(String self) {
        this.self = self;
    }

    /**
     * @return The episodes
     */
    public String getEpisodes() {
        return episodes;
    }

    /**
     * @param episodes The episodes
     */
    public void setEpisodes(String episodes) {
        this.episodes = episodes;
    }

    /**
     * @return The cast
     */
    public String getCast() {
        return cast;
    }

    /**
     * @param cast The cast
     */
    public void setCast(String cast) {
        this.cast = cast;
    }

    /**
     * @return The previousepisode
     */
    public String getPreviousepisode() {
        return previousepisode;
    }

    /**
     * @param previousepisode The previousepisode
     */
    public void setPreviousepisode(String previousepisode) {
        this.previousepisode = previousepisode;
    }

    /**
     * @return The nextepisode
     */
    public String getNextepisode() {
        return nextepisode;
    }

    /**
     * @param nextepisode The nextepisode
     */
    public void setNextepisode(String nextepisode) {
        this.nextepisode = nextepisode;
    }


    protected Links(Parcel in) {
        self = in.readString();
        episodes = in.readString();
        cast = in.readString();
        previousepisode = in.readString();
        nextepisode = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(self);
        dest.writeString(episodes);
        dest.writeString(cast);
        dest.writeString(previousepisode);
        dest.writeString(nextepisode);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Links> CREATOR = new Parcelable.Creator<Links>() {
        @Override
        public Links createFromParcel(Parcel in) {
            return new Links(in);
        }

        @Override
        public Links[] newArray(int size) {
            return new Links[size];
        }
    };
}

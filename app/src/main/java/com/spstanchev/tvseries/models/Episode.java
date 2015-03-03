package com.spstanchev.tvseries.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stefan on 2/8/2015.
 */
public class Episode implements Parcelable {
    private int id;
    private String url;
    private String name;
    private int season;
    private int number;
    private String airdate;
    private String airtime;
    private String airstamp;
    private int runtime;
    private Image image;
    private String summary;
    private int showId;
    private boolean isWatched;

    public Episode() {
    }

    /**
     * @return The id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The season
     */
    public int getSeason() {
        return season;
    }

    /**
     * @param season The season
     */
    public void setSeason(int season) {
        this.season = season;
    }

    /**
     * @return The number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @param number The number
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * @return The airdate
     */
    public String getAirdate() {
        return airdate;
    }

    /**
     * @param airdate The airdate
     */
    public void setAirdate(String airdate) {
        this.airdate = airdate;
    }

    /**
     * @return The airtime
     */
    public String getAirtime() {
        return airtime;
    }

    /**
     * @param airtime The airtime
     */
    public void setAirtime(String airtime) {
        this.airtime = airtime;
    }

    /**
     * @return The airstamp
     */
    public String getAirstamp() {
        return airstamp;
    }

    /**
     * @param airstamp The airstamp
     */
    public void setAirstamp(String airstamp) {
        this.airstamp = airstamp;
    }

    /**
     * @return The runtime
     */
    public int getRuntime() {
        return runtime;
    }

    /**
     * @param runtime The runtime
     */
    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    /**
     * @return The image
     */
    public Image getImage() {
        return image;
    }

    /**
     * @param image The image
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * @return The summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary The summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return The showId
     */
    public int getShowId() {
        return showId;
    }

    /**
     * @param showId The showId
     */
    public void setShowId(int showId) {
        this.showId = showId;
    }

    public boolean isWatched() {
        return isWatched;
    }

    public void setWatched(boolean isWatched) {
        this.isWatched = isWatched;
    }

    protected Episode(Parcel in) {
        id = in.readInt();
        url = in.readString();
        name = in.readString();
        season = in.readInt();
        number = in.readInt();
        airdate = in.readString();
        airtime = in.readString();
        airstamp = in.readString();
        runtime = in.readInt();
        image = (Image) in.readValue(Image.class.getClassLoader());
        summary = in.readString();
        isWatched = (in.readInt() != 0);
        showId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(url);
        dest.writeString(name);
        dest.writeInt(season);
        dest.writeInt(number);
        dest.writeString(airdate);
        dest.writeString(airtime);
        dest.writeString(airstamp);
        dest.writeInt(runtime);
        dest.writeValue(image);
        dest.writeString(summary);
        dest.writeInt(isWatched ? 1 : 0);
        dest.writeInt(showId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Episode> CREATOR = new Parcelable.Creator<Episode>() {
        @Override
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        @Override
        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };

}

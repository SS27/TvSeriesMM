package com.spstanchev.tvseries.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Stefan on 2/8/2015.
 */
public class Show implements Parcelable {

    private int id;
    private String url;
    private String name;
    private String type;
    private String status;
    private int runtime;
    private String premiered;
    private double ratingAverage;
    private Network network;
    private Image image;
    private String summary;
    private Links links;
    private ArrayList<Episode> episodes;

    public Show() {
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
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
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
     * @return The premiered
     */
    public String getPremiered() {
        return premiered;
    }

    /**
     * @param premiered The premiered
     */
    public void setPremiered(String premiered) {
        this.premiered = premiered;
    }

    /**
     * @return The average
     */
    public double getRatingAverage() {
        return ratingAverage;
    }

    /**
     * @param average The average
     */
    public void setRatingAverage(double average) {
        this.ratingAverage = average;
    }

    /**
     * @return The network
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * @param network The network
     */
    public void setNetwork(Network network) {
        this.network = network;
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
     * @return The links
     */
    public com.spstanchev.tvseries.models.Links getLinks() {
        return links;
    }

    /**
     * @param Links The _links
     */
    public void setLinks(com.spstanchev.tvseries.models.Links Links) {
        this.links = Links;
    }

    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }

    protected Show(Parcel in) {
        id = in.readInt();
        url = in.readString();
        name = in.readString();
        type = in.readString();
        status = in.readString();
        runtime = in.readInt();
        premiered = in.readString();
        ratingAverage = in.readDouble();
        network = (Network) in.readValue(Network.class.getClassLoader());
        image = (Image) in.readValue(Image.class.getClassLoader());
        summary = in.readString();
        links = (Links) in.readValue(Links.class.getClassLoader());
        episodes = new ArrayList<>();
        in.readTypedList(episodes, Episode.CREATOR);
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
        dest.writeString(type);
        dest.writeString(status);
        dest.writeInt(runtime);
        dest.writeString(premiered);
        dest.writeDouble(ratingAverage);
        dest.writeValue(network);
        dest.writeValue(image);
        dest.writeString(summary);
        dest.writeValue(links);
        dest.writeTypedList(episodes);
    }

    public static final Parcelable.Creator<Show> CREATOR = new Parcelable.Creator<Show>() {
        @Override
        public Show createFromParcel(Parcel in) {
            return new Show(in);
        }

        @Override
        public Show[] newArray(int size) {
            return new Show[size];
        }
    };
}

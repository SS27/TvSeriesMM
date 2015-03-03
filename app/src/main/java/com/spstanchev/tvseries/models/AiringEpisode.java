package com.spstanchev.tvseries.models;

/**
 * Created by Stefan on 2/26/2015.
 */
public class AiringEpisode {
    private String showName;
    private String networkName;
    private Episode episode;

    public AiringEpisode() {
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public Episode getEpisode() {
        return episode;
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }
}

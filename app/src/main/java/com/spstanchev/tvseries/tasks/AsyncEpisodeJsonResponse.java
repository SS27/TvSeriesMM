package com.spstanchev.tvseries.tasks;

import com.spstanchev.tvseries.models.Episode;

import java.util.ArrayList;

/**
 * Created by Stefan on 2/25/2015.
 */
public interface AsyncEpisodeJsonResponse {
    public void handleEpisodesJsonResponse(ArrayList<Episode> episodes);
}
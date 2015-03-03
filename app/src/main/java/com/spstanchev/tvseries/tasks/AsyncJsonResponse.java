package com.spstanchev.tvseries.tasks;

import com.spstanchev.tvseries.models.Show;

import java.util.ArrayList;

/**
 * Created by Stefan on 2/12/2015.
 */
public interface AsyncJsonResponse {
    public void handleShowJsonResponse(Show show);
    public void handleQueryShowsJsonResponse(ArrayList<Show> shows);
}

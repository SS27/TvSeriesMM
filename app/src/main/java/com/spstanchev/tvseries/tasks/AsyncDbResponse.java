package com.spstanchev.tvseries.tasks;

import com.spstanchev.tvseries.models.Show;

import java.util.ArrayList;

/**
 * Created by Stefan on 2/23/2015.
 */
public interface AsyncDbResponse {
    public void showsFromDbResponse (ArrayList<Show> shows);
}

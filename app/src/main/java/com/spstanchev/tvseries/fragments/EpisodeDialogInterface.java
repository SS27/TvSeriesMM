package com.spstanchev.tvseries.fragments;

import com.spstanchev.tvseries.models.Episode;

/**
 * Created by Stefan on 3/3/2015.
 */
public interface EpisodeDialogInterface {
    public void showDialog(Episode episode, int groupPosition, int childPosition);
    public void doPositiveClick(Episode episode, int groupPosition, int childPosition);
    public void doNegativeClick();
}

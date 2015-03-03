package com.spstanchev.tvseries.fragments;

import com.spstanchev.tvseries.models.AddedShow;

/**
 * Created by Stefan on 3/3/2015.
 */
public interface ShowDialogInterface {
    public void showDialog(AddedShow show, int position);
    public void doPositiveClick(AddedShow show, int position);
    public void doNegativeClick();
}

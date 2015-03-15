package com.spstanchev.tvseries.tasks;

import android.content.ContentResolver;
import android.os.AsyncTask;

import com.spstanchev.tvseries.models.Show;
import com.spstanchev.tvseries.providers.ShowProvider;

import java.util.ArrayList;

/**
 * Created by Stefan on 2/23/2015.
 */
public class AsyncQueryShowsFromDb extends AsyncTask<Boolean, Void, ArrayList<Show>> {
    private AsyncDbResponse asyncDbResponseIf;
    private ContentResolver contentResolver;

    public AsyncQueryShowsFromDb(AsyncDbResponse asyncDbResponseIf, ContentResolver contentResolver) {
        this.asyncDbResponseIf = asyncDbResponseIf;
        this.contentResolver = contentResolver;
    }

    @Override
    protected ArrayList<Show> doInBackground(Boolean... params) {
        return ShowProvider.Helper.getShows(contentResolver, params[0], params[1]);
    }

    @Override
    protected void onPostExecute(ArrayList<Show> shows) {
        asyncDbResponseIf.showsFromDbResponse(shows);
    }
}

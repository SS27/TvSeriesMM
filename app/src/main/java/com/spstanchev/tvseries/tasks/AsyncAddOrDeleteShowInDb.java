package com.spstanchev.tvseries.tasks;

import android.content.ContentResolver;
import android.os.AsyncTask;

import com.spstanchev.tvseries.models.Show;
import com.spstanchev.tvseries.providers.ShowProvider;

/**
 * Created by Stefan on 2/23/2015.
 */
public class AsyncAddOrDeleteShowInDb extends AsyncTask <Show, Void, Boolean> {

    private static final String TAG = AsyncDownloadShow.class.getSimpleName();
    private ContentResolver contentResolver;
    private boolean isDelete = false;

    public AsyncAddOrDeleteShowInDb(ContentResolver contentResolver, boolean isDelete) {
        this.contentResolver = contentResolver;
        this.isDelete = isDelete;
    }

    @Override
    protected Boolean doInBackground(Show... params) {
        boolean successful = false;
        if (!isDelete){
            successful = ShowProvider.Helper.addShow(contentResolver, params[0]);
        }
        else {
            successful = ShowProvider.Helper.deleteShow(contentResolver, params[0].getId());
        }
        return successful;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
    }
}

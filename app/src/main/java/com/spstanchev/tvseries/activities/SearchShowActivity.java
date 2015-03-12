package com.spstanchev.tvseries.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.spstanchev.tvseries.R;
import com.spstanchev.tvseries.adapters.SuggestedShowsAdapter;
import com.spstanchev.tvseries.common.Constants;
import com.spstanchev.tvseries.fragments.ShowDialogInterface;
import com.spstanchev.tvseries.common.Utils;
import com.spstanchev.tvseries.fragments.ShowInfoFragment;
import com.spstanchev.tvseries.models.AddedShow;
import com.spstanchev.tvseries.models.Show;
import com.spstanchev.tvseries.tasks.AsyncAddOrDeleteShowInDb;
import com.spstanchev.tvseries.tasks.AsyncDownloadShow;
import com.spstanchev.tvseries.tasks.AsyncJsonResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchShowActivity extends ActionBarActivity implements AsyncJsonResponse, ShowDialogInterface {

    private static final String TAG = SearchShowActivity.class.getSimpleName();
    private ArrayList<AddedShow> showsList = new ArrayList<>();
    private ArrayList<AddedShow> suggestedShowsList;
    private SuggestedShowsAdapter adapter;
    private ProgressDialog progressSearchShow;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_show);

        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setListView();
        initProgressSearchShow(this);
        if (savedInstanceState == null) {
            if (Utils.isNetworkAvailable(this)) {
                downloadSuggestedShowsList();
            } else {
                Toast.makeText(this, "You need internet access to add new shows!", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            showsList = savedInstanceState.getParcelableArrayList("showsList");
            suggestedShowsList = savedInstanceState.getParcelableArrayList("suggestedShowsList");
            adapter.updateCollection(showsList);
        }

    }

    private void setListView() {
        ListView listView = (ListView) findViewById(R.id.lvAddShow);
        adapter = new SuggestedShowsAdapter(this);
        listView.setAdapter(adapter);
    }

    private void initProgressSearchShow(Context context) {
        progressSearchShow = new ProgressDialog(context);
        progressSearchShow.setTitle(getString(R.string.title_please_wait));
        progressSearchShow.setMessage(getString(R.string.message_searching_for_show));
        progressSearchShow.setIndeterminate(true);
        progressSearchShow.setCancelable(true);
    }

    private void dismissProgressSearchShow() {
        if (progressSearchShow != null && progressSearchShow.isShowing())
            progressSearchShow.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_show, menu);
        setUpSearchView(menu);

        return true;
    }

    private void setUpSearchView(Menu menu) {
        // Associate searchable configuration with the SearchView
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getString(R.string.action_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!s.isEmpty()) {
                    if (Utils.isNetworkAvailable(SearchShowActivity.this)) {
                        if (suggestedShowsList == null)
                            suggestedShowsList = new ArrayList<>(showsList);
                        try {
                            String encoded = URLEncoder.encode(s, "UTF-8");
                            progressSearchShow.show();
                            AsyncDownloadShow taskQueryShows = new AsyncDownloadShow(SearchShowActivity.this);
                            taskQueryShows.execute(Constants.JSON_QUERY_SHOWS_URL + encoded);
                            return true;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(SearchShowActivity.this, "You need internet access to search for new shows!", Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty() && suggestedShowsList != null) {
                    showsList = suggestedShowsList;
                    adapter.updateCollection(showsList);
                    return true;
                } else
                    return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void downloadSuggestedShowsList() {
        for (int i = 0; i < Constants.suggestedShowsIds.length; i++) {
            AsyncDownloadShow taskDownloadSuggestedShow = new AsyncDownloadShow(this);
            taskDownloadSuggestedShow.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.JSON_SHOW_URL + Constants.suggestedShowsIds[i]);
        }
    }

    @Override
    public void handleShowJsonResponse(Show show) {
        AddedShow addedShow = new AddedShow();
        addedShow.setShow(show);
        addedShow.setAdded(sharedPreferences.contains("Show" + show.getId()));
        //add show to the main showsList
        showsList.add(addedShow);
        //update the adapter showsList
        adapter.updateCollection(showsList);
    }

    @Override
    public void handleQueryShowsJsonResponse(ArrayList<Show> shows) {
        dismissProgressSearchShow();
        if (shows.size() == 0){
            Toast.makeText(this, "No shows found!", Toast.LENGTH_LONG).show();
            return;
        }
        showsList.clear();
        for (Show show : shows){
            AddedShow addedShow = new AddedShow();
            addedShow.setShow(show);
            addedShow.setAdded(sharedPreferences.contains("Show" + show.getId()));
            showsList.add(addedShow);
        }
        adapter.updateCollection(showsList);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("showsList", showsList);
        if (suggestedShowsList != null) {
            outState.putParcelableArrayList("suggestedShowsList", suggestedShowsList);
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressSearchShow();
        super.onDestroy();
    }

    @Override
    public void showDialog(AddedShow addedShow, int position) {
        ShowInfoFragment dialog = ShowInfoFragment.newInstance(addedShow, position);
        dialog.show(getSupportFragmentManager(), "Show dialog");
    }

    @Override
    public void doPositiveClick(AddedShow addedShow, int position) {
        AsyncAddOrDeleteShowInDb deleteAsyncTask = new AsyncAddOrDeleteShowInDb(getContentResolver(), addedShow.isAdded());
        deleteAsyncTask.execute(addedShow.getShow());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (addedShow.isAdded()){
            editor.remove("Show" + addedShow.getShow().getId());
            editor.commit();
            Toast.makeText(this, addedShow.getShow().getName() + " removed successfully! ", Toast.LENGTH_SHORT).show();
        }
        else {
            editor.putInt("Show" + addedShow.getShow().getId(), addedShow.getShow().getId());
            editor.commit();
            Toast.makeText(this, addedShow.getShow().getName() + " added successfully! ", Toast.LENGTH_SHORT).show();
        }
        showsList.get(position).setAdded(!addedShow.isAdded());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void doNegativeClick() {

    }
}

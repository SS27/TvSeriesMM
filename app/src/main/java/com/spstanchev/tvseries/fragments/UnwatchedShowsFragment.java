package com.spstanchev.tvseries.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.spstanchev.tvseries.R;
import com.spstanchev.tvseries.activities.EpisodesActivity;
import com.spstanchev.tvseries.adapters.UnwatchedShowsAdapter;
import com.spstanchev.tvseries.common.Constants;
import com.spstanchev.tvseries.common.Utils;
import com.spstanchev.tvseries.models.Episode;
import com.spstanchev.tvseries.models.Show;
import com.spstanchev.tvseries.models.UnwatchedShow;
import com.spstanchev.tvseries.tasks.AsyncDbResponse;
import com.spstanchev.tvseries.tasks.AsyncQueryShowsFromDb;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Stefan on 2/8/2015.
 */
public class UnwatchedShowsFragment extends Fragment implements AdapterView.OnItemClickListener, AsyncDbResponse {

    private static final String TAG = UnwatchedShowsFragment.class.getSimpleName();
    private UnwatchedShowsAdapter adapter;
    private ArrayList<UnwatchedShow> myUnwatchedShows;
    private TextView tvEmpty;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        adapter = new UnwatchedShowsAdapter(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_unwatched_shows, container, false);
        ListView lvMyShows = (ListView) rootView.findViewById(R.id.lvUnwatchedShows);
        lvMyShows.setAdapter(adapter);
        if (savedInstanceState != null){
            myUnwatchedShows = savedInstanceState.getParcelableArrayList("myUnwatchedShows");
            if (myUnwatchedShows != null)
                adapter.updateCollection(myUnwatchedShows);
        }
        lvMyShows.setOnItemClickListener(this);
        tvEmpty = (TextView) rootView.findViewById(R.id.tvEmptyUnwatchedList);
        tvEmpty.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //query the db
        getShowsFromDb();
    }

    private void getShowsFromDb() {
        //query db for current shows
        AsyncQueryShowsFromDb queryAsyncTask = new AsyncQueryShowsFromDb(this, getActivity().getContentResolver());
        queryAsyncTask.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), EpisodesActivity.class);
        intent.putExtra("com.spstanchev.tvseries" + Constants.TAG_SHOW, adapter.getShows().get(position).getShow());
        startActivity(intent);
    }

    @Override
    public void showsFromDbResponse(ArrayList<Show> shows) {
        if (shows != null) {
            populateUnwatchedEpisodeArrayList(shows);
            adapter.updateCollection(myUnwatchedShows);
            if (myUnwatchedShows.size() > 0)
                tvEmpty.setVisibility(View.GONE);
            else
                tvEmpty.setVisibility(View.VISIBLE);
        } else {
            myUnwatchedShows = new ArrayList<>();
            adapter.updateCollection(myUnwatchedShows);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void populateUnwatchedEpisodeArrayList(ArrayList<Show> shows) {
        myUnwatchedShows = new ArrayList<>();
        Date currentDate = new Date();
        for (Show show : shows){
            int unwatchedEpisodes = 0;
            int seasonsNum = 0;
            for (Episode episode : show.getEpisodes()) {
                Date episodeAirdate = Utils.getDateFromString(episode.getAirstamp(), Utils.getJsonAirstampFormat());
                if (!episode.isWatched() && currentDate.after(episodeAirdate))
                    unwatchedEpisodes++;
                seasonsNum = episode.getSeason();
            }
            if (unwatchedEpisodes != 0) {
                UnwatchedShow unwatchedShow = new UnwatchedShow();
                unwatchedShow.setShow(show);
                unwatchedShow.setSeasons(seasonsNum);
                unwatchedShow.setEpisodes(show.getEpisodes().size());
                unwatchedShow.setUnwatchedEpisodes(unwatchedEpisodes);
                myUnwatchedShows.add(unwatchedShow);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_unwatched_shows, menu);
        setUpSearchView(menu);
    }

    private void setUpSearchView(Menu menu) {
        // Associate searchable configuration with the SearchView
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_unwatched_shows).getActionView();
        searchView.setQueryHint(getString(R.string.action_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!s.isEmpty()){
                    adapter.getFilter().filter(s);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (TextUtils.isEmpty(s) && myUnwatchedShows != null) {
                    adapter.updateCollection(myUnwatchedShows);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search_my_shows : {
                return true;
            }
            default :
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("myUnwatchedShows", myUnwatchedShows);
        super.onSaveInstanceState(outState);
    }
}

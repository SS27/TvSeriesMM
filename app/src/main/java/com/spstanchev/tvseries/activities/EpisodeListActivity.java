package com.spstanchev.tvseries.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.spstanchev.tvseries.R;
import com.spstanchev.tvseries.adapters.SeasonsAndEpisodesExpandableAdapter;
import com.spstanchev.tvseries.common.Constants;
import com.spstanchev.tvseries.common.Utils;
import com.spstanchev.tvseries.fragments.EpisodeDialogInterface;
import com.spstanchev.tvseries.fragments.EpisodeInfoFragment;
import com.spstanchev.tvseries.models.Episode;
import com.spstanchev.tvseries.models.Season;
import com.spstanchev.tvseries.models.Show;
import com.spstanchev.tvseries.providers.ShowProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class EpisodeListActivity extends ActionBarActivity implements ExpandableListView.OnChildClickListener, EpisodeDialogInterface {
    private static final String TAG = EpisodeListActivity.class.getSimpleName();
    private ArrayList<Episode> episodes;
    private SeasonsAndEpisodesExpandableAdapter adapter;
    private ArrayList<Season> seasonsList;
    private HashMap<Season, ArrayList<Episode>> listSeasonEpisodes;
    private Show currentShow;
    private CheckBox checkBoxWatchedAll;
    private TextView tvShowTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_list);

        findViews();
        int showId = getIntent().getExtras().getInt("com.spstanchev.tvseries" + Constants.TAG_SHOW_ID, -1);
        if (savedInstanceState == null) {
            getShowAndSetContent(showId);
        } else {
            currentShow = savedInstanceState.getParcelable("currentShow");
            seasonsList = savedInstanceState.getParcelableArrayList("seasonList");
            episodes = savedInstanceState.getParcelableArrayList("episodes");
            updateContent();
        }
    }

    private void findViews() {
        checkBoxWatchedAll = (CheckBox) findViewById(R.id.checkBoxAllP);
        tvShowTitle = (TextView) findViewById(R.id.tvShowTitle);
    }

    private void getShowAndSetContent(int showId) {
        new AsyncTask<Integer, Void, Show>() {
            @Override
            protected Show doInBackground(Integer... params) {
                int showId = params[0];
                if (showId == -1)
                    return null;
                else
                    return ShowProvider.Helper.getShow(getContentResolver(), showId);
            }

            @Override
            protected void onPostExecute(Show show) {
                if (show != null) {
                    currentShow = show;
                    setContent();
                } else {
                    Log.e(TAG, "Something went wrong with getting the show from db!");
                    finish();
                }
            }
        }.execute(showId);
    }

    private void setContent() {
        tvShowTitle.setText(currentShow.getName());
        getAllEpisodes(currentShow);
        getSeasonsList();
        getEpisodesForSeasons();
        setCheckBoxWatchedAll();
        setExpandableListView();
    }

    private void updateContent() {
        tvShowTitle.setText(currentShow.getName());
        getEpisodesForSeasons();
        setCheckBoxWatchedAll();
        setExpandableListView();
    }

    private void getAllEpisodes(Show show) {
        episodes = show.getEpisodes();
    }

    private void getSeasonsList() {
        int seasons = getSeasonNumbers();
        seasonsList = new ArrayList<>();
        for (int i = 1; i <= seasons; i++) {
            Season season = new Season();
            season.setSeason(i);
            seasonsList.add(season);
        }
    }

    private int getSeasonNumbers() {
        int seasons = 1;
        for (int i = 0; i < episodes.size(); i++) {
            if (episodes.get(i).getSeason() > seasons) {
                seasons = episodes.get(i).getSeason();
            }
        }
        return seasons;
    }

    private void getEpisodesForSeasons() {
        listSeasonEpisodes = new HashMap<>();
        for (int i = 0; i < seasonsList.size(); i++) {
            ArrayList<Episode> episodesList = new ArrayList<>();
            for (int j = 0; j < episodes.size(); j++) {
                if (episodes.get(j).getSeason() == i + 1) {
                    Episode ep = episodes.get(j);
                    episodesList.add(ep);
                }
            }
            seasonsList.get(i).setWatchedAll(isSeasonWatched(episodesList));
            listSeasonEpisodes.put(seasonsList.get(i), episodesList);
        }
    }

    private void setCheckBoxWatchedAll() {
        updateCheckBoxWatchedAll();
        checkBoxWatchedAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean watchedAll;
                if (checkBoxWatchedAll.isChecked()) {
                    Toast.makeText(EpisodeListActivity.this, getString(R.string.message_all_episodes_marked_watched), Toast.LENGTH_LONG).show();
                    watchedAll = true;
                } else {
                    Toast.makeText(EpisodeListActivity.this, getString(R.string.message_all_episodes_marked_unwatched), Toast.LENGTH_LONG).show();
                    watchedAll = false;
                }
                for (Episode episode : episodes) {
                    episode.setWatched(watchedAll);
                    //update db
                    new AsyncTask<Episode, Void, Void>() {
                        @Override
                        protected Void doInBackground(Episode... params) {
                            ShowProvider.Helper.updateEpisode(getContentResolver(), params[0]);
                            return null;
                        }
                    }.execute(episode);
                }
                getEpisodesForSeasons();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void updateCheckBoxWatchedAll() {
        boolean watchedAll = true;
        for (int i = seasonsList.size() - 1; i >= 0; i--) {
            if (!seasonsList.get(i).watchedAll) {
                watchedAll = false;
                break;
            }
        }
        checkBoxWatchedAll.setChecked(watchedAll);
    }

    private boolean isSeasonWatched(ArrayList<Episode> episodes) {
        boolean watchedAll = true;
        Date currentDate = new Date();
        for (int i = episodes.size() - 1; i >= 0; i--) {
            Date episodeAirdate = Utils.getDateFromString(episodes.get(i).getAirstamp(), Utils.getJsonAirstampFormat());
            if (!episodes.get(i).isWatched() && currentDate.after(episodeAirdate)) {
                watchedAll = false;
                break;
            }
        }
        return watchedAll;
    }

    private void setExpandableListView() {
        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandableListViewP);
        SeasonsAndEpisodesExpandableAdapter savedAdapter = (SeasonsAndEpisodesExpandableAdapter) getLastCustomNonConfigurationInstance();
        if (savedAdapter != null) {
            adapter = savedAdapter;
        } else {
            adapter = new SeasonsAndEpisodesExpandableAdapter(this, seasonsList, listSeasonEpisodes);
        }
        adapter.setOnWatchedChangeListener(new SeasonsAndEpisodesExpandableAdapter.OnWatchedChangeListener() {
            @Override
            public void onWatchedChanged() {
                updateCheckBoxWatchedAll();
            }
        });
        expandableListView.setAdapter(adapter);
        expandableListView.setOnChildClickListener(this);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Episode episode = listSeasonEpisodes.get(seasonsList.get(groupPosition))
                .get(childPosition);
        showDialog(episode, groupPosition, childPosition);
        return true;
    }

    @Override
    public void showDialog(Episode episode, int groupPosition, int childPosition) {
        EpisodeInfoFragment dialog = EpisodeInfoFragment.newInstance(episode, groupPosition, childPosition);
        dialog.show(getSupportFragmentManager(), "Episode Info Purple");
    }

    @Override
    public void doPositiveClick(final Episode episode, int groupPosition, int childPosition) {
        listSeasonEpisodes.get(seasonsList.get(groupPosition))
                .set(childPosition, episode);
        adapter.notifyDataSetChanged();

        //update db
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ShowProvider.Helper.updateEpisode(getContentResolver(), episode);
                return null;
            }
        }.execute();
    }

    @Override
    public void doNegativeClick() {

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return adapter;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("currentShow", currentShow);
        outState.putParcelableArrayList("seasonList", seasonsList);
        outState.putParcelableArrayList("episodes", episodes);
        super.onSaveInstanceState(outState);
    }
}



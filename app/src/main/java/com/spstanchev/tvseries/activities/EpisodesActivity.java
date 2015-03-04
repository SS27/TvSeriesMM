package com.spstanchev.tvseries.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class EpisodesActivity extends ActionBarActivity implements ExpandableListView.OnChildClickListener, EpisodeDialogInterface {
    private static final String TAG = EpisodesActivity.class.getSimpleName();
    private ArrayList<Episode> episodes, unwatchedEpisodes;
    private SeasonsAndEpisodesExpandableAdapter adapter;
    private ArrayList<Season> seasonsList, unwatchedSeasonsList; // header titles
    // child data in format of header title, child title
    private HashMap<Season, ArrayList<Episode>> listSeasonEpisodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);

        getAllEpisodesAndSetTitle();
        getUnwatched();
        getSeasonsList();
        getEpisodesForSeasons();
        setCheckBoxWatchedAll();
        setExpandableListView();
    }

    private void getAllEpisodesAndSetTitle() {
        Show show = getIntent().getExtras().getParcelable("com.spstanchev.tvseries" + Constants.TAG_SHOW);
        TextView tvShowTitle = (TextView) findViewById(R.id.tvShowTitle);
        tvShowTitle.setText(show.getName());
        episodes = show.getEpisodes();
    }

    private void getUnwatched() {
        unwatchedEpisodes = new ArrayList<>();

        Date currentDate = new Date();
        for (Episode episode : episodes) {
            Date episodeAirdate = Utils.getDateFromString(episode.getAirstamp(), Utils.getJsonAirstampFormat());
            if (!episode.isWatched() && currentDate.after(episodeAirdate)) {
                unwatchedEpisodes.add(episode);
            }
        }
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
        for (Episode episode : episodes) {
            if (episode.getSeason() > seasons) {
                seasons = episode.getSeason();
            }
        }
        return seasons;
    }

    private void getEpisodesForSeasons() {
        listSeasonEpisodes = new HashMap<>();
        unwatchedSeasonsList = new ArrayList<>();
        for (int i = 0; i < seasonsList.size(); i++) {
            ArrayList<Episode> episodesList = new ArrayList<>();
            for (int j = 0; j < unwatchedEpisodes.size(); j++) {
                if (unwatchedEpisodes.get(j).getSeason() == i + 1) {
                    episodesList.add(unwatchedEpisodes.get(j));
                }
            }
            if (!episodesList.isEmpty()) {
                unwatchedSeasonsList.add(seasonsList.get(i));
                listSeasonEpisodes.put(seasonsList.get(i), episodesList);
            }
        }
    }

    private void setCheckBoxWatchedAll() {
        CheckBox checkBoxWatchedAll = (CheckBox) findViewById(R.id.checkBoxAll);
        checkBoxWatchedAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            for (Episode episode : unwatchedEpisodes) {
                                episode.setWatched(true);
                                ShowProvider.Helper.updateEpisode(getContentResolver(), episode);
                            }
                            return null;
                        }
                    }.execute();
                    Toast.makeText(EpisodesActivity.this, "All episodes marked as watched!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void setExpandableListView() {
        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);

        SeasonsAndEpisodesExpandableAdapter savedAdapter = (SeasonsAndEpisodesExpandableAdapter) getLastCustomNonConfigurationInstance();
        if (savedAdapter != null) {
            adapter =  savedAdapter;
        } else {
            adapter = new SeasonsAndEpisodesExpandableAdapter(this, unwatchedSeasonsList, listSeasonEpisodes);
        }
        expandableListView.setAdapter(adapter);
        expandableListView.setOnChildClickListener(this);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Episode episode = listSeasonEpisodes.get(unwatchedSeasonsList.get(groupPosition))
                .get(childPosition);
        showDialog(episode, groupPosition, childPosition);
        return true;
    }

    @Override
    public void showDialog(Episode episode, int groupPosition, int childPosition) {
        EpisodeInfoFragment dialog = EpisodeInfoFragment.newInstance(episode, groupPosition, childPosition);
        dialog.show(getSupportFragmentManager(), "Episode Info");
    }

    @Override
    public void doPositiveClick(final Episode episode, int groupPosition, int childPosition) {
        listSeasonEpisodes.get(unwatchedSeasonsList.get(groupPosition))
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

}
package com.spstanchev.tvseries.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.spstanchev.tvseries.R;
import com.spstanchev.tvseries.common.Utils;
import com.spstanchev.tvseries.models.Episode;
import com.spstanchev.tvseries.models.Season;
import com.spstanchev.tvseries.providers.ShowProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Elle on 2/24/2015.
 */
public class SeasonsAndEpisodesExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    // header titles
    private ArrayList<Season> listDataHeader;
    // child data in format of header title, child title
    private HashMap<Season, ArrayList<Episode>> listDataChild;

    public SeasonsAndEpisodesExpandableAdapter(Context context, ArrayList<Season> listDataHeader,
                                               HashMap<Season, ArrayList<Episode>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    public void updateCollection(ArrayList<Season> latestDataHeader,
                                 HashMap<Season, ArrayList<Episode>> latestChildData) {
        this.listDataHeader = latestDataHeader;
        this.listDataChild = latestChildData;
        notifyDataSetChanged();
    }

    @Override
    public Episode getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Episode episode = getChild(groupPosition, childPosition);
        final String name = episode.getName();
        TextView txtListChild, txtAirStamp;
        final CheckBox checkBox;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.episode_expandable_list_item, null);

            txtListChild = (TextView) convertView.findViewById(R.id.episodeNameExpandable);
            txtAirStamp = (TextView) convertView.findViewById(R.id.episodeAirdateExpandable);
            checkBox = (CheckBox) convertView.findViewById(R.id.checkBoxWatchedEpisode);

            convertView.setTag(R.id.episodeNameExpandable, txtListChild);
            convertView.setTag(R.id.episodeAirdateExpandable, txtAirStamp);
            convertView.setTag(R.id.checkBoxWatchedEpisode, checkBox);
        } else {
            txtListChild = (TextView) convertView.getTag(R.id.episodeNameExpandable);
            txtAirStamp = (TextView) convertView.getTag(R.id.episodeAirdateExpandable);
            checkBox = (CheckBox) convertView.getTag(R.id.checkBoxWatchedEpisode);
        }

        checkBox.setChecked(episode.isWatched());
        Date currentDate = new Date();
        Date episodeAirdate = Utils.getDateFromString(episode.getAirstamp(), Utils.getJsonAirstampFormat());
        checkBox.setEnabled(!currentDate.before(episodeAirdate));
        if (episodeAirdate.after(currentDate))
            checkBox.setChecked(false);

        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    episode.setWatched(true);
                } else {
                    episode.setWatched(false);
                }
                UpdateEpisodeTask updateEpisodeTask = new UpdateEpisodeTask();
                updateEpisodeTask.execute(episode);
            }
        });
        final String childName = episode.getNumber() + ". " + name;
        final String childDate = episode.getAirdate();
        txtListChild.setText(childName);
        txtAirStamp.setText("Aired on " + childDate);
        return convertView;
    }

    private class UpdateEpisodeTask extends AsyncTask<Episode, Void, Void>{
        @Override
        protected Void doInBackground(Episode... params) {
            ShowProvider.Helper.updateEpisode(context.getContentResolver(), params[0]);
            return null;
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Season getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        TextView lblListHeader;
        final CheckBox checkBox;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.season_header_expandable_list_item, null);
            lblListHeader = (TextView) convertView.findViewById(R.id.seasonNameExpandable);
            checkBox = (CheckBox) convertView.findViewById(R.id.cbWatchedSeason);

            convertView.setTag(R.id.seasonNameExpandable, lblListHeader);
            convertView.setTag(R.id.cbWatchedSeason, checkBox);
        } else {
            lblListHeader = (TextView) convertView.getTag(R.id.seasonNameExpandable);
            checkBox = (CheckBox) convertView.getTag(R.id.cbWatchedSeason);
        }

        final Season currentSeason = getGroup(groupPosition);

        String headerTitle = "Season " + currentSeason.getSeason();
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        checkBox.setChecked(currentSeason.watchedAll);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSeason.setWatchedAll(checkBox.isChecked());
                for (Episode episode : listDataChild.get(listDataHeader.get(groupPosition))){
                    episode.setWatched(checkBox.isChecked());
                    UpdateEpisodeTask updateEpisodeTask = new UpdateEpisodeTask();
                    updateEpisodeTask.execute(episode);
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
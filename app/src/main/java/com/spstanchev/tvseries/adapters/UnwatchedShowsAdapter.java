package com.spstanchev.tvseries.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spstanchev.tvseries.R;
import com.spstanchev.tvseries.models.UnwatchedShow;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Stefan on 2/21/2015.
 */
public class UnwatchedShowsAdapter extends BaseAdapter {
    private static final String TAG = MyShowsAdapter.class.getSimpleName();
    private ArrayList<UnwatchedShow> shows = new ArrayList<>();
    private Context context;

    public UnwatchedShowsAdapter(Context context) {
        this.context = context;
    }

    public void updateCollection(ArrayList<UnwatchedShow> latestCollection) {
        shows = latestCollection;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return shows.size();
    }

    @Override
    public UnwatchedShow getItem(int position) {
        return shows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView ivPoster;
        TextView tvName, tvSeasons, tvUnwatchedEpisodes, tvStatus, tvNetworkName;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_show_unwatched_episodes, parent, false);
            ivPoster = (ImageView) convertView.findViewById(R.id.ivPoster);
            tvName = (TextView) convertView.findViewById(R.id.tvName);
            tvSeasons = (TextView) convertView.findViewById(R.id.tvSeasons);
            tvUnwatchedEpisodes = (TextView) convertView.findViewById(R.id.tvUnwatchedEpisodes);
            tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
            tvNetworkName = (TextView) convertView.findViewById(R.id.tvNetworkName);

            convertView.setTag(R.id.ivPoster, ivPoster);
            convertView.setTag(R.id.tvName, tvName);
            convertView.setTag(R.id.tvSeasons, tvSeasons);
            convertView.setTag(R.id.tvUnwatchedEpisodes, tvUnwatchedEpisodes);
            convertView.setTag(R.id.tvStatus, tvStatus);
            convertView.setTag(R.id.tvNetworkName, tvNetworkName);
        } else {
            ivPoster = (ImageView) convertView.getTag(R.id.ivPoster);
            tvName = (TextView) convertView.getTag(R.id.tvName);
            tvSeasons = (TextView) convertView.getTag(R.id.tvSeasons);
            tvUnwatchedEpisodes = (TextView) convertView.getTag(R.id.tvUnwatchedEpisodes);
            tvStatus = (TextView) convertView.getTag(R.id.tvStatus);
            tvNetworkName = (TextView) convertView.getTag(R.id.tvNetworkName);
        }

        UnwatchedShow currentUnwatchedShow = getItem(position);

        if (currentUnwatchedShow.getShow().getImage() != null) {
            Picasso.with(context)
                    .load(currentUnwatchedShow.getShow().getImage().getMedium())
                    .resize(250, 350)
                    .placeholder(R.drawable.tv_series)
                    .error(R.drawable.tv_series)
                    .into(ivPoster);
        }
        tvName.setText(currentUnwatchedShow.getShow().getName());
        String seasonsN = (currentUnwatchedShow.getSeasons() > 0) ? String.valueOf(currentUnwatchedShow.getSeasons()) : "";
         tvSeasons.setText("Seasons: " + seasonsN);
        tvUnwatchedEpisodes.setText("Unwatched episodes: " + String.valueOf(currentUnwatchedShow.getUnwatchedEpisodes()));
        tvStatus.setText("Status: " + currentUnwatchedShow.getShow().getStatus());
        tvNetworkName.setText("Airs on: " + currentUnwatchedShow.getShow().getNetwork().getName());

        return convertView;
    }


}

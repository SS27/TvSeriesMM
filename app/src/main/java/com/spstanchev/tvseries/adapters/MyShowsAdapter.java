package com.spstanchev.tvseries.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.spstanchev.tvseries.R;
import com.spstanchev.tvseries.models.Show;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Stefan on 2/8/2015.
 */
public class MyShowsAdapter extends BaseAdapter {
    private static final String TAG = MyShowsAdapter.class.getSimpleName();
    private ArrayList<Show> shows = new ArrayList<>();
    private Context context;

    public MyShowsAdapter(Context context) {
        this.context = context;
    }

    public void updateCollection(ArrayList<Show> latestCollection) {
        shows = latestCollection;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return shows.size();
    }

    @Override
    public Show getItem(int position) {
        return shows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView ivPoster;
        TextView tvName, tvYear, tvCountry;
        RatingBar ratingBar;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_my_show, parent, false);
            ivPoster = (ImageView) convertView.findViewById(R.id.ivPoster);
            tvName = (TextView) convertView.findViewById(R.id.tvName);
            ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            tvYear = (TextView) convertView.findViewById(R.id.tvYear);
            tvCountry = (TextView) convertView.findViewById(R.id.tvCountry);

            convertView.setTag(R.id.ivPoster, ivPoster);
            convertView.setTag(R.id.tvName, tvName);
            convertView.setTag(R.id.ratingBar, ratingBar);
            convertView.setTag(R.id.tvYear, tvYear);
            convertView.setTag(R.id.tvCountry, tvCountry);
        } else {
            ivPoster = (ImageView) convertView.getTag(R.id.ivPoster);
            tvName = (TextView) convertView.getTag(R.id.tvName);
            ratingBar = (RatingBar) convertView.getTag(R.id.ratingBar);
            tvYear = (TextView) convertView.getTag(R.id.tvYear);
            tvCountry = (TextView) convertView.getTag(R.id.tvCountry);
        }

        Show currentShow = getItem(position);
        if (currentShow.getImage() != null) {
            Picasso.with(context)
                    .load(currentShow.getImage().getMedium())
                    .resize(250, 350)
                    .placeholder(R.drawable.tv_series)
                    .error(R.drawable.tv_series)
                    .into(ivPoster);
        }
        tvName.setText(currentShow.getName());
        ratingBar.setRating((float) (currentShow.getRatingAverage() * 0.5));
        tvYear.setText("Release year: " + currentShow.getPremiered().substring(0, 4));
        tvCountry.setText("Country: " + currentShow.getNetwork().getCountry().getName());

        return convertView;
    }

}

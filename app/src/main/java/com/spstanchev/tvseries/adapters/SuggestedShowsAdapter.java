package com.spstanchev.tvseries.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.spstanchev.tvseries.R;
import com.spstanchev.tvseries.fragments.ShowDialogInterface;
import com.spstanchev.tvseries.models.AddedShow;
import com.spstanchev.tvseries.models.Show;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Elle on 2/12/2015.
 */
public class SuggestedShowsAdapter extends BaseAdapter {
    private ArrayList<AddedShow> showsList = new ArrayList<>();
    private Context context;

    public SuggestedShowsAdapter(Context context) {
        this.context = context;
    }

    public void updateCollection(ArrayList<AddedShow> latestCollection) {
        showsList = latestCollection;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return showsList.size();
    }

    @Override
    public AddedShow getItem(int i) {
        return showsList.get(i);
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        TextView tvTitle;
        ImageButton addOrRemoveShow;
        ImageButton moreInfo;
        ImageView ivPoster;
        RatingBar ratingBar;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_suggested_show, parent, false);
            tvTitle = (TextView) convertView.findViewById(R.id.showTitle);
            ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            ivPoster = (ImageView) convertView.findViewById(R.id.showImageView);
            addOrRemoveShow = (ImageButton) convertView.findViewById(R.id.addShowButton);
            moreInfo = (ImageButton) convertView.findViewById(R.id.moreInfoButton);

            convertView.setTag(R.id.showTitle, tvTitle);
            convertView.setTag(R.id.ratingBar, ratingBar);
            convertView.setTag(R.id.showImageView, ivPoster);
            convertView.setTag(R.id.addShowButton, addOrRemoveShow);
            convertView.setTag(R.id.moreInfoButton, moreInfo);
        } else {
            tvTitle = (TextView) convertView.getTag(R.id.showTitle);
            ratingBar = (RatingBar) convertView.getTag(R.id.ratingBar);
            ivPoster = (ImageView) convertView.getTag(R.id.showImageView);
            addOrRemoveShow = (ImageButton) convertView.getTag(R.id.addShowButton);
            moreInfo = (ImageButton) convertView.getTag(R.id.moreInfoButton);
        }

        final AddedShow currentAddedShow = getItem(i);
        Show currentShow = currentAddedShow.getShow();

        if (currentAddedShow.isAdded()) {
            addOrRemoveShow.setBackgroundResource(R.drawable.custom_btn_remove_show);
        } else {
            addOrRemoveShow.setBackgroundResource(R.drawable.custom_btn_add_show);
        }

        tvTitle.setText(currentShow.getName());
        ratingBar.setRating((float) (currentShow.getRatingAverage() * 0.5));

        moreInfo.setOnClickListener(null);
        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ShowDialogInterface) context).showDialog(currentAddedShow, i);
            }
        });

        addOrRemoveShow.setOnClickListener(null);
        addOrRemoveShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ShowDialogInterface) context).doPositiveClick(currentAddedShow, i);
            }
        });

        Picasso.with(context)
                .load(currentShow.getImage().getMedium())
                        //.resize(210, 295)
                .placeholder(R.drawable.no_poster_available)
                .error(R.drawable.no_poster_available)
                .into(ivPoster);

        return convertView;
    }

}
package com.spstanchev.tvseries.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.spstanchev.tvseries.R;
import com.spstanchev.tvseries.common.Constants;
import com.spstanchev.tvseries.models.Cast;
import com.spstanchev.tvseries.models.Show;
import com.spstanchev.tvseries.tasks.AsyncAddOrDeleteShowInDb;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShowInfoActivity extends ActionBarActivity {
    private Show show;
    private SharedPreferences sharedPreferences;
    private ImageView ivPoster;
    private RatingBar ratingBar;
    private TextView tvName, tvSummary, tvStatus, tvPremiered;
    private ImageButton ibRemove, ibMaze, ibEpisodes;
    private LinearLayout llCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        show = getIntent().getExtras().getParcelable("com.spstanchev.tvseries" + Constants.TAG_SHOW);

        getViews();
        setViews();
        setListeners();
    }

    private void setListeners() {
        ibRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncAddOrDeleteShowInDb deleteAsyncTask = new AsyncAddOrDeleteShowInDb(getContentResolver(), true);
                deleteAsyncTask.execute(show);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("Show" + show.getId());
                editor.commit();
                finish();
            }
        });
        ibMaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPage = new Intent();
                intentPage.setAction(Intent.ACTION_VIEW);
                intentPage.setData(Uri.parse(show.getUrl()));
                startActivity(intentPage);
            }
        });
        ibEpisodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent episodes = new Intent(ShowInfoActivity.this, EpisodeListActivity.class);
                episodes.putExtra("com.spstanchev.tvseries" + Constants.TAG_SHOW_ID, show.getId());
                startActivity(episodes);
            }
        });
        if (show.getImage() != null) {
            Picasso.with(this)
                    .load(show.getImage().getMedium())
                    .placeholder(R.drawable.no_poster_available)
                    .error(R.drawable.no_poster_available)
                    .into(ivPoster);
        }
    }

    private void setViews() {
        tvName.setText(show.getName());
        tvSummary.setText(show.getSummary());
        tvStatus.setText("Status: " + show.getStatus());
        tvPremiered.setText("Premiered on: " + show.getPremiered());
        ratingBar.setRating((float) (show.getRatingAverage() * 0.5));
        setCastLayout();
    }

    private void setCastLayout() {
        ArrayList<Cast> castList = show.getCast();
        for (int i = 0; i < castList.size(); i++) {
            Cast cast = castList.get(i);
            View rootView = LayoutInflater.from(this).inflate(R.layout.list_item_cast, llCast, true);
            setCastImageView(i, cast, rootView);
            setCastTextViews(i, cast, rootView);
        }
    }

    private void setCastTextViews(int i, Cast cast, View rootView) {
        TextView tvPerson = (TextView) rootView.findViewById(R.id.tvPerson);
        TextView tvCharacter = (TextView) rootView.findViewById(R.id.tvCharacter);
        tvPerson.setId(i);
        tvCharacter.setId(i);
        tvPerson.setText(cast.getPerson().getName());
        tvCharacter.setText(cast.getCharacter().getName());
    }

    private void setCastImageView(int i, Cast cast, View rootView) {
        ImageView ivCast = (ImageView) rootView.findViewById(R.id.ivCast);
        ivCast.setId(i);
        String url = "Dummy";
        if (!TextUtils.isEmpty(cast.getCharacter().getImage().getMedium())){
            url = cast.getCharacter().getImage().getMedium();
        } else if (!TextUtils.isEmpty(cast.getPerson().getImage().getMedium())){
            url = cast.getPerson().getImage().getMedium();
        }
        Picasso.with(this)
                .load(url)
                .error(R.drawable.no_image_available)
                .into(ivCast);
    }

    private void getViews() {
        ivPoster = (ImageView) findViewById(R.id.imageViewLarge);
        ratingBar = (RatingBar) findViewById(R.id.showInfoRating);
        tvName = (TextView) findViewById(R.id.showInfoName);
        tvSummary = (TextView) findViewById(R.id.showInfoSummary);
        tvStatus = (TextView) findViewById(R.id.showInfoStatus);
        tvPremiered = (TextView) findViewById(R.id.showInfoPremiered);
        ibRemove = (ImageButton) findViewById(R.id.imageButtonRemove);
        ibMaze = (ImageButton) findViewById(R.id.imageButtonTvMaze);
        ibEpisodes = (ImageButton) findViewById(R.id.imageButtonEpisodes);
        llCast = (LinearLayout) findViewById(R.id.llCast);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
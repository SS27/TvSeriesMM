package com.spstanchev.tvseries.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spstanchev.tvseries.R;
import com.spstanchev.tvseries.common.Utils;
import com.spstanchev.tvseries.models.Episode;
import com.squareup.picasso.Picasso;

import java.util.Date;

/**
 * Created by Stefan on 3/1/2015.
 */
public class EpisodeInfoFragment extends DialogFragment {
    private Episode currentEpisode;
    private int groupPosition, childPosition;

    //Create a new instance of MyDialogFragment
    public static EpisodeInfoFragment newInstance(Episode episode, int groupPosition, int childPosition) {
        EpisodeInfoFragment f = new EpisodeInfoFragment();

        // Supply arguments
        Bundle args = new Bundle();
        args.putParcelable("episode", episode);
        args.putInt("groupPosition", groupPosition);
        args.putInt("childPosition", childPosition);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        currentEpisode = getArguments().getParcelable("episode");
        groupPosition = getArguments().getInt("groupPosition", -1);
        childPosition = getArguments().getInt("childPosition", -1);
        if (currentEpisode == null || groupPosition == -1 || childPosition == -1)
            return super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogLayout = inflater.inflate(R.layout.episode_info_dialog, null);

        //get and set the views
        TextView tvTitleDialog = (TextView) dialogLayout.findViewById(R.id.tvTitleDialog);
        ImageView ivPoster = (ImageView) dialogLayout.findViewById(R.id.ivPoster);
        TextView tvSeasonNum = (TextView) dialogLayout.findViewById(R.id.tvSeasonNum);
        TextView tvEpisodeNum = (TextView) dialogLayout.findViewById(R.id.tvEpisodeNum);
        TextView tvAired = (TextView) dialogLayout.findViewById(R.id.tvAired);
        TextView tvRuntime = (TextView) dialogLayout.findViewById(R.id.tvRuntime);
        TextView tvSummary = (TextView) dialogLayout.findViewById(R.id.tvSummary);
        ImageView tvCredits = (ImageView) dialogLayout.findViewById(R.id.ivCredits);

        tvTitleDialog.setText(currentEpisode.getName());
        if (currentEpisode.getImage() != null){
            Picasso.with(getActivity())
                    .load(currentEpisode.getImage().getOriginal())
                    .into(ivPoster);
        } else {
            ivPoster.setVisibility(View.GONE);
        }

        tvSeasonNum.setText("Season: " + currentEpisode.getSeason());
        tvEpisodeNum.setText("Episode: " + currentEpisode.getNumber());
        tvAired.setText("Aired on: " + currentEpisode.getAirdate());
        tvRuntime.setText("Runtime: " + currentEpisode.getRuntime() + " minutes");
        tvSummary.setText(currentEpisode.getSummary());
        tvCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentEpisode.getUrl()));
                startActivity(intent);
            }
        });

        builder.setView(dialogLayout);

        Date currentDate = new Date();
        Date episodeAirdate = Utils.getDateFromString(currentEpisode.getAirstamp(), Utils.getJsonAirstampFormat());
        if (currentDate.after(episodeAirdate)) {
            String positiveBtn = currentEpisode.isWatched() ? getActivity().getString(R.string.label_btn_unwatched) : getActivity().getString(R.string.label_btn_watched);
            builder.setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    currentEpisode.setWatched(!currentEpisode.isWatched());
                    ((EpisodeDialogInterface) getActivity()).doPositiveClick(currentEpisode, groupPosition, childPosition);
                }
            });
        }
        builder.setNegativeButton("Back to list", null);

        return builder.create();

    }
}

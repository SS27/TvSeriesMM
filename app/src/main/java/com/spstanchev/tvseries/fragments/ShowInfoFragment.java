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
import com.spstanchev.tvseries.models.AddedShow;

/**
 * Created by Elle on 2/16/2015.
 */
public class ShowInfoFragment extends DialogFragment {

    private AddedShow addedShow;
    private int position;

    //Create a new instance of ShowInfoFragment
    public static ShowInfoFragment newInstance(AddedShow show, int position) {
        ShowInfoFragment f = new ShowInfoFragment();

        Bundle args = new Bundle();
        args.putParcelable("addedShow", show);
        args.putInt("position", position);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        addedShow = getArguments().getParcelable("addedShow");
        position = getArguments().getInt("position", -1);
        if (addedShow == null || position == -1)
            return super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View layout = inflater.inflate(R.layout.add_show_dialog, null);
        TextView type = (TextView) layout.findViewById(R.id.textViewType);
        TextView runtime = (TextView) layout.findViewById(R.id.textViewRuntime);
        TextView premiered = (TextView) layout.findViewById(R.id.textViewPremiered);
        TextView summary = (TextView) layout.findViewById(R.id.textViewSummaryDialogue);
        TextView title = (TextView) layout.findViewById(R.id.textViewTitleDialog);
        ImageView credits = (ImageView) layout.findViewById(R.id.textViewCredits);
        TextView network = (TextView) layout.findViewById(R.id.textViewNetwork);
        TextView status = (TextView) layout.findViewById(R.id.textViewStatus);
        TextView staring = (TextView) layout.findViewById(R.id.textViewStaring);

        title.setText(addedShow.getShow().getName());
        summary.setText(addedShow.getShow().getSummary());
        network.setText("Network: " + addedShow.getShow().getNetwork().getName());
        status.setText("Status: " + addedShow.getShow().getStatus());
        type.setText("Type: " + addedShow.getShow().getType());
        runtime.setText("Runtime: " + addedShow.getShow().getRuntime() + " minutes");
        premiered.setText("Premiered on: " + addedShow.getShow().getPremiered());
        if (addedShow.getShow().getCast().isEmpty()){
            staring.setVisibility(View.GONE);
        }
        else {
            staring.setText(getStaringLabel());
        }

        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPage = new Intent();
                intentPage.setAction(Intent.ACTION_VIEW);
                intentPage.setData(Uri.parse(addedShow.getShow().getUrl()));
                startActivity(intentPage);
            }
        });


        builder.setView(layout);
        String positiveBtn = addedShow.isAdded() ? "Delete Show" : "Add Show";
        builder.setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ((ShowDialogInterface) getActivity()).doPositiveClick(addedShow, position);
            }
        });
        builder.setNegativeButton("Back to list", null);

        return builder.create();
    }

    private String getStaringLabel() {
        String staringLabel = "Staring:\n";
        for (int i=0; i < addedShow.getShow().getCast().size(); i++){
            staringLabel = staringLabel.concat(addedShow.getShow().getCast().get(i).getPerson().getName()+"\n");
            if (i == 2)
                break;
        }
        return staringLabel;
    }


}
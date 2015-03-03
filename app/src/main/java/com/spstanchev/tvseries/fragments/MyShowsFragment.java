package com.spstanchev.tvseries.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.spstanchev.tvseries.R;
import com.spstanchev.tvseries.activities.SearchShowActivity;
import com.spstanchev.tvseries.activities.ShowInfoActivity;
import com.spstanchev.tvseries.adapters.MyShowsAdapter;
import com.spstanchev.tvseries.common.Constants;
import com.spstanchev.tvseries.models.Show;
import com.spstanchev.tvseries.tasks.AsyncDbResponse;
import com.spstanchev.tvseries.tasks.AsyncQueryShowsFromDb;

import java.util.ArrayList;

/**
 * Created by Stefan on 2/8/2015.
 */
public class MyShowsFragment extends Fragment implements AdapterView.OnItemClickListener, AsyncDbResponse {

    private static final String TAG = MyShowsFragment.class.getSimpleName();
    private MyShowsAdapter adapter;
    private ArrayList<Show> myShows;
    private TextView tvEmpty;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        adapter = new MyShowsAdapter(activity);
        //query the database
        getShowsFromDb();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_shows, container, false);
        ImageButton btnAddShow = (ImageButton) rootView.findViewById(R.id.btnAddShow);
        ListView lvMyShows = (ListView) rootView.findViewById(R.id.lvMyShows);
        lvMyShows.setAdapter(adapter);
        lvMyShows.setOnItemClickListener(this);

        btnAddShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchShowActivity.class);
                startActivity(intent);
            }
        });


        tvEmpty = (TextView) rootView.findViewById(R.id.tvEmptyShowList);
        tvEmpty.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //query again in case there were updates
        getShowsFromDb();
    }

    private void getShowsFromDb() {
        //query db for current shows
        AsyncQueryShowsFromDb queryAsyncTask = new AsyncQueryShowsFromDb(this, getActivity().getContentResolver());
        queryAsyncTask.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent moreInfo = new Intent(getActivity(), ShowInfoActivity.class);
        moreInfo.putExtra("com.spstanchev.tvseries" + Constants.TAG_SHOW, myShows.get(position));
        startActivity(moreInfo);
    }

    @Override
    public void showsFromDbResponse(ArrayList<Show> shows) {
        if (shows != null) {
            myShows = shows;
            adapter.updateCollection(myShows);
            tvEmpty.setVisibility(View.GONE);
        }
        else {
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

}

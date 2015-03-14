package com.spstanchev.tvseries.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private ImageButton btnAddShow;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        adapter = new MyShowsAdapter(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_shows, container, false);
        btnAddShow = (ImageButton) rootView.findViewById(R.id.btnAddShow);
        ListView lvMyShows = (ListView) rootView.findViewById(R.id.lvMyShows);
        lvMyShows.setAdapter(adapter);
        if (savedInstanceState != null){
            myShows = savedInstanceState.getParcelableArrayList("myShows");
            if (myShows != null)
                adapter.updateCollection(myShows);
        }
        lvMyShows.setOnItemClickListener(this);

        btnAddShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchShowActivity.class);
                startActivity(intent);
            }
        });


        tvEmpty = (TextView) rootView.findViewById(R.id.tvEmptyShowList);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //query the db
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
        moreInfo.putExtra("com.spstanchev.tvseries" + Constants.TAG_SHOW, adapter.getShows().get(position));
        startActivity(moreInfo);
    }

    @Override
    public void showsFromDbResponse(ArrayList<Show> shows) {
        if (shows != null) {
            myShows = shows;
            adapter.updateCollection(myShows);
            tvEmpty.setVisibility(View.GONE);
            btnAddShow.setVisibility(View.GONE);

        } else {
            myShows = new ArrayList<>();
            adapter.updateCollection(myShows);
            tvEmpty.setVisibility(View.VISIBLE);
            btnAddShow.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_my_shows, menu);
        setUpSearchView(menu);
    }

    private void setUpSearchView(Menu menu) {
        // Associate searchable configuration with the SearchView
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_my_shows).getActionView();
        searchView.setQueryHint(getString(R.string.action_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!s.isEmpty()){
                    adapter.getFilter().filter(s);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (TextUtils.isEmpty(s) && myShows != null) {
                    adapter.updateCollection(myShows);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_my_shows: {
                return true;
            }
            case R.id.action_add_show: {
                Intent intent = new Intent(getActivity(), SearchShowActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("myShows", myShows);
        super.onSaveInstanceState(outState);
    }
}

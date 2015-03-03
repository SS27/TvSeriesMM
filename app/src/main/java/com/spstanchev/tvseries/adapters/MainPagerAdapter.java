package com.spstanchev.tvseries.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.spstanchev.tvseries.common.Constants;
import com.spstanchev.tvseries.fragments.MyShowsFragment;
import com.spstanchev.tvseries.fragments.UnwatchedShowsFragment;

/**
 * Created by Stefan on 2/8/2015.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment;
        switch (i) {
            case 0:
                fragment = new MyShowsFragment();
                break;
            case 1:
                fragment = new UnwatchedShowsFragment();
                break;
            default:
                fragment = null;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence title = "";
        switch (position) {
            case 0:
                title = Constants.FRAGMENT_TITLE_MY_SHOWS;
                break;
            case 1:
                title = Constants.FRAGMENT_TITLE_UNWATCHED;
                break;
        }
        return title;
    }
}

package com.spstanchev.tvseries.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.spstanchev.tvseries.R;
import com.spstanchev.tvseries.adapters.MainPagerAdapter;
import com.spstanchev.tvseries.receivers.AlarmReceiver;

/**
 * Created by Stefan on 2/8/2015.
 */
public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private MainPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private AlarmReceiver alarm = new AlarmReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!alarm.isAlarmUp(this)) {
            //set recurring alarm to check for updates regularly
            alarm.setAlarm(this);
        }

        //set up UI
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        viewPager = (ViewPager) findViewById(R.id.mainViewPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        Bundle extras = getIntent().getExtras();

        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            ActionBar.Tab tab = actionBar.newTab();
            tab.setText(pagerAdapter.getPageTitle(i));
            tab.setTabListener(this);
            actionBar.addTab(tab);
        }

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}

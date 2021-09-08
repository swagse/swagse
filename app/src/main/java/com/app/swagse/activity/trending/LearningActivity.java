package com.app.swagse.activity.trending;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.app.swagse.R;
import com.app.swagse.fragment.trend.AllFragment;
import com.app.swagse.fragment.trend.MusicFragment;
import com.google.android.material.tabs.TabLayout;

public class LearningActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    TabLayout trendingTabLayout;
    ViewPager trendingViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Fashion & Beauty");

        trendingTabLayout = findViewById(R.id.trendingTabLayout);
        trendingViewPager = findViewById(R.id.trendingViewPager);

        trendingTabLayout.addTab(trendingTabLayout.newTab().setText("HOME"));
        trendingTabLayout.addTab(trendingTabLayout.newTab().setText("ABOUT"));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), trendingTabLayout.getTabCount());
        trendingViewPager.setAdapter(viewPagerAdapter);
        // trendingViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(trendingTabLayout));

        //Adding onTabSelectedListener to swipe views
        trendingTabLayout.setOnTabSelectedListener(this);
        // addOnPageChangeListener event change the tab on slide
        trendingViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(trendingTabLayout));
        trendingViewPager.setOffscreenPageLimit(5);

    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        trendingViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        // private static int NUM_ITEMS = 2;
        int mNumOfTabs;

        public ViewPagerAdapter(FragmentManager fragmentManager, int NumOfTabs) {
            super(fragmentManager);
            this.mNumOfTabs = NumOfTabs;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    AllFragment allFragment = new AllFragment();
                    return allFragment;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    MusicFragment musicFragment = new MusicFragment();
                    return musicFragment;
                default:
                    return null;
            }
        }
    }
}
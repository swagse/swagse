package com.app.swagse.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.app.swagse.R;
import com.app.swagse.fragment.SwagTubeFragment;
import com.app.swagse.fragment.SwaggerFragment;
import com.app.swagse.fragment.TrendingFragment;
import com.app.swagse.fragment.trend.AllFragment;
import com.app.swagse.fragment.trend.FilmsFragment;
import com.app.swagse.fragment.trend.GamingFragment;
import com.app.swagse.fragment.trend.MusicFragment;
import com.app.swagse.fragment.trend.NewsFragment;
import com.google.android.material.tabs.TabLayout;

public class TrendingActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    TabLayout trendingTabLayout;
    ViewPager trendingViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Trending");

        trendingTabLayout = findViewById(R.id.trendingTabLayout);
        trendingViewPager = findViewById(R.id.trendingViewPager);

        trendingTabLayout.addTab(trendingTabLayout.newTab().setText("NOW"));
        trendingTabLayout.addTab(trendingTabLayout.newTab().setText("MUSIC"));
        trendingTabLayout.addTab(trendingTabLayout.newTab().setText("GAMING"));
        trendingTabLayout.addTab(trendingTabLayout.newTab().setText("NEWS"));
        trendingTabLayout.addTab(trendingTabLayout.newTab().setText("FILMS"));
//        trendingTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), trendingTabLayout.getTabCount());
        trendingViewPager.setAdapter(viewPagerAdapter);
        // trendingViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(trendingTabLayout));

        //Adding onTabSelectedListener to swipe views
        trendingTabLayout.setOnTabSelectedListener(this);
        // addOnPageChangeListener event change the tab on slide
        trendingViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(trendingTabLayout));
        trendingViewPager.setOffscreenPageLimit(5);

        trendingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: // Fragment # 0 - This will show FirstFragment
                        getSupportActionBar().setTitle("TRENDING");
                        break;
                    case 1: // Fragment # 0 - This will show FirstFragment different title
                        getSupportActionBar().setTitle("MUSIC");
                        break;
                    case 2: // Fragment # 1 - This will show SecondFragment
                        getSupportActionBar().setTitle("GAMING");
                        break;
                    case 3: // Fragment # 1 - This will show SecondFragment
                        getSupportActionBar().setTitle("NEWS");
                        break;
                    case 4: // Fragment # 1 - This will show SecondFragment
                        getSupportActionBar().setTitle("FILMS");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
                case 2: // Fragment # 1 - This will show SecondFragment
                    GamingFragment gamingFragment = new GamingFragment();
                    return gamingFragment;
                case 3: // Fragment # 1 - This will show SecondFragment
                    NewsFragment newsFragment = new NewsFragment();
                    return newsFragment;
                case 4: // Fragment # 1 - This will show SecondFragment
                    FilmsFragment filmsFragment = new FilmsFragment();
                    return filmsFragment;
                default:
                    return null;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
package com.example.zombiessy.zombiessyhw9;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mainViewPager;
    private String[] tabTitles = {"SEARCH", "FAVORITE"};
    private Integer[] tabIcons = {
            R.drawable.search,
            R.drawable.heart_fill_white
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        PagerView pagerView = new PagerView(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mainViewPager = findViewById(R.id.viewpager);
        mainViewPager.setAdapter(pagerView);

        TabLayout tabLayout = findViewById(R.id.tabs);
        for (int i = 0; i < tabLayout.getTabCount(); i++ ) {
            LinearLayout currTab = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tab_text = (TextView) currTab.findViewById(R.id.tabContent);
            tab_text.setText("  " + tabTitles[i]);
            tab_text.setCompoundDrawablesWithIntrinsicBounds(tabIcons[i], 0, 0, 0);
            tabLayout.getTabAt(i).setCustomView(tab_text);
        }

        mainViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mainViewPager));
    }

    public class PagerView extends FragmentPagerAdapter {
        private android.support.v4.app.Fragment searchFragment;
        private android.support.v4.app.Fragment favoriteFragment;

        public PagerView(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    if (searchFragment == null) {
                        searchFragment = new SearchFormFragment();
                    }
                    return searchFragment;
                case 1:
                    if (favoriteFragment == null) {
                        favoriteFragment = new FavoriteListFragment();
                    }
                    return favoriteFragment;
            }
            return null;
        }


        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }
}

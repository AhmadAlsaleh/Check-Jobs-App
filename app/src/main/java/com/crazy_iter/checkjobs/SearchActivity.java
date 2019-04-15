package com.crazy_iter.checkjobs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.crazy_iter.checkjobs.Adapters.SlidePagerSearchAdapter;

public class SearchActivity extends AppCompatActivity {

    public static RelativeLayout loadingSearchRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Searching for");

        loadingSearchRL = findViewById(R.id.searchLoadingRL);

        loadSpots();
        setTabs();

    }

    private void loadSpots() {

    }

    private void setTabs() {
        TabLayout tabPages = findViewById(R.id.tabPagesSearch);
        tabPages.addTab(tabPages.newTab().setText("All"));
        tabPages.addTab(tabPages.newTab().setText("Near Places"));
        tabPages.addTab(tabPages.newTab().setText("Top Salary"));
        tabPages.addTab(tabPages.newTab().setText("Best Time"));
        tabPages.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPages = findViewById(R.id.viewPagesSearch);
        viewPages.setOffscreenPageLimit(tabPages.getTabCount() - 1);
        SlidePagerSearchAdapter slidePagerSearchAdapter = new SlidePagerSearchAdapter(getSupportFragmentManager(), tabPages.getTabCount());
        viewPages.setAdapter(slidePagerSearchAdapter);
        viewPages.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabPages));
        tabPages.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPages.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPages.setCurrentItem(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.searchHistoryItem:
                startActivity(new Intent(SearchActivity.this, SearchHistoryActivity.class));
                break;
        }
        return true;
    }

}

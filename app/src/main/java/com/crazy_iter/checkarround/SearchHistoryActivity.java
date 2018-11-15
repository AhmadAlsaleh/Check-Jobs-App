package com.crazy_iter.checkarround;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.crazy_iter.checkarround.Adapters.SearchHistoryAdapter;
import com.crazy_iter.checkarround.Data.SearchHistoryItem;

import java.util.ArrayList;

public class SearchHistoryActivity extends AppCompatActivity {

    private RecyclerView searchHistoryRV;
    private ArrayList<SearchHistoryItem> searchHistoryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search_history);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search History");
        searchHistoryRV = findViewById(R.id.searchResultRV);

        searchHistoryItems = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            searchHistoryItems.add(new SearchHistoryItem("", "Word:"+i,
                    "place:"+i, "100 - 150 $", "5 Hours PM", "6 Days", "Male"));
        }
        searchHistoryRV.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        searchHistoryRV.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new SearchHistoryAdapter(SearchHistoryActivity.this, searchHistoryItems);
        searchHistoryRV.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.clearSearchHistoryItem:
                Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
                this.finish();
                break;
        }
        return true;
    }

}

package com.crazy_iter.checkarround;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.crazy_iter.checkarround.Adapters.SpotsAdapter;
import com.crazy_iter.checkarround.Data.JobItem;

import java.util.ArrayList;

public class StaredSpotsActivity extends AppCompatActivity {

    private RecyclerView staredSpotRV;
    private ArrayList<JobItem> staredJobItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stared_spots);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Stared Spots");
        staredSpotRV = findViewById(R.id.staredSpotRV);
        staredJobItems = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            staredJobItems.add(new JobItem("" + i, "position: " + i, 0.0, 0.0,
//                    "", "", "company: " + i, "", "", true, "desc", 1));
//        }
        showStaredSpots();

    }

    private void showStaredSpots() {
        staredSpotRV.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(StaredSpotsActivity.this, LinearLayoutManager.VERTICAL, false);
        staredSpotRV.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new SpotsAdapter(StaredSpotsActivity.this, staredJobItems);
        staredSpotRV.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}

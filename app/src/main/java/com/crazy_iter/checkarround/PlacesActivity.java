package com.crazy_iter.checkarround;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.crazy_iter.checkarround.Adapters.PlacesAdapter;
import com.crazy_iter.checkarround.Data.Places;
import com.crazy_iter.checkarround.Dialogs.DialogAddNewPlace;
import com.crazy_iter.checkarround.StaticsAndAPIs.API_URLs;
import com.crazy_iter.checkarround.StaticsAndAPIs.StaticCodes;
import com.crazy_iter.checkarround.StaticsAndAPIs.StaticString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlacesActivity extends AppCompatActivity implements StaticCodes, StaticString, API_URLs {

    private FloatingActionButton addPlaceFAB;
    private RecyclerView myPlacesRV;
    private RequestQueue requestQueue;
    private int retryNum = 0;
    private RelativeLayout loadingPlaceRL;
    public static LoadingLayout loadingLayout;
    private boolean fromChoose = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_places);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.my_places);

        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle.getBoolean("choose")) {
                fromChoose = true;
            }
        } catch (Exception ignored) {}

        addPlaceFAB = findViewById(R.id.addPlaceFAB);
        myPlacesRV = findViewById(R.id.myPlacesRV);
        loadingPlaceRL = findViewById(R.id.loadingPlacesRL);
        requestQueue = Volley.newRequestQueue(this);
        loadingLayout = new LoadingLayout(this);
        loadingPlaceRL.addView(loadingLayout.getLoading());

        loadMyPlaces();

        addPlaceFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogAddNewPlace(PlacesActivity.this).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (fromChoose) {
            finish();
            startActivity(new Intent(this, AddSpotActivity.class));
        } else {
            finish();
        }
    }

    private void setPlaces(JSONArray places) {
        ArrayList<Places> placesArrayList = new ArrayList<>();
        try {
            for (int i = 0; i < places.length(); i++) {
                JSONObject jsonObject = places.getJSONObject(i);
                placesArrayList.add(new Places(
                        jsonObject.getString(S_ID),
                        jsonObject.getString(S_TITLE.toLowerCase()),
                        jsonObject.getDouble(S_Latitude.toLowerCase()),
                        jsonObject.getDouble(S_Longitude.toLowerCase())
                ));
            }
        } catch (JSONException e) {
            Log.e("setPlaces", e.getMessage());
        }
        myPlacesRV.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PlacesActivity.this, LinearLayoutManager.VERTICAL, false);
        myPlacesRV.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new PlacesAdapter(placesArrayList, PlacesActivity.this);
        myPlacesRV.setAdapter(adapter);
        loadingLayout.hideLoading();
    }

    private void loadMyPlaces() {
        loadingLayout.showLoading();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(JsonArrayRequest.Method.GET, GET_MY_ALL_PLACES + StartActivity.user.getId(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        setPlaces(jsonArray);
                        requestQueue.cancelAll(S_GET_PLACES);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_GET_PLACES);
                if (retryNum < RETRY_NUM) {
                    retryNum++;
                    loadMyPlaces();
                } else {
                    Toast.makeText(PlacesActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonArrayRequest.setTag(S_GET_PLACES);
        requestQueue.add(jsonArrayRequest);
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

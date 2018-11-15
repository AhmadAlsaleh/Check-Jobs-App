package com.crazy_iter.checkarround.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.crazy_iter.checkarround.Adapters.PlacesAdapter;
import com.crazy_iter.checkarround.Data.Places;
import com.crazy_iter.checkarround.LoadingLayout;
import com.crazy_iter.checkarround.PlacesActivity;
import com.crazy_iter.checkarround.R;
import com.crazy_iter.checkarround.StartActivity;
import com.crazy_iter.checkarround.StaticsAndAPIs.API_URLs;
import com.crazy_iter.checkarround.StaticsAndAPIs.StaticCodes;
import com.crazy_iter.checkarround.StaticsAndAPIs.StaticString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by CrazyITer on 5/18/2018.
 */

public class DialogSelectPlace extends Dialog implements API_URLs, StaticString, StaticCodes {

    private Context context;
    private Button manageYourPlaces;
    private RelativeLayout loadingRL;
    private RecyclerView placesRV;
    private LoadingLayout loadingLayout;
    private RequestQueue requestQueue;
    private int retryNum = 0;
    public static Places selectedPlace = null;
    public static TextView textView;

    public DialogSelectPlace(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_place);

        requestQueue = Volley.newRequestQueue(context);
        manageYourPlaces = findViewById(R.id.manageYourPlacesBTN);
        placesRV = findViewById(R.id.selectPlaceRV);
        loadingRL = findViewById(R.id.loadingSelectPlaceRL);
        loadingLayout = new LoadingLayout(context);
        loadingRL.addView(loadingLayout.getLoading());

        loadMyPlaces();

        manageYourPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent(context, PlacesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("choose", true);
                intent.putExtras(bundle);
                ((Activity) context).finish();
                context.startActivity(intent);
            }
        });

        textView = new TextView(context);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ("done".equals(s.toString())) {
                    dismiss();
                }
            }
        });

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
        placesRV.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        placesRV.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new PlacesAdapter(placesArrayList, context, true);
        placesRV.setAdapter(adapter);
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
                    Toast.makeText(context, R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonArrayRequest.setTag(S_GET_PLACES);
        requestQueue.add(jsonArrayRequest);
    }
}

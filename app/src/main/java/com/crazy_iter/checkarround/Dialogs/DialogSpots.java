package com.crazy_iter.checkarround.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.crazy_iter.checkarround.Adapters.SpotsAdapter;
import com.crazy_iter.checkarround.Data.JobItem;
import com.crazy_iter.checkarround.LoadingLayout;
import com.crazy_iter.checkarround.R;
import com.crazy_iter.checkarround.StartActivity;
import com.crazy_iter.checkarround.StaticsAndAPIs.API_URLs;
import com.crazy_iter.checkarround.StaticsAndAPIs.StaticCodes;
import com.crazy_iter.checkarround.StaticsAndAPIs.StaticString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by CrazyITer on 5/22/2018.
 */

public class DialogSpots extends Dialog implements API_URLs, StaticString, StaticCodes {

    private Context context;
    private RecyclerView spotsRV;
    private RelativeLayout loadingRL;
    private LoadingLayout loadingLayout;
    private RequestQueue requestQueue;
    private int retryNum = 0;
    public static TextView textView;
    public static boolean isStarred = false, isAll = false;
    private Pair<String, String> placeIDUserID;

    public DialogSpots(@NonNull Context context, Pair<String, String> placeIDUserID) {
        super(context);
        this.context = context;
        this.placeIDUserID = placeIDUserID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_spots);

        requestQueue = Volley.newRequestQueue(context);
        loadingLayout = new LoadingLayout(context);
        spotsRV = findViewById(R.id.dialogSpotsRV);
        loadingRL = findViewById(R.id.loadingSpotsDialogRL);
        loadingRL.addView(loadingLayout.getLoading());
        loadingLayout.showLoading();

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
                    loadSpots();
                } else if ("load".equals(s.toString())) {
                    if (loadingLayout.getLoading().getVisibility() == View.VISIBLE) {
                        loadingLayout.hideLoading();
                    } else {
                        loadingLayout.showLoading();
                    }
                }
            }
        });

        loadSpots();

    }

    private void loadSpots() {
        retryNum = 0;
        loadingLayout.showLoading();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(S_PLACE_ID, placeIDUserID.first);
            jsonObject.put(S_UserID, StartActivity.user == null ? "" : StartActivity.user.getId());
            jsonObject.put("isStarred", isStarred);
            jsonObject.put("isAll", isAll);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(JsonArrayRequest.Method.POST, GET_SPOTS_BY_PLACE, jsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        setSpots(jsonArray);
                        requestQueue.cancelAll(S_STARRED);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_STARRED);
                if (retryNum < RETRY_NUM) {
                    retryNum++;
                    loadSpots();
                } else {
                    dismiss();
                    Toast.makeText(context, R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonArrayRequest.setTag(S_STARRED);
        requestQueue.add(jsonArrayRequest);
    }

    private void setSpots(JSONArray jsonArray) {
        loadingLayout.hideLoading();
        if (jsonArray.length() == 0) {
            dismiss();
            return;
        }
        ArrayList<JobItem> jobItems = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JobItem jobItem = new JobItem(
                        jsonObject.getString(S_ID.toLowerCase()),
                        jsonObject.getString(S_TITLE.toLowerCase()),
                        jsonObject.getDouble(S_LOW_SAL),
                        jsonObject.getDouble(S_HIGH_SAL),
                        (jsonObject.getInt(S_MINUTES.toLowerCase()) / 60) + " " + context.getString(R.string.hours_show)
                                + " " + (jsonObject.getBoolean(S_IS_PM) ? context.getString(R.string.pm) : context.getString(R.string.am)),
                        jsonObject.getInt(S_DAYS) + " " + context.getString(R.string.days),
                        jsonObject.getString(S_USER_NAME),
                        jsonObject.getString(S_PLACE_TITLE),
                        jsonObject.getString(S_PHONE.toLowerCase()),
                        (StartActivity.user != null && jsonObject.getBoolean(S_IS_STARED)),
                        jsonObject.getString(S_DESCRIPTION),
                        jsonObject.getBoolean(S_IS_DONE),
                        jsonObject.getString(S_GENDER));
                jobItem.setPlaceID(placeIDUserID.first);
                jobItems.add(jobItem);
            }
        } catch (JSONException e) {
            Log.e("error", e.getMessage());
        }
        spotsRV.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        spotsRV.setLayoutManager(layoutManager);
        if (Objects.equals(placeIDUserID.second, (StartActivity.user == null ? "" : StartActivity.user.getId()))) {
            RecyclerView.Adapter adapter = new SpotsAdapter(context, jobItems, true);
            spotsRV.setAdapter(adapter);
        } else {
            RecyclerView.Adapter adapter = new SpotsAdapter(context, jobItems);
            spotsRV.setAdapter(adapter);
        }
    }
}

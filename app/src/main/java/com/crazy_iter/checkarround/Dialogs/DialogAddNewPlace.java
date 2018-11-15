package com.crazy_iter.checkarround.Dialogs;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crazy_iter.checkarround.Data.Places;
import com.crazy_iter.checkarround.PlacesActivity;
import com.crazy_iter.checkarround.R;
import com.crazy_iter.checkarround.StartActivity;
import com.crazy_iter.checkarround.StaticsAndAPIs.API_URLs;
import com.crazy_iter.checkarround.StaticsAndAPIs.StaticCodes;
import com.crazy_iter.checkarround.StaticsAndAPIs.StaticString;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by CrazyITer on 4/9/2018.
 */

public class DialogAddNewPlace extends Dialog implements StaticCodes, StaticString, API_URLs, OnMapReadyCallback {

    private Context context;
    private TextInputLayout newPlaceTitle;
    private LatLng placeLatLng = null;
    private Button doneNewPlace;
    private MapView mapView;
    private GoogleMap mMap;
    private Places places = null;
    private RequestQueue requestQueue;
    private boolean isNew = false;
    private int tryNum = 0;

    public DialogAddNewPlace(@NonNull Context context) {
        super(context);
        this.context = context;
        this.isNew = true;
        requestQueue = Volley.newRequestQueue(context);
    }

    public DialogAddNewPlace(@NonNull Context context, Places places) {
        super(context);
        this.isNew = false;
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        this.places = places;
        this.placeLatLng = new LatLng(places.getLat(), places.getLon());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_new_place);

        mapView = findViewById(R.id.newPlaceMV);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
        doneNewPlace = findViewById(R.id.doneNewPlace);
        newPlaceTitle = findViewById(R.id.newPlaceTitle);

        if (isNew) {
            newPlaceTitle.getEditText().setText("");
        } else {
            newPlaceTitle.getEditText().setText(places.getTitle());
        }
        doneNewPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNew) {
                    addPlace();
                } else {
                    editPlace();
                }
            }
        });
    }

    private void addPlace() {
        if (placeLatLng == null) {
            Toast.makeText(context, R.string.drag_on_map, Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPlaceTitle.getEditText().getText().length() != 0) {
            PlacesActivity.loadingLayout.showLoading();
            dismiss();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(S_TITLE, newPlaceTitle.getEditText().getText().toString());
                jsonObject.put(S_Latitude, placeLatLng.latitude);
                jsonObject.put(S_Longitude, placeLatLng.longitude);
                jsonObject.put(S_UserID, StartActivity.user.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, ADD_PLACE, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Toast.makeText(context, R.string.added, Toast.LENGTH_SHORT).show();
                            requestQueue.cancelAll(S_ADD);
                            ((Activity) context).recreate();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    requestQueue.cancelAll(S_ADD);
                    if (tryNum < RETRY_NUM) {
                        tryNum++;
                        addPlace();
                    } else {
                        Toast.makeText(context, R.string.try_again, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            jsonObjectRequest.setTag(S_ADD);
            requestQueue.add(jsonObjectRequest);

        } else {
            newPlaceTitle.setError(context.getString(R.string.field_is_required));
        }

    }

    private void editPlace() {
        if (newPlaceTitle.getEditText().getText().length() != 0) {
            dismiss();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(S_ID, places.getId());
                jsonObject.put(S_TITLE, newPlaceTitle.getEditText().getText().toString());
                jsonObject.put(S_Latitude, placeLatLng.latitude);
                jsonObject.put(S_Longitude, placeLatLng.longitude);
                jsonObject.put(S_UserID, StartActivity.user.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("edit JSON", jsonObject.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, EDIT_PLACE, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Toast.makeText(context, "Edited", Toast.LENGTH_SHORT).show();
                            requestQueue.cancelAll(S_EDIT);
                            ((Activity) context).recreate();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    requestQueue.cancelAll(S_EDIT);
                    if (tryNum < RETRY_NUM) {
                        tryNum++;
                        editPlace();
                    } else {
                        Toast.makeText(context, "Try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            jsonObjectRequest.setTag(S_EDIT);
            requestQueue.add(jsonObjectRequest);
        } else {
            newPlaceTitle.setError("Field is required");
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(context);
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        if (this.placeLatLng != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(placeLatLng)
                    .title(""));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 15));
            mMap.addCircle(new CircleOptions()
                    .center(placeLatLng)
                    .radius(200)
                    .strokeColor(context.getResources().getColor(R.color.lightPrimary)).strokeWidth(1)
                    .fillColor(context.getResources().getColor(R.color.colorAccentLight)));
        }
//        else {
//            LatLng latLng = new LatLng(35.5, 35.5);
//            mMap.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .title(""));
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//            mMap.addCircle(new CircleOptions()
//                    .center(latLng)
//                    .radius(200)
//                    .strokeColor(context.getResources().getColor(R.color.lightPrimary)).strokeWidth(1)
//                    .fillColor(context.getResources().getColor(R.color.colorAccentLight)));
//        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(""));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                mMap.addCircle(new CircleOptions()
                        .center(latLng)
                        .radius(200)
                        .strokeColor(context.getResources().getColor(R.color.lightPrimary)).strokeWidth(1)
                        .fillColor(context.getResources().getColor(R.color.colorAccentLight2)));
                placeLatLng = latLng;
            }
        });

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
}

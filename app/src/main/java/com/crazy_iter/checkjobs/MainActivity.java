package com.crazy_iter.checkjobs;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crazy_iter.checkjobs.Dialogs.DialogFilter;
import com.crazy_iter.checkjobs.Dialogs.DialogSpots;
import com.crazy_iter.checkjobs.StaticsAndAPIs.API_URLs;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticCodes;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticString;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        StaticCodes, StaticString, API_URLs {

    private FloatingActionButton addSpotFAB;
    private MapView mapView;
    private static GoogleMap mMap;
    private ImageView layerMainMapIV, starredMainMapIV, refreshMainIV;
    private RequestQueue requestQueue;
    private int retryNum = 0;
    private RelativeLayout loadingMainRL;
    public static LoadingLayout loadingLayout;
    private TextView filteredMainTV;
    private boolean firstShow = true;
    private Map<LatLng, Pair<String, String>> placesMap;
    private DialogSpots dialogSpots;
    private DialogFilter dialogFilter;
    private boolean showDirections = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        loadingMainRL = findViewById(R.id.loadingMainRL);
        loadingLayout = new LoadingLayout(MainActivity.this);
        loadingMainRL.addView(loadingLayout.getLoading());
        placesMap = new HashMap<>();

        mapView = findViewById(R.id.nearSpotsMV);
        addSpotFAB = findViewById(R.id.addSpotFAB);
        layerMainMapIV = findViewById(R.id.layersMainMapIV);
        starredMainMapIV = findViewById(R.id.starredMapIV);
        refreshMainIV = findViewById(R.id.refreshMainIV);
        filteredMainTV = findViewById(R.id.filteredMainTV);
        requestQueue = Volley.newRequestQueue(this);
        dialogFilter = new DialogFilter(this);

        // region set map view
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
        // endregion

        // region add fab
        checkAddFab();
        checkStarred();

        addSpotFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StartActivity.user.isPro()) {
                    startActivity(new Intent(MainActivity.this, AddSpotActivity.class));
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.upgrade_account)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    upgradeToPro();
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .create()
                            .show();
                }
            }
        });
        // endregion

        // region layer main map
        layerMainMapIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });
        // endregion

        // region starred main map
        starredMainMapIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestQueue.cancelAll(S_GET_SPOTS);
                loadStaredSpots();
            }
        });
        // endregion

        // region refresh main map
        refreshMainIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshMain();
            }
        });
        // endregion

        dialogFilter.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (DialogFilter.isGoodDis) {
                    loadFilterPlaces(dialogFilter.filterRequest());
                }
            }
        });

    }

    // region load

    private void loadFilterPlaces(final JSONObject jsonObject) {
        DialogSpots.isAll = true;
        DialogSpots.isStarred = false;
        requestQueue.cancelAll(S_GET_SPOTS);
        requestQueue.cancelAll(S_STARRED);
        requestQueue.cancelAll(S_GET_PLACES);
        requestQueue.cancelAll(S_FILTER);
        loadingLayout.showLoading();
        firstShow = false;
        showDirections = false;
        filteredMainTV.setText(R.string.filtered);
        retryNum = 0;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(JsonArrayRequest.Method.POST, GET_FILTER_PLACES, jsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        setAllPlaces(jsonArray, false);
                        requestQueue.cancelAll(S_FILTER);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_FILTER);
                if (retryNum < RETRY_NUM) {
                    retryNum++;
                    loadFilterPlaces(jsonObject);
                } else {
                    Toast.makeText(MainActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonArrayRequest.setTag(S_FILTER);
        requestQueue.add(jsonArrayRequest);
    }

    private void loadStaredSpots() {
        DialogSpots.isAll = false;
        DialogSpots.isStarred = true;
        requestQueue.cancelAll(S_GET_PLACES);
        requestQueue.cancelAll(S_STARRED);
        requestQueue.cancelAll(S_GET_SPOTS);
        requestQueue.cancelAll(S_FILTER);
        loadingLayout.showLoading();
        retryNum = 0;
        firstShow = false;
        showDirections = false;
        filteredMainTV.setText(R.string.starred);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(JsonArrayRequest.Method.GET, GET_MY_STARRED_PLACES + StartActivity.user.getId(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        setAllPlaces(jsonArray, false);
                        requestQueue.cancelAll(S_STARRED);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_STARRED);
                if (retryNum < RETRY_NUM) {
                    retryNum++;
                    loadMyPlaces();
                } else {
                    Toast.makeText(MainActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonArrayRequest.setTag(S_STARRED);
        requestQueue.add(jsonArrayRequest);
    }

    private void loadMyPlaces() {
        DialogSpots.isAll = false;
        DialogSpots.isStarred = false;
        requestQueue.cancelAll(S_GET_SPOTS);
        requestQueue.cancelAll(S_STARRED);
        requestQueue.cancelAll(S_GET_PLACES);
        requestQueue.cancelAll(S_FILTER);
        loadingLayout.showLoading();
        firstShow = false;
        showDirections = false;
        filteredMainTV.setText(R.string.my_places);
        retryNum = 0;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(JsonArrayRequest.Method.GET, GET_MY_MAP_PLACES + StartActivity.user.getId(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        setAllPlaces(jsonArray, true);
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
                    Toast.makeText(MainActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonArrayRequest.setTag(S_GET_PLACES);
        requestQueue.add(jsonArrayRequest);
    }

    private void loadAllPlaces() {
        DialogSpots.isAll = true;
        DialogSpots.isStarred = false;
        filteredMainTV.setText(R.string.all);
        requestQueue.cancelAll(S_GET_PLACES);
        requestQueue.cancelAll(S_STARRED);
        requestQueue.cancelAll(S_GET_SPOTS);
        requestQueue.cancelAll(S_FILTER);
        loadingLayout.showLoading();
        retryNum = 0;
        showDirections = false;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(JsonArrayRequest.Method.GET, GET_ALL_PLACES,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        setAllPlaces(jsonArray, false);
                        requestQueue.cancelAll(S_GET_SPOTS);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_GET_SPOTS);
                loadAllPlaces();
            }
        });
        jsonArrayRequest.setTag(S_GET_SPOTS);
        requestQueue.add(jsonArrayRequest);
    }

    // endregion

    private void upgradeToPro() {
        loadingLayout.showLoading();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, UPGRADE_TO_PRO + StartActivity.user.getId(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        StartActivity.user.setPro(true);
                        addSpotFAB.setImageDrawable(getDrawable(R.drawable.ic_add));
                        loadingLayout.hideLoading();
                        requestQueue.cancelAll(S_UPGRADE_TO_PRO);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_UPGRADE_TO_PRO);
                upgradeToPro();
            }
        });
        jsonObjectRequest.setTag(S_UPGRADE_TO_PRO);
        requestQueue.add(jsonObjectRequest);
    }

    private void setAllPlaces(JSONArray jsonArray, boolean isForMe) {
        placesMap.clear();
        mMap.clear();
        loadingLayout.hideLoading();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                if (isForMe) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    mMap.addMarker(new MarkerOptions()
                            .position(
                                    new LatLng(jsonObject.getDouble(S_Latitude.toLowerCase()),
                                            jsonObject.getDouble(S_Longitude.toLowerCase()))
                            )
                            .title(
                                    jsonObject.getString(S_TITLE.toLowerCase()) + ", "
                                            + StartActivity.user.getName()
                            )
                    );

                    placesMap.put(
                            new LatLng(jsonObject.getDouble(S_Latitude.toLowerCase()),
                                    jsonObject.getDouble(S_Longitude.toLowerCase())),
                            new Pair<>(jsonObject.getString(S_ID), StartActivity.user.getId())
                    );
                } else {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    mMap.addMarker(new MarkerOptions()
                            .position(
                                    new LatLng(jsonObject.getDouble(S_Latitude.toLowerCase()),
                                            jsonObject.getDouble(S_Longitude.toLowerCase()))
                            )
                            .title(
                                    jsonObject.getString(S_TITLE.toLowerCase()) + ", "
                                            + jsonObject.getString(S_NAME)
                            )
                    );

                    placesMap.put(
                            new LatLng(jsonObject.getDouble(S_Latitude.toLowerCase()),
                                    jsonObject.getDouble(S_Longitude.toLowerCase())),
                            new Pair<>(jsonObject.getString(S_ID), jsonObject.getString("userID"))
                    );
                }
            } catch (JSONException e) {
                Log.e("get all places error", e.getMessage());
            }
        }
    }

    private void checkAddFab() {
        if (StartActivity.user == null) {
            addSpotFAB.setVisibility(View.GONE);
        } else if(StartActivity.user.isPro()) {
            addSpotFAB.setVisibility(View.VISIBLE);
            addSpotFAB.setImageDrawable(getDrawable(R.drawable.ic_add));
        } else {
            addSpotFAB.setVisibility(View.VISIBLE);
            addSpotFAB.setImageDrawable(getDrawable(R.drawable.ic_unfold_more));
        }
    }

    private void checkStarred() {
        if (StartActivity.user == null) {
            starredMainMapIV.setVisibility(View.GONE);
        } else {
            starredMainMapIV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this);
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!showDirections) {
                    dialogSpots = new DialogSpots(MainActivity.this, placesMap.get(marker.getPosition()));
                    dialogSpots.show();
                    dialogSpots.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            loadAllPlaces();
                        }
                    });
                } else {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps" +
                                    "?daddr=" + marker.getPosition().latitude + "," + marker.getPosition().longitude));
                    startActivity(intent);
                }
                return true;
            }
        });


//        mMap.addMarker(new MarkerOptions().position(new LatLng(35.521416, 35.774128)));

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (!firstShow) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                } else {
                    mMap.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(
                                    new LatLng(location.getLatitude(),
                                            location.getLongitude()
                                    ),
                                    ZOOM_MAP
                            )
                    );
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!(getString(R.string.all).equals(filteredMainTV.getText().toString()))) {
            firstShow = true;
            showDirections = true;
            checkAddFab();
            checkStarred();
            loadAllPlaces();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.sure_to_exit);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).setNegativeButton(R.string.no, null)
                    .create()
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        firstShow = true;
        showDirections = false;
        checkAddFab();
        checkStarred();
        loadAllPlaces();
    }

    public void toSearch(View view) {
        // startActivity(new Intent(MainActivity.this, SearchActivity.class));
        dialogFilter.show();
    }

    public void toProfile(View view) {
        if (StartActivity.user == null) {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        } else {
            if (StartActivity.user.isPro()) {
                loadMyPlaces();
            } else {
                Toast.makeText(this, R.string.upgrade_your_account, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void toSettings(View view) {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    public void refreshMain() {
        Toast.makeText(this, R.string.loading, Toast.LENGTH_SHORT).show();
        firstShow = false;
        loadAllPlaces();
    }

    public void toDirections(View view) {
        filteredMainTV.setText(R.string.find_directions);
        Toast.makeText(this, R.string.select_a_olace_to_find_road, Toast.LENGTH_SHORT).show();
        showDirections = true;
    }
}

package com.crazy_iter.checkjobs;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crazy_iter.checkjobs.Adapters.SpotsAdapter;
import com.crazy_iter.checkjobs.Data.JobItem;
import com.crazy_iter.checkjobs.Dialogs.DialogCall;
import com.crazy_iter.checkjobs.Dialogs.DialogEdit;
import com.crazy_iter.checkjobs.StaticsAndAPIs.API_URLs;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticCodes;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticMethods;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticString;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements OnMapReadyCallback, StaticCodes, StaticString, API_URLs {

    private MapView profileMapView;
    private GoogleMap mMap;
    private String phoneNumber = "0951784957";
    private LinearLayout makeCallLL, sendMessageLL, editProfileLL, yourSpotsLL, showNumberLL, staredSpotsLL;
    private CircleImageView profileIV;
    private RecyclerView yourSpots;
    private ImageView arrowYourSpotsIV;
    private TextView profileDetails;
    private CircleImageView profileIVDialog;
    private Bitmap profileImage;
    private FloatingActionButton fabAddSpot;
    private RequestQueue requestQueue;
    private int retryNum = 0;
    private int retryNumImage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(StartActivity.user.getName());

        requestQueue = Volley.newRequestQueue(this);

        fabAddSpot = findViewById(R.id.fabAddSpotProfile);
        fabAddSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StartActivity.user.isPro()) {
                    startActivity(new Intent(ProfileActivity.this, AddSpotActivity.class));
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setMessage("Upgrade to Work Owner account?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    upgradeToPro();
                                }
                            })
                            .setNegativeButton("No", null)
                            .create()
                            .show();
                }
            }
        });

        if (StartActivity.user.isPro()) {
            fabAddSpot.setImageDrawable(getDrawable(R.drawable.ic_add));
        } else {
            fabAddSpot.setImageDrawable(getDrawable(R.drawable.ic_unfold_more));
        }

        editProfileLL = findViewById(R.id.editProfileLL);
        editProfileLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
            }
        });
        showNumberLL = findViewById(R.id.showNumberLL);
        staredSpotsLL = findViewById(R.id.staredSpotsLL);
        yourSpotsLL = findViewById(R.id.yourSpotsLL);
        profileDetails = findViewById(R.id.profileDetails);
        profileDetails.setText(StartActivity.user.getDetails());
        loadYouSpots();
        //        yourSpotsLL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (yourSpots.getVisibility() == View.VISIBLE) {
//                    yourSpots.setVisibility(View.GONE);
//                    arrowYourSpotsIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_up));
//                } else {
//                    yourSpots.setVisibility(View.VISIBLE);
//                    arrowYourSpotsIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_down));
//                    loadYouSpots();
//                }
//            }
//        });

        staredSpotsLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, StaredSpotsActivity.class));
            }
        });

        yourSpots = findViewById(R.id.yourSpotsRV);
        arrowYourSpotsIV = findViewById(R.id.arrowYouSpotsIV);

        makeCallLL = findViewById(R.id.makeCallLL);
        profileIV = findViewById(R.id.profileIV);
        makeCallLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNumber.length() == 0) {
                    Toast.makeText(ProfileActivity.this, "Phone not available", Toast.LENGTH_SHORT).show();
                } else {
                    if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestCall();
                    }
                    new DialogCall(ProfileActivity.this, phoneNumber).show();
                }
            }
        });
        sendMessageLL = findViewById(R.id.sendMessageLL);
        sendMessageLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNumber.length() == 0) {
                    Toast.makeText(ProfileActivity.this, "Phone not available", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(phoneNumber);
                }
            }
        });
        profileMapView = findViewById(R.id.profileMap);
        if (profileMapView != null) {
            profileMapView.onCreate(null);
            profileMapView.onResume();
            profileMapView.getMapAsync(this);
        }

        changeProfileImage();
        loadMyPlaces();

    }

    private void changeProfileImage() {
        profileIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                View imageDialog = getLayoutInflater().inflate(R.layout.dialog_profile_image, null);
                builder.setView(imageDialog);
                final Dialog dialog = builder.create();
                dialog.show();

                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        profileImage = null;
                    }
                });

                final int imagePadding = (int) getResources().getDimension(R.dimen.profile_image_dialog);

                TextView changeProfileImageDialog = imageDialog.findViewById(R.id.changeImageDialog),
                        removeProfileImageDialog = imageDialog.findViewById(R.id.removeImageDialog);
                Button saveProfileImage = imageDialog.findViewById(R.id.saveProfileImageDialog);
                profileIVDialog = imageDialog.findViewById(R.id.profileIVDialog);

                removeProfileImageDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        profileIVDialog.setImageDrawable(getDrawable(R.drawable.ic_camera));
                        profileIVDialog.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
                        profileImage = null;
                    }
                });

                changeProfileImageDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent selectImage = new Intent();
                        selectImage.setType("image/*");
                        selectImage.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(selectImage, IMAGE_CODE);
                    }
                });

                saveProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        saveNewImage();
                    }
                });
                profileIVDialog.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
            }
        });
    }

    private void saveNewImage() {
        if (profileImage != null) {
            String myImage = StaticMethods.imageToString(
                    StaticMethods.getResizedBitmap(
                            profileImage, StaticCodes.IMAGES_SIZE
                    )
            );
            Log.e("new image", myImage);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(S_ID, StartActivity.user.getId());
                jsonObject.put(S_IMAGE_STR, myImage);
            } catch (JSONException e) {
                Log.e("new image", e.getMessage());
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, EDIT_IMAGE, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                Picasso.with(ProfileActivity.this)
                                        .load(MAIN_IMAGES + StartActivity.user.getId() + ".jpg")
                                        .into(profileIV);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.e("image", MAIN_IMAGES + StartActivity.user.getId() + ".jpg");
                            requestQueue.cancelAll(S_EDIT_IMAGE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    requestQueue.cancelAll(S_EDIT_IMAGE);
                    if (retryNumImage < RETRY_NUM) {
                        retryNumImage++;
                        saveNewImage();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            jsonObjectRequest.setTag(S_EDIT_IMAGE);
            requestQueue.add(jsonObjectRequest);
        } else {
            Toast.makeText(this, "Select your image", Toast.LENGTH_SHORT).show();
        }
    }

    private void upgradeToPro() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, UPGRADE_TO_PRO + StartActivity.user.getId(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        StartActivity.user.setPro(true);
                        requestQueue.cancelAll(S_UPGRADE_TO_PRO);
                        ProfileActivity.this.recreate();
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

    private void loadYouSpots() {
        Log.e("my spots", GET_MY_SPOTS + StartActivity.user.getId());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(JsonArrayRequest.Method.GET, GET_MY_SPOTS + StartActivity.user.getId(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        showYourSpots(jsonArray);
                        requestQueue.cancelAll(S_GET_SPOTS);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_GET_SPOTS);
                loadYouSpots();
            }
        });
        jsonArrayRequest.setTag(S_GET_SPOTS);
        requestQueue.add(jsonArrayRequest);
    }

    private void showYourSpots(JSONArray jsonArray) {
        if (jsonArray.length() == 0) {
            return;
        }
        ArrayList<JobItem> jobItems = new ArrayList<>();
        try {
            jobItems.add(new JobItem("",
                    "title",
                    10,
                    100,
                    "6 hours",
                    "6 days",
                    "Ahmad",
                    "Place A",
                    "",
                    false,
                    "DESCRIPTION",
                    false,
                    "Male"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                jobItems.add(new JobItem(
                        jsonObject.getString(S_ID.toLowerCase()),
                        jsonObject.getString(S_TITLE.toLowerCase()),
                        jsonObject.getDouble(S_LOW_SAL),
                        jsonObject.getDouble(S_HIGH_SAL),
                        (jsonObject.getInt(S_MINUTES.toLowerCase()) / 60) + " hours " + (jsonObject.getBoolean(S_IS_PM) ? "PM" : "AM"),
//                        (jsonObject.getInt(S_DAYS) == null ? "" : jsonObject.getInt(S_DAYS) + " days"),
                        "days",
                        jsonObject.getString(S_USER_NAME),
                         jsonObject.getString(S_PLACE_TITLE),
                        jsonObject.getString(S_PHONE.toLowerCase()),
                        false,
                        jsonObject.getString(S_DESCRIPTION),
                        jsonObject.getBoolean(S_IS_DONE),
                        jsonObject.getString(S_GENDER)));
            }
        } catch (JSONException e) {
            Log.e("error", e.getMessage());
        }
        yourSpots.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ProfileActivity.this, LinearLayoutManager.VERTICAL, false);
        yourSpots.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new SpotsAdapter(ProfileActivity.this, jobItems, true);
        yourSpots.setAdapter(adapter);
    }

    private void showEditDialog() {
        new DialogEdit(ProfileActivity.this).show();
    }

    public void sendMessage(String num) {
        Uri uri = Uri.parse("smsto:"+num);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(Intent.createChooser(it, "Send by ..."));
    }

    private void requestCall() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                Manifest.permission.CALL_PHONE)) {
            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Permission needed")
                    .setMessage("needed to continue")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(ProfileActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE}, CALL_PER_CODE);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PER_CODE);
        }
    }

    private void loadMyPlaces() {
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
                    Toast.makeText(ProfileActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonArrayRequest.setTag(S_GET_PLACES);
        requestQueue.add(jsonArrayRequest);
    }

    private void setPlaces(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                mMap.addMarker(new MarkerOptions().position(
                        new LatLng(
                        jsonObject.getDouble(S_Latitude.toLowerCase()),
                        jsonObject.getDouble(S_Longitude.toLowerCase()))
                ));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CALL_PER_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Toast.makeText(this, "Permission Graded", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                profileImage = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                profileIVDialog.setImageBitmap(StaticMethods.getCircleBitmap(
                        StaticMethods.getResizedBitmap(profileImage, IMAGES_SIZE)
                ));
                profileIVDialog.setPadding(0, 0, 0, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this);
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //        LatLng adPlace = new LatLng(35.520671, 35.8041968);
//        mMap.addMarker(new MarkerOptions().position(adPlace).title(""));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(adPlace, 15));
//        mMap.addCircle(new CircleOptions()
//                .center(adPlace)
//                .radius(200)
//                .strokeColor(getResources().getColor(R.color.lightPrimary)).strokeWidth(1)
//                .fillColor(getResources().getColor(R.color.colorAccentLight)));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), ZOOM_MAP));
            }
        });
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
                this.finish();
                break;
        }

        return true;
    }

}

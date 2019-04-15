package com.crazy_iter.checkjobs;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crazy_iter.checkjobs.Data.JobItem;
import com.crazy_iter.checkjobs.Dialogs.DialogCall;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticCodes;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticString;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import static com.crazy_iter.checkjobs.StaticsAndAPIs.StaticCodes.CALL_PER_CODE;

public class JobMapActivity extends AppCompatActivity implements OnMapReadyCallback, StaticString {

    private GoogleMap mMap;
    private FloatingActionButton moreFAB, directionsFAB;
    private RelativeLayout hideMapRL;
    private String phoneNumber = "0951784957";
    public static JobItem showSpot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_job_map);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(showSpot.getPosition());

        MapView mapView = findViewById(R.id.jobMapView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
        LinearLayout showNumberLL = findViewById(R.id.showNumberLL);
        LinearLayout makeCallLL = findViewById(R.id.makeCallLL);
        LinearLayout sendMessageLL = findViewById(R.id.sendMessageLL);
        moreFAB = findViewById(R.id.moreFAB);
        directionsFAB = findViewById(R.id.directionsFAB);
        hideMapRL = findViewById(R.id.hideMapRL);
        hideMapRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreFAB.setVisibility(View.VISIBLE);
                directionsFAB.setVisibility(View.VISIBLE);
                hideMapRL.setVisibility(View.GONE);
            }
        });
        moreFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreFAB.setVisibility(View.GONE);
                directionsFAB.setVisibility(View.GONE);
                hideMapRL.setVisibility(View.VISIBLE);
            }
        });
        ImageView layersIV = findViewById(R.id.layersIV);
        layersIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });
        makeCallLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNumber.length() == 0) {
                    Toast.makeText(JobMapActivity.this, "Phone not available", Toast.LENGTH_SHORT).show();
                } else {
                    if (ContextCompat.checkSelfPermission(JobMapActivity.this,
                            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(JobMapActivity.this, "already graded", Toast.LENGTH_SHORT).show();
                    } else {
                        requestCall();
                    }
                    new DialogCall(JobMapActivity.this, phoneNumber).show();
                }
            }
        });

        sendMessageLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNumber.length() == 0) {
                    Toast.makeText(JobMapActivity.this, "Phone not available", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(phoneNumber);
                }
            }
        });

    }

    private void requestCall() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(JobMapActivity.this,
                Manifest.permission.CALL_PHONE)) {new AlertDialog.Builder(JobMapActivity.this)
                .setTitle("Permission needed")
                .setMessage("needed to continue")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(JobMapActivity.this,
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
            ActivityCompat.requestPermissions(JobMapActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PER_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == StaticCodes.CALL_PER_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Graded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (hideMapRL.getVisibility() == View.VISIBLE) {
            hideMapRL.setVisibility(View.GONE);
            moreFAB.setVisibility(View.VISIBLE);
            directionsFAB.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this);
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

//        mMap.addMarker(new MarkerOptions().position(new LatLng(showSpot.get)))
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(adPlace, 15));
//        mMap.addCircle(new CircleOptions()
//                .center(adPlace)
//                .radius(200)
//                .strokeColor(getResources().getColor(R.color.lightPrimary)).strokeWidth(1)
//                .fillColor(getResources().getColor(R.color.colorAccentLight)));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    public void sendMessage(String num) {
        Uri uri = Uri.parse("smsto:"+num);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(Intent.createChooser(it, "Send by ..."));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.spot_map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.edit_spot_item:
                Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete_spot_item:
                Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

}

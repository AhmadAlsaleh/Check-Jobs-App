package com.crazy_iter.checkjobs;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.crazy_iter.checkjobs.Adapters.MainFragmentsAdapter;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticCodes;

public class MainSlidesActivity extends AppCompatActivity implements StaticCodes {

    private ViewPager mainVP;
    private static final int numberOfBackgrounds = 5;
    private ImageView leftIV, rightIV;
    private TextView skipTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_slides);
        getSupportActionBar().hide();

        findViews();

        // region main view pager background

        mainVP.setOffscreenPageLimit(numberOfBackgrounds - 1);
        MainFragmentsAdapter mainFragmentsAdapter = new MainFragmentsAdapter(getSupportFragmentManager(), numberOfBackgrounds);
        mainVP.setAdapter(mainFragmentsAdapter);
        mainVP.setCurrentItem(0);
        mainVP.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // setLeftRightArrows(position);
                if (position == (numberOfBackgrounds - 1)) {
                    skipTV.setText(R.string.done);
                } else {
                    skipTV.setText(R.string.skip);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // endregion

        leftIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainVP.setCurrentItem(mainVP.getCurrentItem() - 1);
            }
        });

        rightIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainVP.setCurrentItem(mainVP.getCurrentItem() + 1);
            }
        });

        skipTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainVP.getCurrentItem() == (numberOfBackgrounds - 1)) {
                    finish();
                    startActivity(new Intent(MainSlidesActivity.this, MainActivity.class));
                } else {
                    mainVP.setCurrentItem(mainVP.getCurrentItem() + 1);
                }
            }
        });

        requestCall();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PRE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestCallCall();
            } else {
                requestCall();
            }
        }
        if (requestCode == CALL_PER_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                requestCallCall();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestCallCall() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainSlidesActivity.this, Manifest.permission.CALL_PHONE)) {
            new AlertDialog.Builder(MainSlidesActivity.this)
                    .setMessage(R.string.permission_needed)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(
                                    MainSlidesActivity.this,
                                    new String[] {Manifest.permission.CALL_PHONE},
                                    CALL_PER_CODE
                            );
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            requestCallCall();
                        }
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(MainSlidesActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PER_CODE);
        }
    }

    private void requestCall() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainSlidesActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(MainSlidesActivity.this)
                    .setMessage(R.string.permission_needed)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(
                                    MainSlidesActivity.this,
                                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                                    LOCATION_PRE_CODE
                            );
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            requestCall();
                        }
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(MainSlidesActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PRE_CODE);
        }
    }

    private void findViews() {
        mainVP = findViewById(R.id.mainVP);
        leftIV = findViewById(R.id.leftIV);
        rightIV = findViewById(R.id.rightIV);
        skipTV = findViewById(R.id.skipSlideTV);
    }

    public void setLeftRightArrows(int position) {
        if (position == 0) {
            leftIV.setVisibility(View.GONE);
            rightIV.setVisibility(View.VISIBLE);
            return;
        }
        if (position == numberOfBackgrounds - 1) {
            leftIV.setVisibility(View.VISIBLE);
            rightIV.setVisibility(View.GONE);
            return;
        }
        leftIV.setVisibility(View.VISIBLE);
        rightIV.setVisibility(View.VISIBLE);
    }
}

package com.crazy_iter.checkarround;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.crazy_iter.checkarround.Dialogs.DialogAbout;
import com.crazy_iter.checkarround.Dialogs.DialogChangePassword;
import com.crazy_iter.checkarround.Dialogs.DialogEdit;
import com.crazy_iter.checkarround.Dialogs.DialogSendFeedback;
import com.crazy_iter.checkarround.Dialogs.DialogTerms;
import com.crazy_iter.checkarround.StaticsAndAPIs.StaticString;

public class SettingsActivity extends AppCompatActivity {

    private TextView logoutTV, manageMyPlaces, changePassword, changeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.settings);

        logoutTV = findViewById(R.id.logoutTV);
        manageMyPlaces = findViewById(R.id.manageMyPlaces);
        changePassword = findViewById(R.id.changePassword);
        changeInfo = findViewById(R.id.changeInfo);

        if (StartActivity.user == null) {
            logoutTV.setVisibility(View.GONE);
            manageMyPlaces.setVisibility(View.GONE);
            changePassword.setVisibility(View.GONE);
            changeInfo.setVisibility(View.GONE);
        } else {
            logoutTV.setVisibility(View.VISIBLE);
            changePassword.setVisibility(View.VISIBLE);
            changeInfo.setVisibility(View.VISIBLE);
            if (StartActivity.user.isPro()) {
                manageMyPlaces.setVisibility(View.VISIBLE);
            } else {
                 manageMyPlaces.setVisibility(View.GONE);
            }
            if (StartActivity.user.isFacebook()) {
                changePassword.setVisibility(View.GONE);
            } else {
                changePassword.setVisibility(View.VISIBLE);
            }
        }

        manageMyPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, PlacesActivity.class));
            }
        });

        logoutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartActivity.localDB.logout();
                StartActivity.user = null;
                SettingsActivity.this.finish();
            }
        });

        changeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogEdit(SettingsActivity.this).show();
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
                finish();
                break;
        }
        return true;
    }

    public void changePassword(View view) {
        new DialogChangePassword(SettingsActivity.this).show();
    }

    public void toTermsOfUse(View view) {
        // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(StaticString.S_TERMS)));
        new DialogTerms(SettingsActivity.this).show();
    }

    public void toSendFeedback(View view) {
        if (StartActivity.user != null) {
            new DialogSendFeedback(SettingsActivity.this).show();
        } else {
            Toast.makeText(this, R.string.sign_in_please, Toast.LENGTH_SHORT).show();
        }
    }

    public void toLikeOnFacebook(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(StaticString.S_FACEBOOK_PAGE_URL)));
    }

    public void toFollowTwitter(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(StaticString.S_TWITTER_PAGE)));
    }

    public void toAbout(View view) {
        new DialogAbout(SettingsActivity.this).show();
    }

}

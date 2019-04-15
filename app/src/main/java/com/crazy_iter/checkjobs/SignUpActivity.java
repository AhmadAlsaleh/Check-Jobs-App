package com.crazy_iter.checkjobs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crazy_iter.checkjobs.Dialogs.DialogTerms;
import com.crazy_iter.checkjobs.StaticsAndAPIs.API_URLs;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticCodes;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticString;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements StaticCodes, StaticString, API_URLs {

    private TextInputLayout nameTIL, emailTIL, passwordTIL;
    private RadioButton workOwnerRB;
    private RelativeLayout loadingRL;
    private LoadingLayout loadingLayout;
    private Button signUpBtn;
    private RequestQueue requestQueue;
    private int retryNum = 0;
    private boolean fromSlides = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        try {
            Bundle bundle = getIntent().getExtras();
            fromSlides = bundle.getBoolean(S_FROM_SLIDES);
        } catch (Exception ignore) {}

        nameTIL = findViewById(R.id.nameTIL);
        emailTIL = findViewById(R.id.emailTIL);
        passwordTIL = findViewById(R.id.passwordTIL);
        workOwnerRB = findViewById(R.id.workOwnerRB);
        loadingRL = findViewById(R.id.loadingSignUpRL);
        loadingLayout = new LoadingLayout(SignUpActivity.this);
        loadingRL.addView(loadingLayout.getLoading());

        requestQueue = Volley.newRequestQueue(SignUpActivity.this);

        signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputCorrect()) {
                    signUp();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestQueue.cancelAll(S_SIGN_UP);
    }

    private boolean isInputCorrect() {
        boolean b = true;

        if (nameTIL.getEditText().getText().length() == 0) {
            nameTIL.setError(getString(R.string.field_is_required));
            b = false;
        } else {
            nameTIL.setError("");
        }

        boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(emailTIL.getEditText().getText().toString()).matches();
        if (emailTIL.getEditText().getText().length() == 0) {
            emailTIL.setError(getString(R.string.field_is_required));
            b = false;
        } else if (!isEmail) {
            emailTIL.setError(getString(R.string.incorrect_email));
            b = false;
        } else {
            emailTIL.setError("");
        }

        if (passwordTIL.getEditText().getText().length() == 0) {
            passwordTIL.setError(getString(R.string.field_is_required));
            b = false;
        } else if (passwordTIL.getEditText().getText().length() < 6) {
            passwordTIL.setError(getString(R.string.at_least_6));
            b = false;
        } else {
            passwordTIL.setError("");
        }

        return b;
    }

    private void signUp() {
        loadingLayout.showLoading();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(S_NAME, nameTIL.getEditText().getText().toString());
            jsonObject.put(S_EMAIL, emailTIL.getEditText().getText().toString());
            jsonObject.put(S_PASSWORD, passwordTIL.getEditText().getText().toString());
            jsonObject.put(S_IS_PRO, workOwnerRB.isChecked());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, SIGN_UP, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            Log.e("id", jsonObject.getString(S_ID));
                            StartActivity.localDB.insertUser(
                                    jsonObject.getString(S_ID),
                                    nameTIL.getEditText().getText().toString()
                                    ,"","",
                                    workOwnerRB.isChecked(),
                                    false);
                            StartActivity.user = StartActivity.localDB.getUserInfo();
                            Toast.makeText(SignUpActivity.this, R.string.welcome, Toast.LENGTH_LONG).show();
                            finish();
                        } catch (JSONException e) {
                            Log.e("message", e.getMessage());
                            loadingLayout.hideLoading();
                            Toast.makeText(SignUpActivity.this, R.string.already_signed_up, Toast.LENGTH_SHORT).show();
                        }
                        requestQueue.cancelAll(SIGN_UP);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(SIGN_UP);
                if (retryNum < RETRY_NUM) {
                    retryNum++;
                    signUp();
                } else {
                    loadingLayout.hideLoading();
                    Toast.makeText(SignUpActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonObjectRequest.setTag(SIGN_UP);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        if (fromSlides) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            finish();
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        }
    }

    public void toTerms(View view) {
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(StaticString.S_TERMS));
//        startActivity(intent);
        new DialogTerms(this).show();
    }

}

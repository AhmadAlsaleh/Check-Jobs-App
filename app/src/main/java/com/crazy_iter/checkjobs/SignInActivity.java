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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crazy_iter.checkjobs.Data.User;
import com.crazy_iter.checkjobs.Dialogs.DialogForgetPassword;
import com.crazy_iter.checkjobs.Dialogs.DialogTerms;
import com.crazy_iter.checkjobs.StaticsAndAPIs.API_URLs;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticCodes;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticString;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class SignInActivity extends AppCompatActivity implements StaticCodes, StaticString, API_URLs{

    private TextInputLayout emailOrPhoneTIL, passwordTIL;
    private Button signInBtn;
    private RelativeLayout loadingRL;
    private RequestQueue requestQueue;
    private int retryNum = 0;
    private LoadingLayout loadingLayout;
    private boolean fromSlides = false;
//    private Profile profile;

    CallbackManager callbackManager;
    FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Profile profile = Profile.getCurrentProfile();
            try  {
                String name = profile.getName();
                String id = profile.getId();
                LoginManager.getInstance().logOut();
                loginFacebook(id, name);
            } catch (Exception e) {
                Log.e("facebook not success", e.getMessage());
            }
            LoginManager.getInstance().logOut();
        }

        @Override
        public void onCancel() {
            Toast.makeText(SignInActivity.this, R.string.canceled, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(FacebookException error) {
            Toast.makeText(SignInActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
            Log.e("facebook login", error.getMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            FacebookSdk.sdkInitialize(getApplicationContext());
            callbackManager = CallbackManager.Factory.create();
            AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                }
            };
            ProfileTracker profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
             //       profile = currentProfile;
                }
            };
            accessTokenTracker.startTracking();
            profileTracker.startTracking();
        } catch (Exception e) {
            Log.e("error facebook", e.getMessage());
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().hide();

        try {
            Bundle bundle = getIntent().getExtras();
            fromSlides = bundle.getBoolean(S_FROM_SLIDES);
        } catch (Exception ignore) {}

        emailOrPhoneTIL = findViewById(R.id.emailOrPhoneTIL);
        passwordTIL = findViewById(R.id.passwordTIL);
        signInBtn = findViewById(R.id.signInBtn);
        loadingRL = findViewById(R.id.loadingSignInRL);
        loadingLayout = new LoadingLayout(SignInActivity.this);
        loadingRL.addView(loadingLayout.getLoading());

        requestQueue = Volley.newRequestQueue(SignInActivity.this);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputCorrect()) {
                    login();
                }
            }
        });

        LoginButton loginButton = findViewById(R.id.loginWithFacebookBTN);
        loginButton.setReadPermissions(Arrays.asList("public_profile"));
        loginButton.registerCallback(callbackManager, mCallback);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Profile profile = Profile.getCurrentProfile();
//        if (profile != null) {
//            Toast.makeText(activity, profile.getId() + "\n" + profile.getName(), Toast.LENGTH_SHORT).show();
//        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void loginFacebook(final String idFacebook, final String name) {
        loadingLayout.showLoading();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(S_FACEBOOK_ID, idFacebook);
            jsonObject.put(S_PASSWORD, idFacebook);
            jsonObject.put(S_NAME, name);
            jsonObject.put(S_IS_PRO, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, SIGN_IN_FACEBOOK, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        String id, details;
                        boolean isPro;
                        try {
                            id = jsonObject.getString(S_ID);
                            details = jsonObject.getString(S_DETAILS);
                            isPro = jsonObject.getBoolean(S_IS_PRO);

                            StartActivity.user = new User(id, name, "", details, isPro, true);
                            StartActivity.localDB.insertUser(id, name, "", details, isPro, true);
                            Toast.makeText(SignInActivity.this, R.string.welcome, Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } catch (JSONException e) {
                            Log.e("message", e.getMessage());
                            loadingLayout.hideLoading();
                            Toast.makeText(SignInActivity.this, R.string.has_not_signed_up, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_SIGN_IN);
                if (retryNum < RETRY_NUM) {
                    retryNum++;
                    loginFacebook(idFacebook, name);
                } else {
                    loadingLayout.hideLoading();
                    Toast.makeText(SignInActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonObjectRequest.setTag(S_SIGN_IN);
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public void onBackPressed() {
        if (fromSlides) {
            finish();
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestQueue.cancelAll(S_SIGN_IN);
    }

    private boolean isInputCorrect() {
        boolean b = true;
        boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(emailOrPhoneTIL.getEditText().getText().toString()).matches();
        if (emailOrPhoneTIL.getEditText().getText().length() == 0) {
            emailOrPhoneTIL.setError(getString(R.string.field_is_required));
            b = false;
        } else if (!isEmail) {
            emailOrPhoneTIL.setError(getString(R.string.incorrect_email));
            b = false;
        } else {
            emailOrPhoneTIL.setError("");
        }

        if (passwordTIL.getEditText().getText().length() == 0) {
            passwordTIL.setError(getString(R.string.field_is_required));
            b = false;
        } else if (passwordTIL.getEditText().getText().length() < 6) {
            passwordTIL.setError(getString(R.string.password_validation));
            b = false;
        } else {
            passwordTIL.setError("");
        }
        return b;
    }

    private void login() {
        loadingLayout.showLoading();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(S_EMAIL, emailOrPhoneTIL.getEditText().getText().toString());
            jsonObject.put(S_PASSWORD, passwordTIL.getEditText().getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, SIGN_IN, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        String id, name, imagePath, details;
                        boolean isPro;
                        try {
                            id = jsonObject.getString(S_ID);
                            name = jsonObject.getString(S_NAME);
                            imagePath = jsonObject.getString(S_IMAGE_PATH);
                            details = jsonObject.getString(S_DETAILS);
                            isPro = jsonObject.getBoolean(S_IS_PRO);
                            StartActivity.user = new User(id, name, imagePath, details, isPro, false);
                            StartActivity.localDB.insertUser(id, name, imagePath, details, isPro, false);
                            Toast.makeText(SignInActivity.this, R.string.welcome_back, Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } catch (JSONException e) {
                            Log.e("message", e.getMessage());
                            loadingLayout.hideLoading();
                            Toast.makeText(SignInActivity.this, R.string.has_not_signed_up, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_SIGN_IN);
                if (retryNum < RETRY_NUM) {
                    retryNum++;
                    login();
                } else {
                    loadingLayout.hideLoading();
                    Toast.makeText(SignInActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonObjectRequest.setTag(S_SIGN_IN);
        requestQueue.add(jsonObjectRequest);

    }

    public void toTerms(View view) {
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(StaticString.S_TERMS));
//        startActivity(intent);
        new DialogTerms(this).show();
    }

    public void toSignUp(View view) {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        SignInActivity.this.finish();
    }

    public void toForgetPassword(View view) {
        new DialogForgetPassword(SignInActivity.this).show();
    }
}

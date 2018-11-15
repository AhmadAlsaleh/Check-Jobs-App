package com.crazy_iter.checkarround.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crazy_iter.checkarround.R;
import com.crazy_iter.checkarround.StartActivity;
import com.crazy_iter.checkarround.StaticsAndAPIs.API_URLs;
import com.crazy_iter.checkarround.StaticsAndAPIs.StaticCodes;
import com.crazy_iter.checkarround.StaticsAndAPIs.StaticString;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by CrazyITer on 4/5/2018.
 */

public class DialogChangePassword extends Dialog implements StaticCodes, StaticString, API_URLs {

    private Context context;
    private TextInputLayout oldPassword, newPassword1, newPassword2;
    private Button save;
    private RequestQueue requestQueue;
    private static int retryNum = 0;

    public DialogChangePassword(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_change_password);

        requestQueue = Volley.newRequestQueue(context);

        oldPassword = findViewById(R.id.oldPassword);
        newPassword1 = findViewById(R.id.newPassword1);
        newPassword2 = findViewById(R.id.newPassword2);
        save = findViewById(R.id.saveNewPassword);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputCorrect()) {
                    changePassword(oldPassword.getEditText().getText().toString(),
                            newPassword1.getEditText().getText().toString());
                }
            }
        });
    }

    private boolean isInputCorrect() {
        boolean b = true;
        if (oldPassword.getEditText().getText().length() == 0) {
            oldPassword.setError(context.getString(R.string.field_is_required));
            b = false;
        } else if (oldPassword.getEditText().getText().length() < PASSWORD_CHARS_NUM){
            oldPassword.setError(context.getString(R.string.at_least_6));
            b = false;
        } else {
            oldPassword.setError("");
        }

        if (newPassword1.getEditText().getText().length() == 0) {
            newPassword1.setError(context.getString(R.string.field_is_required));
            b = false;
        } else if (newPassword1.getEditText().getText().length() < PASSWORD_CHARS_NUM){
            newPassword1.setError(context.getString(R.string.at_least_6));
            b = false;
        } else {
            newPassword1.setError("");
        }

        if (newPassword2.getEditText().getText().length() == 0) {
            newPassword2.setError(context.getString(R.string.field_is_required));
            b = false;
        } else if (!(newPassword2.getEditText().getText().toString()
                .equals(newPassword1.getEditText().getText().toString()))) {
            newPassword2.setError(context.getString(R.string.no_match_passwords));
            b = false;
        } else {
            newPassword2.setError("");
        }
        return b;
    }

    private void changePassword(final String oldPass, final String newPass) {
        dismiss();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(S_ID, StartActivity.user.getId());
            jsonObject.put(S_OLD_PASSWORD, oldPass);
            jsonObject.put(S_PASSWORD, newPass);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, EDIT_PASSWORD, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Toast.makeText(context, R.string.password_updated, Toast.LENGTH_SHORT).show();
                        requestQueue.cancelAll(S_EDIT_PASSWORD);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_EDIT_PASSWORD);
                if (retryNum <= RETRY_NUM) {
                    changePassword(oldPass, newPass);
                    retryNum++;
                } else {
                    Toast.makeText(context, R.string.check_your_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonObjectRequest.setTag(S_EDIT_PASSWORD);
        requestQueue.add(jsonObjectRequest);
    }

}

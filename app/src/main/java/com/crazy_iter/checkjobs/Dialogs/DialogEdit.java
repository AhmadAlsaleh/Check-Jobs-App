package com.crazy_iter.checkjobs.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crazy_iter.checkjobs.R;
import com.crazy_iter.checkjobs.StartActivity;
import com.crazy_iter.checkjobs.StaticsAndAPIs.API_URLs;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticCodes;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticString;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by CrazyITer on 3/27/2018.
 */

public class DialogEdit extends Dialog implements StaticCodes, StaticString, API_URLs {

    private TextInputLayout newName, newDetails;
    private Button saveBTN;
    private Context context;
    private RequestQueue requestQueue;
    private int retryNum = 0;

    public DialogEdit(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit);

        requestQueue = Volley.newRequestQueue(context);

        newName = findViewById(R.id.newNameTIL);
        newDetails = findViewById(R.id.newDetailsTIL);
        saveBTN = findViewById(R.id.saveBTNDialog);
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputCorrect()) {
                    editInfo();
                }
            }
        });

        newName.getEditText().setText(StartActivity.user.getName());
        newDetails.getEditText().setText(StartActivity.user.getDetails());

    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
        requestQueue.cancelAll(S_EDIT);
    }

    private void editInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(S_ID, StartActivity.user.getId());
            jsonObject.put(S_NAME, newName.getEditText().getText().toString());
            jsonObject.put(S_DETAILS, newDetails.getEditText().getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, EDIT_INFO, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        StartActivity.user.setName(newName.getEditText().getText().toString());
                        StartActivity.user.setDetails(newDetails.getEditText().getText().toString());
                        StartActivity.localDB.editUserInfo(newName.getEditText().getText().toString(), newDetails.getEditText().getText().toString());
                        Toast.makeText(context, R.string.updated, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_EDIT);
                if (retryNum < RETRY_NUM) {
                    retryNum++;
                    editInfo();
                }
            }
        });
        jsonObjectRequest.setTag(S_EDIT);
        requestQueue.add(jsonObjectRequest);
    }

    private boolean isInputCorrect() {
        boolean b = true;
        if (newName.getEditText().getText().length() == 0) {
            b = false;
            newName.setError(context.getString(R.string.field_is_required));
        } else {
            newName.setError("");
        }
        return b;
    }

}

package com.crazy_iter.checkjobs.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
 * Created by CrazyITer on 4/6/2018.
 */

public class DialogSendFeedback extends Dialog implements API_URLs, StaticCodes, StaticString {

    private Context context;
    private EditText yourFeedback;
    private ImageView sendFeedback;
    private RequestQueue requestQueue;
    private int retryNum = 0;

    public DialogSendFeedback(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_send_feedback);

        requestQueue = Volley.newRequestQueue(context);
        yourFeedback = findViewById(R.id.yourFeedback);
        sendFeedback = findViewById(R.id.sendFeedback);
        sendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (yourFeedback.getText().length() != 0) {
                    // sendByEmail();
                    sendReport();
                } else {
                    Toast.makeText(context, R.string.type_your_feedback, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendReport() {
        Toast.makeText(context, R.string.sending, Toast.LENGTH_SHORT).show();
        dismiss();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(S_UserID, StartActivity.user.getId());
            jsonObject.put(S_BODY, yourFeedback.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, SEND_REPORT, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Toast.makeText(context, R.string.feedback_sent, Toast.LENGTH_LONG).show();
                        requestQueue.cancelAll(S_REPORT);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_REPORT);
                if (retryNum < RETRY_NUM) {
                    retryNum++;
                    sendReport();
                } else {
                    Toast.makeText(context, R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonObjectRequest.setTag(S_REPORT);
        requestQueue.add(jsonObjectRequest);
    }

    private void sendByEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"alsaleh.a.ahmad@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "feedback");
        intent.putExtra(Intent.EXTRA_TEXT, yourFeedback.getText().toString());
        try {
            context.startActivity(intent);
            dismiss();
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(context, R.string.no_mail_app, Toast.LENGTH_SHORT).show();
        }
    }

}

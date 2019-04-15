package com.crazy_iter.checkjobs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crazy_iter.checkjobs.Data.Currency;
import com.crazy_iter.checkjobs.Data.JobItem;
import com.crazy_iter.checkjobs.Dialogs.DialogSelectPlace;
import com.crazy_iter.checkjobs.Dialogs.DialogSpots;
import com.crazy_iter.checkjobs.StaticsAndAPIs.API_URLs;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticCodes;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticString;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddSpotActivity extends AppCompatActivity implements StaticString, StaticCodes, API_URLs {

    TextInputLayout titleTIL, descriptionsTIL, placeTILAdd, phoneTILAdd;
    LinearLayout selectPlaceLL;
    TextView hourTimeAdd, daysNumAdd;
    Button disHourAdd, addHourAdd, disDaysAdd, addDaysAdd,
        submitSpot, cancelSpot;
    RadioGroup amPMGBAdd, genderRG;
    RadioButton amRBAdd, pmRBAdd, maleRB, femaleRB, notImportantRB;
    EditText lowSalaryAdd, highSalaryAdd;
    int hoursNum = 4, daysNum = 4;
    Spinner currencySpinnerAdd;
    DialogSelectPlace dialogSelectPlace;
    RequestQueue requestQueue;
    int retryNum = 0;
    private LoadingLayout loadingLayout;
    private RelativeLayout loadingRL;
    private boolean editedPlace = false;
    private TextView title;

    private boolean isEdit = false;
    public static JobItem jobItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_spot);
        getSupportActionBar().hide();

        // region findViews
        title = findViewById(R.id.addActivityTitleTV);
        loadingRL = findViewById(R.id.loadingAddSpotRL);
        loadingLayout = new LoadingLayout(this);
        loadingRL.addView(loadingLayout.getLoading());
        requestQueue = Volley.newRequestQueue(this);
        selectPlaceLL = findViewById(R.id.selectPlaceLL);
        titleTIL = findViewById(R.id.titleTIL);
        placeTILAdd = findViewById(R.id.placeTILAdd);
        descriptionsTIL = findViewById(R.id.descriptionAdd);
        phoneTILAdd = findViewById(R.id.phoneTILAdd);
        hourTimeAdd = findViewById(R.id.hoursTimeAdd);
        daysNumAdd = findViewById(R.id.daysNumAdd);
        disHourAdd = findViewById(R.id.disHoursAdd);
        addHourAdd = findViewById(R.id.addHoursAdd);
        disDaysAdd = findViewById(R.id.disDaysAdd);
        addDaysAdd = findViewById(R.id.addDaysAdd);
        submitSpot = findViewById(R.id.submitSpot);
        cancelSpot = findViewById(R.id.cancelAddSpot);
        amPMGBAdd = findViewById(R.id.amPMGBAdd);
        amRBAdd = findViewById(R.id.amRBAdd);
        pmRBAdd = findViewById(R.id.pmRBAdd);
        genderRG = findViewById(R.id.genderRG);
        maleRB = findViewById(R.id.maleRB);
        femaleRB = findViewById(R.id.femaleRB);
        notImportantRB = findViewById(R.id.notInportantRB);
        lowSalaryAdd = findViewById(R.id.lowSalaryAdd);
        highSalaryAdd = findViewById(R.id.highSalaryAdd);
        currencySpinnerAdd = findViewById(R.id.currencySpinnerAdd);
        // endregion

        dialogSelectPlace = new DialogSelectPlace(AddSpotActivity.this);

        // region buttons
        addHourAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hoursNum < 12) {
                    hoursNum++;
                    hourTimeAdd.setText("" + hoursNum);
                }
            }
        });

        disHourAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hoursNum > 0) {
                    hoursNum--;
                    hourTimeAdd.setText("" + hoursNum);
                }
            }
        });

        addDaysAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (daysNum < 7) {
                    daysNum++;
                    daysNumAdd.setText("" + daysNum);
                }
            }
        });

        disDaysAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (daysNum > 0) {
                    daysNum--;
                    daysNumAdd.setText("" + daysNum);
                }
            }
        });

        submitSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputsCorrect()) {
                    if (isEdit) {
                        updateSpot();
                    } else {
                        uploadNewSpot();
                    }
                }
            }
        });

        cancelSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sureToCancel();
            }
        });
        // endregion

        placeTILAdd.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editedPlace = true;
                    dialogSelectPlace.show();
                }
            }
        });

        dialogSelectPlace.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (DialogSelectPlace.selectedPlace != null) {
                    placeTILAdd.getEditText().setText(DialogSelectPlace.selectedPlace.getTitle());
                }
                placeTILAdd.getEditText().clearFocus();
            }
        });

        try {
            Bundle bundle = getIntent().getExtras();
            isEdit = bundle.getBoolean("isEdit");
            if (bundle.getBoolean("isEdit")) {
                title.setText(R.string.edit_spot);
                titleTIL.getEditText().setText(jobItem.getPosition());
                phoneTILAdd.getEditText().setText(jobItem.getPhone());
                daysNumAdd.setText(jobItem.getDays().split(" ")[0]);
                hourTimeAdd.setText(jobItem.getTime().split(" ")[0]);
                if (jobItem.getTime().charAt(jobItem.getTime().length() - 2) == 'P') {
                    pmRBAdd.setChecked(true);
                } else {
                    amRBAdd.setChecked(true);
                }
                lowSalaryAdd.setText(String.valueOf(jobItem.getLowSalary()));
                highSalaryAdd.setText(String.valueOf(jobItem.getHighSalary()));
                placeTILAdd.getEditText().setText(jobItem.getAddress());
                String gender = jobItem.getGender();
                if ("male".equals(gender.toLowerCase())) {
                    maleRB.setChecked(true);
                } else if ("female".equals(gender.toLowerCase())) {
                    femaleRB.setChecked(true);
                } else {
                    notImportantRB.setChecked(true);
                }
                descriptionsTIL.getEditText().setText(jobItem.getDescription());
            }
        } catch (Exception ignored) {}


//        lowSalaryAdd.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                DecimalFormat decimalFormat = new DecimalFormat("###,###,###.##");
//                lowSalaryAdd.removeTextChangedListener(this);
//                try {
//                    lowSalaryAdd.setText(decimalFormat.format(Double.parseDouble(s.toString())));
//                } catch (Exception e) {
//                    Log.e("error", e.getMessage());
//                }
//                lowSalaryAdd.addTextChangedListener(this);
//            }
//        });
//
//        highSalaryAdd.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                DecimalFormat decimalFormat = new DecimalFormat("###,###,###.##");
//                highSalaryAdd.removeTextChangedListener(this);
//                try {
//                    highSalaryAdd.setText(decimalFormat.format(Double.parseDouble(s.toString())));
//                } catch (Exception e) {
//                    Log.e("error", e.getMessage());
//                }
//                highSalaryAdd.addTextChangedListener(this);
//            }
//        });

        // loadCurrencies();

    }

    private void updateSpot() {
        loadingLayout.showLoading();
        String genderString = "notImportant";
        int genderID = genderRG.getCheckedRadioButtonId();
        if (genderID == maleRB.getId()) {
            genderString = maleRB.getText().toString();
        } else if (genderID == femaleRB.getId()) {
            genderString = femaleRB.getText().toString();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(S_ID, jobItem.getJobId());
            jsonObject.put(S_TITLE, titleTIL.getEditText().getText().toString().trim());
            jsonObject.put(S_PHONE, phoneTILAdd.getEditText().getText().toString().trim());
            jsonObject.put(S_MINUTES, Integer.parseInt(hourTimeAdd.getText().toString()) * 60);
            jsonObject.put(S_IS_PM, pmRBAdd.isChecked());
            jsonObject.put(S_DAYS, Integer.parseInt(daysNumAdd.getText().toString()));
            jsonObject.put(S_LOW_SAL, Double.parseDouble(lowSalaryAdd.getText().toString()));
            jsonObject.put(S_HIGH_SAL, Double.parseDouble(highSalaryAdd.getText().toString()));
            jsonObject.put(S_GENDER, genderString);
            jsonObject.put(S_DESCRIPTION, descriptionsTIL.getEditText().getText().toString().trim());
            if (editedPlace) {
                jsonObject.put(S_PLACE_ID, DialogSelectPlace.selectedPlace.getId());
            } else {
                jsonObject.put(S_PLACE_ID, jobItem.getPlaceID());
            }

        } catch (JSONException e) {
            Log.e("add spot json", e.getMessage());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, EDIT_SPOT, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Toast.makeText(AddSpotActivity.this, R.string.updated, Toast.LENGTH_SHORT).show();
                        finish();
                        DialogSpots.textView.setText("done");
                        requestQueue.cancelAll(S_UPLOAD_SPOT);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_UPLOAD_SPOT);
                if (retryNum < RETRY_NUM) {
                    retryNum++;
                    updateSpot();
                } else {
                    Toast.makeText(AddSpotActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                }
                try {
                    Log.e("update error", volleyError.getMessage());
                } catch (Exception ignored){}
            }
        });
        jsonObjectRequest.setTag(S_UPLOAD_SPOT);
        requestQueue.add(jsonObjectRequest);
    }

    private void uploadNewSpot() {
        loadingLayout.showLoading();
        String genderString = "notImportant";
        int genderID = genderRG.getCheckedRadioButtonId();
        if (genderID == maleRB.getId()) {
            genderString = maleRB.getText().toString();
        } else if (genderID == femaleRB.getId()) {
            genderString = femaleRB.getText().toString();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(S_TITLE, titleTIL.getEditText().getText().toString().trim());
            jsonObject.put(S_PHONE, phoneTILAdd.getEditText().getText().toString().trim());
            jsonObject.put(S_MINUTES, hoursNum * 60);
            jsonObject.put(S_IS_PM, pmRBAdd.isChecked());
            jsonObject.put(S_DAYS, daysNum);
            jsonObject.put(S_LOW_SAL, Double.parseDouble(lowSalaryAdd.getText().toString()));
            jsonObject.put(S_HIGH_SAL, Double.parseDouble(highSalaryAdd.getText().toString()));
            jsonObject.put(S_GENDER, genderString);
            jsonObject.put(S_DESCRIPTION, descriptionsTIL.getEditText().getText().toString().trim());
            jsonObject.put(S_PLACE_ID, DialogSelectPlace.selectedPlace.getId() == null ? "" : DialogSelectPlace.selectedPlace.getId());
        } catch (JSONException e) {
            Log.e("add spot json", e.getMessage());
        }
        Log.e("json add", jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, ADD_SPOT, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Toast.makeText(AddSpotActivity.this, R.string.uploaded, Toast.LENGTH_SHORT).show();
                        finish();
                        requestQueue.cancelAll(S_UPLOAD_SPOT);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_UPLOAD_SPOT);
                if (retryNum < RETRY_NUM) {
                    retryNum++;
                    uploadNewSpot();
                } else {
                    Toast.makeText(AddSpotActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                    loadingLayout.hideLoading();
                }
            }
        });
        jsonObjectRequest.setTag(S_UPLOAD_SPOT);
        requestQueue.add(jsonObjectRequest);
    }

    private boolean isInputsCorrect() {
        boolean b = true;
        if (titleTIL.getEditText().getText().length() == 0) {
            b = false;
            titleTIL.setError(getString(R.string.field_is_required));
        } else {
            titleTIL.setError("");
        }
        if (placeTILAdd.getEditText().getText().length() == 0) {
            b = false;
            placeTILAdd.setError(getString(R.string.field_is_required));
        } else {
            placeTILAdd.setError("");
        }
        if (phoneTILAdd.getEditText().getText().length() == 0) {
            b = false;
            phoneTILAdd.setError(getString(R.string.field_is_required));
        } else {
            boolean isPhone = Patterns.PHONE.matcher(phoneTILAdd.getEditText().getText().toString()).matches();
            if (!isPhone) {
                b = false;
                phoneTILAdd.setError(getString(R.string.incorrect_phone_number));
            } else {
                phoneTILAdd.setError("");
            }
        }

        Double low = Double.parseDouble(lowSalaryAdd.getText().length() == 0 ? "0" : lowSalaryAdd.getText().toString());
        Double high = Double.parseDouble(highSalaryAdd.getText().length() == 0 ? "0" : highSalaryAdd.getText().toString());
        if (lowSalaryAdd.getText().length() == 0 ||
                highSalaryAdd.getText().length() == 0 ||
                low > high) {
            b = false;
            Toast.makeText(this, R.string.check_salary_values, Toast.LENGTH_SHORT).show();
        }

        return b;
    }

    ArrayList<Currency> currencyArrayList;
    private void loadCurrencies() {
        currencyArrayList = new ArrayList<>();
        currencyArrayList.add(new Currency("SP", "Syrian Pond", "S.P"));
        currencyArrayList.add(new Currency("USD", "American Dollar", "$$"));
        ArrayList<String> currencyName = new ArrayList<>();
        for (Currency currency:
                currencyArrayList) {
            currencyName.add(currency.getSymbol());
        }
        ArrayAdapter<String> currencyArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, currencyName);
        currencyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinnerAdd.setAdapter(currencyArrayAdapter);
        currencySpinnerAdd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        sureToCancel();
    }

    private void sureToCancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddSpotActivity.this);
        builder.setMessage(R.string.sure_to_cancel);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AddSpotActivity.this.finish();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        })
                .create()
                .show();
    }

    private void showAreaName(LatLng latLng) {
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(API_URLs.mapAPI + latLng.latitude + "," + latLng.longitude,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            if (jsonObject.getJSONArray("results").length() != 0) {
                                String address = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                                Log.d("address value", address);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("address error", e.getMessage());
                        }
                        requestQueue.cancelAll("address");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("address error", volleyError.getMessage());
                requestQueue.cancelAll("address");
            }
        });
        jsonObjectRequest.setTag("address");
        requestQueue.add(jsonObjectRequest);
    }
}

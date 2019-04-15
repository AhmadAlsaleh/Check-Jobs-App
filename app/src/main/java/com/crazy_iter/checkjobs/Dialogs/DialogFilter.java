package com.crazy_iter.checkjobs.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crazy_iter.checkjobs.Data.Currency;
import com.crazy_iter.checkjobs.Data.Places;
import com.crazy_iter.checkjobs.MainActivity;
import com.crazy_iter.checkjobs.R;
import com.crazy_iter.checkjobs.StaticsAndAPIs.API_URLs;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticCodes;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticString;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by CrazyITer on 3/24/2018.
 */

public class DialogFilter extends Dialog implements API_URLs, StaticString, StaticCodes {

    private Context context;
    private TextInputLayout searchWord;
    private Spinner currencySpinner, placesSpinner;
    private EditText lowSal, highSal;
    private Button addHours, disHours, addDays, disDays;
    private TextView hoursTime, daysNum;
    private RadioGroup amPMRG;
    private RadioButton pmRB, amRB;
    private int hours = 4, days = 4;
    private boolean isPM = true;
    private Button searchBTNDialog;
    private RadioGroup genderRGFilter;
    private RadioButton maleRBFilter, femaleRBFilter, notImportantRBFilter;
    private RadioButton nearestPlaceRB;
    private String genderString = "notImportant";
    private RequestQueue requestQueue;
    private int retryNum = 0;
    private CheckBox dateTimeCB, genderCB;
    private RelativeLayout dateTimeRL;
    public static boolean isGoodDis = false;

    public DialogFilter(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_filter);

        // region findViews
        dateTimeCB = findViewById(R.id.filterDateTimeCB);
        genderCB = findViewById(R.id.filterGenderCB);
        dateTimeRL = findViewById(R.id.dateTimeRL);
        requestQueue = Volley.newRequestQueue(context);
        genderRGFilter = findViewById(R.id.genderRGFilter);
        nearestPlaceRB = findViewById(R.id.nearestPlaceRB);
        maleRBFilter = findViewById(R.id.maleRBFilter);
        femaleRBFilter = findViewById(R.id.femaleRBFilter);
        notImportantRBFilter = findViewById(R.id.notImportantRBFilter);
        searchBTNDialog = findViewById(R.id.searchBTNDialog);
        searchWord = findViewById(R.id.searchTitleTIDialog);
        lowSal = findViewById(R.id.lowSalary);
        highSal = findViewById(R.id.highSalary);
        currencySpinner = findViewById(R.id.currencySpinner);
        placesSpinner = findViewById(R.id.placesSpinner);
        addHours = findViewById(R.id.addHours);
        disHours = findViewById(R.id.disHours);
        hoursTime = findViewById(R.id.hoursTime);
        amPMRG = findViewById(R.id.amPMGB);
        amRB = findViewById(R.id.amRB);
        pmRB = findViewById(R.id.pmRB);
        addDays = findViewById(R.id.addDays);
        disDays = findViewById(R.id.disDays);
        daysNum = findViewById(R.id.daysNum);
        // endregion

        // region buttons listeners
        addDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (days < 7) {
                    days++;
                    daysNum.setText("" + days);
                }
            }
        });
        disDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (days > 0) {
                    days--;
                    daysNum.setText("" + days);
                }
            }
        });

        hours = Integer.parseInt(hoursTime.getText().toString());
        addHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hours < 12) {
                    hours++;
                    hoursTime.setText("" + hours);
                }
            }
        });
        disHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hours > 0) {
                    hours--;
                    hoursTime.setText("" + hours);
                }
            }
        });
        amPMRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                isPM = i == R.id.pmRB;
            }
        });
        // endregion

        // region gender radio
        genderRGFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.maleRBFilter:
                        genderString = "male";
                        break;
                    case R.id.femaleRBFilter:
                        genderString = "female";
                        break;
                    case R.id.notImportantRBFilter:
                        genderString = "notImportant";
                        break;
                }
            }
        });
        // endregion

        dateTimeCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dateTimeRL.setVisibility(View.VISIBLE);
                } else {
                    dateTimeRL.setVisibility(View.GONE);
                }
            }
        });

        genderCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    genderRGFilter.setVisibility(View.VISIBLE);
                } else {
                    genderRGFilter.setVisibility(View.GONE);
                }
            }
        });

        searchBTNDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.loadingLayout.showLoading();
                isGoodDis = true;
                dismiss();
            }
        });

        // loadCurrencies();
        // loadPlaces();

    }

    public JSONObject filterRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(S_TITLE, "");
            if (searchWord.getEditText().getText().toString().trim().length() != 0) {
                jsonObject.put(S_TITLE, searchWord.getEditText().getText().toString());
            }
            if (lowSal.getText().toString().trim().length() != 0) {
                jsonObject.put(S_LOW_SAL, lowSal.getText().toString());
            }
            if (highSal.getText().toString().trim().length() != 0) {
                jsonObject.put(S_HIGH_SAL, highSal.getText().toString());
            }
            if (genderCB.isChecked()) {
                jsonObject.put(S_GENDER, genderString.toLowerCase());
            }
            if (dateTimeCB.isChecked()) {
                jsonObject.put(S_IS_PM, pmRB.isChecked());
                jsonObject.put(S_DAYS, daysNum.getText().toString());
                jsonObject.put(S_MINUTES, Integer.parseInt(hoursTime.getText().toString()) * 60);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  jsonObject;
    }

    // region not used
    private ArrayList<Currency> currencyArrayList;
    private void loadCurrencies() {
        currencyArrayList = new ArrayList<>();
        currencyArrayList.add(new Currency("SP", "Syrian Pond", "S.P"));
        currencyArrayList.add(new Currency("USD", "American Dollar", "$$"));
        ArrayList<String> currencyName = new ArrayList<>();
        for (Currency currency:
                currencyArrayList) {
            currencyName.add(currency.getSymbol());
        }
        ArrayAdapter<String> currencyArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, currencyName);
        currencyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencyArrayAdapter);
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private ArrayList<Places> placesArrayList;
    private void loadPlaces() {
        placesArrayList = new ArrayList<>();
        placesArrayList.add(new Places("", "Place11", 0.0, 0.0));
        placesArrayList.add(new Places("", "Place22", 0.0, 0.0));
        ArrayList<String> placesName = new ArrayList<>();
        for (Places places:
                placesArrayList) {
            placesName.add(places.getTitle());
        }
        ArrayAdapter<String> placesArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, placesName);
        placesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        placesSpinner.setAdapter(placesArrayAdapter);
        placesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    // endregion

}

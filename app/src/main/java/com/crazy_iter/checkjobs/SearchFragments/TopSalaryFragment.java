package com.crazy_iter.checkjobs.SearchFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.crazy_iter.checkjobs.Adapters.SpotsAdapter;
import com.crazy_iter.checkjobs.Data.Currency;
import com.crazy_iter.checkjobs.Data.JobItem;
import com.crazy_iter.checkjobs.R;

import java.util.ArrayList;


public class TopSalaryFragment extends Fragment {

    private View view;
    private Spinner currencySpinner;
    private RecyclerView searchSalRV;
    private EditText lowSal, highSal;

    public TopSalaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_top_salary, container, false);
        currencySpinner = view.findViewById(R.id.currencySpinner);
        searchSalRV = view.findViewById(R.id.searchSalRV);
        lowSal = view.findViewById(R.id.lowSalary);
        highSal = view.findViewById(R.id.highSalary);
        lowSal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                loadData();
            }
        });
        highSal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                loadData();
            }
        });
        loadCurrencies();
        loadData();
        return view;
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
        ArrayAdapter<String> currencyArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, currencyName);
        currencyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencyArrayAdapter);
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadData() {
        ArrayList<JobItem> salaryJobItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            salaryJobItems.add(new JobItem("" + i,
                    "Position: " + i,
                    50.0,
                    100.0,
                    "5 hours P.M",
                    "5 days",
                    "Company: " + i,
                    "Latakia",
                    "",
                    false,
                    "test"));
        }

        searchSalRV.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        searchSalRV.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new SpotsAdapter(getContext(), salaryJobItems);
        searchSalRV.setAdapter(adapter);
    }
}

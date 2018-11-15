package com.crazy_iter.checkarround.SearchFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.crazy_iter.checkarround.Adapters.SpotsAdapter;
import com.crazy_iter.checkarround.Data.JobItem;
import com.crazy_iter.checkarround.R;

import java.util.ArrayList;

public class NearYouFragment extends Fragment {

    private View view;
    // private Spinner spinnerCities, spinnerAreas;
    SeekBar nearPlacesSeekBar;
    TextView meterNum;
    private RecyclerView nearRV;

    public NearYouFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_near_you, container, false);
        // spinnerCities = view.findViewById(R.id.spinnerCities);
        // spinnerAreas = view.findViewById(R.id.spinnerAreas);
        nearPlacesSeekBar = view.findViewById(R.id.nearPlacesSeekBar);
        meterNum = view.findViewById(R.id.meterNum);
        nearPlacesSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                meterNum.setText(i + "m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        nearRV = view.findViewById(R.id.searchNearRV);
        loadData();
        // loadSpinnersData();
        return view;
    }

//    ArrayList<Places> citiesArrayList;
//    ArrayList<Places> areaArrayList;
//    private void loadSpinnersData() {
//        // region spinners list
//        citiesArrayList = new ArrayList<>();
//        citiesArrayList.add(new Places(
//                "",
//                "All Cities"
//        ));
//        for (int i = 0; i < 5; i++) {
//            citiesArrayList.add(new Places(
//                    "" + i,
//                    "City: " + i
//            ));
//        }
//        areaArrayList = new ArrayList<>();
//        areaArrayList.add(new Places(
//                "",
//                "All Areas"
//        ));
//        for (int i = 0; i < 5; i++) {
//            areaArrayList.add(new Places(
//                    "" + i,
//                    "Area: " + i
//            ));
//        }
//        ArrayList<String> citiesTitles = new ArrayList<>();
//        for (Places places:
//                citiesArrayList) {
//            citiesTitles.add(places.getTitle());
//        }
//        ArrayList<String> areasTitles = new ArrayList<>();
//        for (Places places:
//                areaArrayList) {
//            areasTitles.add(places.getTitle());
//        }
//        // endregion
//
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, citiesTitles);
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerCities.setAdapter(arrayAdapter);
//        spinnerCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == 0) {
//                    spinnerAreas.setVisibility(View.GONE);
//                } else {
//                    spinnerAreas.setVisibility(View.VISIBLE);
//                }
//                loadData();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        ArrayAdapter<String> arrayAdapterAreas = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, areasTitles);
//        arrayAdapterAreas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerAreas.setAdapter(arrayAdapterAreas);
//        spinnerAreas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                loadData();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//    }

    private void loadData() {
        ArrayList<JobItem> nearJobItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            nearJobItems.add(new JobItem("" + i,
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
        nearRV.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        nearRV.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new SpotsAdapter(getContext(), nearJobItems);
        nearRV.setAdapter(adapter);
    }
}

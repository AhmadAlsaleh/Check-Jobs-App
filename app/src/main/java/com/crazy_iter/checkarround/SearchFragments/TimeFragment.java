package com.crazy_iter.checkarround.SearchFragments;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.crazy_iter.checkarround.Adapters.SpotsAdapter;
import com.crazy_iter.checkarround.Data.JobItem;
import com.crazy_iter.checkarround.R;

import java.util.ArrayList;

public class TimeFragment extends Fragment {

    View view;
    Button addHours, disHours, addDays, disDays;
    TextView hoursTime, daysNum;
    RadioGroup amPMRG;
    RadioButton pmRB, amRB;
    RecyclerView timeRV;
    int hours = 4, days = 4;
    boolean isPM = true;

    public TimeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_time, container, false);
        addHours = view.findViewById(R.id.addHours);
        disHours = view.findViewById(R.id.disHours);
        hoursTime = view.findViewById(R.id.hoursTime);
        amPMRG = view.findViewById(R.id.amPMGB);
        amRB = view.findViewById(R.id.amRB);
        pmRB = view.findViewById(R.id.pmRB);
        addDays = view.findViewById(R.id.addDays);
        disDays = view.findViewById(R.id.disDays);
        daysNum = view.findViewById(R.id.daysNum);
        timeRV = view.findViewById(R.id.searchTimeRV);
        addDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (days < 7) {
                    days++;
                    daysNum.setText("" + days);
                }
                loadData();
            }
        });
        disDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (days > 0) {
                    days--;
                    daysNum.setText("" + days);
                }
                loadData();
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
                loadData();
            }
        });
        disHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hours > 0) {
                    hours--;
                    hoursTime.setText("" + hours);
                }
                loadData();
            }
        });
        amPMRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                isPM = i == R.id.pmRB;
                loadData();
            }
        });
        loadData();
        return view;
    }

    private void loadData() {
        ArrayList<JobItem> timeJobItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            timeJobItems.add(new JobItem("" + i,
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
        timeRV.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        timeRV.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new SpotsAdapter(getContext(), timeJobItems);
        timeRV.setAdapter(adapter);
    }
}

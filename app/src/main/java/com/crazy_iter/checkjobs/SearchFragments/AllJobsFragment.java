package com.crazy_iter.checkjobs.SearchFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crazy_iter.checkjobs.Adapters.SpotsAdapter;
import com.crazy_iter.checkjobs.Data.JobItem;
import com.crazy_iter.checkjobs.Dialogs.DialogFilter;
import com.crazy_iter.checkjobs.R;

import java.util.ArrayList;

public class AllJobsFragment extends Fragment {

    private View view;
    private RecyclerView allJobsRV;
    LinearLayout filterLLAll;
    ImageView filterIconAll;
    TextView filterTVAll;
    private boolean isFiltered = false;

    public AllJobsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_all_jobs, container, false);
        allJobsRV = view.findViewById(R.id.searchAllRV);
        filterLLAll = view.findViewById(R.id.filterLLAll);
        filterIconAll = view.findViewById(R.id.filterIconAll);
        filterTVAll = view.findViewById(R.id.filterTVAll);
        filterLLAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFiltered) {
                    isFiltered = true;
                    filterIconAll.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear));
                    filterTVAll.setText(".......");
                    new DialogFilter(getContext()).show();
                    allJobsRV.scrollToPosition(0);
                }
            }
        });
        filterIconAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFiltered) {
                    isFiltered = false;
                    filterIconAll.setImageDrawable(getResources().getDrawable(R.drawable.ic_tune));
                    filterTVAll.setText("Filtering...");
                }
            }
        });

        loadAll();

        return view;
    }

    public void loadAll() {
        ArrayList<JobItem> allJobItemArrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            allJobItemArrayList.add(new JobItem("" + i,
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
        allJobsRV.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        allJobsRV.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new SpotsAdapter(getContext(), allJobItemArrayList);
        allJobsRV.setAdapter(adapter);
    }
}

package com.crazy_iter.checkjobs.MainFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crazy_iter.checkjobs.AddSpotActivity;
import com.crazy_iter.checkjobs.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment1 extends Fragment {

    private View view;
    private FloatingActionButton addFAB;

    public MainFragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_fragment1, container, false);

        addFAB = view.findViewById(R.id.addFAB);
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddSpotActivity.class));
            }
        });

        return view;
    }

}

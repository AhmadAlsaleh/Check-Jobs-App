package com.crazy_iter.checkarround.MainFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crazy_iter.checkarround.R;
import com.crazy_iter.checkarround.StaredSpotsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment2 extends Fragment {

    private View view;
    private FloatingActionButton starFAB;

    public MainFragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_fragment2, container, false);

        starFAB = view.findViewById(R.id.starFAB);
        starFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), StaredSpotsActivity.class));
            }
        });

        return view;
    }

}

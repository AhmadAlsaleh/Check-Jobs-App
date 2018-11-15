package com.crazy_iter.checkarround.MainFragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crazy_iter.checkarround.R;
import com.crazy_iter.checkarround.SignInActivity;
import com.crazy_iter.checkarround.SignUpActivity;
import com.crazy_iter.checkarround.StaticsAndAPIs.StaticString;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment4 extends Fragment implements StaticString {

    private View view;
    private Button signUp, signIn;

    public MainFragment4() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_fragment4, container, false);

        signIn = view.findViewById(R.id.signInMainBtn);
        signUp = view.findViewById(R.id.signUpMainBtn);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) getContext()).finish();
                Intent intent = new Intent(getContext(), SignInActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(S_FROM_SLIDES, true);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) getContext()).finish();
                Intent intent = new Intent(getContext(), SignUpActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(S_FROM_SLIDES, true);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }

}

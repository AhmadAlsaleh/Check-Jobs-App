package com.crazy_iter.checkjobs.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.crazy_iter.checkjobs.R;

/**
 * Created by CrazyITer on 4/6/2018.
 */

public class DialogTerms extends Dialog {

    private Context context;

    public DialogTerms(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_terms);
    }
}

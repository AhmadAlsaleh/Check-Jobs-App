package com.crazy_iter.checkjobs.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crazy_iter.checkjobs.R;

/**
 * Created by CrazyITer on 4/10/2018.
 */

public class DialogForgetPassword extends Dialog {

    private Context context;
    private TextInputLayout emailReset;
    private Button resetPassword;

    public DialogForgetPassword(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_forget_password);
        emailReset = findViewById(R.id.emailResetTIL);
        resetPassword = findViewById(R.id.resetPassword);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCorrect()) {
                    dismiss();
                    Toast.makeText(context, "TODO", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isCorrect() {
        boolean b = true;
        boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(emailReset.getEditText().getText().toString()).matches();

        if (emailReset.getEditText().getText().length() == 0) {
            emailReset.setError("Field is required");
            b = false;
        } else if (!isEmail) {
            emailReset.setError("Incorrect email address");
            b = false;
        } else {
            emailReset.setError("");
            b = true;
        }
        return b;
    }

}

package com.crazy_iter.checkarround.Dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crazy_iter.checkarround.R;

/**
 * Created by CrazyITer on 3/28/2018.
 */

public class DialogCall extends Dialog {

    private Context context;
    private LinearLayout callLL, copyLL, addLL;
    private TextView phone;
    private String phoneString;

    public DialogCall(@NonNull Context context, @NonNull String phone) {
        super(context);
        this.context = context;
        this.phoneString = phone;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_call);
        callLL = findViewById(R.id.callLL);
        copyLL = findViewById(R.id.copyLL);
        addLL = findViewById(R.id.addLL);
        phone = findViewById(R.id.phoneNumberDialog);
        phone.setText(phoneString);

        callLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call();
            }
        });

        copyLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyPhone();
            }
        });

        addLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContact();
            }
        });

    }

    private void call() {
        Toast.makeText(context, R.string.calling, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneString));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        context.startActivity(intent);
    }

    private void copyPhone() {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Phone Number", phone.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    private void addContact() {
        Intent addIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        addIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        addIntent.putExtra(ContactsContract.Intents.Insert.PHONE, phone.getText().toString());
        //addIntent.putExtra(ContactsContract.Intents.Insert.NAME, usernameTV.getText().toString() + " | " + titleTV.getText().toString());
        context.startActivity(addIntent);
    }

}

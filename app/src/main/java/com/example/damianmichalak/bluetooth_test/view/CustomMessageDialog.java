package com.example.damianmichalak.bluetooth_test.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.damianmichalak.bluetooth_test.R;

public class CustomMessageDialog {

    private final Context context;

    public CustomMessageDialog(Context context) {
        this.context = context;
    }

    public void show() {
        View layout = LayoutInflater.from(context).inflate(R.layout.custom_message_dialog_view, null);
        final EditText editText = (EditText) layout.findViewById(R.id.editText);

        DialogInterface.OnClickListener positiveOnClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String messageToSend = editText.getText().toString();

                ((MainActivity) context).popupResult(messageToSend);
                dialogInterface.dismiss();
            }
        };

        DialogInterface.OnClickListener negativeOnClick = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        };

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(layout)
                .setTitle("Custom MSG")
                .setPositiveButton("SEND", positiveOnClick)
                .setNegativeButton("CANCEL", negativeOnClick)
                .create();
        dialog.show();
    }
}

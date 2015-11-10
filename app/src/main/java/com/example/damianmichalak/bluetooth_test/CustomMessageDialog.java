package com.example.damianmichalak.bluetooth_test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Kamil on 2015-11-08.
 */
public class CustomMessageDialog {

    private final Context context;
    private final ConnectedThread socket;
    private Logger logger;

    public CustomMessageDialog(Context context, ConnectedThread socket, Logger logger) {
        this.context = context;
        this.socket = socket;
        this.logger = logger;
    }

    public void show() {
        View layout = LayoutInflater.from(context).inflate(R.layout.custom_message_dialog_view, null);
        final EditText editText = (EditText) layout.findViewById(R.id.editText);

        DialogInterface.OnClickListener positiveOnClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String messageToSend = editText.getText().toString();
                socket.write(messageToSend);
                dialogInterface.dismiss();
            }
        };

        DialogInterface.OnClickListener negativeOnClick = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        };

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(layout)
                .setTitle("Custom MSG")
                .setPositiveButton("SEND", positiveOnClick)
                .setNegativeButton("CANCEL", negativeOnClick)
                .create();
        dialog.show();
    }
}

package com.example.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.R;


public class CustomProgressDialogue {
    AlertDialog dialog;
    TextView msg;

    public void showCustomDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View customLayout = LayoutInflater.from(context).inflate(R.layout.wait_dialogue, null);
        builder.setView(customLayout);
        msg = customLayout.findViewById(R.id.msg);
        dialog
                = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void showCustomDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View customLayout = LayoutInflater.from(context).inflate(R.layout.wait_dialogue, null);
        builder.setView(customLayout);
        msg = customLayout.findViewById(R.id.msg);
        msg.setText(message);
        dialog
                = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void hideCustomDialog() {
        try {
            dialog.dismiss();
        } catch (Exception ignored) {
        }

    }
}

package com.example.pratham.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Pratham on 2/23/2017.
 */

public class CustomDialog {
    public void showCustomDialog(final Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogLayout = inflater.inflate(R.layout.custom_rounded_box, null);
        AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setView(dialogLayout);
        final AlertDialog customAlertDialog = builder.create();
        int width = (int)(context.getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(context.getResources().getDisplayMetrics().heightPixels*0.80);
        customAlertDialog.getWindow().setLayout(width, height);
        customAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btn_positive = (Button) dialogLayout.findViewById(R.id.customDialogPositive);
        Button btn_negative = (Button) dialogLayout.findViewById(R.id.customDialogNegative);
        TextView textView = (TextView) dialogLayout.findViewById(R.id.textviewInCustomDialog);
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,
                        "POSITIVE", Toast.LENGTH_SHORT).show();
                customAlertDialog.dismiss();
            }
        });
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,
                        "NEGATIVE", Toast.LENGTH_SHORT).show();
                customAlertDialog.dismiss();
            }
        });
        textView.setText(msg);
        customAlertDialog.show();
    }
    public void showCustomDialog(final Context context, String msg, final Intent intent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogLayout = inflater.inflate(R.layout.custom_rounded_box, null);
        AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setView(dialogLayout);
        final AlertDialog customAlertDialog = builder.create();
        int width = (int)(context.getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(context.getResources().getDisplayMetrics().heightPixels*0.80);
        customAlertDialog.getWindow().setLayout(width, height);
        customAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btn_positive = (Button) dialogLayout.findViewById(R.id.customDialogPositive);
        Button btn_negative = (Button) dialogLayout.findViewById(R.id.customDialogNegative);
        TextView textView = (TextView) dialogLayout.findViewById(R.id.textviewInCustomDialog);
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(intent);
                customAlertDialog.dismiss();
            }
        });
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,
                        "NEGATIVE", Toast.LENGTH_SHORT).show();
                customAlertDialog.dismiss();
            }
        });
        textView.setText(msg);
        customAlertDialog.show();
    }
}

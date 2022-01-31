package com.tranxit.enterprise.common;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.tranxit.enterprise.user.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by santhosh@appoets.com on 04-05-2018.
 */

public class Utilities {
    public static boolean isEmailValid(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void printV(String TAG, String message) {
        System.out.println(TAG + "==>" + message);
    }

    public void hideKeypad(Context context, View view) {
        // Check if no view has focus:
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showAlert(final Context context, String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder.setMessage(message)
                    .setTitle(context.getString(R.string.app_name))
                    .setCancelable(true)
                    //.setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
            final AlertDialog alert = builder.create();
            alert.setOnShowListener(arg -> {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            });
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

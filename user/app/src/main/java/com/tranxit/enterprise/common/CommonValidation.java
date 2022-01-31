package com.tranxit.enterprise.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by CSS88 on 09-01-2018.
 */

public class CommonValidation {
    public static String NullPointerHandle(String Values) {
        if (Values != null && !Values.equalsIgnoreCase("null"))
            return Values;
        else
            return "";
    }

    public static boolean Validation(String Values) {
        return Values == null || Values.equalsIgnoreCase("null") || Values.isEmpty();

    }

    public static boolean isNameValidation(EditText Values) {
        return Values.getText().toString().length() < 3;
    }

    public static boolean PasswordLength(EditText Values) {
        return Values.getText().toString().length() < 6;
    }

    public static boolean isValidPass(final String password) {
        return !Pattern.compile("[\\S]{6,}$").matcher(password).matches();
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public static boolean isPasswordMatcher(String Password, String ConfirmationPassword) {
        return !Password.equals(ConfirmationPassword);
    }

    public static void CommonToast(Context context, String Message) {
        Toast.makeText(context, Message, Toast.LENGTH_LONG).show();
    }

    public static boolean isValidEmail(CharSequence target) {
        return !(!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidPhone(String phone) {
        boolean check;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            check = phone.length() < 6 || phone.length() > 13;
        } else {
            check = true;
        }
        return check;
    }

    public static void displayMessage(final String toastString, final Activity activity) {
        try {

            Snackbar snackbar = Snackbar.make(activity.getCurrentFocus(), toastString, Snackbar.LENGTH_LONG).setDuration(Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            TextView tv = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setMaxLines(3);
            snackbar.show();

//            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
//                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(activity, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                e.printStackTrace();
            }
        }
    }

    public static TextView UnderLine(TextView underlineText) {
        underlineText.setPaintFlags(underlineText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        return underlineText;
    }
}

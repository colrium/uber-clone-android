package com.tranxit.enterprise;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.tranxit.enterprise.common.ConnectivityReceiver;
import com.tranxit.enterprise.common.LocaleHelper;
import com.tranxit.enterprise.data.SharedHelper;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Created by santhosh@appoets.com on 02-05-2018.
 */

public class MvpApplication extends Application {

    private static MvpApplication mInstance;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    public static final int PICK_LOCATION_REQUEST_CODE = 3;
    public static final int PERMISSIONS_REQUEST_PHONE = 4;
    public static float DEFAULT_ZOOM = 18;
    public static Location mLastKnownLocation;

    public static boolean isCCAvenueEnabled = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        mInstance = this;
        MultiDex.install(this);
    }

    public static synchronized MvpApplication getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
        //super.attachBaseContext(newBase);
        MultiDex.install(newBase);
    }

    public NumberFormat getNumberFormat() {
        String currencyCode = SharedHelper.getKey(getApplicationContext(), "currency_code", "INR");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        numberFormat.setCurrency(Currency.getInstance(currencyCode));
        numberFormat.setMinimumFractionDigits(2);
        return numberFormat;
    }

    public static void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}

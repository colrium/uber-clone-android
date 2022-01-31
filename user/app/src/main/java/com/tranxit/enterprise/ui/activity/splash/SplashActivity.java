package com.tranxit.enterprise.ui.activity.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.tranxit.enterprise.base.BaseActivity;
import com.tranxit.enterprise.data.SharedHelper;
import com.tranxit.enterprise.data.network.model.User;
import com.tranxit.enterprise.ui.activity.OnBoardActivity;
import com.tranxit.enterprise.ui.activity.main.MainActivity;
import com.tranxit.enterprise.user.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.HttpException;
import retrofit2.Response;

public class SplashActivity extends BaseActivity implements SplashIView {

    private static final int REQUEST_CHECK_SETTINGS = 1;

    @BindView(R.id.note)
    TextView note;

    private SplashPresenter<SplashActivity> presenter = new SplashPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {

        }

        Log.d("FCM", "FCM Token: " + SharedHelper.getKey(activity(), "device_token"));
    }

    private void init() {
        new Handler().postDelayed(() -> {

            Log.d("Loggedin", String.valueOf(SharedHelper.getBoolKey(SplashActivity.this, "logged_in", false)));
            String device_token = String.valueOf(SharedHelper.getKey(SplashActivity.this, "device_token"));
            Log.d("device_token", device_token);
            if (SharedHelper.getBoolKey(SplashActivity.this, "logged_in", false)) {
                presenter.profile();
            } else {
                startActivity(new Intent(SplashActivity.this, OnBoardActivity.class));
                finish();
            }
        }, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        enableGPS();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSuccess(User user) {
        SharedHelper.putKey(this, "logged_in", true);
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();
            Log.e("onError", response.code() + "");

            if (response.code() == 500) {
                note.setText(getString(R.string.internal_server_error));
            }
            if (response.code() == 401) {
                SharedHelper.clearSharedPreferences(activity());
                startActivity(new Intent(activity(), OnBoardActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    protected void enableGPS() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000 * 10);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, locationSettingsResponse -> {
            init();
        });

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(SplashActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    Toast.makeText(this, sendEx.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        init();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }
}

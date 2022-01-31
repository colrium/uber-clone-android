package com.tranxit.enterprise.ui.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tranxit.enterprise.ui.activity.OnBoardActivity;
import com.tranxit.enterprise.user.BuildConfig;
import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseActivity;
import com.tranxit.enterprise.data.SharedHelper;
import com.tranxit.enterprise.data.network.model.ForgotResponse;
import com.tranxit.enterprise.data.network.model.Token;
import com.tranxit.enterprise.ui.activity.forgot_password.ForgotPasswordActivity;
import com.tranxit.enterprise.ui.activity.main.MainActivity;
import com.tranxit.enterprise.ui.activity.otp.OTPActivity;
import com.tranxit.enterprise.ui.activity.register.RegisterActivity;
import com.tranxit.enterprise.ui.countrypicker.Country;
import com.tranxit.enterprise.ui.countrypicker.CountryPicker;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.HttpException;
import retrofit2.Response;

public class EmailActivity extends BaseActivity implements LoginIView {

    @BindView(R.id.mobile)
    EditText mobile;
    private loginPresenter<EmailActivity> presenter = new loginPresenter<EmailActivity>();
    private CountryPicker mCountryPicker;
    private String countryDialCode = "+60";
    private String countryCode = "MY";

    @BindView(R.id.ivCountry)
    ImageView ivCountry;
    @BindView(R.id.tvCountry)
    TextView tvCountry;

    @Override
    public int getLayoutId() {
        return R.layout.activity_email;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        if (BuildConfig.DEBUG) mobile.setText("2025550086");
        setCountryList();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(EmailActivity.this, OnBoardActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @OnClick({R.id.sign_up, R.id.next, R.id.forgot_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sign_up:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.next:
                if (mobile.getText().toString().isEmpty()) {
                    Toast.makeText(this, R.string.invalid_mobile, Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(this, PasswordActivity.class)
                        .putExtra("email", mobile.getText().toString())
                        .putExtra("country_code", countryDialCode));
                break;
            case R.id.forgot_password:
                if (mobile.getText().toString().isEmpty()) {
                    Toast.makeText(this, R.string.invalid_mobile, Toast.LENGTH_SHORT).show();
                    return;
                }
                showLoading();
                presenter.forgotPassword(mobile.getText().toString());
                break;
        }
    }

    private void login() {
        if (mobile.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("grant_type", "password");
        map.put("username", mobile.getText().toString());
        map.put("password", "123456");
        map.put("client_secret", BuildConfig.CLIENT_SECRET);
        map.put("client_id", BuildConfig.CLIENT_ID);
        map.put("device_token", SharedHelper.getKey(this, "device_token"));
        map.put("device_id", SharedHelper.getKey(this, "device_id"));
        map.put("device_type", BuildConfig.DEVICE_TYPE);
        map.put("scope", "");
        map.put("country_code", countryDialCode);
        showLoading();
        presenter.login(map);
    }

    private void setCountryList() {
        mCountryPicker = CountryPicker.newInstance("Select Country");
        List<Country> countryList = Country.getAllCountries();
        Collections.sort(countryList, (s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
        mCountryPicker.setCountriesList(countryList);

        mCountryPicker.setListener((name, code, dialCode, flagDrawableResID) -> {
            tvCountry.setText(dialCode);
            countryDialCode = dialCode;
            ivCountry.setImageResource(flagDrawableResID);
            mCountryPicker.dismiss();
        });

        ivCountry.setOnClickListener(v -> mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER"));

        tvCountry.setOnClickListener(v -> mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER"));

        Country country = getDeviceCountry(EmailActivity.this);
        ivCountry.setImageResource(country.getFlag());
        tvCountry.setText(country.getDialCode());
        countryDialCode = country.getDialCode();
        countryCode = country.getCode();
    }

    @Override
    public void onSuccess(Token token) {
        hideLoading();
        String accessToken = token.getTokenType() + " " + token.getAccessToken();
        SharedHelper.putKey(this, "access_token", accessToken);
        SharedHelper.putKey(this, "refresh_token", token.getRefreshToken());
        SharedHelper.putKey(this, "logged_in", true);
        finishAffinity();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onSuccess(ForgotResponse forgotResponse) {
        hideLoading();
        Toast.makeText(this, forgotResponse.getMessage(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        intent.putExtra("email", forgotResponse.getUser().getEmail());
        intent.putExtra("otp", forgotResponse.getUser().getOtp().toString());
        intent.putExtra("id", forgotResponse.getUser().getId());
        startActivity(intent);
    }

    @Override
    public void onError(Throwable e) {
        hideLoading();
        if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                if (jObjError.has("message"))
                    Toast.makeText(activity(), jObjError.optString("message"), Toast.LENGTH_SHORT).show();
                if (jObjError.has("otp")) {
                    Intent intent = new Intent(activity(), OTPActivity.class);
                    intent.putExtra("mobile", mobile.getText().toString());
                    intent.putExtra("otp", String.valueOf(jObjError.optString("otp")));
                    startActivityForResult(intent, PICK_OTP_VERIFY);
                }
            } catch (Exception exp) {
                Log.e("Error", exp.getMessage());
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_OTP_VERIFY && resultCode == Activity.RESULT_OK) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("otp", data.getStringExtra("otp"));
            map.put("username", mobile.getText().toString());
            map.put("device_token", SharedHelper.getKey(this, "device_token"));
            map.put("device_id", SharedHelper.getKey(this, "device_id"));
            map.put("grant_type", "password");
            map.put("password", "123456");
            map.put("client_secret", BuildConfig.CLIENT_SECRET);
            map.put("client_id", BuildConfig.CLIENT_ID);
            map.put("device_type", BuildConfig.DEVICE_TYPE);
            showLoading();
            presenter.verifyOTP(map);
            Toast.makeText(this, "Thanks your Mobile is successfully verified", Toast.LENGTH_SHORT).show();
        }
    }
}

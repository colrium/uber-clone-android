package com.tranxit.enterprise.ui.activity.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tranxit.enterprise.data.network.model.SettingsResponse;
import com.tranxit.enterprise.ui.activity.OnBoardActivity;
import com.tranxit.enterprise.ui.activity.login.EmailActivity;
import com.tranxit.enterprise.ui.countrypicker.CountryPickerListener;
import com.tranxit.enterprise.user.BuildConfig;
import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseActivity;
import com.tranxit.enterprise.data.SharedHelper;
import com.tranxit.enterprise.data.network.model.MyOTP;
import com.tranxit.enterprise.data.network.model.Token;
import com.tranxit.enterprise.ui.activity.main.MainActivity;
import com.tranxit.enterprise.ui.activity.otp.OTPActivity;
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

import static com.tranxit.enterprise.common.CommonValidation.Validation;
import static com.tranxit.enterprise.common.CommonValidation.isValidEmail;
import static com.tranxit.enterprise.common.CommonValidation.isValidPass;
import static com.tranxit.enterprise.common.CommonValidation.isValidPhone;

public class RegisterActivity extends BaseActivity implements RegisterIView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.first_name)
    EditText firstName;
    @BindView(R.id.last_name)
    EditText lastName;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.mobile_layout)
    LinearLayout mobile_layout;
    @BindView(R.id.registration_layout)
    LinearLayout registration_layout;

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.tvEmergencyNoOne)
    EditText etEmergencyNoOne;
    @BindView(R.id.tvEmergencyNoTwo)
    EditText etEmergencyNoTwo;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etConfirmPassword)
    EditText etConfirmPassword;
    @BindView(R.id.ivCountry)
    ImageView ivCountry;
    @BindView(R.id.tvCountry)
    TextView tvCountry;

    private RegisterPresenter registerPresenter = new RegisterPresenter();
    private CountryPicker mCountryPicker;
    private String countryDialCode = "+60";
    private String countryCode = "MY";

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        registerPresenter.attachView(this);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar.setTitle(getString(R.string.register));
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mToolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(this, OnBoardActivity.class));
            finish();
        });
        registerPresenter.getSettings();
        setCountryList();

        /*if (BuildConfig.DEBUG) {
            mobile.setText("9988776655");
            etEmail.setText("aaa@aaa.aaa");
            firstName.setText("Test");
            lastName.setText("aaa");
            etEmergencyNoOne.setText("9988776655");
            etPassword.setText("112233");
            etConfirmPassword.setText("112233");
        }*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(RegisterActivity.this, OnBoardActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    private void setCountryList() {
        mCountryPicker = CountryPicker.newInstance("Select Country");
        List<Country> countryList = Country.getAllCountries();
        Collections.sort(countryList, (s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
        mCountryPicker.setCountriesList(countryList);

        mCountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                tvCountry.setText(dialCode);
                countryDialCode = dialCode;
                ivCountry.setImageResource(flagDrawableResID);
                mCountryPicker.dismiss();
            }
        });

        ivCountry.setOnClickListener(v -> mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER"));

        tvCountry.setOnClickListener(v -> mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER"));

        Country country = getDeviceCountry(RegisterActivity.this);
        ivCountry.setImageResource(country.getFlag());
        tvCountry.setText(country.getDialCode());
        countryDialCode = country.getDialCode();
        countryCode = country.getCode();
    }

    @OnClick({R.id.next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next:

                /*if (mobile_layout.getVisibility() == View.VISIBLE) {

                    if (Validation(mobile.getText().toString().trim())) {
                        Toast.makeText(this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (isValidPhone(mobile.getText().toString().trim())) {
                        Toast.makeText(this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    registerPresenter.verifyMobileAlreadyExits(mobile.getText().toString());

                } else*/
            {

                if (Validation(mobile.getText().toString().trim())) {
                    Toast.makeText(this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT).show();
                    return;
                } else if (isValidPhone(mobile.getText().toString().trim())) {
                    Toast.makeText(this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT).show();
                    return;
                } else if (isValidEmail(etEmail.getText().toString().trim())) {
                    Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                    return;
                } else if (firstName.getText().toString().isEmpty()) {
                    Toast.makeText(this, getString(R.string.invalid_first_name), Toast.LENGTH_SHORT).show();
                    return;
                } else if (lastName.getText().toString().isEmpty()) {
                    Toast.makeText(this, getString(R.string.invalid_last_name), Toast.LENGTH_SHORT).show();
                    return;
                } else if (etEmergencyNoOne.getText().toString().isEmpty()) {
                    Toast.makeText(this, getString(R.string.invalid_emergency_contact), Toast.LENGTH_SHORT).show();
                    return;
                } else if (isValidPass(etPassword.getText().toString())) {
                    Toast.makeText(this, getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
                    return;
                } else if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                    Toast.makeText(this, getString(R.string.invalid_confirm_password), Toast.LENGTH_SHORT).show();
                    return;
                }
                register();
            }
            break;
        }
    }

    private void register() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("first_name", firstName.getText().toString());
        map.put("last_name", lastName.getText().toString());
        map.put("email", etEmail.getText().toString());
        map.put("password", etPassword.getText().toString());
        map.put("password_confirmation", etConfirmPassword.getText().toString());
        map.put("emergency_contact1", etEmergencyNoOne.getText().toString());
        map.put("emergency_contact2", etEmergencyNoTwo.getText().toString());
        map.put("device_token", SharedHelper.getKey(this, "device_token"));
        map.put("device_id", SharedHelper.getKey(this, "device_id"));
        map.put("mobile", mobile.getText().toString());
        map.put("device_type", BuildConfig.DEVICE_TYPE);
        map.put("login_by", "manual");
        map.put("country_code", countryDialCode);

        System.out.println("RRR map = " + map);
        registerPresenter.register(map);
    }

    private boolean validate() {
//        if (email.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
//            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (Validation(mobile.getText().toString().trim())) {
            Toast.makeText(this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT).show();
            return false;
        } else if (isValidPhone(mobile.getText().toString().trim())) {
            Toast.makeText(this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT).show();
            return false;
        } else if (firstName.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_first_name), Toast.LENGTH_SHORT).show();
            return false;
        } else if (lastName.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_last_name), Toast.LENGTH_SHORT).show();
            return false;
        } /*else if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.getText().toString().length() < 6) {
            Toast.makeText(this, getString(R.string.invalid_password_length), Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordConfirmation.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_confirm_password), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.getText().toString().equals(passwordConfirmation.getText().toString())) {
            Toast.makeText(this, getString(R.string.password_should_be_same), Toast.LENGTH_SHORT).show();
            return false;
        } */ else if (SharedHelper.getKey(this, "device_token").isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_device_token), Toast.LENGTH_SHORT).show();
            return false;
        } else if (SharedHelper.getKey(this, "device_id").isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_device_id), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onSuccess(Token token) {
        Toast.makeText(this, getString(R.string.you_have_been_successfully_registered), Toast.LENGTH_SHORT).show();
        String accessToken = token.getTokenType() + " " + token.getAccessToken();
        SharedHelper.putKey(this, "access_token", accessToken);
        SharedHelper.putKey(this, "refresh_token", token.getRefreshToken());
        SharedHelper.putKey(this, "logged_in", true);
        finishAffinity();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onSuccess(MyOTP otp) {
        Intent intent = new Intent(activity(), OTPActivity.class);
        intent.putExtra("mobile", mobile.getText().toString());
        intent.putExtra("otp", String.valueOf(otp.getOtp()));
        startActivityForResult(intent, PICK_OTP_VERIFY);
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
                else if (jObjError.has("error"))
                    Toast.makeText(activity(), jObjError.optString("error"), Toast.LENGTH_SHORT).show();
                else if (jObjError.has("mobile"))
                    Toast.makeText(activity(), jObjError.optString("mobile"), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(activity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            } catch (Exception exp) {
                Log.e("Error", exp.getMessage());
            }
        }
    }

    @Override
    public void onSuccess(SettingsResponse response) {

        SharedHelper.putKey(this, SharedHelper.GOOGLE_API_KEY, response.getApiKey());
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_OTP_VERIFY && resultCode == Activity.RESULT_OK) {
            registration_layout.setVisibility(View.VISIBLE);
            mobile_layout.setVisibility(View.GONE);
            Toast.makeText(this, "Thanks your Mobile is successfully verified, Please enter your First Name and Last Name to create your account", Toast.LENGTH_SHORT).show();
        }
    }

}

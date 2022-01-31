package com.tranxit.enterprise.ui.activity.setting;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseActivity;
import com.tranxit.enterprise.common.LocaleHelper;
import com.tranxit.enterprise.data.network.model.AddressResponse;
import com.tranxit.enterprise.data.network.model.Address;
import com.tranxit.enterprise.ui.activity.location_pick.LocationPickActivity;
import com.tranxit.enterprise.ui.activity.splash.SplashActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tranxit.enterprise.MvpApplication.PICK_LOCATION_REQUEST_CODE;

public class SettingsActivity extends BaseActivity implements SettingsIView {

    @BindView(R.id.choose_language)
    RadioGroup chooseLanguage;
    @BindView(R.id.english)
    RadioButton english;
    @BindView(R.id.arabic)
    RadioButton arabic;
    @BindView(R.id.home_status)
    TextView homeStatus;
    @BindView(R.id.home_address)
    TextView homeAddress;
    @BindView(R.id.work_status)
    TextView workStatus;
    @BindView(R.id.work_address)
    TextView workAddress;

    String type = "home";
    private SettingsPresenter<SettingsActivity> presenter = new SettingsPresenter<>();
    Address work = null;
    Address home = null;


    String currentSAddress;
    Object currentSLatitude;
    Object currentSLongitude;

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        presenter.address();

        if(RIDE_REQUEST.containsKey("s_address")){
            currentSAddress = RIDE_REQUEST.get("s_address").toString();
            currentSLatitude = RIDE_REQUEST.get("s_latitude");
            currentSLongitude = RIDE_REQUEST.get("s_longitude");
        }

        String dd = LocaleHelper.getLanguage(this);
        switch (dd) {
            case "en":
                english.setChecked(true);
                break;
            case "ar":
                arabic.setChecked(true);
                break;
            default:
                english.setChecked(true);
                break;
        }

        chooseLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.english:
                    setLanguage("en");
                    break;
                case R.id.arabic:
                    setLanguage("ar");
                    break;
            }
        });
    }


    private void setLanguage(String language) {
        LocaleHelper.setLocale(this, language);
        finishAffinity();
        startActivity(new Intent(this, SplashActivity.class));
    }

    @Override
    public void onSuccessAddress(Object object) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        presenter.address();
    }

    @Override
    public void onSuccess(AddressResponse address) {
        if(currentSAddress != null){
            RIDE_REQUEST.put("s_address", currentSAddress);
            RIDE_REQUEST.put("s_latitude", currentSLatitude);
            RIDE_REQUEST.put("s_longitude", currentSLongitude);
        }
        if (address.getHome().isEmpty()) {
            homeAddress.setText("");
            homeStatus.setText(getString(R.string.add));
            homeStatus.setTag("add");
            home = null;
        } else {
            home = address.getHome().get(address.getHome().size() - 1);
            homeAddress.setText(home.getAddress());
            homeStatus.setText(getString(R.string.delete));
        }

        if (address.getWork().isEmpty()) {
            workAddress.setText("");
            workStatus.setText(getString(R.string.add));
            workStatus.setTag("add");
            work = null;
        } else {
            work = address.getWork().get(address.getWork().size() - 1);
            workAddress.setText(work.getAddress());
            workStatus.setText(getString(R.string.delete));
        }
    }

    @Override
    public void onError(Throwable e) {

    }

    @OnClick({R.id.home_status, R.id.work_status})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.home_status:
                if (home == null) {
                    type = "home";
                    Intent intent = new Intent(this, LocationPickActivity.class);
                    intent.putExtra("isHideDestination", true);
                    startActivityForResult(intent, PICK_LOCATION_REQUEST_CODE);
                } else {
                    presenter.deleteAddress(home.getId(), new HashMap<>());
                }
                break;
            case R.id.work_status:
                if (work == null) {
                    type = "work";
                    Intent workIntent = new Intent(this, LocationPickActivity.class);
                    workIntent.putExtra("isHideDestination", true);
                    startActivityForResult(workIntent, PICK_LOCATION_REQUEST_CODE);
                } else {
                    presenter.deleteAddress(work.getId(), new HashMap<>());
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_LOCATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (RIDE_REQUEST.containsKey("s_address")) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("type", type);
                    map.put("address", RIDE_REQUEST.get("s_address"));
                    map.put("latitude", RIDE_REQUEST.get("s_latitude"));
                    map.put("longitude", RIDE_REQUEST.get("s_longitude"));
                    presenter.addAddress(map);

                }
            }
        }
    }
}

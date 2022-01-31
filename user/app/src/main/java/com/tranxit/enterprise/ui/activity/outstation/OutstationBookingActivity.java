package com.tranxit.enterprise.ui.activity.outstation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseActivity;
import com.tranxit.enterprise.data.network.model.EstimateFare;
import com.tranxit.enterprise.data.network.model.Service;
import com.tranxit.enterprise.ui.activity.location_pick.LocationPickActivity;
import com.tranxit.enterprise.ui.activity.main.MainActivity;
import com.tranxit.enterprise.ui.activity.payment.PaymentActivity;
import com.tranxit.enterprise.ui.adapter.ServiceAdapterSingle;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tranxit.enterprise.MvpApplication.PICK_LOCATION_REQUEST_CODE;
import static com.tranxit.enterprise.MvpApplication.isCCAvenueEnabled;
import static com.tranxit.enterprise.common.fcm.MyFirebaseMessagingService.INTENT_FILTER;
import static com.tranxit.enterprise.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

public class OutstationBookingActivity extends BaseActivity implements OutstationBookingIView {

    @BindView(R.id.source)
    TextView source;
    @BindView(R.id.destination)
    TextView destination;
    @BindView(R.id.pick_location_layout)
    LinearLayout pickLocationLayout;
    @BindView(R.id.one_way)
    RadioButton oneWay;
    @BindView(R.id.round_trip)
    RadioButton roundTrip;
    @BindView(R.id.outstation_type_radio_group)
    RadioGroup outstationTypeRadioGroup;
    @BindView(R.id.leave_on)
    TextView leaveOn;
    @BindView(R.id.leave_on_time)
    TextView leaveOnTime;
    @BindView(R.id.return_by_time)
    TextView returnByTime;
    @BindView(R.id.leave_on_layout)
    LinearLayout leaveOnLayout;
    @BindView(R.id.return_by)
    TextView returnBy;
    @BindView(R.id.return_by_layout)
    LinearLayout returnByLayout;
    @BindView(R.id.car_type_rv)
    RecyclerView carTypeRv;
    @BindView(R.id.payment_type)
    TextView paymentType;
    @BindView(R.id.use_wallet)
    CheckBox useWallet;
    @BindView(R.id.get_pricing)
    Button getPricing;

    ServiceAdapterSingle adapter;
    private OutstationBookingPresenter<OutstationBookingActivity> presenter = new OutstationBookingPresenter<>();
    List<Service> list = new ArrayList<>();
    HashMap<String, Object> map = new HashMap<>();
    NumberFormat numberFormat;
    TimePickerDialog.OnTimeSetListener leaveOnTimeSetListener, returnByTimeSetListener;

    @Override
    public int getLayoutId() {
        return R.layout.activity_outstation_booking;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        numberFormat = getNumberFormat();
        leaveOnTimeSetListener = (timePicker, selectedHour, selectedMinute) -> leaveOnTime.setText(String.format(Locale.getDefault(),"%02d:%02d:%02d", selectedHour, selectedMinute, 0));
        returnByTimeSetListener = (timePicker, selectedHour, selectedMinute) -> returnByTime.setText(String.format(Locale.getDefault(),"%02d:%02d:%02d", selectedHour, selectedMinute, 0));

        carTypeRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        carTypeRv.setItemAnimator(new DefaultItemAnimator());

        outstationTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.one_way:
                    leaveOnLayout.setVisibility(View.VISIBLE);
                    returnByLayout.setVisibility(View.GONE);
                    break;
                case R.id.round_trip:
                    leaveOnLayout.setVisibility(View.VISIBLE);
                    returnByLayout.setVisibility(View.VISIBLE);
                    break;
            }
        });


        if (RIDE_REQUEST.containsKey("s_address")) {
            source.setText(String.valueOf(RIDE_REQUEST.get("s_address")));
        }
        if (RIDE_REQUEST.containsKey("d_address")) {
//            destination.setText(String.valueOf(RIDE_REQUEST.get("d_address")));
        }
        initPayment(paymentType);
        presenter.services();
    }

    @Override
    public void onSuccess(ServiceAdapterSingle adapter) {
        this.adapter = adapter;
        carTypeRv.setAdapter(adapter);
    }

    @Override
    public void onSuccessRequest(Object object) {
        Intent intent = new Intent(INTENT_FILTER);
        sendBroadcast(intent);
        finishAffinity();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onSuccess(EstimateFare estimateFare) {
        map.put("distance", estimateFare.getDistance());
        //map.put("pricing_id", estimateFare.getPricingId());

        String medd = "Estimate Fare: "+numberFormat.format(estimateFare.getEstimatedFare())+"\n"+
                "Distance: " + estimateFare.getDistance()+"KM";

        new AlertDialog.Builder(this)
                .setMessage(medd)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.accept_and_confirm), (dialog, which) -> {
                    presenter.sendRequest(map);
                }).show();

    }

    @OnClick({R.id.source, R.id.destination, R.id.leave_on, R.id.leave_on_time, R.id.return_by, R.id.return_by_time, R.id.get_pricing, R.id.payment_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.source:
                Intent sourceIntent = new Intent(this, LocationPickActivity.class);
                startActivityForResult(sourceIntent, PICK_LOCATION_REQUEST_CODE);
                break;
            case R.id.destination:
                Intent destinationIntent = new Intent(this, LocationPickActivity.class);
                destinationIntent.putExtra("desFocus", true);
                startActivityForResult(destinationIntent, PICK_LOCATION_REQUEST_CODE);
                break;
            case R.id.leave_on:

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    leaveOn.setText(SIMPLE_DATE_FORMAT.format(calendar.getTime()));
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();

                break;
            case R.id.leave_on_time:
                timePicker(leaveOnTimeSetListener);
                break;
            case R.id.return_by:
                DatePickerDialog return_byDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    returnBy.setText(SIMPLE_DATE_FORMAT.format(calendar.getTime()));
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                return_byDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                return_byDialog.show();
                break;
            case R.id.return_by_time:
                timePicker(returnByTimeSetListener);
                break;
            case R.id.get_pricing:
                estimateFare();
                break;
            case R.id.payment_type:
                if (isCCAvenueEnabled)
                    startActivityForResult(new Intent(this, PaymentActivity.class), PICK_PAYMENT_METHOD);
                break;
        }
    }

    void estimateFare() {

        map = new HashMap<>(RIDE_REQUEST);

        if (!map.containsKey("s_address") || !map.containsKey("d_address")) {
            Toast.makeText(this, getString(R.string.please_enter_address), Toast.LENGTH_SHORT).show();
            return;
        }

        if(adapter == null){
            Toast.makeText(this, getString(R.string.please_select_car_type), Toast.LENGTH_SHORT).show();
            return;
        }
        if (adapter.getSelectedService() == null) {
            Toast.makeText(this, getString(R.string.please_select_car_type), Toast.LENGTH_SHORT).show();
            return;
        }

        Service service = adapter.getSelectedService();

        if (service.getStatus() != 1) {
            Toast.makeText(activity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        if (oneWay.isChecked()) {
            if (leaveOn.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.please_select_leave_on_date), Toast.LENGTH_SHORT).show();
                return;
            }
            if (leaveOnTime.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.please_select_leave_on_time), Toast.LENGTH_SHORT).show();
                return;
            }

            map.put("day", "oneway");
            map.put("leave", leaveOn.getText().toString() + " " + leaveOnTime.getText().toString());
            map.remove("return");
        }

        if (roundTrip.isChecked()) {
            if (leaveOn.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.please_select_leave_on_date), Toast.LENGTH_SHORT).show();
                return;
            }
            if (leaveOnTime.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.please_select_leave_on_time), Toast.LENGTH_SHORT).show();
                return;
            }
            if (returnBy.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.please_select_return_date), Toast.LENGTH_SHORT).show();
                return;
            }
            if (returnByTime.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.please_select_return_time), Toast.LENGTH_SHORT).show();
                return;
            }

            map.put("day", "round");
            map.put("leave", leaveOn.getText().toString() + " " + leaveOnTime.getText().toString());
            map.put("return", returnBy.getText().toString() + " " + returnByTime.getText().toString());
        }


        map.put("service_type", service.getId());
        map.put("service_required", "outstation");
        map.put("use_wallet", useWallet.isChecked() ? 1 : 0);

        presenter.estimateFare(map);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (RIDE_REQUEST.containsKey("s_address")) {
                source.setText(String.valueOf(RIDE_REQUEST.get("s_address")));
            } else {
                source.setText("");
            }

            if (RIDE_REQUEST.containsKey("d_address")) {
                destination.setText(String.valueOf(RIDE_REQUEST.get("d_address")));
            } else {
                destination.setText("");
            }
        } else if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            RIDE_REQUEST.put("payment_mode", data.getStringExtra("payment_mode"));
            if (data.getStringExtra("payment_mode").equals("CARD")) {
                RIDE_REQUEST.put("card_id", data.getStringExtra("card_id"));
                RIDE_REQUEST.put("card_last_four", data.getStringExtra("card_last_four"));
            }
            initPayment(paymentType);
        }
    }
}

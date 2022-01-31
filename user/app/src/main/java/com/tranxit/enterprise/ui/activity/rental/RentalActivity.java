package com.tranxit.enterprise.ui.activity.rental;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseActivity;
import com.tranxit.enterprise.common.RecyclerTouchListener;
import com.tranxit.enterprise.data.network.model.EstimateFare;
import com.tranxit.enterprise.data.network.model.RentalHourPackage;
import com.tranxit.enterprise.data.network.model.Service;
import com.tranxit.enterprise.ui.activity.location_pick.LocationPickActivity;
import com.tranxit.enterprise.ui.activity.payment.PaymentActivity;
import com.tranxit.enterprise.ui.adapter.ServiceAdapterSingle;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tranxit.enterprise.MvpApplication.PICK_LOCATION_REQUEST_CODE;
import static com.tranxit.enterprise.MvpApplication.isCCAvenueEnabled;
import static com.tranxit.enterprise.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

public class RentalActivity extends BaseActivity implements RentBookingIView {

    @BindView(R.id.source)
    TextView source;
    @BindView(R.id.rental)
    Spinner rental;
    @BindView(R.id.car_type_rv)
    RecyclerView carTypeRv;
    @BindView(R.id.payment_type)
    TextView paymentType;
    @BindView(R.id.use_wallet)
    CheckBox useWallet;

    ServiceAdapterSingle adapter;
    private RentBookingPresenter<RentalActivity> presenter = new RentBookingPresenter<>();
    HashMap<String, Object> map = new HashMap<>();
    NumberFormat numberFormat;

    @Override
    public int getLayoutId() {
        return R.layout.activity_rental;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        numberFormat = getNumberFormat();
        carTypeRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        carTypeRv.setItemAnimator(new DefaultItemAnimator());
        carTypeRv.addOnItemTouchListener(new RecyclerTouchListener(activity(), carTypeRv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Service service = adapter.getItem(position);
                if (service != null) {
                    ArrayAdapter<RentalHourPackage> userAdapter = new ArrayAdapter<>(activity(), R.layout.spinner1, service.getRentalHourPackage());
                    rental.setAdapter(userAdapter);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if (RIDE_REQUEST.containsKey("s_address")) {
            source.setText(String.valueOf(RIDE_REQUEST.get("s_address")));
        }
        initPayment(paymentType);
        presenter.services();

    }


    @Override
    public void onSuccess(List<Service> services) {
        adapter = new ServiceAdapterSingle(activity(), services);
        carTypeRv.setAdapter(adapter);
        if (services.size() > 0) {
            ArrayAdapter<RentalHourPackage> userAdapter = new ArrayAdapter<>(activity(), R.layout.spinner1, services.get(0).getRentalHourPackage());
            rental.setAdapter(userAdapter);
        }
    }

    @Override
    public void onSuccessRequest(Object object) {
        finish();
    }

    @Override
    public void onSuccess(EstimateFare estimateFare) {
        map.put("distance", estimateFare.getDistance());
        //map.put("pricing_id", estimateFare.getPricingId());

        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.estimate_fare) + numberFormat.format(estimateFare.getEstimatedFare()))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.accept_and_confirm), (dialog, which) -> {
                    presenter.sendRequest(map);
                }).show();
    }


    @OnClick({R.id.source, R.id.get_pricing, R.id.payment_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.source:
                Intent sourceIntent = new Intent(this, LocationPickActivity.class);
                sourceIntent.putExtra("isHideDestination", true);
                startActivityForResult(sourceIntent, PICK_LOCATION_REQUEST_CODE);
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

        if (!map.containsKey("s_address")) {
            Toast.makeText(this, getString(R.string.please_enter_address), Toast.LENGTH_SHORT).show();
            return;
        }
        if (adapter == null) {
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

        RentalHourPackage rental = (RentalHourPackage) ((Spinner) findViewById(R.id.rental)).getSelectedItem();
        if (rental == null) {
            Toast.makeText(this, getString(R.string.please_select_rental_type), Toast.LENGTH_SHORT).show();
            return;
        }

        map.put("service_type", service.getId());
        map.put("rental_hours", rental.getId());
        map.put("use_wallet", useWallet.isChecked() ? 1 : 0);
        map.put("service_required", "rental");
        presenter.estimateFare(map);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_LOCATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (RIDE_REQUEST.containsKey("s_address")) {
                    source.setText(String.valueOf(RIDE_REQUEST.get("s_address")));
                } else {
                    source.setText("");
                }
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

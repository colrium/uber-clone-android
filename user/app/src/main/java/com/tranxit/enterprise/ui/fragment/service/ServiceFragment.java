package com.tranxit.enterprise.ui.fragment.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseActivity;
import com.tranxit.enterprise.base.BaseFragment;
import com.tranxit.enterprise.data.network.model.EstimateFare;
import com.tranxit.enterprise.data.network.model.RateCardResponse;
import com.tranxit.enterprise.data.network.model.Service;
import com.tranxit.enterprise.ui.activity.main.MainActivity;
import com.tranxit.enterprise.ui.activity.outstation.OutstationBookingActivity;
import com.tranxit.enterprise.ui.activity.payment.PaymentActivity;
import com.tranxit.enterprise.ui.adapter.ServiceAdapter;
import com.tranxit.enterprise.ui.fragment.book_ride.BookRideFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.tranxit.enterprise.MvpApplication.isCCAvenueEnabled;
import static com.tranxit.enterprise.base.BaseActivity.RIDE_REQUEST;
import static com.tranxit.enterprise.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

public class ServiceFragment extends BaseFragment implements ServiceIView {
    @BindView(R.id.service_rv)
    RecyclerView serviceRv;
    @BindView(R.id.capacity)
    TextView capacity;
    @BindView(R.id.payment_type)
    TextView paymentType;
    @BindView(R.id.error_layout)
    TextView errorLayout;
    Unbinder unbinder;
    private ServicePresenter<ServiceFragment> presenter = new ServicePresenter<>();

    ServiceAdapter adapter;
    List<Service> list = new ArrayList<>();


    public ServiceFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_service;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        adapter = new ServiceAdapter(getActivity(), list, capacity);
        serviceRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        serviceRv.setItemAnimator(new DefaultItemAnimator());
        serviceRv.setAdapter(adapter);
        ((BaseActivity) Objects.requireNonNull(getContext())).initPayment(paymentType);
        presenter.services();

        return view;
    }

    @OnClick({R.id.payment_type, R.id.get_pricing})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.payment_type:
                if (isCCAvenueEnabled)
                    startActivityForResult(new Intent(getActivity(), PaymentActivity.class), PICK_PAYMENT_METHOD);
                break;
            case R.id.get_pricing:
                Service service = adapter.getSelectedService();
                if (service != null) {
                    if (service.getStatus() == 1) {
                        RIDE_REQUEST.put("service_type", service.getId());
                        presenter.estimateFare(RIDE_REQUEST);
                    } else {
                        Toast.makeText(getContext(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onSuccess(List<Service> services) {

        list.clear();
        list.addAll(services);
        adapter.notifyDataSetChanged();

        if (list.isEmpty()) {
            errorLayout.setVisibility(View.VISIBLE);
        } else {
            errorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccess(RateCardResponse service) {

    }

    @Override
    public void onSuccess(EstimateFare estimateFare) {
        Service service = adapter.getSelectedService();
        if (service != null) if (estimateFare.getCityLimits() == 1)
            confirmPopup(estimateFare.getLimitMessage(), service, estimateFare);
        else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("service", service);
            bundle.putSerializable("estimate_fare", estimateFare);
            BookRideFragment bookRideFragment = new BookRideFragment();
            bookRideFragment.setArguments(bundle);
            ((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(bookRideFragment);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                if (jObjError.has("message"))
                    Toast.makeText(activity(), jObjError.optString("message"), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(activity(), jObjError.toString(), Toast.LENGTH_SHORT).show();
            } catch (Exception exp) {
                Log.e("Error", exp.getMessage());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            RIDE_REQUEST.put("payment_mode", data.getStringExtra("payment_mode"));
            if (data.getStringExtra("payment_mode").equals("CARD")) {
                RIDE_REQUEST.put("card_id", data.getStringExtra("card_id"));
                RIDE_REQUEST.put("card_last_four", data.getStringExtra("card_last_four"));
            }
            ((BaseActivity) Objects.requireNonNull(getContext())).initPayment(paymentType);

        }
    }

    void confirmPopup(String msg, Service service, EstimateFare estimateFare) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity());
        alertDialogBuilder
                .setMessage(msg)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> startActivity(new Intent(activity(), OutstationBookingActivity.class)))
                .setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

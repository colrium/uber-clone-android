package com.tranxit.enterprise.ui.fragment;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseBottomSheetDialogFragment;
import com.tranxit.enterprise.data.network.model.Datum;
import com.tranxit.enterprise.data.network.model.Payment;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tranxit.enterprise.base.BaseActivity.DATUM;

public class InvoiceDialogFragment extends BaseBottomSheetDialogFragment {


    @BindView(R.id.booking_id)
    TextView bookingId;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.payable)
    TextView payable;
    @BindView(R.id.wallet_detection_layout)
    LinearLayout wallet_detection_layout;
    @BindView(R.id.wallet_detection)
    TextView walletDetection;

    NumberFormat numberFormat = getNumberFormat();

    public InvoiceDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_invoice_dialog;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this, view);

        if (DATUM != null) {
            Datum datum = DATUM;
            FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(datum.getId()));
            bookingId.setText(datum.getBookingId());
            Payment payment = datum.getPayment();
            if (payment != null) {
                total.setText(numberFormat.format(payment.getTotal()));
                walletDetection.setText(numberFormat.format(payment.getWallet()));
                wallet_detection_layout.setVisibility(payment.getWallet() > 0 ? View.VISIBLE : View.GONE);
                payable.setText(numberFormat.format(payment.getPayable()));
            }
        }
    }

    @OnClick(R.id.close)
    public void onViewClicked() {
        dismiss();
    }

    @Override
    public void onError(Throwable e) {

    }
}

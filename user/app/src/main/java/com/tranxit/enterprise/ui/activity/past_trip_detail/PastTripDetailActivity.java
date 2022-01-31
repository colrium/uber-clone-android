package com.tranxit.enterprise.ui.activity.past_trip_detail;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseActivity;
import com.tranxit.enterprise.data.network.model.Datum;
import com.tranxit.enterprise.data.network.model.Payment;
import com.tranxit.enterprise.data.network.model.Provider;
import com.tranxit.enterprise.data.network.model.Rating;
import com.tranxit.enterprise.ui.fragment.InvoiceDialogFragment;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class PastTripDetailActivity extends BaseActivity {

    @BindView(R.id.static_map)
    ImageView staticMap;
    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.finished_at)
    TextView finishedAt;
    @BindView(R.id.booking_id)
    TextView bookingId;
    @BindView(R.id.payment_mode)
    TextView paymentMode;
    @BindView(R.id.payable)
    TextView payable;
    @BindView(R.id.user_comment)
    TextView userComment;
    @BindView(R.id.view_receipt)
    Button viewReceipt;

    @BindView(R.id.lblSource)
    TextView lblSource;
    @BindView(R.id.lblDestination)
    TextView lblDestination;

    NumberFormat numberFormat;

    @Override
    public int getLayoutId() {
        return R.layout.activity_past_trip_detail;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        numberFormat = getNumberFormat();
        if (DATUM != null) {
            Datum datum = DATUM;
            bookingId.setText(datum.getBookingId());
            finishedAt.setText(datum.getFinishedAt());
            Glide.with(activity()).load(datum.getStaticMap()).apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background).dontAnimate().error(R.drawable.ic_launcher_background)).into(staticMap);

            lblSource.setText(datum.getSAddress());
            lblDestination.setText(datum.getDAddress());
            Provider provider = datum.getProvider();
            if (provider != null) {
                Glide.with(activity()).load(provider.getAvatar()).apply(RequestOptions.placeholderOf(R.drawable.user).dontAnimate().error(R.drawable.user)).into(avatar);
                name.setText(String.format("%s %s", provider.getFirstName(), provider.getLastName()));
                rating.setRating(Float.parseFloat(provider.getRating()));
            }

            initPayment(datum.getPaymentMode());

            Payment payment = datum.getPayment();
            if (payment != null) {
                payable.setText(numberFormat.format(payment.getTotal()));
            }

            Rating rating = datum.getRating();
            if (rating != null) {
                userComment.setText(rating.getUserComment());
            }
        }

    }

    void initPayment(String mode) {

        switch (mode) {
            case "CASH":
                paymentMode.setText(getString(R.string.cash));
                paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, 0, 0);
                break;
            case "CARD":
                paymentMode.setText(getString(R.string.card));
                paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_visa, 0, 0, 0);
                break;
            case "PAYPAL":
                paymentMode.setText(getString(R.string.paypal));
                paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_paypal, 0, 0, 0);
                break;
            case "WALLET":
                paymentMode.setText(getString(R.string.wallet));
                paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, 0, 0);
                break;
            case "CC_AVENUE":
                paymentMode.setText(getString(R.string.cc_avenue));
                paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_visa, 0, 0, 0);
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.view_receipt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.view_receipt:
                InvoiceDialogFragment invoiceDialogFragment = new InvoiceDialogFragment();
                invoiceDialogFragment.show(getSupportFragmentManager(), invoiceDialogFragment.getTag());
                break;
        }
    }
}

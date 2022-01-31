package com.tranxit.enterprise.ui.activity.wallet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseActivity;
import com.tranxit.enterprise.common.cc_avenue.AvenuesParams;
import com.tranxit.enterprise.common.cc_avenue.CCAvenueWebViewActivity;
import com.tranxit.enterprise.common.cc_avenue.ServiceUtility;
import com.tranxit.enterprise.data.SharedHelper;
import com.tranxit.enterprise.data.network.model.User;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tranxit.enterprise.MvpApplication.isCCAvenueEnabled;


public class WalletActivity extends BaseActivity implements WalletIView {

    private static final int CC_AVENUE_METHOD = 43;
    @BindView(R.id.wallet_balance)
    TextView walletBalance;
    @BindView(R.id.amount)
    EditText amount;
    @BindView(R.id._199)
    Button _199;
    @BindView(R.id._599)
    Button _599;
    @BindView(R.id._1099)
    Button _1099;
    @BindView(R.id.add_amount)
    Button addAmount;
    @BindView(R.id.cvAddMoneyContainer)
    CardView cvAddMoneyContainer;

    NumberFormat numberFormat;
    private WalletPresenter<WalletActivity> presenter = new WalletPresenter<>();

    String regexNumber = "^(\\d{0,9}\\.\\d{1,4}|\\d{1,9})$";

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        numberFormat = getNumberFormat();
        presenter.profile();
        if (isCCAvenueEnabled) {
            cvAddMoneyContainer.setVisibility(View.VISIBLE);
            addAmount.setVisibility(View.VISIBLE);
        } else {
            cvAddMoneyContainer.setVisibility(View.GONE);
            addAmount.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick({R.id._199, R.id._599, R.id._1099, R.id.add_amount})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id._199:
                amount.setText(String.valueOf("199"));
                break;
            case R.id._599:
                amount.setText(String.valueOf("599"));
                break;
            case R.id._1099:
                amount.setText(String.valueOf("1099"));
                break;
            case R.id.add_amount:
                if (!amount.getText().toString().trim().matches(regexNumber)) {
                    Toast.makeText(activity(), getString(R.string.invalid_amount), Toast.LENGTH_SHORT).show();
                    return;
                }

                initCCAvenuePayment(amount.getText().toString());

                break;
        }
    }

    void initCCAvenuePayment(String amount) {

        String accessCode = "AVVH81FJ20BW35HVWB";
        String merchantId = "194437";
        String orderId = "wallet-" + SharedHelper.getIntKey(this, "user_id");
        String redirectUrl = "https://diff.com/indipay/ccavanue/response";
        String cancelUrl = "https://diff.com/indipay/ccavanue/cancel/response";
        String rsaKeyUrl = "https://diff.com/rsa/key";

        String vAccessCode = ServiceUtility.chkNull(accessCode).toString().trim();
        String vMerchantId = ServiceUtility.chkNull(merchantId).toString().trim();
        String vCurrency = ServiceUtility.chkNull("INR").toString().trim();
        String vAmount = ServiceUtility.chkNull(amount).toString().trim();
        if (!vAccessCode.equals("") && !vMerchantId.equals("") && !vCurrency.equals("") && !vAmount.equals("")) {
            Intent intent = new Intent(activity(), CCAvenueWebViewActivity.class);

            intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull(accessCode).toString().trim());
            intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull(merchantId).toString().trim());
            intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(orderId).toString().trim());
            intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull("INR").toString().trim());
            intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull(amount).toString().trim());

            intent.putExtra(AvenuesParams.REDIRECT_URL, ServiceUtility.chkNull(redirectUrl).toString().trim());
            intent.putExtra(AvenuesParams.CANCEL_URL, ServiceUtility.chkNull(cancelUrl).toString().trim());
            intent.putExtra(AvenuesParams.RSA_KEY_URL, ServiceUtility.chkNull(rsaKeyUrl).toString().trim());

            startActivityForResult(intent, CC_AVENUE_METHOD);
        } else {
            Toast.makeText(activity(), "All parameters are mandatory.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CC_AVENUE_METHOD && resultCode == Activity.RESULT_OK && data != null) {
            Toast.makeText(this, getString(R.string._added_to_your_wallet, numberFormat.getCurrency().getCurrencyCode() + amount.getText().toString()), Toast.LENGTH_SHORT).show();
            amount.setText("");
            presenter.profile();
        }
    }

    @Override
    public void onSuccess(User user) {
        walletBalance.setText(numberFormat.format(user.getWalletBalance()));
        SharedHelper.putKey(this, "user_id", user.getId());
        SharedHelper.putKey(this, "wallet_amount", user.getWalletBalance());
    }

    @Override
    public void onError(Throwable e) {
        hideLoading();
        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
    }
}

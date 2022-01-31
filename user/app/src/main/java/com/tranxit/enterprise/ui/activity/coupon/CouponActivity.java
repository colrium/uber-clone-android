package com.tranxit.enterprise.ui.activity.coupon;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CouponActivity extends BaseActivity implements CouponIView {

    @BindView(R.id.promocode)
    EditText promocode;
    @BindView(R.id.add_coupon)
    Button addCoupon;

    private CouponPresenter<CouponActivity> presenter = new CouponPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_coupon;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        promocode.setText("");
        presenter.attachView(this);
    }

    @Override
    public void onSuccess(Object object) {
        promocode.setText("");
        hideLoading();
        Toast.makeText(this, "Coupon code applied successfully!", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onError(Throwable e) {
        hideLoading();
        Toast.makeText(this, "Coupon code is not valid", Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.add_coupon)
    public void onViewClicked() {

        if (promocode.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.invalid_coupon_code, Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading();
        presenter.coupon(promocode.getText().toString());

    }
}

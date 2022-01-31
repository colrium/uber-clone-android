package com.tranxit.enterprise.ui.activity.coupon;


import com.tranxit.enterprise.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface CouponIPresenter<V extends CouponIView> extends MvpPresenter<V> {
    void coupon(String promoCode);
}

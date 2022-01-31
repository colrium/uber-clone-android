package com.tranxit.enterprise.ui.fragment.coupon_history;


import com.tranxit.enterprise.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface CouponHistoryIPresenter<V extends CouponHistoryIView> extends MvpPresenter<V> {
    void coupon();
}

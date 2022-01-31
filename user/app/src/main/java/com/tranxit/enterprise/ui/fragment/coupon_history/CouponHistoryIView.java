package com.tranxit.enterprise.ui.fragment.coupon_history;

import com.tranxit.enterprise.base.MvpView;
import com.tranxit.enterprise.data.network.model.Coupon;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface CouponHistoryIView extends MvpView {
    void onSuccess(List<Coupon> couponList);
    void onError(Throwable e);
}

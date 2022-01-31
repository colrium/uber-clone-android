package com.tranxit.enterprise.ui.activity.coupon;


import com.tranxit.enterprise.base.BasePresenter;
import com.tranxit.enterprise.data.network.APIClient;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by santhosh@appoets.com on 02-05-2018.
 */
public class CouponPresenter<V extends CouponIView> extends BasePresenter<V> implements CouponIPresenter<V> {


    @Override
    public void coupon(String promoCode) {
        Observable modelObservable = APIClient.getAPIClient().coupon(promoCode);

        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> getMvpView().onSuccess((Object) trendsResponse),
                        throwable -> getMvpView().onError((Throwable) throwable));
    }
}

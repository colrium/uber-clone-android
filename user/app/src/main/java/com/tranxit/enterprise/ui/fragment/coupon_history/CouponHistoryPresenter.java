package com.tranxit.enterprise.ui.fragment.coupon_history;


import com.tranxit.enterprise.base.BasePresenter;
import com.tranxit.enterprise.data.network.APIClient;
import com.tranxit.enterprise.data.network.model.Coupon;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by santhosh@appoets.com on 02-05-2018.
 */
public class CouponHistoryPresenter<V extends CouponHistoryIView> extends BasePresenter<V> implements CouponHistoryIPresenter<V> {


    @Override
    public void coupon() {
        Observable modelObservable = APIClient.getAPIClient().coupon();

        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> getMvpView().onSuccess((List<Coupon>) trendsResponse),
                        throwable -> getMvpView().onError((Throwable) throwable));
    }
}

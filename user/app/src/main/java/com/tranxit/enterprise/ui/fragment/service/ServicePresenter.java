package com.tranxit.enterprise.ui.fragment.service;

import com.tranxit.enterprise.base.BasePresenter;
import com.tranxit.enterprise.data.network.APIClient;
import com.tranxit.enterprise.data.network.model.EstimateFare;
import com.tranxit.enterprise.data.network.model.RateCardResponse;
import com.tranxit.enterprise.data.network.model.Service;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by santhosh@appoets.com on 02-05-2018.
 */
public class ServicePresenter<V extends ServiceIView> extends BasePresenter<V> implements ServiceIPresenter<V> {


    @Override
    public void services() {
        Observable modelObservable = APIClient.getAPIClient().services();
        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> getMvpView().onSuccess((List<Service>) trendsResponse),
                        throwable -> getMvpView().onError((Throwable) throwable));
    }

    @Override
    public void estimateFare(HashMap<String, Object> obj) {
        Observable modelObservable = APIClient.getAPIClient().estimateFare(obj);
        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> getMvpView().onSuccess((EstimateFare) trendsResponse),
                        throwable -> getMvpView().onError((Throwable) throwable));
    }


    @Override
    public void estimateFareService(HashMap<String, Object> obj) {
        Observable modelObservable = APIClient.getAPIClient().estimateFareService(obj);
        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> getMvpView().onSuccess((RateCardResponse) trendsResponse),
                        throwable -> getMvpView().onError((Throwable) throwable));
    }
}

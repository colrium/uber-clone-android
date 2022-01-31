package com.tranxit.enterprise.ui.activity.outstation;


import android.widget.Toast;

import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BasePresenter;
import com.tranxit.enterprise.data.network.APIClient;
import com.tranxit.enterprise.data.network.model.EstimateFare;
import com.tranxit.enterprise.data.network.model.Service;
import com.tranxit.enterprise.ui.adapter.ServiceAdapterSingle;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by santhosh@appoets.com on 02-05-2018.
 */
public class OutstationBookingPresenter<V extends OutstationBookingIView> extends BasePresenter<V> implements OutstationBookingIPresenter<V> {
    @Override
    public void services() {

        Observable modelObservable = APIClient.getAPIClient().services();

        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> {
                            ServiceAdapterSingle adapter = new ServiceAdapterSingle(getMvpView().activity(), (List<Service>) trendsResponse);
                            getMvpView().onSuccess(adapter);
                        },
                        (Consumer) throwable -> getMvpView().onError((Throwable) throwable));
    }

    @Override
    public void sendRequest(HashMap<String, Object> params) {
        Observable modelObservable = APIClient.getAPIClient().sendRequest(params);
        getMvpView().showLoading();
        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> {
                            getMvpView().hideLoading();
                            Toast.makeText(activity(), R.string.new_outstation_request_created, Toast.LENGTH_SHORT).show();
                            OutstationBookingPresenter.this.getMvpView().onSuccessRequest((Object) trendsResponse);
                        },
                        throwable -> {
                            getMvpView().hideLoading();
                            OutstationBookingPresenter.this.getMvpView().onError((Throwable) throwable);
                        });
    }

    @Override
    public void estimateFare(HashMap<String, Object> params) {
        getMvpView().showLoading();
        Observable modelObservable = APIClient.getAPIClient().estimateFare(params);
        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> {
                            getMvpView().hideLoading();
                            OutstationBookingPresenter.this.getMvpView().onSuccess((EstimateFare) trendsResponse);
                        },
                        (Consumer) throwable -> {
                            getMvpView().hideLoading();
                            OutstationBookingPresenter.this.getMvpView().onError((Throwable) throwable);
                        });
    }
}

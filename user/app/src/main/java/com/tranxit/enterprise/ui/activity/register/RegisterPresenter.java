package com.tranxit.enterprise.ui.activity.register;

import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.tranxit.enterprise.base.BasePresenter;
import com.tranxit.enterprise.data.network.APIClient;
import com.tranxit.enterprise.data.network.model.MyOTP;
import com.tranxit.enterprise.data.network.model.SettingsResponse;
import com.tranxit.enterprise.data.network.model.Status;
import com.tranxit.enterprise.data.network.model.Token;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by santhosh@appoets.com on 02-05-2018.
 */
public class RegisterPresenter<V extends RegisterIView> extends BasePresenter<V> implements RegisterIPresenter<V> {
    @Override
    public void register(HashMap<String, Object> obj) {

        Observable modelObservable = APIClient.getAPIClient().register(obj);
        getMvpView().hideLoading();
        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> {
                            getMvpView().hideLoading();
                            RegisterPresenter.this.getMvpView().onSuccess((Token) trendsResponse);
                        },
                        (Consumer) throwable -> {
                            getMvpView().hideLoading();
                            RegisterPresenter.this.getMvpView().onError((Throwable) throwable);
                        });
    }

    @Override
    public void sendOTP(Object mobile) {

        Observable modelObservable = APIClient.getAPIClient().sendOtp(mobile);
        getMvpView().showLoading();
        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> {
                            getMvpView().hideLoading();
                            RegisterPresenter.this.getMvpView().onSuccess((MyOTP) trendsResponse);
                        },
                        (Consumer) throwable -> {
                            getMvpView().hideLoading();
                            RegisterPresenter.this.getMvpView().onError((Throwable) throwable);
                        });
    }

    @Override
    public void verifyMobileAlreadyExits(String mobile) {
        getMvpView().showLoading();
        Observable modelObservable = APIClient.getAPIClient().verifyMobileAlreadyExits(mobile);

        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> {
                    RegisterPresenter.this.getMvpView().hideLoading();
                    Status status = (Status) trendsResponse;
                    if (status.getStatus()) {
                        Toast.makeText(RegisterPresenter.this.getMvpView().activity(), "Mobile Already Registered", Toast.LENGTH_SHORT).show();
                    } else {
                        RegisterPresenter.this.sendOTP(mobile);
                    }
                },
                        (Consumer) throwable -> {
                            getMvpView().hideLoading();
                            RegisterPresenter.this.getMvpView().onError((Throwable) throwable);
                        });
    }


    @Override
    public void getSettings() {
        Observable modelObservable = APIClient.getAPIClient().getSettings();
        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response ->{
                    RegisterPresenter.this.getMvpView().onSuccess((SettingsResponse) response);
                }, (Consumer) throwable -> {
                    RegisterPresenter.this.getMvpView().onError((Throwable) throwable);
                });
    }
}

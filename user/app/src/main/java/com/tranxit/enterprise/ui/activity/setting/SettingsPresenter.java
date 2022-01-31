package com.tranxit.enterprise.ui.activity.setting;

import com.tranxit.enterprise.base.BasePresenter;
import com.tranxit.enterprise.data.network.APIClient;
import com.tranxit.enterprise.data.network.model.AddressResponse;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by santhosh@appoets.com on 02-05-2018.
 */
public class SettingsPresenter<V extends SettingsIView> extends BasePresenter<V> implements SettingsIPresenter<V> {

    @Override
    public void addAddress(HashMap<String, Object> params) {
        Observable modelObservable = APIClient.getAPIClient().addAddress(params);

        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> getMvpView().onSuccessAddress((Object) trendsResponse),
                        throwable -> getMvpView().onError((Throwable) throwable));
    }
    @Override
    public void deleteAddress(Integer id, HashMap<String, Object> params) {
        Observable modelObservable = APIClient.getAPIClient().deleteAddress(id);

        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> getMvpView().onSuccessAddress((Object) trendsResponse),
                        throwable -> getMvpView().onError((Throwable) throwable));
    }
    @Override
    public void address() {
        Observable modelObservable = APIClient.getAPIClient().address();

        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> getMvpView().onSuccess((AddressResponse) trendsResponse),
                        throwable -> getMvpView().onError((Throwable) throwable));
    }
}

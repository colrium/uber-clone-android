package com.tranxit.enterprise.ui.fragment.book_ride;

import android.widget.Toast;

import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BasePresenter;
import com.tranxit.enterprise.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by santhosh@appoets.com on 02-05-2018.
 */
public class BookRidePresenter<V extends BookRideIView> extends BasePresenter<V> implements BookRideIPresenter<V> {

    @Override
    public void rideNow(HashMap<String, Object> obj) {
        Observable modelObservable = APIClient.getAPIClient().sendRequest(obj);
        getMvpView().showLoading();
        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> {
                            getMvpView().hideLoading();
                            Toast.makeText(activity(), R.string.new_request_created, Toast.LENGTH_SHORT).show();
                            BookRidePresenter.this.getMvpView().onSuccess((Object) trendsResponse);
                        },
                        throwable -> {
                            getMvpView().hideLoading();
                            BookRidePresenter.this.getMvpView().onError((Throwable) throwable);
                        });
    }
}

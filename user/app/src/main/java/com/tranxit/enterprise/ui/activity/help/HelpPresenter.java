package com.tranxit.enterprise.ui.activity.help;


import com.tranxit.enterprise.base.BasePresenter;
import com.tranxit.enterprise.data.network.APIClient;
import com.tranxit.enterprise.data.network.model.Help;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by santhosh@appoets.com on 02-05-2018.
 */
public class HelpPresenter<V extends HelpIView> extends BasePresenter<V> implements HelpIPresenter<V> {


    @Override
    public void help() {
        Observable modelObservable = APIClient.getAPIClient().help();

        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> getMvpView().onSuccess((Help) trendsResponse),
                        throwable -> getMvpView().onError((Throwable) throwable));
    }
}

package com.tranxit.enterprise.ui.activity.outstation;


import com.tranxit.enterprise.base.MvpView;
import com.tranxit.enterprise.data.network.model.EstimateFare;
import com.tranxit.enterprise.ui.adapter.ServiceAdapterSingle;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface OutstationBookingIView extends MvpView {

    void onSuccess(ServiceAdapterSingle adapter);

    void onSuccessRequest(Object object);
    void onSuccess(EstimateFare estimateFare);
}

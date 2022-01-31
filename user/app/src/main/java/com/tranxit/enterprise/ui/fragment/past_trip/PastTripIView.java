package com.tranxit.enterprise.ui.fragment.past_trip;

import com.tranxit.enterprise.base.MvpView;
import com.tranxit.enterprise.data.network.model.Datum;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface PastTripIView extends MvpView{
    void onSuccess(List<Datum> datumList);
    void onError(Throwable e);
}

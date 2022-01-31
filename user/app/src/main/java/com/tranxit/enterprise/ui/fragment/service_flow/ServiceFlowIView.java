package com.tranxit.enterprise.ui.fragment.service_flow;

import com.tranxit.enterprise.base.MvpView;
import com.tranxit.enterprise.data.network.model.DataResponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ServiceFlowIView extends MvpView{
    void onSuccess(DataResponse dataResponse);
    void onError(Throwable e);
}

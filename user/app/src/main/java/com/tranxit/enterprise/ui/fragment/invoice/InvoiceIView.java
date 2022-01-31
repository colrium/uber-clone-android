package com.tranxit.enterprise.ui.fragment.invoice;

import com.tranxit.enterprise.base.MvpView;
import com.tranxit.enterprise.data.network.model.Message;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface InvoiceIView extends MvpView{
    void onSuccess(Message message);
    void onError(Throwable e);
}

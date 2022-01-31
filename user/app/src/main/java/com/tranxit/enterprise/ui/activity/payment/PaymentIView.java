package com.tranxit.enterprise.ui.activity.payment;

import com.tranxit.enterprise.base.MvpView;
import com.tranxit.enterprise.data.network.model.Card;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface PaymentIView extends MvpView{
    void onSuccess(Object card);
    void onSuccess(List<Card> cards);
    void onError(Throwable e);
}

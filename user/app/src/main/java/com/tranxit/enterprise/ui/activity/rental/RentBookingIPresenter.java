package com.tranxit.enterprise.ui.activity.rental;


import com.tranxit.enterprise.base.MvpPresenter;

import java.util.HashMap;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface RentBookingIPresenter<V extends RentBookingIView> extends MvpPresenter<V> {
    void services();
   void sendRequest(HashMap<String, Object> parms);
    void estimateFare(HashMap<String, Object> parms);
}

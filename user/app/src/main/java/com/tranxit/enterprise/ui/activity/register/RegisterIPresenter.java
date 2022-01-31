package com.tranxit.enterprise.ui.activity.register;

import com.tranxit.enterprise.base.MvpPresenter;

import java.util.HashMap;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface RegisterIPresenter<V extends RegisterIView> extends MvpPresenter<V>{
    void register(HashMap<String, Object> obj);
    void sendOTP(Object obj);
    void verifyMobileAlreadyExits(String mobile);
    void getSettings();
}

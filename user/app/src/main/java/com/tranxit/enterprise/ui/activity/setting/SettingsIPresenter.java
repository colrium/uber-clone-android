package com.tranxit.enterprise.ui.activity.setting;

import com.tranxit.enterprise.base.MvpPresenter;

import java.util.HashMap;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SettingsIPresenter<V extends SettingsIView> extends MvpPresenter<V>{
    void addAddress(HashMap<String, Object> params);
    void deleteAddress(Integer id, HashMap<String, Object> params);
    void address();
}

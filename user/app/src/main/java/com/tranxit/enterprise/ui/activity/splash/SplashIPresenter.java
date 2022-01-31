package com.tranxit.enterprise.ui.activity.splash;

import com.tranxit.enterprise.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SplashIPresenter<V extends SplashIView> extends MvpPresenter<V>{
    void profile();
}

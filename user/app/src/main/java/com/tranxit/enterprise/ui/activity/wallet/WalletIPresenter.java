package com.tranxit.enterprise.ui.activity.wallet;

import com.tranxit.enterprise.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface WalletIPresenter<V extends WalletIView> extends MvpPresenter<V>{
    void profile();
}

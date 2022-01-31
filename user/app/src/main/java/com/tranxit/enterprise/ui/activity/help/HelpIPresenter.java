package com.tranxit.enterprise.ui.activity.help;


import com.tranxit.enterprise.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface HelpIPresenter<V extends HelpIView> extends MvpPresenter<V> {
    void help();
}

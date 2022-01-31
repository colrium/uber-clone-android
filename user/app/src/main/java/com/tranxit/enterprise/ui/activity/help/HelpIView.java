package com.tranxit.enterprise.ui.activity.help;

import com.tranxit.enterprise.base.MvpView;
import com.tranxit.enterprise.data.network.model.Help;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface HelpIView extends MvpView {
    void onSuccess(Help help);
    void onError(Throwable e);
}

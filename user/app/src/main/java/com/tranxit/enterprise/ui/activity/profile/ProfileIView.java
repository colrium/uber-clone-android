package com.tranxit.enterprise.ui.activity.profile;

import com.tranxit.enterprise.base.MvpView;
import com.tranxit.enterprise.data.network.model.User;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ProfileIView extends MvpView{
    void onSuccess(User user);
    void onError(Throwable e);
}

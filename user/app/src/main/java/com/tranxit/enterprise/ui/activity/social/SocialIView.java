package com.tranxit.enterprise.ui.activity.social;

import com.tranxit.enterprise.base.MvpView;
import com.tranxit.enterprise.data.network.model.Token;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SocialIView extends MvpView{
    void onSuccess(Token token);
    void onError(Throwable e);
}

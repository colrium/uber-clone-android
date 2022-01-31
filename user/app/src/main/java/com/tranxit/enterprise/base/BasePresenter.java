package com.tranxit.enterprise.base;

import android.app.Activity;

import com.tranxit.enterprise.MvpApplication;

import io.reactivex.disposables.CompositeDisposable;

public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {
    public CompositeDisposable compositeDisposable;
    public BasePresenter() {
        compositeDisposable=new CompositeDisposable();
    }

    private V mMvpView;

    @Override
    public Activity activity() {
        return getMvpView().activity();
    }

    @Override
    public MvpApplication appContext() {
        return MvpApplication.getInstance();
    }

    @Override
    public void attachView(V mvpView) {
        mMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mMvpView = null;
        compositeDisposable.dispose();
    }

    public boolean isViewAttached() {
        return mMvpView != null;
    }

    public V getMvpView() {
        return mMvpView;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }

}

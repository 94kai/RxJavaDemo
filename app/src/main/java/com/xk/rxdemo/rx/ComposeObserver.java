package com.xk.rxdemo.rx;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by xuekai on 2018/2/7.
 */

public abstract class ComposeObserver<T> extends DisposableObserver<T> {
    private static final String TAG = "ComposeObserver";
    private CompositeDisposable disposables;
//    private BaseActivity activity;
    private boolean showLoading;

    public ComposeObserver(CompositeDisposable disposables) {
        super();
        this.disposables = disposables;
    }

//    public ComposeObserver(CompositeDisposable disposables, BaseActivity activity, boolean showLoading) {
//        this(disposables);
//        this.activity = activity;
//        this.showLoading = showLoading;
//    }

    /**
     * 订阅开始时调用
     */
    @Override
    public void onStart() {
        super.onStart();
        disposables.add(this);
        if (showLoading) {
//            if (activity!=null) {
//                activity.showLoadingView();
//            }
        }
    }

    @Override
    public void onComplete() {
        disposables.remove(this);
        disposables = null;
        if (showLoading) {
//            if (activity!=null) {
//                activity.hideLoadingView();
//                activity=null;
//            }
        }
    }


    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        disposables.remove(this);
        disposables = null;
        if (showLoading) {
//            if (activity!=null) {
//                activity.hideLoadingView();
//                activity=null;
//            }
        }
    }


    @Override
    public void onNext(T t) {
        onSuccess(t);
    }


    public abstract void onSuccess(T t);

}

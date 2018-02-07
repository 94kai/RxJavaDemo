package com.xk.rxdemo.net;

/**
 * Created by xuekai on 2018/2/7.
 */

public interface Callback<T> {
    void onError(Throwable throwable);

    void onSuccess(T t);
}

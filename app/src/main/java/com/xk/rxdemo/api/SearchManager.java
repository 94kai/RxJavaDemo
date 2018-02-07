package com.xk.rxdemo.api;

import com.xk.rxdemo.bean.NetData;
import com.xk.rxdemo.bean.Product;
import com.xk.rxdemo.net.Callback;
import com.xk.rxdemo.net.HttpUtil;
import com.xk.rxdemo.rx.RxSchedulerHelper;

import io.reactivex.Observable;

/**
 * 搜索相关接口
 * Created by xuekai on 2018/2/7.
 */

public class SearchManager {

    public static Observable<Product> getProduct1(int functionId) {

        return Observable.<Product>create(emitter -> HttpUtil.asyncRequest(functionId, new Callback<NetData<Product>>() {
            @Override
            public void onError(Throwable throwable) {
                emitter.onError(throwable);
            }

            @Override
            public void onSuccess(NetData<Product> data) {
                if (data.getCode() == 0) {//success
                    emitter.onNext(data.getData());
                    emitter.onComplete();
                } else {//error
                    //构造一个throwable，也可以自定义
                    Throwable throwable = new Throwable(data.getMsg());
                    emitter.onError(throwable);
                }
            }
        })).compose(RxSchedulerHelper.io_main());
    }

    public static Observable<Product> getProduct2(int functionId) {

        return Observable.<Product>create(emitter -> {
            NetData<Product> data = HttpUtil.syncRequest(functionId);
            if (data == null) {
                Throwable throwable = new Throwable("网络没通");
                emitter.onError(throwable);
            } else {
                if (data.getCode() == 0) {//success
                    emitter.onNext(data.getData());
                    emitter.onComplete();
                } else {//error
                    //构造一个throwable，也可以自定义
                    Throwable throwable = new Throwable(data.getMsg());
                    emitter.onError(throwable);
                }
            }
        }).compose(RxSchedulerHelper.io_main());
    }
}


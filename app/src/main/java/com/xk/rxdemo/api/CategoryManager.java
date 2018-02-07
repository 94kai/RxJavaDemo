package com.xk.rxdemo.api;

import com.xk.rxdemo.bean.NetData;
import com.xk.rxdemo.bean.Product;
import com.xk.rxdemo.net.Callback;
import com.xk.rxdemo.net.HttpUtil;
import com.xk.rxdemo.rx.RxSchedulerHelper;

import java.util.List;

import io.reactivex.Observable;

/**
 * 分类相关接口
 * Created by xuekai on 2018/2/7.
 */

public class CategoryManager {

    public static Observable<List<Product>> getProductList(int functionId) {

        return Observable.<List<Product>>create(emitter -> HttpUtil.asyncRequest(functionId, new Callback<NetData<List<Product>>>() {
            @Override
            public void onError(Throwable throwable) {
                emitter.onError(throwable);
            }

            @Override
            public void onSuccess(NetData<List<Product>> data) {
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

}

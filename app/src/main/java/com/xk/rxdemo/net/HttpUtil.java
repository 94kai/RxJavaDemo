package com.xk.rxdemo.net;

/**
 * Created by xuekai on 2018/2/7.
 */

public class HttpUtil {
    /**
     * 异步请求,不用管线程，会在rx中处理
     */
    public static <T> void asyncRequest(int function, Callback<T> callback) {
        T t = null;
        Throwable throwable = null;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (function == 0) {
            callback.onSuccess(t);
        } else {
            callback.onError(throwable);
        }
    }


    /**
     * 同步步请求,不用管线程，会在rx中处理
     */
    public static <T> T syncRequest(int function) {
        T t = null;
        try {
            Thread.sleep(1000);

            //拿到数据，赋值
            t = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            //请求出错，置空
            t = null;
        }
        return t;
    }
}

package com.xk.rxdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.xk.rxdemo.api.CategoryManager;
import com.xk.rxdemo.api.SearchManager;
import com.xk.rxdemo.bean.Product;
import com.xk.rxdemo.bean.UIProduct;
import com.xk.rxdemo.rx.ComposeObserver;
import com.xk.rxdemo.rx.RxSchedulerHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        setContentView(R.layout.activity_main);

    }


    public void method() {

        //线程切换
        Observable
                .create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                        //子线程
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .compose(RxSchedulerHelper.io_main())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        //主线程
                    }
                });


        //消灭回调地狱
        //1.普通写法
        new Thread(new Runnable() {
            @Override
            public void run() {
                netWork(1, new Callback() {
                    @Override
                    public void onSuccess(int result) {
                        netWork(result, new Callback() {
                            @Override
                            public void onSuccess(int result) {
                                netWork(result, new Callback() {
                                    @Override
                                    public void onSuccess(int result) {
                                        netWork(result, new Callback() {
                                            @Override
                                            public void onSuccess(int result) {
                                                netWork(result, new Callback() {
                                                    @Override
                                                    public void onSuccess(int result) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }).start();

        //2. rx写法(用lambda写会很简单)
        netWork(1)
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer integer) throws Exception {
                        return netWork(integer);
                    }
                })
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer integer) throws Exception {
                        return netWork(integer);
                    }
                })
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer integer) throws Exception {
                        return netWork(integer);
                    }
                })
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer integer) throws Exception {
                        return netWork(integer);
                    }
                })
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer integer) throws Exception {
                        return netWork(integer);
                    }
                })
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer integer) throws Exception {
                        return netWork(integer);
                    }
                })
                .compose(RxSchedulerHelper.io_main())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Toast.makeText(MainActivity.this, integer, Toast.LENGTH_SHORT).show();
                    }
                });


        //简单的
        SearchManager.getProduct1(1)
                .subscribe(this::updateUIByProduct);

        //map   订阅上游之后，下游拿到的结果，经过一个转换，直接会在尽头使用。
        //flatmap 订阅上游之后，下游拿到的结果，还要再进行订阅


        //map的使用
        SearchManager.getProduct1(1)
                .map(UIProduct::new)
                .subscribe(this::updateUIByUIProduct);

        //flatmap的使用
        CategoryManager.getProductList(1)
                .flatMap(products -> {
                    int functionId = products.get(0).hashCode();
                    return SearchManager.getProduct1(functionId);
                })
                .subscribe(this::updateUIByProduct);

        //flatmap结合map
        CategoryManager.getProductList(1)
                .flatMap(products -> {
                    int functionId = products.get(0).hashCode();
                    return SearchManager.getProduct1(functionId);
                })
                .map(UIProduct::new)
                .subscribe(this::updateUIByUIProduct);


        //zip操作符
        Observable.zip(CategoryManager.getProductList(1), SearchManager.getProduct1(1), (products, product) -> {
            int i = products.size() + product.hashCode();
            return "" + i;
        })
                .compose(RxSchedulerHelper.io_main())
                .subscribe(s -> Log.e(TAG, s));

        //对error的处理
        //1.
        CategoryManager.getProductList(1)
                .flatMap(new Function<List<Product>, ObservableSource<? extends Product>>() {
                    @Override
                    public ObservableSource<? extends Product> apply(List<Product> products) throws Exception {
                        int functionId = products.get(0).hashCode();
                        if (functionId == 0) {
                            return SearchManager.getProduct1(functionId);
                        } else {
                            return Observable.error(new Throwable("第一个接口出错"));
                        }
                    }
                })
                //这里其实有问题，没有处理错误
                .subscribe(this::updateUIByProduct);
        //2.
        SearchManager.getProduct1(1)
                .subscribe
                        (
                                new Consumer<Product>() {
                                    @Override
                                    public void accept(Product product) throws Exception {
                                        MainActivity.this.updateUIByProduct(product);
                                    }
                                },
                                new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        //根据throwable显示ui
                                        if (throwable instanceof MyThrowable) {

                                        } else {

                                        }
                                    }
                                }
                        );

        //3.
        SearchManager.getProduct1(1)
                .subscribe(new DisposableObserver<Product>() {
                    @Override
                    public void onNext(Product product) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //4.
        SearchManager.getProduct1(1)
                .subscribe(new ComposeObserver<Product>(compositeDisposable) {
                    @Override
                    public void onSuccess(Product product) {

                    }
                });

        //5.
        SearchManager.getProduct1(1)
                .subscribe(new ComposeObserver<Product>(compositeDisposable) {
                    @Override
                    public void onSuccess(Product product) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        // TODO: by xk 2018/2/7 11:16

                    }
                });


        //内存泄漏
        Disposable subscribe = SearchManager.getProduct1(1)
                .subscribe(this::updateUIByProduct);

        //取消订阅
        //1.直接取消
        if (!subscribe.isDisposed()) {
            subscribe.dispose();
        }
        //2.添加到集合,让集合去控制
        compositeDisposable.add(subscribe);

        compositeDisposable.clear();

        //3.封装observer
        SearchManager.getProduct1(1)
                //compositeDisposable要和生命周期同步
                .subscribe(new ComposeObserver<Product>(compositeDisposable) {
                    @Override
                    public void onSuccess(Product product) {

                    }
                });


        //统一处理错误UI 同上

        //emitter.onNext(对象)中不可传入null，会抛出异常，不过会在最后的onError中捕获到，也可以作为一种错误逻辑处理的方式，
        //不过不推荐使用


        //几个常用的操作符
        // map  flatmap  zip  filter  interval.......


        //上面说的都是一次发送一条消息，以下是结合操作符，发送多条消息的例子


        //发送多条消息
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            for (int i = 0; i < 100; i++) {
                emitter.onNext(i);
            }
            emitter.onComplete();
        })
                .subscribe(System.out::println);

        Observable.just(3, 4, 5, 6)
                .subscribe(System.out::println);

        //interval
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(aLong -> {
                    //每隔1秒发送数据项，从0开始计数
                    //0,1,2,3....
                });

        //filter
        Observable.just(3, 4, 5, 6)
                .filter(integer -> integer > 4)
                .subscribe(item -> Log.d("JG", item.toString())); //5,6


        //merge 看图 一条信息、N条信息

        //zip 看图 一条信息、N条信息

        //背压 Flowable 上游数据产生过快，下游来不及处理
    }

    private void updateUIByProduct(Product product) {
    }

    private void updateUIByUIProduct(UIProduct product) {
    }


    public void netWork(int params, Callback callback) {
        try {
            Thread.sleep(1000);
            callback.onSuccess(params);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public Observable<Integer> netWork(int params) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i("MainActivity","netWork-->"+Thread.currentThread().getName());
                emitter.onNext(params);
                emitter.onComplete();
            }
        });
    }

}

interface Callback {
    void onSuccess(int result);
}

class MyThrowable extends Throwable {
}
## rx的优势：
- 线程切换，不需要像handler那样 请求与结果在不同地方
- 链式编程， 复杂的逻辑形成一条链，消灭回调地狱，并且可以在期间做各种线程切换

> 后面所有的线程切换都是用封装好的方法


## 接下来是rxjava在项目中的常用写法

> 通常有一个类 专门写各种借口，XXXManager，返回值为Observable<T>,可以包装同步、异步请求，然后看一下manager的实现

- 简单的使用，子线程网络，主线程UI

- map实现从网络bean到uibean的转换

- flatmap实现串行网络请求

- flatmap结合map

- zip操作符实现并行操作

- 对error的处理

  - 如何发出一个error

  - 如何处理error

  - 两个参数去处理error

  - 一个类处理error

  - 封装类去同意处理error

- 内存泄漏

  - 直接取消订阅

  - 添加到集合（CompositeDisposable）然后统一控制
  - 封装observer

  - emitter.onNext(对象)中不可传入null，会抛出异常，不过会在最后的onError中捕获到，也可以作为一种错误逻辑处理的方式，不过不推荐使用

  - 几个常用的操作符 map flatmap zip filter interval.......


> 上面说的都是一次发送一条消息，以下是结合操作符，发送多条消息的例子

- 几个操作符的例子，都是基于发送多条消息

- merge 看图 一条信息、N条信息
![image.png](http://upload-images.jianshu.io/upload_images/4242627-68e4ce6bdfd12acf.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- zip 看图 一条信息、N条信息
![image.png](http://upload-images.jianshu.io/upload_images/4242627-f4ecf9e034cdfd7a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 背压 Flowable 上游数据产生过快，下游来不及处理


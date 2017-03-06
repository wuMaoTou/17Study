#RxJava2.0使用

###1.RxJava介绍和原理简析
RxJava是一个实现反应性扩展框架的Java虚拟机：用于通过使用观察序列构成异步和基于事件的程序库。

扩展了观察者模式，以支持数据/事件序列，并增加了操作符，他可以将将序列清晰的组合在一起的。这些序列组合可以是抽象出来的某些数据/事件，如低级别的线程，同步，线程安全和并发数据结构。

简单来说RxJava就是一个实现异步操作的库

RxJava 有四个基本概念：`Observable (可观察者，即被观察者)`、`Observer (观察者)`、 `subscribe (订阅)`、事件。`Observable `和 `Observer `通过 `subscribe()` 方法实现订阅关系，从而 Observable 可以在需要的时候发出事件来通知 `Observer`,RxJava2.0开始最核心的是`publisher`和`Subscriber`.`Publisher`可以发出一系列的事件,而Subscriber负责和处理这些事件
与传统观察者模式不同， RxJava 的事件回调方法除了普通事件 onNext() （相当于 onClick() / onEvent()）之外，还定义了两个特殊的事件：onCompleted() 和 onError()。
>onCompleted(): 事件队列完结。RxJava 不仅把每个事件单独处理，还会把它们看做一个队列。RxJava 规定，当不会再有新的 onNext() 发出时，需要触发 onCompleted() 方法作为标志。
onError(): 事件队列异常。在事件处理过程中出异常时，onError() 会被触发，同时队列自动终止，不允许再有事件发出。

在一个正确运行的事件序列中, onCompleted() 和 onError() 有且只有一个，并且是事件序列中的最后一个。需要注意的是，onCompleted() 和 onError() 二者也是互斥的，即在队列中调用了其中一个，就不应该再调用另一个。
RxJava2.0中平常用的最多的`Publisher`是`Flowable`.`Flowable`是2.0新增的类,它添加了一个新的回调方法`OnSubscribe()`,这是为了解决RxJava1.0的backpressure(背压)问题([RxJava2.0中backpressure背压概念的理解](http://blog.csdn.net/jdsjlzx/article/details/52717636)),Observer不支持 backpressure,

要使用RxJava2.0需要先引入相应的jar包
```java
compile 'io.reactivex.rxjava2:rxjava:2.0.0'
compile 'org.reactivestreams:reactive-streams:1.0.0'
```
###2.RxJava的基本实现
####基本使用
创建Subscriber(2.0)/Observer(2.0)
```java
Subscriber<string> subscriber = new Subscriber<string>() {
            @Override
            public void onSubscribe(Subscription s) {
                Logger.i("hello  onSubscribe");
                s.request(Integer.MAX_VALUE);
            }
 
            @Override
            public void onNext(String s) {
                Logger.i("hello  onNext-->" + s);
            }
 
            @Override
            public void onError(Throwable t) {
                Logger.i("hello  onError");
            }
 
            @Override
            public void onComplete() {
                Logger.i("hello  onComplete");
            }
            };
...        
 Observer<string> observer = new Observer<string>() {
 
            @Override
            public void onSubscribe(Disposable d) {
                Logger.i("hello  onSubscribe");
            }
 
            @Override
            public void onNext(String value) {
                Logger.i("hello  onNext-->" + value);
            }
 
            @Override
            public void onError(Throwable e) {
                Logger.i("hello  onError");
            }
 
            @Override
            public void onComplete() {
                Logger.i("hello  onComplete");
            }
        };
```
Subscriber 和 Observer的接口是分别独立的，Obsesrver用于订阅Observable，而Subscriber用于订阅Flowable
>需要注意的是，在onSubscribe中，我们需要调用request去请求资源，参数就是要请求的数量，一般如果不限制请求数量，可以写成Long.MAX_VALUE。如果你不调用request，Subscriber的onNext和onComplete方法将不会被调用。

创建Flowable（2.0）/Observable（2.0）
```java
Flowable<string> stringFlowable = Flowable.create(new FlowableOnSubscribe<string>() {
            @Override
            public void subscribe(FlowableEmitter<string> e) throws Exception {
                Logger.i("---rxHelloFlowable---");
                e.onNext("Inke");
                e.onComplete();
            }
        }, FlowableEmitter.BackpressureMode.BUFFER);
        
        ...

Observable<string> stringObservable = Observable.create(new ObservableOnSubscribe<string>() {
            @Override
            public void subscribe(ObservableEmitter<string> e) throws Exception {
                e.onNext("Hello");
                e.onNext("Inke");
                e.onComplete();
            }
        });
```
可以看到，这里传入了一个 ObservableOnSubscribe对象作为参数，它的作用相当于一个计划表，当 Observable被订阅的时候，ObservableOnSubscribe的subscribe()方法会自动被调用，事件序列就会依照设定依次触发（对于上面的代码，就是观察者Subscriber 将会被调用两次 onNext()和一次 onCompleted()）。这样，由被观察者调用了观察者的回调方法，就实现了由被观察者向观察者的事件传递，即观察者模式。

####更简洁的代码
创建Flowable（2.0）/Observable（2.0）
```java
Observable<String> myObservable = Observable.just("Hello,RxJava2.0!");
...
Flowable<String> flowable = Flowable.just("hello RxJava 2.0")；
```
创建Subscriber(2.0)/Observer(2.0)
对于 Subscriber/Observer 来说，我们目前仅仅关心onNext方法。所以可以简写成下面这样
```java
Subscriber:
Consumer consumer = new Consumer<String>() {
    @Override
    public void accept(String s) throws Exception {
        System.out.println(s);
    }
};
...
Observer:1.0的如下写法,2.0同上
Action1<String> onNextAction = new Action1<String>() {  
    @Override  
    public void call(String s) {  
        System.out.println(s);  
    }  
};
```
subscribe方法有一个重载版本，接受三个Action1类型的参数，分别对应OnNext，OnComplete， OnError函数。
`myObservable.subscribe(onNextAction, onErrorAction, onCompleteAction); `2.0后写法为`subscribe(Consumer<Objecr> onNext,Consumer<Throwable> onError ,Action onComplete)`

####终极简写
```java
Flowable.just("hello RxJava 2")
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s);
            }
        });
...
2.0后Action1替换为Consumer
Observable.just("Hello, world!")  
    .subscribe(new Action1<String>() {  
        @Override  
        public void call(String s) {  
              System.out.println(s);  
        }  
    });
```

上面可以看到Flowable在create()的时候多了一个参数 BackpressureMode，是用来处理backpressure的发射器
一共有以下几种模式
```java
public enum BackpressureStrategy {
    /**
     * OnNext events are written without any buffering or dropping.
     * Downstream has to deal with any overflow.
     * <p>Useful when one applies one of the custom-parameter onBackpressureXXX operators.
     */
    MISSING,
    /**
     * Signals a MissingBackpressureException in case the downstream can't keep up.
     */
    ERROR,
    /**
     * Buffers <em>all</em> onNext values until the downstream consumes it.
     */
    BUFFER,
    /**
     * Drops the most recent onNext value if the downstream can't keep up.
     */
    DROP,
    /**
     * Keeps only the latest onNext value, overwriting any previous value if the
     * downstream can't keep up.
     */
    LATEST
}
```
BUFFER较为安全，api解释为缓冲器存有onNext值，直到下游消费它。

####线程控制Scheduler
RxJava 中，Scheduler ——调度器，相当于线程控制器，RxJava 通过它来指定每一段代码应该运行在什么样的线程。RxJava 已经内置了几个 Scheduler ，它们已经适合大多数的使用场景：

* Schedulers.immediate(): 直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler
* Schedulers.newThread(): 总是启用新线程，并在新线程执行操作
* Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler
>行为模式和 new Thread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io() 比new Thread()更有效率。不要把计算工作放在 io() 中，可以避免创建不必要的线程。
* Schedulers.computation(): 计算所使用的 Scheduler
>这个计算指的是 CPU 密集型计算，即不会被 I/O 等操作限制性能的操作，例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。不要把 I/O 操作放在computation()中，否则 I/O 操作的等待时间会浪费 CPU
* AndroidSchedulers.mainThread():Android 主线程运行

有了这几个 Scheduler ，就可以使用 subscribeOn()和 observeOn()两个方法来对线程进行控制了:
* subscribeOn(): 指定 subscribe()所发生的线程，即 Observable.OnSubscribe被激活时所处的线程。或者叫做事件产生的线程。
* observeOn(): 指定 Subscriber 所运行在的线程。或者叫做事件消费的线程。
例:
```java
Flowable<bitmap> bitmapFlowable = Flowable.just(R.drawable.effect_icon001)
                .subscribeOn(Schedulers.io())
                .map(new Function<integer, bitmap="">() {
                    @Override
                    public Bitmap apply(Integer integer) throws Exception {
                        Logger.i("这是在io线程做的bitmap绘制圆形");
                        return BitmapUtils.createCircleImage(BitmapFactory.decodeResource(MainActivity.this.getResources(), integer));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        Logger.i("这是在main线程做的UI操作");
                        imageView.setImageBitmap(bitmap);
                    }
                });
        bitmapFlowable.subscribe();
```
####订阅（Subscriptions）
当调用Observable.subscribe()，会返回一个Subscription对象。这个对象代表了被观察者和订阅者之间的联系。
```java
Subscription subscription = Observable.just("Hello, World!")
    .subscribe(s -> System.out.println(s));
```
你可以在后面使用这个Subscription对象来操作被观察者和订阅者之间的联系.
```java
subscription.unsubscribe();
```
调用unsubscribing的时候，会停止整个调用链。如果你使用了一串很复杂的操作符，调用unsubscribe将会在他当前执行的地方终止。不需要做任何额外的工作！

###3.RxJava创建事件流
简单的工厂方法

* Observable.just:创建一个发射预定义好的数据的 Observable ，发射完这些数据后，事件流就结束了。
* Observable.empty:创建的 Observable 只发射一个 onCompleted 事件就结束了
* Observable.never:这个 Observable 将不会发射任何事件和数据
* Observable.error:这个 Observable 将会发射一个 error 事件，然后结束
* Observable.defer:当 Subscriber 订阅到一个 Observable 上时，该 Observable 被创建,每当一个新的 Subscriber 订阅的时候，这个函数就重新执行一次
* Observable.create:创建任何你需要的 Observable

拓展函数

* Observable.range:发射一个区间整数序列
* Observable.interval:创建一个无限的计时序列，每隔一段时间发射一个数字，从 0 开始(不调用 unsubscribe 的话，这个序列是不会停止的)
* Observable.timer:等待一段时间，然后发射数据 0 ，然后就结束了
* Observable.from:使用 from可以把相应的结果发射到 Observable 

###4.操作符
操作符是为了解决 Flowable 对象变换问题而设计的，操作符可以在传递的途中对数据进行修改。 
RxJava提供了很多实用的操作符。

过滤数据:

* filter:使用predicate 函数接口来判断每个发射的值是否能通过这个判断。如果返回 true，则该数据继续往下一个（过滤后的） Observable 发射
* distinct:用来过滤掉已经出现过的数据(重载函数,用Function指定对比条件)
* distinctUntilChanged:distinct的变体,过滤相邻的key一样的数据
* ignoreElements:忽略所有发射的数据，只让 onCompleted 和 onError 可以通过
* take:从头开始获取前 N 个发射数据,切断数据流
* skip:从头开始 跳过 N 个发射数据,切断数据流
* takeWhile:判断条件为 true 的时候,保留该数据
* skipWhile:判断条件为 true 的时候过滤该数据
* takeLast:从头开始获取前 N 个发射数据,切断数据流
* skipLast:从尾开始 跳过 N 个发射数据,切断数据流
* takeUntil:判断条件为 false 的时候， takeUntil 保留该数据
* skipUntil:判断条件为 false 的时候， takeUntil 保留该数据

检查数据:

* all:判断 observable 中发射的所有数据是否都满足一个条件
* exists(2.0改为any):如果源 exists 发射的数据中有一个满足条件，则 exists 就返回 true
* isEmpty:判断一个 Observable 是否是空的
* contains: 使用 Object.equals 函数来判断源 Observable 是否发射了相同的数据,遇到相同的数据 就立刻返回。
* defaultIfEmpty:设置当发射数据为空时返回的默认数据
* elementAt:从特定的位置选择一个数据发射
* sequenceEqual:比较两个 Observable 发射的数据是否是一样

聚合:

* count:统计源 Observable 完成的时候一共发射了多少个数据
* first:返回第一个满足该条件的数据
* last:返回最后一个满足该条件的数据
* single:用来检查数据流中是否有且仅有一个符合条件的数据
* reduce:使用源 Observable 中的所有数据两两组合来生成一个单一的 数据
* scan:使用源 Observable 中的所有数据两两组合来生成一个单一的数据并每次都把结果返回
* collect:自定义收集数据
* toList: 将发射来的多个数据存储到集合
* toSortedList:将发射来的多个数据存储到集合并进行排序
* toMap:数据流变为一个 Map
* toMultimap:通常情况下多个 value 的 key 可能是一样的。 一个 key 可以映射多个 value 的数据结构为 multimap，multimap 的 value 为一个集合
* groupBy: toMultimap 函数的 Rx 方式的实现

转换数据:

* map:变换 Flowable 然后返回一个指定类型的 Flowable 对象
* cast：用来把一个对象强制转换为子类型
* ofType：用来判断数据是否为 该类型，如果不是则跳过这个数据
* timestamp：timestamp 






资料来源:[RxJava入门专栏](http://blog.csdn.net/column/details/rxjava.html)























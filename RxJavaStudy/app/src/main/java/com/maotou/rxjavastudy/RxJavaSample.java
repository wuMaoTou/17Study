package com.maotou.rxjavastudy;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.maotou.rxjavastudy.databinding.ActivitySampleBinding;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class RxJavaSample extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "RxJava";

    private TextView tv;
    private ActivitySampleBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sample);

        mBinding.btHelloRxjava.setOnClickListener(this);
        mBinding.btSimplyRxjava.setOnClickListener(this);
        mBinding.btMap.setOnClickListener(this);

    }

    private void helloRxJava() {
        Flowable<String> flowable = Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                Log.d(TAG, "Flowable.subscribe");
                e.onNext("Flowable Hello RxJava2");
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER);

        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {
                Log.d(TAG, "Subscriber.onSubscribe");
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "Subscriber.onNext");
                Log.d(TAG, "Subscriber.onNext----" + s);
            }

            @Override
            public void onError(Throwable t) {
                Log.d(TAG, "Subscriber.onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Subscriber.onComplete");
            }
        };

        flowable.subscribe(subscriber);


        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                Log.d(TAG, "Observable.subscribe");
                e.onNext("Observable Hello RxJava2");
                e.onComplete();
            }
        });

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "Observer.onSubscribe");
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "Observer.onNext");
                Log.d(TAG, "Observer.onNext----" + s);
            }

            @Override
            public void onError(Throwable t) {
                Log.d(TAG, "Observer.onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Observer.onComplete");
            }
        };

        observable.subscribe(observer);
    }

    void simplyRxJava() {

        Flowable<String> flowable = Flowable.just("Flowable Simply Rxjava");

        Consumer<String> consumer = new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "Consumer.accept" + s);
            }
        };
        flowable.subscribe(consumer);

        Observable<String> observable = Observable.just("Observable Simply Rxjava");

        Consumer<String> consumer1 = new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "Consumer1.next" + s);
            }
        };

        Action action = new Action() {
            @Override
            public void run() throws Exception {
                Log.d(TAG, "Action.Comolete");
            }
        };

        Consumer<Throwable> consumer2 = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

                Log.d(TAG, "Consumer.error");
            }
        };

        observable.subscribe(consumer1, consumer2, action);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_hello_rxjava:
                helloRxJava();
                break;
            case R.id.bt_simply_rxjava:
                simplyRxJava();
                break;
            case R.id.bt_map:
                mapCombin();
                break;
        }
    }

    private void mapCombin() {

        Flowable.just("map").
                map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(String s) throws Exception {
                        return s.hashCode();
                    }
                }).
                map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return integer + "woo";
                    }
                }).
                subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG,"map--"+s);
                    }
                });

    }
}

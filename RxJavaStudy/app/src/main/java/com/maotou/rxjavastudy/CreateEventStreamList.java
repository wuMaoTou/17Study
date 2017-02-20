package com.maotou.rxjavastudy;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.maotou.rxjavastudy.databinding.ActivityMainBinding;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.operators.flowable.FlowableJust;

/**
 * Created by wuchundu on 17-2-16.
 */
public class CreateEventStreamList extends AppCompatActivity {

    private String TAG = ">>>>>RxJava 2.0<<<<<";
    private ActivityMainBinding mainBinding;
    public String[] menu = new String[]{"just", "empty", "error", "defer", "range", "interval", "timer","form"};
    public static final int JUST = 0;
    public static final int EMPTY = 1;
    public static final int ERROR = 2;
    public static final int DEFER = 3;
    public static final int RANGE = 4;
    public static final int INTERVAL = 5;
    public static final int TIMER = 6;
    public static final int FORM = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.listview.setAdapter(new CreateEventStreamList.MyAdapter());
        mainBinding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case JUST:
                        just();
                        break;
                    case EMPTY:
                        empty();
                        break;
                    case ERROR:
                        error();
                        break;
                    case DEFER:
                        defer();
                        break;
                    case RANGE:
                        range();
                        break;
                    case INTERVAL:
                        interval();
                        break;
                    case TIMER:
                        timer();
                        break;
                    case FORM:
                        form();
                        break;
                }
            }
        });
    }

    /**
     * 使用 from可以把相应的结果发射到 Observable
     */
    private void form() {
        FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "FutureTask";
            }
        });
        new Thread(futureTask).start();
        Flowable.fromFuture(futureTask,5,TimeUnit.SECONDS)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "onNext:" + s);
                    }
                });

        String[] stringArray = new String[]{"one","two","three","four","five"};
        Flowable.fromArray(stringArray)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "onNext:" + s);
                    }
                });

        List<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        Flowable.fromIterable(list)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "onNext:" + s);
                    }
                });
    }

    /**
     * 等延迟一段时间，然后发射数据 0 ，然后就结束了
     */
    private void timer() {
        Flowable.timer(2000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.d(TAG, "onNext:" + aLong + "");
                    }
                });

    }

    /**
     * 创建一个无限的计时序列，每隔一段时间发射一个数字，从 0 开始(不调用 unsubscribe 的话，这个序列是不会停止的)
     */
    Disposable subscribe;

    private void interval() {
        if (subscribe != null) {
            subscribe.dispose();
        }
        subscribe = Flowable.interval(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.d(TAG, "onNext:" + aLong + "");
                    }
                });
    }

    /**
     * 发射一个区间整数序列
     */
    private void range() {
        Flowable.range(15, 15)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "onNext:" + integer + "");
                    }
                });
    }

    /**
     * 当 Subscriber 订阅到一个 Observable 上时，该 Observable 被创建,每当一个新的 Subscriber 订阅的时候，这个函数就重新执行一次
     */
    private void defer() {
        Flowable<Long> flowable = Flowable.defer(new FlowableJust<Publisher<Long>>(new Flowable<Long>() {
            @Override
            protected void subscribeActual(Subscriber<? super Long> s) {
                s.onNext(System.currentTimeMillis());
            }
        }));
        flowable.subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(TAG, aLong + "");
            }
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        flowable.subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(TAG, aLong + "");
            }
        });
    }

    /**
     * 这个Observable将会发射一个error事件，然后结束了
     */
    private void error() {
        Flowable.error(new Throwable("error"))
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Object s) {
                        Log.d(TAG, "onNext:" + s.toString());
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "onError:" + t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * 创建的 Observable 只发射一个 onCompleted 事件就结束了
     */
    private void empty() {
        Flowable.empty()
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Object s) {
                        Log.d(TAG, "onNext:" + s.toString());
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "onError:" + t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * 创建一个发射预定义好的数据的 Observable ，发射完这些数据后，事件流就结束了
     */
    private void just() {
        Flowable.just("one", "two", "three")
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, "onNext:" + s);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "onError:" + t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return menu.length;
        }

        @Override
        public Object getItem(int position) {
            return menu[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView textView = new TextView(CreateEventStreamList.this);
            textView.setText(menu[position]);
            int padding = 30;
            textView.setPadding(padding, padding, padding, padding);

            return textView;
        }
    }
}

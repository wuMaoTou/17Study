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

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.MaybeObserver;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.flowables.GroupedFlowable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.operators.flowable.FlowableJust;

/**
 * Created by wuchundu on 17-2-16.
 */
public class OperatorActivity extends AppCompatActivity {

    private String TAG = ">>>>>RxJava 2.0<<<<<";
    private ActivityMainBinding mainBinding;
    public String[] menu = new String[]{"map", "filter", "distinct", "distinctUntilChanged", "ignoreElements", "take", "skip", "all"
            , "exists", "contains", "defaultIfEmpty", "elementAt", "sequenceEqual", "reduce", "scan", "collect", "toList", "toSortedList"
            , "toMap", "toMultimap","groupBy"};
    public static final int MAP = 0;
    public static final int FILTER = 1;
    public static final int DISTINCT = 2;
    public static final int DISTINCTUNTILCHANGED = 3;
    public static final int IGNOREELEMENTS = 4;
    public static final int TAKE = 5;
    public static final int SKIP = 6;
    public static final int ALL = 7;
    public static final int EXISTS = 8;
    public static final int CONTAINS = 9;
    public static final int DEFAULTIFEMPTY = 10;
    public static final int ELEMENTAT = 11;
    public static final int SEQUENCEEQUAL = 12;
    public static final int REDUCE = 13;
    public static final int SCAN = 14;
    public static final int COLLECT = 15;
    public static final int TOLIST = 16;
    public static final int TOSORTEDLIST = 17;
    public static final int TOMAP = 18;
    public static final int TOMULTIMAP = 19;
    public static final int GROUPBY = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.listview.setAdapter(new OperatorActivity.MyAdapter());
        mainBinding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case MAP:
                        map();
                        break;
                    case FILTER:
                        filter();
                        break;
                    case DISTINCT:
                        distinct();
                        break;
                    case DISTINCTUNTILCHANGED:
                        distinctUntilChanged();
                        break;
                    case IGNOREELEMENTS:
                        ignoreElements();
                        break;
                    case TAKE:
                        take();
                        break;
                    case SKIP:
                        skip();
                        break;
                    case ALL:
                        all();
                        break;
                    case EXISTS:
                        exists();
                        break;
                    case CONTAINS:
                        contains();
                        break;
                    case DEFAULTIFEMPTY:
                        defaultIfEmpty();
                        break;
                    case ELEMENTAT:
                        elementAt();
                        break;
                    case SEQUENCEEQUAL:
                        sequenceEqual();
                        break;
                    case REDUCE:
                        reduce();
                        break;
                    case SCAN:
                        scan();
                        break;
                    case COLLECT:
                        collect();
                        break;
                    case TOLIST:
                        toList();
                        break;
                    case TOSORTEDLIST:
                        toSortedList();
                        break;
                    case TOMAP:
                        toMap();
                        break;
                    case TOMULTIMAP:
                        toMultimap();
                        break;
                    case GROUPBY:
                        groupBy();
                        break;
                }
            }
        });
    }

    private void groupBy() {
        Flowable.range(8,10)
                .groupBy(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return integer % 2== 0 ? "test" : integer+"t";
                    }
                }, new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return "testd";
                    }
                },false)
                .subscribe(new Subscriber<GroupedFlowable<String, String>>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(GroupedFlowable<String, String> stringStringGroupedFlowable) {
                        stringStringGroupedFlowable.toList()
                                .subscribe(new SingleObserver<List<String>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(List<String> value) {
                                        Log.d(TAG,"onNext:"+value.toString());
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                        Log.d(TAG,"onError");
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG,"onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete");
                    }
                });
    }

    private void toMultimap() {
        Flowable.range(8, 10)
                .toMultimap(new Function<Integer, String>() {
                                @Override
                                public String apply(Integer integer) throws Exception {
                                    return integer % 2== 0 ? "test" : integer+"t";
                                }
                            }, new Function<Integer, String>() {
                                @Override
                                public String apply(Integer integer) throws Exception {
                                    return "testd";
                                }
                            }, new FlowableJust<Map<String, Collection<String>>>(new HashMap<String, Collection<String>>()),
                        new Function<String, Collection<? super String>>() {
                            @Override
                            public Collection<? super String> apply(String s) throws Exception {
                                return new ArrayList<String>();
                            }
                        })
                .subscribe(new SingleObserver<Map<String, Collection<String>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Map<String, Collection<String>> value) {
                        Log.d(TAG, "onNext:" + value.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError:" + e);
                    }
                });
    }

    private void toMap() {
        Flowable.range(8, 10)
                .toMap(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return integer + "test";
                    }
                }, new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return integer + "..";
                    }
                }, new FlowableJust<Map<String, String>>(new HashMap<String, String>()))
                .subscribe(new SingleObserver<Map<String, String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Map<String, String> value) {
                        Log.d(TAG, "onNext:" + value.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError:" + e);
                    }
                });
    }

    private void toSortedList() {
        Flowable.just(3, 7, 2, 9, 11, 0)
                .toSortedList()
                .subscribe(new SingleObserver<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Integer> value) {
                        Log.d(TAG, "onNext:" + value.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError:" + e);
                    }
                });
    }

    private void toList() {
        Flowable.range(17, 9)
                .toList()
                .subscribe(new SingleObserver<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Integer> value) {
                        Log.d(TAG, "onNext:" + value.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError:" + e);
                    }
                });
    }

    private void collect() {
        Flowable.range(10, 9)
                .collect(new Callable<List<Integer>>() {
                    @Override
                    public List<Integer> call() throws Exception {
                        return new ArrayList<Integer>();
                    }
                }, new BiConsumer<List<Integer>, Integer>() {
                    @Override
                    public void accept(List<Integer> integers, Integer integer) throws Exception {
                        integers.add(integer);
                    }
                })
                .subscribe(new SingleObserver<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Integer> value) {
                        Log.d(TAG, "onNext:" + value.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError:" + e);
                    }
                });
    }

    private void scan() {
        Flowable.range(0, 10)
                .scan(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        Log.d(TAG, "apply:" + integer + "---" + integer2);
                        return integer + integer2;
                    }
                })
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext:" + integer);
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
     * 使用源 Observable 中的所有数据两两组合来生成一个单一的数据
     */
    private void reduce() {
        Flowable.range(0, 10)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        Log.d(TAG, "apply:" + integer + "---" + integer2);
                        return integer + integer2;
                    }
                })
                .subscribe(new MaybeObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer value) {
                        Log.d(TAG, "onNext:" + value);
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

        Flowable.range(10, 8)
                .reduce(new ArrayList<Integer>(), new BiFunction<ArrayList<Integer>, Integer, ArrayList<Integer>>() {
                    @Override
                    public ArrayList<Integer> apply(ArrayList<Integer> integers, Integer integer) throws Exception {
                        integers.add(integer);
                        return integers;
                    }
                })
                .subscribe(new SingleObserver<ArrayList<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ArrayList<Integer> value) {
                        Log.d(TAG, "onNext:" + value.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError:" + e);
                    }
                });
    }

    /**
     * 比较两个 Observable 发射的数据是否是一样
     */
    private void sequenceEqual() {

        Flowable<Integer> range = Flowable.range(0, 5);
        Flowable<Integer> just = Flowable.just(0, 1, 2, 3, 4);

        Flowable.sequenceEqual(range, just)
                .subscribe(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Boolean value) {
                        Log.d(TAG, "onSuccess:" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError" + e);
                    }
                });
    }

    /**
     * 从特定的位置选择一个数据发射
     */
    private void elementAt() {
        Flowable.range(10, 15)
                .elementAt(10)
                .subscribe(new MaybeObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer value) {
                        Log.d(TAG, "onNext:" + value);
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
     * 设置当发射数据为空时返回的默认数据
     */
    private void defaultIfEmpty() {
        Flowable.empty()
                .defaultIfEmpty(10)
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d(TAG, "onNext:" + (int) o);
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
     * 使用 Object.equals 函数来判断源 Observable 是否发射了相同的数据,遇到相同的数据 就立刻返回
     */
    private void contains() {
        Flowable.interval(200, TimeUnit.MILLISECONDS)
                .contains(4L)
                .subscribe(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Boolean value) {
                        Log.d(TAG, "onSuccess:" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError" + e);
                    }
                });
    }

    /**
     * 如果源 exists 发射的数据中有一个满足条件，则 exists 就返回 true
     */
    private void exists() {
        Flowable.range(0, 10)
                .any(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer > 7;
                    }
                })
                .subscribe(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Boolean value) {
                        Log.d(TAG, "onSuccess:" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError" + e);
                    }
                });
    }

    /**
     * 判断 observable 中发射的所有数据是否都满足一个条件
     */
    private void all() {
        Flowable.range(0, 10)
                .all(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {

                        Log.d(TAG, "test:" + integer);
                        return integer < 5;
                    }
                })
                .subscribe(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Boolean value) {
                        Log.d(TAG, "onSuccess:" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError" + e);
                    }
                });
    }

    /**
     * 从头开始 跳过 N 个发射数据,切断数据流
     */
    private void skip() {
        Flowable.range(0, 10)
                .skip(5)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext:" + integer);
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
     * 从头开始获取前 N 个发射数据,切断数据流
     */
    private void take() {
        Flowable.range(0, 10)
                .take(5)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext:" + integer);
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

        Flowable.interval(100, TimeUnit.MILLISECONDS)
                .take(250, TimeUnit.MILLISECONDS)
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Long integer) {
                        Log.d(TAG, "onNext:" + integer);
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

    private void ignoreElements() {
        Flowable.range(3, 16)
                .ignoreElements()
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError:" + e);
                    }
                });
    }

    /**
     * distinct的变体,过滤相邻的key一样的数据
     */
    private void distinctUntilChanged() {
        Flowable.just("A", "A", "B", "C", "B", "C")
                .distinctUntilChanged()
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(String integer) {
                        Log.d(TAG, "onNext:" + integer);
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
     * 用来过滤掉已经出现过的数据(重载函数,用Function指定对比条件)
     */
    private void distinct() {
        Flowable.just("A", "A", "B", "C", "B", "C")
                .distinct()
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(String integer) {
                        Log.d(TAG, "onNext:" + integer);
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

        Flowable.just("Frist", "Second", "Third", "Fourth", "Fifth")
                .distinct(new Function<String, Character>() {
                    @Override
                    public Character apply(String s) throws Exception {
                        return s.charAt(0);
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(String integer) {
                        Log.d(TAG, "onNext:" + integer);
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
     * 使用 predicate 函数接口来判断每个发射的值是否能通过这个判断。如果返回 true，则该数据继续往下一个（过滤后的） Observable 发射
     */
    private void filter() {
        Flowable.range(1, 20)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer % 2 == 0;
                    }
                })
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext:" + integer);
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

    private void map() {

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
                        Log.d(TAG, "map--" + s);
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

            TextView textView = new TextView(OperatorActivity.this);
            textView.setText(menu[position]);
            int padding = 30;
            textView.setPadding(padding, padding, padding, padding);

            return textView;
        }
    }
}

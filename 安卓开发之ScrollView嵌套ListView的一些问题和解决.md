##安卓开发之ScrollView嵌套ListView的一些问题和解决

﻿一、ListView的高度不能完全展开

这种情况是当ScrollView嵌套ListView时，ListView的高度设置为wrap_content时会产生，一般情况下ListView只显示的第一个Item。

正常情况下，高度设置为“wrap_content”的ListView在测量自己的高度会使用MeasureSpec.AT_MOST(最大模式)这个模式高度来返回可包含住其内容的高度。

而实际上当ListView被ScrollView嵌套时，ListView使用的测量模式是ScrollView传入的MeasureSpec.UNSPECIFIED(未指定模式)。

```java

// ScrollView的measureChildWithMargins()代码：
@Override
protected void measureChild(View child, int parentWidthMeasureSpec,
        int parentHeightMeasureSpec) {
    ViewGroup.LayoutParams lp = child.getLayoutParams();
    int childWidthMeasureSpec;
    int childHeightMeasureSpec;
    childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, mPaddingLeft
            + mPaddingRight, lp.width);
    childHeightMeasureSpec = MeasureSpec.makeSafeMeasureSpec(
            MeasureSpec.getSize(parentHeightMeasureSpec), MeasureSpec.UNSPECIFIED);
    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
}

```

而在ListView的onMeasure方法中：使用MeasureSpec.UNSPECIFIED模式测量时只能加载一部分高度

```java
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // Sets up mListPadding
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
     ...
     ...
    if (heightMode == MeasureSpec.UNSPECIFIED) {
        heightSize = mListPadding.top + mListPadding.bottom + childHeight +
                getVerticalFadingEdgeLength() * 2;
    }
    setMeasuredDimension(widthSize, heightSize);
    mWidthMeasureSpec = widthMeasureSpec;
}
```

解决方法一：覆写ListView的onMeasure方法：

```java
public class MyListView extends ListView {
    public MyListView(Context context) {
        super(context);
    }
    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2, // 设计一个较大的值和AT_MOST模式
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);//再调用原方法测量
    }
}

```

解决方法二：在Activity中动态修改ListView的高度，注意当ListView的子item需要根布局是LinearLayout，或需要为一个View，因为有些布局中没有measure这个方法，比如RelativeLayout。

```java
public void setListViewHeightBasedOnChildren(ListView listView) {
    ListAdapter listAdapter = listView.getAdapter();
    if (listAdapter == null) {
        return;
    }
    int totalHeight = 0;
    for (int i = 0; i < listAdapter.getCount(); i++) {
        View listItem = listAdapter.getView(i, null, listView);
        listItem.measure(0, 0);  // 获取item高度
        totalHeight += listItem.getMeasuredHeight();
    }
    ViewGroup.LayoutParams params = listView.getLayoutParams();
    // 最后再加上分割线的高度和padding高度，否则显示不完整。
    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))+listView.getPaddingTop()+listView.getPaddingBottom();
    listView.setLayoutParams(params);
}
```

缺陷：上述两种解决方法都有一个缺陷就是，当第一次进入界面动态加载listview的i数据后，ScrollView会跳滑到listview的第一个子项。
处理缺陷可以有两种方式：

方式一： 
先设置ListView失去焦点，这并不影响item的点击事件发生
```java
setFocusable(false);
```
方式二： 
重新调整ScrollView的位置
```java
mScrollView.post(new Runnable() {
        @Override
        public void run() {
            mScrollView.scrollTo(0,0); 
        }
  });
```

二、ListView高度固定后无法触发滑动事件

当对listview的高度设置为固定值（例200dp）时，listview的高度是可以直接显示出来的。但嵌套在一起后ScrollView中的ListView就没法上下滑动了，事件先被ScrollView响应了。

解决方法：当ListView自身接收到的滑动事件时，使ScrollView取消拦截。ListView区域内的滑动事件由自己处理，ListView区域外滑动事件由外层ScrollView处理。可以系统自带的API来实现：requestDisallowInterceptTouchEvent这一方法。

解决方法一：在这里我们自定义ListView来重写ListView的dispatchTouchEvent函数：
```java
public class ScollListView extends ListView {
    public ScollListView(Context context) {
        super(context);
    }
    public ScollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ScollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScollListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
```

解决方式二：或者也可以给ListView绑定触摸事件的监听：
```java
scrollListView.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    scrollListView.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    scrollListView.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return false;
        }
    });
```

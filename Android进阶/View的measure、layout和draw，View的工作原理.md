##View的measure、layout和draw，View的工作原理

### View的工作原理
View的绘制流程从ViewRoot的performTraversals方法开始的,经过measure,layout和draw三个过程最终将一个View绘制出来,其中measure方法测量View的宽和高,layout确定View在父容器中的位置,draw则负责将View绘制在屏幕上
![performTraversals流程图](https://upload-images.jianshu.io/upload_images/3985563-5f3c64af676d9aee.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/679/format/webp)
在`perfromTraversals`中依次调用`performMeasure`,`performLayout`和`performDraw`三个方法,这三个方法分别完成顶级View的measure,layout和draw这三大流程,其中在performMeasure中会调用measure方法,在measure方法中又会调用onMeasure方法,在onMeasure方法中则会对所有的子元素进行measure过程,子元素重复父容器的measure过程,如此反复完成整个View树的遍历,同理,performLayout和performDraw的传递流程和performMeasure是类似的

### View的measure、layout和draw

####1 measure
> View中实际测量工作在onMeasure中

#### 1.1 MeasureSpec
MeasureSpec.UNSPECIFIED: 父布局不对View有任何限制
MeasureSpec.EXACTLUY: 父布局确认了view的精确大小
MeasureSpec.AT_MOST: 父布局指定一个大小,view不能大于这个值

LayoutParams.MATCH_PARENT = -1//填充父布局
LayoutParams.WRAP_CONTENT = -2//包裹内容

![普通View的MeasureSpec的创建规则](https://i.imgur.com/q8FKFFH.png)
总结:View的MeasureSpec是由父容器的MeasureSpec和自身的LayoutParams来共同决定的,所以View会有多种MeasureSpec情况:
1.View采用固定宽/高的时候,不管父容器的模式是什么,View的模式都是精确模式(EXACTLUY)并且其大小遵循LayoutParams中的大小
2.View的宽/高是match_parent时,父布局的模式是精确模式,那么View也是精确模式并且其大小是父容器的剩余空间
3.View的宽/高是match_parent时,父布局的模式是最大模式,那么View也是最大模式并且大小是父容器的剩余空间
4.View的宽/高是wrap_content时,不管父布局的模式是什么,View的模式总是最大模式(AT_MOST)并且大小不能超过父容器的剩余空间
5.UNSPECIFIED模式主要用于系统内部多次Measure的情形,一般不需要关注此模式

##### 1.2 View的measure
View的onMeasure的方法
```java
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }
```

onMeasure中主要的用的3个方法:

**getDefaultSize**
```java
	public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
        case MeasureSpec.UNSPECIFIED:
            result = size;
            break;
        case MeasureSpec.AT_MOST:
        case MeasureSpec.EXACTLY:
            result = specSize;
            break;
        }
        return result;
    }
```


**getSuggestedMinimumWidth**
```java
	protected int getSuggestedMinimumWidth() {
        return (mBackground == null) ? mMinWidth : max(mMinWidth, mBackground.getMinimumWidth());
    }
```

**getSuggestedMinimumHeight**
```java
	protected int getSuggestedMinimumHeight() {
        return (mBackground == null) ? mMinHeight : max(mMinHeight, mBackground.getMinimumHeight());

    }
```

在getDefaultSize中,我们只需要看AT_MOST和EXACTLY两种情况,即最终返回的结果就是measureSpec的specSize,这里的specSize是view测量后的大小,最终大小是在layout中确定的,几乎所有情况下他们都是相等的.UNSPECIFIED情况一般用于系统内部的测量过程,这种情况,getDefault返回的结果来自于`getSuggestedMinimumWidth`/`getSuggestedMinimumHeight`,其内部逻辑是判断View是否设置了背景,如果没有设置背景就返回最小宽度,可以为0,如果设置了背景则返回最小宽度(miniWidth)和背景最小宽度两者中的最大值

**让View支持wrap_content**
在自定义View时,直接继承View的自定义控件需要重写onMeasure方法并设置weap_content时的自身大小,否则自定义View的wrap_content等于match_parent.因为使用wrap_content,view的specmode是AT_MOST模式,getDefaultSize中返回的父布局的剩余空间,相当于match_parent.所以自定义view时需要重写onMeasure方法支持wrap_content
```java
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
			//设置默认的内部宽高
			setMeasureDimension(mWidth, mHeight);
		} else if (widthSpecMode == MeasureSpec.AT_MOST) {
			setMeasureDimension(mWidth, heightMeasureSpec);
		} else if (heightSpecMode == MeasureSpec.AT_MOST) {
			setMeasureDimension(widthMeasureSpec, mHeight);
		}
    }
```

##### 1.3 ViewGroup的measure
ViewGroup除了测量自己还会遍历去调用子元素的mesure方法,它是一个抽象类,没有重写onMeasure方法,提供了一个measureChildren方法遍历测量子元素

```java
	//测量所有子元素
	protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
        final int size = mChildrenCount;
        final View[] children = mChildren;
        for (int i = 0; i < size; ++i) {
            final View child = children[i];
            if ((child.mViewFlags & VISIBILITY_MASK) != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

	//测量子元素
	protected void measureChild(View child, int parentWidthMeasureSpec,
            int parentHeightMeasureSpec) {
        final LayoutParams lp = child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                mPaddingLeft + mPaddingRight, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                mPaddingTop + mPaddingBottom, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }
```

measure完成后,通过getMeasureWidth/Height方法可以获取到View的测量宽/高,但在某些情况下,系统肯会多次measure才能确定最终的测量宽/高,因此在onMeasure方法中拿到的测量宽/高可能不是最终的宽高,所以一般确定View最终宽/高都放到onLayout方法中获取

**在Activity中获取View的最终宽/高**
因为View的measure过程和Activity的的生命周期方法不是同步执行的,因此无法保证在哪个生命周期回调方法去获取view的最终宽/高,可以通过以下几种方式获取

* Activity/View#onWindowFocusChanged
	该方法回调的时机如同方法名称是在window的焦点发生改变时触发,所以该方法在生命周期内会被多次执行

* view.post(runable)
	通过post将获取宽/高的runable投递到消息队列发尾部,即调用此runable时,view已经完成了measure和layout
	[View的post方法执行的时机](https://blog.csdn.net/Small_Lee/article/details/79424093)

* ViewTreeObserver
	使用ViewTreeObserver的OnGlobalLayoutLinstaner接口,当View树的状态发生改变或者View树内部的View可见性发生改变时,onGloablLayout方法都会被回调,可以在此获取View的宽/高.onGloablLayout随着View树的状态改变也会被多次调用
	```java
		ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = listView.getMeasuredWidth();
                int height = listView.getMeasuredHeight();
            }
        });
	```

* view.measure(int widthMeasureSpec, int heightMeasureSpec)
	手动对View进行measure来得到View的宽/高

>onMeasure()方法：单一View，一般重写此方法，针对wrap_content情况，规定View默认的大小值，避免于match_parent情况一致。ViewGroup，若不重写，就会执行和单子View中相同逻辑，不会测量子View。一般会重写onMeasure()方法，循环测量子View。

	
####2.layout
> Layout的作用是ViewGroup用来确定子元素的位置,当ViewGroup的位置确定后,在onLayout中会遍历所有的子元素并调用其layout方法,在layout方法中onLayout方法又会被调用

```java
	public void layout(int l, int t, int r, int b) {
        if ((mPrivateFlags3 & PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT) != 0) {
            onMeasure(mOldWidthMeasureSpec, mOldHeightMeasureSpec);
            mPrivateFlags3 &= ~PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT;
        }

        int oldL = mLeft;
        int oldT = mTop;
        int oldB = mBottom;
        int oldR = mRight;

        boolean changed = isLayoutModeOptical(mParent) ?
                setOpticalFrame(l, t, r, b) : setFrame(l, t, r, b);

        if (changed || (mPrivateFlags & PFLAG_LAYOUT_REQUIRED) == PFLAG_LAYOUT_REQUIRED) {
            onLayout(changed, l, t, r, b);

            if (shouldDrawRoundScrollbar()) {
                if(mRoundScrollbarRenderer == null) {
                    mRoundScrollbarRenderer = new RoundScrollbarRenderer(this);
                }
            } else {
                mRoundScrollbarRenderer = null;
            }

            mPrivateFlags &= ~PFLAG_LAYOUT_REQUIRED;

            ListenerInfo li = mListenerInfo;
            if (li != null && li.mOnLayoutChangeListeners != null) {
                ArrayList<OnLayoutChangeListener> listenersCopy =
                        (ArrayList<OnLayoutChangeListener>)li.mOnLayoutChangeListeners.clone();
                int numListeners = listenersCopy.size();
                for (int i = 0; i < numListeners; ++i) {
                    listenersCopy.get(i).onLayoutChange(this, l, t, r, b, oldL, oldT, oldR, oldB);
                }
            }
        }

        mPrivateFlags &= ~PFLAG_FORCE_LAYOUT;
        mPrivateFlags3 |= PFLAG3_IS_LAID_OUT;
    }


	protected boolean setFrame(int left, int top, int right, int bottom) {
        boolean changed = false;

        if (DBG) {
            Log.d("View", this + " View.setFrame(" + left + "," + top + ","
                    + right + "," + bottom + ")");
        }

        if (mLeft != left || mRight != right || mTop != top || mBottom != bottom) {
            changed = true;

            // Remember our drawn bit
            int drawn = mPrivateFlags & PFLAG_DRAWN;

            int oldWidth = mRight - mLeft;
            int oldHeight = mBottom - mTop;
            int newWidth = right - left;
            int newHeight = bottom - top;
            boolean sizeChanged = (newWidth != oldWidth) || (newHeight != oldHeight);

            // Invalidate our old position
            invalidate(sizeChanged);

            mLeft = left;
            mTop = top;
            mRight = right;
            mBottom = bottom;
            mRenderNode.setLeftTopRightBottom(mLeft, mTop, mRight, mBottom);

            mPrivateFlags |= PFLAG_HAS_BOUNDS;


            if (sizeChanged) {
                sizeChange(newWidth, newHeight, oldWidth, oldHeight);
            }

            if ((mViewFlags & VISIBILITY_MASK) == VISIBLE || mGhostView != null) {
                // If we are visible, force the DRAWN bit to on so that
                // this invalidate will go through (at least to our parent).
                // This is because someone may have invalidated this view
                // before this call to setFrame came in, thereby clearing
                // the DRAWN bit.
                mPrivateFlags |= PFLAG_DRAWN;
                invalidate(sizeChanged);
                // parent display list may need to be recreated based on a change in the bounds
                // of any child
                invalidateParentCaches();
            }

            // Reset drawn bit to original value (invalidate turns it off)
            mPrivateFlags |= drawn;

            mBackgroundSizeChanged = true;
            if (mForegroundInfo != null) {
                mForegroundInfo.mBoundsChanged = true;
            }

            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
        return changed;
    }

	public static boolean isLayoutModeOptical(Object o) {
        return o instanceof ViewGroup && ((ViewGroup) o).isLayoutModeOptical();
    }

    private boolean setOpticalFrame(int left, int top, int right, int bottom) {
        Insets parentInsets = mParent instanceof View ?
                ((View) mParent).getOpticalInsets() : Insets.NONE;
        Insets childInsets = getOpticalInsets();
        return setFrame(
                left   + parentInsets.left - childInsets.left,
                top    + parentInsets.top  - childInsets.top,
                right  + parentInsets.left + childInsets.right,
                bottom + parentInsets.top  + childInsets.bottom);
    }
```
在layout中
```java
	boolean changed = isLayoutModeOptical(mParent) ? setOpticalFrame(l, t, r, b) : setFrame(l, t, r, b);
```
通过`setFram()/setOpticalFrame()`确定View自身的位置,`setOpticalFrame()`内部最终也是调用了`setFrame()`,在setFrame中设定View的四个顶点位置,初始化mLeft,mRight,mTop和mBottom,确定View在父布局中的位置,接着调用onLayout方法,在onLayout中确定子元素的布局,View和ViewGroup的onLayout没有具体实现,onLayout和onMeasure的具体布局控件实现不一样,基本就是通过上面的流程遍历整个ViewTree完成layout

**View的测量宽/高和最终宽/高有什么区别**
默认情况下测量宽高就是最终宽高,但是在重写layout方法后改变了l,t,r,b中的任意一个值时就会出现不相等的情况,所以获取控件的宽高放在layout才能回去到最终的尺寸

>onLayout()方法:单一View，不需要实现该方法。ViewGroup必须实现，该方法是个抽象方法，实现该方法，来对子View进行布局。


####2.draw
>draw的作用是将View绘制到屏幕上面

**View的绘制过程遵循如下几步:**
1.绘制背景background.draw(canvas)
2.绘制自己(onDraw)
3.绘制children(dispatchDraw)
4.绘制装饰(onDrawScrollBars)

> 论是ViewGroup还是单一的View，都需要实现这套流程，不同的是，在ViewGroup中，实现了 dispatchDraw()方法，而在单一子View中不需要实现该方法。自定义View一般要重写onDraw()方法，在其中绘制不同的样式。

**setWillNotDraw**
```java
    /**
     * If this view doesn't do any drawing on its own, set this flag to
     * allow further optimizations. By default, this flag is not set on
     * View, but could be set on some View subclasses such as ViewGroup.
     *
     * Typically, if you override {@link #onDraw(android.graphics.Canvas)}
     * you should clear this flag.
     *
     * @param willNotDraw whether or not this View draw on its own
     */
    public void setWillNotDraw(boolean willNotDraw) {
        setFlags(willNotDraw ? WILL_NOT_DRAW : 0, DRAW_MASK);
    }
```
如果一个View不需要绘制任何内容那么设置这个标记为true以后,系统会进行相应的优化,View默认没有开启这个标记,ViewGroup默认开启这个标记
开发中我们的自定义控件继承于ViewGroup并且本身不具备绘制功能时可以开启该标记便于系统进行后续优化,当这个自定义控件是通过onDraw来绘制内容时,需要我们显式关闭这个标记

### 自定义View
#### 1.自定义View的分类
* 继承View重新onDraw方法
* 继承ViewGroup,派生特殊的Layout
* 继承特定的View(系统控件)
* 继承特定的ViewGroup(系统控件)

#### 2.自定义View须知
* 让View支持wrap_content
* 让View支持padding
* 尽量不要再View中使用Handler
* View中的线程或动画需及时停止
* 处理好View的嵌套滑动冲突

>onDraw()方法：无论单一View，或者ViewGroup都需要实现该方法，因其是个空方法



### 参考资料
[Android艺术开发探索]()
[图解View测量、布局及绘制原理](https://www.jianshu.com/p/3d2c49315d68)
[简析Window、Activity、DecorView以及ViewRoot之间的错综关系](https://www.jianshu.com/p/8766babc40e0)
![](https://upload-images.jianshu.io/upload_images/3985563-e773ab2cb83ad214.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/690/format/webp)

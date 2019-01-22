##DataBinding的基本使用

注意:(本文基于AndroidStudio1.5及以上工具)

####1.DataBinding介绍
2015年谷歌I/O大会上介绍了一个框架DataBinding，DataBinding是一个数据绑定框架，以前我们在Activity里写很多的findViewById，现在如果我们使用DataBinding，就可以抛弃findViewById。DataBinding主要解决了两个问题： 
- 需要多次使用findViewById，损害了应用性能且令人厌烦 
- 更新UI数据需切换至UI线程，将数据分解映射到各个view比较麻烦
	
####2.DataBinding的导入
在appModule的build.gradle文件中添加开启DataBinding的代码:
```xml
	android {
	    ...
	    //导入dataBinding支持
	    dataBinding{
	        enabled true
	    }
	    ...
	}
```
>使用DataBinding在写布局时必须以`<layout>`为根布局标签包裹我们的展示布局,否则在编译期间无法生成对应的Binding对象,运行时会报错`java.lang.RuntimeException: view tag isn't correct on view:null`
	
####3.DataBinding基本使用包括一下内容
* [摆脱findViewById](#3.1)
* [绑定基本数据类型及String](#3.2)
* [绑定Module数据](#3.3)
* [绑定事件](#3.4)
* [通过静态方法转换数据类型](#3.5)
* [通过运算符操作数据](#3.6)
* [自定义binding的类名](#3.7)
* [绑定相同Moudle的操作](#3.8)
* [绑定model自动更新数据](#3.9)
* [绑定List/Map等集合数据](#3.10)
* [Observable自动更新](#3.11)
* [Databinding与include标签的结合](#3.12)


#####<h id="3.1">3.1摆脱findVIewById<h>
布局通过DataBindingUtils.setContentView(context,rid)加载到代码中，而且会生成对应一个Binding对象，对象名是布局文件文称加上Binding后缀,通过Binding对象.id名称，就能相当于拿到了指定的布局中的id的控件了，使用起来和findviewbyid获取的控件是一样的
Fragment中的使用:DataBinding库还提供了另外一个初始化布局的方法：DataBindingUtil.inflate()。
```java
@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_blank,container,false);
        return binding.getRoot();
    }
```

#####<h id="3.2">3.2绑定基本数据类型及String<h>
在`<layout>`下加多一个和我们展示布局同级的根标签`<data>`,用于定义我们的数据的名称和类型
```xml
<layout>
    <data>
        <!--绑定基本数据类型及String-->
        <!--name:   和java代码中的对象是类似的，名字自定义-->
        <!--type:   和java代码中的类型是一致的-->
        <variable
            name="content"
            type="String" />

        <variable
            name="enabled"
            type="boolean" />
    </data>
    <!--我们需要展示的布局-->
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <!--绑定基本数据类型及String的使用是通过   @{数据类型的对象}  通过对应数据类型的控制显示-->
        <Button
            android:id="@+id/main_tv2"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:clickable="@{enabled}"
            android:text="@{content}" />
    </LinearLayout>
</layout>
```
在布局中通过@{}来绑定数据,{}中的数据必须与该控件属性对应的数据类型匹配,否则会在编译报错`Error:Cannot find the setter for attribute 'android:enabled' with parameter type java.lang.String on android.widget.TextView. `,在编译后生成的Binding对象中生成了对应数据名称的get,set方法,以供修改和获取数据


#####<h id="3.3">3.3绑定model数据<h>
绑定model和绑定数据基本相同,这里我把单个的数据换成了一个模型(Moudle),把分散的数据封装到模型里
```xml
<layout>
    <data>
        <!--绑定Model数据2中形式，一中是导入该类型的，一种指定类型的全称，和java一样-->
        <!--  方式一 -->
        <variable
            name="user"
            type="com.maotou.module.User" />
        <!--  方式二 -->
        <import type="com.maotou.module.User" />
        <variable
            <name="user"
            type="User" />

    </data>
    <!--我们需要展示的布局-->
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <Button
            android:id="@+id/main_btn3"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@{user.text}" /><!--这里user.text相当于user.getText()-->
    </LinearLayout>
    </layotu>
```
同样的会有`setUser`和`getUser`直接设置一个对象


#####<h id='3.4'>3.4事件的绑定<h>
事件的绑定也和绑定module差不多,事件绑定的是一个接口,通过@{}给onClick绑定事件接口的回调方法
```xml
<layout>
    <data>
        <variable
            name="event"
            type="www.zhang.com.databinding.model.EventListener" />
    </data>
    <!--我们需要展示的布局-->
        <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical">
            <Button
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:onClick="@{event.click1}"
                android:text="事件绑定写法1" />

            <!--android:onClick="@{event::click2}"  编译报错没关系，可以运行的-->
            <Button
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:onClick="@{event::click2}"
                android:text="事件绑定写法2" />
            <Button
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:onClick="@{()->event.cilck3(title4)}"
                android:text="事件绑定写法3" />
                
               <!-- [注]：()->event.cilck3(title4)是lambda表达式写法，也可以写成：(view)->event.cilck3(title4),前面(view)表示onClick方法的传递的参数，如果event.click3()方法中不需要用到view参数，可以将view省略。
               当然event.click1也可以写成(view)->event.click1(view)，将onClick(View view)的view参数传递给event.click1(view)方法。
               大概就这意思，以下是伪代码
               onclick(View view){
                                event.click1(view)
                                }-->

        </LinearLayout>
</layout>
```
```java
public interface EventListener{
    public void click1(View v);
    public void click2(View v);
    public void cilck3(String s);
}
```
```java
public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //通过DataBInding加载布局都会对应的生成一个对象，如ActivityMainBinding，对象名在布局文件名称后加上了一个后缀Binding
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        binding.setTitle1("事件绑定1");
        binding.setTitle2("事件绑定2");
        binding.setTitle3("事件绑定3");
        binding.setTitle4("change ok");

        binding.setEvent(new EventListener() {
            @Override
            public void click1(View v) {
                binding.setTitle1("事件1方法调用");
            }

            @Override
            public void click2(View v) {
                binding.setTitle2("事件2方法调用");
            }

            @Override
            public void cilck3(String s) {
                binding.setTitle3(s);
            }
        });
    }
}
```

#####<h id='3.5'>3.5通过静态方法转化数据类型<h>
布局文件
```xml
...
<data>
     <variable
            name="user"
            type="www.zhang.com.databinding.User" />

        <!--调用静态方法，需要先导入需要的包    当然java中的lang包可以不用导包-->
        <import type="www.zhang.com.databinding.Utils" />
</data>
 ...
 
 <!--就和java中写代码一样，只要符合数据类型就好-->
 <Button
            android:id="@+id/main_btn5"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@{Utils.getName(user)}" />
            
 <TextView
            android:id="@+id/tv_name"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{`是否为空:`+TextUtils.isEmpty(user.name)}" />
...
```
静态方法类(也可以是Java自带api)
```java

public class Utils {
    public static String getName(Object o) {
        return o.getClass().getName();
    }
}
```

#####<h id='3.6'>3.6通过运算符操作数据<h>
```xml
<Button
            android:id="@+id/main_btn5"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:paddingLeft="@{user2.state ? @dimen/largepadding : (int)@dimen/smallpadding}"
            android:text="@{user2.content ?? @string/app_name}" />
        <!-- android:text="@{user2.content ?? @string/app_name}"
         等价于
         android:text="@{user2.content!=null? user2.content : @string/app_name}"-->

        <Button
            android:id="@+id/main_btn6"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@{`Hello World`+ @string/app_name}" /><!-- ``字符包裹的表示字符串，可用作拼接字符串 -->

```
DataBinding支持的表达式有：

* 数学表达式： + - / * %
* 字符串拼接 +
* 逻辑表达式 && ||
* 位操作符 & | ^
* 一元操作符 + - ! ~
* 位移操作符 >> >>> <<
* 比较操作符 == > < >= <=
* instanceof
* 分组操作符 ()
* 字面量 - character, String, numeric, null
* 强转、方法调用
* 字段访问
* 数组访问 []
* 三元操作符 ?:
* 聚合判断（Null Coalescing Operator）语法 ‘??’

#####<h id='3.7'>3.7自定义Binding的类名<h>
data标签有个class属性，它可以用来对Binding对象重新命名(一般是以布局文件名加上Binding后缀作为该Binding类名)
自定义类名有3中方式 :
1.通过指定全类名的方式
```
<data class="www.zhang.com.databinding.activity.Item">
    ...
</data>

import www.zhang.com.databinding.activity.Item;

Item binding = DataBindingUtil.setContentView(FiveActivity.this, R.layout.activity_five);

```
2.直接生成在项目的包目录下
```
<data class=".Item">
    ...
</data>

import www.zhang.com.databinding.Item;

Item binding = DataBindingUtil.setContentView(FiveActivity.this, R.layout.activity_five);
```
3.如果FiveActivity直接在包下与方式二相同，如果FiveActivity在包的子目录下，则Binding生成的目录如下
```
<data class="Item">
    ...
</data>

import www.zhang.com.databinding.databinding.Item;

Item binding =DataBindingUtil.setContentView(FiveActivity.this, R.layout.activity_five);
```

#####<h id='3.8'>3.8绑定相同model的操作<h>
绑定相同model有2种，一种是同类的2个对象，另一种类名相同的2个类

* 同类的2个对象
```xml
<data>

        <import type="www.zhang.com.databinding.User" />

        <variable
        name="user3"
        type="User" />

        <variable
        name="user4"
        type="User" />

    </data>
```
* 类名相同的2个类
```xml
<data>

        <import type="www.zhang.com.databinding.User" />

        <variable
            name="user4"
            type="User" />
<!--因为type="User"都为User类，会导致不知道到那个包，所以可以通过alias属性重命名type的类型，但实际上alias被指定的那个类型(如：www.zhang.com.databinding.model.User)-->
        <import type="www.zhang.com.databinding.model.User" alias="Model"/>

        <variable
            name="user5"
            type="Model"  />

    </data>
```
因为2个地方都有type=”User”都为User类，会导致不知道导入哪个包，所以可以通过alias属性重命名type的类型

#####<h id='3.9'>3.9绑定model自动更新数据<h>
Model类继承BaseObservable,BaseObservable实现Android.databinding.Observable接口，Observable接口可以允许附加一个监听器到model对象以便监听对象上的所有属性的变化。

Observable接口有一个机制来添加和删除监听器，但通知与否由开发人员管理。为了使开发更容易，BaseObservable实现了监听器注册机制。DataBinding实现类依然负责通知当属性改变时。这是通过指定一个Bindable注解给getter以及setter内通知来完成的。

notifyPropertyChanged(BR.参数名)通知更新这一个参数，需要与@Bindable注解配合使用。notifyChange()通知更新所有参数，可以不用和@Bindable注解配合使用

#####<h id='3.10'>3.10绑定List/Map等集合数据<h>
布局文件
```xml
...
 <data>
        <import type="java.util.ArrayList" />

        <import type="java.lang.String" />

        <variable
            name="list"
            type="ArrayList&lt;String>" />
        <!--泛型的支持会在编译时期报红线，但是是可以直接运行的
        但是需要通过转义字符才行，如：&lt;数据类型> 或者&lt;数据类型&gt;-->

        <import type="java.util.Map" />

        <variable
            name="map"
            type="Map&lt;String,String&gt;" />

        <variable
            name="arrays"
            type="String[]" />
    </data>
...
<TextView
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="@{list[0]}" />
        <!--List集合既可以和数组一样通过索引获取值list[index]方式，也可以调用API-->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="@{list.get(1)}" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="@{map[`name`]}" />
        <!--Map集合既可以通过map[key]的方式，也可以通过调用API-->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="@{map.get(`age`)}" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="@{arrays[0]}" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="@{arrays[1]}" />
...
```
泛型的支持会在编译时期报红线，是可以直接运行的,但是需要通过转义字符才行,
>如：&lt;数据类型> 或者 &lt;数据类型&gt;

#####<h id='3.11'>3.11 Observable数据改变自动更新<h>
Observable是一个接口，它的子类有BaseObservable,ObservableField,ObservableBoolean, ObservableByte, ObservableChar, ObservableShort, ObservableInt, ObservableLong, ObservableFloat, ObservableDouble, and ObservableParcelable，ObservableArrayList,ObservableArrayMap
布局文件
```xml
...
<data>
        <import type="www.zhang.com.databinding.model.Animal"/>
        <variable
            name="animal"
            type="Animal"/>
        <variable
            name="list"
            type="android.databinding.ObservableArrayList&lt;String&gt;"/>

        <variable
            name="map"
            type="android.databinding.ObservableArrayMap&lt;String,String&gt;"/>
    </data>
...
<TextView
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="@{animal.field}" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="@{String.valueOf(animal.age)}" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="@{list[0]}" />
        <!--Map集合既可以通过map[key]的方式，也可以通过调用API-->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="@{list[1]}" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="@{map[`name`]}" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="@{map[`age`]}" />
```
Animal类
```java
public class Animal {
    public final ObservableField<String> field = new ObservableField<>();
    public final ObservableInt age = new ObservableInt();
}
```

#####<h id='3.12'>3.12 Databinding与include标签的结合<h>
主布局文件
```xml
...
<data>

        <variable
            name="text"
            type="String"/>

    </data>
...
<include
            android:id="@+id/include"
            layout="@layout/item_include"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            bind:text="@{text}"
            />

        <TextView
            android:layout_below="@+id/include"
            android:layout_marginTop="20dp"
            android:id="@+id/text_view"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{text}" />
...
```

include的布局文件
```xml
...
<data>

        <variable
            name="text"
            type="String"/>

    </data>
...
<TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{`include:`+text}"/>
...
```

在布局文件中通过bind:text=@{text}将text参数传递到include里面











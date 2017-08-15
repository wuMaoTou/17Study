###前言
在IDE上跟进查看源码时,源码里总是各种找不到类或注解而红色报警,这些默认在IDE中无法查找到的类或者方法，一般都是因为其被标识了@hide或者是属于com.android.internal中的类。
因此我们查看API源码会发现很多类或方法找不到,如PhoneWindow，ActivityThread等都没有找到！ 
这时只能去Android SDK源码目录搜索找不到的类打开来查看源码,很不方便

###解决方案
解决方案很简单,去掉@hide标示,加上属于com.android.internal中的class文件或者方法.在GitHub上已有人帮我们做好了这事:[android-hidden-api ](https://github.com/anggrayudi/android-hidden-api )

我们只需下载对应API版本Android.jar,然后替换SDK/platforms/android-版本/Android.jar 
这样在跟进源码就不会到处报错了

方替换了jar包后还有额外便利,就是可以直接使用隐藏API,不需要通过反射来调用这些隐藏的API
例如,挂断电话API被隐藏了TelephonyManager.getDefault().endCall(), 
用正常Android.jar无法调用endCall(),只能通过反射调用; 
用去除@hide的Android.jar,就可直接调用endCall();

直接调用隐藏API的缺点: 
1.Android隐藏API是因为不能保证这些API还存在新系统版本,所以尽量少用隐藏API！ 
2.不利于团队合作,如果有人使用正常Android.jar就无法编译如endCall()之类的隐藏API！


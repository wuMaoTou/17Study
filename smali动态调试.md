##smali动态调试

###1.开启调试模式
下载[setpropex](https://www.liangchan.net/liangchan/11075.html)

拷贝对应CPU架构的文件到/system/xbin/目录下

重启手机

```
adb shell su

setpropex ro.debuggable 1

注:如果还不行,修改完后kill system_server 就好了，再adb jdwp看看

adb shell su
getprop ro.debuggable
可查询设置是否成功

```

###2.反编译获取smali代码
反编译apk获取所有smali代码,创建项目文件夹,里面创建一个src文件夹,把所有smali代码复制到src文件夹里

###3.导入项目
1.使用android studio - import project -选择刚才创建的项目打开
2.导入后，点击顶部菜单栏的这个位置，「Edit Configurations」，点「+」选择「Remote」
3.随便填个名字，然后设置一个端口号

###4.开启等待调试
```
adb shell su

am start -D -n <Packagename>/<MainActivity Name>

此时手机提示Wait for Dubugger

打开sdk下的tools/monitor.bat

找到标有红色虫子的进程,记录端口号(8632)

点击顶部菜单栏的这个位置，「Edit Configurations」,修改端口号为8632

点击顶部 [Run] -「Debug」 开始调试
```
![](\images\monitor.png)

![](\images\android_studio_configurations.png)
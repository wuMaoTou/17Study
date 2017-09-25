#Ubuntu上使用Fiddler抓包

此前一直在windows上开发一直使用的是Fiddler做为抓包工具,Fiddler真的很好用,很方便,后来转到mac上使用Charles也不错,用这和fiddler一样顺手,再到现在使用ubuntu开发,一直没找到太好用的抓包工具,最近刚好发现了ubuntu下页可以使用fiddler,估计我不是最后一个发现的O(∩_∩)O哈哈~

##正确启动方式
1.首先安装Mono环境
```
sudo apt-get update

sudo apt-get install mono-complete
```

2.下载Fiddler for Mono
[http://fiddler.wikidot.com/mono](http://fiddler.wikidot.com/mono)
![](/home/lichun/桌面/fiddler_download.png) 

3.解压运行
解压后进入解压目录,命令启动fiddler
```
mono Fiddler.exe
```
启动fiddler,浪起来

4.快捷启动
每次都命令行启动,麻烦有木有,做个快捷方式吧
4.1终端进入快捷方式存放目录
```
cd /usr/share/applications
```

4.2新建启动方式文件
```
sudo gedit fiddler.desktop
```
添加如下内容：
```
[Desktop Entry]
Encoding=UTF-8
Name=Fiddler
Comment=Fiddler
Exec= /home/这里来一段文件路径/解压目录/fiddler.sh
Terminal=false
#fiddler.png是自己找来的一张图片做图标
Icon=/home/这里来一段文件路径/解压目录/fiddler.png
Type=Application
Categories=Application;Development;
```
**参数说明：**
Encoding  ----制定编码方式，一般是UTF-8
Name --------快捷方式的名字
Exec  ---------启动文件的路径（重要）
Terminal----------是否启动终端（黑框框）
Icon ----------指定图标路径
其他的参数和我一样就行了

完成后保存即可

4.3新建fiddler.sh文件
在解压目录下新建fiddler.sh文件
添加一下内容:
```
mono /home/这里来一段文件路径/解压目录/Fiddler.exe
```

4.4快捷方式
打开/usr/share/applications,将Fiddler复制到桌面或者快捷启动栏


##使用Fiddler
1.配置fiddler
1.1启动fiddler,打开fiddler设置页面
Tool - Fiddler Options
![](/home/lichun/桌面/open_options.png) 

1.2开启拦截https
选择https选项卡，勾选下面两项，这样fiddler就可以拦截HTTPS请求了
![](/home/lichun/桌面/capture_https.png) 

1.3开启代理
选择connections选项卡，勾选下面一项，这样就可以允许远程机器把HTTP/HTTPS请求发送到Fiddler上来
![](/home/lichun/图片/connections.png) 
**注:**端口号默认为8888,可手动修改端口号,避免撞号

2.配置手机wifi
首先要保证手机和电脑在同一个网段下
2.1设置代理
打开手机的wifi设置页面给当前使用的wifi设置代理,代理服务器添上你电脑的IP地址,端口号添上Fiddler上设置的端口号,保存即可
![](/home/lichun/桌面/proxy.png) 




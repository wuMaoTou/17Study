我们可以通过修改hosts文件来提速,获取github的IP地址

1.手动访问：[DNS查询网站](http://tool.chinaz.com/dns/)，然后依次获取以下三个网址的IP

github.com
github.githubassets.com
avatars0.githubusercontent.com

选择TTL最大的IP，在hosts文件最后添加

192.30.253.113 github.com
151.101.25.194 github.global.ssl.fastly.net
192.30.253.121 codeload.github.com

--- 
修改系统本地的hosts文件，添加内容


**Windows**系统的hosts文件路径：C:\Windows\System32\drivers\etc\hosts

添加下面查询到的IP到hosts文件中。。

```
# GitHub Start
    192.30.253.113  github.com
    185.199.109.154 github.githubassets.com
    185.199.111.154 github.githubassets.com
    203.98.7.65     gist.github.com
    151.101.108.133 assets-cdn.github.com
    151.101.108.133 raw.githubusercontent.com
    151.101.108.133 gist.githubusercontent.com
    151.101.108.133 cloud.githubusercontent.com
    151.101.108.133 camo.githubusercontent.com
    151.101.108.133 avatars0.githubusercontent.com
    151.101.108.133 avatars1.githubusercontent.com
    151.101.108.133 avatars2.githubusercontent.com
    151.101.108.133 avatars3.githubusercontent.com
    151.101.108.133 avatars4.githubusercontent.com
    151.101.108.133 avatars5.githubusercontent.com
    151.101.108.133 avatars6.githubusercontent.com
    151.101.108.133 avatars7.githubusercontent.com
    151.101.108.133 avatars8.githubusercontent.com
# GitHub End
```

刷新DNS
ipconfig /flushdns

**
linux**系统配置流程

打开hosts文件并修改
sudo vim /etc/hosts

2.重启网络服务
sudo /etc/init.d/networking restart
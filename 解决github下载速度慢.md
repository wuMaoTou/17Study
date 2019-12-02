我们可以通过修改hosts文件来提速,获取github的IP地址

1.手动访问：https://www.ipaddress.com/ 网址，然后依次获取以下三个网址的IP

github.com
github.global.ssl.fastly.net
codeload.github.com

这是我获取的IP

192.30.253.113 github.com
151.101.25.194 github.global.ssl.fastly.net
192.30.253.121 codeload.github.com

--- 
修改系统本地的hosts文件，添加内容


**Windows**系统的hosts文件路径：C:\Windows\System32\drivers\etc\hosts

添加下面查询到的IP到hosts文件中。。

192.30.253.113 github.com
151.101.25.194 github.global.ssl.fastly.net
192.30.253.121 codeload.github.com

刷新DNS
ipconfig /flushdns

**
linux**系统配置流程

打开hosts文件并修改
sudo vim /etc/hosts

2.重启网络服务
sudo /etc/init.d/networking restart
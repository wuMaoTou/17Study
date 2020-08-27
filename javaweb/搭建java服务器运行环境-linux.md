1.安装jdk
  -下载jdk linux版本(*.tar.gz)
  https://www.oracle.com/cn/java/technologies/javase-jdk8-downloads.html

  -通过ftp上传文件服务器

  -解压jdk (/usr/local/src/java/)
    `tar -zxvf jdk-8u181-linux-x64.tar.gz`
   
  -配置环境变量
    普通用户 `vim ~/.bashrc` 
    root用户 `vim /etc/profile`
    `
        export JAVA_HOME=/home/hjw/app/jdk1.8.0_181
	export PATH=$JAVA_HOME/bin:$PATH
	export JAVA_BIN=$JAVA_HOME/bin
	export JAVA_LIB=$JAVA_HOME/lib
	export JRE_HOME=$JAVA_HOME/jre
	export CLASSPATH=.:$JAVA_LIB/tools.jar:$JAVA_LIB/dt.jar
    `
    普通用户 `source ~/.bashrc` 
    root用户 `v`sourceim /etc/profile`

  -命令行测试
    `java -version`

2.安装tomcat
  -下载tomcat linux版本(*.tar.gz)
  http://tomcat.apache.org/

  -通过ftp上传文件服务器

  -解压jdk (/usr/local/src/tomcat/)
    `tar -zxvf apache-tomcat-8.5.33.tar.gz`

  -运行tomcat
    `./startup.sh`
  
  -浏览器输入http://公网ip:8080查看tomcat主页面(服务器安全组开发8080端口)

  -tomcat开机自启
    修改脚本文件rc.local，这个脚本是使用者自定的开机启动程序，可以在里面添加想在系统启动之后执行的脚本或者脚本执行命令
    添加内容
      `/usr/local/src/tomcat/apache-tomcat-8.5.34/bin/startup.sh`
    授权
      chmod 777 /etc/rc.d/rc.local

  -windows tomcat开机自启
	下载安装完成tomcat后,cd到tomcat的bin目录下,执行服务安装命令
	  `service.bat install`
	命令启动安装好的服务
	  `net start tomcat8`
	按住win+R并输入services.msc，找到tomcat8服务并设置属性为自动启动即可。

3.安装mysql
  -下载安装rpm文件(注意修改版本号)
	`rpm -Uvh http://repo.mysql.com/mysql-community-release-el6-5.noarch.rpm`

  -查看所有发行版本
	`yum repolist all | grep mysql`

  -已用和禁用安装版本
	`sudo yum-config-manager --disable mysql80-community`
	`sudo yum-config-manager --enable mysql57-community`

  -命令行安装mysql
    `yum -y install mysql-community-server`
  
  -开机启动
    `systemctl enable mysqld`

  -启动mysql服务
    `systemctl start mysqld`

  -配置mysql
    运行 `mysql_secure_installation`
    提示 Enter current password for root (enter for none):由于是刚刚安装，直接按回车通过
    `Set root password? [Y/n] y` 输入y设置root密码
    `Remove anonymous users? [Y/n] y` 删除匿名用户
    `Disallow root login remotely? [Y/n] y ` 禁止root远程登录
    `Remove test database and access to it? [Y/n] y ` 删除test数据库
    `Reload privilege tables now? [Y/n] y` 刷新权限
    如果运行上面的命令中途发生错误：ERROR 1558 (HY000): Column count of mysql.user is wrong. Expected 43, found 39.
    运行`mysql_upgrade -uroot -p`
   
  -进入mysql,开启远程访问权限
    `mysql -uroot -p+密码`
    `use mysql;`
    `GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '密码' WITH GRANT OPTION;`

  **注:** 
   1.安装错误：

      软件包：mysql-community-server-5.7.20-1.el6.x86_64 (mysql57-community)
      需要：libsasl2.so.2()(64bit)
      解决方法：
      
      修改vim /etc/yum.repos.d/mysql-community.repo 源文件
      
      [mysql57-community]
      name=MySQL 5.7 Community Server
      ## baseurl=http://repo.mysql.com/yum/mysql-5.7-community/el/6/$basearch/  
      baseurl=http://repo.mysql.com/yum/mysql-5.7-community/el/7/$basearch/
      enabled=1
      gpgcheck=0
      gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-mysql   

   2.centOS删除mysql

	  1、备份数据库，升级MySQL通常不会丢失数据，但保险起见，我们需要做这一步。输入命令：
		mysqldump -u xxx -h xxx -P 3306 -p --all-databases > databases.sql
	  2、停止MySQL服务，输入命令：
		service mysqld stop
	  3、卸载旧版MySQL，输入命令：
		yum remove mysql mysql-*
		执行过程中会询问你是否移除，此时输入“Y”
		查看已安装的软件：rpm -qa¦grep mysql
		卸载mysql：yum remove mysql mysql-server mysql-libs compat-mysql51
	  4、移除命令执行后，可再看看是否有残余的mysql，输入命令：
		yum list installed | grep mysql
		如果有，可输入命令删除：
		yum remove mysql－libs
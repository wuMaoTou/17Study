1.��װjdk
  -����jdk linux�汾(*.tar.gz)
  https://www.oracle.com/cn/java/technologies/javase-jdk8-downloads.html

  -ͨ��ftp�ϴ��ļ�������

  -��ѹjdk (/usr/local/src/java/)
    `tar -zxvf jdk-8u181-linux-x64.tar.gz`
   
  -���û�������
    ��ͨ�û� `vim ~/.bashrc` 
    root�û� `vim /etc/profile`
    `
        export JAVA_HOME=/home/hjw/app/jdk1.8.0_181
	export PATH=$JAVA_HOME/bin:$PATH
	export JAVA_BIN=$JAVA_HOME/bin
	export JAVA_LIB=$JAVA_HOME/lib
	export JRE_HOME=$JAVA_HOME/jre
	export CLASSPATH=.:$JAVA_LIB/tools.jar:$JAVA_LIB/dt.jar
    `
    ��ͨ�û� `source ~/.bashrc` 
    root�û� `v`sourceim /etc/profile`

  -�����в���
    `java -version`

2.��װtomcat
  -����tomcat linux�汾(*.tar.gz)
  http://tomcat.apache.org/

  -ͨ��ftp�ϴ��ļ�������

  -��ѹjdk (/usr/local/src/tomcat/)
    `tar -zxvf apache-tomcat-8.5.33.tar.gz`

  -����tomcat
    `./startup.sh`
  
  -���������http://����ip:8080�鿴tomcat��ҳ��(��������ȫ�鿪��8080�˿�)

  -tomcat��������
    �޸Ľű��ļ�rc.local������ű���ʹ�����Զ��Ŀ����������򣬿����������������ϵͳ����֮��ִ�еĽű����߽ű�ִ������
    �������
      `/usr/local/src/tomcat/apache-tomcat-8.5.34/bin/startup.sh`
    ��Ȩ
      chmod 777 /etc/rc.d/rc.local

3.��װmysql
  -�����а�װmysql
    `rpm -Uvh http://dev.mysql.com/get/mysql-community-release-el7-5.noarch.rpm`
    `yum -y install mysql-community-server`
  
  -��������
    `systemctl enable mysqld`

  -����mysql����
    `systemctl start mysqld`

  -����mysql
    ���� `mysql_secure_installation`
    ��ʾ Enter current password for root (enter for none):�����Ǹոհ�װ��ֱ�Ӱ��س�ͨ��
    `Set root password? [Y/n] y` ����y����root����
    `Remove anonymous users? [Y/n] y` ɾ�������û�
    `Disallow root login remotely? [Y/n] y ` ��ֹrootԶ�̵�¼
    `Remove test database and access to it? [Y/n] y ` ɾ��test���ݿ�
    `Reload privilege tables now? [Y/n] y` ˢ��Ȩ��
    ������������������;��������ERROR 1558 (HY000): Column count of mysql.user is wrong. Expected 43, found 39.
    ����`mysql_upgrade -uroot -p`
   
  -����mysql,����Զ�̷���Ȩ��
    `mysql -uroot -p+����`
    `use mysql;`
    `GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '����' WITH GRANT OPTION;`
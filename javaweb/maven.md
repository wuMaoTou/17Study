1.maven介绍
  什么是maven
  maven是apache下的一个纯java开发的开源项目,是一个项目管理工具,使用maven对java项目进行构建,依赖管理

  maven项目构建过程
	清理 - 编译 - 测试 - 报告 - 打包 - 部署
  maven工程构建的优点: 1.一个命令完成构建运行,方便快捷 2.maven对每个构建阶段进行规范,非常有利于大型团队协作开发

2.依赖管理
  什么是依赖
  一个java项目可能要使用一些第三方的jar包才可以运行,那么我们说这个java项目依赖了这些第三方的jar包
  
  什么是依赖管理
  就是对项目所有依赖的jar包进行规范管理

  maven项目的依赖管理
  maven项目管理所依赖的jar包不需要手动向工程添加jar包,只需要在pom.xml(maven工程配置文件)添加jar包的坐标,自动从maven仓库中下载jar包,运行

  使用maven依赖管理的好处
  1.通过pom.xml文件对jar包的版本进行统一管理,可以避免版本冲突
  2.maven团队维护了一个非常全的maven仓库,里面包括了当前使用的jar包,maven工程可以自动从maven仓库下载jar包,非常方便

3.使用maven的好处
  1.一步构建
  2.依赖管理
  3.maven的跨平台
  4.maven遵循规范,提高效率降低维护成本
  
4.maven安装
  下载安装
  官网下载 http://maven.apache.org/download.cgi 解压到不含中文和空格的目录中
  
  包结构
    bin目录 mvn.bat (以run方式运行项目), mvnDebug.bat(以debug方式运行项目)
    boot 目录 maven 运行需要类加载器
    conf 目录 setting.xml 整个 maven 工具核心配置文件
    lib 目录 maven 运行依赖jar包

  环境变量配置
  配置path - /项目路径/bin
  在MAVEN_HOME/conf/setting.xml 文件中的<localRepository>标签中可配置本地仓库位置

5.maven使用
  运行web工程
    进入maven工程目录,运行tomcat:run目录
  
  maven项目工程目录结构
    Project
      |-src
      |  |-main
      |  |  |-java -- 存放项目的.java文件
      |  |  |-resources -- 存放项目资源文件,如 spring,hibernate配置文件
      |  |  |-webapp -- webapp目录是web工程的主目录
      |  |      |-WEB-INF
      |  |	    |-web.xml
      |  |-test
      |      |-java -- 存放所有测试.java文件,如JUnit测试类
      |      |-resources -- 测试资源文件
      |-target -- 目标文件输出位置,如 .class, .jar, .war文件
      |-pom.xml -- maven项目核心配置文件
 
  maven常用命令
    1.compile - 编译命令,编译/src/main/java下的所有.java文件
    2.test - 测试命令,执行/src/test/java下的测试单元
    3.clean - 清理命令,执行删除target目录的内容
    4.package - 打包命令,java工程打包成jar包,web工程打包成war包
    5.install - 安装命令,将工程打包发布到本地仓库

6.项目构建
  定义maven坐标
```
    <!-- 项目名称,定义为组织名+项目名,类似包名 -->
    <groupId>cn.maotou.maven</groupId>
    <!-- 模块名称 -->
    <artifactId>maven-first</artifactId>
    <!-- 当前项目版本号,snapshot为快照版本即非正式版本, release为正式版本-->
    <version>0.0.1-SNAPSHOT</version>
    <packageing></packageing>:打包类型(jar,war,pom通常父工程设置为pom)
```

7.依赖管理-添加依赖
  添加依赖 - dependency
  在 pom.xml 中添加dependency标签
```
    <dependency>
	<groupId></groupId>
	<artifactId></artifactId>
	<version></version>
    </depandency>
```
  
  查找坐标
  方法一:从互联网搜索
    http://search.maven.org
    http://mvnrepository.com
  方法二:使用maven插件的索引功能

  依赖范围 (scop)
    1.compile - 编译范围,默认依赖范围
    2.provided - 当一个容器已提供该依赖后才使用
    3.runtime - 在运行和测试系统时需要
    4.test - 测试范围依赖,在编译运行时不需要
    5.system - 类似provided

8.依赖冲突解决
  依赖调解原则
    1.第一声明者优先原则,在pom文件定义依赖,先声明的依赖为准
    2.路径近者优先原则
  
  排除依赖
```
    通过定义<exclusions>标签排除
    <exclusions>
        <exclusion>
	    <groupId>org.springframework</groupId>
	    <artifactId>spring-beans</artifactId>
	</exclusion>
    </exclusions>
```

  锁定版本
```
     不考虑依赖路径,声明优化等因素可以采用这种方法
     <dependencyManagement>
     	<dependencies>
		锁定版本的依赖
        </dependencies>
     </dependencyManagement>
```

9.分模块问题
  分模块时,spring注入配置文件之间需要通过<import>标签引入依赖模块的配置文件

10.nexus
  下载安装 http://www.sonatype.org/nexus/archived
    安装 - nexus.bat install 
    卸载 - nexus.bat uninstall
    可在服务列表启动nexus服务

  配置文件 - /conf/nexus.properties
    # Jetty section
    application-port=8081 # nexus的访问端口配置
    application-host=0.0.0.0 # nexus主机监听配置(不需要修改)
    nexus-webapp=${bundleBasedir}/nexus # nexus工程目录
    nexus-webapp-context-path=/nexus # nexus的web访问路径

    # Nexus section
    nexus-work=${bundleBasedir}/../sonatype-work/nexus # nexus仓库目录
    runtime=${bundleBasedir}/nexus/WEB-INF # nexus运行程序目录
  
  访问地址
    http://localhost:8081/nexus/
    内置账户密码 admin/admin123

  仓库类型
    1.hosted - 宿主仓库,部署自己的jar到这个类型的仓库
    2.proxy - 代理仓库,用于代理远程的公共仓库
    3.group - 仓库组,用来合并多个hosted/proxy仓库
    4.virtual - 兼容maven1版本的jar或插件

    nexus仓库默认在sonatype-work目录中
   
  发布项目到私服
```
     修改maven ./conf/setting.xml
	<server>
	  <id>releases</id>
	  <username>admin</username>
	  <password>admin123</password>
	</server>
	<server>
	  <id>snapshots</id>
	  <username>admin</username>
	  <password>admin123</password>
	</server>
     修改pom.xml
        <distributionManagement>
            <repository>
                <id>releases</id>
                <url>http://localhost:8081/nexus/content/repositories/releases/</url>
            </repository>
            <snapshotRepository>
                <id>snapshots</id>
                <url>http://localhost:8081/nexus/content/repositories/snapshots/</url>
            </snapshotRepository>
	</distributionManagement>
```

  从私服下载jar包
```
    修改maven ./conf/setting.xml
    <profiles>
      <id>dev</id>
      <repositories>
      	<repository>
      	   <!-- 仓库id,repositories可以配置多个仓库,id不可重复 -->
      	   <id>nexus</id>
      	   <!-- 仓库地址 -->
      	   <url>http://localhost:8081/nexus/content/groups/public</url>
      	   <release>
      	   	<enabled>true</enabled>
      	   </release>
      	   <snapshots>
      	   	<enabled>true</enabled>
      	   </snapshots>
      	</repository>
      </repositories>
      <pluginRepositories>
      	<pluginRepository>
      	   <id>public</id>
      	   <name>Public Repositories</name>
      	   <url>http://localhost:8081/nexus/content/groups/public</url>
      	</pluginRepository>
      </pluginRepositories>
    </profiles>
    <activeProfiles>
      <activeProfile>dev</activeProfile>
    </activeProfiles>

    修改pom.xml
    <dependencies>
        <dependency>
            <groupId>cn.woo.maven</groupId>
            <artifactId>ssh-dao</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
    </dependencies>
    <repositories>
        <repository>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
            <id>public</id>
            <name>Public Repositories</name>
            <url>http://localhost:8081/nexus/content/groups/public/</url>
        </repository>
        <repository>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>central</id>
            <name>Central Repositories</name>
            <url>https://repo.maven.apache.org/maven2</url>
        </repository>
    </repositories>

    <pluginRepositories>
        <pluginRepository>
            <id>public</id>
            <name>Public Repositories</name>
            <url>http://localhost:8081/nexus/content/groups/public/</url>
        </pluginRepository>
        <pluginRepository>
            <releases>
                <updatePolicy>never</updatePolicy>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>central</id>
            <name>Central Repositories</name>
            <url>https://repo.maven.apache.org/maven2</url>
        </pluginRepository>
    </pluginRepositories>
```
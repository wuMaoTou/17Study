1.Mybatis介绍
  定义
  MyBatis是一个优秀的持久层框架，它对jdbc的操作数据库的过程进行封装,使开发者只需要关注 SQL 本身

  基本原理
  Mybatis通过xml或注解的方式将要执行的各种statement（statement、preparedStatemnt、CallableStatement）配置起来，并通过java对象和statement中的sql进行映射生成最终执行的sql语句，最后由mybatis框架执行sql并将结果映射成java对象并返回

2.Mybatis架构
  1.mybatis配置
    `SqlMapConfig.xml`文件作为mybatis的全局配置文件,配置了mybatis的运行环境等信息
    `mapper.xml`文件为sql映射文件,文件中配置了操作数据库的sql语句,次文件需要在SqlMapConfig.xml中加载

  2.通过配置信息构造`SqlSessionFactory`会话工厂

  3.由会话工厂创建`sqlSession`会话,操作数据库需要通过`sqlSession`进行

  4.mybatis底层自定义了Executor执行器接口操作数据库，Executor接口有两个实现，一个是基本执行器、一个是缓存执行器

  5.Mapped Statement也是mybatis一个底层封装对象，它包装了mybatis配置信息及sql映射信息等。mapper.xml文件中一个sql对应一个Mapped Statement对象，sql的id即是Mapped statement的id

  6.Mapped Statement对sql执行输入参数进行定义，包括HashMap、基本类型、pojo，Executor通过Mapped Statement在执行sql前将输入的java对象映射至sql中，输入参数映射就是jdbc编程中对preparedStatement设置参数

  7.Mapped Statement对sql执行输出结果进行定义，包括HashMap、基本类型、pojo，Executor通过Mapped Statement在执行sql后将输出结果映射至java对象中，输出结果映射过程相当于jdbc编程中对结果的解析处理过程

3.Mybatis入门程序
  1.maven引入依赖
  	<dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
	    //对应安装的mysql版本
            <version>5.1.8</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.2.7</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.5</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
        </dependencies>
  2.在resources下加入配置文件logj.properties和mybatis-config.xml
  	logj.properties
	 `### direct log messages to stdout ###
          log4j.appender.stdout=org.apache.log4j.ConsoleAppender
          log4j.appender.stdout.Target=System.err
          log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
          log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
          
          ### direct messages to file mylog.log ###
          log4j.appender.file=org.apache.log4j.FileAppender
          log4j.appender.file.File=G\:mylog.log
          log4j.appender.file.layout=org.apache.log4j.PatternLayout
          log4j.appender.file.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
          
          ### set log levels - for more verbose logging change 'info' to 'debug' ###
          # error warn info debug trace
          log4j.rootLogger= debug, info, stdout`

        mybatis-config.xml
	  `<?xml version="1.0" encoding="UTF-8" ?>
	  <!DOCTYPE configuration
	          PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
	          "http://mybatis.org/dtd/mybatis-3-config.dtd">
	  <configuration>
	      <environments default="development">
	          <environment id="development">
	              <transactionManager type="JDBC"></transactionManager>
	              <dataSource type="POOLED">
	                  <property name="driver" value="com.mysql.jdbc.Driver"/>
	                  <property name="url" value="jdbc:mysql://localhost:3306/mybatis?cgaracterEncoding=utf-8"/>
	                  <property name="username" value="root"/>
	                  <property name="password" value="root"/>
	              </dataSource>
	          </environment>
	      </environments>
	      <mappers>
	          <mapper resource="mappers/user.xml"/>
	      </mappers>
	  </configuration>`
  3.创建pojo,pojo类作为mybatis进行sql映射使用,通常与数据库表对应
    创建表数据
    	`DROP TABLE IF EXISTS `user`;
         CREATE TABLE `user` (
           `id` int(11) NOT NULL AUTO_INCREMENT,
           `username` varchar(32) NOT NULL COMMENT '用户名称',
           `birthday` date DEFAULT NULL COMMENT '生日',
           `sex` char(1) DEFAULT NULL COMMENT '性别',
           `address` varchar(256) DEFAULT NULL COMMENT '地址',
           PRIMARY KEY (`id`)
         ) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;`
    创建pojo
    	`Public class User {
	private int id;
	private String username;// 用户姓名
	private String sex;// 性别
	private Date birthday;// 生日
	private String address;// 地址

	get/set……}`
  4.创建sql映射文件
    user.xml
    	`<?xml version="1.0" encoding="UTF-8" ?>
	 <!DOCTYPE mapper
	 PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
	 "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
	 <mapper namespace="user">
	     <select id="queryUserById" parameterType="int" resultType="com.mt.mybatis.User">
	         SELECT * FROM mybatis.user WHERE id = #{id}
	     </select>
	 </mapper>`
  5.在mybatis加入映射文件路径
  	`<mappers>
	     <mapper resource="mappers/user.xml"/>
	 </mappers>`
  6.测试
  	`public class MyBatisTest {

	    private SqlSessionFactory sessionFactory;

	    @Before
	    public void init() throws Exception {
	        //1.加载配置文件
	        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
		//2.创建SqlSessionFactory对象
	        sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
	    }

	    @Test
	    public void test () throws Exception {
	        //3.创建SqlSeesion对象
	        SqlSession sqlSession = sessionFactory.openSession();
		//4.SqlSession对象执行查询
	        Object user = sqlSession.selectOne("queryUserById", 1);
	        System.out.println(user);
		//5.释放资源
	        sqlSession.close();
	    }
	}`

4.Mybatis特点
  Mybatis解决jdbc编程的问题
    1、数据库连接创建、释放频繁造成系统资源浪费从而影响系统性能，如果使用数据库连接池可解决此问题。
    解决：在SqlMapConfig.xml中配置数据连接池，使用连接池管理数据库链接。
    2、Sql语句写在代码中造成代码不易维护，实际应用sql变化的可能较大，sql变动需要改变java代码。
    解决：将Sql语句配置在XXXXmapper.xml文件中与java代码分离。
    3、向sql语句传参数麻烦，因为sql语句的where条件不一定，可能多也可能少，占位符需要和参数一一对应。
    解决：Mybatis自动将java对象映射至sql语句，通过statement中的parameterType定义输入参数的类型。
    4、对结果集解析麻烦，sql变化导致解析代码变化，且解析前需要遍历，如果能将数据库记录封装成pojo对象解析比较方便。
    解决：Mybatis自动将sql执行结果映射至java对象，通过statement中的resultType定义输出结果的类型。

  mybatis与hibernate不同
    Mybatis和hibernate不同，它不完全是一个ORM框架，因为MyBatis需要程序员自己编写Sql语句。mybatis可以通过XML或注解方式灵活配置要运行的sql语句，并将java对象和sql语句映射生成最终执行的sql，最后将sql执行的结果再映射生成java对象。

    Mybatis学习门槛低，简单易学，程序员直接编写原生态sql，可严格控制sql执行性能，灵活度高，非常适合对关系数据模型要求不高的软件开发，例如互联网软件、企业运营类软件等，因为这类软件需求变化频繁，一但需求变化要求成果输出迅速。但是灵活的前提是mybatis无法做到数据库无关性，如果需要实现支持多种数据库的软件则需要自定义多套sql映射文件，工作量大。
    
    Hibernate对象/关系映射能力强，数据库无关性好，对于关系模型要求高的软件（例如需求固定的定制化软件）如果用hibernate开发可以节省很多代码，提高效率。但是Hibernate的学习门槛高，要精通门槛更高，而且怎么设计O/R映射，在性能和对象模型之间如何权衡，以及怎样用好Hibernate需要具有很强的经验和能力才行。
    总之，按照用户的需求在有限的资源环境下只要能做出维护性、扩展性良好的软件架构都是好架构，所以框架只有适合才是最好。
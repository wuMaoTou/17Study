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

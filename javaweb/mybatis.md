1.Mybatis����
  ����
  MyBatis��һ������ĳ־ò��ܣ�����jdbc�Ĳ������ݿ�Ĺ��̽��з�װ,ʹ������ֻ��Ҫ��ע SQL ����

  ����ԭ��
  Mybatisͨ��xml��ע��ķ�ʽ��Ҫִ�еĸ���statement��statement��preparedStatemnt��CallableStatement��������������ͨ��java�����statement�е�sql����ӳ����������ִ�е�sql��䣬�����mybatis���ִ��sql�������ӳ���java���󲢷���

2.Mybatis�ܹ�
  1.mybatis����
    `SqlMapConfig.xml`�ļ���Ϊmybatis��ȫ�������ļ�,������mybatis�����л�������Ϣ
    `mapper.xml`�ļ�Ϊsqlӳ���ļ�,�ļ��������˲������ݿ��sql���,���ļ���Ҫ��SqlMapConfig.xml�м���

  2.ͨ��������Ϣ����`SqlSessionFactory`�Ự����

  3.�ɻỰ��������`sqlSession`�Ự,�������ݿ���Ҫͨ��`sqlSession`����

  4.mybatis�ײ��Զ�����Executorִ�����ӿڲ������ݿ⣬Executor�ӿ�������ʵ�֣�һ���ǻ���ִ������һ���ǻ���ִ����

  5.Mapped StatementҲ��mybatisһ���ײ��װ��������װ��mybatis������Ϣ��sqlӳ����Ϣ�ȡ�mapper.xml�ļ���һ��sql��Ӧһ��Mapped Statement����sql��id����Mapped statement��id

  6.Mapped Statement��sqlִ������������ж��壬����HashMap���������͡�pojo��Executorͨ��Mapped Statement��ִ��sqlǰ�������java����ӳ����sql�У��������ӳ�����jdbc����ж�preparedStatement���ò���

  7.Mapped Statement��sqlִ�����������ж��壬����HashMap���������͡�pojo��Executorͨ��Mapped Statement��ִ��sql��������ӳ����java�����У�������ӳ������൱��jdbc����жԽ���Ľ����������

3.Mybatis���ų���
  1.maven��������
  	<dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
	    //��Ӧ��װ��mysql�汾
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
  2.��resources�¼��������ļ�logj.properties��mybatis-config.xml
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
  3.����pojo,pojo����Ϊmybatis����sqlӳ��ʹ��,ͨ�������ݿ���Ӧ
    ����������
    	`DROP TABLE IF EXISTS `user`;
         CREATE TABLE `user` (
           `id` int(11) NOT NULL AUTO_INCREMENT,
           `username` varchar(32) NOT NULL COMMENT '�û�����',
           `birthday` date DEFAULT NULL COMMENT '����',
           `sex` char(1) DEFAULT NULL COMMENT '�Ա�',
           `address` varchar(256) DEFAULT NULL COMMENT '��ַ',
           PRIMARY KEY (`id`)
         ) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;`
    ����pojo
    	`Public class User {
	private int id;
	private String username;// �û�����
	private String sex;// �Ա�
	private Date birthday;// ����
	private String address;// ��ַ

	get/set����}`
  4.����sqlӳ���ļ�
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
  5.��mybatis����ӳ���ļ�·��
  	`<mappers>
	     <mapper resource="mappers/user.xml"/>
	 </mappers>`
  6.����
  	`public class MyBatisTest {

	    private SqlSessionFactory sessionFactory;

	    @Before
	    public void init() throws Exception {
	        //1.���������ļ�
	        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
		//2.����SqlSessionFactory����
	        sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
	    }

	    @Test
	    public void test () throws Exception {
	        //3.����SqlSeesion����
	        SqlSession sqlSession = sessionFactory.openSession();
		//4.SqlSession����ִ�в�ѯ
	        Object user = sqlSession.selectOne("queryUserById", 1);
	        System.out.println(user);
		//5.�ͷ���Դ
	        sqlSession.close();
	    }
	}`

4.Mybatis�ص�
  Mybatis���jdbc��̵�����
    1�����ݿ����Ӵ������ͷ�Ƶ�����ϵͳ��Դ�˷ѴӶ�Ӱ��ϵͳ���ܣ����ʹ�����ݿ����ӳؿɽ�������⡣
    �������SqlMapConfig.xml�������������ӳأ�ʹ�����ӳع������ݿ����ӡ�
    2��Sql���д�ڴ�������ɴ��벻��ά����ʵ��Ӧ��sql�仯�Ŀ��ܽϴ�sql�䶯��Ҫ�ı�java���롣
    �������Sql���������XXXXmapper.xml�ļ�����java������롣
    3����sql��䴫�����鷳����Ϊsql����where������һ�������ܶ�Ҳ�����٣�ռλ����Ҫ�Ͳ���һһ��Ӧ��
    �����Mybatis�Զ���java����ӳ����sql��䣬ͨ��statement�е�parameterType����������������͡�
    4���Խ���������鷳��sql�仯���½�������仯���ҽ���ǰ��Ҫ����������ܽ����ݿ��¼��װ��pojo��������ȽϷ��㡣
    �����Mybatis�Զ���sqlִ�н��ӳ����java����ͨ��statement�е�resultType���������������͡�

  mybatis��hibernate��ͬ
    Mybatis��hibernate��ͬ��������ȫ��һ��ORM��ܣ���ΪMyBatis��Ҫ����Ա�Լ���дSql��䡣mybatis����ͨ��XML��ע�ⷽʽ�������Ҫ���е�sql��䣬����java�����sql���ӳ����������ִ�е�sql�����sqlִ�еĽ����ӳ������java����

    Mybatisѧϰ�ż��ͣ�����ѧ������Աֱ�ӱ�дԭ��̬sql�����ϸ����sqlִ�����ܣ����ȸߣ��ǳ��ʺ϶Թ�ϵ����ģ��Ҫ�󲻸ߵ�������������绥�����������ҵ��Ӫ������ȣ���Ϊ�����������仯Ƶ����һ������仯Ҫ��ɹ����Ѹ�١���������ǰ����mybatis�޷��������ݿ��޹��ԣ������Ҫʵ��֧�ֶ������ݿ���������Ҫ�Զ������sqlӳ���ļ�����������
    
    Hibernate����/��ϵӳ������ǿ�����ݿ��޹��Ժã����ڹ�ϵģ��Ҫ��ߵ��������������̶��Ķ��ƻ�����������hibernate�������Խ�ʡ�ܶ���룬���Ч�ʡ�����Hibernate��ѧϰ�ż��ߣ�Ҫ��ͨ�ż����ߣ�������ô���O/Rӳ�䣬�����ܺͶ���ģ��֮�����Ȩ�⣬�Լ������ú�Hibernate��Ҫ���к�ǿ�ľ�����������С�
    ��֮�������û������������޵���Դ������ֻҪ������ά���ԡ���չ�����õ�����ܹ����Ǻüܹ������Կ��ֻ���ʺϲ�����á�
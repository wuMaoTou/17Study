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

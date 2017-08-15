##GreenDao - Android ORM SQLite数据库
greenDAO是一个开源的Android ORM框架,它使得SQLite数据库开发成为一种乐趣,节省了开发人员处理底层数据库的开发时间。SQLite是一个嵌入式数据库。编写SQL和解析查询结果是相当繁琐和耗时的事情,greenDAO通过将Java对象映射到数据库表(称为ORM对象/关系映射)解决了这些问题。这样你就可以通过Java对象使用一个简单的面向对象的API来进行存储、更新、删除和查询等操作。

###greenDAO的特性
* 性能最大化(可能是Android中最快的ORM)
* 易于使用,强大的api覆盖关系和连接
* 内存占用最小化
* 依赖包小于100KB,减少了编译时间和避免65k方法限制
* 数据库加密,支持SQL加密保持你的数据安全
* 强大的社区:超过5.000 GitHub starts,说明有一个强大和活跃的社区

###greenDAO的使用

####greenDAO添加到你的项目
```
// 你的项目根目录 build.gradle 文件:
buildscript {
    repositories {
        jcenter()
        mavenCentral() // add repository
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.3.1'
        classpath 'org.greenrobot:greendao-gradle-plugin:3.2.2' // add plugin
    }
}
 
// 你的app Model build.gradle 文件:
apply plugin: 'com.android.application'
apply plugin: 'org.greenrobot.greendao' // apply plugin
 
dependencies {
    compile 'org.greenrobot:greendao:3.2.2' // add library
}

```
注:如果使用代码混淆需添加去混淆规则
```
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use Rx:
-dontwarn rx.**
```

####实体模型
在项目中使用greenDAO,创建一个实体模型表示应用程序中的持久数据。然后,基于这个模型greenDAO构建插件会生成该模型对应的DAO类。

**Schema**
greenDAO的使用基本没有任何额外的配置。但是,在项目中你至少应该考虑设置数据库的版本
```
// 你的app Model里的 build.gradle 文件:
android {
...
}
greendao{
	schemaVersion 1	//指定数据库schema版本好,迁移等操作会用到
}
```
此外,greendao配置元素支持的配置选项:

* schemaVersion:数据库当前版本。使用在*`OpenHelpers`类数据库版本之间的迁移。如果你改变实体/数据库模式,这个值增加。默认为1。
*  daoPackage:生成的dao类的包名,DaoMaster和DaoSession默认生成在与实体同级的目录下。
* targetGenDir:dao类生成后存储的路径。默认生成在构建目录(build/generated/source / greendao)。
* generateTests:设置为true来自动生成单元测试
* targetGenDirTests:指定生成的单元测试代码存储位置。默认为src / androidTest / java。

**实体类和注解**
greenDAO 3使用注解来定义模式和实体
```
@Entity
public class User {
    @Id
    private Long id;
 
    private String name;
 
    @Transient
    private int tempUsageCount; // not persisted
 
   // getters and setters for id and user ...
}
```
@ entity注释的Java类`user`转化为一个数据库支持的实体。同时也会生成必要的代码(例如DAOs)。
注:仅支持Java类。如果你使用的是Kotlin,你的实体类必须仍然是Java。
同时还可以对@Entity进行详细的配置

** @Entitiy注解 **
```
@Entity(
        // 如果你有多个数据库,你可以告诉greenDao这个实体属于哪个数据库
        schema = "myschema",
        
        // 标记这个实体为活跃的,活跃的实体会有update,delete和refresh等方法
        active = true,
        
        // 设置该实体在数据库中的表名称,默认是该实体的类名
        nameInDb = "AWESOME_USERS",
        
        // 定义索引生成多个列
        indexes = {
                @Index(value = "name DESC", unique = true)
        },
        
        // 指定数据库是否创建该表(默认为true),
        // 设置为false,如果你有多个实体映射到一个表,或者这个表不需要greenDao创建
        createInDb = false,
 
        // 指定是否生成所有属性的构造函数,默认总会生产无参构造
        generateConstructors = true,
 
        // 如果没有get/set方法,是否自动生成
        generateGettersSetters = true
)
public class User {
  ...
}
```
主:目前还不支持生成多个数据库

**基础参数**
```
@Entity
public class User {
    @Id(autoincrement = true)
    private Long id;
 
    @Property(nameInDb = "USERNAME")
    private String name;
 
    @NotNull
    private int repos;
 
    @Transient
    private int tempUsageCount;
 
    ...
}
```

* @Id:选择long/Long属性作为实体的ID。在数据库表里它是主键.autoincrement标记的ID值自动增长。
* @Property:设置一个非默认关系映射所对应的列名,默认使用字段名称的大写驼峰格式
* @NotNull:设置数据库表当前列的数据不能为空
* ＠Transient：添加此标记后不会生成数据库表的列

**主键限制**
目前,实体必须有一个long/Long属性作为主键，这是 Androidhe 和SQL推荐的写法，为了解决这一问题,定义你的关键属性作为一个附加属性,但为它创建一个惟一的索引:
```
@Id
private Long id;
 
@Index(unique = true)
private String key;
```
**索引属性**
使用@Index属性来创建一个数据库索引对应的数据库列。使用以下参数来定制:

* name:如果你不喜欢默认的名字greenDAO生成索引,你可以在这里指定的
* unique:给索引添加唯一的约束,让该列的值不重复
```
@Entity
public class User {
    @Id private Long id;
    @Index(unique = true)
    private String name;
}
```
**默认**
greenDao希望使用合理的默认值,因此一般使用不需要过多的配置

###关联
数据库表和实体类可能会是1:1,1:N,或者N:M关系。
**1对1的模型关联**
@ToOne定义与另一个实体(实体对象)的关系。
在内部,greenDAO需要`joinProperty`指向目标实体的ID。如果没添加这个参数,那么会自动创建一个自动增长的主键。
```
@Entity
public class Order {
    @Id private Long id;
 
    private long customerId;
 
    @ToOne(joinProperty = "customerId")
    private Customer customer;
}
 
@Entity
public class Customer {
    @Id private Long id;
}
```
关联的getter方法(在本例getCustomer())生成目标实体在第一次调用时才被加载。后续调用会立即返回之前生成的对象。
注:如果更改外键属性(这里customerId),再次调用getter(getCustomer())时获取实体时会是更新后的ID。还有,如果你创造了一个新的实体(setCustomer()),外键属性(customerId)也会被更新
注:如果急切的加载1对1的关联使用实体dao类的`loadDeep()`和`queryDeep()`,这可以解决一个实体的关联是单一的数据库查询,如果这个实体操作和频繁,这将有效的提高性能

**1对多的模型关联**
@ToMany：定义与多个实体对象的关系
有三种可能性指定映射的关系,只使用其中一种:

* referencedJoinProperty:指定的名称“外键”属性在目标实体指向这个实体的id。
```
@Entity
public class Customer {
    @Id private Long id;
 
    @ToMany(referencedJoinProperty = "customerId")
    @OrderBy("date ASC")
    private List<Order> orders;
}
 
@Entity
public class Order {
    @Id private Long id;
    private Date date;
    private long customerId;
}
```
* joinProperties:对于更复杂的关系可以指定@JoinProperty注释列表。每个@JoinProperty需要源属性在原始的实体和属性在目标实体引用。
```
@Entity
public class Customer {
    @Id private Long id;
    @Unique private String tag;
 
    @ToMany(joinProperties = {
            @JoinProperty(name = "tag", referencedName = "customerTag")
    })
    @OrderBy("date ASC")
    private List<Site> orders;
}
 
@Entity
public class Order {
    @Id private Long id;
    private Date date;
    @NotNull private String customerTag;
}
```
* @JoinEntity:添加这个附加注释你的属性,如果你正在做一个N:M(多对多)的关联涉及到另一个连接实体/表。
```
@Entity
public class Product {
    @Id private Long id;
 
    @ToMany
    @JoinEntity(
            entity = JoinProductsWithOrders.class,
            sourceProperty = "productId",
            targetProperty = "orderId"
    )
    private List<Order> ordersWithThisProduct;
}
 
@Entity
public class JoinProductsWithOrders {
    @Id private Long id;
    private Long productId;
    private Long orderId;
}
 
@Entity
public class Order {
    @Id private Long id;
}
```

注：定义实体类的时，不需要添加get/set方法。只需方法名和相关注解,编译后会自动生成get/set方法

####在Application初始化
```
public class App extends BaseApplications {

    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper o2o = new DaoMaster.DevOpenHelper(this, "o2o");
        Database db = o2o.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession(){
        return daoSession;
    }
}
```
####dao的使用
```
UserDao userDao = App.getDaoSession().getUserDao();
                User user = new User();
                user.setAge(22);
                user.setName("小米");
                userDao.insert(user);
```




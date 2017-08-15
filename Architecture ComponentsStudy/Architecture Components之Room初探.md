##Room的初衷

提起SQLite，作为Android开发者还是比较幸福的的，android核心框架已为处理SQL提供了相当大的支持，API也非常强大，省起来很大的力气。但是其模板化处理方式，导致开发者花费大量的时间和精力去维护数据库：

在编译时，没有对原始SQL查询语句验证。随着表结构的更改，需要手动更新SQL查询语句。这个过程不仅耗时耗精力，而且很容易出错。
需要使用大量的样板代码执行SQL操作和Java数据对象之间的转换。
正因为这些原因，一批大神造轮子，开源了很多优秀的开源框架，比如GreenDao、OrmLite、Active Android等等，给我们带来了十分的便利。 
这里给大家推荐另外一个开源框架 - Room，其作者是Android的爹Google。既然是Google出品，我想有必要学习一下。

####Room给我们带来了什么样的惊喜呢？
1.避免了样板间似的代码块。
2.能轻松的将SQLite表数据转换为Java对象
3.Room提供了编译SQLite语句时检查，避免了SQL语句在执行时，才发现错误
4.可以返回RxJava的Flowable和LiveData的可观察实例，对SQLite的异步操作提供强力支持。

**Gradle配置**

在buildl.gradle里面添加依赖即可：
```
compile “android.arch.persistence.room:runtime:1.0.0-alpha1”
annotationProcessor “android.arch.persistence.room:compiler:1.0.0-alpha1”
```
Room对RxJava 2是完美支持， 如果习惯了RxJava异步操作的，可以添加RxJava支持库：
```
compile “android.arch.persistence.room:rxjava2:1.0.0-alpha1”
```

####Room的三大组成部分

* Database(数据库)：使用此组件创建数据库Holder。 
	* 通过注解实体类定义表结构，该实体类的实例即为数据库中的数据访问对象(在Dao内操作)。
	* 它是链接SQL底层的主要接入点。
	* 注解的类必须是继承于RoomDatabase的抽象类
	* 在运行时，可以通过调用Room.databaseBuilder()或Room.inMemoryDatabaseBuilder()获取其实例
* Entity(实体类)：该组件表示持有一个表的字段（即数据库一行的数据）的实体类。 
	* 对于每个实体，创建一个数据库表来保存它们。
	* 必须通过Database类中的Entity数组引用实体类
	* 实体类的每个字段都会保存数据库的表中。如果不想保存某字段，该字段需使用@Ignore注解 
>如果Dao类可以访问每个持久化的字段(即表中的字段)，实体可以有一个空构造函数。当然，该实体类还可以有一个构造函数，其参数应包含与实体中的字段相匹配的类型和名称。Room可以使用含有全部字段或者部分字段的构造函数，例如只含有部分字段的构造函数？？？？？？

* DAO(抽象类/接口)：该组件表示作为数据访问对象(DAO即为Data Access Object的简写)的类或者接口。 
	* DAO是Room的主要组件，负责定义访问数据库的方法。
	* 该抽象类或接口使用@Database注解，同时必须含有一个无参数的抽象方法，并返回@Dao注解的实际操作类。
	* 在编译时，Room创建这个抽象类或接口的实现。 
>注意：通过使用DAO类访问数据库，而不是使用查询构建器或直接查询，可以将数据库体系结构的不同组件分离。另外，在测试应用程序时，DAO可以轻松的模拟数据库访问。

###Room与应用程序的架构体系

**简单使用**

现在已经对Room库有了初步的认识，下面我们来看看Room库在应用程序中，怎么应用的呢？

1.创建实体类UserEntity，使用@Entity注解，将其作为数据库中的一个表的实体
UserEntity.java
```
@Entity(tablename="user")
public class UserEntity {
    @PrimaryKey
    private int uid;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.
}
```

2.创建数据库访问对象(DAO)， 用于访问数据库
UserDao.java
```
@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND "
           + "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);

    @Insert
    void insertAll(User... users);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)//替换插入
    void insert(User users);

    @Delete
    void delete(User user);
}
```
3.声明抽象类AppDatabase，继承于RoomDatabase，该类使用@Database注解，用于为应用程序创建一个数据库,在AppDatabase引用UserDao,就是在AppDatabase中声明一个抽象方法，返回UserDao实例
AppDatabase.java
```
@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
```
**请注意:**
	a.在Kotlin中，entities注解参数为vararg参数传递时，必须将参数的显示的的声明为arrayOf()。
	b.在编译时，由Room库对AppDatabase实现，我们不需多做处理。 

4.创建数据库，像数据库添加数据后，并查询。
```
class RoomActivity : BaseActivity() {

    ***

    @SuppressLint("StaticFieldLeak")
    override fun setListener() {
        acb_create.setOnClickListener {
            doAsync {
                applicationContext.deleteDatabase(DATABASE_NAME)

                mDataBase = Room.databaseBuilder(applicationContext,
                        AppDatabase::class.java, DATABASE_NAME).build()
            }
        }

        acb_insert.setOnClickListener {
            doAsync {
                val users: MutableList<UserEntity> = mutableListOf()

                (0..10).mapTo(users) {
                    val user = UserEntity(id, "Test - it", id % 2)
                    id++
                    user
                }

                mDataBase?.beginTransaction()

                try {
                    mDataBase?.userDao()?.insertUser(users)
                    mDataBase?.setTransactionSuccessful()
                } finally {
                    mDataBase?.endTransaction()
                }
            }
        }

        ***

        acb_query.setOnClickListener {
            var users: List<UserEntity>? = null
            doAsync {
                mDataBase?.beginTransaction()

                try {
                    users = mDataBase?.userDao()?.queryAll()
                    Log.i("123", users?.toString())
                    mDataBase?.setTransactionSuccessful()
                } finally {
                    mDataBase?.endTransaction()
                }

                runOnUiThread {
                    mList.clear()
                    mList.addAll(mList.size, users!!.toList())
                    mAdapterUser.notifyDataSetChanged()
                }
            }


        }
    }
}
```
**请注意:**
在Kotlin中，Room库对数据库的操作必须在后台线程中执行。如果在UI主线程中操作数据库，将会报错。因为Room认为操作数据库是耗时操作，所以为了操作数据库而阻塞UI线程，Room默认这种行为是禁止的，故而报错。如果想开启在UI线程中访问数据库，可以再构建AppDatabase实例是设置禁用Room检查在主线程查询数据库。，但是不推荐，也就是这样，：
```
  mDataBase = Room.databaseBuilder(applicationContext,
                    AppDatabase::class.java, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build()             
```

###Entities
当一个类被@Entity注解并且引用的实体属性是被@Database注解的,Room会为该实体在数据库中创建一个数据库表.
默认情况下Room会为每一个字段创建一个列中定义的实体,如果实体中有字段不想持久到数据库中,可以使用@Ignore注解,如:
```
@Entity
class User {
    @PrimaryKey
    public int id;

    public String firstName;
    public String lastName;

    @Ignore
    Bitmap picture;
}
```
存留一个字段,Room必须能够访问它,你可以把该字段公开用public修饰,或者使用provite修饰,再赋予get/set方法,如果你使用get/set方法,在Room必须基于JavaBean约定
**Primary key**
每个实体都必须至少有一个主键,即使只有一个字段,你仍然需要用@PrimaryKey注解,同样,如果你需要Room自动分配实体的_Id,你可以设置@PrimaryKeys自动构建属性,如果实体有复合主键,可以使用@Entity注解的primaryKeys属性,如:
```
@Entity(primaryKeys = {"firstName", "lastName"})
class User {
    public String firstName;
    public String lastName;

    @Ignore
    Bitmap picture;
}
```
默认情况下,Room使用类名作为数据库表名称,你也可以使用@Entity注解的tableName属性设置表名称,如:
```
@Entity(tableName = "users")
class User {
    ...
}
```
同样的,Room也会使用字段名作为数据库中的列名,如果需要指定不同名称,使用@ColumnInfo(name = "")注解修改列名,如:
```
@Entity(tableName = "users")
class User {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    @Ignore
    Bitmap picture;
}
```
**索引和唯一性**

这取决于你如何访问数据,你可能需要索引数据库中的某些字段加快查询速度将索引添加到实体中,可以将索引和综合索引列表清单插入到@Entitiy注解,如:
```
@Entity(indices = {@Index("name"), @Index("last_name", "address")})
class User {
    @PrimaryKey
    public int id;

    public String firstName;
    public String address;

    @ColumnInfo(name = "last_name")
    public String lastName;

    @Ignore
    Bitmap picture;
}
```

有时候,某些字段获取字段组在数据库中必须是唯一的,可以使用@Index注解的属性,通过unique的属性为true,如:
```
@Entity(indices = {@Index(value = {"first_name", "last_name"},
        unique = true)})//表示防止表中有两个行包含`first_name`和`last_name`相同的一组值
class User {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    @Ignore
    Bitmap picture;
}

```

**关联**
因为数据库是一个关联数据库,你可以指定对象之间的关系.尽管大多数ORM库允许实体对象相互关联,但在Room是不允许的
即使您不能使用直接关系，Room仍允许您在实体之间定义Foreign Key约束。
例如,有一个实体Book,你可以使用@ForeignKey注解来定义与User实体的关系,如:
```
@Entity(foreignKeys = @ForeignKey(entity = User.class,
                                  parentColumns = "id",
                                  childColumns = "user_id"))
class Book {
    @PrimaryKey
    public int bookId;

    public String title;

    @ColumnInfo(name = "user_id")
    public int userId;
}
```
外键很强大,因为它们允许您指定引用实体更新时发生的情况,例如,如果通过在@ForeignKey注解中包含onDelete=CASCADE来删除用户的相应实体,则可以让SQLite删除用户的所以图书
**注意:**SQLite将@Insert(onConflict=REPLACE)作为一组REMOVE和REPLACE操作而不是单个UPDATE操作来处理,这种替换冲突值的方法可能会影响你的外键约束

**嵌套对象**
有时候，您希望在数据库逻辑中表达一个实体或普通的Java对象（PO​​JO），即使对象包含多个字段。 在这些情况下，您可以使用`@Embedded`注解来表示您要在表中分解为其子字段的对象。 然后，您可以像其他单独的列一样查询嵌入的字段。
例如，我们的User类可以包括一个Address类型的字段，它表示一个名为street ， city ， state和postCode的字段的组合。 要在表格中分别存储组合列，请在User类中包含一个用@Embedded注释的Address字段，如下面的代码片段所示：
```
class Address {
    public String street;
    public String state;
    public String city;

    @ColumnInfo(name = "post_code")
    public int postCode;
}

@Entity
class User {
    @PrimaryKey
    public int id;

    public String firstName;

    @Embedded
    public Address address;
}
```
表示User对象的表包含以下名称的列： id ， firstName ， street ， state ， city和post_code 。
**注意:**嵌入字段也可以包括其他嵌入字段。
如果实体具有多个相同类型的嵌入字段，则可以通过设置prefix属性来保留每个列的唯一性。 Room会将提供的值添加到嵌入对象中的每个列名的开头。

###数据访问对象（DAO）
Room的主要组成部分是Dao。 DAOs以干净的方式抽象地访问数据库。
**注意:**Room不允许在主线程上访问数据库,除非你在构建器上调用了allowMainThreadQueries(),因为它可能会长时间的锁死UI线程,异步查询(返回LiveData或RxJava Flowable的查询)将避免这种情况,因为访问数据库需要在后台线程上异步查询

####操作数据库

前面，我们已经了解到了Room的基本使用，下面我们来看看对数据库操作的细节。对于数据库的操作，莫过于“增删改查”，那我们从这些操作来深入了解Room的应用。

**增(@Insert)**

向数据库中添加数据，使用到了@Insert注解：

1.将@Dao注解类中的方法标记为插入方法。
2.该方法的实现将其参数插入到数据库中
3.@Insert注解方法的所有参数必须是使用@Entity注解的类或其集合/数组
例如：
```
@Dao 
public interface MyDao { 

	@Insert(onConflict = OnConflictStrategy.REPLACE) 
	public void insertUsers(User... users); 
	
	@Insert 
	public void insertBothUsers(User user1, User user2); 
	
	@Insert
	 public void insertUsersAndFriends(User user, List<User> friends);
	 
 } 
```
如果@Insert方法只接收一个参数，它可以返回一个long ，这是插入项的新rowId 。 如果参数是数组或集合，它应该返回long[]或List<Long> 。

对数据库设计时，不允许重复数据的出现。否则，必然造成大量的冗余数据。实际上，难免会碰到这个问题：冲突。当我们像数据库插入数据时，该数据已经存在了，必然造成了冲突。该冲突该怎么处理呢？在@Insert注解中有conflict用于解决插入数据冲突的问题，其默认值为OnConflictStrategy.ABORT。对于OnConflictStrategy而言，它封装了Room解决冲突的相关策略。对于冲突不熟悉的，可以参考SQL As Understood By SQLite.

1.OnConflictStrategy.REPLACE：冲突策略是取代旧数据同时继续事务
2.OnConflictStrategy.ROLLBACK：冲突策略是回滚事务
3.OnConflictStrategy.ABORT：冲突策略是终止事务
4.OnConflictStrategy.FAIL：冲突策略是事务失败
5.OnConflictStrategy.IGNORE：冲突策略是忽略冲突

当遇到冲突时，Room的默认的处理方式为终止事务。有这样一个需求，User表中的name索引是唯一的。将User实例插入表时，如果出现冲突，用新数据取代旧数据。那么，我们需要做以下工作：

1.修改UserEntity类，把name字段标记为唯一的索引；
```
@Entity(tableName = "user", indices = {@Index(value ={"name"}, unique = true)})
    public class UserEntity (val name: String,val isBrrowed: Int) {

   public UserEntity(String name, int isBrrowed){
	
   }
    @PrimaryKey(autoGenerate = true)
    public int id;
}
```
2.修改插入方法的@Insert注解，将其onConflict属性设置为OnConflictStrategy.REPLACE
```
@Dao
interface UserDao {
    // 向表中插入一系列
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUser(UserEntity user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUser(List<UserEntity> users);
}
```
至于，如何设置数据库表中的字段或字段组的唯一性，后续再做讲解。

其他的冲突解决策略，这里不多做说明，有兴趣的可以尝试设计场景实现。

**删@Delete**

@Delete注解的方法，将从数据库表中删除与所接收的实体参数所对应的数据，以每个实体的主键作为查询对应关系的依据。 如下所示：
```
@Dao
interface UserDao {

    // 删除表中的数据
    @Delete
    public int deleteUser(UserEntity user);

    @Delete
    public int deleteUser(List<UserEntity> users);
}
```
在此方法中， 可以返回一个int值， 表示数据库中删除的行数。

**改@Update**

@Update注解的方法， 以接收到一组实体更新数据库表中的数据， 它使用每个实体的主键作为查询的依据。 如下所示
```
@Dao
interface UserDao {
    // 更新表中一系列数据
    @Update
     public int updateUser(UserEntity user);

    @Update
     public int updateUser( List<UserEntity> users);
}
```
在此方法中， 可以返回一个int值， 表示数据库中更新的行数。

**查@Query**
@Query是DAO类中使用的主要注释。 它允许您对数据库执行读/写操作。 每个@Query方法都在编译时进行验证，因此如果查询有问题，则会发生编译错误而不是运行时报异常。

Room还会验证查询的返回值，以便如果返回对象中的字段名称与查询响应中相应的列名称不匹配，则房间将以以下两种方式之一提醒您：

* 如果只有一些字段名匹配，则会发出警告。
* 如果没有字段名称匹配，则会给出错误。

1.简单的查询
```
@Dao 
public interface MyDao { 
	@Query("SELECT * FROM user") 
	public User[] loadAllUsers(); 
}
```
这是一个非常简单的查询，加载所有用户。 在编译时，Room知道它正在查询用户表中的所有列。 如果查询包含语法错误，或者如果用户表不存在于数据库中，则在应用程序编译时，Room会显示相应消息的错误。

2.将参数传递到查询中
大多数情况下，您需要将参数传递给查询以执行过滤操作，例如只显示年龄以上的用户。 要完成此任务，请在Room注释中使用方法参数
```
 @Dao 
 public interface MyDao { 
 	@Query("SELECT * FROM user WHERE age > :minAge") 
 	public User[] loadAllUsersOlderThan(int minAge);
  } 
```
当在编译时处理此查询时，Room将:minAge绑定参数与minAge方法参数相匹配。 Room使用参数名称执行匹配。 如果不匹配，您的应用程序编译时会发生错误。
您还可以在查询中传递多个参数或多次引用它们，如:
```
@Dao 
public interface MyDao { 

	@Query("SELECT * FROM user WHERE age BETWEEN :minAge AND :maxAge")
 	public User[] loadAllUsersBetweenAges(int minAge, int maxAge); 
 
	 @Query("SELECT * FROM user WHERE first_name LIKE :search  OR last_name LIKE :search") 
	 public List<User> findUserWithName(String search); 
 } 
```
3.返回列的子集
大多数时候，你只需要获得一个实体的几个字段。 例如，您的UI可能仅显示用户的名字和姓氏，而不是每个用户的详细信息。 通过仅获取应用程序UI中显示的列，您可以节省宝贵的资源，并且您的查询更快速地完成。
只要可以将结果列的集合映射到返回的对象中，您可以从查询返回任何Java对象。 例如，您可以创建以下POJO来获取用户的名字和姓氏：
```
public class NameTuple { 
@ColumnInfo(name="first_name") 
public String firstName; 

@ColumnInfo(name="last_name") 
public String lastName; 
} 
```
现在，您可以在查询方法中使用此POJO：
```
@Dao 
public interface MyDao { 
	@Query("SELECT first_name, last_name FROM user") 
	public List<NameTuple> loadFullName(); 
} 
```
Room了解查询返回first_name和last_name列的值，并将这些值映射到NameTuple类的字段。 因此，Room可以生成正确的代码。 如果查询返回太多列或NameTuple类中不存在的列，则Room会显示警告。

4.传递一组参数
您的一些查询可能需要您传递可变数量的参数，直到运行时才知道参数的确切数量。 例如，您可能希望从区域的子集中检索有关所有用户的信息。 空间了解参数何时代表一个集合，并根据提供的参数数量在运行时自动扩展它。
```
 @Dao 
 public interface MyDao { 
 @Query("SELECT first_name, last_name FROM user WHERE region IN (:regions)") 
 public List<NameTuple> loadUsersFromRegions(List<String> regions);
  } 
```

5.可观察的查询
执行查询时，您经常希望应用程序的UI在数据更改时自动更新。 要实现这一点，在查询方法描述中使用类型为LiveData的返回值。 当数据库更新时，房间将生成所有必需的代码来更新LiveData 。
```
@Dao 
public interface MyDao { 
@Query("SELECT first_name, last_name FROM user WHERE region IN (:regions)") public LiveData<List<User>> loadUsersFromRegionsSync(List<String> regions); 
} 
```
**注意：** 从版本1.0起，Room使用查询中访问的表列表来决定是否更新LiveData对象。

6.RxJava
Room还可以从您定义的查询中返回RxJava2 Publisher和Flowable对象。 
使用RxJava需要添加Room组依赖
```
compile “android.arch.persistence.room:rxjava2:1.0.0-alpha1”
```
 然后，您可以返回在RxJava2中定义的类型的对象，如:
 ```
 @Dao 
 public interface MyDao { 
 	@Query("SELECT * from user where id = :id LIMIT 1") 
 	public Flowable<User> loadUserById(int id);
  } 
 ```

7.直接访问Cursor
如果您的应用程序的逻辑需要直接访问返回行，则可以从查询中返回一个Cursor对象，如:
```
@Dao 
public interface MyDao { 
@Query("SELECT * FROM user WHERE age > :minAge LIMIT 5") 
public Cursor loadRawUsersOlderThan(int minAge); 
} 
```
**注意：**不赞成使用Cursor API，因为它不能保证行是否存在，或者行包含什么值。 只有当您已经具有期望光标的代码并且不能轻易重构时，才能使用此功能。

8.查询多个表
您的某些查询可能需要访问多个表才能计算结果。 房间允许你写任何查询，所以你也可以加入表。 此外，如果响应是可观察的数据类型，例如LiveData或LiveData ，则Room会LiveData查询中引用的无效的所有表。

以下代码片段显示了如何执行表连接以整合包含借阅书的用户的表和包含目前借出的图书数据的表格之间的信息：
```
@Dao 
public interface MyDao { 
@Query("SELECT * FROM book " + "INNER JOIN loan ON loan.book_id = book.id " + "INNER JOIN user ON user.id = loan.user_id " + "WHERE user.name LIKE :userName") 
public List<Book> findBooksBorrowedByNameSync(String userName);
 } 
```
您也可以从这些查询返回POJO。 例如，您可以编写一个加载用户的查询及其宠物的名称，如下所示：
```
@Dao 
public interface MyDao { 
	@Query("SELECT user.name AS userName, pet.name AS petName "
          + "FROM user, pet "
          + "WHERE user.id = pet.user_id")
   	public LiveData<List<UserPet>> loadUserAndPetNames();
	
	// You can also define this class in a separate file, as long as you add the 
/	/ "public" access modifier. 
	static class UserPet { 
		public String userName; 
		public String petName;
	 } 
 } 
```

###使用类型转换器
Room内置了对原始图案和盒装替代品的支持。 但是，有时您需要使用要在单个列中将数据存储在其中的值的自定义数据类型。 要为自定义类型添加这种支持，您可以提供一个TypeConverter ，它可以将一个自定义类转换为Room已知类型。
例如，如果我们要保留Date实例，我们可以写下面的TypeConverter来存储数据库中的等效的Unix时间戳记：
```
public class Converters { 
	@TypeConverter 
	public static Date fromTimestamp(Long value) {
 	return value == null ? null : new Date(value); 
	 } 
 
 	@TypeConverter 
 	public static Long dateToTimestamp(Date date) { 
	 return date == null ? null : date.getTime();
 	 } 
  } 
```
上述示例定义了两个函数，一个将Date对象转换为Long对象，另一个将执行逆转换，从Long到Date 。 由于Room已经知道如何保存Long对象，所以可以使用此转换器来持久保存Date类型的值。
接下来，您将@TypeConverters注释添加到AppDatabase类，以便Room可以使用您为该AppDatabase每个实体和DAO定义的转换器：
AppDatabase.java
```
@Database(entities = {User.java}, version = 1)
@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
```
使用这些转换器，您可以在其他查​​询中使用自定义类型，就像使用原始类型一样，如以下代码片段所示：
User.java
```
@Entity
public class User {
    ...
    private Date birthday;
}
```
UserDao.java
```
@Dao
public interface UserDao {
    ...
    @Query("SELECT * FROM user WHERE birthday BETWEEN :from AND :to")
    List<User> findUsersBornBetweenDates(Date from, Date to);
}
```
您还可以将@TypeConverters限制为不同的范围，包括单个实体，DAOs和DAO方法

###数据库迁移
在您添加和更改应用程序中的功能时，您需要修改实体类以反映这些更改。 当用户更新到最新版本的应用程序时，您不希望它们丢失所有现有数据，特别是如果您无法从远程服务器恢复数据。
Room允许您以这种方式编写Migration类来保留用户数据。 每个Migration类都指定一个startVersion和endVersion 。 在运行时，Room运行每个Migration类的migrate()方法，使用正确的顺序将数据库迁移到更高版本。
**注意：**如果您不提供必要的迁移，则房间将重建数据库，这意味着您将丢失数据库中的所有数据。
```
Room.databaseBuilder(getApplicationContext(), MyDb.class, "database-name")
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3).build();

static final Migration MIGRATION_1_2 = new Migration(1, 2) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER, "
                + "`name` TEXT, PRIMARY KEY(`id`))");
    }
};

static final Migration MIGRATION_2_3 = new Migration(2, 3) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE Book "
                + " ADD COLUMN pub_year INTEGER");
    }
};
```
**注意：**为了保持迁移逻辑正常运行，请使用完整查询，而不是引用表示查询的常量。

迁移过程完成后，Room会验证模式以确保迁移发生正确。 如果Room发现问题，它会引发包含不匹配信息的异常。

####测试迁移
迁移不是简单的写入，并且无法正确写入它们可能会导致应用程序中的崩溃循环。 为了保持您的应用的稳定性，您应该事先测试您的迁移。 Room提供了一个测试 Maven工件来协助测试过程。 但是，要使此工件正常工作，您需要导出数据库的模式。

####导出模式
编译后，Room将数据库的架构信息导出为JSON文件。 要导出模式，请在build.gradle文件中设置room.schemaLocation注解处理器属性，如以下代码片段所示：
build.gradle
```
android {
    ...
    defaultConfig {
        ...
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = ["room.schemaLocation":
                             "$projectDir/schemas".toString()]
            }
        }
    }
}
```
您应该将出口的JSON文件（表示数据库的架构历史记录）存储在版本控制系统中，因为它允许Room创建旧版本的数据库以进行测试。
要测试这些迁移，请将`android.arch.persistence.room:testing` Maven工件从Room添加到测试依赖项中，并将模式位置添加为资产文件夹，如以下代码片段所示：
build.gradle
```
android {
    ...
    sourceSets {
        androidTest.assets.srcDirs += files("$projectDir/schemas".toString())
    }
}
```
测试包提供了一个MigrationTestHelper类，可以读取这些模式文件。 它也是一个Junit4 TestRule类，因此可以管理创建的数据库。
示例迁移测试将显示在以下代码段中：
```
@RunWith(AndroidJUnit4.class)
public class MigrationTest {
    private static final String TEST_DB = "migration-test";

    @Rule
    public MigrationTestHelper helper;

    public MigrationTest() {
        helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                MigrationDb.class.getCanonicalName(),
                new FrameworkSQLiteOpenHelperFactory());
    }

    @Test
    public void migrate1To2() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 1);

        // db has schema version 1. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL(...);

        // Prepare for the next version.
        db.close();

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2);

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
    }
}
```
###测试你的数据库
当您的应用程序运行测试时，如果您没有测试数据库本身，则不需要创建完整的数据库。 房间允许您轻松地模拟测试中的数据访问层。 这个过程是可能的，因为你的DAO不会泄露你的数据库的任何细节。 测试其余的应用程序时，应该创建DAO类的模拟或假的实例。

有两种方式可以测试您的数据库：

* 在你的主机开发机器上。
* 在Android设备上

1.在你的主机上进行测试
房间使用SQLite支持库，它提供了与Android框架类相匹配的界面。 此支持允许您传递支持库的自定义实现来测试数据库查询。

即使此设置允许您的测试运行速度非常快，但不推荐使用，因为您的设备上运行的SQLite版本与用户的设备可能与主机上的版本不符。

2.在Android设备上测试
测试数据库实现的推荐方法是编写一个在Android设备上运行的JUnit测试。 因为这些测试不需要创建一个活动，所以它们应该比您的UI测试执行得更快。

设置测试时，您应该创建数据库的内存中版本，以使测试更加密封，如以下示例所示：
```
@RunWith(AndroidJUnit4.class)
public class SimpleEntityReadWriteTest {
    private UserDao mUserDao;
    private TestDatabase mDb;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, TestDatabase.class).build();
        mUserDao = mDb.getUserDao();
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void writeUserAndReadInList() throws Exception {
        User user = TestUtil.createUser(3);
        user.setName("george");
        mUserDao.insert(user);
        List<User> byName = mUserDao.findUsersByName("george");
        assertThat(byName.get(0), equalTo(user));
    }
}
```

##Dagger2学习笔记

###概述
Dagger2 是一个Android依赖注入框架。而android开发当前非常流行的非MVP模式莫属了，Dagger2的目标便是将MVP中的V P 进一步解耦，达到模块化最大的解耦，使得代码更容易维护。

###Gradle配置
Gradle插件在2.3以上:
```
dependencies {
  compile 'com.google.dagger:dagger:2.x'
  annotationProcessor 'com.google.dagger:dagger-compiler:2.x'
}
```

如果Gradle插件在2.3以下，需要apt插件：
1.在根gradle中
```
dependencies {
     ... // 其他classpath
     classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8' //添加apt命令
 }
```
2.在App.gradle中
```
dependencies {
  compile 'com.google.dagger:dagger:2.x'
  apt 'com.google.dagger:dagger-compiler:2.x'
}
```

###注解
Dagger2是基于Java注解来实现依赖注入的，那么在正式使用之前我们需要先了解下Dagger2中的注解。Dagger2使用过程中我们通常接触到的注解主要包括：@Inject, @Module, @Provides, @Component, @Qulifier, @Scope, @Singleten。
**@Inject和@Component**
@Inject:主要有两个作用，一个是使用在构造函数上，通过标记构造函数让Dagger2来使用（Dagger2通过Inject标记可以在需要这个类实 例的时候来找到这个构造函数并把相关实例new出来）从而提供依赖，另一个作用就是标记在需要依赖的变量让Dagger2为其提供依赖。
@Component：用于标注接口，是依赖需求方和依赖提供方之间的桥梁。被Component标注的接口在编译时会生成该接口的实现类（如果@Component标注的接口为CarComponent，则编译期生成的实现类为DaggerCarComponent）,我们通过调用这个实现类的方法完成注入；
例:
User.java
```
public  class User{
    private String name;
    private int age;
    private String sex;

    @Inject
    public User(){
        this.age = 20;
        this.name = "猫头";
        this.sex = "男";
    }
}
```
User的构造函数用@Inject注解标记后编译时生成了User_Factory,User_Factory继承与Provider相当于实例的提供者,创建提供User的实例
User_Factory.java
```
public final class User_Factory implements Factory<User> {
  private static final User_Factory INSTANCE = new User_Factory();

  @Override
  public User get() {
    return new User();
  }

  public static Factory<User> create() {
    return INSTANCE;
  }
}
```

InjectActivity.java
```
public class InjectActivity extends Activity {

    @Inject
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Factory<User> userFactory = User_Factory.create();
        MembersInjector<InjectActivity> injectActivityMembersInjector = InjectActivity_MembersInjector.create(userFactory);
        injectActivityMembersInjector.injectMembers(this);
        TextView textView = new TextView(this);
        textView.setText(user.toString);
        setContentView(textView);
    }
}
```
user字段使用@inJect注解标记后,编译时会生成InjectActivity_MembersInjector,
InjectActivity_MembersInjector中持有Provider(即User_Factory),从Provider获取User实例赋值给InjectActivity中用@Inject标记的变量
InjectActivity_MembersInjector.java
```
public final class InjectActivity_MembersInjector implements MembersInjector<InjectActivity> {
  private final Provider<User> userProvider;

  public InjectActivity_MembersInjector(Provider<User> userProvider) {
    assert userProvider != null;
    this.userProvider = userProvider;
  }

  public static MembersInjector<InjectActivity> create(Provider<User> userProvider) {
    return new InjectActivity_MembersInjector(userProvider);
  }

  @Override
  public void injectMembers(InjectActivity instance) {
    if (instance == null) {
      throw new NullPointerException("Cannot inject members into a null reference");
    }
    instance.user = userProvider.get();
  }

  public static void injectUser(InjectActivity instance, Provider<User> userProvider) {
    instance.user = userProvider.get();
  }
}
```
但是像上面的InjectActivity中的注入代码太繁杂了,所以一般使用的话会用再创建多一个用@Component注解标记的接口来作为中间连接Bean和Activity之间的注入关系
```
@Component
public interface InjectActivityComponent {
    void Inject(InjectActivity injectACtivity);
}
```
创建了InjectActivityComponent接口后,编译时会自动生成一个实现InjectActivityComponent接口的类,生成DaggerInjectActivityComponent(Dagger+接口名称),这个实现类主要就是做了上面的繁杂的操作
DaggerInjectActivityComponent.java
```
public final class DaggerInjectActivityComponent implements InjectActivityComponent {
  private MembersInjector<InjectActivity> injectActivityMembersInjector;

  private DaggerInjectActivityComponent(Builder builder) {
    assert builder != null;
    initialize(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static InjectActivityComponent create() {
    return new Builder().build();
  }

  @SuppressWarnings("unchecked")
  private void initialize(final Builder builder) {

    this.injectActivityMembersInjector =
        InjectActivity_MembersInjector.create(User_Factory.create());
  }

  @Override
  public void Inject(InjectActivity injectACtivity) {
    injectActivityMembersInjector.injectMembers(injectACtivity);
  }

  public static final class Builder {
    private Builder() {}

    public InjectActivityComponent build() {
      return new DaggerInjectActivityComponent(this);
    }
  }
}
```
所以加了InjectActivityComponent后在InjectActivity的写法就简单多了
```
public class InjectActivity extends Activity {

    @Inject
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerInjectActivityComponent.builder().build().Inject(this);
        TextView textView = new TextView(this);
        textView.setText(user.toString);
        setContentView(textView);
    }
}
```

**注意:**

* 如果在类中有@Inject注解的字段，但是该字段的没有使用@Inject注解其构造函数。此时，Dagger将在请求时注入这些字段，但不会创建新实例,也就意味着将获得一个空的对象。使用@Inject注释添加一个无参数的构造函数，以指示Dagger也可以创建实例。此时，甚至会出现编译不成功，提示添加Provider方法(后续提到)，提供所需实例。
* Dagger支持方法注入，不过，通常用来构造器或字段的依赖注入。
* @Inject并不是万能,接口和第三方库的类不能够使用@Inject创建实例

**@Module和@Provider**
@Module：@Module用于标注提供依赖的类。你可能会有点困惑，上面不是提到用@Inject标记构造函数就可以提供依赖了么，为什么还需要@Module？很多时候我们需要提供依赖的构造函数是第三方库的，我们没法给它加上@Inject注解，又比如说提供以来的构造函数是带参数的，如果我们之所简单的使用@Inject标记它，那么他的参数又怎么来呢？@Module正是帮我们解决这些问题的。

@Provides：@Provides用于标注Module所标注的类中的方法，该方法在需要提供依赖时被调用，从而把预先提供好的对象当做依赖给标注了@Inject的变量赋值；
案例 :
如果创建User的构造函数是带参数的,这时@Inject就不再管用了,这时就会用到@Module和@Provider,使用@Module是User的构造函数上的@Inject就可以去掉了

User.java
```
public class User {

    private String name;
    private int age;
    private String sex;

    public User(String name,int age,String sex){
        this.age = age;
        this.name = name;
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                '}';
    }
}
```
User的构造函数没有@Inject注解标记后之前的方法就行不通了,需要创建一个用@Module和@Provider注解标记的类来生成User的依赖提供

MarkModuleActivityModule.java
```
@Module
public class MarkModuleActivityModule {

    private String name;
    private int age;
    private String sex;

    MarkModuleActivityModule(String name,int age,String sex){
        this.age = age;
        this.name = name;
        this.sex = sex;
    }

    @Provides
    User providerUserBean(){
        return new User(name,age,sex);
    }

}
```
创建用@Module和@Provider注解标记的类后,编译时会生成MarkModuleActivityModule_ProviderUserBeanFactory(类名_@Provider修饰的方法名)类,该类接受一个Module提供初始化提供依赖的前置条件 ,调用@provider修饰的方法获取依赖实例

MarkModuleActivityModule_ProviderUserBeanFactory.java
```
public final class MarkModuleActivityModule_ProviderUserBeanFactory implements Factory<User> {
  private final MarkModuleActivityModule module;

  public MarkModuleActivityModule_ProviderUserBeanFactory(MarkModuleActivityModule module) {
    assert module != null;
    this.module = module;
  }

  @Override
  public User get() {
    return Preconditions.checkNotNull(
        module.providerUserBean(), "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<User> create(MarkModuleActivityModule module) {
    return new MarkModuleActivityModule_ProviderUserBeanFactory(module);
  }

  /** Proxies {@link MarkModuleActivityModule#providerUserBean()}. */
  public static User proxyProviderUserBean(MarkModuleActivityModule instance) {
    return instance.providerUserBean();
  }
}
```
这时样就可以用了

ModuleActivity.java
```
public class ModuleActivity extends Activity {

    @Inject
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MarkModuleActivityModule module = new MarkModuleActivityModule("猫头",20,"男");
        MarkModuleActivityModule_ProviderUserBeanFactory providerAreaBeanFactory = new MarkModuleActivityModule_ProviderUserBeanFactory(module);
        ModuleActivity_MembersInjector moduleActivity_membersInjector = new ModuleActivity_MembersInjector(providerAreaBeanFactory);
        moduleActivity_membersInjector.injectMembers(this);

        TextView textView = new TextView(this);
        textView.setText(user.toString());
        setContentView(textView);
    }
}
```
同样的上面的这些繁杂的代码编译器已经帮我们写了,所以我们只需要再改改Component标记的类就可以一句代码注入依赖了

ModuleActivityComponent.java
```
@Component( modules = {MarkModuleActivityModule.class})
public interface ModuleActivityComponent {
    void Inject(ModuleActivity moduleActivity);
}
```
编译是生成DaggerModuleActivityComponent,在使用了@Module后DaggerModuleActivityComponent在build时会校验是否设置了Module类,如果没有的话会报错,提示你必须设置module,因为在初始化是会在module获取依赖实例
DaggerModuleActivityComponent.java
```
public final class DaggerModuleActivityComponent implements ModuleActivityComponent {
  private Provider<User> providerUserBeanProvider;

  private MembersInjector<ModuleActivity> moduleActivityMembersInjector;

  private DaggerModuleActivityComponent(Builder builder) {
    assert builder != null;
    initialize(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  @SuppressWarnings("unchecked")
  private void initialize(final Builder builder) {

    this.providerUserBeanProvider =
        MarkModuleActivityModule_ProviderUserBeanFactory.create(builder.markModuleActivityModule);

    this.moduleActivityMembersInjector =
        ModuleActivity_MembersInjector.create(providerUserBeanProvider);
  }

  @Override
  public void Inject(ModuleActivity moduleActivity) {
    moduleActivityMembersInjector.injectMembers(moduleActivity);
  }

  public static final class Builder {
    private MarkModuleActivityModule markModuleActivityModule;

    private Builder() {}

    public ModuleActivityComponent build() {
      if (markModuleActivityModule == null) {
        throw new IllegalStateException(
            MarkModuleActivityModule.class.getCanonicalName() + " must be set");
      }
      return new DaggerModuleActivityComponent(this);
    }

    public Builder markModuleActivityModule(MarkModuleActivityModule markModuleActivityModule) {
      this.markModuleActivityModule = Preconditions.checkNotNull(markModuleActivityModule);
      return this;
    }
  }
}
```
在Activiyt注入依赖时也要多加一个方法markModuleActivityModule(Module),传入Module提供依赖实例
ModuleActivity.java
```
public class ModuleActivity extends Activity {

    @Inject
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/       DaggerModuleActivityComponent.builder().markModuleActivityModule(new MarkModuleActivityModule("猫头",20,"男")).build().Inject(this);

        TextView textView = new TextView(this);
        textView.setText(user.toString());
        setContentView(textView);
    }
}
```

**@Qulifier**
@Qulifier：@Qulifier用于自定义注解，也就是说@Qulifier就如同Java提供的几种基本元注解一样用来标记注解类。我们在使用@Module来标注提供依赖的方法时，方法名我们是可以随便定义的（虽然我们定义方法名一般以provide开头，但这并不是强制的，只是为了增加可读性而已）。那么Dagger2怎么知道这个方法是为谁提供依赖呢？答案就是返回值的类型，Dagger2根据返回值的类型来决定为哪个被@Inject标记了的变量赋值。但是问题来了，一旦有多个一样的返回类型Dagger2就懵逼了。@Qulifier的存在正式为了解决这个问题，我们使用@Qulifier来定义自己的注解，然后通过自定义的注解去标注提供依赖的方法和依赖需求方（也就是被@Inject标注的变量），这样Dagger2就知道为谁提供依赖了。----一个更为精简的定义：当类型不足以鉴别一个依赖的时候，我们就可以使用这个注解标示；
案例:
假如Activity需要同时依赖2个User,这时我们会用到@Qualifier
这里根据上面的例子进行修改
QualifierA.java
```
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface QualifierA {
}
```
QualifierB.java
```
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface QualifierB {
}
```

MarkQualifierActivityModule.java
```
@Module
public class MarkQualifierActivityModule {

    private String name;
    private int age;
    private String sex;

    MarkQualifierActivityModule(String name, int age, String sex){
        this.age = age;
        this.name = name;
        this.sex = sex;
    }

    @QualifierA
    @Provides
    User providerUserBeanA(){
        return new User(name+"A",age,sex);
    }

    @QualifierB
    @Provides
    User providerUserBeanB(){
        return new User(name+"B",age+1,sex);
    }
}
```
使用@Qualifier标记后这时会生成2个类MarkQualifierActivityModule_ProviderUserBeanAFactory和MarkQualifierActivityModule_ProviderUserBeanBFactory,原理同于上面的例子

QualifierActivity.java
```
public class QualifierActivity extends Activity {

    @QualifierA
    @Inject
    User usera;

    @QualifierB
    @Inject
    User userb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerQualifierActivityComponent.builder().markQualifierActivityModule(new MarkQualifierActivityModule("猫头",20,"男")).build().Inject(this);

        TextView textView = new TextView(this);
        textView.setText(usera.toString()+"\n"+userb.toString());
        setContentView(textView);
    }
}
```

**@Scope**
@Scope：@Scope同样用于自定义注解，我能可以通过@Scope自定义的注解来限定注解作用域，实现局部的单例；
@Scope是javax.inject包下的一个注解，其是用来标识范围的注解，该注解适用于注解包含可注入的构造函数的类，并控制该如何重复使用类的实例。在默认情况下，也就是说仅仅使用@Inject注解构造函数，而没有使用@Scope注解类时，每次依赖注入都会创建一个实例(通过注入类型的构造函数创建实例)。如果使用了@Scope注解了该类，注入器会缓存第一次创建的实例，然后每次重复注入缓存的实例，而不会再创建新的实例。
@Singleton：@Singleton其实就是一个通过@Scope定义的注解，我们一般通过它来实现全局单例。但实际上它并不能提前全局单例，是否能提供全局单例还要取决于对应的Component是否为一个全局对象。













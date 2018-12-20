
## Binder原理、AIDL的使用、多进程的定义和特性

### 多进程的定义和特性

进程指的是系统中能独立运行并作为资源分配的基本单位,多进程一般指一个应用中包含多个进程

Android需要开启多进程模式需要在Manifest文件给四大组件加上`android:process`属性

Android系统中每个应用程序都会有一个唯一的UID,每个UID对应的程序都是独立的,不可共享内存和数据,多进程模式可以通过在Manifest文件给四大组件加上`android:process`属性开启,这些进程都有一个自己PID,他们属于同一个应用,所以他们的UID是一样,这些进程拥有自己的内存空间,他们可共享数据但不共享内存,所以内存空间上他们是互相不影响的,这也说明进程的结构性,独立性,并发性和异步性

**开启多进程导致的问题:**
 1. 静态成员,单例模式和线程同步机制失效,不同进程操作各自内存下的对象
 2. SharedPreferences的可靠性下降,多进程并发操作导致sp写入或读取不可靠
 3. Application多次初始化,每个进程启动都会创建新的application

### AIDL的使用
### 基本使用
#### 1.服务端
##### 1.1创建一个Service监听客户端的连接请求
- 1.1.1 创建一个实现与Service的类,并在Manifest文件声明

##### 1.2创建一个AIDL文件,在AIDL文件中声明对客户端提供的接口
- 1.2.1 AIDL文件用到自定义的Parcelable对象必须新建一个同名的aidl文件,并声明为Pracelable类型
- 1.2.2 aidl自定义的Parcelable对象和AIDL对象必须显示import进来
- 1.2.3 aidl中除了,基本数据类型,其他类型的参数必须标上方向in,out或inout
			`void addBook(in Book book);`

##### 1.3在Service中实现接口,并返回
- 1.3.1 创建一个binder对象,对象实现与aidl接口.Stub,并实现它的接口
- 1.3.2 aidl的方法时在服务端的Binder线程池中执行的,注意处理线程同步(CopyOnWriteArrayList和ConcurrentHashMap)

#### 2.客户端
##### 2.1绑定Service,将service返回来的Binder对象转成AIDL接口所属的类型
- 2.1.1 aidl接口.Stub.asInterface(binder)将Binder对象转换

##### 2.2调用AIDL提供的接口
- 2.2.1 调用接口注意有可能是耗时操作

##### 2.3在onDestroy方法解绑Service

### 客户端监听服务端数据变化
#### 1.服务端
##### 1.1 创建一个Listener.aidl文件,声明监听回调接口方法

##### 1.2 在aidl接口中增加addListener(listener)和removeListener(listener)的接口方法

##### 1.3 在Service的Binder中实现接口方法,并添加一个容器保存listener
- 1.3.1 使用RemoteCallbackList<E extends IInterface>容器来保存listener
- 1.3.2 使用register和unregister方法添加或移除listener
- 1.3.3 使用getBroadcastItem(index)获取Listener
- 1.3.4 beginBroadcast和finishBroadcast必须要配对使用
- 1.3.5 listener的回调方法运行在客户端的Binder线程池中,所以建议在非UI线程运行

#### 2.客户端
##### 2.1 创建一个Listener.Stub,实现接口回调方法获取数据

##### 2.2 在绑定Service后调用addListener添加监听

##### 2.3 在回调方法接受服务端回调信息
- 2.3.1 需要考虑是否主线程

##### 2.4 在onDestroy方法调用removeListener(listener)方法移除接口

### 监听服务端的断连状态
#### 1.通过onServiceDisconnedtion方法监听
#### 2.通过设置DeathRecipient在biderDied监听
> 这区别在于1在客户端的UI线程中回调,2在Binder的线程池中被回调



### 服务端添加权限认证
#### 1.在onBind方法中进行验证
##### 1.1 在Manifest文件中声明所需的权限

```xml
	<permission android:name="${applicationId}.permission.ACCESS_BOOK_SERVICE"
        android:protectionLevel="normal"/>
```

##### 1.2 在服务端的onBind方法中校验权限
```java
		int check = checkCallingOrSelfPermission("Manifest中声明的权限");
        if (check == PackageManager.PERMISSION_DENIED){
            return null;
        }
		return binder;
```
##### 1.3 在使用该服务的客户端就必须在Manifest中添加服务权限声明
```xml
<uses-permission android:name="${applicationId}.permission.ACCESS_BOOK_SERVICE"/>
```
#### 2.在服务端的onTransact方法中进行权限验证
##### 2.1 重写Binder的onTransact,在发放里采用和onBind方法一样的permission方法检验
##### 2.2 重写Binder的onTransact,通过getCallingUid和getCallingPid获取Uid和Pid,校验调用服务者的包名
```java
	@Override
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {

//检验权限声明
        int check = checkCallingOrSelfPermission("${applicationId}.permission.ACCESS_BOOK_SERVICE");
        if (check == PackageManager.PERMISSION_DENIED){
            return false;
        }

//检验包名
        String packageName = null;
        String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
        if (packages != null && packages.length > 0){
            packageName = packages[0];
        }
        if (!packageName.startsWith("${applicationId}")){
            return false;
        }

        return super.onTransact(code, data, reply, flags);
    }
```
### Binder连接池
#### 1.创建IBinderPool.aidl文件,声明`IBinder queryBinder(int binderCode)`方法

#### 2.创建BinderPoolImpl.java实现IBinderPool.Stub
- 2.1 定义每个服务的binderCoder常量
- 2.2 根据传入的binderCode构造对应的Binder实现返回

#### 3.创建BinderPoolService.java,在onbind返回BinderPoolImpl的实例

#### 4.创建BinderPool.java
- 4.1 构造BinderPool时连接绑定BinderPoolService
-  4.2 在绑定服务的过程中使用CountDownLatch锁住线程,服务连接后释放线程
- 4.3 设置IBinder.DeathRecipient监听,断连时重连服务

#### 5.使用BinderPool
- 5.1 在子线程中获取BinderPool单例
- 5.2 调用queryBidner,传入binderCode获取Binder实例


### Binder的原理

### 远程服务的调用过程
#### 1.Activity创建Intent绑定服务
- 1.1 [绑定服务的过程分析](https://blog.csdn.net/luoshengyang/article/details/6745181)
- 1.2 [bindService源码跟踪](https://blog.csdn.net/jelly_fang/article/details/50488915)

#### 2.在连接成功的回调中asInterface(IBinder pBinder)拿到远程服务代理
- 2.1 拿到的服务是IBookManager.Stub.Proxy(IBinder pBinder)
```java
	public static com.maotou.aidldemo.IBookManager asInterface(android.os.IBinder obj) {
         if ((obj == null)) {
             return null;
         }
//查询本地服务
         android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
         if (((iin != null) && (iin instanceof com.maotou.aidldemo.IBookManager))) {
             return ((com.maotou.aidldemo.IBookManager) iin);
         }
//远程服务走了这里
         return new com.maotou.aidldemo.IBookManager.Stub.Proxy(obj);
     }
```

#### 3.调用服务的addBook方法`List<Book> addBook(in Book book)`
- 3.1 这里实际调用了IBookManager.Stub.Proxy的addBook方法
```java
	@Override
    public java.util.List<com.maotou.aidldemo.Book> addBook(com.maotou.aidldemo.Book book) throws android.os.RemoteException {
       android.os.Parcel _data = android.os.Parcel.obtain();
       android.os.Parcel _reply = android.os.Parcel.obtain();
       java.util.List<com.maotou.aidldemo.Book> _result;
       try {
           _data.writeInterfaceToken(DESCRIPTOR);
           if ((book != null)) {
               _data.writeInt(1);
               book.writeToParcel(_data, 0);
           } else {
               _data.writeInt(0);
           }
           mRemote.transact(Stub.TRANSACTION_addBook, _data, _reply, 0);
           _reply.readException();
           _result = _reply.createTypedArrayList(com.maotou.aidldemo.Book.CREATOR);
       } finally {
           _reply.recycle();
           _data.recycle();
       }
       return _result;
   }
```
- 3.2 addBook方法中数据通过实现Parelable是重写的writeToParel写入_data

#### 4.调用远程服务代理`mRemote.transact()`
- 4.1 transact()方法的实现在Binder中
	```java
	public final boolean transact(int code, @NonNull Parcel data, @Nullable Parcel reply,
            int flags) throws RemoteException {
        if (false) Log.v("Binder", "Transact: " + code + " to " + this);

        if (data != null) {
            data.setDataPosition(0);
        }
        boolean r = onTransact(code, data, reply, flags);
        if (reply != null) {
            reply.setDataPosition(0);
        }
        return r;
    }
	```
- 4.2 transact中调到onTransact,期实现在IBookManager.Stub.onTransact()
	
#### 5.调用远程服务的业务方法IBookManager.Stub.onTransact()
```java
	case TRANSACTION_addBook: {
        data.enforceInterface(descriptor);
        com.maotou.aidldemo.Book _arg0;
        if ((0 != data.readInt())) {
            _arg0 = com.maotou.aidldemo.Book.CREATOR.createFromParcel(data);
        } else {
            _arg0 = null;
        }
        java.util.List<com.maotou.aidldemo.Book> _result = this.addBook(_arg0);
        reply.writeNoException();
        reply.writeTypedList(_result);
        return true;
    }
```
- 5.1 通过实现Parelable是重写的createFromParcel方法将data转为实体Book
- 5.2 调用IBookManager.Stub.addBook(),这个addBook方法的实现就是在BookService里写的业务逻辑
- 5.3 返回书本列表_result,通过writeTypedList方法将_result写入reply

#### 6.onTransact方法走完,一层一层返回到IBookManager.Stub.Proxy的addBook方法
- 6.1 通过reply的`createTypedArrayList`方法获取业务返回的图书列表
- 6.2 最终将结果返回掉业务调用端

### 参考资料
[Android深入浅出之Binder机制](http://www.cnblogs.com/innost/archive/2011/01/09/1931456.html)
**MediaService初始化过程**
```
int main(int argc, char** argv)
{

//获得一个ProcessState实例
	sp<ProcessState> proc(ProcessState::self());

//得到一个ServiceManager对象
    sp<IServiceManager> sm = defaultServiceManager();

    MediaPlayerService::instantiate();//初始化MediaPlayerService服务

    ProcessState::self()->startThreadPool();//看名字，启动Process的线程池？

    IPCThreadState::self()->joinThreadPool();//将自己加入到刚才的线程池？
}
```

第一步
打开/dev/binder,又来和内核Binder机制的交互通道,映射fd内存(估计这块内存适合binder设备共享的)

第二步
初始化ServiceManager,中间创建ProcessState,IPCThreadState(主线程),BpBinder(handler值为0),defaultServiceManager()返回了ServiceManager的代理BpServiceManager,他的remote是BpBinder

第三步
初始化MediaService并添加进ServiceManager,过程中是将BnMediaPlayService通过BpServiceManager与ServiceManager通讯,将其添加进ServiceManager
调用addService方法时,是调用了remote(BpBinder)的transact方法,再到IPCTreadState的transact方法,通过writeTransactionData()方法将MediaPlayService的名称和data(命令包)写入mOut缓存区,最后从wirteForResponse()中调用talkWriteDriver()通过ioctl()将mIn和mOut赋值给bwr(binder_write_read)

第四步
BnServiceManager(servicemanager.c)处理BpServiceManager发送到binderDriver的请求命令,BnServiceManager初始化打开/dev/binder/,将自己设置为manager(BINDER_SET_CONTEXT_MGR,0写入binderDriver),调用binder_loop()开启循环从binderDriver中读取请求命令,binder_parse()解析命令,最后在一个类似于handleMessage的地方(svcmgr_handler)处理解析出来的命令,svcmgr_handler()中通过code(SVC_MGR_ADD_SERVICE)去调用对应方法,通过code调用do_add_service()将服务添加到svclist(ServiceManager的服务列表)

第五步
创建线程池,在工作线程中循环调用executeCommand(cmd)处理消息,最后调用BBinder的transact再调用自己的onTransact通过code确认调用那个业务方法

[罗升阳的Android进程间通信（IPC）机制Binder简要介绍和学习计划](https://blog.csdn.net/Luoshengyang/article/details/6618363)
[MediaService初始化过程]()




#Retrofit2.0使用总结
###1.概述
随着Google对HttpClient 摒弃,和Volley的逐渐没落,OkHttp开始异军突起,而Retrofit则对okHttp进行了强制依赖。

Retrofit是由Square公司出品的针对于Android和Java的类型安全的Http客户端，如果看源码会发现其实质上就是对okHttp的封装，使用面向接口的方式进行网络请求，利用动态生成的代理类封装了网络接口请求的底层,其将请求返回javaBean，对网络认证 REST API进行了很好对支持此，使用Retrofit将会极大的提高我们应用的网络体验。

###2.Retrofit2.0的使用配置
####引入依赖
```java
	//Retrofit的主依赖
	compile 'com.squareup.retrofit2:retrofit:2.0.2'
	//Gson转换器
    compile 'com.squareup.retrofit2:converter-gson:2.0.2'
    //RxJava支持适配器
    compile 'com.squareup.retrofit2:adapter-rxjava:2.0.2'
    //okhttp日志拦截器
    compile 'com.squareup.okhttp3:logging-interceptor:3.2.0'
```
####Retrofit配置
```java
	Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();
        apiService = retrofit.create(ApiService.class);
```
baseUrl相当于设置host
client提供okhttpclient,自定义的client
addCallAdapterFactory提供RxJava支持，如果没有提供响应的支持(RxJava,Call),则会抛出异常。
addConverterFactory提供Gson支持，可以添加多种序列化Factory，但是GsonConverterFactory必须放在最后,否则会抛出异常。
    
####OkHttp配置
```java
	//日志拦截器
	HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .addNetworkInterceptor(authorizationInterceptor)
                .build();
```
level为设置日志打印的级别NONE(不打印)/BASIC(打印请求和响应信息) / HEADERS(打印基础的请求,响应和请求头信息) / BODY(打印基础的请求,响应,请求头信息和请求体信息)
retryOnConnectionFailure:错误重联
addInterceptor:设置应用拦截器，可用于设置公共参数，头信息，日志拦截等
addNetworkInterceptor：网络拦截器，可以用于重试或重写，对应与1.9中的setRequestInterceptor。
[中文翻译：Okhttp-wiki 之 Interceptors 拦截器](http://www.jianshu.com/p/2710ed1e6b48)

###3.Retrofit2.0的使用介绍
* <font color=#DC143C size=3>注：</font>retrofit2.0后：BaseUrl要以/结尾；@GET 等请求不要以/开头；@Url: 可以定义完整url，不要以 / 开头。

####基本用法
```java
//定以接口
public interface GitHubService {
  @GET("users/{user}/repos")
  Call<List<Repo>> listRepos(@Path("user") String user);
}

//获取实例
Retrofit retrofit = new Retrofit.Builder()
    //设置OKHttpClient,如果不设置会提供一个默认的
    .client(new OkHttpClient())
    //设置baseUrl
    .baseUrl("https://api.github.com/")
    //添加Gson转换器
    .addConverterFactory(GsonConverterFactory.create())
    .build();

GitHubService service = retrofit.create(GitHubService.class);

//同步请求
//https://api.github.com/users/octocat/repos
Call<List<Repo>> call = service.listRepos("octocat");
try {
     Response<List<Repo>> repos  = call.execute();
} catch (IOException e) {
     e.printStackTrace();
}

//call只能调用一次。否则会抛 IllegalStateException
Call<List<Repo>> clone = call.clone();

//异步请求
clone.enqueue(new Callback<List<Repo>>() {
        @Override
        public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
            // Get result bean from response.body()
            List<Repo> repos = response.body();
            // Get header item from response
            String links = response.headers().get("Link");
            /**
              * 不同于retrofit1 可以同时操作序列化数据javabean和header
              */
        }

        @Override
        public void onFailure(Call<List<Repo>> call, Throwable t) {

        }
    });

// 取消
call.cancel();
```
####RxJava支持
```java
//rxjava support
public interface GitHubService {
  @GET("users/{user}/repos")
  Observable<List<Repo>> listRepos(@Path("user") String user);
}

// 获取实例
// Http request
Observable<List<Repo>> call = service.listRepos("octocat");
```
####retrofit注解
* 方法注解:包含@GET、@POST、@PUT、@DELETE、@PATH、@HEAD、@OPTIONS、@HTTP
* 标记注解:包含@FormUrlEncoded、@Multipart、@Streaming
* 参数注解:包含@Query,@QueryMap、@Body、@Field，@FieldMap、@Part，@PartMap
* 其他注解:@Path、@Header,@Headers、@Url

<font color=#DC143C size=4>几个特殊的注解</font>

* @HTTP：可以替代其他方法的任意一种
```java
/**
     * method 表示请的方法，不区分大小写
     * path表示路径
     * hasBody表示是否有请求体
     */
    @HTTP(method = "get", path = "users/{user}", hasBody = false)
    Call<ResponseBody> getFirstBlog(@Path("user") String user);
```

* @Url：使用全路径复写baseUrl，适用于非统一baseUrl的场景。
```java
	@GET
	Call<ResponseBody> v3(@Url String url);
```

* @Streaming:用于下载大文件
```java
	@Streaming
	@GET
	Call<ResponseBody> downloadFileWithDynamicUrlAsync(@Url String fileUrl);
	
	----
	
	ResponseBody body = response.body();
	long fileSize = body.contentLength();
	InputStream inputStream = body.byteStream();
```

<font color=#DC143C size=4>常用注解</font>

* @Path：URL占位符，用于替换和动态更新,相应的参数必须使用相同的字符串被@Path进行注释
```java
@GET("group/{id}/users")
Call<List<User>> groupList(@Path("id") int groupId);
//--> http://baseurl/group/groupId/users

//等同于：
@GET
Call<List<User>> groupListUrl(
      @Url String url);
```
 * @Query,@QueryMap:查询参数，用于GET查询,需要注意的是@QueryMap可以约定是否需要encode
```java
@GET("group/users")
Call<List<User>> groupList(@Query("id") int groupId);
//--> http://baseurl/group/users?id=groupId

Call<List<News>> getNews((@QueryMap(encoded=true) Map<String, String> options);
```
* @Body:用于POST请求体，将实例对象根据转换方式转换为对应的json字符串参数，这个转化方式是GsonConverterFactory定义的。
```java
@POST("add")
 Call<List<User>> addUser(@Body User user);
```
* @Field，@FieldMap:Post方式传递简单的键值对,需要添加@FormUrlEncoded表示表单提交Content-Type:application/x-www-form-urlencoded
```java
@FormUrlEncoded
@POST("user/edit")
Call<User> updateUser(@Field("first_name") String first, @Field("last_name") String last);
```
* @Part，@PartMap：用于POST文件上传其中@Part MultipartBody.Part代表文件，@Part("key") RequestBody代表参数需要添加@Multipart表示支持文件上传的表单，Content-Type: multipart/form-data
```java
@Multipart
    @POST("upload")
    Call<ResponseBody> upload(@Part("description") RequestBody description,@Part MultipartBody.Part file);
    // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
    // use the FileUtils to get the actual file by uri
    File file = FileUtils.getFile(this, fileUri);

    // create RequestBody instance from file
    RequestBody requestFile =
            RequestBody.create(MediaType.parse("multipart/form-data"), file);

    // MultipartBody.Part is used to send also the actual file name
    MultipartBody.Part body =MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

    // add another part within the multipart request
    String descriptionString = "hello, this is description speaking";
    RequestBody description =RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
```
[Retrofit2 完全解析 探索与okhttp之间的关系](http://blog.csdn.net/lmj623565791/article/details/51304204)
[Retrofit 2 — How to Upload Files to Server](https://futurestud.io/tutorials/retrofit-2-how-to-upload-files-to-server)
* @Header：header处理，不能被互相覆盖，用于修饰参数
```java
//动态设置Header值
@GET("user")
Call<User> getUser(@Header("Authorization") String authorization)

等同于:

//静态设置Header值
@Headers("Authorization: authorization")//这里authorization就是上面方法里传进来变量的值
@GET("widget/list")
Call<User> getUser()
```
* @Headers 用于修饰方法,用于设置多个Header值
```java
@Headers({
    "Accept: application/vnd.github.v3.full+json",
    "User-Agent: Retrofit-Sample-App"
})
@GET("users/{username}")
Call<User> getUser(@Path("username") String username);
```
####自定义Converter
retrofit默认情况支持的converts
```
Gson: com.squareup.retrofit2:converter-gson
Jackson: com.squareup.retrofit2:converter-jackson
Moshi: com.squareup.retrofit2:converter-moshi
Protobuf: com.squareup.retrofit2:converter-protobuf
Wire: com.squareup.retrofit2:converter-wire
Simple XML: com.squareup.retrofit2:converter-simplexml
Scalars (primitives, boxed, and String): com.squareup.retrofit2:converter-scalars
```
要自定义`Converter<F,T>`,需要先看一下GsonConverterFactory的实现,GsonConverterFactory实现了内部类Converter.Factory.其中GsonConverterFactory中的主要两个方法,主要用于解析request和response的,在Factory中还有一个方法stringConverter,用String的转换.
```java
//主要用于响应体的处理，Factory中默认实现为返回null，表示不处理
 @Override
  public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
      Retrofit retrofit) {
    TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
    return new GsonResponseBodyConverter<>(gson, adapter);
  }

/**
  *主要用于请求体的处理，Factory中默认实现为返回null，不能处理返回null
  *作用对象Part、PartMap、Body
  */
  @Override
  public Converter<?, RequestBody> requestBodyConverter(Type type,
      Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
    TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
    return new GsonRequestBodyConverter<>(gson, adapter);
  }
```
GsonRequestBodyConverter实现了Converter<F, T>接口，主要实现了转化的方法`T convert(F value) throws IOException;`

* StringConverterFactory实现源码
```java
//StringConverterFactory
public class StringConverterFactory extends Converter.Factory {

    public static StringConverterFactory create() {
        return new StringConverterFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,Retrofit retrofit) {
        return new StringResponseBodyConverter();
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new StringRequestBodyConverter();
    }
}

//StringRequestBodyConverter
public class StringRequestBodyConverter  implements Converter<String, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override public RequestBody convert(String value) throws IOException {
        Buffer buffer = new Buffer();
        Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
        writer.write(value);
        writer.close();
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
    }
}

//StringResponseBodyConverter
public class StringResponseBodyConverter implements Converter<ResponseBody, String> {
    @Override
    public String convert(ResponseBody value) throws IOException {
        try {
            return value.string();
        } finally {
            value.close();
        }
    }
}
```
####自定义Interceptor
Retrofit2.0底层依赖与OkHttp,所以需要使用OkHttp的Interceptors来对所有请求进行拦截,我们可以通过自定义Interceptor来实现很多操作,打印日志,缓存,重试等等.
要实现机子的拦截器需要有以下的步骤
(1)需要实现`Interceptor`接口,并复写`intercept(Chain chain)`方法,返回response
(2)Request和Response的Builder中有header,addHeader,headers方法,需要注意的是使用header有重复的将会被覆盖,而addHeader则不会.

* 标准的Interceptor写法
```java
public class OAuthInterceptor implements Interceptor {

  private final String username;
  private final String password;

  public OAuthInterceptor(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override public Response intercept(Chain chain) throws IOException {

    String credentials = username + ":" + password;

    String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

    Request originalRequest = chain.request();
    String cacheControl = originalRequest.cacheControl().toString();

    Request.Builder requestBuilder = originalRequest.newBuilder()
        //Basic Authentication,也可用于token验证,OAuth验证
        .header("Authorization", basic)
        .header("Accept", "application/json")
        .method(originalRequest.method(), originalRequest.body());

    Request request = requestBuilder.build();

    Response originalResponse = chain.proceed(request);
    Response.Builder responseBuilder =
        //Cache control设置缓存
        originalResponse.newBuilder().header("Cache-Control", cacheControl);

    return responseBuilder.build();
  }
}
```
####缓存策略
设置缓存就需要用到OkHttp的inrerceptors,缓存的设置需要靠请求和响应头.如果想要弄清楚缓存机制,则需要了解一下HTTP语义,其中控制缓存的就是`Cache-Control`字段
[彻底弄懂 Http 缓存机制 - 基于缓存策略三要素分解法](http://mp.weixin.qq.com/s?__biz=MzA3NTYzODYzMg==&mid=2653578381&idx=1&sn=3f676e2b2e08bcff831c69d31cf51c51&chksm=84b3b68ab3c43f9c9f5fc826f462494dc8457d8994c3789007b7182e3d30e86876688ea1bc8f&mpshare=1&scene=1&srcid=0426rHPmAj0LBjqZNvP3EKZ1#rd)
[Retrofit2.0+okhttp3缓存机制以及遇到的问题](http://blog.csdn.net/picasso_l/article/details/50579884)
[How Retrofit with OKHttp use cache data when offline](http://stackoverflow.com/questions/31321963/how-retrofit-with-okhttp-use-cache-data-when-offline)
[使用Retrofit和Okhttp实现网络缓存。无网读缓存，有网根据过期时间重新请求](http://www.jianshu.com/p/9c3b4ea108a7)
>一般情况下我们需要达到的缓存效果是这样的:
* 没有网或者网络较差的时候要使用缓存(统一设置)
* 有网络的时候,要保证不同的需求,实时性数据不用缓存,一般请求需要缓存(单个请求的header来实现)

OkHttp3中有一个Cache类是用来定义缓存的,此类详细介绍了几种缓存策略,具体看此类的源码
>noCache ：不使用缓存，全部走网络
noStore ： 不使用缓存，也不存储缓存
onlyIfCached ： 只使用缓存
maxAge ：设置最大失效时间，失效则不使用
maxStale ：设置最大失效时间，失效则不使用
minFresh ：设置最小有效时间，失效则不使用
FORCE_NETWORK ： 强制走网络
FORCE_CACHE ：强制走缓存

####配置目录
这个是缓存文件的存放位置,okhttp默认是没有缓存,且没有缓存目录的
```java
 private static final int HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;

  private Cache cache() {
         //设置缓存路径
         final File baseDir = AppUtil.getAvailableCacheDir(sContext);
         final File cacheDir = new File(baseDir, "HttpResponseCache");
         //设置缓存 10M
         return new Cache(cacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE);
     }
```
其中获取cache目录,我们一般采取的策略就是应用卸载,即删除.一般使用如下2个目录

* data/$packageName$/cache:Context.getCacheDir()
* /storage/sdcard0/Andorid/data/$packageName$/cache:Context.getExternalCacheDir()
且当sd卡空间小于data可用空间时,使用data目录
[Android文件存储使用参考 - liaohuqiu](http://www.tuicool.com/articles/AvUnqiy)
####缓存第一种类型
配置单个请求的@Headers,设置次请求的缓存策略,不影响其他请求的缓存策略,不设置则没有缓存
```java
// 设置 单个请求的 缓存时间
@Headers("Cache-Control: max-age=640000")
@GET("widget/list")
Call<List<Widget>> widgetList();
```
####缓存第二种类型
有网和没网都优先读缓存,统一缓存策略,降低服务器压力
```java
private Interceptor cacheInterceptor() {
      Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);

                String cacheControl = request.cacheControl().toString();
                if (TextUtils.isEmpty(cacheControl)) {
                    cacheControl = "public, max-age=60";
                }
                return response.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            }
        };
      }
```
此中方式的缓存Interceptor实现：ForceCachedInterceptor.java(https://github.com/wuMaoTou/AndroidDev/blob/master/retrofit2/htttp/src/main/java/com/bobomee/android/htttp/okhttp/interceptor/ForceCachedInterceptor.java)

####缓存第三种
结合前两种,离线读取本地缓存,在线获取最新数据(读取单个请求的请求头,亦可统一设置)
```java
private Interceptor cacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                if (!AppUtil.isNetworkReachable(sContext)) {
                    request = request.newBuilder()
                            //强制使用缓存
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }

                Response response = chain.proceed(request);

                if (AppUtil.isNetworkReachable(sContext)) {
                    //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                    String cacheControl = request.cacheControl().toString();
                    Logger.i("has network ,cacheControl=" + cacheControl);
                    return response.newBuilder()
                            .header("Cache-Control", cacheControl)
                            .removeHeader("Pragma")
                            .build();
                } else {
                    int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                    Logger.i("network error ,maxStale="+maxStale);
                    return response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale="+maxStale)
                            .removeHeader("Pragma")
                            .build();
                }

            }
        };
    }
```
此种方式的缓存interceptor实现:[OfflineCacheControlInterceptor.java](https://github.com/wuMaoTou/AndroidDev/blob/master/retrofit2/htttp/src/main/java/com/bobomee/android/htttp/okhttp/interceptor/OfflineCacheControlInterceptor.java)

####错误处理
在请求网络的时候,我们不止会得到HttpException,还有我们和服务器约定的errorCode和errorMessage,为了统一处理,我可以预处理一下上面的2个字段,定义BaseModel,在ConverterFactory中进行处理
[Retrofit+RxJava实战日志(3)-网络异常处理](http://www.jianshu.com/p/9c3f0af1180d)
[retrofit-2-simple-error-handling](https://futurestud.io/tutorials/retrofit-2-simple-error-handling)


####Retrofit封装参考
[官方文档](http://square.github.io/retrofit/#restadapter-configuration)
[Articles tagged in: Retrofit](https://futurestud.io/blog/tag/retrofit)
[Retrofit2 完全解析 探索与okhttp之间的关系](http://blog.csdn.net/lmj623565791/article/details/51304204)
[Retrofit 2.0 + OkHttp 3.0 配置](https://drakeet.me/retrofit-2-0-okhttp-3-0-config)
[更新到Retrofit2的一些技巧](http://blog.csdn.net/tiankong1206/article/details/50720758)
[Effective OkHttp](http://omgitsmgp.com/2015/12/02/effective-okhttp/)
[Okhttp-wiki 之 Interceptors 拦截器](http://www.jianshu.com/p/2710ed1e6b48)
[Retrofit2.0+okhttp3缓存机制以及遇到的问题](http://blog.csdn.net/picasso_l/article/details/50579884)
[How Retrofit with OKHttp use cache data when offline](http://stackoverflow.com/questions/31321963/how-retrofit-with-okhttp-use-cache-data-when-offline)
[使用Retrofit和Okhttp实现网络缓存。无网读缓存，有网根据过期时间重新请求](http://www.jianshu.com/p/9c3b4ea108a7)
[用 Retrofit 2 简化 HTTP 请求](https://realm.io/cn/news/droidcon-jake-wharton-simple-http-retrofit-2/)
[Retrofit请求参数注解字段说明](http://www.loongwind.com/archives/242.html)
[Android文件存储使用参考 - liaohuqiu](http://www.tuicool.com/articles/AvUnqiy)
[Retrofit+RxJava实战日志(3)-网络异常处理](http://www.jianshu.com/p/9c3f0af1180d)
[retrofit-2-simple-error-handling](https://futurestud.io/tutorials/retrofit-2-simple-error-handling)







### 什么是RESTful?
Representational State Transfer(表现层状态转移),如果一个接口架构设计符合REST原则,我们就称之为RESTful架构

> 
RESTful架构是目前最流行的互联网软件架构,它结构清晰,符合标准,易于理解,扩展方便等特点,所以越来越多的网站使用RESTful原则设计


### 1.起源 
REST源之于Fielding的博士论文, 提出的初衷是想在符合架构原理的前提下,理解和评估以网络为基础的应用软件的架构设计,得到一个功能强,性能好,适宜通信的架构.

### 2.RESTful的来源
Fielding将他对互联网软件的架构原则命名为REST,即Representational State Transfer的缩写,所以我们称符合REST原则设计的架构叫RESTful架构

### 3.解析RESTful

* 表现层(Representational)  
"表现层"其实也可以是指系统中"资源"(Resources),也就是开发中对应前后端交互传送的实体,这里交互中把"资源"呈现出来的形式就叫做它的表现层

* 状态转化(State Transfer)
前后端的交互就是一个数据和状态的变化,而我们常使用的http协议是一个无状态协议,所以,前端想要操作服务器就必须让服务器发生"状态转化",也就相当于前端让服务器的资源发生状态变化,这也就是表现层的状态转化(通过GET,POST,PUT.DELETE操作方式)


**综上,我们能得出RESTful架构的一些总结** 

* 每一个URI代表一种资源
* 客户端和服务器之间,传递这种资源的某中表现层
* 客户端通过四个HTTP动词,对服务器短资源进行操作,实现"表现层状态转化"


**个人总结:** 
我的理解是一个URI表示服务器的资源,这个URI相对来说应该是固定的,对于这个资源的状态改变都基于这个URI地址,通过不同的操作方式去改变它的状态


参考:
[理解RESTful架构 - 阮一峰](http://www.ruanyifeng.com/blog/2011/09/restful.html)






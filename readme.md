官方文档请点击：
（https://github.com/newsettle/ns4_frame/blob/master/docs/ns4_frame%E8%AF%B4%E6%98%8E%E6%96%87%E6%A1%A3.pdf）
#### 2015-11-19
 #### 版本0.3.3
1. 整个业务流程中mesageId统一
2. 日志格式做了大幅度调整
3. 增加了返回码统一
4. 增加了HTML模式自定义返回内容
5. ns_chains增加了循环死链配置的探测
6. 修改了业务层链节点可中断设置的潜在bug
7. ns_chains的配置文件增加了对desc属性的强制要求
8. 增加了一个业务链条件判断的统一实现

 #### 2015-12-04
 #### 版本0.3.4
1. 增加while循环条件判断
2. 缩减了transporter中的线程名称
3. ns_controller增加了可以指定service属性 使得可以发送特定消息到controller监控的队列 
	自己调用自己的内部服务 绕过dispatcher
4. ns_controller添加了remotecall元素 可以绕过dispatcher 直接发送消息给controller处理	
5. ns_mq增加redis连接检测 如果redis无法连接 启动会报错

 #### 2015-12-10
1. dispatcher 修复MQTemplates不是静态导致打开太多文件句柄问题

 #### 2016-01-05
1. 修改了defaultservicemessage中获取getJsonOut如果data域中没有值，构造json串不合规范的问题。

 #### 2016-01-19
1. 添加notstopException 表示抛出此异常不会中断执行链，添加这种异常是对于AOP代理无法简单的设置标记
进行处理

 #### 2016-01-26
1. 修改ns_dispatcher 增加可以获取queryString的功能

 #### 2016-02-01
1. 紧急修改ns_dispatcher 当没有参数的时候会出现报错的bug

 #### 2016-04-25
 #### 版本0.3.11-SNAPSHOT
1. ns_dispatcher添加了对https的单向认证支持

 #### 2016-09-01
 #### 版本0.3.15
1. 修改ns_common的getjsonout方法 里面的符号是中文改成英文

 #### 2018-08-28
 #### 版本0.7.0

1. 合并zhongbang 和fuqianla分支成为新的0.7.0分支

    1).支持通过dispatcher.out.sign.type 来支持是否使用签名模式，默认为关闭

    2).支持通过HttpContext.getRealRemoteIp()获得nginx真实的ip
2. dispatcher变更：

    1).调整线程调度模型，不再使用netty的默认模型，将http处理和rpc处理分开，中间通过内部队列来传递消息，rpc线程抢占并消费消息，不再主动分配消息给rpc线程
    
    2).超时时间，在等待队列中的消息，在rpc线程接收后会检测是否超过设置的时间，如果超过，直接返回超时，不再走rpc流程
    
    3).增加了文件检查请求，可以根据此测试检测程序工作线程是否正常，同时上线时可以决定是否将服务逻辑下线. 服务地址：dispatcher_check 
    
    4).增加内部队列长度设置和长度日志输出，可以依据此进行报警
    
    5). 增加dispatcher状态检测，获得内部队列长度，和工作线程数量，服务名称：dispatcher_status

3. Transporter生命周期注册过程中的顺序：
  启动：
  ```
        XmlConfigManager 配置
        DefaultBufferManager 缓存
        DefaultSenderManager 发送线程
        DefaultHandlerManager 工作线程
        DefaultFetcherManager 获取线程
  ```
  停止：
  ```
        DefaultFetcherManager 获取线程
        DefaultHandlerManager 工作线程
        DefaultSenderManager 发送线程
        DefaultBufferManager 缓存
        XmlConfigManager 配置
  ```
  
 #### 版本0.8.0
 1. redis获取数据采用超时重试模式
 2. 支持内建线程池，启动和停止
 3. 支持netty简单小应用
 
 #### 版本0.8.1
 1. 增加TCP的支持，请求和响应支持
 2. 支持跨模块测试功能
 
#### 版本0.8.2
1. 支持data中当对象的存储
2. 支持uri长服务名，模拟多级分服务
3. 单独启动本地小应用可以不依赖redis
4. 修正dispatcher命名

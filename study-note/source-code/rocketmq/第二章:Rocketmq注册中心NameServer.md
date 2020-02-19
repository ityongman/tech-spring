# Rocketmq注册中心NameServer

## 一、NameServer整体架构

​		消息中间件是一款基于**发布/订阅**机制中间件, 消息生产者(Producer)发送某一主题(Topic)的消息到消息服务器(broker), 消息服务器(broker)负责消息的持久化存储,  消息消费者(Consume)负责订阅消息的消费。

​		消费者消费消息的方式, 存在下面两种方式：

- PULL: 消息**消费者**(Consume)主动从**消息服务器**(broker)**拉取**(pull)消息
- PUSH: **消息服务器**(broker)根据订阅的信息(路由信息 route), **推送**(push)给**消费者**(push)



<font color="#0f0">**消息服务器注册及消息发送流程**</font>

- 消息服务器(Broker)向所有注册中心(NameServer)进行注册
- 消息生产者(Producer)发送消息之前,从注册中心(Nameserver)获取消息服务器(Broker)服务器地址列表
- 消息生产者(Producer)根据负载均衡算法, 选择一台消息服务器(Broker)进行消息发送



<font color="#0f0">**注册中心和消息服务器如何保持链接畅通?**</font>

- NameServer与每台消息服务器进行长链接
- NameServer通过定时任务, 每隔10s钟检测一下Broker是否还存活着
- Broker通过定时任务每隔30s向NameServer发送心跳
- 如何broker主动下线(unRegister)或者超过120s没有检测到消息服务器心跳, 会将broker从注册表剔除



<font color="#f0f">**NameServer保持高可用的方式?**</font>

​	NameServer服务器之间不会通信, 高可用的方式通过部署多台NameServer服务器实现, 虽然某些时刻NameServer可能信息不一定同步，但是对消息发送不会造成影响



​	<font color="#f0f">**讨论问题**:</font>

- 消息**生产者**(Producer)如何确定**消息**(Message)发往哪台**消息服务器**(Broker) ?
- **消息服务器**(Broker)宕机了, 消息**生产者**(Producer)如何对不工作的服务器进行感知 ?



## 二、NameServer启动流程

​	NameServer启动类: **org.apache.rocketmq.namesrv.NamesrvStartup**， 其启动代码如下:

### 2.1 进入启动流程入口

```java
public static void main(String[] args) {
        main0(args);
}

public static NamesrvController main0(String[] args) {
    try {
      //1. 创建NamesrvController实例, 并初始化该实例, 该实例为NameServer核心控制器
      NamesrvController controller = createNamesrvController(args);
      //2. 启动核心控制器
      start(controller);
      String tip = "The Name Server boot success. serializeType=" + RemotingCommand.getSerializeTypeConfigInThisServer();
      log.info(tip);
      System.out.printf("%s%n", tip);
      return controller;
    } catch (Throwable e) {
      e.printStackTrace();
      System.exit(-1);
    }

    return null;
}
```



### 2.2 创建核心控制器 NamesrvController

​		解析配置文件, 填充NameServerConfig、NettyServerConfig属性指

```java
public static NamesrvController createNamesrvController(String[] args) throws IOException, JoranException {
      	// ...省略部分代码
			  //1. 创建NameServer业务参数 实体对象
        final NamesrvConfig namesrvConfig = new NamesrvConfig();
  			//2. 创建NameServer网络参数 实体对象
        final NettyServerConfig nettyServerConfig = new NettyServerConfig();
        nettyServerConfig.setListenPort(9876); // 设置监听端口
  			//3. 解析 指定配置配置文件或启动命令中配置的选项值(-c 参数)
        if (commandLine.hasOption('c')) {
            String file = commandLine.getOptionValue('c');
            if (file != null) {
                InputStream in = new BufferedInputStream(new FileInputStream(file));
                properties = new Properties();
                properties.load(in);
                MixAll.properties2Object(properties, namesrvConfig);
                MixAll.properties2Object(properties, nettyServerConfig);

                namesrvConfig.setConfigStorePath(file);

                System.out.printf("load config properties file OK, %s%n", file);
                in.close();
            }
        }
				//4. 解析 -p 参数, 打印配置属性
        if (commandLine.hasOption('p')) {
            InternalLogger console = InternalLoggerFactory.getLogger(LoggerName.NAMESRV_CONSOLE_NAME);
            MixAll.printObjectProperties(console, namesrvConfig);
            MixAll.printObjectProperties(console, nettyServerConfig);
            System.exit(0);
        }
				//... 省略部分打印日志代码
			  //5. 创建NameServer核心控制器NameServerController
        final NamesrvController controller = new NamesrvController(namesrvConfig, nettyServerConfig);
			  //6. 记住当前处理的所有配置
        controller.getConfiguration().registerConfig(properties);

        return controller;
    }
```



​		**参数的来源有下面的两种方式**:

​	1) -c configFilePath 通过-c 命令指定配置文件位置

​	2) 使用 “-- 属性名 属性值”，--listenPort 9876



### 2.3 根据启动属性创建NameSrvController实例, 并初始化实例

​		NameSrvController控制器是NameServer核心控制器

```java
public static NamesrvController start(final NamesrvController controller) throws Exception {
				//1. 如果 核心控制器 没有创建, 抛出异常
        if (null == controller) {
            throw new IllegalArgumentException("NamesrvController is null");
        }
				//2. 初始化核心控制器(加载配置参数、创建定时任务)
        boolean initResult = controller.initialize();
        if (!initResult) {
            controller.shutdown();
            System.exit(-3);
        }
				//3. 创建JVM 钩子方法 , JVM关闭之前, 先关闭线程池资源, 及时释放资源
        Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(log, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                controller.shutdown();
                return null;
            }
        }));
				//4. 启动 核心控制器 ，以便监听Broker、消息生产者的网络请求
        controller.start();

        return controller;
    }
```

### 2.4 实例化NameSrvController信息

​		主要包含下面的内容;

- 加载KV Config Manager配置
- 创建NettyServer网络处理对象
- 创建定时任务
  - NameServer每隔 10s 检测一次Broker，移除状态不活跃的Broker
  - NameServer每隔10mins 打印KV配置信息



```java
public boolean initialize() {
				//1. 加载KV Config Manager配置信息
        this.kvConfigManager.load();
				//2. 创建nettyServer网络处理对象
        this.remotingServer = new NettyRemotingServer(this.nettyServerConfig, this.brokerHousekeepingService);
        this.remotingExecutor =
            Executors.newFixedThreadPool(nettyServerConfig.getServerWorkerThreads(), new ThreadFactoryImpl("RemotingExecutorThread_"));

        this.registerProcessor();
  
  			//3 创建定时任务
				//3.1 每隔 10s 检测一次Broker，移除状态不活跃的Broker
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                NamesrvController.this.routeInfoManager.scanNotActiveBroker();
            }
        }, 5, 10, TimeUnit.SECONDS);
				
			  //3.2 每隔10mins 打印KV配置信息
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                NamesrvController.this.kvConfigManager.printAllPeriodically();
            }
        }, 1, 10, TimeUnit.MINUTES);

       	//... 省略部分代码

        return true;
}
```



### 2.5 NamesrvConfig属性

​		NameSrvConfig主要包含下面的属性信息

```java
//1. 设置rocketmq主目录，可以通过 -Drocketmq.home.dir=path 或 配置环境变量 ROCKETMQ_HOME		
private String rocketmqHome = System.getProperty(MixAll.ROCKETMQ_HOME_PROPERTY, System.getenv(MixAll.ROCKETMQ_HOME_ENV));
//2. 路径信息，NameServer存储KV config manager配置属性的持久化路径
private String kvConfigPath = System.getProperty("user.home") + File.separator + "namesrv" + File.separator + "kvConfig.json";
//3. 默认的配置文件路径信息(不生效), 可以通过-c参数进行配置, nameServer启动时回进行解析
private String configStorePath = System.getProperty("user.home") + File.separator + "namesrv" + File.separator + "namesrv.properties";
//4. 环境名称
private String productEnvName = "center";
//5. 是否集群测试环境, 默认false
private boolean clusterTest = false;
//6. 是否支持顺序消息, 默认false
private boolean orderMessageEnable = false;

```



### 2.6 NettyServerConfig属性

​		NettyServerConfig主要包含下面的属性信息

```java
//1. nameserver默认监听端口, 初始化时会被修改为9876
private int listenPort = 8888;
//2. netty业务线程池线程个数
private int serverWorkerThreads = 8;
//3. 公共的任务线程池线程个数, Rocketmq会基于业务(消息发送、消息消费、心跳检测)创建不同的线程池
private int serverCallbackExecutorThreads = 0;
//4. IO线程池线程个数, 用于处理网络请求, 根据网络请求包, 然后转发到对应的业务线程池进行业务处理
private int serverSelectorThreads = 3;
//5. onwWay单向消息请求并发度
private int serverOnewaySemaphoreValue = 256;
//6. 发送异步消息的并发度
private int serverAsyncSemaphoreValue = 64;
//7. 网络链接的最大空闲时间，默认120s, 如多超过这个时间会将Broker从注测表剔除
private int serverChannelMaxIdleTimeSeconds = 120;
//8. Socket发送缓冲区大小
private int serverSocketSndBufSize = NettySystemConfig.socketSndbufSize;
//9. Socket接收缓冲区大小
private int serverSocketRcvBufSize = NettySystemConfig.socketRcvbufSize;
//10. ByteBuffer Allocator是否开启，默认开启
private boolean serverPooledByteBufAllocatorEnable = true;
//11. 是否启用本地 Epoll 作为选择器， linux环境推荐开启
private boolean useEpollNativeSelector = false;
```



## 三、NameServer路由信息及操作

​		NameServer主要是为消息的生产者、消费者提供关于主题Topic的路由信息, 其提供了路由下面相关的信息

- 存储路由相关的基础信息
- 管理broker节点注册、删除相关的信息



### 3.1 NameServer路由元信息(Route Info)

​		<font color="#f0f">**存储路由信息类是RouteInfoManager，其包含下面核心字段信息**</font>

```java
//1. broker缓存时间信息
private final static long BROKER_CHANNEL_EXPIRED_TIME = 1000 * 60 * 2;
//2. 读写锁, 高并发情况下需要保证写的安全性
private final ReadWriteLock lock = new ReentrantReadWriteLock();
//3. topic消息队列路由信息, 消息发送时根据路由表进行负载均衡
//topic相关缓存, key->topic, value->队列数据, 默认4个读队列，4个写队列
private final HashMap<String/* topic */, List<QueueData>> topicQueueTable;
//4. Broker基础信息, 包含brokerName、所属集群名称、主备brokr地址
//broker相关缓存，key->brokerName, value->broker信息
private final HashMap<String/* brokerName */, BrokerData> brokerAddrTable;
//5. broker集群信息, 存储集群种所有broker名字
//集群地址信息缓存, key->clusterName, value->集群包含的brokerName信息
private final HashMap<String/* clusterName */, Set<String/* brokerName */>> clusterAddrTable;
//6. broker状态信息，NameServer每次收到信息都会替换该信息
//存活的broker缓存, key->broker地址, value->存活的broker信息
private final HashMap<String/* brokerAddr */, BrokerLiveInfo> brokerLiveTable;
//7. broker上FiltrServer列表, 用于消息过滤
//服务器过滤器信息, key->broker地址, value->过滤器列表
private final HashMap<String/* brokerAddr */, List<String>/* Filter Server */> filterServerTable;

```

- BrokerName相同多台broker组成Master-Slave架构
- brokerId=0表示主服务器，brokerId>0表示从服务器
- BrokerLiveInfo中lastUpdateTimestamp表示上次心跳时间



​		<font color="#f0f">**RouteInfoManager消息数据结构**</font>

- topicQueueTable

```java
// org.apache.rocketmq.common.protocol.route.QueueData关键字段
private String brokerName;
private int readQueueNums;
private int writeQueueNums;
private int perm;
private int topicSynFlag;
```



```json
{
  "topicQueueTable":{
    "topic01":[{
      "brokerName":"broker-a",
      "readQueueNums":4,
      "writeQueueNums":4,
      "perm":6, //permition读写权限
      "topicSynFlag":0 // topic同步标志
    },{
      "brokerName":"broker-b",
      "readQueueNums":4,
      "writeQueueNums":4,
      "perm":6, //permition读写权限
      "topicSynFlag":0 // topic同步标志
    }]    ]
  }
}
```



- brokerAddrTable

```java
// org.apache.rocketmq.common.protocol.route.BrokerData 关键字段
private String cluster;
private String brokerName;
private HashMap<Long/* brokerId */, String/* broker address */> brokerAddrs;
```



```json
{
  "brokerAddrTable":{
    "broker-a":{
      "cluster":"cluster01",
      "brokerName":"broker-a",
      "brokerAddrs":{
        "0":"192.168.1.1:10000",
        "1":"192.168.1.2:10000"
      }
    },
    "broker-b":{
      "cluster":"cluster01",
      "brokerName":"broker-b",
      "brokerAddrs":{
        "0":"192.168.1.3:10000",
        "1":"192.168.1.4:10000"
      }
    }
  }
}
```



- brokerLiveTable

```java
//org.apache.rocketmq.namesrv.routeinfo.RouteInfoManager.BrokerLiveInfo
private long lastUpdateTimestamp;
private DataVersion dataVersion;
private Channel channel;
private String haServerAddr;
```



```json
{
  "brokerLiveTable":{
    "192.168.1.1:10000":{
      "lastUpdateTimestamp":1581752992722,
      "dataVersion":dataVer01,
      "channel":channel01,
      "haServerAddr":"192.168.1.2:10000"
    },
    "192.168.1.2:10000":{
      "lastUpdateTimestamp":1581752992722,
      "dataVersion":dataVer01,
      "channel":channel01,
      "haServerAddr":""
    }
  }
}
```



- clusterAddrTable

```json
{
  "clusterAddrTable":{
    "cluster01":[
      "broker-a",
      "broker-b"
    ]
  }
}
```



### 3.2 NameServer路由注册

​		<font color="#f0f">**Broker是如何进行路由注册的呢？**</font>

- NameServer通过定时任务, 每隔10s进行一次检测, 检测 brokerLiveTable 缓存中的broker时候在线
- Broker启动后会向所有的NameServer进行注册, 然后通过定时任务, 每隔一段时间(10s~60s)进行一次心跳
- 如果超过120s, broker没有进行心跳会更新brokerLiveTable缓存信息

```java
//NameServer 定时任务
this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

  @Override
  public void run() {
    NamesrvController.this.routeInfoManager.scanNotActiveBroker();
  }
}, 5, 10, TimeUnit.SECONDS);

//BrokerServer 定时任务
this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
  @Override
  public void run() {
    try {
      BrokerController.this.registerBrokerAll(true, false, brokerConfig.isForceRegister());
    } catch (Throwable e) {
      log.error("registerBrokerAll Exception", e);
    }
  }
}, 1000 * 10, Math.max(10000, Math.min(brokerConfig.getRegisterNameServerPeriod(), 60000)), TimeUnit.MILLISECONDS);

```

​		broker启动时会获取所有的nameServer地址, 通过for循环方式依次向所有的nameServer注册, 下面是代码调用流程

```java
//第一部分, BrokerController#registerBrokerAll
public synchronized void registerBrokerAll(final boolean checkOrderConfig, boolean oneway, boolean forceRegister) {
  //... 省略部分代码
  //向所有nameServer注册broker
  if (forceRegister || needRegister(this.brokerConfig.getBrokerClusterName(),
                                    this.getBrokerAddr(),
                                    this.brokerConfig.getBrokerName(),
                                    this.brokerConfig.getBrokerId(),
                                    this.brokerConfig.getRegisterBrokerTimeoutMills())) {
    //核心注册逻辑
    doRegisterBrokerAll(checkOrderConfig, oneway, topicConfigWrapper);
  }
}


//第二部分, BrokerController#doRegisterBrokerAll
private void doRegisterBrokerAll(boolean checkOrderConfig, boolean oneway,
        TopicConfigSerializeWrapper topicConfigWrapper) {
	//1. brokerOuterAPI#registerBrokerAll进行broker注册
  List<RegisterBrokerResult> registerBrokerResultList = this.brokerOuterAPI.registerBrokerAll(
    this.brokerConfig.getBrokerClusterName(),
    this.getBrokerAddr(),
    this.brokerConfig.getBrokerName(),
    this.brokerConfig.getBrokerId(),
    this.getHAServerAddr(),
    topicConfigWrapper,
    this.filterServerManager.buildNewFilterServerList(),
    oneway,
    this.brokerConfig.getRegisterBrokerTimeoutMills(),
    this.brokerConfig.isCompressedRegister());
	//2. 注册成功后, 更新masterAddr、topicConfig信息
  if (registerBrokerResultList.size() > 0) {
    RegisterBrokerResult registerBrokerResult = registerBrokerResultList.get(0);
    if (registerBrokerResult != null) {
      if (this.updateMasterHAServerAddrPeriodically && registerBrokerResult.getHaServerAddr() != null) {
        //更新masterAddr信息
        this.messageStore.updateHaMasterAddress(registerBrokerResult.getHaServerAddr());
      }
			// 更新master地址信息
      this.slaveSynchronize.setMasterAddr(registerBrokerResult.getMasterAddr());

      if (checkOrderConfig) {
			//更新topicConfig信息        	this.getTopicConfigManager().updateOrderTopicConfig(registerBrokerResult.getKvTable());
      }
            }
        }
}

//第三部分, BrokerOutApi#registerBrokerAll
public List<RegisterBrokerResult> registerBrokerAll(
  final String clusterName, // 集群名字
  final String brokerAddr, //broker地址
  final String brokerName, //broker名字
  final long brokerId, //brokerId, 等于0表示主节点, 大于0表示从节点
  final String haServerAddr, // 高可用服务器地址, 主节点地址
  final TopicConfigSerializeWrapper topicConfigWrapper,
  final List<String> filterServerList, //消息过滤服务器列表
  final boolean oneway, //是否单向发送模式
  final int timeoutMills,
  final boolean compressed) {

  final List<RegisterBrokerResult> registerBrokerResultList = Lists.newArrayList();
  //1. 获取远程所有nameServer地址
  List<String> nameServerAddressList = this.remotingClient.getNameServerAddressList();
  if (nameServerAddressList != null && nameServerAddressList.size() > 0) {
		
    //2. 创建请求头, 并设置相关属性
    final RegisterBrokerRequestHeader requestHeader = new RegisterBrokerRequestHeader();
    requestHeader.setBrokerAddr(brokerAddr);
    requestHeader.setBrokerId(brokerId);
    requestHeader.setBrokerName(brokerName);
    requestHeader.setClusterName(clusterName);
    requestHeader.setHaServerAddr(haServerAddr);
    requestHeader.setCompressed(compressed);
		
    //3. 创建请求Body，并设置属性信息
    RegisterBrokerBody requestBody = new RegisterBrokerBody();
    requestBody.setTopicConfigSerializeWrapper(topicConfigWrapper);
    requestBody.setFilterServerList(filterServerList);
    final byte[] body = requestBody.encode(compressed);
    final int bodyCrc32 = UtilAll.crc32(body);
    requestHeader.setBodyCrc32(bodyCrc32);
    //4. 通过CountDownLatch控制, 在所有的nameserver都注册后返回
    final CountDownLatch countDownLatch = new CountDownLatch(nameServerAddressList.size());
    for (final String namesrvAddr : nameServerAddressList) {
      //5. 线程池异步处理, 提升性能
      brokerOuterExecutor.execute(new Runnable() {
        @Override
        public void run() {
          try {
            RegisterBrokerResult result = registerBroker(namesrvAddr,oneway, timeoutMills,requestHeader,body);
            if (result != null) {
              registerBrokerResultList.add(result);
            }

            log.info("register broker[{}]to name server {} OK", brokerId, namesrvAddr);
          } catch (Exception e) {
            log.warn("registerBroker Exception, {}", namesrvAddr, e);
          } finally {
            // 注册成功后, 进行并发减1, countDownLatch == 0时返回
            countDownLatch.countDown();
          }
        }
      });
    }

    try {
      countDownLatch.await(timeoutMills, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
    }
  }

  return registerBrokerResultList;
}

//第四部分, BrokerOutApi#registerBroker
private RegisterBrokerResult registerBroker(
  final String namesrvAddr,
  final boolean oneway,
  final int timeoutMills,
  final RegisterBrokerRequestHeader requestHeader,
  final byte[] body
) throws RemotingCommandException, MQBrokerException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException,
InterruptedException {
  //1. 创建request对象, 并设置请求头、请求体信息
  RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.REGISTER_BROKER, requestHeader);
  request.setBody(body);
	
  //2. 如果是oneWay发送方式, 执行invokeOneWay进行注册
  if (oneway) {
    try {
      this.remotingClient.invokeOneway(namesrvAddr, request, timeoutMills);
    } catch (RemotingTooMuchRequestException e) {
      // Ignore
    }
    return null;
  }
	
  //3. 如果不是oneWay发送方式, 直接调用invokeSync进行注册
  RemotingCommand response = this.remotingClient.invokeSync(namesrvAddr, request, timeoutMills);
  //。。。 省略部分代码
}
```



<font color="#f0f">**NameServer是如何处理心跳的呢？**</font>

​		NameServer通过org.apache.rocketmq.namesrv.processor.DefaultRequestProcessor类进行统一处理, 根据 RequestCode 进行区分, 如果请求码是RequestCode#REGISTER_BROKER, 会将请求转发给RouteInfoManager#registerBroker进行注册

```java
public RegisterBrokerResult registerBroker(
  final String clusterName,
  final String brokerAddr,
  final String brokerName,
  final long brokerId,
  final String haServerAddr,
  final TopicConfigSerializeWrapper topicConfigWrapper,
  final List<String> filterServerList,
  final Channel channel) {
  RegisterBrokerResult result = new RegisterBrokerResult();
  try {
    try {
      //1. broker注册需要加锁, 防止并发修改RouteInfoManager路由表出现并发问题
      this.lock.writeLock().lockInterruptibly();

      Set<String> brokerNames = this.clusterAddrTable.get(clusterName);
      //判断brokrName是否存在, 如果不存在则创建，并添加到集群集合中去
      if (null == brokerNames) {
        brokerNames = new HashSet<String>();
        this.clusterAddrTable.put(clusterName, brokerNames);
      }
      brokerNames.add(brokerName);

      boolean registerFirst = false;
			//3. 维护BrokerData信息
      //3.1 从brokerAddrTable根据BrokerName获取Broker信息, 不存在则创建 registerFirst = true
      BrokerData brokerData = this.brokerAddrTable.get(brokerName);
      if (null == brokerData) {
        registerFirst = true;
        brokerData = new BrokerData(clusterName, brokerName, new HashMap<Long, String>());
        this.brokerAddrTable.put(brokerName, brokerData);
      }
      
      //3.2 如果存在则替换原的，registerFirst = false, 表示非第一次注册
      Map<Long, String> brokerAddrsMap = brokerData.getBrokerAddrs();
      Iterator<Entry<Long, String>> it = brokerAddrsMap.entrySet().iterator();
      while (it.hasNext()) {
        Entry<Long, String> item = it.next();
        if (null != brokerAddr && brokerAddr.equals(item.getValue()) && brokerId != item.getKey()) {
          it.remove();
        }
      }
      String oldAddr = brokerData.getBrokerAddrs().put(brokerId, brokerAddr);
      registerFirst = registerFirst || (null == oldAddr);
			
      //4.1 如果是主节点
      if (null != topicConfigWrapper
          && MixAll.MASTER_ID == brokerId) {
        //4.2 broker topic配置发生变化 或 第一次注册
        if (this.isBrokerTopicConfigChanged(brokerAddr, topicConfigWrapper.getDataVersion())
            || registerFirst) {
          ConcurrentMap<String, TopicConfig> tcTable =
            topicConfigWrapper.getTopicConfigTable();
          if (tcTable != null) {
            // 4.3 创建或者更新topic路由元信息, 为默认主题自动创建路由信息
            for (Map.Entry<String, TopicConfig> entry : tcTable.entrySet()) {
              this.createAndUpdateQueueData(brokerName, entry.getValue());
            }
          }
        }
      }
			
      //5. brokerLiveTable添加活跃的Broker信息
      BrokerLiveInfo prevBrokerLiveInfo = this.brokerLiveTable.put(brokerAddr,
            new BrokerLiveInfo(                                                                     							System.currentTimeMillis(),                                                                     							topicConfigWrapper.getDataVersion(),
             	channel,
             	haServerAddr));
      if (null == prevBrokerLiveInfo) {
        log.info("new broker registered, {} HAServer: {}", brokerAddr, haServerAddr);
      }
			//6. 如果过滤器表存在, 注册broker的过滤器表, 
      if (filterServerList != null) {
        if (filterServerList.isEmpty()) {
          this.filterServerTable.remove(brokerAddr);
        } else {
          this.filterServerTable.put(brokerAddr, filterServerList);
        }
      }
			//7. 如果此Broker节点为从节点, 查找对应的master节点, 并更新mastrAddr信息
      if (MixAll.MASTER_ID != brokerId) {
        String masterAddr = brokerData.getBrokerAddrs().get(MixAll.MASTER_ID);
        if (masterAddr != null) {
          BrokerLiveInfo brokerLiveInfo = this.brokerLiveTable.get(masterAddr);
          if (brokerLiveInfo != null) {
            result.setHaServerAddr(brokerLiveInfo.getHaServerAddr());
            result.setMasterAddr(masterAddr);
          }
        }
      }
    } finally {
      this.lock.writeLock().unlock();
    }
  } catch (Exception e) {
    log.error("registerBroker Exception", e);
  }

  return result;
}
```



<font color="#f0f">**createAndUpdateQueueData()创建和更新topicQueueTable信息, 根据TopicConfig创建QueueData, 然后更新topicQueueTable信息**</font>, 具体代码如下：


```java
private void createAndUpdateQueueData(final String brokerName, final TopicConfig topicConfig) {
  //1. 创建QueueData，填充相关参数信息
  QueueData queueData = new QueueData();
  queueData.setBrokerName(brokerName);
  queueData.setWriteQueueNums(topicConfig.getWriteQueueNums());
  queueData.setReadQueueNums(topicConfig.getReadQueueNums());
  queueData.setPerm(topicConfig.getPerm());
  queueData.setTopicSynFlag(topicConfig.getTopicSysFlag());
	
  //2.1 topicQueueTable查询指定topic信息是否存在, 如果不存在则创建
  List<QueueData> queueDataList = this.topicQueueTable.get(topicConfig.getTopicName());
  if (null == queueDataList) {
    queueDataList = new LinkedList<QueueData>();
    queueDataList.add(queueData);
    this.topicQueueTable.put(topicConfig.getTopicName(), queueDataList);
    log.info("new topic registered, {} {}", topicConfig.getTopicName(), queueData);
  //2.2 如果存在则更新
  } else {
    boolean addNewOne = true;

    Iterator<QueueData> it = queueDataList.iterator();
    while (it.hasNext()) {
      QueueData qd = it.next();
      if (qd.getBrokerName().equals(brokerName)) {
        if (qd.equals(queueData)) {
          addNewOne = false;
        } else {
          log.info("topic changed, {} OLD: {} NEW: {}", topicConfig.getTopicName(), qd,
                   queueData);
          it.remove();
        }
      }
    }

    if (addNewOne) {
      queueDataList.add(queueData);
    }
  }
}
```

总结: 

- Broker和NameServer保持长链接
- Broker状态信息存储在brokerLiveTable中
- NameServer接收到心跳信息后, 将会更新brokerLiveTable信息， 以及topicQueueTable、brokerAddrTable、filterServerTable信息
- 心跳更新使用了细粒度的读写所 ReadWriteLock, 保持同一时刻只处理一个broker心跳包, 多个心跳爆串行执行



### 3.3 NameServer路由删除

​		根据3.2知识点, Broker隔(10s,60s)时间向NameServer发送一个心跳包, 心跳包包含brokerId、brokerName、clusterName、filerServer列表, brokerAddr; NameServer隔10s会通过brokerLiveTable检测：broker存活状态, 如果超过120s没有收到broker的心跳， 会提出该broker。所以broker的剔除通过下面的方式：

- 两次心跳间隔超过120s
- broker主动下线unRegister



​		剔除broker后会更新topicQueueTable、brokerAddrTable、brokerSrvTable、brokerLiveTable、filterServerTable

​		读者应该还记得NameServer启动时下面的代码，其中scanNotActiveBroker就是broker剔除的逻辑代码, 每隔10s中会扫描一次

```java
this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
  @Override
  public void run() {
    NamesrvController.this.routeInfoManager.scanNotActiveBroker();
  }
}, 5, 10, TimeUnit.SECONDS);
```

​		<font color="#f0f">**剔除不活跃broker节点入口**</font>

```java
public void scanNotActiveBroker() {
  //1. 获取brokerLiveTable中所有存活的broker
  Iterator<Entry<String, BrokerLiveInfo>> it = this.brokerLiveTable.entrySet().iterator();
  while (it.hasNext()) {
    Entry<String, BrokerLiveInfo> next = it.next();
    long last = next.getValue().getLastUpdateTimestamp();
    //2. 上次心跳和距离这次间隔超过120s，进行剔除
    if ((last + BROKER_CHANNEL_EXPIRED_TIME) < System.currentTimeMillis()) {
      RemotingUtil.closeChannel(next.getValue().getChannel());
      it.remove();
      log.warn("The broker channel expired, {} {}ms", next.getKey(), BROKER_CHANNEL_EXPIRED_TIME);
      //3. 关闭channel，删除该broker相关的路由信息
      this.onChannelDestroy(next.getKey(), next.getValue().getChannel());
    }
  }
}
```

​		<font color="#f0f">**剔除topicQueueTable、brokerAddrTable、clusterSrvTable、filterServerTable中对应brokerAddr节点信息**</font>

```java
public void onChannelDestroy(String remoteAddr, Channel channel) {
  String brokerAddrFound = null;
  //第一部分，校验channel对应的节点信息是否在brokerLiveTable中, 默认没有
  if (channel != null) {
    try {
      try {
        this.lock.readLock().lockInterruptibly();
        Iterator<Entry<String, BrokerLiveInfo>> itBrokerLiveTable =
          this.brokerLiveTable.entrySet().iterator();
        while (itBrokerLiveTable.hasNext()) {
          Entry<String, BrokerLiveInfo> entry = itBrokerLiveTable.next();
          if (entry.getValue().getChannel() == channel) {
            // 如果在brokerLiveTable查询到channel，则brokerLiveTable != null
            brokerAddrFound = entry.getKey();
            break;
          }
        }
      } finally {
        this.lock.readLock().unlock();
      }
    } catch (Exception e) {
      log.error("onChannelDestroy Exception", e);
    }
  }

  if (null == brokerAddrFound) {
    brokerAddrFound = remoteAddr;
  } else {
    log.info("the broker's channel destroyed, {}, clean it's data structure at once", brokerAddrFound);
  }
	
  if (brokerAddrFound != null && brokerAddrFound.length() > 0) {

    try {
      try {
        //第二部分, 提出brokerLiveTable中remoteAddr对应的信息
        // 申请写锁, 根据 brokerAddress 从brokerLiveTable、filterServerTable、topicQueueTable删除信息
        this.lock.writeLock().lockInterruptibly();
        this.brokerLiveTable.remove(brokerAddrFound);
        this.filterServerTable.remove(brokerAddrFound);
        String brokerNameFound = null;
        boolean removeBrokerName = false;
        //NOte: 遍历HashMap<String/* brokerName */, BrokerData> brokerAddrTable，从
        //brokerData中的HashMap<Long/* brokerId */, String/* broker address */> brokerAddrs
				//找到具体的brokr, 从BrokerData中删除, 如果删除后brokrData中不再包含其它broker，从brokerAddrTable删除
        Iterator<Entry<String, BrokerData>> itBrokerAddrTable =
          this.brokerAddrTable.entrySet().iterator();
        while (itBrokerAddrTable.hasNext() && (null == brokerNameFound)) {
          BrokerData brokerData = itBrokerAddrTable.next().getValue();

          Iterator<Entry<Long, String>> it = brokerData.getBrokerAddrs().entrySet().iterator();
          while (it.hasNext()) {
            Entry<Long, String> entry = it.next();
            Long brokerId = entry.getKey();
            String brokerAddr = entry.getValue();
            //2.1 brokerAddr 与 brokerAddrFound相等时, 从brokerData剔除
            if (brokerAddr.equals(brokerAddrFound)) {
              brokerNameFound = brokerData.getBrokerName();
              it.remove();
              log.info("remove brokerAddr[{}, {}] from brokerAddrTable, because channel destroyed",
                       brokerId, brokerAddr);
              break;
            }
          }
					//2.2 如果brokerata已经剔除完, 从brokerAddrTable剔除
          if (brokerData.getBrokerAddrs().isEmpty()) {
            removeBrokerName = true;
            itBrokerAddrTable.remove();
            log.info("remove brokerName[{}] from brokerAddrTable, because channel destroyed",
                     brokerData.getBrokerName());
          }
        }
				
        //第三部分, 如果brokerNameFound存在, 并且删除removeBrokerName, 执行下面的逻辑
        if (brokerNameFound != null && removeBrokerName) {
          //NOTE: 根据brokerName从clusterAddrTable找到broker, 并从中移除， 如果移除后, 集群中不再包含任何其它broker，将集群从clusterAddrTable删除
          //3.1 获取所有的集群地址列表
          Iterator<Entry<String, Set<String>>> it = this.clusterAddrTable.entrySet().iterator();
          while (it.hasNext()) {
            Entry<String, Set<String>> entry = it.next();
            String clusterName = entry.getKey();
            Set<String> brokerNames = entry.getValue();
            //3.2 从clusterAddrTable中Set集合中删除需要剔除的brokerNameFound
            boolean removed = brokerNames.remove(brokerNameFound);
            if (removed) {
              log.info("remove brokerName[{}], clusterName[{}] from clusterAddrTable, because channel destroyed",
                       brokerNameFound, clusterName);
							//3.3 如果Set集合为空，从clusterAddrTable完全删除
              if (brokerNames.isEmpty()) {
                log.info("remove the clusterName[{}] from clusterAddrTable, because channel destroyed and no broker in this cluster",
                         clusterName);
                it.remove();
              }

              break;
            }
          }
        }
				//第四部分, 如果removeBrokerName=true执行下面的逻辑
        if (removeBrokerName) {
          //NOTE：根据brokerName, 遍历所有主题队列topicQueueTable, 如果主题中包含当前broker队列, 则移除, 如果队列中只包含该broker, 将该topic从topicQueueTable移除
          //4.1 获取所有的topicQueueTable信息
          Iterator<Entry<String, List<QueueData>>> itTopicQueueTable =
            this.topicQueueTable.entrySet().iterator();
          while (itTopicQueueTable.hasNext()) {
            Entry<String, List<QueueData>> entry = itTopicQueueTable.next();
            String topic = entry.getKey();
            List<QueueData> queueDataList = entry.getValue();

            Iterator<QueueData> itQueueData = queueDataList.iterator();
            ////4.2 便利整个队列, 如果brokerNameFound与队列信息匹配, 从队列中删除
            while (itQueueData.hasNext()) {
              QueueData queueData = itQueueData.next();
              if (queueData.getBrokerName().equals(brokerNameFound)) {
                itQueueData.remove();
                log.info("remove topic[{} {}], from topicQueueTable, because channel destroyed",
                         topic, queueData);
              }
            }
						//4.3 如果队列为空, 从HashMap中删除相关信息
            if (queueDataList.isEmpty()) {
              itTopicQueueTable.remove();
              log.info("remove topic[{}] all queue, from topicQueueTable, because channel destroyed",
                       topic);
            }
          }
        }
      } finally {
        // 释放写锁
        this.lock.writeLock().unlock();
      }
    } catch (Exception e) {
      log.error("onChannelDestroy Exception", e);
    }
  }
}
```



### 3.4 NameServer路由发现

​		从前面几小节的分析, Rocketmq路由发现是非实时的, Topic路由信息的变化后, NameServer不会主动发送信息给client, 而是由client发送RequestCode为GET_ROUTEINTO_BY_TOPIC指令请求到NameServer进行信息查询, 具体逻辑代码如下:

```java
public RemotingCommand getRouteInfoByTopic(ChannelHandlerContext ctx,
                                           RemotingCommand request) throws RemotingCommandException {
  final RemotingCommand response = RemotingCommand.createResponseCommand(null);
  final GetRouteInfoRequestHeader requestHeader =
    (GetRouteInfoRequestHeader) request.decodeCommandCustomHeader(GetRouteInfoRequestHeader.class);
	
  //核心处理逻辑，从RouteInfoManager获取相关数据
  TopicRouteData topicRouteData = this.namesrvController.getRouteInfoManager().pickupTopicRouteData(requestHeader.getTopic());
	// 如果找到主题的路由信息
  if (topicRouteData != null) {
    // 如果主题为顺序信息
    if (this.namesrvController.getNamesrvConfig().isOrderMessageEnable()) {
      String orderTopicConf =
        this.namesrvController.getKvConfigManager().getKVConfig(NamesrvUtil.NAMESPACE_ORDER_TOPIC_CONFIG,
                                                                requestHeader.getTopic());
      topicRouteData.setOrderTopicConf(orderTopicConf);
    }

    byte[] content = topicRouteData.encode();
    response.setBody(content);
    response.setCode(ResponseCode.SUCCESS);
    response.setRemark(null);
    return response;
  }

  response.setCode(ResponseCode.TOPIC_NOT_EXIST);
  response.setRemark("No topic route info in name server for the topic: " + requestHeader.getTopic()
                     + FAQUrl.suggestTodo(FAQUrl.APPLY_TOPIC_URL));
  return response;
}
```

- 执行RouteInfoManager方法，从路由表topicQueueTable、brokerAddrTable、filterServerTable获取相关信息, 分别填充List<QueueData>、List<BrokerData>、filterServer地址列表
- 如果找到主题的路由信息, 如果主题为顺序信息, 则从NameServer的Kv config Manager获取顺序消息相关的配置信息



​	TopicRouteData相关核心字段如下

```java
private String orderTopicConf; // 顺序消息配置, 来自NameServer kv config manager
private List<QueueData> queueDatas; // topic队列元数据
private List<BrokerData> brokerDatas; // brokerAddr基础元数据
private HashMap<String/* brokerAddr */, List<String>/* Filter Server */> filterServerTable //broker过滤服务器
```


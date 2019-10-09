## 本工程用于熟悉Redis常用的技术
### 一、相关案例
- 红包兑换码案例, 熟悉Redis在秒杀业务场景中的使用方式
- <font color="#ff00ff">发布订阅案例, 熟悉Redis在实时性高、可靠性不高时的使用方式</font>
    <br/>使用场景:
    - 微博, 粉丝对用户的订阅
    - 新闻, 用户对频道进行订阅, 新闻发布时, 订阅这可以获得更新
    - 电商, 对感兴趣的商品进行订阅, 有促销情况时会收到消息
<br/>缺点:
    - 消息无法持久, 存在丢失风险
    - 没有ACK机制, 消息消费情况无法确认
- Hyperloglog使用案例, 熟悉Redis在数据统计发面的使用案例, 比如: PV、UV等
- Dao层数据缓存案例, 熟悉Redis在数据缓存方面的使用案例

### 二、案例步骤讲解
#### 2.1 创建spring boot工程, 引入web, spring-boot-starter-data-redis、redis.clients jar包
    NOTE: redis.clients jar包在spring boot 2.0 之后需要单独引入
#### 2.2 创建RedisConfig配置类, 创建需要使用的JedisConnectionFactory , redisTemplate, RedisMessageListenerContainer、ChannelTopic相关Bean对象
    NOTE: RedisMessageListenerContainer bean需要指定topic名字, 限定接收哪些topic对象
#### 2.3 创建ConsumerListener监听器对象, 实现对业务处理逻辑
#### 2.4 创建PubSubController对象用于发送生产者消息

    NOTE: 
    1. 具体实现逻辑, 可以根据上面的步骤查看对应的代码逻辑
    2. spring-redis 使用RedisMessageListenerContainer进行消息监听, 客户程序需要自己实现MessageListener, 并以指定的topic
    注册到RedisMessageListenerContainer, 如果指定Topic有消息, RedisMessageListenerContainer会通知该MessageListener

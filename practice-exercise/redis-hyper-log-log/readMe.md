## 本工程用于熟悉Redis常用的技术
### 一、相关案例
- 红包兑换码案例, 熟悉Redis在秒杀业务场景中的使用方式
- 发布订阅案例, 熟悉Redis在实时性高、可靠性不高时的使用方式
    <br/>使用场景:
    - 微博, 粉丝对用户的订阅
    - 新闻, 用户对频道进行订阅, 新闻发布时, 订阅这可以获得更新
    - 电商, 对感兴趣的商品进行订阅, 有促销情况时会收到消息
<br/>缺点:
    - 消息无法持久, 存在丢失风险
    - 没有ACK机制, 消息消费情况无法确认
- <font color="#ff00ff"> Hyperloglog使用案例, 熟悉Redis在数据统计发面的使用案例, 比如: PV、UV等 </font>
- Dao层数据缓存案例, 熟悉Redis在数据缓存方面的使用案例

### 二、案例步骤讲解
#### 2.1 创建spring-boot工程, 并引入需要的jar包, 具体查看pom文件
#### 2.2 统计PV、UV之类数据落库(redis)方案
     2.2.1 具体请求接口中(比如: queryArticleById), 添加数据统计的逻辑; 缺点: 会与原来的业务耦合在一起
     2.2.2 定义注解和切面信息, 对需要统计数据的接口添加自定义注解; 缺点: 实现比较复杂
     2.2.3 定义需要 @Around 注解修饰的方法, 并在方法中处理对应的逻辑
     2.2.4 声明Controller接口, 实现实际业务需求
#### 2.3 HyperLogLog常用命令
    pfadd、pfcount、pfmerge

NOTE: 可以添加定时任务, 用于处理数据同步到数据库中
https://zhuanlan.zhihu.com/p/58358264
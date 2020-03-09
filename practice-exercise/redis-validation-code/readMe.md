## 本工程用于熟悉Redis常用的技术
### 一、相关案例
- <font color="#ff00ff">红包兑换码案例, 熟悉Redis在秒杀业务场景中的使用方式</font>
- 发布订阅案例, 熟悉Redis在实时性高、可靠性不高时的使用方式
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

### 三、未完事项 TODO
- RedisTemplate直接使用的默认配置, 补充自定义配置
- 兑换码功能时间缓存有效期目前直接写死, 需要修改, 按照规则进行缓存
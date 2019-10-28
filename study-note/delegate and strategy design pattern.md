## 委派模式和策略模式

### 一、定义和要求

#### 1.1 定义

##### 1.1.1 委派模式

​	**负责任务的调度和分配**, 和代理模式有些相似, 可以看作是特殊情况下静态代理的全权代理, 代理模式注重过程, 委派模式注重结果。

​	**NOTE: 不属于23中设计模式之一, 属于行为型模式**

##### 1.1.2 策略模式

​	是指定义了算法家族，并分别封装起来, 算法之间可以相互替换, 并且算法的变化不会影响到使用算法的用户

#### 1.2 使用场景

##### 1.2.1 委派模式

​	DispatcherServlet、BeanDefinitionParseDelegate

##### 1.2.2 策略模式

​	java.util.Comparator, TreeMap(Comparator<? super K> comparator), Arrays.mergeSort()



#### 1.3 优缺点

#####1.3.1 委派模式

- 精简程序逻辑, 提升代码的可读性
- 消除程序中大量的 if...else... 和 switch语句
- 理解策略模式应用场景后, 提高算法的保密性和安全性

##### 1.3.2 策略模式

优点

- 符合开闭原则

- 可以避免多分枝的if...else... 和 switch语句
- 可以提高算法的保密性和安全性

缺点

- 客户必须知道所有的策略, 并且自行决定使用哪一个类
- 代码中会存在很多的策略类, 增加维护难度

### 二、委派模式

​	这里以公司Boss下达命令场景来熟悉委派模式用法 （除了Boss, 还存在Leader、worker角色）

#### 2.1 类结构图

![](/Users/shedunze/workspace/sourceWorkspace/tech-spring/delegate-strategy-design-pattern/Boss.png)

####2.2 实践用例

##### 2.2.1 创建Boss

```java
public class Boss {
    public void doWork(Leader leader, String cmd){
        leader.doWork(cmd);
    }
}
```

##### 2.2.2 创建IWorker

```java
public interface IWorker {
    void doWork(String cmd);
}
```

##### 2.2.3 创建Leader

和代理模式中代理角色有些像, 注意区别, 代理模式注重过程, 委派模式注重结果

```java
public class Leader implements IWorker {

    private Map<String, IWorker> workers = new HashMap<>();

    public Leader() {
        workers.put("java", new WorkerA());
        workers.put("php", new WorkerB());
        workers.put("go", new WorkerC());
    }

    public void doWork(String cmd) {
        IWorker worker = workers.get(cmd);
        worker.doWork(cmd);
    }
}
```

##### 2.2.4 创建Worker

- WorkerA

```java
public class WorkerA implements IWorker {
    @Override
    public void doWork(String cmd) {
        System.out.println("WorkerA do work " + cmd);
    }
}
```

- WorkerB

```java
public class WorkerB implements IWorker {
    @Override
    public void doWork(String cmd) {
        System.out.println("WorkerA do work " + cmd);
    }
}
```

- WorkerC

```java
public class WorkerC implements IWorker {
    @Override
    public void doWork(String cmd) {
        System.out.println("WorkerA do work " + cmd);
    }
}
```

##### 2.2.5 创建测试类

```java
public class DelegateWorkerTest {
    public static void main(String[] args) {
        Boss boss = new Boss();
        Leader leader = new Leader();

        boss.doWork(leader, "java");
    }
}
```

#### 2.3 DispatcherServlet简单实现

​	具体逻辑代码可以参考git地址, https://github.com/ityongman/tech-spring/tree/master/delegate-strategy-design-pattern

```java
public class UserController {
    //RequestMapping("queryUserById")
    public String queryUserById(String id) {
        return "UserController queryUserById --> " + id ;
    }
}

public class TradeController {
    //RequestMapping("queryOrderById")
    public String queryOrderById(String id) {
        return "TradeController queryOrderById --> " + id ;
    }
}

public class ItemController {

    //RequestMapping("queryItemById")
    public String queryItemById(String id) {
        return "ItemController queryItemById --> " + id ;
    }
}


public class DispatcherServlet {

    private static Map<String , Object> controllers = new HashMap<>();
    static {
        controllers.put("queryUserById", new UserController());
        controllers.put("queryOrderById", new TradeController());
        controllers.put("queryItemById", new ItemController());
    }


//    public String doDispatcher(HttpServletRequest req , HttpServletResponse resp){
//        String reqUri = req.getRequestURI();
//        String id = req.getParameter("id");
    public String doDispatcher(String reqUri , String id){
        Object objCtl = controllers.get(reqUri);
        String ret = "SUCCESS" ;
        if (objCtl instanceof UserController) {
            UserController userCtl = (UserController) objCtl;
            ret = userCtl.queryUserById(id);
        } else if(objCtl instanceof TradeController) {
            TradeController tradeCtl = (TradeController) objCtl;
            ret = tradeCtl.queryOrderById(id);
        } else if(objCtl instanceof  ItemController) {
            ItemController itemCtl = (ItemController) objCtl;
            ret = itemCtl.queryItemById(id);
        } else {
            ret = "Request Error , 404 not found !!!" ;
        }
        return ret;
    }
}
```

### 三、策略模式

#### 3.1 类结构图

![](/Users/shedunze/workspace/sourceWorkspace/tech-spring/delegate-strategy-design-pattern/PromotionStrategy.png)

#### 3.2 实践用例

##### 3.2.1 创建营销策略算法

- 策略核心

```java
/**
 * 这里以商品购买 优惠方式举例策略
 */
public interface IPromotion {
    double getBalance();
}

public abstract class AbstractPromotion implements IPromotion{

    public void pay(Order order){
        double balance = getBalance();
        double amount = order.getAmount();

        if(balance < amount){
            System.out.println("account balance not enough");
        } else {
            System.out.println("account balance enough, balance = "+balance+" , amount = " + amount);
        }
    }
}
```

- 打折Discount

```java
public class DiscountPromotion extends AbstractPromotion {

    @Override
    public double getBalance() {
        System.out.println("DiscountPromotion getBalance ...");
        return 200;
    }
}
```

- 优惠券Coupon

```java
public class CouponPromotion extends AbstractPromotion {
    @Override
    public double getBalance() {
        System.out.println("CouponPromotion getBalance ...");
        return 250;
    }
}
```

- 无优惠NO

```java
public class NoPromotion extends AbstractPromotion {
    @Override
    public double getBalance() {
        System.out.println("NoPromotion getBalance ...");
        return 100;
    }
}
```

##### 3.2.2 订单实体

```java
// 省略getter、setter方法
public class Order {
    private String id ;
    private double amount ;
    private String orderId ;
}
```

##### 3.2.3 创建获取营销算法的策略类

```java
/**
 * 策略模式
 * 1. 通过工厂方式创建促销活动
 * 2. 通过单例模式(容器式), 保证所有活动唯一, 不可变
 */
public class PromotionStrategy {
    //1. 构造方法私有
    private PromotionStrategy() {}

    //2. 单例容器
    private final static Map<String, AbstractPromotion> promotions = new HashMap<>();

    static {
        promotions.put(PromotionStrategyKey.COUPON, new CouponPromotion());
        promotions.put(PromotionStrategyKey.DISCOUNT, new DiscountPromotion());
        promotions.put(PromotionStrategyKey.NO, new NoPromotion());
    }

    //3. 全局访问接口
    public static AbstractPromotion getPromotion(String promotionMethod) {
        AbstractPromotion promotion = promotions.get(promotionMethod);

        return promotion == null ? promotions.get(PromotionStrategyKey.NO) : promotion ;
    }


    // 为了方便, 这里使用String
    public class PromotionStrategyKey{
        public static final String COUPON = "COUPON";
        public static final String DISCOUNT = "DISCOUNT";
        public static final String NO = "NO";
    }
}
```

##### 3.2.4 创建活动类

```java
/**
 * 通过代理模式对活动进行处理, 并达到对目标方法的增强作用
 */
public class PromotionActivity {
    private AbstractPromotion promotion ;

    public PromotionActivity(AbstractPromotion promotion){
        this.promotion = promotion ;
    }


    void pay(Order order) {
        System.out.println("PromotionActivity pay ...");
        promotion.pay(order);
    }
}
```

##### 3.2.5 创建测试类

```java
public class PromotionActityTest {
    public static void main(String[] args) {
        Order order = new Order();

        order.setAmount(200);
        order.setId("10086");
        order.setOrderId("orderId:1000000");

        PromotionActivity activity =
                new PromotionActivity(PromotionStrategy.getPromotion(PromotionStrategy.PromotionStrategyKey.NO));

        activity.pay(order);
    }
}
```


## 设计模式之代理模式

### 一、定义及要求

#### 1.1 定义

​	<font color="#ff00">代理模式(Proxy Pattern)是指为其它对象提供一种代理, 以便控制对这个对象的访问;代理对象(Proxy Object)在客户端和目标对象之间起到中介作用</font>

​	<b>NOTE: 属于结构型设计模式</b>

#### 1.2 分类

​	静态代理、动态代理

- 静态代理, 显式声明被代理对象

#### 1.3 使用场景

​	Aop切面编程、日志监控

#### 1.4 优缺点

##### 1.4.1 优点

- 代理模式能够将代理对像与真实被调用目标对象分离, 起到保护目标对象的目的
- 一定程度上降低了系统的耦合度, 易于扩展
- 增强目标对象

##### 1.4.2 缺点

- 代理模式会造成系统设计中类的数目增加
- 调用者和目标对象之间增加代理对象, 会造成请求处理速度变慢
- 增加了系统的复杂度

### 二、静态代理

#### 2.1 缺陷

​		<font color="#aaee">不符合开闭原则, 代理的目标对象发生变化时, 代理对象也需要跟着 发生变化</font>

#### 2.2 类结构图

![](/Users/shedunze/workspace/sourceWorkspace/tech-spring/design-pattern-proxy/OrderServiceStaticProxy.png)

#### 2.3 实践用例

​	这里以分布式场景下, 分库分表、数据源的切换为例来讲解静态代理的用法

##### 2.3.1 创建Order对象

```java
// 这里省略getter、setter方法
public class Order {
    private String id ;
    private Long createTime ;
    private Object orderInfo ;
}
```

##### 2.3.2 创建Dao访问层

```java
//注意没有引入 注解相关元素
public class OrderDao {
    public int insert(Order order){
        System.out.println("OrderDao insert order to DB");
        return 1 ;
    }
}
```

##### 2.3.3 创建service对象

```java
public interface IOrderService {
    int insert(Order order);
}
```

```java
public class OrderService implements IOrderService {
    private OrderDao orderDao ;

    public OrderService(){
        orderDao = new OrderDao();
    }

    public int insert(Order order) {
        System.out.println("OrderService call OrderDao interface");
        return orderDao.insert(order);
    }
}
```

##### 2.3.4 创建数据源对象

```java
public class DbDatasource {
    public static final String DEFAULT_DB = null ;

    //ThreadLocal 单例模式
    //1.
    private DbDatasource (){};

    //2. String 数据源名称
    private final static ThreadLocal<String> dbs = new ThreadLocal<>();

    //3.
    public static String getDB() {
        return dbs.get();
    }
    //
    public static void restore(){
        dbs.set(DEFAULT_DB);
    }

    public static void setDB(String db){
        dbs.set(db);
    }

}
```

##### 2.3.5 创建静态代理对象

```java
/**
 * 静态代理类
 */
public class OrderServiceStaticProxy implements IOrderService {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private IOrderService orderService ;

    public OrderServiceStaticProxy(IOrderService orderService){
        this.orderService = orderService ;
    }

    @Override
    public int insert(Order order) {
        before();

        Long createTime = order.getCreateTime();
        String dbRouter = dateFormat.format(new Date(createTime));

        System.out.println("static proxy router datasource [DB_" + dbRouter +"] to process");

        DbDatasource.setDB(dbRouter);
        int ret = orderService.insert(order);
//        DbDatasource.restore();

        after();

        return ret;
    }

    private void before() {
        System.out.println("static proxy before process");
    }

    private void after() {
        System.out.println("static proxy after process");
    }
}

```

##### 2.3.6 测试用例

```java
public class OrderServiceStaticProxyTest {
    public static void main(String[] args) {
        //1.
        Order order = new Order();
        order.setCreateTime(System.currentTimeMillis());
        order.setId("10086");
        order.setOrderInfo(new Object());

        //2.
        IOrderService orderService = new OrderServiceStaticProxy(new OrderService());
        orderService.insert(order);
    }
}
```

#### 2.4 总结

​	在上面分库分表、切换数据源的案例中, 除了OrderServiceStaticProxy使用了静态代理, OrderService -> OrderDao也属于静态代理

### 三、动态代理

#### 3.1 优势

​	<font color="#aa00">相对于静态代理存在的问题, 动态代理在代理的目标对象发生变化时, 代理对象不用发生改变</font>

#### 3.2 JDK 动态代理

##### 3.2.1 实现原理

- 拿到被代理类的引用, 并通过反射机制获取它的所有接口
- JDKProxy会重新生成一个新类, 这个新类实现了被代理对象所有接口方法(注: jdk动态代理必须实现接口)
- 动态生成Java代码, 把增强逻辑加入到新生成的代码中
- 编译生成新Java代码class文件
- 加载并运行新class文件, 得到的类属于全新的类
- 通过 InvocationHandler.invoke(proxy, method, args) 实现对目标方法的增强

​	<font color="#aa00">NOTE: 反射获取代理类接口信息 -> 基于接口生成新类, 复写接口方法 -> 动态生成java代码 -> 编译成class文件 -> 加载class文件, 得到全新的类</font>

##### 3.2.2 实现方式

```java
/**
	前面业务逻辑实现
	1. 代理对象必须实现 InvocationHandler 接口
	2. 生成、获取目标代理对象接口 getInstance(Object proxyObj)
	3. 接口调用方法 invoke(Object proxy, Method method, Object[] args)
	4. 需要在invoke方法中增强的逻辑 before()、 after()
*/
public class OrderServiceDynamicProxy implements InvocationHandler {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     *  对OrderService insert(Order order)方法进行代理举例
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    private Object proxyObj ;

    public Object getInstance(Object proxyObj) {
        this.proxyObj = proxyObj ;
        Class<?> clazz = proxyObj.getClass();

        return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before(args[0]);

        Object invokeObj = method.invoke(proxyObj, args);

        after();
        return invokeObj;
    }

    private void after() {
        DbDatasource.restore();
    }

    private void before(Object obj) {
        System.out.println("dynamic proxy before");
        try {
            Long createTime = (Long) obj.getClass().getMethod("getCreateTime").invoke(obj);
            String dbRouter = dateFormat.format(new Date(createTime));

            System.out.println("static proxy router datasource [DB_" + dbRouter +"] to process");
            DbDatasource.setDB(dbRouter);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
```

##### 3.2.3 测试逻辑

```java
public class OrderServiceDynamicProxyTest {
    public static void main(String[] args) {
        //1.
        Order order = new Order();
        order.setCreateTime(System.currentTimeMillis());
        order.setId("10086");
        order.setOrderInfo(new Object());

        //2.
        IOrderService orderService = (IOrderService) new OrderServiceDynamicProxy().getInstance(new OrderService());
        orderService.insert(order);
    }
}
```

#### 3.3 CGlib动态代理

##### 3.3.1 实现原理

- 拿到被代理类的引用, 通过继承的方式复写父类的方法
- 编译生成新Java代码class文件
- 加载并运行新class文件, 得到的类属于全新的类
- 通过  MethodInterceptor.intercept(obj, method, objs, methodProxy) 方法实现对目标方法的增强

##### 3.3.2 实现方式

```java
public class OrderServiceCglibProxy implements MethodInterceptor {

    public Object getInstance(Class<?> clazz) {
        //1. 类似于JDK Proxy中 Proxy.newProxyInstance 作用
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);

        return enhancer.create() ;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        before();

        // 子类重写了父类的方法, 所以这里调用invokerSuper
        Object invokeObj = methodProxy.invokeSuper(o, objects);

        after();
        return invokeObj;
    }

    private void after() {
        System.out.println("cglib proxy after");
    }

    private void before() {
        System.out.println("cglib proxy before");
    }
}
/**
	需要引入下面的jar
  <dependency>
     <groupId>cglib</groupId>
     <artifactId>cglib-nodep</artifactId>
     <version>2.2</version>
  </dependency>
*/

```

##### 3.3.3 测试逻辑

```java
public class OrderServiceCglibProxyTest {
    public static void main(String[] args) {
				// 生成Cglib代理的对象
      	// 会在当前过程的根目录生成cglib_proxy目录, 里面保存了详细的class文件
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY,"cglib_proxy");

        OrderService orderService = (OrderService) new OrderServiceCglibProxy().getInstance(OrderService.class);

        Order order = new Order();
        order.setCreateTime(System.currentTimeMillis());

        orderService.insert(order);
    }
}
```

##### 3.3.5 cglib生成的文件

```java
//为了减省篇幅, 下面部分内容已删除, 具体信息可以在cglib_proxy目录中查找
public class OrderService$$EnhancerByCGLIB$$ba0750bd extends OrderService implements Factory {
    static void CGLIB$STATICHOOK1() {
        CGLIB$THREAD_CALLBACKS = new ThreadLocal();
        CGLIB$emptyArgs = new Object[0];
        Class var0 = Class.forName("com.ityongman.service.impl.OrderService$$EnhancerByCGLIB$$ba0750bd");
        CGLIB$insert$0$Proxy = MethodProxy.create(var1, var0, "(Lcom/ityongman/entity/Order;)I", "insert", "CGLIB$insert$0");
    }

    final int CGLIB$insert$0(Order var1) {
        return super.insert(var1);
    }

    public final int insert(Order var1) {
        MethodInterceptor var10000 = this.CGLIB$CALLBACK_0;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_0;
        }

        if (var10000 != null) {
            Object var2 = var10000.intercept(this, CGLIB$insert$0$Method, new Object[]{var1}, CGLIB$insert$0$Proxy);
            return var2 == null ? 0 : ((Number)var2).intValue();
        } else {
            return super.insert(var1);
        }
    }

    final void CGLIB$finalize$1() throws Throwable {
        super.finalize();
    }

    public static MethodProxy CGLIB$findMethodProxy(Signature var0) {
        String var10000 = var0.toString();
        switch(var10000.hashCode()) {
        case 1748382207:
            if (var10000.equals("insert(Lcom/ityongman/entity/Order;)I")) {
                return CGLIB$insert$0$Proxy;
            }
            break;
        }

        return null;
    }

    static {
        CGLIB$STATICHOOK1();
    }
}

```

#### 3.4 JDKProxy 和 Cglib比较

- 目的: 都是生成新的类, 对目标方法实现增强
  - JDK采用反射, 实现被代理对象的接口方式实现
  - Cglib覆盖父类方法方式实现
- 代理对象复杂度不一样
  - JDK Proxy对使用者而言必须要有一个接口, 目标类相对复杂
  - Cglib可以代理任意一个普通类, 没有任何特殊要求
- 都是在运行期生成字节码, 但是生成效率不一样
  - JDK Proxy是直接写Class字节码, 生成效率比cglib高
  - Cglib使用ASM写字节码, 代理生成更复杂, 生成效率比JDK低
- 生成的代理逻辑复杂度不一样
  - 生产的代理逻辑更复杂, 但生产了一个包含所有方法逻辑的FastClass类, 调用效率更高, 不需要反射
  - JDK Proxy生成代理逻辑简单, 但是每次都是通过反射机制动态调用, 执行效率相对要低

NOTE: 

- cglib不能代理final修饰的类
- 在没有配置**aspectj-autoproxy proxy-target-class="true"**时， Spring是依据代理对象是否实现了接口来决定使用 jdk proxy 还是cglib
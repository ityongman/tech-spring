## 设计模式之包装模式

### 一、定义和要求

#### 1.1 定义

​	装饰器模式(Decorator Pattern) 是指在不改变原有对象的基础上, 给该对象附加额外功能, 提供了比继承(extends)更有弹性的替代方案

​	NOTE: 属于结构性模式

#### 1.2 使用场景

- 用于扩展一个类的功能或者添加一些附加职责

- 动态的给一个对象添加功能(该功能也可以动态的取消)

  InputStream -> FilterInputStream -> BufferedInputStream、TransactionAwareCacheDecorator、

  org.apache.ibatis.cache.decorators.* mybatis关于Cache包装器类


#### 1.3 优点和缺点

有点

- 比继承(extends)更有弹性、灵活, 不改变原有逻辑的情况下, 可以动态的给一个对象扩展功能
- 通过不同的装饰器类, 可以实现不同的效果(Mybatis的Cache装饰器类)
- 遵循开闭原则

缺点

- 会增加过多的代码和类, 会使程序变得复杂
- 多层包装会更复杂, 不易理解

### 二、实践用例

​	这里还是用登陆方式变化, 来说明包装器的使用方式(传统登陆密码方式 -> QQ、Wechat第三方登陆方式支持)

NOTE: 装饰器模式最本质的特征是将原有功能类的附加功能抽离出来, 简化原有逻辑, 但是抽象的装饰器类是可有可无的，具体需根据业务来处理

#### 2.1 用例类结构图

![](..\decorator-design-pattern\SinginServiceDecorator.png)

#### 2.2 创建基础信息

```java
//Member
public class Member {
    private String userName ;
    private String password ;
}

//ResultMsg
public class ResultMsg {
    private int code ;
    private String msg ;
    private Object data ;
}
```

#### 2.3 传统注册方式

```java
public class SinginService implements ISinginService {
    public ResultMsg register(String userName, String password) {
        String msg = String.format("register by common type, userName=%s , password=%s", userName, password);
        return new ResultMsg(200, msg, new Object());
    }
}

public interface ISinginService {
    ResultMsg register(String userName , String password);
}
```



#### 2.4 包装器类

```java
public class SinginServiceDecorator implements ISinginServiceDecorator {
    private ISinginService singinService ;

    public SinginServiceDecorator(ISinginService singinService) {
        this.singinService = singinService;
    }

    public ResultMsg register(String userName, String password) {
        return singinService.register(userName, password);
    }

    public ResultMsg registerByQQ(String openId) {
        String msg = String.format("register by QQ, openId = %s", openId);
        return new ResultMsg(200, msg, new Object());
    }

    public ResultMsg registerByWeChat(String openId) {
        String msg = String.format("register by WeChat, openId = %s", openId);
        return new ResultMsg(200, msg, new Object());
    }
}

public interface ISinginServiceDecorator {
    ResultMsg registerByQQ(String openId);

    ResultMsg registerByWeChat(String openId);
}
```

#### 2.5 测试类

```
public class SinginDecoratorTest {
    public static void main(String[] args) {
        ISinginService singinService = new SinginService();
        SinginServiceDecorator singinDecorator = new SinginServiceDecorator(singinService);

        ResultMsg result1 = singinDecorator.register("Bob", "12345");
        ResultMsg result2 = singinDecorator.registerByQQ("aabbcc");
        ResultMsg result3 = singinDecorator.registerByWeChat("hahahoho");

        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
    }
}
```

### 三、包装器和适配器比较

​	包装器和适配器都属于包装模式(Wrapper), 了解代理模式的,  可以看出包装器也属于特殊的代理模式

|      |                          装饰器模式                          |                          适配器模式                          |
| ---- | :----------------------------------------------------------: | :----------------------------------------------------------: |
| 形式 |              Wrapper模式一种形式，具有层级关系               |              Wrapper模式一种形式, 没有层级关系               |
| 定义 | 装饰者和被装饰者一般实现同一个接口, 目的为了满足OOP关系; 但是也可以通过代理继承的方式, 比如上面的例子用到了代理 | 适配器和被适配者之间没有必然联系, 一般通过继承或者代理的方式包装 |
| 关系 |                         满足is-a关系                         |                        满足has-a关系                         |
| 功能 |                        注重覆盖、扩展                        |                        注重兼容、转换                        |


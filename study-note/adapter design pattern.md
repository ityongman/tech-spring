## 适配器模式

### 一、定义和要求

#### 1.1 定义

​	适配器模式(Adapter pattern)是指将一个类的接口转为客户期望的另一个接口, 使原本不兼容的接口可以一起工作。

​	属于结构型设计模式

#### 1.2 适用场景

- 已经存在的类, 不满足现在业务需求, 但是方法执行结果相同或相似
- 不是软件设计阶段应该考虑的事情, 而是随着业务需求不断变化、代码逻辑不断维护, 产生功能类似但是接口不同的解决方案

#### 1.3 优点和缺点

优点

- 提高类的透明性和复用(现有的类复用不作修改)
- 目标类和适配器类结偶, 提高程序的扩展性
- 多数场景下符合开闭原则

缺点

- 适配器编写过程中需要全面考虑, 会增加系统的复杂性
- 增加代码阅读难度, 代码可读性变差

### 二、适配器模式 -- 实践用例

​	这里以传统注册式登陆, 增加了QQ、Wechart、Sina三方登陆方式为例说明适配器(Adapter)使用方式

#### 2.1传统登陆服务

```java
public class SigninService implements ISigninService {
    public ResultMsg register(String userName, String pwd) {
        return new ResultMsg(200, "Register success", new Object());
    }
}

/**
 * 老的登陆方法
 */
public interface ISigninService {
    ResultMsg register(String userName, String pwd);
}
```



#### 2.2 新增第三方登陆适配器类(Adapter)

```java
/**
 * 适配器类, 适配器不一定要有接口
 */
public interface IRegisterAdapter {
    /**
     * 当前obj是否支持该适配器
     * @param obj
     * @return
     */
    boolean support(Object obj);

    ResultMsg register(String id);
}
```

- QQ

```java
public class QQRegister implements IRegisterAdapter {
    public boolean support(Object obj) {
        return obj instanceof QQRegister;
    }

    public ResultMsg register(String id) {
        return new ResultMsg(200, "register by QQ success", new Object());
    }
}
```

- Wechat

```java
public class WeChatRegister implements IRegisterAdapter {
    public boolean support(Object obj) {
        return obj instanceof WeChatRegister;
    }

    public ResultMsg register(String id) {
        return new ResultMsg(200, "register by Wechat success", new Object());
    }
}
```

#### 2.3 新增第三方注册方式

```java
/**
 * 支持的第三方注册方式
 */
public interface IThirdSigninService {
    ResultMsg registerByQQ(String id);

    ResultMsg registerByWechat(String id);

}

public class ThirdSigninService extends SigninService implements IThirdSigninService {
    public ResultMsg registerByQQ(String id) {
        return processRegister(id, QQRegister.class);
    }

    public ResultMsg registerByWechat(String id) {
        return processRegister(id, WeChatRegister.class);
    }

    private ResultMsg processRegister(String id, Class<? extends IRegisterAdapter> clazz) {
        try {
            IRegisterAdapter registerAdapter = clazz.newInstance();

            if(registerAdapter.support(registerAdapter)) {
                return registerAdapter.register(id);
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
```

#### 2.4 创建测试类

```java
public class ThirdSigninRegisterTest {
    public static void main(String[] args) {
        ThirdSigninService signinService = new ThirdSigninService();
        //1.
        ResultMsg register = signinService.register("tom", "123456");
        System.out.println(register.toString());
        //2.
        ResultMsg qq = signinService.registerByQQ("10010");
        System.out.println(qq.toString());

        //3.
        ResultMsg wechat = signinService.registerByWechat("wechat");
        System.out.println(wechat.toString());
    }
}
```


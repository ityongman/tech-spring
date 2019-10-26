## 委派模式和策略模式

### 一、定义和要求

#### 1.1 定义

##### 1.1.1 委派模式

​	**负责任务的调度和分配**, 和代理模式有些相似, 可以看作是特殊情况下静态代理的全权代理, 代理模式注重过程, 委派模式注重结果。

​	**NOTE: 不属于23中设计模式之一, 属于行为型模式**

#### 1.2 使用场景

##### 1.2.1 委派模式

​	DispatcherServlet、BeanDefinitionParseDelegate

#### 1.3 优缺点

#####1.3.1 委派模式

- 精简程序逻辑, 提升代码的可读性
- 消除程序中大量的 if...else... 和 switch语句
- 理解策略模式应用场景后, 提高算法的保密性和安全性



### 二、委派模式

​	这里以公司Boss下达命令场景来熟悉委派模式用法 （除了Boss, 还存在Leader、worker角色）

#### 2.1 类结构图

![](/Users/shedunze/workspace/sourceWorkspace/tech-spring/delegate-strategy-design-pattern/Boss.png)

####2.2 实践用例

- 创建Boss

```java
public class Boss {
    public void doWork(Leader leader, String cmd){
        leader.doWork(cmd);
    }
}
```

- 创建IWorker

```java
public interface IWorker {
    void doWork(String cmd);
}
```

- 创建Leader

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

- 创建WorkerA 

```java
public class WorkerA implements IWorker {
    @Override
    public void doWork(String cmd) {
        System.out.println("WorkerA do work " + cmd);
    }
}
```

- 创建WorkerB

```java
public class WorkerB implements IWorker {
    @Override
    public void doWork(String cmd) {
        System.out.println("WorkerA do work " + cmd);
    }
}
```

- 创建WorkerC

```java
public class WorkerC implements IWorker {
    @Override
    public void doWork(String cmd) {
        System.out.println("WorkerA do work " + cmd);
    }
}
```

- 创建测试类

```java
public class DelegateWorkerTest {
    public static void main(String[] args) {
        Boss boss = new Boss();
        Leader leader = new Leader();

        boss.doWork(leader, "java");
    }
}
```

##### 2.3 DispatcherServlet简单实现

​	具体逻辑代码可以参考git地址
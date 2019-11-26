## 设计模式之单例模式

### 一、定义及要求

####1.1 定义

<font color="#ff00">单例模式(Singleton Pattern)是指确保一个类, 在任何情况下都绝对只有一个实例, 并只提供一个全局的访问点。</font>

NOTE: 属于创建型模式

####1.2 要求

<font color="#eeaa">隐藏所有的构造方法(构造方法私有)</font>

<font color="#eeaa">提供一个私有化的变量</font>

<font color="#eeaa">提供全局访问接口</font>

<font color="#eeaa">延迟加载</font>

<font color="#eeaa">保证线程安全</font>

<font color="#eeaa">防止序列化和反序列化破坏单例</font>

<font color="#eeaa">防止反射破坏单例</font>

#### 1.3 常见单例模式使用场景

<font color="#aabb">ServletContext、ServletConfig、ApplicationContext、DBPool</font>

#### 1.4 创建单例步骤

- 私有化构造方法

- 提供一个已经创建好的实例对象

- 提供一个访问实例对象的全局方法

- 单例模式常见写法
  - 饿汉式单例
  - 懒汉式单例
  - 注册式单例
  - ThreadLocal单例

#### 1.5 优缺点

- 优点
  - 在内存只有一个实例, 减少内存开销
  - 可以避免对资源的多重占用
  - 设置全局访问点, 严格控制访问
- 缺点
  - 没有接口、扩展困难
  - 如果要扩展单例对象, 只有修改代码, 没有其它途径

### 二、单例创建 - 饿汉式

#### 2.1 缺点

​	对象在未使用之前就已经创建好对象, 会浪费内存空间, 适合小范围使用, 不适合大范围

#### 2.2 创建方法

##### 方法一 、直接创建

- <font color="#ff00">在单例类首次加载时就创建实例</font>

- <font color="#aa00">类实例化前, 直接创建变量</font>

- ```java
  /**
   * 1. 构造方法私有
   * 2. 提供一个已经创建好的实例对象
   * 3. 提供一个访问实例对象的全局方法
   */
  public class HungrySingleton {
  
      //1.
      private HungrySingleton() {}
  
      //2.
      private static final HungrySingleton instance = new HungrySingleton();
  
      //3.
      public HungrySingleton getInstance() {
          return instance ;
      }
  }
  ```
##### 方法二、 静态代码块创建

- <font color="#ff00">在单例类首次加载时就创建实例</font>

- <font color="#aa00">类实例化前, 静态代码块创建变量</font>

  ```java
  /**
   * 1. 构造方法私有
   * 2. 提供一个已经创建好的实例对象
   * 3. 提供一个访问实例对象的全局方法
   */
  public class HungryStaticSingleton {
  
      //1.
      private HungryStaticSingleton() {}
  
      //2.
      private static final HungryStaticSingleton instance ;
  
      static {
          instance = new HungryStaticSingleton();
      }
  
      //3.
      public HungryStaticSingleton getInstance() {
          return instance ;
      }
  }
  ```

### 三、单例创建 - 懒汉式

#### 3.1 优势(相对饿汉式)

​	<font color="#ff00">被外部类调用时才创建, 避免内存占用</font>

#### 3.2 创建方法

##### 方法一、普通创建方式

- 线程不安全, 在并发的情况下可能出现下面情况
  - 只创建一个对象, 之后的线程都使用第一个线程创建的对象

  ```java
  Thread-0:com.ityongman.design.singleton.lazy.LazySimpleSingleton@484ec0a3
  Thread-1:com.ityongman.design.singleton.lazy.LazySimpleSingleton@484ec0a3
  ```

  - 只创建一个对象, 之后的线程会覆盖前面线程创建的对象, 并且该线程之后的其它线程都使用该对象

  ```java
  Thread-1:com.ityongman.design.singleton.lazy.LazySimpleSingleton@3210faa
  Thread-0:com.ityongman.design.singleton.lazy.LazySimpleSingleton@3210faa
  ```

  - 创建多个线程, 之后的线程与之前的线程会分别创建不同的对象

  ```java
  Thread-1:com.ityongman.design.singleton.lazy.LazySimpleSingleton@de6f066
  Thread-0:com.ityongman.design.singleton.lazy.LazySimpleSingleton@326a3857
  ```

```java
/**
 * 1. 构造方法私有
 * 2. 提供一个待创建的变量
 * 3. 提供一个访问实例对象的全局方法
 */
public class LazySimpleSingleton {
    //1.
    private LazySimpleSingleton(){}

    //2.
    private static LazySimpleSingleton singleton = null ;

    public LazySimpleSingleton getSingleton(){
        if (null == singleton) {
            singleton = new LazySimpleSingleton();
        }

        return singleton ;
    }

}
```

##### 方法二 、方法枷锁

- <font color="#aabb"><b>方法加锁</b>, 虽然JDK1.6之后对synchronized进行了优化, 性能有了很大的提升, 但是还是有性能问题</font>

- <font color="#bb00">会使整个类文件加锁, 如果一个线程已经进入, 其它的线程会进入monitor状态</font>

```java
/**
 * 1. 构造方法私有
 * 2. 提供一个待创建的变量
 * 3. 提供一个访问实例对象的全局方法(static)
 */
public class LazySynchronizeSigleton {
    //1.
    private LazySynchronizeSigleton(){}

    //2.
    private static LazySynchronizeSigleton singleton = null ;

    //3.
    public static synchronized LazySynchronizeSigleton getSingleton(){
        if (null == singleton) {
            singleton = new LazySynchronizeSigleton();
        }

        return singleton ;
    }

}
```

##### 方法三 、双重检查 (推荐)

- <font color="#aabb">代码块加锁 + 双重检查 + volatile </font>

  <font color="#ff00"><b>NOTE: 添加volatile关键字原因 -> 保持变量可见性</b></font>

- CPU执行时候会转换成JVM指令执行 -> 会存在指令重排序的问题(下面步骤2, 3)
    - 分配内存给这个对象
    - 初始化对象
    - 将初始化好的对象和内存地址建立关联, 赋值
    - 用户初次访问

  ```java
  /**
   * 1. 构造方法私有
   * 2. 提供一个待创建的变量
   * 3. 提供一个访问实例对象的全局方法(static)
   */
  public class LazyDoubleCheckSingleton {
      //1.
      private LazyDoubleCheckSingleton(){}
  
      //2. volatile 保持变量的可见性
      private volatile static LazyDoubleCheckSingleton singleton = null ;
  
      //3.
      public static LazyDoubleCheckSingleton getSingleton(){
          if (null == singleton) {
              synchronized (LazySimpleSingleton.class) {
                  if (null == singleton) {
                      singleton = new LazyDoubleCheckSingleton();
                  }
              }
  
          }
  
          return singleton ;
      }
  }
  ```

##### 方法四 、内部类(推荐)

- <font color="#ff00">使用内部类的特性, 只有外部类被调用的时候, 内部类才会被创建, 不然初始时候内部类不会被创建, 并且可以避免线程安全、性能低下问题</font>

- <font color="#5566"><b>存在反射攻击问题</b></font>

  ```java
  public class LazyInnerClassSingleton {
  		//constract 可以通过反射破解
      private LazyInnerClassSingleton(){}
  
      //1. 使用的是内部类的特性
      //2. InnerCreateSingleton 里面逻辑需要等到外面方法调用的时候才执行
      //3. JVM底层执行逻辑, 可以避免线程安全问题
      public static final LazyInnerClassSingleton getInstance(){
          return InnerCreateSingleton.singleton ;
      }
  
      private static class InnerCreateSingleton {
          private static final LazyInnerClassSingleton singleton = new LazyInnerClassSingleton();
      }
  }
  ```

- 反射方式暴力破解内部类单例

  ```java
  /**
   * 针对内部类创建单例模式, 模拟反射机制暴力破解单例
   */
  public class LzayInnerClassSingletonTest {
      public static void main(String[] args) {
          try {
              //1. 反射暴力破解
              Class<?> clazz = LazyInnerClassSingleton.class;
              Constructor<?> c = clazz.getDeclaredConstructor(null);
              c.setAccessible(true);
              Object o1 = c.newInstance();
  
              //2. 单例直接获取对象
              Object o2 = LazyInnerClassSingleton.getInstance();
  
              System.out.println(o1 == o2);
          } catch (Exception e){
              e.printStackTrace();
          }
      }
  }
  
  // 输出: false
  ```

- 避免反射破解内部类方式

  ```java
  private LazyInnerClassSingleton(){
  	if(null != InnerCreateSingleton.singleton) {
  		throw new RunTimeException("Not support create Obj by reflect");
  	}
  }
  ```


### 四、序列化反射的方式破坏单例

#### 4.1 饿汉式创建对象

```java
/**
 * 饿汉式单例
 * 反序列化创建对象
 * 1. 将已经持久化的字节码内容, 转换为IO流
 * 2. 通过对iO 流的读取, 进而将读取的内容转为Java对象
 * 3. 在转换过程中会重新创建对象
 */
public class SeriableSingleton implements Serializable {
    private SeriableSingleton () {}

    private final static SeriableSingleton singleton = new SeriableSingleton();

    public static SeriableSingleton getSingleton() {
        return singleton ;
    }

    // 避免序列化问题
//    private Object readResolve(){
//        return singleton ;
//    }
}
```

#### 4.2 通过字节流的方式破坏单例

- 会创建两个对象，一个newInstance()创建、一个readResolve()方法创建

```java
/**
 * 对饿汉式,进行序列化破解
 */
public class SeriableSingletonTest {
    public static void main(String[] args) {
        SeriableSingleton ss1 = SeriableSingleton.getSingleton();
        SeriableSingleton ss2 = null ;

        try {
            //1. 将单例文件输出到磁盘
            FileOutputStream fopt = new FileOutputStream("SeriableSingleton.serial");
            ObjectOutputStream oos = new ObjectOutputStream(fopt);

            oos.writeObject(ss1);
            fopt.close();
            oos.close();

            //2. 从磁盘读取前面输出的文件
            FileInputStream fis = new FileInputStream("SeriableSingleton.serial");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ss2 = (SeriableSingleton) ois.readObject();

            fis.close();
            ois.close();

            System.out.println(ss1);
            System.out.println(ss2);
            System.out.println(ss1 == ss2);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}

```

#### 4.3 解决字节流破坏单例

```java
//避免序列化问题 , 具体细节可以查看ois.readObject()内部源码
//该方法覆盖了反序列化出来的对象
//会创建两个对象, 之前创建出来的反序列化对象会被GC回收掉
private Object readResolve(){
   return singleton ;
}
```

### 五、注册式单例

- <font color="#ff00">将每一个实例都缓存到统一的容器中，使用唯一标识获取实例</font>

#### 5.1 枚举式单例

- 创建枚举式单例方式

```java
/**
 * 枚举式单例
 */
public enum  EnumSingleton {
    INSTANCE ;

    public static EnumSingleton getInstance(){
        return INSTANCE ;
    }

    private Object data ;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

```

- 通过字节流方式无法破坏单例

```java
package com.ityongman.design.singleton.lazy;

import com.ityongman.design.singleton.register.EnumSingleton;
import com.ityongman.design.singleton.seriable.SeriableSingleton;

import java.io.*;

/**
 * 对饿汉式,进行序列化破解
 */
public class EnumSingletonTest {
    public static void main(String[] args) {
        EnumSingleton e1 = EnumSingleton.getInstance();
        EnumSingleton e2 = null ;

        try {
            //1. 将单例文件输出到磁盘
            FileOutputStream fopt = new FileOutputStream("EnumSingleton.enum");
            ObjectOutputStream oos = new ObjectOutputStream(fopt);

            oos.writeObject(e1);
            fopt.close();
            oos.close();

            //2. 从磁盘读取前面输出的文件
            FileInputStream fis = new FileInputStream("EnumSingleton.enum");
            ObjectInputStream ois = new ObjectInputStream(fis);
            e2 = (EnumSingleton) ois.readObject();

            fis.close();
            ois.close();

            System.out.println(e1);
            System.out.println(e2);
            System.out.println(e1 == e2);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}

// 输出结果如下: 
// INSTANCE
// INSTANCE
// true
```

#### 5.2 容器式单例

- 优点
  - 对象方便管理
  - 非线程安全的, 需要添加synchronized关键字修饰代码块

- 创建容器式单例 --可以对比Spring 容器

```java
/**
 * 容器式单例 -- 可以比较Spring 容器
 */
public class ContainerSingleton {
    //1.
    private ContainerSingleton(){}

    //2.
    private static Map<String, Object> map = new ConcurrentHashMap<String, Object>();

    //3.没有synchronized关键字修饰会有线程安全问题
    public static Object getBean(String className){
        synchronized (map) { // 可以手动添加、关闭代码块来测试线程安全问题
            if(!map.containsKey(className)) {
                Object obj = null ;
                try {
                    obj = Class.forName(className).newInstance();
                    map.put(className, obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return obj ;
            }

            return map.get(className);
        }
    }
}
```

- 如果没有synchronized关键字修饰代码块, 会存在线程安全问题

```java
public class ContainerSingletonTest {

    public static void main(String[] args) throws InterruptedException {
        ConcurrentExecutor.execute(() -> {
            Object obj = ContainerSingleton.getBean("com.ityongman.entity.User");
            System.out.println(obj);
        }, 10, 5);
    }
}
```

### 六、ThreadLocal(伪线程安全)

####6.1 核心 

- 在线程内是线程安全的 
- 在线程之间是线程不安全的
- 属于注册式容器单例
- <font color="#a2a2">应用场景: 动态切换数据源</font>
- <font color="#ff00">原理: 以当前线程本身作为key, 以 Object作为值 ; 当同一线程过来时, 反回的结果会一样</font>

#### 6.2 创建ThreadLocal单例对象

```java
public class ThreadLocalSingleton {
    //1.
    private ThreadLocalSingleton(){}

    //2.
    private static final ThreadLocal<ThreadLocalSingleton> threadLocal = new ThreadLocal<ThreadLocalSingleton>(){
        @Override
        protected ThreadLocalSingleton initialValue() {
            return new ThreadLocalSingleton();
        }
    };

    //3.
    public static ThreadLocalSingleton getInstance() {
        return threadLocal.get() ;
    }

}
```

#### 6.3 获取ThreadLocal对象

```java
public class ThreadLocalSingletonTest {
    public static void main(String[] args) {
        //1.
//        for (int i = 0 ; i < 10 ; i++) {
//            ThreadLocalSingleton instance = ThreadLocalSingleton.getInstance();
//            System.out.println(instance);
//        }

        //2.
        Thread t1 = new Thread(() -> {
            for (int i = 0 ; i < 5 ; i++) {
                ThreadLocalSingleton instance = ThreadLocalSingleton.getInstance();
                System.out.println(Thread.currentThread() + "" +instance);
            }
        });

        //3.
        Thread t2 = new Thread(() -> {
            for (int i = 0 ; i < 5 ; i++) {
                ThreadLocalSingleton instance = ThreadLocalSingleton.getInstance();
                System.out.println(Thread.currentThread() + "" +instance);
            }
        });

        t1.start();
        t2.start();

        System.out.println("End...");
    }
}

```


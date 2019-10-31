## 设计模式之观察者模式

### 一、定义和要求

#### 1.1 定义

​		定义对象之间的一对多依赖, 让多个观察者对象同时监听一个主题(Subject)对象, 当主题对象发生变化时, 他的所有观察者都会收到通知并更新

NOTE: 属于行为型模式, 也被称为发布/订阅模式(pub/sub)

#### 1.2 适用场景

- 关联行为之间需要建立一套, 触发机制的场景
- java.awt.Event、dubbo 服务提供者和消费者之间、微博消息通知

#### 1.3 优点和缺点

优点

- 观察者和被观察者之间建立的是抽象的耦合
- 观察者模式支持广播通信

缺点

- 观察者之间有过多的细节依赖、提高了时间消耗
- 程序的复杂度提高了
- 使用时需要注意循环依赖

### 二、观察者模式 -- 实践代码

​	这里我们参考桌面应用程序(java.awt.Event)来熟悉观察者模式原理

#### 2.1 事件监听结构和观察者模式关系图

​	![](..\observer-design-pattern\观察者模式和EventListener设计类比图.png)

#### 2.2 代码用例图

#### 2.3 创建事件信息Event

```java
/**
 * 事件格式定义
 */
public class Event {
    /**
     * 事件源
     */
    private Object source;
    /**
     * 事件通知对象, 事件触发的时候需要通知谁
     */
    private Object target ;
    /**
     * 事件触发的时候, 需要做的回调操作
     */
    private Method callback ;
    /**
     * 触发的是什么事件
     */
    private String trigger ;
    /**
     * 触发的时间 timestamp
     */
    private long time ;

    /**
     * @param target
     * @param callback
     */
    public Event(Object target, Method callback) {
        this.target = target;
        this.callback = callback;
    }
}
```

#### 2.4 事件监听器EventListener

```java
	public class EventListener {
    /**
     * 容器: 用来存放事件
     * 一般情况都是通过容器来处理, Map、List之类
     */
    private Map<String, Event> events = new HashMap<>();

    public void addListener(String eventType, Object target) {
        try {
            addListener(
                    eventType,
                    target,
                    target.getClass().getMethod(eventType, Event.class)
            );
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void addListener(String eventType, Object target, Method callback) {
        events.put(eventType, new Event(target, callback));
    }

    public void trigger(String eventType) {
        if (events.containsKey(eventType)) {
            trigger(events.get(eventType).setTrigger(eventType));
        }
    }

    private void trigger(Event event) {
        event.setSource(this);
        event.setTime(System.currentTimeMillis());

        try{
            if(event.getCallback() != null) {
                event.getCallback().invoke(event.getTarget(), event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

#### 2.5 事件触发时, 观察者处理逻辑

```java
public class EventCallback implements IEventCallback{

    @Override
    public void click(Event e) {
        System.out.println("click event + " + e.toString());
    }

    @Override
    public void doubleClick(Event e) {
        System.out.println("doubleClick event + " + e.toString());
    }

    @Override
    public void up(Event e) {
        System.out.println("up event + " + e.toString());
    }
}

public interface IEventCallback {
    /**
     * 单击
     */
    void click(Event e);

    /**
     * 双击
     */
    void doubleClick(Event e);

    /**
     * 上移
     */
    void up(Event e);
}
```

#### 2.6 事件触发源Mouse

```java
public class Mouse extends EventListener{
    public void click() {
        System.out.println("Mouse click ...");
        trigger(EventType.ON_CLICK);
    }

    public void doubleClick() {
        System.out.println("Mouse doubleClick ...");
        trigger(EventType.ON_DOUBLE_CLICK);
    }

    public void up() {
        System.out.println("Mouse up ...");
        trigger(EventType.ON_UP);
    }
}
```

#### 2.7 测试用例

```java
public class EventTest {
    public static void main(String[] args) {
        Mouse mouse = new Mouse(); // 事件源 source
        EventCallback callback = new EventCallback(); // 通知的目标对象 target
        mouse.addListener(EventType.ON_CLICK, callback); // 将监听对象target = callback存入listener map中
        mouse.click(); // 触发事件源, listener通知观察者 EventCallback, 执行对应逻辑
    }
}
```


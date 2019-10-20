## 设计模式之单例模式

### 一、定义及要求

- 定义

  <font color="#ff00">单例模式(Singleton Pattern)是指确保一个类, 在任何情况下都绝对只有一个实例, 并只提供一个全局的访问点。</font>

- 要求

  <font color="#eeaa">隐藏所有的构造方法(构造方法私有)</font>

  <font color="#eeaa">属于创建型模式</font>

- 常见单例模式使用场景

  <font color="#aabb">ServletContext、ServletConfig、ApplicationContext、DBPool</font>

- 单例模式常见写法
  - 饿汉式单例
  - 懒汉式单例
  - 注册式单例
  - ThreadLocal单例
## 多线程基础知识讲解

### 一、创建多线程的方式

- 继承Thread类
- 实现Runnable接口
- 使用ExecutorService、Callable、Future创建带返回值的线程



### 二、Thread启动方式(start、run ?)

#### 2.1 线程启动方式

​	Thread类本质是实现了Runnable接口的一个实例, 启动线程的方法是通过start()实例方法, start()方法是一个native方法, 它会启动一个新线程, 并执行run方法; 而单独调用run方法只是进行了方法调用, 不符合线程创建调用流程

#### 2.2 线程创建

```java

```



JVM_ENTRY(void, JVM_StartThread(JNIEnv* env, jobject jthread)) --> native_thread = new JavaThread(&thread_entry, sz) --> JavaThread::JavaThread(ThreadFunction entry_point, size_t stack_sz) --> os::create_thread(this, thr_type, stack_sz) --> Thread::start(native_thread) --> os::start_thread(thread)
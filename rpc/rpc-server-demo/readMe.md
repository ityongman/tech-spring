## RPC接口

### 一. 基本通信方式
#### 1、定义请求接口IHelloService 
#### 2、定义请求接口实现类 HelloServiceImpl
#### 3、定义发布服务实现类RpcProxyServer
#### 4、定义异步处理业务逻辑的 Runnable --> ProcessorHandler
#### 5、反射调用方法, 获取逻辑调用值 invokeMehtod
#### 6、将方法调用结果 通过输出流响应给客户端

### 二. spring注解通信方式
#### 1. 定义Spring注解RpcService
#### 2. 定义新的RpcServer, 利用Spring ApplicationContextAware和InitializingBean机制, 启动服务端和加载所有需要处理的服务(afterPropertiesSet,setApplicationContext)
#### 3. 定义异步处理逻辑 Runnable --> ProcessorHandler

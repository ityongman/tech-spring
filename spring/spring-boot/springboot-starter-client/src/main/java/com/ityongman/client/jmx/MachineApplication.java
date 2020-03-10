package com.ityongman.client.jmx;

/**
 * @Author shedunze
 * @Date 2020-03-10 09:34
 * @Description 测试jmx的启动类入口
 */

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * 如果 jconsole 连接存在问题, 请配置vm参数
 * -Dcom.sun.management.jmxremote.port=9999
 * -Dcom.sun.management.jmxremote.authenticate=false
 * -Dcom.sun.management.jmxremote.ssl=false
 */

/**
 * JMX规范
 * 1. 实体命名必须以MBean结尾
 * 2. 实现类 必须和 MBean 在同一个包(package)下
 * 3. 实现类的名字 是 接口名字去掉MBean之后的名字, 比如: MachineMBean -> Machine
 */

/**
 * NOTE: spring boot 中actuator的实现原理就是基于jmx实现的
 */
public class MachineApplication {
    public static void main(String[] args) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, IOException {
        //1. 参见beanServer
        MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
        //2. 创建 ObjectName
        ObjectName objectName = new ObjectName("com.ityongman.client.jmx.Machine:type=machine");
        //3. 创建 MachineOperate
        MachineMBean machine = new Machine();
        //4. 信息注册
        beanServer.registerMBean(machine, objectName);

        System.in.read();

    }
}

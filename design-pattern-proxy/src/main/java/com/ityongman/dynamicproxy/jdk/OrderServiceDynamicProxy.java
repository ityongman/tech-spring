package com.ityongman.dynamicproxy.jdk;

import com.ityongman.entity.DbDatasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;

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

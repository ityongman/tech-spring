package com.ityongman.service.impl;

import com.ityongman.dao.OrderDao;
import com.ityongman.entity.Order;

/**
 * 没有引入 spring 相关jar, 这里以手动的方式创建
 *
 * 使用JDK Proxy 时候, 需要去掉注释,
 * JDK proxy 必须实现接口
 */
public class OrderService /*implements IOrderService*/ {
    private OrderDao orderDao ;

    public OrderService(){
        orderDao = new OrderDao();
    }

    public int insert(Order order) {
        System.out.println("OrderService call OrderDao interface");
        // 注意: printLog也会被代理,
        // 注意: 和expose-proxy=true, AopContext.getCurrentProxy()的区别
        // 实质上区别是this
        this.printLog();
        return orderDao.insert(order);
    }

    public int printLog() {
        System.out.println("OrderService call printLog method");
        return 1 ;
    }
}

package com.ityongman.service.impl;

import com.ityongman.dao.OrderDao;
import com.ityongman.entity.Order;
import com.ityongman.service.IOrderService;

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
        return orderDao.insert(order);
    }
}

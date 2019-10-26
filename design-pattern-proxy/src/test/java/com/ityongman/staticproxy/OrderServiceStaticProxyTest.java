package com.ityongman.staticproxy;

import com.ityongman.entity.Order;
import com.ityongman.service.IOrderService;
import com.ityongman.service.impl.OrderService;

public class OrderServiceStaticProxyTest {
    public static void main(String[] args) {
        //1.
        Order order = new Order();
        order.setCreateTime(System.currentTimeMillis());
        order.setId("10086");
        order.setOrderInfo(new Object());

        //2.
//        IOrderService orderService = new OrderServiceStaticProxy(new OrderService());
//        orderService.insert(order);
    }
}

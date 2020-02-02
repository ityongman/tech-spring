package com.ityongman.dynamicproxy.cglib;

import com.ityongman.entity.Order;
import com.ityongman.service.impl.OrderService;
import net.sf.cglib.core.DebuggingClassWriter;

public class OrderServiceCglibProxyTest {
    public static void main(String[] args) {

        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY,"cglib_proxy");

        OrderService orderService = (OrderService) new OrderServiceCglibProxy().getInstance(OrderService.class);

        Order order = new Order();
        order.setCreateTime(System.currentTimeMillis());

        orderService.insert(order);
    }
}

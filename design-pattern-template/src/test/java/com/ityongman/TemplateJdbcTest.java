package com.ityongman;

import com.ityongman.dao.impl.OrderDao;

import java.util.List;

public class TemplateJdbcTest {
    public static void main(String[] args) {
        //NOTE: 这里没有真正的数据源
        OrderDao orderDao = new OrderDao(null);
        List<?> orders = orderDao.selectAll();

        System.out.println(orders);
    }
}

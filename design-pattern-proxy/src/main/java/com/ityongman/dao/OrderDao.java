package com.ityongman.dao;

import com.ityongman.entity.Order;

public class OrderDao {
    public int insert(Order order){
        System.out.println("OrderDao insert order to DB");
        return 1 ;
    }
}

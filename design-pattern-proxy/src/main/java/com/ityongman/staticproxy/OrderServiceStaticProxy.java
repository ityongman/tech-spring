package com.ityongman.staticproxy;

import com.ityongman.entity.DbDatasource;
import com.ityongman.entity.Order;
import com.ityongman.service.IOrderService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 静态代理类
 */
public class OrderServiceStaticProxy implements IOrderService {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private IOrderService orderService ;

    public OrderServiceStaticProxy(IOrderService orderService){
        this.orderService = orderService ;
    }

    @Override
    public int insert(Order order) {
        before();

        Long createTime = order.getCreateTime();
        String dbRouter = dateFormat.format(new Date(createTime));

        System.out.println("static proxy router datasource [DB_" + dbRouter +"] to process");

        DbDatasource.setDB(dbRouter);
        int ret = orderService.insert(order);
//        DbDatasource.restore();

        after();

        return ret;
    }

    private void before() {
        System.out.println("static proxy before process");
    }

    private void after() {
        System.out.println("static proxy after process");
    }
}

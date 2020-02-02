package com.ityongman.dao.impl;

import com.ityongman.RowMapper;
import com.ityongman.dao.JdbcTemplate;
import com.ityongman.entity.Order;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderDao extends JdbcTemplate {
    public OrderDao(DataSource dataSource) {
        super(dataSource);
    }


    public List<?> selectAll() {
        String sql = "select * from order where id = ?" ;
        return super.executeQuery(sql, new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                Order order = new Order();
                //1.
//                order.setId("10086");
//                order.setOrderNo("orderId:10086");
//                order.setCreateTime(System.currentTimeMillis());

                //2.
                order.setId(rs.getString("id"));
                order.setOrderNo(rs.getString("orderNo"));
                order.setCreateTime(rs.getLong("createTime"));

                return order;
            }
        }, new Object[]{"10086"});
    }
}

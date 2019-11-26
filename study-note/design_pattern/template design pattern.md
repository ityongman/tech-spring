## 模版模式

### 一、定义及要求

#### 1.1 定义

​	模版模式又称为模版方法模式(Template method pattern), 是指定义一个算法的骨架， 子类可以为一个或多个步骤提供实现

​	模版方法使得子类在不改变算法结构的前提下, 重新定义算法的某些步骤

​	属于行为型设计模式

#### 1.2 使用场景

- 各子类中公共的部分被提取到父类中, 避免代码的重复
- 一次性实现算法中不变的部分, 可变的部分交给子类来实现
- JDBC、AbstractList、GenericServlet

#### 1.3 优缺点

##### 1.3.1优点

- 提高代码的重用性, 公共的代码被提取到父类中
- 提高代码的扩展性, 可变的部分由子类来实现
- 符合开闭原则

##### 1.3.2 缺点

- 类数目增加
- 间接的增加了系统实现的复杂度
- 继承特点:   如果父类新增加了抽象方法, 所有相关子类都必须实现这个子类

### 二、模版方法 -- 实践用例

​	模版方法适用于流程固定, 特定细节由子类实现的场景, 这里以JDBC操作数据库为例来说明模板方法的使用方式

#### 2.1 创建模版类JdbcTemplate

```java

public abstract class JdbcTemplate {

    private DataSource dataSource = null ;

    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource ;
    }
		
  	//NOTE: mapper 属于特定细节, 由子类实现
    public List<?> executeQuery(String sql, RowMapper mapper, Object[] values) {
        try {
            //1. 获取连接 conn
            Connection conn = this.getConnection();
            //2. 创建语句集 PreparedStatement
            PreparedStatement pstm = createPstm(conn, sql);
            //3. 执行语句集 ResultSet
            ResultSet rs = executeQuery(pstm, values);
            //4. 处理结果集 processResultSet
            List<?> result = processResultSet(rs, mapper);
            //5. 关闭结果集 closeResultSet
            closeResultSet(rs);
            //6. 关闭语句集 closePstm
            closePstm(pstm);
            //7. 关闭连接 closeConn
            closeConn(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null ;
    }

    //1. 获取连接, 这里也可以通过连接池获取连接
    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    //2. 创建语句集 PreparedStatement
    protected PreparedStatement createPstm(Connection conn, String sql) throws SQLException {
        return conn.prepareStatement(sql);
    }

    //3. 执行语句集 ResultSet
    protected ResultSet executeQuery(PreparedStatement pstm, Object[] values) throws SQLException {
        for (int i = 0 , len = values.length ; i < len ; i++) {
            pstm.setObject(i, values[i]);
        }
        return pstm.executeQuery() ;
    }

    //4. 处理结果集 processResultSet
    private List<?> processResultSet(ResultSet rs, RowMapper rowMapper) throws SQLException {
        List<Object> result = new ArrayList<>();
        int rowNum = 1 ;
        while (rs.next()) {
            result.add(rowMapper.mapRow(rs, rowNum++));
        }
        return result ;
    }

    //5. 关闭结果集 closeResultSet
    protected void closeResultSet(ResultSet rs) throws SQLException {
        rs.close();
    }

    //6. 关闭语句集 closePstm
    protected void closePstm(PreparedStatement pstm) throws SQLException {
        pstm.close();
    }
    //7. 关闭连接 closeConn , 这里也可以通过连接池回收
    protected void closeConn(Connection conn) throws SQLException {
        conn.close();
    }
}
```

#### 2.2 创建RowMapper

```java
public interface RowMapper<T> {
    T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
```

#### 2.3 创建OrderDao

```java
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

public class Order {
    private String id ;
    private String orderNo ;
    private Long createTime ;
}
```

#### 2.4 创建测试类

```java
public class TemplateJdbcTest {
    public static void main(String[] args) {
        //NOTE: 这里没有真正的数据源
        OrderDao orderDao = new OrderDao(null);
        List<?> orders = orderDao.selectAll();

        System.out.println(orders);
    }
}
```


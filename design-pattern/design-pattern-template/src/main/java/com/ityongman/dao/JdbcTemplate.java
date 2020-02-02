package com.ityongman.dao;

import com.ityongman.RowMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class JdbcTemplate {

    private DataSource dataSource = null ;

    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource ;
    }

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

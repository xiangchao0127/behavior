package com.handge.bigdata.dao.proxy;


import com.handge.bigdata.config.Configure;
import com.handge.bigdata.pools.EnvironmentContainer;
import com.handge.bigdata.pools.Pools;
import com.handge.bigdata.utils.LogUtil;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Liujuhao on 2018/4/28.
 */
public class MySQLProxy implements Proxy {

    private static Logger logger = LogManager.getLogger(MySQLProxy.class);
    private Pools pools = null;
    private static int count = 0;

    public MySQLProxy() {
        Configure configure = Configure.getInstance(true);
        Configuration cnf = configure.getDBConfiguration();
        pools = Pools.createPool(cnf);
    }

    @Override
    public ResultSet queryBySQL(String sql) {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs =null;
        try {
            connection = pools.getMysqlConnection();
            count++;
           LogUtil.printSomething("mysql 连接数："+ count);
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            CachedRowSet crs = new CachedRowSetImpl();
            crs.populate(rs);
            return crs;
        } catch (SQLException e) {
            logger.error(LogUtil.getTrace(e));
            throw new RuntimeException(LogUtil.getTrace(e));
        } finally {
            if (rs !=  null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new RuntimeException(LogUtil.getTrace(e));
                }
            }
            if (statement !=  null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(LogUtil.getTrace(e));
                }
            }
            if (connection != null) {
                pools.returnMysqlConnection(connection);
                count--;
            }
        }
    }

}

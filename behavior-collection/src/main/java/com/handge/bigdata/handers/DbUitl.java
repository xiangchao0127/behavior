package com.handge.bigdata.handers;

import java.io.Serializable;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DbUitl implements Serializable {
    // JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://172.20.31.108:3306/sangfor?useUnicode=true&characterEncoding=utf8";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "mysql";
    static final String PASS = "mysql";
    static Connection connection = null;

    public static Connection getConnection() {
        try {
            Class.forName(JDBC_DRIVER);
            try {
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void insert(String id, String text, String descp, String website) {
        String sql = "insert into website (id,text,descp,website) values (?,?,?,?)";
        try {
            PreparedStatement preStmt = getConnection().prepareStatement(sql);
            preStmt.setString(1, id);
            preStmt.setString(2, text);
            preStmt.setString(3, descp);
            preStmt.setString(4, website);
            preStmt.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getSignName(String serverName, String appName) {
        String sql = "select a.handge_class FROM type t INNER JOIN app a where t.类型ID = a.归属类型 " +
                "AND a.`归属类型`= (select t.`类型ID` from type t where t.`关联属性`= ? ) AND a.`应用属性名`= ?;";
        try {
            PreparedStatement preStmt = getConnection().prepareStatement(sql);
            preStmt.setString(1, serverName);
            preStmt.setString(2, appName);
//            preStmt.setString(1, id);
            ResultSet resultSet = preStmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("handge_class");
            } else {
                sql = "select a.handge_class from Attribution a where  a.`属性名` = ?;";
                PreparedStatement preStmt2 = getConnection().prepareStatement(sql);
                preStmt2.setString(1, appName);
                ResultSet resultSet2 = preStmt2.executeQuery();
                if (resultSet2.next()) {
                    return resultSet2.getString("handge_class");
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static HashMap<String, String> getAllClassName(Connection connection) {
        String sqlApp = "select a.`应用属性名`,a.handge_class from app a;";
        String sqlAttr = "select a.`属性名`,a.handge_class from Attribution a;";
        HashMap<String, String> hashMapAll = null;
        try {
            PreparedStatement preStmt = connection.prepareStatement(sqlApp);
            ResultSet resultSet = preStmt.executeQuery();
            hashMapAll = new HashMap<>();
            while (resultSet.next()) {
                hashMapAll.put(resultSet.getString(1), resultSet.getString("handge_class"));
            }
            PreparedStatement preStmt2 = connection.prepareStatement(sqlAttr);
            ResultSet resultSet2 = preStmt2.executeQuery();
            while (resultSet2.next()) {
                hashMapAll.put(resultSet2.getString(1), resultSet2.getString("handge_class"));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return hashMapAll;
    }

    /**
     * 查询应用分类
     *
     * @param connection
     * @return
     * @throws Exception
     */
    public static HashMap<String, String> getAllBasicClass(Connection connection) throws Exception {
        String basicSql = "select app_name,basic_class from app";
        HashMap<String, String> hashMapAll = new HashMap<>();
        PreparedStatement preStmt = connection.prepareStatement(basicSql);
        ResultSet resultSet = preStmt.executeQuery();
        while (resultSet.next()) {
            hashMapAll.put(resultSet.getString(1), resultSet.getString("basic_class"));
            System.out.println(resultSet.getString("basic_class"));
        }
        return hashMapAll;
    }

    public static HashMap<String, String> getMappingClass(Connection connection) throws Exception {
        String sql = "select basic_class,abstract_label from class_map";
        HashMap<String, String> hashMapAll = new HashMap<>();
        PreparedStatement preStmt = connection.prepareStatement(sql);
        ResultSet resultSet = preStmt.executeQuery();
        while (resultSet.next()) {
            hashMapAll.put(resultSet.getString(1), resultSet.getString("abstract_label"));
        }
        return hashMapAll;
    }

    public static HashMap<String, String> getAbstractMapping(Connection connection) throws Exception {
        HashMap<String, String> mappingClass = getMappingClass(connection);
        String sql = "select abstract_label,basic_class from class_map";
        HashMap<String, String> hashMapAll = new HashMap<>();
        PreparedStatement preStmt = connection.prepareStatement(sql);
        ResultSet resultSet = preStmt.executeQuery();
        while (resultSet.next()) {
            if (!resultSet.getString(1).contains("/"))
                hashMapAll.put(resultSet.getString(1), resultSet.getString("basic_class"));
        }
        HashMap<String, String> hashMap = new HashMap<>();
        Set<String> keys = mappingClass.keySet();  //basic abstract
        Set<String> keys2 = hashMapAll.keySet();   //abstract basic
        for (String key2 : keys2) {
            String val = "";
            for (String key : keys) {
                if (mappingClass.get(key).contains(key2)) {
                    val += key + ",";
                }
            }
            hashMap.put(key2, val.substring(0, val.length() - 1));
        }
        return hashMap;
    }

    public static HashMap<String, String> getJobClass(Connection connection) throws Exception {
        HashMap<String, String> hashMap = new HashMap<>();
        String sql = "select basic_class,job_class from job_map";
        PreparedStatement preStmt = connection.prepareStatement(sql);
        ResultSet resultSet = preStmt.executeQuery();
        while (resultSet.next()) {
            hashMap.put(resultSet.getString(1), resultSet.getString("job_class"));
        }
        return hashMap;
    }


    public static void main(String[] args) throws Exception {
        System.out.println( getAllClassName(getConnection()));
    }
}

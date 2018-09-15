/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-server
 * Class : MysqlProperty
 * User : XueFei Wang
 * Date : 7/2/18 5:20 PM
 * Modified :7/2/18 5:20 PM
 * Todo :
 *
 */

package com.handge.bigdata.context;

import com.handge.bigdata.config.Configure;
import org.apache.commons.configuration2.Configuration;
import org.springframework.stereotype.Component;

@Component
public class MysqlProperty {

    public MysqlProperty(){
        Configuration config = Configure.getInstance(true).getDBConfiguration();
        this.username =   config.getString("server.database.user");
        this.password = config.getString("server.database.password");
        this.maxTotal = config.getInt("server.database.maxTotal");
        this.initialSize = config.getInt("server.database.initialSize");
        this.dbType = config.getString("server.database.type");
        this.dbName =  config.getString("server.database.name");
        this.dbIp =  config.getString("server.database.ip");
        this.dbPort =  config.getString("server.database.port");
    }

    private String username;


    private String password;


    private int maxTotal;


    private int initialSize;


    private String dbType;


    private String dbName;


    private String dbIp;


    private String dbPort;

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbIp() {
        return dbIp;
    }

    public void setDbIp(String dbIp) {
        this.dbIp = dbIp;
    }

    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

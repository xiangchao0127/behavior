/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-common
 * Class : ExportData
 * User : XueFei Wang
 * Date : 5/28/18 7:53 PM
 * Modified :5/28/18 7:53 PM
 * Todo :
 *
 */

package com.handge.bigdata;

import com.handge.bigdata.config.Configure;
import com.handge.bigdata.pools.EnvironmentContainer;
import com.handge.bigdata.pools.Pools;
import org.apache.commons.configuration2.Configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ExportData {


    public static void main(String[] args) {

        String search = "select * from app_basic";

        String insert = " insert into tag_url (app_name, app_category, app_domain_name, web_url,app_association,site_tag_set,app_desc,keyword2,keyword3,handge_class,basic_class )  values (?, ?, ?, ?, ?,?, ?, ?, ?, ?,?)";

        EnvironmentContainer.setENV();
        Configure configure = Configure.getInstance(true);
        Configuration cnf = configure.getDBConfiguration();


        Pools pools = Pools.createPool(cnf);

        Connection connection = pools.getMysqlConnection();
        try {
            ResultSet result = connection.createStatement().executeQuery(search);
            PreparedStatement preparedStmt = connection.prepareStatement(insert);

            while (result.next()) {
                preparedStmt.setString(1, result.getString("app_name"));
                preparedStmt.setString(2, result.getString("app_category"));
                preparedStmt.setString(3, result.getString("app_domain_name"));
                preparedStmt.setString(4, result.getString("web_url"));
                preparedStmt.setString(5, result.getString("app_association"));
                preparedStmt.setString(6, result.getString("site_tag_set"));
                preparedStmt.setString(7, result.getString("app_desc"));
                preparedStmt.setString(8, result.getString("keyword2"));
                preparedStmt.setString(9, result.getString("keyword3"));
                preparedStmt.setString(10, result.getString("handge_class"));
                preparedStmt.setString(11, result.getString("basic_class"));
                try {
                    preparedStmt.execute();
                } catch (Exception e) {
                    continue;
                }

            }
        } catch (Exception e) {
            System.out.println(e.getCause().getMessage());
        }

    }
}

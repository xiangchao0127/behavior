/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-common
 * Class : ParserData
 * User : XueFei Wang
 * Date : 6/1/18 2:10 PM
 * Modified :6/1/18 2:10 PM
 * Todo :
 *
 */

package com.handge.bigdata;

import com.handge.bigdata.config.Configure;
import com.handge.bigdata.pools.EnvironmentContainer;
import com.handge.bigdata.pools.Pools;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public class ParserData {

    public static void main(String[] args) {
        EnvironmentContainer.setENV();
        Configure configure = Configure.getInstance(true);
        Configuration cnf = configure.getDBConfiguration();
        Pools pools = Pools.createPool(cnf);
        final String sql_insert = "insert into  tag_url(app_name,app_domain_name) values('%s','%s')";
        try {
            List<String> lines = FileUtils.readLines(new File("/home/ubuntu/Desktop/app_feature.txt"), Charset.defaultCharset());
            Connection connect = pools.getMysqlConnection();
            Statement state = connect.createStatement();
            for (String l : lines) {
                String[] s = l.split("=");
                String domain = s[1].split("\\/")[0];
                String name = s[0];
                String sql = String.format(sql_insert, name, domain);
                try {
                    state.execute(sql);
                } catch (Exception e) {
                    continue;
                }

            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}

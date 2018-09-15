/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata;

import com.handge.bigdata.config.Configure;
import com.handge.bigdata.pools.EnvironmentContainer;
import org.apache.commons.configuration2.Configuration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

public class ConfigTest {
    public static void main(String[] args) throws IOException, SQLException {
        EnvironmentContainer.setENV();
        while (true){
            Configure configure = Configure.getInstance(true);
            // Example:  tableName    "config_extend"
            Configuration configuration = configure.getDBConfiguration();
            Iterator<String> keys = configuration.getKeys();
            while (keys.hasNext()) {
                String key = keys.next();
                String[] values = configuration.getStringArray(key);
            }
        }

    }
}

/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-common
 * Class : Configure
 * User : XueFei Wang
 * Date : 5/24/18 11:08 AM
 * Modified :5/17/18 10:44 AM
 * Todo :
 *
 */

package com.handge.bigdata.config;

import com.handge.bigdata.utils.Preconditions;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


/**
 *
 *
 *
 */
public class Configure {


    public static File configs;
    private static volatile Configure configure = null;
    private static volatile CombinedConfiguration combinedConfiguration = new CombinedConfiguration();
    public final String BASEPTH = "/etc/handge-configs/";
    public final String Config_Env = "HANDGE_CONF";
    public final String Stuff_conf = "conf";
    public final String Stuff_properties = "properties";
    public final String[] extensions = {Stuff_conf, Stuff_properties};
    public final String CONF_DB_HOST = "CONF_DB_HOST";
    public final String CONF_DB_PORT = "CONF_DB_PORT";
    public final String CONF_DB_DATABASE = "CONF_DB_DATABASE";

    public final String CONF_DB_TABLE = "CONF_DB_TABLE";

    public final String CONF_DB_URL = "CONF_DB_URL";

    public final String CONF_DB_USER = "CONF_DB_USER";
    public final String CONF_DB_PASSWORD = "CONF_DB_PASSWORD";

    public final String EXTEND_DB_CONF = "extend.conf";

    /**
     * @param use_db_conf
     */
    private Configure(boolean use_db_conf) {
        EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration();
        addConfiguration(environmentConfiguration, "ENV");
        if (use_db_conf) {
            String dbtble = combinedConfiguration.getString(CONF_DB_TABLE, "config");
            addDBConfg(dbtble);
        } else {
            String confdir = combinedConfiguration.getString(Config_Env, BASEPTH);
            configs = new File(confdir);
            if (!configs.exists()) {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                URL resource = classLoader.getResource("");
                if (resource != null) {
                    configs = new File(resource.getPath());
                } else {
                    configs = new File("");
                }
            }
            System.out.println("load config from " + configs.getAbsolutePath());
            parse(configs);
        }
    }

    private static void debugConf() {
        Set<String> names = combinedConfiguration.getConfigurationNames();
        for (String name : names) {
            System.out.println("==================================================");
            System.out.println(name + "  :");
            System.out.println("=======");
            Configuration configuration = combinedConfiguration.getConfiguration(name);
            Iterator<String> keys = configuration.getKeys();
            while (keys.hasNext()) {
                String key = keys.next();
                String[] values = configuration.getStringArray(key);
                System.out.println(key + "  :  " + String.join(" , ", values));
            }
        }
    }


    public static Configure getInstance(boolean use_db_conf) {
        if (null == configure) {
            synchronized (Configure.class) {
                if (null == configure) {
                    configure = new Configure(use_db_conf);
                    debugConf();
                }
            }
        }
        return configure;
    }

    /**
     * 递归加载配置
     *
     * @param file
     */
    private void parse(File file) {
        Collection<File> fs = FileUtils.listFiles(file, extensions, true);
        Iterator<File> files = fs.iterator();
        while (files.hasNext()) {
            File f = files.next();
            String fileName = f.getName().trim();
            try {
                addConfiguration(f, fileName);
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param configuration
     * @param name          　配置单元名字
     */
    public void addConfiguration(AbstractConfiguration configuration, String name) {
        configuration.addEventListener(Event.ANY, new ConfigurationListener());
        combinedConfiguration.addConfiguration(configuration, name);
    }

    /**
     * @param file
     * @throws ConfigurationException
     */
    public void addConfiguration(File file, String fileName) throws ConfigurationException {
        if (file.isFile()) {
            Parameters params = new Parameters();
            FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
                    new FileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
                            .configure(params.properties()
                                    .setFile(file).setEncoding("UTF-8"));
            addConfiguration(builder.getConfiguration(), fileName);
        }
    }


    /**
     * @param host
     * @param port
     * @param database
     * @param userName
     * @param password
     * @param table
     */
    public void addDataBaseConfig(String host, int port, String database, String userName, String password, String table) {

        String jdbcurl = String.format("jdbc:mysql://%s:%d/%s", host, port, database);
        addDataBaseConfig(jdbcurl, userName, password, table);
    }

    /**
     * @param jdbcurl
     * @param userName
     * @param password
     * @param table
     */
    public void addDataBaseConfig(String jdbcurl, String userName, String password, String table) {
        try {
            MysqlDataSource ds = new MysqlDataSource();
            ds.setURL(jdbcurl);
            ds.setUser(userName);
            ds.setPassword(password);
            Parameters params = new Parameters();
            BasicConfigurationBuilder<DatabaseConfig> builder =
                    new BasicConfigurationBuilder<DatabaseConfig>(DatabaseConfig.class);
            builder.configure(
                    params.database()
                            .setDataSource(ds)
                            .setTable(table)
                            .setKeyColumn("_key")
                            .setValueColumn("_value")
            );
            addConfiguration(builder.getConfiguration(), table);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }


    public Configuration getConfiguration() {
        return this.combinedConfiguration;
    }


    public Configuration getDBConfiguration() {
        Configuration conf = getConfiguration(combinedConfiguration.getString(CONF_DB_TABLE, "config"));
        return conf;
    }


    /**
     * 指定单元　优于全配扫描
     *
     * @param name 配置单元名字
     * @return
     */
    public Configuration getConfiguration(String name) {
        Configuration conf = this.combinedConfiguration.getConfiguration(name);
        return conf;
    }


    /**
     * @param confTableName table name
     * @return
     */
    public Configuration getExtendConfiguration(String confTableName) {
        addDBConfg(confTableName);
        return getConfiguration(confTableName);
    }


    private void addDBConfg(String confTableName) {
        String dbuser = Preconditions.checkNotNull(combinedConfiguration.getString(CONF_DB_USER), "should set " + CONF_DB_USER + " in ENV");
        String dbpasswd = Preconditions.checkNotNull(combinedConfiguration.getString(CONF_DB_PASSWORD), "should set " + CONF_DB_PASSWORD + " in ENV");
        String dburl = combinedConfiguration.getString(CONF_DB_URL);
        String dbhost = combinedConfiguration.getString(CONF_DB_HOST);
        int dbport = combinedConfiguration.getInt(CONF_DB_PORT);
        String db = combinedConfiguration.getString(CONF_DB_DATABASE);
        if (null == dburl || dburl.isEmpty()) {
            addDataBaseConfig(dbhost, dbport, db, dbuser, dbpasswd, confTableName);
        } else {
            addDataBaseConfig(dburl, dbuser, dbpasswd, confTableName);
        }
    }

}
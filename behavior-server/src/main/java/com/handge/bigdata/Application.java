package com.handge.bigdata;

import com.handge.bigdata.context.CustomProperty;
import com.handge.bigdata.context.MysqlProperty;
import com.handge.bigdata.pools.EnvironmentContainer;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Liujuhao
 * @date 2018/5/28.
 */

@SpringBootApplication
//@EnableScheduling
public class Application extends SpringBootServletInitializer implements TomcatConnectorCustomizer, EnvironmentAware {

    @Autowired
    CustomProperty customProperty;

    Log logger = LogFactory.getLog(this.getClass());

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * ShiroFilterFactoryBean 处理拦截资源文件问题。
     * Filter Chain定义说明 1、一个URL可以配置多个Filter，使用逗号分隔 2、当设置多个过滤器时，全部验证通过，才视为通过
     * 部分过滤器可指定参数，如perms，roles
     */
    @Bean
    @Order(0)
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        logger.debug("ShiroConfiguration.shirFilter()");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 拦截器.
        // <!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
        // <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->

        //配置shiro默认登录界面地址，前后端分离中登录界面跳转应由前端路由控制，后台仅返回json数据
        shiroFilterFactoryBean.setLoginUrl("/behavior/common/unauth");
//        // 登录成功后要跳转的链接
//        shiroFilterFactoryBean.setSuccessUrl("/index");
//        // 未授权界面;
//        shiroFilterFactoryBean.setUnauthorizedUrl("/403");

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        // 配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filterChainDefinitionMap.put("/logout", "logout");
        // 配置不会被拦截的链接 顺序判断
        filterChainDefinitionMap.put("/**/login/**", "anon");
        filterChainDefinitionMap.put("/**", "anon");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }


    @Bean
    @Order(1)
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        logger.debug("-------- loaded bean: cros filter --------");
        return bean;
    }

    @Bean
    @Order(2)
    public ServletRegistrationBean dispatcherRegistration( DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean bean = new ServletRegistrationBean(dispatcherServlet);
        bean.getUrlMappings().clear();
        bean.addUrlMappings("/behavior/*");
        logger.debug("-------- loaded bean: servlet mapping --------");
        return bean;
    }

    @Bean
    @Order(3)
    public ServletWebServerFactory servletContainer() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.setPort(9090);
        tomcat.setDisplayName("net-behavior");
        tomcat.setAddress(address);
        tomcat.addConnectorCustomizers(this::customize);
        return tomcat;
    }

    @Bean
    @Order(4)
    public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
        return new OpenEntityManagerInViewFilter();
    }

    @Override
    public void customize(Connector connector) {
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        protocol.setMaxConnections(1024 * 1024 * 250);
        protocol.setMaxThreads(Runtime.getRuntime().availableProcessors() * 4);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }

    @Override
    public void setEnvironment(Environment environment) {
        EnvironmentContainer.setENV();
    }

    @Bean(destroyMethod = "")
    public DataSource dataSource(MysqlProperty configProperty)
    {
        String url = "";
        String password = configProperty.getPassword();
        String dbType = configProperty.getDbType();
        String driverClassName = "";
        if ("mysql".equalsIgnoreCase(dbType)) {
            driverClassName = "com.mysql.jdbc.Driver";
            url = "jdbc:mysql://" + configProperty.getDbIp() + ":" + configProperty.getDbPort() + "/"
                    + configProperty.getDbName();
        }

        else if ("oracle".equalsIgnoreCase(dbType)) {
            driverClassName = "oracle.jdbc.driver.OracleDriver";
            url = "jdbc:oracle:thin:@" + configProperty.getDbIp() + ":" + configProperty.getDbPort() + ":"
                    + configProperty.getDbName();
        }

        return DataSourceBuilder.create().driverClassName(driverClassName).url(url).username(configProperty.getUsername()).password(password)
                .type(MysqlDataSource.class).build();
    }
}


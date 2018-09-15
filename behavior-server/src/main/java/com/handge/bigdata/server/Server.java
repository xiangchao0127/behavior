package com.handge.bigdata.server;

import com.handge.bigdata.pools.EnvironmentContainer;
import com.handge.bigdata.server.filters.Cors;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.reflections.Reflections;

import javax.annotation.Resource;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class Server extends Application implements IServer {


    private volatile static Server instance = null;
    private NettyJaxrsServer netty;
    private String rootResourcePath = "/behavior";

    private Server(int port) {
        int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 2;
        int executorThreadCount = Runtime.getRuntime().availableProcessors() * 4;
        int maxRequestSize = 1024 * 1024 * 250;
        ResteasyDeployment dp = new ResteasyDeployment();
        dp.setApplication(this);
        dp.getProviders().add(new Cors());
        netty = new NettyJaxrsServer();
        netty.setDeployment(dp);
        netty.setPort(port);
        netty.setRootResourcePath(rootResourcePath);
        netty.setIoWorkerCount(ioWorkerCount);
        netty.setExecutorThreadCount(executorThreadCount);
        netty.setMaxRequestSize(maxRequestSize);
        netty.setKeepAlive(false);
        netty.setSecurityDomain(null);
    }

    /**
     * thread safe single  instance
     *
     * @param port
     * @return
     */

    public static Server getInstance(final int port) {
        if (instance == null) {
            synchronized (Server.class) {
                if (instance == null) {
                    instance = new Server(port);
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        EnvironmentContainer.setENV();
        Server server = Server.getInstance(9090);
        Set<Object> resources = server.getSingletons();
        for (Object r : resources) {
            System.out.println(r);
        }
        server.start();

    }

    @Override
    public Set<Object> getSingletons() {

        Reflections reflections = new Reflections("com.handge.bigdata.resource");
        HashSet objects = new HashSet();
        Set<Class<?>> resources = reflections.getTypesAnnotatedWith(Resource.class);
        for (Class c : resources) {
            try {
                objects.add(c.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return objects;
    }

    @Override
    public void start() {
        netty.start();
    }

    @Override
    public void stop() {
        netty.stop();
    }
}

package com.handge.bigdata.server;

public interface IServer {

    /**
     * start server at port
     *
     * @param port
     */
    public void start();


    /**
     * to stop server
     */
    public void stop();
}

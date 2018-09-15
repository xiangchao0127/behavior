package com.handge.bigdata.server.filters;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;

import javax.annotation.Priority;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
@Priority(900)
public class Cors extends CorsFilter {
    public Cors() {
        this.getAllowedOrigins().add("*");
        this.setAllowedHeaders("Content-Type, H-TOKEN,Pragma,Cache-Control");
        this.setAllowedMethods("GET, POST, DELETE, PUT, PATCH, OPTIONS");
    }
}


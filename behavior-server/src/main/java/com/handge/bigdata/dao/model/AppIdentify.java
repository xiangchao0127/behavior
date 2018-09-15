/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-common
 * Class : AppIdentify
 * User : XueFei Wang
 * Date : 5/31/18 10:38 AM
 * Modified :5/31/18 10:21 AM
 * Todo :
 *
 */

package com.handge.bigdata.dao.model;

public class AppIdentify {

    String app_name;
    String website;
    String keyword2;
    String keyword3;
    String basicClass;

    public AppIdentify() {
    }

    public AppIdentify(String app_name, String website, String keyword2, String keyword3, String basicClass) {
        this.app_name = app_name;
        this.website = website;
        this.keyword2 = keyword2;
        this.keyword3 = keyword3;
        this.basicClass = basicClass;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getKeyword2() {
        return keyword2;
    }

    public void setKeyword2(String keyword2) {
        this.keyword2 = keyword2;
    }

    public String getKeyword3() {
        return keyword3;
    }

    public void setKeyword3(String keyword3) {
        this.keyword3 = keyword3;
    }

    public String getBasicClass() {
        return basicClass;
    }

    public void setBasicClass(String basicClass) {
        this.basicClass = basicClass;
    }

    @Override
    public String toString() {
        return "AppIdentify{" +
                "app_name='" + app_name + '\'' +
                ", website='" + website + '\'' +
                ", keyword2='" + keyword2 + '\'' +
                ", keyword3='" + keyword3 + '\'' +
                ", basicClass='" + basicClass + '\'' +
                '}';
    }
}

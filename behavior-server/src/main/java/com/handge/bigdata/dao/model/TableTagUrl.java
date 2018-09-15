package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by XC on 2018/6/25.
 */
@Entity
@Table(name = "tag_url")
public class TableTagUrl implements Serializable {
    private static final long serialVersionUID = -2560801591266431426L;
    @Id
    @Column(name = "app_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long app_id;

    @Column(name = "app_name")
    private String appName;

    @Column(name = "app_category")
    private String appCategory;

    @Column(name = "app_domain_name")
    private String appDomainName;

    @Column(name = "web_url")
    private String webUrl;

    @Column(name = "app_association")
    private String appAssociation;

    @Column(name = "site_tag_set")
    private String siteTagSet;

    @Column(name = "app_desc")
    private String appDesc;

    @Column(name = "keyword2")
    private String keyword2;

    @Column(name = "keyword3")
    private String keyword3;

    @Column(name = "version_number")
    private String versionNumber;

    @Column(name = "handge_class")
    private String handgeClass;

    @Column(name = "basic_class")
    private String basicClass;


    public long getApp_id() {
        return app_id;
    }

    public void setApp_id(long app_id) {
        this.app_id = app_id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppCategory() {
        return appCategory;
    }

    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

    public String getAppDomainName() {
        return appDomainName;
    }

    public void setAppDomainName(String appDomainName) {
        this.appDomainName = appDomainName;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getAppAssociation() {
        return appAssociation;
    }

    public void setAppAssociation(String appAssociation) {
        this.appAssociation = appAssociation;
    }

    public String getSiteTagSet() {
        return siteTagSet;
    }

    public void setSiteTagSet(String siteTagSet) {
        this.siteTagSet = siteTagSet;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
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

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getHandgeClass() {
        return handgeClass;
    }

    public void setHandgeClass(String handgeClass) {
        this.handgeClass = handgeClass;
    }

    public String getBasicClass() {
        return basicClass;
    }

    public void setBasicClass(String basicClass) {
        this.basicClass = basicClass;
    }

    @Override
    public String toString() {
        return "TableTagUrl{" +
                "app_id=" + app_id +
                ", appName='" + appName + '\'' +
                ", appCategory='" + appCategory + '\'' +
                ", appDomainName='" + appDomainName + '\'' +
                ", webUrl='" + webUrl + '\'' +
                ", appAssociation='" + appAssociation + '\'' +
                ", siteTagSet='" + siteTagSet + '\'' +
                ", appDesc='" + appDesc + '\'' +
                ", keyword2='" + keyword2 + '\'' +
                ", keyword3='" + keyword3 + '\'' +
                ", versionNumber='" + versionNumber + '\'' +
                ", handgeClass='" + handgeClass + '\'' +
                ", basicClass='" + basicClass + '\'' +
                '}';
    }
}

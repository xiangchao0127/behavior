package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/6/19.
 */
@Entity
@Table(name = "app_web_identify")
public class TableAppWebIdentify implements Serializable{

    private static final long serialVersionUID = -5831638455789627265L;

    @Id
    @Column(name = "web_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "web_name")
    private String webName;

    @Column(name = "type")
    private String type;

    @Column(name = "web_protocol")
    private String webProtocol;

    @Column(name = "web_domain_name")
    private String webDomainName;

    @Column(name = "web_url")
    private String webUrl;

    @Column(name = "category")
    private String category;

    @Column(name = "app_association")
    private String appAssociation;

    @Column(name = "site_tag_set")
    private String siteTagSet;

    @Column(name = "web_desc")
    private String webDesc;

    @Column(name = "version_number")
    private String versionNumber;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWebName() {
        return webName;
    }

    public void setWebName(String webName) {
        this.webName = webName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWebProtocol() {
        return webProtocol;
    }

    public void setWebProtocol(String webProtocol) {
        this.webProtocol = webProtocol;
    }

    public String getWebDomainName() {
        return webDomainName;
    }

    public void setWebDomainName(String webDomainName) {
        this.webDomainName = webDomainName;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getWebDesc() {
        return webDesc;
    }

    public void setWebDesc(String webDesc) {
        this.webDesc = webDesc;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    @Override
    public String toString() {
        return "TableAppWebIdentify{" +
                "id=" + id +
                ", webName='" + webName + '\'' +
                ", type='" + type + '\'' +
                ", webProtocol='" + webProtocol + '\'' +
                ", webDomainName='" + webDomainName + '\'' +
                ", webUrl='" + webUrl + '\'' +
                ", category='" + category + '\'' +
                ", appAssociation='" + appAssociation + '\'' +
                ", siteTagSet='" + siteTagSet + '\'' +
                ", webDesc='" + webDesc + '\'' +
                ", versionNumber='" + versionNumber + '\'' +
                '}';
    }
}

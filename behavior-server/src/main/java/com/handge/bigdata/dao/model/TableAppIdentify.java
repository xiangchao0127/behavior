package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author liuqian
 * @date 2018/6/25
 * @Description:
 */
@Entity
@Table(name = "app_identify")
public class TableAppIdentify implements Serializable{
    private static final long serialVersionUID = 6689947617124091807L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "number")
    private String number;

    @Column(name = "name")
    private String name;

    @Column(name = "protocol")
    private String protocol;

    @Column(name = "domain_name")
    private String domainName;

    @Column(name = "port")
    private String port;

    @Column(name = "official_address")
    private String officialAddress;

    @Column(name = "domain")
    private String domain;

    @Column(name = "category")
    private String category;

    @Column(name = "tag_set")
    private String tagSet;

    @Column(name = "description")
    private String description;

    @Column(name = "version_number")
    private String versionNumber;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getOfficialAddress() {
        return officialAddress;
    }

    public void setOfficialAddress(String officialAddress) {
        this.officialAddress = officialAddress;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTagSet() {
        return tagSet;
    }

    public void setTagSet(String tagSet) {
        this.tagSet = tagSet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    @Override
    public String toString() {
        return "TableAppIdentify{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", protocol='" + protocol + '\'' +
                ", domainName='" + domainName + '\'' +
                ", port='" + port + '\'' +
                ", officialAddress='" + officialAddress + '\'' +
                ", domain='" + domain + '\'' +
                ", category='" + category + '\'' +
                ", tagSet='" + tagSet + '\'' +
                ", description='" + description + '\'' +
                ", versionNumber='" + versionNumber + '\'' +
                '}';
    }
}

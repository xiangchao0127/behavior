package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Liujuhao
 * @date 2018/6/4.
 */
@Entity
@Table(name = "auth_api")
public class TableAuthApi implements Serializable {

    private static final long serialVersionUID = -1854348904932295016L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "url")
    private String url;

    @Column(name = "type")
    private String type;

    @Column(name = "is_actived")
    private Boolean isActived;

    @Column(name = "md5")
    private String md5;

    @Column(name = "update_at")
    private Date updateAt;

    @Column(name = "remark")
    private String remark;

    @Column(name = "module_id")
    private int moduleId;

    @OneToOne(mappedBy = "api", cascade = CascadeType.ALL)
    private TableAuthPermission permission;

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsActived() {
        return isActived;
    }

    public void setIsActived(Boolean isActived) {
        this.isActived = isActived;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    @Override
    public String toString() {
        return "TableAuthApi{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", isActived=" + isActived +
                ", md5='" + md5 + '\'' +
                ", updateAt=" + updateAt +
                ", remark='" + remark + '\'' +
                ", moduleId=" + moduleId +
                '}';
    }
}

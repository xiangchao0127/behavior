package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author liuqian
 * @date 2018/6/25
 * @Description:
 */
@Entity
@Table(name = "config_param")
public class TableConfigParam implements Serializable {
    private static final long serialVersionUID = -8902724842115873309L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "param_name")
    private String paramName;

    @Column(name = "param_value")
    private String paramValue;

    @Column(name = "param_unit")
    private String paramUnit;

    @Column(name = "remark")
    private String remark;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamUnit() {
        return paramUnit;
    }

    public void setParamUnit(String paramUnit) {
        this.paramUnit = paramUnit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "TableConfigParam{" +
                "id=" + id +
                ", paramName='" + paramName + '\'' +
                ", paramValue='" + paramValue + '\'' +
                ", paramUnit='" + paramUnit + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}

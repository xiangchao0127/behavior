package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by XC on 2018/6/25.
 */
@Entity
@Table(name = "lib_professional_accomplishment")
public class TableLibProfessionalAccomplishment implements Serializable {

    private static final long serialVersionUID = 6097606347318861653L;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "static_ip")
    private String staticIp;

    @Column(name = "time")
    private String time;

    @Column(name = "loyalty")
    private String loyalty;

    @Column(name = "working_attitude")
    private String workingAttitude;

    @Column(name = "compliance_discipline")
    private String complianceDiscipline;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStaticIp() {
        return staticIp;
    }

    public void setStaticIp(String staticIp) {
        this.staticIp = staticIp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLoyalty() {
        return loyalty;
    }

    public void setLoyalty(String loyalty) {
        this.loyalty = loyalty;
    }

    public String getWorkingAttitude() {
        return workingAttitude;
    }

    public void setWorkingAttitude(String workingAttitude) {
        this.workingAttitude = workingAttitude;
    }

    public String getComplianceDiscipline() {
        return complianceDiscipline;
    }

    public void setComplianceDiscipline(String complianceDiscipline) {
        this.complianceDiscipline = complianceDiscipline;
    }

    @Override
    public String toString() {
        return "TableLibProfessionalAccomplishment{" +
                "id=" + id +
                ", staticIp='" + staticIp + '\'' +
                ", time='" + time + '\'' +
                ", loyalty='" + loyalty + '\'' +
                ", workingAttitude='" + workingAttitude + '\'' +
                ", complianceDiscipline='" + complianceDiscipline + '\'' +
                '}';
    }
}

package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author liuqian
 * @date 2018/6/25
 * @Description:
 */
@Entity
@Table(name = "lib_model")
public class TableLibModel implements Serializable{
    private static final long serialVersionUID = -294373640342124596L;

    @Id
    @Column(name = "model_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "model_desc")
    private String modelDesc;

    @Column(name = "accuracy")
    private float accuracy;

    @Column(name = "recall_rate")
    private float recallRate;

    @Column(name = "training_sample_distribution")
    private String trainingSampleDistribution;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "version_number")
    private String versionNumber;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelDesc() {
        return modelDesc;
    }

    public void setModelDesc(String modelDesc) {
        this.modelDesc = modelDesc;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getRecallRate() {
        return recallRate;
    }

    public void setRecallRate(float recallRate) {
        this.recallRate = recallRate;
    }

    public String getTrainingSampleDistribution() {
        return trainingSampleDistribution;
    }

    public void setTrainingSampleDistribution(String trainingSampleDistribution) {
        this.trainingSampleDistribution = trainingSampleDistribution;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    @Override
    public String toString() {
        return "TableLibModel{" +
                "id=" + id +
                ", modelName='" + modelName + '\'' +
                ", modelDesc='" + modelDesc + '\'' +
                ", accuracy=" + accuracy +
                ", recallRate=" + recallRate +
                ", trainingSampleDistribution='" + trainingSampleDistribution + '\'' +
                ", createTime=" + createTime +
                ", versionNumber='" + versionNumber + '\'' +
                '}';
    }
}

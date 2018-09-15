package com.handge.bigdata.resource.models.response.professional;

/**
 * 模型结果实体类
 * create by xc on 2018/6/14
 */
public class ProfessionalAccomplishmentResult {
    /**
     * ip
     */
    private String staticIp;
    /**
     * 年月
     */
    private String time;
    /**
     * 工作态度等级
     */
    private String workingAttitude;

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

    public String getWorkingAttitude() {
        return workingAttitude;
    }

    public void setWorkingAttitude(String workingAttitude) {
        this.workingAttitude = workingAttitude;
    }

    @Override
    public String toString() {
        return "ProfessionalAccomplishmentResult{" +
                "staticIp='" + staticIp + '\'' +
                ", time='" + time + '\'' +
                ", workingAttitude='" + workingAttitude + '\'' +
                '}';
    }
}

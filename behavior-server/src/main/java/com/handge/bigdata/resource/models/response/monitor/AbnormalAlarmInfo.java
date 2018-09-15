package com.handge.bigdata.resource.models.response.monitor;

/**
 * Created by DaLu Guo on 2018/6/20.
 */
public class AbnormalAlarmInfo {
    /**
     * 部门
     */
    private String department;
    /**
     * 姓名
     */
    private String name;
    /**
     * 工号
     */
    private String number;
    /**
     * 警告时间
     */
    private String time;
    /**
     * 警告内容
     */
    private String content;
    /**
     * 警告描述
     */
    private String desc;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "AbnormalAlarmInfo{" +
                "department='" + department + '\'' +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", time='" + time + '\'' +
                ", content='" + content + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}

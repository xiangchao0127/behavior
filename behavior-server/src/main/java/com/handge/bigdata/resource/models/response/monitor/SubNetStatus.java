package com.handge.bigdata.resource.models.response.monitor;

public class SubNetStatus {
    /**
     * 类型
     */
    private String type;
    /**
     * 人数
     */
    private String numberOfPerson;
//    /**
//     * 占比
//     */
//    private String numberOfRatio;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumberOfPerson() {
        return numberOfPerson;
    }

    public void setNumberOfPerson(String numberOfPerson) {
        this.numberOfPerson = numberOfPerson;
    }

//    public String getNumberOfRatio() {
//        return numberOfRatio;
//    }
//
//    public void setNumberOfRatio(String numberOfRatio) {
//        this.numberOfRatio = numberOfRatio;
//    }

    @Override
    public String toString() {
        return "SubNetStatus{" +
                "type='" + type + '\'' +
                ", numberOfPerson='" + numberOfPerson + '\'' +
//                ", numberOfRatio='" + numberOfRatio + '\'' +
                '}';
    }
}

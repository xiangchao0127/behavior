package com.handge.bigdata.resource.models.response.monitor;

/**
 * @auther Liujuhao
 * @date 2018/5/17.
 */
public class CountryVisitInfo {

    /**
     * 国家名称
     */
    private String name;

    /**
     * 国家英文缩写
     */
    private String nickName;

    /**
     * 访问次数
     */
    private String number;

    /**
     * 经纬度
     */
    private String geo;

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "CountryVisitInfo{" +
                "name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", number='" + number + '\'' +
                ", geo='" + geo + '\'' +
                '}';
    }
}

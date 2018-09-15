package com.handge.bigdata.resource.models.response.organization;

import java.io.Serializable;

/**
 * @author Liujuhao
 * @date 2018/6/22.
 */
public class DeviceItemByEmployee implements Serializable{

    private static final long serialVersionUID = -4724707766288062614L;

    private String deviceId;

    private String deviceNmae;

    private String deviceStaticIp;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceNmae() {
        return deviceNmae;
    }

    public void setDeviceNmae(String deviceNmae) {
        this.deviceNmae = deviceNmae;
    }

    public String getDeviceStaticIp() {
        return deviceStaticIp;
    }

    public void setDeviceStaticIp(String deviceStaticIp) {
        this.deviceStaticIp = deviceStaticIp;
    }

    @Override
    public String toString() {
        return "DeviceItemByEmployee{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceNmae='" + deviceNmae + '\'' +
                ", deviceStaticIp='" + deviceStaticIp + '\'' +
                '}';
    }
}

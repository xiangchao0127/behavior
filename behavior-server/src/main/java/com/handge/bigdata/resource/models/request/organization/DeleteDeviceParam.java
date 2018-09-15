package com.handge.bigdata.resource.models.request.organization;

import java.util.List;

/**
 * @author liuqian
 * @date 2018/6/21
 * @Description:
 */
public class DeleteDeviceParam {
    /**
     * 设备编号集合
     */
    private List<String> deviceIds;

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }

    @Override
    public String toString() {
        return "DeleteDeviceParam{" +
                "deviceIds=" + deviceIds +
                '}';
    }
}

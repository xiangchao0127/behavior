package com.handge.bigdata.resource.service.api.organization;

import com.handge.bigdata.resource.models.request.organization.*;

/**
 * @author liuqian
 * @date 2018/6/21
 * @Description:
 */
public interface IDeviceManager {
    /**
     * 设备列表（模糊查询）
     * @param listDeviceParam
     * @return
     */
    Object listDevice(ListDeviceParam listDeviceParam);

    /**
     * 新增设备
     * @param addDeviceParam
     * @return
     */
    Object addDevice(AddDeviceParam addDeviceParam);

    /**
     * 编辑设备
     * @param editDeviceParam
     * @return
     */
    Object editDevice(EditDeviceParam editDeviceParam);

    /**
     * 禁用设备
     * @param o
     * @return
     */
    Object disableDevice(Object o);

    /**
     * 删除设备
     * @param deleteDeviceParam
     * @return
     */
    Object deleteDevice(DeleteDeviceParam deleteDeviceParam);

    /**
     * 根据员工姓名模糊查询获取姓名工号账号id集合
     * @param findNameAndNumberParam
     * @return
     */
    Object findNameAndNumber(FindNameAndNumberParam findNameAndNumberParam);


}

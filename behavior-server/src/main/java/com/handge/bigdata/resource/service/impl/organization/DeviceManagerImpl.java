package com.handge.bigdata.resource.service.impl.organization;

import com.handge.bigdata.dao.api.ViewDevice;
import com.handge.bigdata.dao.model.TableEntityDevice;
import com.handge.bigdata.dao.model.TableEntityEmployee;
import com.handge.bigdata.resource.models.request.organization.*;
import com.handge.bigdata.resource.models.response.organization.FindNameAndNumber;
import com.handge.bigdata.resource.models.response.organization.ListDevice;
import com.handge.bigdata.resource.service.api.organization.IDeviceManager;
import com.handge.bigdata.utils.PageResults;
import com.handge.bigdata.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuqian
 * @date 2018/6/21
 * @Description:
 */
@Component
public class DeviceManagerImpl implements IDeviceManager {
    @Autowired
    ViewDevice viewDevice;

    @Override
    public PageResults<ListDevice> listDevice(ListDeviceParam listDeviceParam) {
        Page<TableEntityDevice> page = (Page<TableEntityDevice>) viewDevice.selectListDevice(listDeviceParam);
        List<ListDevice> listDevices = new ArrayList<>();
        page.getContent().forEach(o -> {
            listDevices.add(new ListDevice(){
                {
                    this.setId(o.getId()+"");
                    this.setDeviceNumber(o.getNumber());
                    this.setDeviceName(o.getName());
                    this.setType(o.getType());
                    this.setEmployeeName(o.getAccount().getEmployee().getName());
                    this.setEmployeeNumber(o.getAccount().getEmployee().getNumber());
                    this.setMac(o.getMac());
                    this.setIp(o.getStaticIp());
                    this.setStatus(o.getStatus());
                    this.setProperty(o.getProperty());
                }
            });
        });
        return PageUtil.fromJpaPage(listDevices,page);
    }

    @Override
    public Object addDevice(AddDeviceParam addDeviceParam) {
        viewDevice.insertDevice(addDeviceParam);
        return 1;
    }

    @Override
    public Object editDevice(EditDeviceParam editDeviceParam) {
        viewDevice.updateDevice(editDeviceParam);
        return 1;
    }

    @Override
    public Object disableDevice(Object o) {
        return null;
    }

    @Override
    public Object deleteDevice(DeleteDeviceParam deleteDeviceParam) {
        viewDevice.deleteDevice(deleteDeviceParam);
        return 1;
    }

    @Override
    public List<FindNameAndNumber> findNameAndNumber(FindNameAndNumberParam findNameAndNumberParam) {
        List<TableEntityEmployee> tableEntityEmployees = (List<TableEntityEmployee>) viewDevice.selectNameAndNumber(findNameAndNumberParam);
        List<FindNameAndNumber> findNameAndNumbers = new ArrayList<>();
        tableEntityEmployees.forEach(o -> {
            findNameAndNumbers.add(new FindNameAndNumber(){
                {
                    this.setAccountId(String.valueOf(o.getAccount().getId()));
                    this.setDepartment(o.getDepartment().getDepartmentName());
                    this.setEmployeeName(o.getAccount().getEmployee().getName());
                    this.setEmployeeNumber(o.getAccount().getEmployee().getNumber());
                }
            });
        });
        return findNameAndNumbers;
    }
}

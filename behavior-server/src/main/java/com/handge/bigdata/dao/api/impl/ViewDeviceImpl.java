package com.handge.bigdata.dao.api.impl;

import com.handge.bigdata.dao.api.RepositoryAccount;
import com.handge.bigdata.dao.api.RepositoryDevice;
import com.handge.bigdata.dao.api.RepositoryEmployee;
import com.handge.bigdata.dao.api.ViewDevice;
import com.handge.bigdata.dao.model.TableAuthAccount;
import com.handge.bigdata.dao.model.TableEntityDevice;
import com.handge.bigdata.dao.model.TableEntityEmployee;
import com.handge.bigdata.resource.models.request.organization.*;
import com.handge.bigdata.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author liuqian
 * @date 2018/6/21
 * @Description:
 */
@Component
public class ViewDeviceImpl implements ViewDevice {


    @Autowired
    RepositoryDevice repositoryDevice;

    @Autowired
    RepositoryAccount repositoryAccount;

    @Autowired
    RepositoryEmployee repositoryEmployee;

    @Override
    public Page<TableEntityDevice> selectListDevice(ListDeviceParam listDeviceParam) {
        return repositoryDevice.findAll(new Specification<TableEntityDevice>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<TableEntityDevice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<>();
                String name = listDeviceParam.getEmployeeName();
                String number = listDeviceParam.getNumber();
                String deviceName = listDeviceParam.getDeviceName();
                String ip = listDeviceParam.getIp();
                if (StringUtils.notEmpty(name)) {
                    predicateList.add(
                            criteriaBuilder.and(
                                    criteriaBuilder.like(
                                            root.get("account").get("employee").get("name"), "%" + name + "%")));
                }
                if (StringUtils.notEmpty(number)) {
                    predicateList.add(
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(
                                            root.get("account").get("employee").get("number"), number)));
                }
                if (StringUtils.notEmpty(deviceName)) {
                    predicateList.add(
                            criteriaBuilder.and(
                                    criteriaBuilder.like(
                                            root.get("name"), "%" + deviceName + "%")));
                }
                if (StringUtils.notEmpty(ip)) {
                    predicateList.add(
                            criteriaBuilder.and(
                                    criteriaBuilder.like(
                                            root.get("staticIp"), "%" + ip + "%")
                            ));
                }
                return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        }, PageRequest.of(
                listDeviceParam.getPageNo() - 1,
                listDeviceParam.getPageSize(),
                "ASC".equals(listDeviceParam.getOrderBy()) ? Sort.Direction.ASC : Sort.Direction.DESC,
                listDeviceParam.getOrderBy()));
    }

    @Override
    public void insertDevice(AddDeviceParam addDeviceParam) {
        TableEntityDevice tableEntityDevice = new TableEntityDevice();
        //设备名称
        tableEntityDevice.setName(addDeviceParam.getDeviceName());
        Optional<TableAuthAccount> tableAuthAccount = repositoryAccount.findById(Long.valueOf(addDeviceParam.getAccountId()));
        tableEntityDevice.setAccount(tableAuthAccount.get());
        //设备所属
        tableEntityDevice.setProperty(addDeviceParam.getProperty());
        //Mac地址
        tableEntityDevice.setMac(addDeviceParam.getMac());
        //ip地址
        tableEntityDevice.setStaticIp(addDeviceParam.getIp());
        // TODO: 2018/6/21 改为动态
        //是否禁用
        tableEntityDevice.setStatus("0");
        repositoryDevice.save(tableEntityDevice);
    }

    @Override
    public void updateDevice(EditDeviceParam editDeviceParam) {
        Optional<TableEntityDevice> device = repositoryDevice.findById(Long.valueOf(editDeviceParam.getid()));
        TableEntityDevice tableEntityDevice = device.get();
        //设备名称
        tableEntityDevice.setName(editDeviceParam.getDeviceName());
        Optional<TableAuthAccount> tableAuthAccount = repositoryAccount.findById(Long.valueOf(editDeviceParam.getAccountId()));
        tableEntityDevice.setAccount(tableAuthAccount.get());
        //设备所属
        tableEntityDevice.setProperty(editDeviceParam.getProperty());
        //Mac地址
        tableEntityDevice.setMac(editDeviceParam.getMac());
        //ip地址
        tableEntityDevice.setStaticIp(editDeviceParam.getIp());
        //是否禁用
        tableEntityDevice.setStatus(editDeviceParam.getStatus());
        repositoryDevice.save(tableEntityDevice);
    }

    @Override
    public Object disableDevice(Object o) {
        return null;
    }

    @Override
    public void deleteDevice(DeleteDeviceParam deleteDeviceParam) {
        List<TableEntityDevice> tableEntityDevices = new ArrayList<>();
        List<String> list = deleteDeviceParam.getDeviceIds();
        list.forEach(o ->{
            Optional<TableEntityDevice> tableEntityDevice = repositoryDevice.findById(Long.valueOf(o));
            tableEntityDevices.add(tableEntityDevice.get());
        });
        repositoryDevice.deleteAll(tableEntityDevices);
    }

    @Override
    public List<TableEntityEmployee> selectNameAndNumber(FindNameAndNumberParam findNameAndNumberParam) {
        return repositoryEmployee.findAll(new Specification<TableEntityEmployee>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<TableEntityEmployee> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<>();
                String name = findNameAndNumberParam.getName();
                if (StringUtils.notEmpty(name)) {
                    predicateList.add(
                            criteriaBuilder.and(
                                    criteriaBuilder.like(
                                            root.get("name"), "%" + name + "%")));
                }
                return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        });
    }
}

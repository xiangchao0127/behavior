package com.handge.bigdata.dao.api.impl;

import com.handge.bigdata.dao.api.*;
import com.handge.bigdata.dao.model.TableAuthAccount;
import com.handge.bigdata.dao.model.TableAuthRoleBasic;
import com.handge.bigdata.dao.model.TableEntityDepartment;
import com.handge.bigdata.dao.model.TableEntityEmployee;
import com.handge.bigdata.enumeration.EmployeeStatusEnum;
import com.handge.bigdata.resource.models.request.organization.DeleteEmployeeParam;
import com.handge.bigdata.resource.models.request.organization.EmployeeParam;
import com.handge.bigdata.resource.models.request.organization.ListEmployeeParam;
import com.handge.bigdata.resource.models.request.organization.SelectEmployeeParam;
import com.handge.bigdata.resource.models.response.organization.DeviceItemByEmployee;
import com.handge.bigdata.resource.models.response.organization.SelectEmployee;
import com.handge.bigdata.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Liujuhao
 * @date 2018/6/13.
 */
@Component
public class ViewEmployeeImpl implements ViewEmployee {

    @Autowired
    RepositoryEmployee repositoryEmployee;

    @Autowired
    RepositoryDepartment repositoryDepartment;

    @Autowired
    RepositoryAccount repositoryAccount;

    @Autowired
    RepositoryRole repositoryRole;

    @Override
    public void insertEmployee(EmployeeParam employeeParam) {
        TableAuthAccount tableAuthAccount = new TableAuthAccount();
        TableEntityEmployee tableEntityEmployee = new TableEntityEmployee();
        addAndUpdateEmployee(tableAuthAccount, tableEntityEmployee, employeeParam);
    }

    @Override
    public Page<TableEntityEmployee> pageQuery(ListEmployeeParam dto) {
        return repositoryEmployee.findAll(new Specification<TableEntityEmployee>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<TableEntityEmployee> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<>();
                String name = dto.getEmployeeName();
                String number = dto.getEmployeeNumber();
                String department = dto.getDepartmentName();
                String status = dto.getStatus();
                String role = dto.getRole();
                if (StringUtils.notEmpty(name)) {
                    predicateList.add(
                            criteriaBuilder.and(
                                    criteriaBuilder.like(
                                            root.get("name"), "%" + name + "%")));
                }
                if (StringUtils.notEmpty(number)) {
                    predicateList.add(
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(
                                            root.get("number"), number)));
                }
                if (StringUtils.notEmpty(department)) {
                    predicateList.add(
                            criteriaBuilder.and(
                                    criteriaBuilder.like(
                                            root.get("department").get("departmentName"), "%" + department + "%")));
                }
                if (StringUtils.notEmpty(role)) {
                    Join<Object, Object> join = root.join("account").join("roleList");
                    predicateList.add(
                            criteriaBuilder.and(
                                    criteriaBuilder.like(
                                            join.get("roleName"), "%" + role + "%")
                            ));
                }
                if (StringUtils.notEmpty(status)) {
                    predicateList.add(
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(
                                            root.get("status"), status)));
                }
                return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        }, PageRequest.of(
                dto.getPageNo() - 1,
                dto.getPageSize(),
                "ASC".equals(dto.getOrderBy()) ? Sort.Direction.ASC : Sort.Direction.DESC,
                dto.getOrderBy()));
    }

    @Override
    public void updateEmployee(EmployeeParam employeeParam) {
        Optional<TableAuthAccount> tableAuthAccount = repositoryAccount.findById(Long.valueOf(employeeParam.getAccountId()));
        TableEntityEmployee tableEntityEmployee = tableAuthAccount.get().getEmployee();
        addAndUpdateEmployee(tableAuthAccount.get(), tableEntityEmployee, employeeParam);
    }

    @Override
    public SelectEmployee selectEmployee(SelectEmployeeParam selectEmployeeParam) {
        Optional<TableAuthAccount> tableAuthAccount = repositoryAccount.findById(Long.valueOf(selectEmployeeParam.getAccountId()));
        TableEntityEmployee tableEntityEmployee = tableAuthAccount.get().getEmployee();
        SelectEmployee selectEmployee = new SelectEmployee();
        //账号id
        selectEmployee.setAccountId(String.valueOf(tableAuthAccount.get().getId()));
        //姓名
        selectEmployee.setEmployeeName(tableEntityEmployee.getName());
        //工号
        selectEmployee.setEmployeeNumber(tableEntityEmployee.getNumber());
        //部门名称
        selectEmployee.setDepartmentName(tableEntityEmployee.getDepartment().getDepartmentName());
        //职位
        selectEmployee.setPosition(tableEntityEmployee.getPost());
        //工龄
        selectEmployee.setSeniority(String.valueOf(tableEntityEmployee.getSeniority()));
        //岗龄
        selectEmployee.setPostAge(String.valueOf(tableEntityEmployee.getPostAge()));
        //状态
        selectEmployee.setStatus(tableEntityEmployee.getStatus());
        //角色
        Set<TableAuthRoleBasic> roleList = tableAuthAccount.get().getRoleList();
        List<String> roles = new ArrayList<>();
        for (TableAuthRoleBasic tableAuthRoleBasic : roleList) {
            roles.add(tableAuthRoleBasic.getRoleName());
        }
        selectEmployee.setRoles(roles);
        //账号状态
        selectEmployee.setAccountStatus(tableAuthAccount.get().getStatus());
        //用户名
        selectEmployee.setIdCard(tableAuthAccount.get().getUsername());
        //密码不需要返回
        //联系电话
        selectEmployee.setPhone(tableEntityEmployee.getPhone());
        //邮箱
        selectEmployee.setEmail(tableEntityEmployee.getEmail());
        //身份证
        selectEmployee.setIdCard(tableEntityEmployee.getIdCard());
        //居住地址
        selectEmployee.setHomeAddress(tableEntityEmployee.getHomeAddress());
        //设备信息（列表）
        List<DeviceItemByEmployee> deviceItemByEmployeeList = tableAuthAccount.get().getDeviceList().stream().map(o -> new DeviceItemByEmployee() {
            {
                this.setDeviceId(o.getId() + "");
                this.setDeviceNmae(o.getName());
                this.setDeviceStaticIp(o.getStaticIp());
            }
        }).collect(Collectors.toList());
        selectEmployee.setDeviceByEmployeeList(deviceItemByEmployeeList);
        return selectEmployee;
    }

    @Override
    public void deleteEmployee(DeleteEmployeeParam deleteEmployeeParam) {
        List<TableAuthAccount> list = new ArrayList<>();
        for (String id : deleteEmployeeParam.getAccountIds()) {
            Optional<TableAuthAccount> tableAuthAccount = repositoryAccount.findById(Long.valueOf(id));
            list.add(tableAuthAccount.get());
        }
        repositoryAccount.deleteAll(list);
    }

    /**
     * 新增员工和修改员工信息公用代码
     *
     * @param tableEntityEmployee
     * @param employeeParam
     */
    private void addAndUpdateEmployee(TableAuthAccount tableAuthAccount, TableEntityEmployee tableEntityEmployee, EmployeeParam employeeParam) {
        TableEntityDepartment tableEntityDepartment = repositoryDepartment.findByDepartmentName(employeeParam.getDepartmentName());
        //员工姓名
        tableEntityEmployee.setName(employeeParam.getEmployeeName());
        //工号
        tableEntityEmployee.setNumber(employeeParam.getEmployeeNumber());
        //部门
        tableEntityEmployee.setDepartment(tableEntityDepartment);
        //职位
        tableEntityEmployee.setPost(employeeParam.getPosition());
        if (StringUtils.notEmpty(employeeParam.getSeniority())) {
            //工龄
            tableEntityEmployee.setSeniority(BigDecimal.valueOf(Long.valueOf(employeeParam.getSeniority())));
        }
        if (StringUtils.notEmpty(employeeParam.getPostAge())) {
            //岗龄
            tableEntityEmployee.setPostAge(BigDecimal.valueOf(Long.valueOf(employeeParam.getPostAge())));
        }
        //状态
        tableEntityEmployee.setStatus(EmployeeStatusEnum.getStatusByDesc(employeeParam.getStatus()));
        //创建时间
        tableEntityEmployee.setCreateAt(new Date());
        //联系电话
        tableEntityEmployee.setPhone(employeeParam.getPhone());
        //邮箱
        tableEntityEmployee.setEmail(employeeParam.getEmail());
        //居住地址
        tableEntityEmployee.setHomeAddress(employeeParam.getHomeAddress());
        //身份证
        tableEntityEmployee.setIdCard(employeeParam.getIdCard());

        //账户信息设置
        //账号状态
        tableAuthAccount.setStatus("正常");
        //角色
        List<String> roles = employeeParam.getRoles();
        Set<TableAuthRoleBasic> set = new HashSet<>();
        for (String role : roles
                ) {
            TableAuthRoleBasic tableAuthRoleBasic = repositoryRole.findByRoleName(role);
            set.add(tableAuthRoleBasic);
        }
        tableAuthAccount.setRoleList(set);
        //用户名
        tableAuthAccount.setUsername(employeeParam.getUsername());
        //密码
        tableAuthAccount.setPassword(employeeParam.getPassword());
        //盐
        tableAuthAccount.setSalt(employeeParam.getUsername());

        tableAuthAccount.setEmployee(tableEntityEmployee);
        repositoryAccount.save(tableAuthAccount);
    }

}

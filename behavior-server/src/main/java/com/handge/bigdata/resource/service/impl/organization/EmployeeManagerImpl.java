package com.handge.bigdata.resource.service.impl.organization;

import com.handge.bigdata.auth.ShiroPwdSecurity;
import com.handge.bigdata.dao.api.ViewEmployee;
import com.handge.bigdata.dao.model.TableEntityEmployee;
import com.handge.bigdata.resource.models.request.organization.DeleteEmployeeParam;
import com.handge.bigdata.resource.models.request.organization.EmployeeParam;
import com.handge.bigdata.resource.models.request.organization.ListEmployeeParam;
import com.handge.bigdata.resource.models.request.organization.SelectEmployeeParam;
import com.handge.bigdata.resource.models.response.organization.ListEmployeeInfo;
import com.handge.bigdata.resource.models.response.organization.SelectEmployee;
import com.handge.bigdata.resource.service.api.organization.IEmployeeManager;
import com.handge.bigdata.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Liujuhao
 * @date 2018/6/13.
 */
@Component
public class EmployeeManagerImpl implements IEmployeeManager {

    @Autowired
    ViewEmployee viewEmployee;

    @Override
    public Object listEmployee(ListEmployeeParam listEmployeeParam) {
        Page<TableEntityEmployee> page = viewEmployee.pageQuery(listEmployeeParam);
        List<ListEmployeeInfo> listEmployeeInfos = new ArrayList<>();
        page.getContent().forEach(o -> {
            listEmployeeInfos.add(new ListEmployeeInfo(){
                {
                    this.setAccountId(o.getId()+"");
                    this.setEmployeeName(o.getName());
                    this.setEmployeeNumber(o.getNumber());
                    this.setDepartmentName(o.getDepartment().getDepartmentName());
                    List<String> roles = new ArrayList<>();
                    o.getAccount().getRoleList().forEach(role -> roles.add(role.getRoleName()));
                    this.setRoles(roles);
                    this.setPosition(o.getPositionalTitles());
                    this.setStatus(o.getStatus());
                    this.setAccountStatus(o.getAccount().getStatus());
                }
            });
        });
        return PageUtil.fromJpaPage(listEmployeeInfos, page);
    }

    @Override
    public Object addEmployee(EmployeeParam addEmployeeParam) {
        //密码加密
        String cipher = ShiroPwdSecurity.securityTransform(addEmployeeParam.getPassword(), addEmployeeParam.getUsername());
        addEmployeeParam.setPassword(cipher);
        viewEmployee.insertEmployee(addEmployeeParam);
        return 1;
    }

    @Override
    public Object alterEmployee(EmployeeParam employeeParam) {
        //密码加密
        String cipher = ShiroPwdSecurity.securityTransform(employeeParam.getPassword(), employeeParam.getUsername());
        employeeParam.setPassword(cipher);
        viewEmployee.updateEmployee(employeeParam);
        return 1;
    }

    @Override
    public SelectEmployee showEmployeeDetail(SelectEmployeeParam selectEmployeeParam) {
        return (SelectEmployee) viewEmployee.selectEmployee(selectEmployeeParam);
    }

    @Override
    public Object deleteEmployee(DeleteEmployeeParam deleteEmployeeParam) {
        viewEmployee.deleteEmployee(deleteEmployeeParam);
        return 1;
    }
}

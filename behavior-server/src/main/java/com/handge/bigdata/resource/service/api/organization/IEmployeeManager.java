package com.handge.bigdata.resource.service.api.organization;

import com.handge.bigdata.resource.models.request.organization.DeleteEmployeeParam;
import com.handge.bigdata.resource.models.request.organization.EmployeeParam;
import com.handge.bigdata.resource.models.request.organization.ListEmployeeParam;
import com.handge.bigdata.resource.models.request.organization.SelectEmployeeParam;

/**
 * @author Liujuhao
 * @date 2018/6/13.
 */
public interface IEmployeeManager {

    /**
     * 员工列表（按条件搜索）
     *
     * @param listEmployeeParam
     * @return
     */
    Object listEmployee(ListEmployeeParam listEmployeeParam);

    /**
     * 新增员工
     *
     * @param employeeParam
     * @return
     */
    Object addEmployee(EmployeeParam employeeParam);

    /**
     * 编辑员工
     *
     * @param employeeParam
     * @return
     */
    Object alterEmployee(EmployeeParam employeeParam);

    /**
     * 查看员工详情
     *
     * @param selectEmployeeParam
     */
    Object showEmployeeDetail(SelectEmployeeParam selectEmployeeParam);

    /**
     * 删除员工
     *
     * @param deleteEmployeeParam
     */
    Object deleteEmployee(DeleteEmployeeParam deleteEmployeeParam);
}

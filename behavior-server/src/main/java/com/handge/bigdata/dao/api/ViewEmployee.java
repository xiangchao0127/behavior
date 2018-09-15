package com.handge.bigdata.dao.api;

import com.handge.bigdata.dao.model.TableEntityEmployee;
import com.handge.bigdata.resource.models.request.organization.DeleteEmployeeParam;
import com.handge.bigdata.resource.models.request.organization.EmployeeParam;
import com.handge.bigdata.resource.models.request.organization.ListEmployeeParam;
import com.handge.bigdata.resource.models.request.organization.SelectEmployeeParam;
import org.springframework.data.domain.Page;

/**
 * @author Liujuhao
 * @date 2018/6/13.
 */
public interface ViewEmployee {
    /**
     * 添加员工信息
     *
     * @param employeeParam
     */
    void insertEmployee(EmployeeParam employeeParam);

    /**
     * 按条件分页查询
     *
     * @param dto
     * @return
     */
    Page<TableEntityEmployee> pageQuery(ListEmployeeParam dto);

    /**
     * 修改员工信息
     *
     * @param employeeParam
     */
    void updateEmployee(EmployeeParam employeeParam);

    /**
     * 查询员工信息
     *
     * @param  selectEmployeeParam
     * @return
     */
    Object selectEmployee(SelectEmployeeParam selectEmployeeParam);

    /**
     * 删除员工信息
     *
     * @param deleteEmployeeParam
     */
    void deleteEmployee(DeleteEmployeeParam deleteEmployeeParam);
}

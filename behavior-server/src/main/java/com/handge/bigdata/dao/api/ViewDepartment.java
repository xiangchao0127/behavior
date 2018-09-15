package com.handge.bigdata.dao.api;

import com.handge.bigdata.resource.models.request.organization.DeleteDepartmentParam;
import com.handge.bigdata.resource.models.request.organization.InsertDepartmentParam;
import com.handge.bigdata.resource.models.request.organization.ListMemberParam;
import com.handge.bigdata.resource.models.request.organization.UpdateDepartmentParam;
import com.handge.bigdata.resource.models.response.organization.MoveEmployee;

/**
 * @author Liujuhao
 * @date 2018/6/13.
 */
public interface ViewDepartment {

    /**
     * 查询公司部门架构
     * @return
     */
    Object selectDepartmentStructure();

    /**
     * 部门列表
     *
     * @return
     */
    Object listDepartment();

    /**
     * 新增部门
     *
     * @param insertDepartmentParam
     * @return
     */
    void insertDepartment(InsertDepartmentParam insertDepartmentParam);

    /**
     * 编辑部门
     *
     * @param updateDepartmentParam
     * @return
     */
    void updateDepartment(UpdateDepartmentParam updateDepartmentParam);

    /**
     * 部门成员列表
     *
     * @param listMemberParam
     * @return
     */
    Object listMember(ListMemberParam listMemberParam);

    /**
     * 删除部门
     *
     * @param deleteDepartmentParam
     * @return
     */
    void deleteDepartment(DeleteDepartmentParam deleteDepartmentParam);

    /**
     * 查询各部门及其部门成员
     * @return
     */
    Object selectDepartmentAndMembers();

    /**
     * 人员调动
     * @param moveEmployee
     * @return
     */
    void moveEmployee(MoveEmployee moveEmployee);
}

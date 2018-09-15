package com.handge.bigdata.resource.service.api.organization;

import com.handge.bigdata.resource.models.request.organization.DeleteDepartmentParam;
import com.handge.bigdata.resource.models.request.organization.InsertDepartmentParam;
import com.handge.bigdata.resource.models.request.organization.ListMemberParam;
import com.handge.bigdata.resource.models.request.organization.UpdateDepartmentParam;
import com.handge.bigdata.resource.models.response.organization.MoveEmployee;

/**
 * @author Liujuhao
 * @date 2018/6/13.
 */
public interface IDepartmentManager {

    /**
     * 显示公司部门架构
     *
     * @return
     */
    Object showDepartmentStructure();

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
    Object addDepartment(InsertDepartmentParam insertDepartmentParam);

    /**
     * 编辑部门
     *
     * @param updateDepartmentParam
     * @return
     */
    Object alterDepartment(UpdateDepartmentParam updateDepartmentParam);

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
    Object deleteDepartment(DeleteDepartmentParam deleteDepartmentParam);

    /**
     * 获取各部门及其部门成员
     * @return
     */
    Object findDepartmentAndMembers();

    /**
     * 人员调动
     * @param moveEmployee
     * @return
     */
    Object moveEmployee(MoveEmployee moveEmployee);
}

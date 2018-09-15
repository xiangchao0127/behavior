package com.handge.bigdata.resource.service.api.organization;

import com.handge.bigdata.resource.models.request.organization.*;

/**
 * @author Liujuhao
 * @date 2018/6/13.
 */
public interface IRoleManager {

    /**
     * 岗位（角色）列表（按条件搜索）
     *
     * @param condition
     * @return
     */
    Object listRole(ListRoleParam condition);

    /**
     * 新增岗位（角色）
     *
     * @param model
     * @return
     */
    Object addRole(RoleParam model);

    /**
     * 编辑岗位（角色）
     *
     * @param model
     * @return
     */
    Object alterRole(RoleParam model);

    /**
     * 删除岗位（角色）
     *
     * @param model
     * @return
     */
    Object deleteRole(DeleteRoleParam model);

    /**
     * 岗位（角色）下的成员管理
     *
     * @param model
     * @return
     */
    Object alterMember(RoleMemberParam model);

    /**
     * 查看岗位（角色）下的成员以及非成员
     * @return
     */
    Object listMemberByRole(ListMemberByRoleParam model);

}

package com.handge.bigdata.dao.api;

import com.handge.bigdata.dao.model.TableAuthAccount;
import com.handge.bigdata.dao.model.TableAuthRoleBasic;
import com.handge.bigdata.resource.models.request.organization.ListRoleParam;
import com.handge.bigdata.resource.models.request.organization.RoleMemberParam;
import com.handge.bigdata.resource.models.request.organization.RoleParam;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Liujuhao
 * @date 2018/6/13.
 */
public interface ViewRole {

    Page<TableAuthRoleBasic> pageQuery(ListRoleParam dto);

    void insertRole(RoleParam dto);

    void updateRole(RoleParam dto);

    void deleteRole(List<String> ids);

    void bindRoleForUser(RoleMemberParam dto);

    List<TableAuthAccount> queryMemberByRole(String roleId);

    List<TableAuthAccount> queryMemberByRoleNot(String roleId);

}

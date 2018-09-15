package com.handge.bigdata.resource.service.impl.organization;

import com.handge.bigdata.dao.api.ViewRole;
import com.handge.bigdata.dao.model.TableAuthAccount;
import com.handge.bigdata.dao.model.TableAuthRoleBasic;
import com.handge.bigdata.resource.models.request.organization.*;
import com.handge.bigdata.resource.models.response.organization.ListRoleInfo;
import com.handge.bigdata.resource.models.response.organization.RoleMemberManage;
import com.handge.bigdata.resource.models.response.organization.RoleMemberManageSection;
import com.handge.bigdata.resource.service.api.organization.IRoleManager;
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
public class RoleManagerImpl implements IRoleManager {

    @Autowired
    ViewRole viewRole;

    @Override
    public Object listRole(ListRoleParam condition) {
        Page<TableAuthRoleBasic> page = viewRole.pageQuery(condition);
        List<ListRoleInfo> roleInfos = new ArrayList<>();
        page.getContent().forEach(o -> {
            roleInfos.add(new ListRoleInfo() {
                {
                    this.setDepartmentName(o.getDepartment().getDepartmentName());
                    this.setId(o.getId() + "");
                    this.setRoleDescription(o.getDescription());
                    this.setRoleName(o.getRoleName());
                    this.setMembers(new ArrayList<String>() {
                        {
                            o.getAccountList().forEach(act -> {
                                this.add(act.getEmployee().getName());
                            });
                        }
                    });
                }
            });
        });
        return PageUtil.fromJpaPage(roleInfos, page);
    }

    @Override
    public Object addRole(RoleParam model) {
        viewRole.insertRole(model);
        return 1;
    }

    @Override
    public Object alterRole(RoleParam model) {
        viewRole.updateRole(model);
        return 1;
    }

    @Override
    public Object deleteRole(DeleteRoleParam model) {
        viewRole.deleteRole(model.getRoleIds());
        return 1;
    }

    @Override
    public Object alterMember(RoleMemberParam model) {
        viewRole.bindRoleForUser(model);
        return 1;
    }

    @Override
    public Object listMemberByRole(ListMemberByRoleParam model) {
        List<TableAuthAccount> roleAccountList = viewRole.queryMemberByRole(model.getRoleId());
        List<TableAuthAccount> notRoleAccountList = viewRole.queryMemberByRoleNot(model.getRoleId());
        RoleMemberManage roleMemberManage = new RoleMemberManage();
        List<RoleMemberManageSection> roleMembers = new ArrayList<>();
        roleAccountList.forEach(
                acct -> {
                    roleMembers.add(new RoleMemberManageSection() {
                        {
                            this.setId(acct.getId());
                            this.setName(acct.getEmployee().getName());
                        }
                    });
                }
        );
        List<RoleMemberManageSection> notRoleMembers = new ArrayList<>();
        notRoleAccountList.forEach(
                acct -> {
                    notRoleMembers.add(new RoleMemberManageSection() {
                        {
                            this.setId(acct.getId());
                            this.setName(acct.getEmployee().getName());
                        }
                    });
                }
        );
        roleMemberManage.setSelectedMemberList(roleMembers);
        roleMemberManage.setOptionalMemberList(notRoleMembers);
        return roleMemberManage;
    }
}

package com.handge.bigdata.dao.api.impl;

import com.handge.bigdata.dao.api.RepositoryAccount;
import com.handge.bigdata.dao.api.RepositoryDepartment;
import com.handge.bigdata.dao.api.RepositoryRole;
import com.handge.bigdata.dao.api.ViewRole;
import com.handge.bigdata.dao.model.TableAuthAccount;
import com.handge.bigdata.dao.model.TableAuthRoleBasic;
import com.handge.bigdata.dao.model.TableEntityDepartment;
import com.handge.bigdata.resource.models.request.organization.ListRoleParam;
import com.handge.bigdata.resource.models.request.organization.RoleMemberParam;
import com.handge.bigdata.resource.models.request.organization.RoleParam;
import com.handge.bigdata.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Liujuhao
 * @date 2018/6/13.
 */

@Component
public class ViewRoleImpl implements ViewRole {

    @Autowired
    RepositoryRole repositoryRole;

    @Autowired
    RepositoryDepartment repositoryDepartment;

    @Autowired
    RepositoryAccount repositoryAccount;

    @Override
    public Page<TableAuthRoleBasic> pageQuery(ListRoleParam dto) {

        return repositoryRole.findAll(new Specification<TableAuthRoleBasic>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<TableAuthRoleBasic> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<>();
                String roleName = dto.getRoleName();
                String departmentName = dto.getDepartmentName();
                String roleDescription = dto.getRoleDescription();
                String departmentMember = dto.getDepartmentMember();
                if (StringUtils.notEmpty(roleName)) {
                    predicateList.add(
                            criteriaBuilder.and(
                                    criteriaBuilder.like(
                                            root.get("roleName"), "%" + roleName + "%")));
                }
                if (StringUtils.notEmpty(departmentName)) {
                    predicateList.add(
                            criteriaBuilder.and(
                                    criteriaBuilder.like(
                                            root.get("department").get("departmentName"), "%" + departmentName + "%")));
                }
                if (StringUtils.notEmpty(roleDescription)) {
                    predicateList.add(
                            criteriaBuilder.and(
                                    criteriaBuilder.like(
                                            root.get("description"), "%" + roleDescription + "%")));
                }
                if (StringUtils.notEmpty(departmentMember)) {
                    Join<Object, Object> join = root.join("accountList").join("employee");
                    predicateList.add(
                            criteriaBuilder.and(
                                    criteriaBuilder.like(
                                            join.get("name"), "%" + departmentMember + "%")));
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
    public void insertRole(RoleParam dto) {
        TableAuthRoleBasic role = new TableAuthRoleBasic();
        Optional<TableEntityDepartment> departmentOpt = repositoryDepartment.findById(Long.valueOf(dto.getDepartment()));
        role.setRoleName(dto.getRoleName());
        role.setDepartment(departmentOpt.get());
        role.setDescription(dto.getRoleDescription());
        repositoryRole.save(role);
    }

    @Override
    public void updateRole(RoleParam dto) {
        Optional<TableAuthRoleBasic> roleOpt = repositoryRole.findById(Long.valueOf(dto.getRoleId()));
        Optional<TableEntityDepartment> departmentOpt = repositoryDepartment.findById(Long.valueOf(dto.getDepartment()));
        TableAuthRoleBasic role = roleOpt.get();
        role.setRoleName(dto.getRoleName());
        role.setDepartment(departmentOpt.get());
        role.setDescription(dto.getRoleDescription());
        repositoryRole.save(role);
    }

    @Override
    public void deleteRole(List<String> ids) {
        List<TableAuthRoleBasic> roleList = new ArrayList<>();
        for (String id : ids) {
            Optional<TableAuthRoleBasic> roleOpt = repositoryRole.findById(Long.valueOf(id));
            roleList.add(roleOpt.get());
        }
        repositoryRole.deleteAll(roleList);
    }

    @Override
    public void bindRoleForUser(RoleMemberParam dto) {
        Optional<TableAuthRoleBasic> roleOpt = repositoryRole.findById(Long.valueOf(dto.getRoleId()));
        TableAuthRoleBasic role = roleOpt.get();
        Set<TableAuthAccount> dbAccountList = role.getAccountList();
        List<String> currentAccountList = dto.getAccountList();

        //原先数据库中存在，但现在不应存在的
        List<TableAuthAccount> cancelList = new ArrayList<>();
        //原先数据库中存在，现在依然存在的
        List<String> existAccountList = new ArrayList<>();

        for (TableAuthAccount account : dbAccountList) {
            String accountId = String.valueOf(account.getId());
            if (!currentAccountList.contains(accountId)) {
                cancelList.add(account);
            } else {
                existAccountList.add(accountId);
            }
        }
        //原先数据库不存在，但现在应该存在的
        currentAccountList.removeAll(existAccountList);
        dbAccountList.removeAll(cancelList);
        for (String id: currentAccountList) {
            Optional<TableAuthAccount> newAccount = repositoryAccount.findById(Long.valueOf(id));
            dbAccountList.add(newAccount.get());
        }
        repositoryRole.save(role);
    }

    @Override
    public List<TableAuthAccount> queryMemberByRole(String roleId) {
        Optional<TableAuthRoleBasic> roleOpt = repositoryRole.findById(Long.valueOf(roleId));
        TableAuthRoleBasic role = roleOpt.get();
        Set<TableAuthAccount> accountSet = role.getAccountList();
        List<TableAuthAccount> accountList = new ArrayList<>(accountSet);
        return accountList;
    }

    @Override
    public List<TableAuthAccount> queryMemberByRoleNot(String roleId) {
        List<TableAuthAccount> accountList = new ArrayList<>();
        repositoryAccount.findAll().forEach(o -> {
            boolean noneRole = o.getRoleList().stream().noneMatch(o2 -> o2.getId() == Long.valueOf(roleId));
            if (noneRole) {
                accountList.add(o);
            }
        });
        return accountList;
    }
}

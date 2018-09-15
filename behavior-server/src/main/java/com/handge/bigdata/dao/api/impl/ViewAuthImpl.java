package com.handge.bigdata.dao.api.impl;

import com.handge.bigdata.dao.api.*;
import com.handge.bigdata.dao.model.TableAuthAccount;
import com.handge.bigdata.dao.model.TableAuthApi;
import com.handge.bigdata.dao.model.TableAuthPermission;
import com.handge.bigdata.dao.model.TableAuthRoleBasic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Liujuhao
 * @date 2018/6/7.
 */

@Component
public class ViewAuthImpl implements ViewAuth {

    private static String ADMIN_ROLE_NAME = "admin";

    @Autowired
    RepositoryPermission repositoryPermission;

    @Autowired
    RepositoryApi repositoryApi;

    @Autowired
    RepositoryRole repositoryRole;

    @Autowired
    RepositoryAccount repositoryAccount;

    @Override
    public void updateAllApi(List<TableAuthApi> nowApiList) {

        List<TableAuthApi> dbApiList = repositoryApi.getALL();

        Iterator<TableAuthApi> deleteApiList = dbApiList.iterator();

        Iterator<TableAuthApi> addApiList = nowApiList.iterator();

        List<TableAuthApi> tempCoList = new ArrayList<>();

        while (deleteApiList.hasNext()) {
            TableAuthApi currentApi = deleteApiList.next();
            for (TableAuthApi nowApi : nowApiList) {
                boolean existed = currentApi.getUrl().equals(nowApi.getUrl()) && currentApi.getType().equals(nowApi.getType());
                if (existed) {
                    tempCoList.add(currentApi);
                    deleteApiList.remove();
                }
            }
        }

        repositoryApi.deleteAll(dbApiList);

        while (addApiList.hasNext()) {
            TableAuthApi currentApi = addApiList.next();
            for (TableAuthApi coApi : tempCoList) {
                boolean existed = currentApi.getUrl().equals(coApi.getUrl()) && currentApi.getType().equals(coApi.getType());
                if (existed) {
                    addApiList.remove();
                }
            }
        }

        repositoryApi.saveAll(nowApiList);
    }

    @Override
    public void updateByNewApi() {

        List<Long> dbPermissionList = repositoryPermission.getAllApiId();
        List<TableAuthApi> nowApiIdList = repositoryApi.getALL();
        List<TableAuthPermission> addPermissionList = new ArrayList<>();
        for (TableAuthApi api : nowApiIdList) {
            Long apiId = api.getId();
            if (!containsNumberItem(dbPermissionList, apiId)) {
                TableAuthPermission permission = new TableAuthPermission();
                permission.setApi_id(api);
                permission.setAction(api.getType());
                String pattern = api.getUrl().split("/")[1] + ":" + api.getType().toLowerCase() + ":" + apiId;
                permission.setPattern(pattern);
                addPermissionList.add(permission);
            }
        }
        repositoryPermission.saveAll(addPermissionList);
    }

    private <T extends Number> boolean containsNumberItem(List<T> list, T item) {
        for (Number number : list) {
            if (number.byteValue() == item.byteValue()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void grantAllPermissionForAdmin() {
        TableAuthRoleBasic admin = repositoryRole.findByRoleName(ADMIN_ROLE_NAME);
        if (admin == null) {
            admin = new TableAuthRoleBasic();
            admin.setRoleName(ADMIN_ROLE_NAME);
        }
        List<TableAuthPermission> pemissionList = repositoryPermission.getAll();
        Set<TableAuthPermission> dbAdminPermissionList = admin.getPermissionList();
        if (dbAdminPermissionList == null) {
            dbAdminPermissionList = new HashSet<>();
            admin.setPermissionList(dbAdminPermissionList);
        }
        for (TableAuthPermission permission : pemissionList) {
            boolean exist = false;
            for (TableAuthPermission dbPermission : dbAdminPermissionList) {
                if (dbPermission.getId() == permission.getId()) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                dbAdminPermissionList.add(permission);
            }
        }
        repositoryRole.save(admin);
    }

    @Override
    public void alterPassword(String username, String newPassword) {
        TableAuthAccount userAccount = repositoryAccount.findByUsername(username);
        userAccount.setPassword(newPassword);
        repositoryAccount.save(userAccount);
    }


}

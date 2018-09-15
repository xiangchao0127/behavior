package com.handge.bigdata.dao.api;

import com.handge.bigdata.dao.model.TableAuthApi;

import java.util.List;

/**
 * @author Liujuhao
 * @date 2018/6/7.
 */
public interface ViewAuth {

    void updateAllApi(List<TableAuthApi> apiList);

    void updateByNewApi();

    void grantAllPermissionForAdmin();

    void alterPassword(String username, String newPassword);
}

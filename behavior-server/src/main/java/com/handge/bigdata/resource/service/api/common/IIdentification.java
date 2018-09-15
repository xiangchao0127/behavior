package com.handge.bigdata.resource.service.api.common;

import com.handge.bigdata.resource.models.request.common.ChangePasswordFormParam;
import com.handge.bigdata.resource.models.request.common.LoginFormParam;

/**
 * @author Liujuhao
 * @date 2018/6/27.
 */
public interface IIdentification {


    /**
     * 用户登录
     *
     * @param loginFormParam
     * @return
     */
    Object login(LoginFormParam loginFormParam);

    /**
     * 没有登录时跳转（后台）
     *
     * @return
     */
    Object unauth();

    /**
     * 用户登出
     *
     * @return
     */
    Object logout();

    /**
     * 修改账户密码
     *
     * @return
     */
    Object changePassword(ChangePasswordFormParam changePasswordFormParam);
}

package com.handge.bigdata.resource.service.impl.common;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.auth.ShiroPwdSecurity;
import com.handge.bigdata.context.CustomProperty;
import com.handge.bigdata.dao.api.RepositoryAccount;
import com.handge.bigdata.dao.api.ViewAuth;
import com.handge.bigdata.dao.model.TableAuthAccount;
import com.handge.bigdata.dao.model.TableAuthRoleBasic;
import com.handge.bigdata.enumeration.ExceptionWrapperEnum;
import com.handge.bigdata.enumeration.RolePropertyEnum;
import com.handge.bigdata.resource.models.request.common.ChangePasswordFormParam;
import com.handge.bigdata.resource.models.request.common.LoginFormParam;
import com.handge.bigdata.resource.models.response.common.LoginRes;
import com.handge.bigdata.resource.models.response.common.UserInfo;
import com.handge.bigdata.resource.service.api.common.IIdentification;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Liujuhao
 * @date 2018/6/27.
 */

@Component
public class IdentificationImpl implements IIdentification {

    @Autowired
    CustomProperty customProperty;

    @Autowired
    ViewAuth viewAuth;

    @Autowired
    RepositoryAccount repositoryAccount;

    @Override
    public Object login(LoginFormParam loginFormParam) {
        LoginRes loginRes = new LoginRes();
        String username = loginFormParam.getUsername();
        String password = loginFormParam.getPassword();
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, ShiroPwdSecurity.securityTransform(password, username));
        subject.login(token);
        loginRes.setToken(subject.getSession().getId().toString());
        loginRes.setDescription(customProperty.successDescription);
        UserInfo userInfo = new UserInfo();
        loginRes.setUserInfo(userInfo);
        TableAuthAccount user = (TableAuthAccount) subject.getPrincipals().getPrimaryPrincipal();
        userInfo.setDepartment(user.getEmployee().getDepartment().getDepartmentName());
        userInfo.setEmployeeName(user.getEmployee().getName());
        userInfo.setNumber(user.getEmployee().getNumber());
        List<String> rolePropertyList = user.getRoleList().stream().map(TableAuthRoleBasic::getRoleProperty).collect(Collectors.toList());
        Collections.sort(rolePropertyList, String::compareTo);
        userInfo.setRoleList(rolePropertyList);
        if (!isAccess(rolePropertyList)) {
            throw new UnifiedException(userInfo.getEmployeeName(), ExceptionWrapperEnum.Auth_Role_Grade_Error);
        }
        if (customProperty.isValidateIP) {
            if (!isValidHyperViosr(rolePropertyList, subject.getSession().getHost())) {
                throw new UnifiedException(subject.getSession().getHost(), ExceptionWrapperEnum.Auth_IP_VALID_HYPER_ERROR);
            }
        }
        return loginRes;
    }

    /**
     * 判断用户角色是否可以进入系统
     *
     * @return
     */
    private boolean isAccess(List<String> rolePropertyList) {
        return !(rolePropertyList.isEmpty() || (rolePropertyList.size() == 1 && rolePropertyList.contains(RolePropertyEnum.Default.getCode())));
    }

    /**
     * 判断IP是否允许超级管理员登录
     *
     * @param rolePropertyList
     * @param host
     * @return
     */
    private boolean isValidHyperViosr(List<String> rolePropertyList, String host) {
        return !rolePropertyList.contains(RolePropertyEnum.HyperVisor.getCode()) || customProperty.allowIPs.contains(host);
    }

    /**
     * 认证失败或认证过期
     *
     * @return
     */
    @Override
    public Object unauth() {
        LoginRes loginRes = new LoginRes();
        loginRes.setCode(ExceptionWrapperEnum.Auth_TOKEN_ERROR.getCode());
        loginRes.setDescription(ExceptionWrapperEnum.Auth_TOKEN_ERROR.getExplain2());
        return loginRes;
    }

    @Override
    public Object logout() {
        Subject subject = SecurityUtils.getSubject();
        UserInfo userInfo = new UserInfo();
        TableAuthAccount user = (TableAuthAccount) subject.getPrincipals().getPrimaryPrincipal();
        TableAuthAccount authAccount = repositoryAccount.findByUsername(user.getUsername());
        userInfo.setDepartment(authAccount.getEmployee().getDepartment().getDepartmentName());
        userInfo.setEmployeeName(authAccount.getEmployee().getName());
        userInfo.setNumber(authAccount.getEmployee().getNumber());
        userInfo.setRoleList(authAccount.getRoleList().stream().map(TableAuthRoleBasic::getRoleProperty).collect(Collectors.toList()));
        LoginRes loginRes = new LoginRes();
        loginRes.setDescription(customProperty.successDescription);
        loginRes.setUserInfo(userInfo);
        subject.logout();
        return loginRes;
    }

    @Override
    public Object changePassword(ChangePasswordFormParam changePasswordFormParam) {
        String username = changePasswordFormParam.getUsername();
        String oldPwd = changePasswordFormParam.getOldPassword();
        String newPwd = changePasswordFormParam.getNewPassword();

        Subject subject = SecurityUtils.getSubject();
        TableAuthAccount user = (TableAuthAccount) subject.getPrincipals().getPrimaryPrincipal();
        if (user.getUsername().equals(username)) {
            if (user.getPassword().equals(ShiroPwdSecurity.securityTransform(oldPwd, username))) {
                viewAuth.alterPassword(username, ShiroPwdSecurity.securityTransform(newPwd, username));
            } else {
                throw new UnifiedException("原密码", ExceptionWrapperEnum.Auth_Match_Error);
            }
        } else {
            throw new UnifiedException("用户名", ExceptionWrapperEnum.Auth_Match_Error);
        }

        return 1;
    }
}

package com.handge.bigdata.auth;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.api.RepositoryAccount;
import com.handge.bigdata.dao.model.TableAuthAccount;
import com.handge.bigdata.dao.model.TableAuthPermission;
import com.handge.bigdata.dao.model.TableAuthRoleBasic;
import com.handge.bigdata.enumeration.ExceptionWrapperEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Liujuhao
 * @date 2018/5/31.
 */
@Component
public class ShiroRealm extends AuthorizingRealm {

    Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    RepositoryAccount repositoryAccount;

    // TODO: 2018/6/1 基于身份授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        logger.debug("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
        // 获取身份信息
        TableAuthAccount user = (TableAuthAccount) principals.getPrimaryPrincipal();
        TableAuthAccount authAccount = repositoryAccount.findByUsername(user.getUsername());
        // 将权限信息封闭为AuthorizationInfo
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        // 添加角色和权限
        for (TableAuthRoleBasic role : authAccount.getRoleList()) {
            simpleAuthorizationInfo.addRole(role.getId() + "");
            for (TableAuthPermission permission : role.getPermissionList()) {
                simpleAuthorizationInfo.addStringPermission(permission.getPattern());
            }
        }
        return simpleAuthorizationInfo;
    }

    // TODO: 2018/6/1 登录身份验证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        logger.debug("身份认证方法：ShiroRealm.doGetAuthenticationInfo()");

        String username = (String) authcToken.getPrincipal();

        // 通过 username 从数据库中查询
        TableAuthAccount user = repositoryAccount.findByUsername(username);

        if (user == null) {
            throw new UnifiedException(null, ExceptionWrapperEnum.Auth_NOT_Validate);
        }

        /**
         * 获取权限信息
         * 获取之后可以在前端for循环显示所有连接
         */
        // FIXME: 2018/6/5 获取权限信息

        //自动加密有BUG，暂时改用手动加密
//        return new SimpleAuthenticationInfo(user, user.getPassword(), ByteSource.Util.bytes(user.getUsername()), getName());
        return new SimpleAccount(user, user.getPassword(), getName());
    }

}

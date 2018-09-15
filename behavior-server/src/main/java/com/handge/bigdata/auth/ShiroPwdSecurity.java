package com.handge.bigdata.auth;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.enumeration.ExceptionWrapperEnum;
import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * @author Liujuhao
 * @date 2018/6/12.
 */
public class ShiroPwdSecurity {

    /**
     * 加密方式
     */
    static private String SECURITY_METHOD = "md5";

    /**
     * 迭代执行次数
     */
    static private int ITERATION_TIMES = 4;

    static public String securityTransform(String clearText, String salt) {

        if (clearText == null || salt == null) {
            throw new UnifiedException("用户名或密码", ExceptionWrapperEnum.NullPointerException);
        }

        Object result = new SimpleHash(SECURITY_METHOD, clearText, salt, ITERATION_TIMES);

        return result.toString();
    }
}

package com.handge.bigdata.auth;

import com.handge.bigdata.enumeration.ExceptionWrapperEnum;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Liujuhao
 * @date 2018/6/5.
 */
public class ShiroExcpetionHandler implements HandlerExceptionResolver {

    @Nullable
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @Nullable Object o, Exception e) {
        ModelAndView modelAndView = new ModelAndView();
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        Map<String, Object> attributes = new HashMap();
        if (e instanceof UnauthenticatedException) {
            attributes.put("code", ExceptionWrapperEnum.Auth_NOT_Validate.getCode());
            attributes.put("description", ExceptionWrapperEnum.Auth_NOT_Validate.getExplain2());
        } else if (e instanceof UnauthorizedException) {
            attributes.put("code", ExceptionWrapperEnum.Role_NOT_Power.getCode());
            attributes.put("description", ExceptionWrapperEnum.Role_NOT_Power.getExplain2());
        } else {
            attributes.put("code", ExceptionWrapperEnum.Auth_Unknow_Error.getCode());
            attributes.put("description", ExceptionWrapperEnum.Auth_Unknow_Error.getExplain2());
        }
        view.setAttributesMap(attributes);
        modelAndView.setView(view);
        return modelAndView;
    }
}

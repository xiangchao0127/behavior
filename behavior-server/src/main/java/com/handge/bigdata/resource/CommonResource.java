package com.handge.bigdata.resource;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.enumeration.ExceptionWrapperEnum;
import com.handge.bigdata.resource.models.request.common.ChangePasswordFormParam;
import com.handge.bigdata.resource.models.request.common.IpsByNumberParam;
import com.handge.bigdata.resource.models.request.common.LoginFormParam;
import com.handge.bigdata.resource.models.request.common.UserInfoByNameParam;
import com.handge.bigdata.resource.service.api.common.ICommon;
import com.handge.bigdata.resource.service.api.common.IIdentification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * Created by DaLu Guo on 2018/5/30.
 */
@SuppressWarnings("ALL")
@RestController
@RequestMapping(value = "/common", produces = {"application/json", "application/xml"}, consumes = {"application/json", "application/xml"})
public class CommonResource {

    @Autowired
    ICommon common;

    @Autowired
    IIdentification identification;

//    CommonService commonService = CommonService.getService();

    /**
     * 用户登录
     *
     * @param userInfo
     * @return
     */
    @PostMapping(value = "/login")
    public ResponseEntity login(@Valid @RequestBody LoginFormParam loginFormParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(identification.login(loginFormParam));
    }

    /**
     * 未登录跳转响应
     *
     * @return
     */
    @RequestMapping(value = "/unauth")
    public ResponseEntity unauth() {
        return ResponseEntity.ok().body(identification.unauth());
    }

    /**
     * 用户登出
     *
     * @return
     */
    @PostMapping(value = "/logout")
    public ResponseEntity logout() {
        return ResponseEntity.ok().body(identification.logout());
    }

    /**
     * 修改密码
     *
     * @return
     */
    @PostMapping(value = "/change_password")
    public ResponseEntity changePassword(@Valid @RequestBody ChangePasswordFormParam changePasswordFormParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(identification.changePassword(changePasswordFormParam));
    }

    /**
     * 根据姓名匹配员工信息
     *
     * @param name 姓名
     * @return
     */
    @GetMapping("/user_info_by_name")
    public ResponseEntity listUserInfoByName(@Valid UserInfoByNameParam userInfoByNameParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(common.listUserInfoByName(userInfoByNameParam));
    }

    /**
     * 根据number获取IPS
     *
     * @param number
     * @return
     */
    @GetMapping("/ips_by_number")
    public ResponseEntity getIpsByNumber(@Valid IpsByNumberParam ipsByNumberParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(common.getIpsByNumber(ipsByNumberParam));
    }

}

/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.resource;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.enumeration.ExceptionWrapperEnum;
import com.handge.bigdata.resource.models.request.monitor.*;
import com.handge.bigdata.resource.service.api.monitor.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

/**
 * @author liuqian
 * @date 2018/4/25
 */
@SuppressWarnings("ALL")
@RestController
@RequestMapping(value = "/monitor", produces = {"application/json", "application/xml"}, consumes = {"application/json", "application/xml"})
public class MonitorResource {
//    MonitorService monitorService = MonitorService.getService();

    @Autowired
    MessageSource messageSource;
    @Autowired
    IAbnormalWathcher abnormalWathcher;
    @Autowired
    IFlowTendency flowTendency;
    @Autowired
    IFlowTrend flowTrend;
    @Autowired
    INetStatus netStatus;
    @Autowired
    ITopOfIpFlow topOfIpFlow;
    @Autowired
    ITopOfNonWorkingApp topOfNonWorkingApp;
    @Autowired
    ITopOfNonWorkingTime topOfNonWorkingTime;
    @Autowired
    ITopOfProtocolFlow topOfProtocolFlow;
    @Autowired
    ITopOfNonWorkingDepartment topOfNonWorkingDepartment;
    @Autowired
    IIllegalInfo iIllegalInfo;
    @Autowired
    ITopOfNonWorkingAppTime topOfNonWorkingAppTime;


    /**
     * 协议流量排名
     *
     * @param context 用户信息
     * @param n       排名数量（1,2,3,4,5...）
     * @return
     */
    @RequiresPermissions(value = "monitor:get:6")
    @GetMapping("/top_of_protocol_flow")
    public ResponseEntity topOfProtocolFlow(@Valid TopOfProtocolFlowParam topOfProtocolFlowParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuffer stringBuffer = new StringBuffer();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            Locale currentLocale = LocaleContextHolder.getLocale();
            for (FieldError fieldError : fieldErrors) {
                //收集错误信息
                String msg = messageSource.getMessage(fieldError, currentLocale);
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(topOfProtocolFlow.listTopOfProtocolFlow(topOfProtocolFlowParam));
    }

    /**
     * 协议流量详情
     *
     * @param context  用户信息
     * @param pageNo   页码
     * @param pageSize 每页记录数
     * @param protocol 协议名
     * @return
     */
    @RequiresPermissions(value = "monitor:get:7")
    @GetMapping("/top_of_protocol_flow/detail")
    public ResponseEntity protocolFlowDetail(@Valid ProtocolFlowDetailParam protocolFlowDetailParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getField(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(topOfProtocolFlow.listProtocolFlowDetail(protocolFlowDetailParam));
    }


    /**
     * IP流量排名
     *
     * @param n       排名数量（1,2,3,4,5...）
     * @param context 用户信息
     * @return
     */
    @RequiresPermissions(value = "monitor:get:8")
    @GetMapping("/top_of_ip_flow")
    public ResponseEntity topOfIpFlow(@Valid TopOfIpFlowParam topOfIpFlowParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(topOfIpFlow.listTopOfIpFlow(topOfIpFlowParam));
    }

    /**
     * IP流量详情
     *
     * @param userContext 用户信息
     * @param pageNo      页码
     * @param pageSize    每页记录数
     * @param ip          IP地址
     * @return
     */
    @RequiresPermissions(value = "monitor:get:9")
    @GetMapping("/top_of_ip_flow/detail")
    public ResponseEntity ipFlowDetail(@Valid IpFlowDetailParam ipFlowDetailParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getField(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(topOfIpFlow.listIpFlowDetail(ipFlowDetailParam));
    }

    /**
     * 流量趋势
     *
     * @param userContext 用户信息
     * @param model       两种模式：1=24小时模式 2=实时模式
     * @param size        应用数量,默认为3
     * @return
     */
    @RequiresPermissions(value = "monitor:get:10")
    @GetMapping("/flow_tendency")
    public ResponseEntity flowTendency(@Valid FlowTendencyParam flowTendencyParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(flowTendency.listFlowTendency(flowTendencyParam));
    }

    /**
     * 当前上网人数占比
     *
     * @param userContext 用户信息
     * @return
     */
    @RequiresPermissions(value = "monitor:get:11")
    @GetMapping("/net_status")
    public ResponseEntity netStatus(@Valid NetStatusParam netStatusParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getField(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(netStatus.listNetStatus(netStatusParam));
    }

    /**
     * 工作无关应用TOP
     *
     * @param userContext 用户信息
     * @param n           排名数量（1,2,3,4,5...）
     * @return
     */
    @RequiresPermissions(value = "monitor:get:12")
    @GetMapping("/top_of_nonworking_app")
    public ResponseEntity topOfNonWorkingApp(@Valid TopOfNonWorkingAppParam topOfNonWorkingAppParam, BindingResult bindingResult) {
        System.out.println("listTopOfNonWorkingApp...");
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(topOfNonWorkingApp.listTopOfNonWorkingApp(topOfNonWorkingAppParam));
    }

    /**
     * 工作无关应用详情
     *
     * @param userContext 用户信息
     * @param appName     应用名称
     * @param pageNo      页码
     * @param pageSize    每页记录数
     * @return
     */
    @RequiresPermissions(value = "monitor:get:13")
    @GetMapping("/top_of_nonworking_app/detail")
    public ResponseEntity nonWorkingAppDetail(@Valid NonWorkingAppDetailParam nonWorkingAppDetailParam, BindingResult bindingResult) {
        System.out.println("listNonWorkingAppDetail...");
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(topOfNonWorkingApp.listNonWorkingAppDetail(nonWorkingAppDetailParam));
    }

    /**
     * 工作无关上网时长
     *
     * @param n           排名数量（1,2,3,4,5...），如果不传则默认为拿top 5
     * @param userContext 用户信息
     * @param departmentName 部门名称
     * @return
     */
    @RequiresPermissions(value = "monitor:get:14")
    @GetMapping("/top_of_nonworking_time")
    public ResponseEntity topOfNonWorkingTime(@Valid TopOfNonWorkingTimeParam topOfNonWorkingTimeParam, BindingResult bindingResult) {
        System.out.println("listTopOfNonWorkingTime...");
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(topOfNonWorkingTime.listTopOfNonWorkingTime(topOfNonWorkingTimeParam));
    }

    /**
     * 工作无关上网时长详情
     *
     * @param context    用户信息
     * @param pageNo     页码
     * @param pageSize   每页记录数
     * @param number     工号
     * @param name       姓名
     * @param department 部门
     * @return
     */
    @RequiresPermissions(value = "monitor:get:15")
    @GetMapping("/top_of_nonworking_time/detail")
    public ResponseEntity nonWorkingTimeDetail(@Valid NonWorkingTimeDetailParam nonWorkingTimeDetailParam, BindingResult bindingResult) {
        System.out.println("listNonWorkingTimeDetail...");
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(topOfNonWorkingTime.listNonWorkingTimeDetail(nonWorkingTimeDetailParam));
    }

    /**
     * 工作无关上网人数占比
     *
     * @param n           排名数量（1,2,3,4,5...）
     * @param userContext 用户信息
     * @return
     */
    @RequiresPermissions(value = "monitor:get:16")
    @GetMapping("/top_of_nonworking_department")
    public ResponseEntity topOfNonWorkingDepartment(@Valid TopOfNonWorkingDepartmentParam topOfNonWorkingDepartmentParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(topOfNonWorkingDepartment.listTopOfNonWorkingDepartment(topOfNonWorkingDepartmentParam));
    }

    /**
     * 工作无关上网人数占比详情
     *
     * @param userContext 用户信息
     * @param pageNo      页码
     * @param pageSize    每页记录数
     * @param department  所在部门
     * @return
     */
    @RequiresPermissions(value = "monitor:get:17")
    @GetMapping("/top_of_nonworking_department/detail")
    public ResponseEntity nonWorkingDepartmentDetail(@Valid NonWorkingDepartmentDetailParam nonWorkingDepartmentDetailParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getField(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(topOfNonWorkingDepartment.listNonWorkingDepartmentDetail(nonWorkingDepartmentDetailParam));
    }


    /**
     * 异常报警
     *
     * @param n           排名数量（1,2,3,4,5...）
     * @param userContext 用户信息
     * @return
     */
    @RequiresPermissions(value = "monitor:get:18")
    @GetMapping("/alarm_info")
    public ResponseEntity alarmInfo(@Valid AlarmInfoParam alarmInfoParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(abnormalWathcher.listSingleAlarmInfo(alarmInfoParam));
    }

    /**
     * 异常报警详情
     *
     * @param userContext 用户信息
     * @return
     */
    @RequiresPermissions(value = "monitor:get:19")
    @GetMapping("/alarm_info/detail")
    public ResponseEntity alarmInfoDetail(@Valid AlarmInfoDetailParam alarmInfoDetailParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(abnormalWathcher.listSingleAlarmInfoDetail(alarmInfoDetailParam));
    }

    /**
     * 违规信息
     *
     * @param n           排名数量（1,2,3,4,5...）
     * @param userContext 用户信息
     * @return
     */
    @RequiresPermissions(value = "monitor:get:20")
    @GetMapping("/illegal_info")
    public ResponseEntity illegalInfo(@Valid IllegalInfoParam illegalInfoParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(iIllegalInfo.listIllegalInfo(illegalInfoParam));
    }

    /**
     * 违规信息详情
     *
     * @param userContext 用户信息
     * @param number      工号
     * @param name        姓名
     * @param department  部门
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @param pageNo      页码
     * @param pageSize    一页的数量
     * @return
     */
    @RequiresPermissions(value = "monitor:get:21")
    @GetMapping("/illegal_info/detail")
    public ResponseEntity illegelInfoDetail(@Valid IllegalDetailParam illegalDetailParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(iIllegalInfo.listIllegalInfoDetail(illegalDetailParam));
    }

    /**
     * 流量走势
     *
     * @param userContext 用户信息
     * @return
     */
    @RequiresPermissions(value = "monitor:get:22")
    @GetMapping("/flow_trend")
    public ResponseEntity flowTrend(@Valid FlowTrendParam flowTrendParam, BindingResult bindingResult) {
        System.out.println("flowTrend...");
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getField(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(flowTrend.flowTrend(flowTrendParam));
    }

    /**
     * 工作无关上网应用时长
     *
     * @param n           排名数量（1,2,3,4,5...），如果不传则默认为拿top 5
     * @param userContext 用户信息
     * @param departmentName 部门名称
     * @return
     */
    @RequiresPermissions(value = "monitor:get:23")
    @GetMapping("/top_of_nonworking_app_time")
    public ResponseEntity topOfNonWorkingAppTime(@Valid TopOfNonWorkingAppTimeParam topOfNonWorkingAppTimeParam, BindingResult bindingResult) {
        System.out.println("listTopOfNonWorkingAppTime...");
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(topOfNonWorkingAppTime.listTopOfNonWorkingAppTime(topOfNonWorkingAppTimeParam));
    }
    /**
     * 工作无关上网应用时长详情
     *
     * @return
     */
    @RequiresPermissions(value = "monitor:get:24")
    @GetMapping("/nonworking_app_time_detail")
    public ResponseEntity nonWorkingAppTimeDetail(@Valid NonWorkingAppTimeDetailParam nonWorkingAppTimeDetailParam, BindingResult bindingResult) {
        System.out.println("listNonWorkingAppTimeDetail...");
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(topOfNonWorkingAppTime.listNonWorkingAppTimeDetail(nonWorkingAppTimeDetailParam));
    }
}

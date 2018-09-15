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
import com.handge.bigdata.enumeration.TimeSectionEnum;
import com.handge.bigdata.resource.models.request.statistics.*;
import com.handge.bigdata.resource.service.api.statistics.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Guo Dalu
 * @String 2018/4/25
 */
@SuppressWarnings("ALL")
@RestController
@RequestMapping(value = "/statistics", produces = {"application/json", "application/xml"}, consumes = {"application/json", "application/xml"})
public class StatisticsResource {

//    StatisticsService statisticsService = StatisticsService.getService();

    @Autowired
    INonWorkingTimeSolt nonWorkingTimeSolt;
    @Autowired
    IInternetFootprint internetFootprint;
    @Autowired
    INonWorkingTrend nonWorkingTrend;
    @Autowired
    ITopOfAppRatio topOfAppRatio;
    @Autowired
    ITopOfTimeByStaff topOfTimeByStaff;
    @Autowired
    ITopOfTimeByDepartment topOfTimeByDepartment;

    Log logger = LogFactory.getLog(this.getClass());

    /**
     * 工作无关上网时段分布
     *
     * @param startTime 开始时间 2018-01-01 00:00:00
     * @param context   用户信息
     * @param endTime   结束时间 2018-01-01 00:00:00
     * @return
     */
    @RequiresPermissions(value = "statistics:get:23")
    @GetMapping("/nonworking_time_solt")
    public ResponseEntity notWorkingTimeSolt(@Valid NonWorkingTimeSoltParam nonWorkingTimeSoltParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getField(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(nonWorkingTimeSolt.listNonWorkingTimeSolt(nonWorkingTimeSoltParam));
    }

    /**
     * 工作无关部门上网人均时长TOP
     *
     * @param startTime 统计开始时间 形如：2018-01-01 00:00:00
     * @param endTime   统计结束时间 形如：2018-01-01 23:59:59
     * @param n         排名数量（1,2,3,4,5...）
     * @param context   用户信息
     * @return
     */
    @RequiresPermissions(value = "statistics:get:24")
    @GetMapping("/top_of_time_by_department")
    public ResponseEntity timeByDeptTop(@Valid TopOfTimeByDepartmentParam topOfTimeByDepartmentParam, BindingResult bindingResult) {
        logger.debug("timeByDeptTop...");
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(topOfTimeByDepartment.listTopOfTimeByDepartment(topOfTimeByDepartmentParam));
    }

    /**
     * 工作无关部门上网人均时长详情
     *
     * @param context   用户信息
     * @param startTime 统计开始时间 形如：2018-01-01 00:00:00
     * @param endTime   统计结束时间 形如：2018-01-01 23:59:59
     * @param pageNo    页码
     * @param pageSize  每页记录数
     * @return
     */
    @RequiresPermissions(value = "statistics:get:25")
    @GetMapping("/top_of_time_by_department/detail")
    public ResponseEntity timeByDeptTop(@Valid TimeByDepartmentDetailParam timeByDepartmentDetailParam, BindingResult bindingResult) {
        logger.debug("listTimeByDepartmentDetail...");
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(topOfTimeByDepartment.listTimeByDepartmentDetail(timeByDepartmentDetailParam));
    }

    /**
     * 工作无关公司上网人均时长TOP
     *
     * @param startTime 统计开始时间 形如：2018-01-01 00:00:00
     * @param endTime   统计结束时间 形如：2018-01-01 23:59:59
     * @param n         排名数量（1,2,3,4,5...）
     * @param context   用户信息
     * @return
     */
    @RequiresPermissions(value = "statistics:get:27")
    @GetMapping("/top_of_time_by_staff")
    public ResponseEntity timeByStaffTop(@Valid TopOfTimeByStaffParam topOfTimeByStaffParam, BindingResult bindingResult) {
        logger.debug("listTopOfTimeByStaff...");
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(topOfTimeByStaff.listTopOfTimeByStaff(topOfTimeByStaffParam));
    }

    /**
     * 工作无关公司上网人均时长详情
     *
     * @param startTime 统计开始时间 形如：2018-01-01 00:00:00
     * @param endTime   统计结束时间 形如：2018-01-01 23:59:59
     * @param context   用户信息
     * @param pageNo    页码
     * @param pageSize  每页记录条数
     * @return
     */
    @RequiresPermissions("statistics:get:25")
    @GetMapping("/top_of_time_by_staff/detail")
    public ResponseEntity timeByStaffTop(@Valid TimeByStaffDetailParam timeByStaffDetailParam, BindingResult bindingResult) {
        logger.debug("listTopOfTimeByStaff...");
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(topOfTimeByStaff.listTimeByStaffDetail(timeByStaffDetailParam));
    }

    /**
     * 工作无关上网趋势
     *
     * @param date      时间，形如“2018-05-16 10:02:50”
     * @param context   用户信息
     * @param threshold 阈值，每天上网时间（单位分钟）超过该阈值的才记入
     * @return
     */
    @RequiresPermissions(value = "statistics:get:28")
    @GetMapping("/nonworking_trend")
    public ResponseEntity nonWorkingTrend(@Valid NonWorkingTrendParam nonWorkingTrendParam, BindingResult bindingResult) {
        logger.debug("listNonWorkingTrend...");
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getField(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(nonWorkingTrend.listNonWorkingTrend(nonWorkingTrendParam));
    }

    /**
     * 上网足迹查询
     *
     * @param startTime 统计开始时间  形如 “2018-05-16 10:02:50 ”
     * @param endTime   统计结束时间   形如 “2018-05-17 10:20:10 ”
     * @param context   用户信息
     * @param name      姓名|部门|工号
     * @param ip        具体IP地址或全部
     * @param app       应用
     * @param type      类型
     * @param property  性质   0：工作无关   1：工作相关    2：不确定
     * @param pageSize  每一页显示条数
     * @param pageNo    页数
     * @return
     */
    @RequiresPermissions(value = "statistics:get:31")
    @GetMapping("/internet_footprint")
    public ResponseEntity internetFootprint(@Valid FootPrintParam footPrintParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(internetFootprint.listInternetFootprint(footPrintParam));
    }
}

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
import com.handge.bigdata.resource.models.request.professional.*;
import com.handge.bigdata.resource.service.api.professional.*;
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
 * @date 2018/4/25
 */
@SuppressWarnings("ALL")
@RestController
@RequestMapping(value = "/smart", produces = {"application/json", "application/xml"}, consumes = {"application/json", "application/xml"})
public class ProfessionalResource {

//    ProfessionalService service = ProfessionalService.getService();
    @Autowired
    IEmployeeDetails employeeDetails;
    @Autowired
    IGoodOrBad goodOrBad;
    @Autowired
    IProfessionalAccomplishmentByDepartment professionalAccomplishmentByDepartment;
    @Autowired
    IProfessionalAccomplishmentByDepartmentManager professionalAccomplishmentByDepartmentManager;
    @Autowired
    IProfessionalAccomplishment professionalAccomplishment;
    @Autowired
    IProfessionalTrend professionalTrend;
    @Autowired
    IRanking ranking;
    @Autowired
    IScoreDistribution scoreDistribution;

    /**
     * 职业素养
     *
     * @return
     */
    @RequiresPermissions(value = "monitor:get:7")
    @GetMapping("/professional_accomplishment")
    public ResponseEntity professionalAccomplishment(@Valid ProfessionalAccomplishmentParam professionalAccomplishmentParam, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(professionalAccomplishment.getProfessionalAccomplishment(professionalAccomplishmentParam));
    }

    /**
     * 员工详情
     * @return
     */
    @RequiresPermissions(value = "monitor:get:7")
    @GetMapping("/employee_details")
    public ResponseEntity employeeDetails(@Valid EmployeeDetailsParam employeeDetailsParam, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(employeeDetails.getEmployeeDetails(employeeDetailsParam));
    }

    /**
     * 职业素养趋势
     *
     * @return
     */
    @RequiresPermissions(value = "monitor:get:7")
    @GetMapping("/professional_trend")
    public ResponseEntity professionalTrend(@Valid ProfessionalTrendParam professionalTrendParam, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(professionalTrend.listProfessionalTrend(professionalTrendParam));
    }

    /**
     * 职业素养员工排名
     *
     * @return
     */
    @GetMapping("/professional_ranking")
    public ResponseEntity professionalRanking(@Valid RankingParam rankingParam, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(ranking.getRanking(rankingParam));
    }

    /**
     * 职业素养员工所在位置
     *
     * @return
     */
    @GetMapping("/professional_score_distribution")
    public ResponseEntity listScoreDistribution(@Valid ScoreDistributionParam scoreDistributionParam, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(scoreDistribution.listScoreDistribution(scoreDistributionParam));
    }



    /**
     * 优秀员工 TOP5
     *
     * Created by MaJianfu on 2018/6/13
     * @return
     */
    @GetMapping("/top_of_best_staff")
    public ResponseEntity topOfBestStaff(@Valid TopOfStaffParam topOfStaffParam, BindingResult bindingResult) {
        topOfStaffParam.setModel("1");
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(goodOrBad.listTopOfStaff(topOfStaffParam));
    }
    /**
     * 优秀员工排名
     *
     * Created by MaJianfu on 2018/6/13
     * @return
     */
    @GetMapping("/best_staff_detail")
    public ResponseEntity BestStaffDetail(@Valid StaffDetailParam staffDetailParam, BindingResult bindingResult) {
        staffDetailParam.setModel("1");
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(goodOrBad.listStaffDetail(staffDetailParam));
    }
    /**
     * 差劲员工 TOP5
     *
     * Created by MaJianfu on 2018/6/13
     * @return
     */
    @GetMapping("/top_of_poor_staff")
    public ResponseEntity TopOfPoorStaff(@Valid TopOfStaffParam topOfStaffParam, BindingResult bindingResult) {
        topOfStaffParam.setModel("2");
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(goodOrBad.listTopOfStaff(topOfStaffParam));
    }
    /**
     * 差劲员工排名
     *
     * Created by MaJianfu on 2018/6/13
     * @return
     */
    @GetMapping("/poor_staff_detail")
    public ResponseEntity PoorStaffDetail(@Valid StaffDetailParam staffDetailParam, BindingResult bindingResult) {
        staffDetailParam.setModel("2");
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(goodOrBad.listStaffDetail(staffDetailParam));
    }
    /**
     * 部门职业素养 TOP5
     *
     * Created by MaJianfu on 2018/6/13
     * @return
     */
    @GetMapping("/top_of_professional_accomplishment_by_department")
    public ResponseEntity TopOfProfessionalAccomplishmentByDepartment(@Valid TopOfProfessionalAccomplishmentByDepartmentParam topOfProfessionalAccomplishmentByDepartmentParam, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(professionalAccomplishmentByDepartment.listTopOfProfessionalAccomplishmentByDepartment(topOfProfessionalAccomplishmentByDepartmentParam));
    }
    /**
     * 部门职业素养详情
     *
     * Created by MaJianfu on 2018/6/13
     * @return
     */
    @GetMapping("/professional_accomplishment_by_department_detail")
    public ResponseEntity ProfessionalAccomplishmentByDepartmentDetail(@Valid ProfessionalAccomplishmentByDepartmentDetailParam professionalAccomplishmentByDepartmentDetailParam, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getField(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(professionalAccomplishmentByDepartment.listProfessionalAccomplishmentByDepartmentDetail(professionalAccomplishmentByDepartmentDetailParam));
    }
    /**
     * 部门经理职业素养 TOP5
     *
     * Created by MaJianfu on 2018/6/13
     * @return
     */
    @GetMapping("/top_of_professional_accomplishment_by_department_manager")
    public ResponseEntity TopOfProfessionalAccomplishmentByDepartmentManager(@Valid TopOfProfessionalAccomplishmentByDepartmentManagerParam topOfProfessionalAccomplishmentByDepartmentManagerParam, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getDefaultMessage(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(professionalAccomplishmentByDepartmentManager.listTopOfProfessionalAccomplishmentByDepartmentManager(topOfProfessionalAccomplishmentByDepartmentManagerParam));
    }
    /**
     * 部门经理职业素养详情
     *
     * Created by MaJianfu on 2018/6/13
     * @return
     */
    @GetMapping("/professional_accomplishment_by_department_manager_detail")
    public ResponseEntity ProfessionalAccomplishmentByDepartmentManagerDetail(@Valid ProfessionalAccomplishmentByDepartmentManagerDetailParam professionalAccomplishmentByDepartmentManagerDetailParam, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                throw new UnifiedException(fieldError.getField(), ExceptionWrapperEnum.IllegalArgumentException);
            }
        }
        return ResponseEntity.ok().body(professionalAccomplishmentByDepartmentManager.listProfessionalAccomplishmentByDepartmentManagerDetail(professionalAccomplishmentByDepartmentManagerDetailParam));
    }



}

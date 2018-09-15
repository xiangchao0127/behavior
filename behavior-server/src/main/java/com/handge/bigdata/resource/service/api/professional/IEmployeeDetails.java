package com.handge.bigdata.resource.service.api.professional;

import com.handge.bigdata.resource.models.request.professional.EmployeeDetailsParam;

/**
 * Created by DaLu Guo on 2018/6/12.
 */


public interface IEmployeeDetails {
    /**
     * 员工详情
     */
    Object getEmployeeDetails(EmployeeDetailsParam employeeDetailsParam);
}

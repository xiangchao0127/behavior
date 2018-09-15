package com.handge.bigdata.resource.service.api.monitor;

import com.handge.bigdata.resource.models.request.monitor.NonWorkingDepartmentDetailParam;
import com.handge.bigdata.resource.models.request.monitor.TopOfNonWorkingDepartmentParam;

/**
 * Created by DaLu Guo on 2018/5/4.
 */
public interface ITopOfNonWorkingDepartment {
    /**
     * 工作无关上网人数占比
     *
     * @return
     */
    public Object listTopOfNonWorkingDepartment(TopOfNonWorkingDepartmentParam topOfNonWorkingDepartmentParam);

    /**
     * 工作无关上网人数占比详情
     *
     * @return
     */
    public Object listNonWorkingDepartmentDetail(NonWorkingDepartmentDetailParam nonWorkingDepartmentDetailParam);

}

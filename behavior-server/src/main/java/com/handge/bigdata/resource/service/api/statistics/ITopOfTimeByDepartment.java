package com.handge.bigdata.resource.service.api.statistics;

import com.handge.bigdata.resource.models.request.statistics.TimeByDepartmentDetailParam;
import com.handge.bigdata.resource.models.request.statistics.TopOfTimeByDepartmentParam;

/**
 * Created by DaLu Guo on 2018/5/4.
 */
public interface ITopOfTimeByDepartment {
    /**
     * 工作无关部门上网人均时长TOP
     *
     * @return
     */
    public Object listTopOfTimeByDepartment(TopOfTimeByDepartmentParam topOfTimeByDepartmentParam);

    /**
     * 工作无关部门上网人均时长详情
     *
     * @return
     */
    public Object listTimeByDepartmentDetail(TimeByDepartmentDetailParam timeByDepartmentDetailParam);
}

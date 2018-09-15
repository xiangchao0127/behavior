package com.handge.bigdata.resource.service.api.monitor;

import com.handge.bigdata.resource.models.request.monitor.NonWorkingAppTimeDetailParam;
import com.handge.bigdata.resource.models.request.monitor.TopOfNonWorkingAppTimeParam;

/**
 * @author liuqian
 * @date 2018/7/13
 * @Description:
 */
public interface ITopOfNonWorkingAppTime {
    /**
     * 工作无关应用时长TOP
     * @param topOfNonWorkingAppTimeParam
     * @return
     */
    public Object listTopOfNonWorkingAppTime(TopOfNonWorkingAppTimeParam topOfNonWorkingAppTimeParam);

    /**
     * 工作无关应用时长详情
     * @param nonWorkingAppTimeDetailParam
     * @return
     */
    public Object listNonWorkingAppTimeDetail(NonWorkingAppTimeDetailParam nonWorkingAppTimeDetailParam);
}

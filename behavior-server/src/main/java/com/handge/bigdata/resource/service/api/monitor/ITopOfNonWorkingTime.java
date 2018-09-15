package com.handge.bigdata.resource.service.api.monitor;

import com.handge.bigdata.resource.models.request.monitor.NonWorkingTimeDetailParam;
import com.handge.bigdata.resource.models.request.monitor.TopOfNonWorkingTimeParam;

/**
 * Created by DaLu Guo on 2018/5/4.
 */
public interface ITopOfNonWorkingTime {
    /**
     * 工作无关上网时长
     *
     * @return
     */
    public Object listTopOfNonWorkingTime(TopOfNonWorkingTimeParam topOfNonWorkingTimeParam);

    /**
     * 工作无关上网时长详情
     *
     * @return
     */
    public Object listNonWorkingTimeDetail(NonWorkingTimeDetailParam nonWorkingTimeDetailParam);
}

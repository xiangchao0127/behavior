package com.handge.bigdata.resource.service.api.statistics;

import com.handge.bigdata.resource.models.request.statistics.NonWorkingTimeSoltParam;

/**
 * Created by DaLu Guo on 2018/5/4.
 */


public interface INonWorkingTimeSolt {


    /**
     * 工作无关上网时段分布
     *
     * @return
     */
    public Object listNonWorkingTimeSolt(NonWorkingTimeSoltParam nonWorkingTimeSoltParam);
}

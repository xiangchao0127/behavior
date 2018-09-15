package com.handge.bigdata.resource.service.api.monitor;

import com.handge.bigdata.resource.models.request.monitor.FlowTendencyParam;

/**
 * Created by DaLu Guo on 2018/5/4.
 */
public interface IFlowTendency {
    /**
     * 流量趋势
     *
     * @return
     */
    public Object listFlowTendency(FlowTendencyParam flowTendencyParam);
}

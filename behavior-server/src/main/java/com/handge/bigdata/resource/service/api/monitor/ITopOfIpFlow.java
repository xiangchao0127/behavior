package com.handge.bigdata.resource.service.api.monitor;

import com.handge.bigdata.resource.models.request.monitor.IpFlowDetailParam;
import com.handge.bigdata.resource.models.request.monitor.TopOfIpFlowParam;

/**
 * Created by DaLu Guo on 2018/5/4.
 */
public interface ITopOfIpFlow {

    /**
     * IP流量
     *
     * @return
     */
    public Object listTopOfIpFlow(TopOfIpFlowParam topOfIpFlowParam);

    /**
     * IP流量详情
     *
     * @return
     */
    public Object listIpFlowDetail(IpFlowDetailParam ipFlowDetailParam);
}

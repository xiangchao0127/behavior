package com.handge.bigdata.resource.service.api.monitor;

import com.handge.bigdata.resource.models.request.monitor.IllegalDetailParam;
import com.handge.bigdata.resource.models.request.monitor.IllegalInfoParam;

/**
 * Created by DaLu Guo on 2018/5/16.
 */
public interface IIllegalInfo {
    /**
     * 违规信息
     *
     * @return
     */
    public Object listIllegalInfo(IllegalInfoParam illegalInfoParam);

    /**
     * 违规信息详情
     *
     * @return
     */
    public Object listIllegalInfoDetail(IllegalDetailParam illegalDetailParam);
}

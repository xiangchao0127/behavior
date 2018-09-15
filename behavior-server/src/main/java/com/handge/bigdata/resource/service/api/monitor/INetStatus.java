package com.handge.bigdata.resource.service.api.monitor;

import com.handge.bigdata.resource.models.request.monitor.NetStatusParam;

/**
 * Created by DaLu Guo on 2018/5/4.
 */
public interface INetStatus {

    /**
     * 当前上网人数占比(圆饼图)
     *
     * @param
     * @return
     */
    public Object listNetStatus(NetStatusParam netStatusParam);

    /**
     * 当前上网人数占比(折线图)
     *
     * @param
     * @return
     */
    public Object listNetStatusNew(NetStatusParam netStatusParam);


}

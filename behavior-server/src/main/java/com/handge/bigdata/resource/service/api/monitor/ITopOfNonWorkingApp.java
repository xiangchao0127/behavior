package com.handge.bigdata.resource.service.api.monitor;

import com.handge.bigdata.resource.models.request.monitor.NonWorkingAppDetailParam;
import com.handge.bigdata.resource.models.request.monitor.TopOfNonWorkingAppParam;

/**
 * Created by DaLu Guo on 2018/5/4.
 */
public interface ITopOfNonWorkingApp {
    /**
     * 工作无关应用TOP
     *
     * @return
     */
    public Object listTopOfNonWorkingApp(TopOfNonWorkingAppParam topOfNonWorkingAppParam);

    /**
     * 工作无关应用详情
     *
     * @return
     */
    public Object listNonWorkingAppDetail(NonWorkingAppDetailParam nonWorkingAppDetailParam);


}

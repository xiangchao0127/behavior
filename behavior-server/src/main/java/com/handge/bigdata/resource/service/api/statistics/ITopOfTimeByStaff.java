package com.handge.bigdata.resource.service.api.statistics;

import com.handge.bigdata.resource.models.request.statistics.TimeByStaffDetailParam;
import com.handge.bigdata.resource.models.request.statistics.TopOfTimeByStaffParam;

/**
 * Created by DaLu Guo on 2018/5/4.
 */
public interface ITopOfTimeByStaff {
    /**
     * 工作无关公司上网人均时长TOP
     *
     * @return
     */
    public Object listTopOfTimeByStaff(TopOfTimeByStaffParam topOfTimeByStaffParam);

    /**
     * 工作无关公司上网人均时长详情
     *
     * @return
     */
    public Object listTimeByStaffDetail(TimeByStaffDetailParam timeByStaffDetailParam);
}

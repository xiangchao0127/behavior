package com.handge.bigdata.resource.service.api.professional;

import com.handge.bigdata.resource.models.request.professional.StaffDetailParam;
import com.handge.bigdata.resource.models.request.professional.TopOfStaffParam;

import java.util.HashMap;

/**
 * Created by DaLu Guo on 2018/6/12.
 */
public interface IGoodOrBad {
    /**
     * 优秀员工 TOP5 或者 差劲员工 TOP5
     *
     * @param
     * @return
     * @author MaJianfu
     * @date 2018/6/12 16:01
     **/
    public Object listTopOfStaff(TopOfStaffParam topOfStaffParam);

    /**
     * 优秀员工排名 或者 差劲员工排名
     *
     * @param
     * @return
     * @author MaJianfu
     * @date 2018/6/12 16:01
     **/
    public Object listStaffDetail(StaffDetailParam staffDetailParam);

    /**
     * 个人分数
     *
     * @param
     * @return
     * @author MaJianfu
     * @date 2018/6/12 16:01
     **/
    public HashMap<String, String> scoreStaff(String date, String department_name);


}

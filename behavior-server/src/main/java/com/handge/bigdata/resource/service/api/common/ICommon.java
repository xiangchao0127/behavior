package com.handge.bigdata.resource.service.api.common;

import com.handge.bigdata.resource.models.request.common.ChangePasswordFormParam;
import com.handge.bigdata.resource.models.request.common.IpsByNumberParam;
import com.handge.bigdata.resource.models.request.common.LoginFormParam;
import com.handge.bigdata.resource.models.request.common.UserInfoByNameParam;

/**
 * 用户开发公共的、与前端交互的接口
 * Created by DaLu Guo on 2018/5/29.
 */
public interface ICommon {

    /**
     * 通过给定的数据模糊查询员工
     *
     * @return
     */
    public Object listUserInfoByName(UserInfoByNameParam userInfoByNameParam);

    /**
     * 根据工号查询员工Ips
     */
    public Object getIpsByNumber(IpsByNumberParam ipsByNumberParam);
}

package com.handge.bigdata.schedule;

import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.resource.models.request.monitor.NetStatusParam;
import com.handge.bigdata.resource.service.api.monitor.INetStatus;
import com.handge.bigdata.utils.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.Date;

@Component
public class NetStatusJob {
    private static Logger logger = LogManager.getLogger(NetStatusJob.class);
    @Autowired
    INetStatus iNetStatus;
    @Scheduled(fixedRate = 60000)
    public void reportCurrentTime() {
        logger.info("当前上网情况定时任务执行时间：" + DateUtil.date2Str(new Date(), DateFormatEnum.MINUTES));
        iNetStatus.listNetStatus(new NetStatusParam());
    }
}

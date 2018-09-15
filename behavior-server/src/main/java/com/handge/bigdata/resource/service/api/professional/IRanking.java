package com.handge.bigdata.resource.service.api.professional;

import com.handge.bigdata.resource.models.request.professional.RankingParam;

/**
 * Created by DaLu Guo on 2018/6/12.
 */
public interface IRanking {
    /**
     * 排名
     */
    Object getRanking(RankingParam rankingParam);
}

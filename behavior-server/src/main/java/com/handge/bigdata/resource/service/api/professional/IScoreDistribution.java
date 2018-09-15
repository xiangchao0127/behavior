package com.handge.bigdata.resource.service.api.professional;

import com.handge.bigdata.resource.models.request.professional.ScoreDistributionParam;

/**
 * Created by DaLu Guo on 2018/6/12.
 */
public interface IScoreDistribution {
    Object listScoreDistribution(ScoreDistributionParam scoreDistributionParam);
}

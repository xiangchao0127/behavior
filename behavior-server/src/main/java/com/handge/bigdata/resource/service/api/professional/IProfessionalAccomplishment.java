package com.handge.bigdata.resource.service.api.professional;

import com.handge.bigdata.resource.models.request.professional.ProfessionalAccomplishmentParam;

/**
 * Created by DaLu Guo on 2018/6/12.
 */
public interface IProfessionalAccomplishment {
    /**
     * 职业素养
     */
    Object getProfessionalAccomplishment(ProfessionalAccomplishmentParam professionalAccomplishmentParam);
}

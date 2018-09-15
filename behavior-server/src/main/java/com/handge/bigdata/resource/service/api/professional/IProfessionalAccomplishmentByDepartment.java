package com.handge.bigdata.resource.service.api.professional;

import com.handge.bigdata.resource.models.request.professional.ProfessionalAccomplishmentByDepartmentDetailParam;
import com.handge.bigdata.resource.models.request.professional.TopOfProfessionalAccomplishmentByDepartmentParam;

/**
 * Created by DaLu Guo on 2018/6/12.
 */
public interface IProfessionalAccomplishmentByDepartment {
    /**
     * 部门职业素养 TOP5
     *
     * @param
     * @return
     * @author MaJianfu
     * @date 2018/6/12 16:01
     **/
    public Object listTopOfProfessionalAccomplishmentByDepartment(TopOfProfessionalAccomplishmentByDepartmentParam topOfProfessionalAccomplishmentByDepartmentParam);

    /**
     * 部门职业素养排名
     *
     * @param
     * @return
     * @author MaJianfu
     * @date 2018/6/12 16:01
     **/
    public Object listProfessionalAccomplishmentByDepartmentDetail(ProfessionalAccomplishmentByDepartmentDetailParam professionalAccomplishmentByDepartmentDetailParam);


}

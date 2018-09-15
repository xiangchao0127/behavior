package com.handge.bigdata.resource.service.impl.professional;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.SQLBuilder;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.Proxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.enumeration.ExceptionWrapperEnum;
import com.handge.bigdata.enumeration.ModeEnum;
import com.handge.bigdata.resource.models.request.professional.ProfessionalAccomplishmentParam;
import com.handge.bigdata.resource.models.response.professional.ProfessionalAccomplishment;
import com.handge.bigdata.resource.service.api.professional.IProfessionalAccomplishment;
import com.handge.bigdata.utils.DateUtil;
import com.handge.bigdata.utils.FormulaUtil;
import com.handge.bigdata.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by DaLu Guo on 2018/6/12.
 */
@Component
public class ProfessionalAccomplishmentImpl implements IProfessionalAccomplishment {
    /**
     * 基础数据库查询Bean
     */
    @Autowired
    IBaseDAO baseDAO;

    @Override
    public Object getProfessionalAccomplishment(ProfessionalAccomplishmentParam professionalAccomplishmentParam) {
        String searchTime = null;
        try {
            searchTime = DateUtil.str2Str(professionalAccomplishmentParam.getStartTime(), DateFormatEnum.MONTHNEW);
        } catch (ParseException e) {
            throw new UnifiedException(e);
        }
        List<String> ips = new ArrayList<>();
        if (ModeEnum.PERSON.getMode().equals(professionalAccomplishmentParam.getModel())) {
            if (StringUtils.isEmpty(professionalAccomplishmentParam.getNumber())) {
                throw new UnifiedException("工号", ExceptionWrapperEnum.IllegalArgumentException);
            }
            String number = professionalAccomplishmentParam.getNumber();
            ips = baseDAO.getIpsByNo(number);

        } else if (ModeEnum.DEPARTMENT.getMode().equals(professionalAccomplishmentParam.getModel())) {
            if (StringUtils.isEmpty(professionalAccomplishmentParam.getDepartment())) {
                throw new UnifiedException("部门", ExceptionWrapperEnum.IllegalArgumentException);
            }
            String departName = professionalAccomplishmentParam.getDepartment();
            ips = baseDAO.getEmployeeIps(departName).get(departName);

        } else {
            for (String key : baseDAO.getEmployeeIps("").keySet()) {
                ips.addAll(baseDAO.getEmployeeIps("").get(key));
            }
        }
        Map<String, String[]> result = getIpLevel(searchTime, ips);
        if(result.size()>0) {
            return calculateScores(result);
        }
        else {
            return new ProfessionalAccomplishment() {{
                this.setComplianceDiscipline("");
                this.setComprehensiveScore("");
                this.setWorkingAttitude("");
                this.setLoyalty("");
            }};
        }
    }

    public Map<String, String[]> getIpLevel(String time, List<String> ips) {
        Proxy mySQLProxy = ProxyFactory.createProxy(DAOProxyEnum.MySQL);
        Map<String, String[]> result = new HashMap<>();
        String preSql = "SELECT\n" +
                "\ta.static_ip,\n" +
                "\ta.working_attitude,\n" +
                "\ta.loyalty,\n" +
                "\ta.compliance_discipline\n" +
                "FROM\n" +
                "\tlib_professional_accomplishment a\n" +
                "WHERE\n" +
                "\ta.time = #{time}" +
                "\tAND a.static_ip in #{ips} ;";

        String sql = SQLBuilder.sql(preSql)
                .setParamter("time", time)
                .setParamter("ips",ips)
                .toString();
        ResultSet resultSet = (ResultSet) mySQLProxy.queryBySQL(sql);
        try {
            while (resultSet.next()) {
                String[] level = new String[3];
                level[0] = resultSet.getString("working_attitude");
                level[1] = resultSet.getString("loyalty");
                level[2] = resultSet.getString("compliance_discipline");
                result.put(resultSet.getString("static_ip"), level);
            }
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        return result;
    }

    private ProfessionalAccomplishment calculateScores(Map<String, String[]> result) {

        List<BigDecimal> workingAttitudeScores = new ArrayList<>();
        List<BigDecimal> loyaltyScores = new ArrayList<>();
        List<BigDecimal> complianceDisciplineScores = new ArrayList<>();
        List<BigDecimal> ipScores = new ArrayList<>();
        for (String ip : result.keySet()) {
            workingAttitudeScores.add(new BigDecimal(result.get(ip)[0]));
            loyaltyScores.add(new BigDecimal(result.get(ip)[1]));
            complianceDisciplineScores.add(new BigDecimal(result.get(ip)[2]));
            BigDecimal ipScore = FormulaUtil.calculateComprehensiveScore(new BigDecimal(result.get(ip)[0]), new BigDecimal(result.get(ip)[1]), new BigDecimal(result.get(ip)[2]));
            ipScores.add(ipScore);
        }
        ProfessionalAccomplishment professionalAccomplishment = new ProfessionalAccomplishment();
        BigDecimal workingAttitudeScore = FormulaUtil.avgScore(workingAttitudeScores);
        BigDecimal loyaltyScore = FormulaUtil.avgScore(loyaltyScores);
        BigDecimal complianceDisciplineScore = FormulaUtil.avgScore(complianceDisciplineScores);
        BigDecimal comprehensiveScore = FormulaUtil.avgScore(ipScores);
        professionalAccomplishment.setWorkingAttitude(String.valueOf(workingAttitudeScore));
        professionalAccomplishment.setLoyalty(String.valueOf(loyaltyScore));
        professionalAccomplishment.setComplianceDiscipline(String.valueOf(complianceDisciplineScore));
        professionalAccomplishment.setComprehensiveScore(comprehensiveScore.toString());
        return professionalAccomplishment;
    }
}

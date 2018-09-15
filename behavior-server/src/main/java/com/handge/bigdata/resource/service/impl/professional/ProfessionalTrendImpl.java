package com.handge.bigdata.resource.service.impl.professional;

import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.enumeration.ModeEnum;
import com.handge.bigdata.resource.models.request.professional.ProfessionalTrendParam;
import com.handge.bigdata.resource.models.response.professional.ProfessionalAccomplishmentResult;
import com.handge.bigdata.resource.models.response.professional.ProfessionalTrend;
import com.handge.bigdata.resource.service.api.professional.IProfessionalTrend;
import com.handge.bigdata.utils.CollectionUtils;
import com.handge.bigdata.utils.DateUtil;
import com.handge.bigdata.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 职业素养趋势
 * Created by DaLu Guo on 2018/6/12.
 */
@Component
public class ProfessionalTrendImpl implements IProfessionalTrend {
    @Autowired
    IBaseDAO baseDAO;
    //记录每月IP数量
    Map<String, Integer> hashMonthSumPerson = new HashMap<>();
    //年月
    private String yearMonth = "";

    @Override
    public Object listProfessionalTrend(ProfessionalTrendParam professionalTrendParam) {
        String time = professionalTrendParam.getTime();
        if(StringUtils.isEmpty(time)){
            String timeMonth = DateUtil.timeStampToStrDate(System.currentTimeMillis(), DateFormatEnum.MONTH);
            yearMonth = DateUtil.getLastMonthIfNow(timeMonth).replace("-", "");
        }else {
            yearMonth = professionalTrendParam.getTime().substring(0, 7).replace("-", "");
        }

        if (!haveData()) {
            ArrayList<ProfessionalTrend> arrayListTrend = new ArrayList<>();
            ProfessionalTrend professionalTrend = new ProfessionalTrend();
            arrayListTrend.add(professionalTrend);
            return arrayListTrend;
        }
        if (ModeEnum.COMAPNY.getMode().equals(professionalTrendParam.getModel())) {
            return listProfessionalTrendCompany();
        } else if (ModeEnum.DEPARTMENT.getMode().equals(professionalTrendParam.getModel())) {
            return listProfessionalTrendDepartment(professionalTrendParam.getDepartment());
        }

        //个人职业素养趋势
        ArrayList<ProfessionalTrend> professionalTrends = listProfessionalTrendPerson(professionalTrendParam.getNumber());
        return professionalTrends;
    }

    /**
     * 公司职业素养趋势
     *
     * @return
     */
    private ArrayList<ProfessionalTrend> listProfessionalTrendCompany() {
        //获取模型结果
        List<ProfessionalAccomplishmentResult> professionalAccomplishmentResults = baseDAO.getProfessionalAccomplishmentResults(yearMonth, true);
        //按照月份分组
        Map<String, List<ProfessionalAccomplishmentResult>> groupProfessionalAccomplishmentResult = CollectionUtils.group(professionalAccomplishmentResults, d -> ((ProfessionalAccomplishmentResult) d).getTime());
        //按月求出总分
        Map<String, Double> sumScore = getSumScore(groupProfessionalAccomplishmentResult);
        //计算每月平均分
        Map<String, String> avgScore = getAvgScore(sumScore);
        ArrayList<ProfessionalTrend> professionalTrends = wrapDataCompany(avgScore);
        return professionalTrends;
    }

    /**
     * 部门职业素养趋势
     *
     * @return
     */
    private ArrayList<ProfessionalTrend> listProfessionalTrendDepartment(String departmentName) {
        //获取模型结果
        List<ProfessionalAccomplishmentResult> professionalAccomplishmentResults = baseDAO.getProfessionalAccomplishmentResults(yearMonth, true);
        //查询部门所有Ip
        HashMap<String, ArrayList<String>> depIps = baseDAO.getEmployeeIps(departmentName);
        //保留部门数据
        professionalAccomplishmentResults.removeIf(r -> !depIps.get(departmentName).contains(r.getStaticIp()));
        if(professionalAccomplishmentResults.size()==0){
            return new ArrayList<ProfessionalTrend>(){{
                this.add(new ProfessionalTrend());
            }};
        }
        //按照月份分组
        Map<String, List<ProfessionalAccomplishmentResult>> groupProfessionalAccomplishmentResult = CollectionUtils.group(professionalAccomplishmentResults, d -> ((ProfessionalAccomplishmentResult) d).getTime());
        //按月求出总分
        Map<String, Double> sumScore = getSumScore(groupProfessionalAccomplishmentResult);
        //计算每月平均分
        Map<String, String> avgScore = getAvgScore(sumScore);
        ArrayList<ProfessionalTrend> professionalTrends = wrapDataDepartment(avgScore);
        return professionalTrends;
    }

    /**
     * 个人职业素养趋势
     *
     * @return
     */
    private ArrayList<ProfessionalTrend> listProfessionalTrendPerson(String number) {
        //获取模型结果
        List<ProfessionalAccomplishmentResult> professionalAccomplishmentResults = baseDAO.getProfessionalAccomplishmentResults(yearMonth, true);
        //通过工号查询员工所有Ip
        List<String> depIpsByNo = baseDAO.getIpsByNo(number);
        //保留个人数据
        professionalAccomplishmentResults.removeIf(r -> !depIpsByNo.contains(r.getStaticIp()));
        if(professionalAccomplishmentResults.size()==0){
            return new ArrayList<ProfessionalTrend>(){{
                this.add(new ProfessionalTrend());
            }};
        }
        //按照月份分组
        Map<String, List<ProfessionalAccomplishmentResult>> groupProfessionalAccomplishmentResult = CollectionUtils.group(professionalAccomplishmentResults, d -> ((ProfessionalAccomplishmentResult) d).getTime());
        //按月求出总分
        Map<String, Double> sumScore = getSumScore(groupProfessionalAccomplishmentResult);
        //计算每月平均分
        Map<String, String> avgScore = getAvgScore(sumScore);
        ArrayList<ProfessionalTrend> professionalTrends = wrapDataPerson(avgScore, number);
        return professionalTrends;
    }


    /**
     * 包装数据给前端
     *
     * @return
     */
    private ArrayList<ProfessionalTrend> wrapDataCompany(Map<String, String> avgScore) {
        ArrayList<ProfessionalTrend> professionalTrends = new ArrayList<>();
        Set<String> times = avgScore.keySet();
        for (String time : times) {
            ProfessionalTrend professionalTrend = new ProfessionalTrend();
            professionalTrend.setMonth(time.substring(4, 6));
            professionalTrend.setName("");
            professionalTrend.setPersonAvgScore("");
            professionalTrend.setCompanyAvgScore(avgScore.get(time));
            professionalTrend.setDepartmentAvgScore("");
            professionalTrends.add(professionalTrend);
        }

        return professionalTrends;
    }

    private ArrayList<ProfessionalTrend> wrapDataDepartment(Map<String, String> avgScore) {
        ArrayList<ProfessionalTrend> professionalTrends = new ArrayList<>();
        Set<String> times = avgScore.keySet();
        for (String time : times) {
            ProfessionalTrend professionalTrend = new ProfessionalTrend();
            professionalTrend.setMonth(time.substring(4, 6));
            professionalTrend.setName("");
            professionalTrend.setPersonAvgScore("");
            professionalTrend.setCompanyAvgScore("");
            professionalTrend.setDepartmentAvgScore(avgScore.get(time));
            professionalTrends.add(professionalTrend);
        }

        return professionalTrends;
    }

    private ArrayList<ProfessionalTrend> wrapDataPerson(Map<String, String> avgScore, String number) {
        ArrayList<ProfessionalTrend> professionalTrends = new ArrayList<>();
        Set<String> times = avgScore.keySet();
        for (String time : times) {
            ProfessionalTrend professionalTrend = new ProfessionalTrend();
            professionalTrend.setMonth(time.substring(4, 6));
            professionalTrend.setName(baseDAO.getEmployeeNameByNumber(number));
            professionalTrend.setPersonAvgScore(avgScore.get(time));
            professionalTrend.setCompanyAvgScore("");
            professionalTrend.setDepartmentAvgScore("");
            professionalTrends.add(professionalTrend);
        }

        return professionalTrends;
    }

    private Map<String, Double> getSumScore(Map<String, List<ProfessionalAccomplishmentResult>> map) {
        Map<String, Double> mapD = new HashMap<>();
        Set<String> times = map.keySet();
        for (String time : times) {
            Double reduce = CollectionUtils.reduce(map.get(time), 0.0, (d, p) -> Double.valueOf(p.getWorkingAttitude()) + d);
            hashMonthSumPerson.put(time, map.get(time).size());
            mapD.put(time, reduce);
        }
        return mapD;
    }

    private Map<String, String> getAvgScore(Map<String, Double> map) {
        Map<String, String> mapD = new HashMap<>();
        Set<String> times = map.keySet();
        for (String time : times) {
            String avg = new BigDecimal(map.get(time)).divide(new BigDecimal(hashMonthSumPerson.get(time)),1, RoundingMode.HALF_UP).toString();
            mapD.put(time, avg);
        }
        return mapD;
    }

    private boolean haveData() {
        //查询当月数据
        List<ProfessionalAccomplishmentResult> professionalAccomplishmentResults = baseDAO.getProfessionalAccomplishmentResults(yearMonth, true);
        if (professionalAccomplishmentResults.size() == 0) {
            return false;
        }
        return true;
    }

}

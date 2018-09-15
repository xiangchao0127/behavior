package com.handge.bigdata.resource.service.impl.professional;

import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.enumeration.ModeEnum;
import com.handge.bigdata.resource.models.request.professional.ScoreDistributionParam;
import com.handge.bigdata.resource.models.response.professional.ProfessionalAccomplishmentResult;
import com.handge.bigdata.resource.models.response.professional.ScoreDistribution;
import com.handge.bigdata.resource.models.response.professional.ScoreDistributionTrend;
import com.handge.bigdata.resource.models.response.professional.ScoreDistributionTrendMaker;
import com.handge.bigdata.resource.service.api.professional.IScoreDistribution;
import com.handge.bigdata.utils.CollectionUtils;
import com.handge.bigdata.utils.DateUtil;
import com.handge.bigdata.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

/**
 * 所在位置
 * Created by DaLu Guo on 2018/6/12.
 */
@Component
public class ScoreDistributionImpl implements IScoreDistribution {

    @Autowired
    IBaseDAO baseDAO;
    //年月
    private String yearMonth = "";

    @Override
    public Object listScoreDistribution(ScoreDistributionParam scoreDistributionParam) {
        String time = scoreDistributionParam.getTime();
        if(StringUtils.isEmpty(time)){
            String timeMonth = DateUtil.timeStampToStrDate(System.currentTimeMillis(), DateFormatEnum.MONTH);
            yearMonth = DateUtil.getLastMonthIfNow(timeMonth).replace("-", "");
        }else {
            yearMonth = scoreDistributionParam.getTime().substring(0, 7).replace("-", "");
        }
        if (!haveData()) {
            return wrapEmptyData();
        }
        if (ModeEnum.COMAPNY.getMode().equals(scoreDistributionParam.getModel())) {
            return listScoreDistributionCompany();
        } else if (ModeEnum.DEPARTMENT.getMode().equals(scoreDistributionParam.getModel())) {
            return listScoreDistributionDepartment(scoreDistributionParam.getDepartment());
        }
        //个人职业素养
        ScoreDistribution scoreDistribution = listScoreDistributionPerson(scoreDistributionParam.getNumber());
        return scoreDistribution;
    }

    /**
     * 公司分数分布
     *
     * @return
     */
    private Object listScoreDistributionCompany() {
        Map<String, String> mapAvg = getCommonData();

        //通过分数分组
        Map<String, Integer> stringIntegerMap = groupByScore(mapAvg);
        ScoreDistribution scoreDistribution = wrapDataCompanyAndDep(stringIntegerMap, ModeEnum.COMAPNY);
        return scoreDistribution;
    }

    /**
     * 部门分数分布
     *
     * @return
     */
    private Object listScoreDistributionDepartment(String departmentName) {
        Map<String, String> mapAvg = getCommonData();
        List<String> numbersByDepName = baseDAO.getNumbersByDepName(departmentName);

        for (Iterator<Map.Entry<String, String>> it = mapAvg.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, String> item = it.next();
            if (!numbersByDepName.contains(item.getKey())) {
                it.remove();
            }
        }
        //通过分数分组
        Map<String, Integer> stringIntegerMap = groupByScore(mapAvg);
        ScoreDistribution scoreDistribution = wrapDataCompanyAndDep(stringIntegerMap, ModeEnum.DEPARTMENT);
        return scoreDistribution;
    }

    /**
     * 个人分数
     *
     * @return
     */
    private ScoreDistribution listScoreDistributionPerson(String number) {
        //查询员工部门名称以及部门人数
        String[] depInfoByNo = baseDAO.getDepInfoByNo(number);
        Map<String, String> mapAvg = getCommonData();
        List<String> numbersByDepName = baseDAO.getNumbersByDepName(depInfoByNo[0]);
        Map<String, Integer> stringIntegerMapComp = groupByScore(mapAvg);

        for (Iterator<Map.Entry<String, String>> it = mapAvg.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, String> item = it.next();
            if (!numbersByDepName.contains(item.getKey())) {
                it.remove();
            }
        }
        //通过分数分组
        Map<String, Integer> stringIntegerMapDep = groupByScore(mapAvg);
        ScoreDistribution scoreDistribution = wrapDataPerson(stringIntegerMapComp, stringIntegerMapDep, mapAvg, number);
        return scoreDistribution;
    }

    /**
     * 获取公共数据
     *
     * @return
     */
    private Map<String, String> getCommonData() {
        //查询当月数据
        List<ProfessionalAccomplishmentResult> professionalAccomplishmentResults = baseDAO.getProfessionalAccomplishmentResults(yearMonth, false);

        //获取所有员工ip及对应的职工编号
        HashMap<String, String> allEmployeeIpAndNumber = baseDAO.getAllEmployeeIpAndNumber();
        //将ip转化为工号,等级转化为分数
        List<ProfessionalAccomplishmentResult> listProfessionalResults = (List<ProfessionalAccomplishmentResult>) CollectionUtils.map(professionalAccomplishmentResults, new Function() {
            @Override
            public Object apply(Object o) {
                ProfessionalAccomplishmentResult professionalAccomplishmentResult = (ProfessionalAccomplishmentResult) o;
                professionalAccomplishmentResult.setStaticIp(allEmployeeIpAndNumber.get(professionalAccomplishmentResult.getStaticIp()));
                return professionalAccomplishmentResult;
            }
        });
        //通过工号分组
        Map<String, List<ProfessionalAccomplishmentResult>> datasByNumber = CollectionUtils.group(listProfessionalResults, r -> ((ProfessionalAccomplishmentResult) r).getStaticIp());
        //获取员工平均得分
        Map<String, String> mapAvg = getAvg(datasByNumber);
        return mapAvg;
    }

    /**
     * 封装数据到前端 公司,部门
     *
     * @param map
     * @return
     */
    private ScoreDistribution wrapDataCompanyAndDep(Map<String, Integer> map, ModeEnum modeEnum) {
        ScoreDistribution scoreDistribution = new ScoreDistribution();
        ScoreDistributionTrendMaker scoreDistributionTrendMaker = new ScoreDistributionTrendMaker();
        ArrayList<ScoreDistributionTrend> scoreDistributionTrends = new ArrayList<>();
        Set<String> scores = map.keySet();
        for (String score : scores) {
            ScoreDistributionTrend scoreDistributionTrend = new ScoreDistributionTrend();
            scoreDistributionTrend.setStartScore(score + "0");
            scoreDistributionTrend.setNumberOfPeople(String.valueOf(map.get(score)));
            scoreDistributionTrends.add(scoreDistributionTrend);
        }
        scoreDistributionTrendMaker.setScoreDistributionTrends(scoreDistributionTrends);
        if (ModeEnum.COMAPNY.equals(modeEnum)) {
            scoreDistributionTrendMaker.setMode(ModeEnum.COMAPNY.getDesc());
            scoreDistribution.setScoreDistributionTrendComp(scoreDistributionTrendMaker);
        } else if (ModeEnum.DEPARTMENT.equals(modeEnum)) {
            scoreDistributionTrendMaker.setMode(ModeEnum.DEPARTMENT.getDesc());
            scoreDistribution.setScoreDistributionTrendDep(scoreDistributionTrendMaker);
        }

        return scoreDistribution;
    }

    /**
     * 封装数据到前端 个人
     *
     * @param mapCompany
     * @return
     */
    private ScoreDistribution wrapDataPerson(Map<String, Integer> mapCompany, Map<String, Integer> mapDepartment, Map<String, String> avgMap, String number) {
        ScoreDistribution scoreDistribution = new ScoreDistribution();
        scoreDistribution.setName(baseDAO.getEmployeeNameByNumber(number));
        scoreDistribution.setScore(avgMap.get(number));
        //公司
        ScoreDistributionTrendMaker scoreDistributionTrendComp = new ScoreDistributionTrendMaker();
        ArrayList<ScoreDistributionTrend> scoreDistributionTrends = new ArrayList<>();
        Set<String> scores = mapCompany.keySet();
        for (String score : scores) {
            ScoreDistributionTrend scoreDistributionTrend = new ScoreDistributionTrend();
            scoreDistributionTrend.setStartScore(score + "0");
            scoreDistributionTrend.setNumberOfPeople(String.valueOf(mapCompany.get(score)));
            scoreDistributionTrends.add(scoreDistributionTrend);
        }

        scoreDistributionTrendComp.setMode(ModeEnum.COMAPNY.getDesc());
        scoreDistributionTrendComp.setScoreDistributionTrends(scoreDistributionTrends);
        //部门
        ScoreDistributionTrendMaker scoreDistributionTrendDep = new ScoreDistributionTrendMaker();
        ArrayList<ScoreDistributionTrend> scoreDistributionTrendDeps = new ArrayList<>();
        Set<String> scoresDep = mapDepartment.keySet();
        for (String score : scoresDep) {
            ScoreDistributionTrend scoreDistributionTrend = new ScoreDistributionTrend();
            scoreDistributionTrend.setStartScore(score + "0");
            scoreDistributionTrend.setNumberOfPeople(String.valueOf(mapDepartment.get(score)));
            scoreDistributionTrendDeps.add(scoreDistributionTrend);
        }

        scoreDistributionTrendDep.setMode(ModeEnum.DEPARTMENT.getDesc());
        scoreDistributionTrendDep.setScoreDistributionTrends(scoreDistributionTrendDeps);

        scoreDistribution.setScoreDistributionTrendComp(scoreDistributionTrendComp);
        scoreDistribution.setScoreDistributionTrendDep(scoreDistributionTrendDep);

        return scoreDistribution;
    }


    private Map<String, String> getAvg(Map<String, List<ProfessionalAccomplishmentResult>> map) {
        Map<String, String> mapAvg = new HashMap<>();
        Set<String> numbers = map.keySet();
        for (String number : numbers) {
            Double sumScore = CollectionUtils.reduce(map.get(number), 0.0, (d, p) -> Double.valueOf(p.getWorkingAttitude()) + d);
            String v = new BigDecimal(sumScore / map.get(number).size()).setScale(1, 1).toString();
            mapAvg.put(number, v);
        }
        return mapAvg;
    }

    private Map<String, Integer> groupByScore(Map<String, String> map) {
        Map<String, Integer> scoreMap = new HashMap<>();
        Set<String> numbers = map.keySet();
        for (String number : numbers) {
            String key = map.get(number).substring(0, 1);
            if (scoreMap.get(key) == null) {
                scoreMap.put(key, 1);
            } else {
                scoreMap.put(key, scoreMap.get(key) + 1);
            }
        }

        return scoreMap;
    }

    private boolean haveData() {
        //查询当月数据
        List<ProfessionalAccomplishmentResult> professionalAccomplishmentResults = baseDAO.getProfessionalAccomplishmentResults(yearMonth, false);
        if (professionalAccomplishmentResults.size() == 0) {
            return false;
        }
        return true;
    }

    private ScoreDistribution wrapEmptyData(){
        ScoreDistribution scoreDistribution = new ScoreDistribution();
        //部门
        ScoreDistributionTrendMaker scoreDistributionTrendMaker = new ScoreDistributionTrendMaker();
        ScoreDistributionTrend scoreDistributionTrend = new ScoreDistributionTrend();
        ArrayList<ScoreDistributionTrend> arrayListTrend = new ArrayList<>();
        arrayListTrend.add(scoreDistributionTrend);
        scoreDistributionTrendMaker.setScoreDistributionTrends(arrayListTrend);
        //公司
        scoreDistribution.setScoreDistributionTrendDep(scoreDistributionTrendMaker);
        scoreDistribution.setScoreDistributionTrendComp(scoreDistributionTrendMaker);
        return scoreDistribution;
    }

}

package com.handge.bigdata.resource.service.impl.professional;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.resource.models.request.professional.RankingParam;
import com.handge.bigdata.resource.models.response.professional.ProfessionalAccomplishmentResult;
import com.handge.bigdata.resource.models.response.professional.Ranking;
import com.handge.bigdata.resource.service.api.professional.IRanking;
import com.handge.bigdata.utils.CollectionUtils;
import com.handge.bigdata.utils.DateUtil;
import com.handge.bigdata.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;


/**
 * 位置
 * Created by DaLu Guo on 2018/6/12.
 */
@Component
public class RankingImpl implements IRanking {
    @Autowired
    IBaseDAO baseDAO;

    @Override
    public Object getRanking(RankingParam rankingParam) {
        //通过工号查ip
        ArrayList<String> ipsByNo = (ArrayList) baseDAO.getIpsByNo(rankingParam.getNumber());
        String time = rankingParam.getTime();
        if(StringUtils.isEmpty(time)){
            String timeMonth = DateUtil.timeStampToStrDate(System.currentTimeMillis(), DateFormatEnum.MONTH);
            time = DateUtil.getLastMonthIfNow(timeMonth);
        }else {
            try {
                time = DateUtil.str2Str(time,DateFormatEnum.MONTH);
            } catch (ParseException e) {
                throw new UnifiedException(e);
            }
        }
        //查询员工部门名称以及部门人数
        String[] depInfoByNo = baseDAO.getDepInfoByNo(rankingParam.getNumber());
        //查询当月数据
        List<ProfessionalAccomplishmentResult> professionalAccomplishmentResults = baseDAO.getProfessionalAccomplishmentResults(time.replace("-", ""), false);
        if (professionalAccomplishmentResults.size() == 0 || !baseDAO.isExistInLib(ipsByNo,time)) {
            return new Ranking();
        }
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
        List<Map.Entry<String, String>> listAvg = new ArrayList<>(mapAvg.entrySet());
        //排序
        listAvg.sort((r1, r2) -> -r1.getValue().compareTo(r2.getValue()));

        /**
         * 封装数据到前段
         */
        Ranking ranking = wrapData(listAvg, rankingParam.getNumber(), baseDAO.getNumbersByDepName(depInfoByNo[0]));
        return ranking;
    }

    /**
     * 封装数据到前段
     */
    private Ranking wrapData(List<Map.Entry<String, String>> list, String number, List<String> numbers) {
        list.removeIf(r -> r.getKey() == null);
        int rank = 0;
        for (Map.Entry<String, String> entry : list) {
            rank++;
            if (entry.getKey().equals(number)) {
                break;
            }
        }
        Ranking ranking = new Ranking();
        ranking.setOverPercent(new BigDecimal("100.00").subtract(new BigDecimal(rank).divide(new BigDecimal(baseDAO.totalNumberOfEmployeesOnGuard()), 4, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal("100"))).setScale(1, 1).toString());
        ranking.setRankingCompany(String.valueOf(rank));
        ranking.setCountCompany(String.valueOf(baseDAO.totalNumberOfEmployeesOnGuard()));
        list.removeIf(r -> !numbers.contains(r.getKey()));
        int rankDep = 0;
        for (Map.Entry<String, String> entry : list) {
            rankDep++;
            if (entry.getKey().equals(number)) {
                break;
            }
        }
        ranking.setRankingDepartment(String.valueOf(rankDep));
        ranking.setCountDepartment(String.valueOf(baseDAO.getDepInfoByNo(number)[1]));

        return ranking;
    }

    private Map<String, String> getAvg(Map<String, List<ProfessionalAccomplishmentResult>> map) {
        Map<String, String> mapAvg = new HashMap<>();
        Set<String> numbers = map.keySet();
        for (String number : numbers) {
            Double sumScore = CollectionUtils.reduce(map.get(number), 0.0, (d, p) -> Double.valueOf(p.getWorkingAttitude()) + d);
            String v = new BigDecimal(sumScore / map.get(number).size()).setScale(2, 1).toString();
            mapAvg.put(number, v);
        }
        return mapAvg;
    }
}

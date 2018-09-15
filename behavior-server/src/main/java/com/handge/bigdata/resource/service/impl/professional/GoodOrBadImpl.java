package com.handge.bigdata.resource.service.impl.professional;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.SQLBuilder;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.Proxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.enumeration.StaffModelEnum;
import com.handge.bigdata.resource.models.request.professional.StaffDetailParam;
import com.handge.bigdata.resource.models.request.professional.TopOfStaffParam;
import com.handge.bigdata.resource.models.response.professional.Staff;
import com.handge.bigdata.resource.service.api.professional.IGoodOrBad;
import com.handge.bigdata.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

/**
 *
 * @author MaJianfu
 * @date 2018/6/15 13:08
 **/
@Component
public class GoodOrBadImpl implements IGoodOrBad {
    /**
     * 优秀员工 TOP5 或者 差劲员工 TOP5
     *
     * @return
     */

    @Autowired
    IBaseDAO baseDAO;


    @Override
    public Object listTopOfStaff(TopOfStaffParam topOfStaffParam) {
        HashMap<String, String> stringStringHashMap = scoreStaff(topOfStaffParam.getStartTime(),topOfStaffParam.getDepartment());
        if(stringStringHashMap == null || stringStringHashMap.size() == 0){
            List<Staff> resultStaffTotal = new ArrayList<>();
            Staff sta=new Staff();
            resultStaffTotal.add(sta);
            return resultStaffTotal;
        }else{
        List<Staff> resultStaff = staffList(stringStringHashMap);
        if(StaffModelEnum.优秀员工.getModel().equals(topOfStaffParam.getModel())){
            List<Staff> resultStaffTotal = bestStaff(resultStaff);
            List<Staff> topOfBestStaff = TopOfBestStaff(topOfStaffParam.getN(), resultStaffTotal);
            return topOfBestStaff.subList(0, topOfBestStaff.size() >topOfStaffParam.getN() ? topOfStaffParam.getN() :topOfBestStaff.size());
        }else{
            List<Staff> topOfPoorStaff = TopOfPoorStaff(topOfStaffParam.getN(), resultStaff);
            return topOfPoorStaff.subList(0, topOfPoorStaff.size() >topOfStaffParam.getN() ? topOfStaffParam.getN() :topOfPoorStaff.size());
        }
        }
    }
    /**
     * 优秀员工排名 或者 差劲员工排名
     *
     * @return
     */
    @Override
    public Object listStaffDetail(StaffDetailParam staffDetailParam) {
        HashMap<String, String> stringStringHashMap = scoreStaff(staffDetailParam.getStartTime(),staffDetailParam.getDepartment());
        if(stringStringHashMap == null || stringStringHashMap.size() == 0){
            List<Staff> resultStaffTotal = new ArrayList<>();
            Staff sta=new Staff();
            resultStaffTotal.add(sta);
            return resultStaffTotal;
        }else{
        List<Staff> resultStaff = staffList(stringStringHashMap);
        if(StaffModelEnum.优秀员工.getModel().equals(staffDetailParam.getModel())){
            List<Staff> resultStaffTotal = bestStaff(resultStaff);
            PageResults<Staff> pageResult = CollectionUtils.getPageResult(resultStaffTotal, staffDetailParam.getPageNo(), staffDetailParam.getPageSize());
            return pageResult;
        }else{
            List<Staff> poorStaffList = poorStaff(resultStaff);
            PageResults<Staff> pageResult = CollectionUtils.getPageResult(poorStaffList, staffDetailParam.getPageNo(), staffDetailParam.getPageSize());
            return pageResult;
        }
        }
    }

    Proxy proxyMysql = ProxyFactory.createProxy(DAOProxyEnum.MySQL);
    @Override
    public HashMap<String, String> scoreStaff(String date,String department_name) {
        String sqlIp ="select * from lib_professional_accomplishment where time=#{date}";
        if(StringUtils.notEmpty(date)){
            date= date.substring(0, 7).replace("-", "");
        }else{
            try {
                date=DateUtil.getNextMonth(DateUtil.timeStampToStrDate(System.currentTimeMillis(), DateFormatEnum.DAY),-1).replace("-", "");
            } catch (ParseException e) {
                throw new UnifiedException(e);
            }
        }
            String excuteSqlToTal = SQLBuilder.sql(sqlIp)
                    .setParamter("date",date)
                    .toString();
            ResultSet resultSet = (ResultSet) proxyMysql.queryBySQL(excuteSqlToTal);
            DecimalFormat dFormat = new DecimalFormat("#0.0");
            HashMap<String, String> ipMap = new HashMap<>();
        try {
            while (resultSet.next()) {
                String static_ip=resultSet.getString("static_ip");
                String time=resultSet.getString("time");
                String loyalty=resultSet.getString("loyalty");
                String working_attitude=resultSet.getString("working_attitude");
                String compliance_discipline=resultSet.getString("compliance_discipline");
                String avgScore= FormulaUtil.calculateComprehensiveScore(new BigDecimal(loyalty), new BigDecimal(working_attitude), new BigDecimal(compliance_discipline)).toString();
                ipMap.put(static_ip+"|"+time,loyalty+"|"+working_attitude+"|"+compliance_discipline+"|"+avgScore);
            }
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        HashMap<String, ArrayList<String>> numberMap = new HashMap<>();
            HashMap<String, String> map = baseDAO.getAllEmployeeIpAndNumber();
            HashMap<String, String> ipDepMap = ipDepMap();
            for (Map.Entry<String,String> entry :ipMap.entrySet()) {
                String[] str = entry.getValue().split("\\|");
                String number ="";
                if(StringUtils.notEmpty(department_name)){
                    if(department_name.equals(ipDepMap.get(entry.getKey().split("\\|")[0]))){
                        if(map.containsKey(entry.getKey().split("\\|")[0])){
                            number = map.get(entry.getKey().split("\\|")[0]);
                            if (numberMap.get(number+"|"+entry.getKey().split("\\|")[1]) == null) {
                                numberMap.put(number+"|"+entry.getKey().split("\\|")[1], new ArrayList<>(Arrays.asList(str[0]+"|"+str[1]+"|"+str[2]+"|"+str[3])));
                            } else {
                                numberMap.get(number+"|"+entry.getKey().split("\\|")[1]).add(str[0]+"|"+str[1]+"|"+str[2]+"|"+str[3]);
                            }
                        }
                    }
                }else{
                    if(map.containsKey(entry.getKey().split("\\|")[0])){
                        number = map.get(entry.getKey().split("\\|")[0]);
                        if (numberMap.get(number+"|"+entry.getKey().split("\\|")[1]) == null) {
                            numberMap.put(number+"|"+entry.getKey().split("\\|")[1], new ArrayList<>(Arrays.asList(str[0]+"|"+str[1]+"|"+str[2]+"|"+str[3])));
                        } else {
                            numberMap.get(number+"|"+entry.getKey().split("\\|")[1]).add(str[0]+"|"+str[1]+"|"+str[2]+"|"+str[3]);
                        }
                    }
                }

            }
            HashMap<String, String> scoreMap = new HashMap<>();
            for (Map.Entry<String, ArrayList<String>> entry : numberMap.entrySet()) {
                ArrayList<String> value = entry.getValue();
                Double loyalty=0.0;
                Double working_attitude=0.0;
                Double compliance_discipline=0.0;
                Double avgScore=0.0;
                int i=0;
                for(String scoreToTal:value){
                    String[] score = scoreToTal.split("\\|");
                    loyalty+= Double.parseDouble(score[0]);
                    working_attitude+= Double.parseDouble(score[1]);
                    compliance_discipline+= Double.parseDouble(score[2]);
                    avgScore+= Double.parseDouble(score[3]);
                    i++;
                }
                String loyaltyTotal=dFormat.format(loyalty/i);
                String working_attitudeTotal=dFormat.format(working_attitude/i);
                String compliance_disciplineTotal=dFormat.format(compliance_discipline/i);
                String avgScoreTotal=dFormat.format(avgScore/i);
                scoreMap.put(entry.getKey().split("\\|")[0]+"|"+entry.getKey().split("\\|")[1],loyaltyTotal+"|"+working_attitudeTotal+"|"+compliance_disciplineTotal+"|"+avgScoreTotal);
            }
            return scoreMap;
    }

    public List<Staff> staffList(HashMap<String, String> scoreMap){
        List<Staff> resultStaff = new ArrayList<>();
            Map<String, String> numberNameMap = baseDAO.getAllEmployeeNumberAndName();
        for (Map.Entry<String,String> entry :scoreMap.entrySet()) {
                String name ="";
                //String grade ="";
            if(numberNameMap.containsKey(entry.getKey().split("\\|")[0])){
                name = numberNameMap.get(entry.getKey().split("\\|")[0]);
                }
                String value[] = entry.getValue().split("\\|");
                /*String professionalism =score(value[0]+"|"+ProfessionalismEnum.loyalty.getValue(),
                        value[1]+"|"+ProfessionalismEnum.working_attitude.getValue(),
                        value[2]+"|"+ProfessionalismEnum.compliance_discipline.getValue());
            double v = Double.parseDouble(professionalism.split("\\|")[0]);
            if(v >= 0 && v <= 20){
                grade= GradeEnum.E.getValue();
            }else if(v > 20 && v <= 40){
                grade= GradeEnum.D.getValue();
            }else if(v > 40 && v <= 60){
                grade= GradeEnum.C.getValue();
            }else if(v > 60 && v <= 80){
                grade= GradeEnum.B.getValue();
            }else if(v > 80 && v <= 100){
                grade= GradeEnum.A.getValue();
            }*/
                Staff staff=new Staff();
                staff.setNumber(entry.getKey().split("\\|")[0]);
                staff.setName(name);
                staff.setScore(value[3]);
                //staff.setProfessionalism(professionalism.split("\\|")[1]);
                //staff.setGrade(grade);
                resultStaff.add(staff);
            }
            return resultStaff;
    }

    /*public String score(String a,String b,String c){
        Double double1=Double.parseDouble(a.split("\\|")[0]);
        Double double2=Double.parseDouble(b.split("\\|")[0]);
        Double double3=Double.parseDouble(c.split("\\|")[0]);
        Double min= (double1<double2) ? double1 : double2;
        min = (min <double3) ? min :double3;
        if(min.equals(double1)){
            return a;
        }else if(min.equals(double2)){
            return b;
        }else{
            return c;
        }

    }*/

    public List<Staff> bestStaff(List<Staff> resultStaff){
        List<Staff> resultStaffTotal = new LinkedList<>();
        Collections.sort(resultStaff, new Comparator<Staff>() {
            @Override
            public int compare(Staff o1, Staff o2) {
                return -Double.compare(Double.parseDouble(o1.getScore()),
                        Double.parseDouble(o2.getScore()));
            }
        });
        int i=1;
        for(Staff st:resultStaff){
            Staff staff=new Staff();
            staff.setNum(String.valueOf(i));
            staff.setName(st.getName());
            staff.setScore(st.getScore());
            staff.setNumber(st.getNumber());
            resultStaffTotal.add(staff);
            i++;
        }
        return resultStaffTotal;
    }

    public List<Staff> TopOfBestStaff(int n,List<Staff> resultStaffTotal){
        int i = 0;
        List<Staff> list2 = new LinkedList<>();
        for (Staff mapping : resultStaffTotal) {
            Staff staff=new Staff();
            staff.setName(mapping.getName());
            staff.setScore(mapping.getScore());
            list2.add(staff);
            i++;
            if (i == n) {
                break;
            }
        }
        return list2;
    }

    public List<Staff> poorStaff(List<Staff> resultStaff){
        List<Staff> poorStaffTotal = new LinkedList<>();
        Collections.sort(resultStaff, new Comparator<Staff>() {
            @Override
            public int compare(Staff o1, Staff o2) {
                return Double.compare(Double.parseDouble(o1.getScore()),
                        Double.parseDouble(o2.getScore()));
            }
        });
        int i=1;
        for(Staff sta:resultStaff){
            Staff staff=new Staff();
            staff.setNum(String.valueOf(i));
            staff.setName(sta.getName());
            staff.setScore(sta.getScore());
            staff.setNumber(sta.getNumber());
            poorStaffTotal.add(staff);
            i++;
        }
        return poorStaffTotal;
    }

    public List<Staff> TopOfPoorStaff(int n,List<Staff> resultStaff){
        List<Staff> TopOfPoorStaff = new LinkedList<>();
        Collections.sort(resultStaff, new Comparator<Staff>() {
            @Override
            public int compare(Staff o1, Staff o2) {
                return Double.compare(Double.parseDouble(o1.getScore()),
                        Double.parseDouble(o2.getScore()));
            }
        });
        int i = 0;
        for(Staff sta:resultStaff){
            Staff staff=new Staff();
            staff.setName(sta.getName());
            staff.setScore(sta.getScore());
            //staff.setProfessionalism(sta.getProfessionalism());
            //staff.setGrade(sta.getGrade());
            TopOfPoorStaff.add(staff);
            i++;
            if (i == n) {
                break;
            }
        }
        return TopOfPoorStaff;
    }

    public HashMap<String,String> ipDepMap(){
        HashMap<String,String> ipDepMap=new HashMap<>();
        String sqlIpDep ="SELECT dep.department_name AS department_name,dev.static_ip AS static_ip\n" +
                "FROM entity_employee_information_basic emp \n" +
                "JOIN auth_account cou ON cou.employee_id = emp.id\n" +
                "JOIN entity_device_basic dev ON dev.account_id = cou.id\n" +
                "INNER JOIN entity_department_information_basic AS dep\n" +
                "WHERE emp.department_id = dep.department_id\n" +
                "AND emp. STATUS != 4";
        ResultSet rs= (ResultSet) proxyMysql.queryBySQL(sqlIpDep);
        try {
            while (rs.next()){
                String static_ip = rs.getString("static_ip");
                String department_name = rs.getString("department_name");
                ipDepMap.put(static_ip,department_name);
            }
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        return ipDepMap;
    }
}

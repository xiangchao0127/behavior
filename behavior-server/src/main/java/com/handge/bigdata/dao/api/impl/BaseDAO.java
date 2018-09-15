package com.handge.bigdata.dao.api.impl;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.SQLBuilder;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.model.AppIdentify;
import com.handge.bigdata.dao.proxy.MySQLProxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.EmployeeStatusEnum;
import com.handge.bigdata.resource.models.response.monitor.Alarm;
import com.handge.bigdata.resource.models.response.monitor.Illegal;
import com.handge.bigdata.resource.models.response.professional.ProfessionalAccomplishmentResult;
import com.handge.bigdata.utils.DateUtil;
import com.handge.bigdata.utils.FormulaUtil;
import com.handge.bigdata.utils.NumberUtil;
import com.handge.bigdata.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

@Component
public class BaseDAO implements IBaseDAO {

    Log logger = LogFactory.getLog(this.getClass());

    private MySQLProxy mySQLProxy;

    public BaseDAO() {
        mySQLProxy = ProxyFactory.createProxy(DAOProxyEnum.MySQL);
    }

    @Override
    public Integer totalNumberOfEmployeesOnGuard() {
        ResultSet resultSet = mySQLProxy.queryBySQL("SELECT COUNT(*) AS sumPerson FROM entity_employee_information_basic WHERE status!=" + EmployeeStatusEnum.QUIT.getStatus());
        try {
            while (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        return 0;
    }

    @Override
    public Integer totalNumberOfEmployeesOnGuard(String time) {
        if (!DateUtil.checkDateStr(time)) {
            throw new RuntimeException("参数异常,时间格式为yyyy-MM或者yyyy-MM-dd");
        }
        String nextMonth = "";
        if (time.length() == 7) {
            try {
                nextMonth = DateUtil.getNextMonth(time, 1);
            } catch (ParseException e) {
                 throw new UnifiedException(e);
            }
        } else {
            nextMonth = time;
        }
        String preSql = "SELECT COUNT(*) AS sumPerson FROM entity_employee_information_basic AS emp WHERE\n" +
                "IF(\n" +
                "( STATUS != #{status} ),\n" +
                "( emp.hire_date <= #{startTime} ),\n" +
                "( emp.hire_date <= #{startTime} AND emp.leave_date >= #{endTime} ) \n" +
                ")";
        String sql = SQLBuilder.sql(preSql).setParamter("startTime", nextMonth).setParamter("endTime", time).setParamter("status", EmployeeStatusEnum.QUIT.getStatus()).toString();
        ResultSet resultSet = mySQLProxy.queryBySQL(sql);
        try {
            while (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
             throw new UnifiedException(e);
        }

        return 0;
    }


    @Override
    public Integer totalNumberOfEmployees() {
        ResultSet resultSet = mySQLProxy.queryBySQL("SELECT COUNT(*) AS sumPerson FROM entity_employee_information_basic");
        try {
            while (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        return 0;
    }

    @Override
    public HashMap<String, Integer> numberOfEmployeesGroupByDep() {
        HashMap<String, Integer> hashMapDepInfo = new HashMap<>();
        ResultSet resultSet = mySQLProxy.queryBySQL("SELECT dep.department_name,COUNT(*) AS sumPerson FROM entity_employee_information_basic AS emp  JOIN entity_department_information_basic AS " +
                "dep where emp.department_id = dep.department_id and emp.status!=4 GROUP BY emp.department_id ");
        try {
            while (resultSet.next()) {
                hashMapDepInfo.put(resultSet.getString(1), resultSet.getInt(2));
            }
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        return hashMapDepInfo;
    }

    @Override
    public List<String> listTagsOfNonWorking() {
        List<String> tags = new ArrayList<>();

        String sql = "SELECT tag_name AS tagName FROM tag_property WHERE property ='0'";

        ResultSet resultSet = mySQLProxy.queryBySQL(sql);
        try {
            while (resultSet.next()) {
                tags.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
             throw new UnifiedException(e);
        }
        return tags;
    }

    @Override
    public List<String> listTagsOfWorking() {
        List<String> tags = new ArrayList<>();

        String sql = "SELECT tag_name AS tagName FROM tag_property WHERE property ='1'";

        ResultSet resultSet = mySQLProxy.queryBySQL(sql);
        try {
            while (resultSet.next()) {
                tags.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        return tags;
    }

    @Override
    public String getEmployeeNumberBySourceIP(String sourceIP) {
        String employeeNumber = null;

        String preSql = "SELECT\n" +
                "\temp.number\n" +
                "FROM\n" +
                "\tentity_employee_information_basic emp\n" +
                "JOIN auth_account cou ON cou.employee_id = emp.id\n" +
                "JOIN entity_device_basic dev ON dev.account_id = cou.id\n" +
                "WHERE dev.static_ip = #{sourceIP}";

        String excuteSQL = SQLBuilder.sql(preSql)
                .setParamter("sourceIP", sourceIP)
                .toString();

        ResultSet resultSet = mySQLProxy.queryBySQL(excuteSQL);

        try {
            if (resultSet.next()) {
                employeeNumber = resultSet.getString(1);
            }
        } catch (SQLException e) {
             throw new UnifiedException(e);
        }
        return employeeNumber;
    }

    @Override
    public HashMap<String, String> getAllEmployeeIpAndNumber() {
        HashMap<String, String> hashMapDepInfo = new HashMap<>();
        ResultSet resultSet = mySQLProxy.queryBySQL("SELECT\n" +
                "\tdev.static_ip,\n" +
                "\temp.number\n" +
                "FROM\n" +
                "\tentity_employee_information_basic emp\n" +
                "JOIN auth_account cou ON cou.employee_id = emp.id\n" +
                "JOIN entity_device_basic dev ON dev.account_id = cou.id\n");
        try {
            while (resultSet.next()) {
                hashMapDepInfo.put(resultSet.getString(1), resultSet.getString(2));
            }

        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        return hashMapDepInfo;
    }

    @Override
    public Map<String, String> getAllEmployeeNumberAndName() {
        HashMap<String, String> hashPersonInfo = new HashMap<>();
        ResultSet resultSet = mySQLProxy.queryBySQL("SELECT emp.number, emp.name FROM entity_employee_information_basic emp");
        try {
            while (resultSet.next()) {
                hashPersonInfo.put(resultSet.getString(1), resultSet.getString(2));
            }

        } catch (SQLException e) {

        }
        return hashPersonInfo;
    }

    @Override
    public HashMap<String, String> getJobClass() {
        HashMap<String, String> hashMap = new HashMap<>();
        String sql = "select tag_name,property from tag_property";
        ResultSet resultSet = mySQLProxy.queryBySQL(sql);
        try {
            while (resultSet.next()) {
                hashMap.put(resultSet.getString(1), resultSet.getString(2));
            }
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        return hashMap;
    }


    @Override
    public HashMap<String, ArrayList<String>> getEmployeeIps(String departmentName) {
        HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
        String sql = "SELECT\n" +
                "\tdep.department_name,\n" +
                "\tdev.static_ip\n" +
                "FROM\n" +
                "\tentity_employee_information_basic emp\n" +
                "JOIN auth_account cou ON cou.employee_id = emp.id\n" +
                "JOIN entity_device_basic dev ON dev.account_id = cou.id\n" +
                "INNER JOIN entity_department_information_basic AS dep\n" +
                "WHERE\n" +
                "\temp.department_id = dep.department_id\n" +
                "AND emp. STATUS != 4";
        String excuteSql;
        if (StringUtils.notEmpty(departmentName)) {
            sql += " and dep.department_name LIKE #{departmentName}";
            excuteSql = SQLBuilder.sql(sql).setParamter("departmentName","%" + departmentName + "%").toString();
        } else {
            excuteSql = SQLBuilder.sql(sql).toString();
        }
        ResultSet resultSet = mySQLProxy.queryBySQL(excuteSql);
        try {
            while (resultSet.next()) {
                if (hashMap.get(resultSet.getString(1)) == null) {
                    hashMap.put(resultSet.getString(1), new ArrayList<>(Arrays.asList(resultSet.getString(2))));
                } else {
                    hashMap.get(resultSet.getString(1)).add(resultSet.getString(2));
                }
            }
        } catch (SQLException e) {
             throw new UnifiedException(e);
        }
        return hashMap;
    }

    @Override
    public Map<Long, Object[]> getIpRegionOfCountry() {
        Map<Long, Object[]> countryMap = new TreeMap<>();

        String sql = "SELECT " +
                " cb.name, " +
                " m.start, " +
                " m.end " +
                "FROM " +
                " dict_country_basic cb " +
                " INNER JOIN dict_global_ip_country_map m ON m.country_id = cb.id ";

        ResultSet resultSet = mySQLProxy.queryBySQL(sql);

        try {
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                String start = resultSet.getString(2);
                String end = resultSet.getString(3);

                long key = NumberUtil.transferIp2Number(start);
                long value1 = NumberUtil.transferIp2Number(end);
                countryMap.put(key, new Object[]{value1, name});
            }
        } catch (SQLException e) {
             throw new UnifiedException(e);
        }
        return countryMap;
    }

    @Override
    public Map<String, Object[]> getInfoOfCountry() {
        Map<String, Object[]> result = new HashMap<>();
        String sql = "SELECT " +
                " `name`, " +
                " `nick_name`, " +
                " `capital_geo`  " +
                "FROM " +
                " dict_country_basic";

        ResultSet rs = mySQLProxy.queryBySQL(sql);
        try {
            while (rs.next()) {
                String name = rs.getString(1);
                String nickName = rs.getString(2);
                String capitalGEO = rs.getString(3);
                result.put(name, new Object[]{nickName, capitalGEO});
            }
        } catch (SQLException e) {
             throw new UnifiedException(e);
        }

        return result;
    }

    @Override
    public ArrayList<AppIdentify> getTagInfo() {
        ArrayList<AppIdentify> appIdentifys = new ArrayList<>();
        String sql = "select a.app_name,a.web_url,a.keyword2,a.keyword3,a.basic_class from app_basic as a";
        ResultSet rs = mySQLProxy.queryBySQL(sql);
        try {
            while (rs.next()) {
                AppIdentify appIdentify = new AppIdentify();
                appIdentify.setApp_name(rs.getString(1));
                appIdentify.setWebsite(rs.getString(2));
                appIdentify.setKeyword2(rs.getString(3));
                appIdentify.setKeyword3(rs.getString(4));
                appIdentify.setBasicClass(rs.getString(5));
                appIdentifys.add(appIdentify);
            }
        } catch (java.lang.Exception e) {
             throw new UnifiedException(e);
        }
        return appIdentifys;
    }

    @Override
    public Map<String, Object> getConfigParam() {
        Map<String, Object> result = new HashMap<>();
        String sql = "SELECT\n" +
                "\tcp.param_name AS 'name',\n" +
                "\tcp.param_value AS 'value'\n" +
                "FROM\n" +
                "\tconfig_param cp; ";
        ResultSet rs = mySQLProxy.queryBySQL(sql);
        try {
            while (rs.next()) {
                result.put(rs.getString(1), rs.getString(2));
            }
        } catch (SQLException e) {
             throw new UnifiedException(e);
        }
        return result;
    }

    @Override
    public List<String> getIpsByNo(String no) {
        String preSql = "SELECT static_ip AS ip FROM entity_employee_information_basic emp\n" +
                "JOIN auth_account cou ON cou.employee_id = emp.id\n" +
                "JOIN entity_device_basic dev ON dev.account_id = cou.id WHERE emp.number = #{no}";
        String sql = SQLBuilder.sql(preSql).setParamter("no", no).toString();
        ArrayList<String> arrayListIps = new ArrayList<>();
        ResultSet resultSet = mySQLProxy.queryBySQL(sql);
        try {
            while (resultSet.next()) {
                arrayListIps.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
             throw new UnifiedException(e);
        }

        return arrayListIps;
    }

    @Override
    public String[] getDepInfoByNo(String no) {
        String[] depInfo = new String[2];
        String preSql = "SELECT COUNT(*),d.department_name FROM entity_employee_information_basic e INNER JOIN" +
                " entity_department_information_basic d on e.department_id = d.department_id WHERE e.department_id = " +
                "(SELECT e.department_id FROM entity_employee_information_basic e WHERE e.number = #{no}) AND e.status != #{status}";
        String sql = SQLBuilder.sql(preSql).setParamter("no", no).setParamter("status", EmployeeStatusEnum.QUIT.getStatus()).toString();
        ResultSet resultSet = mySQLProxy.queryBySQL(sql);
        try {
            while (resultSet.next()) {
                depInfo[0] = resultSet.getString(2);
                depInfo[1] = resultSet.getString(1);
                logger.debug(depInfo[0]);
                logger.debug(depInfo[1]);
            }
        } catch (SQLException e) {
             throw new UnifiedException(e);
        }
        return depInfo;
    }

    @Override
    public List<ProfessionalAccomplishmentResult> getProfessionalAccomplishmentResults(String yearMonth, boolean flag) {
        String sql = "";
        if (flag) {
            String preSql = "SELECT l.static_ip,l.time,l.working_attitude,l.loyalty,l.compliance_discipline FROM lib_professional_accomplishment AS l WHERE time <= #{yearMonth} AND time >= #{year}";
            sql = SQLBuilder.sql(preSql).setParamter("yearMonth", yearMonth).setParamter("year", yearMonth.substring(0, 4) + "00").toString();
        } else {
            String preSql = "SELECT l.static_ip,l.time,l.working_attitude,l.loyalty,l.compliance_discipline FROM lib_professional_accomplishment AS l WHERE time = #{yearMonth}";
//            sql = SQLBuilder.sql(preSql).setParamter("yearMonth", DateUtil.getLastMonthIfNow(yearMonth)).toString();
            sql = SQLBuilder.sql(preSql).setParamter("yearMonth", yearMonth).toString();
        }
        ResultSet resultSet = mySQLProxy.queryBySQL(sql);
        List<ProfessionalAccomplishmentResult> professionalAccomplishmentResults = new ArrayList<>();
        try {
            while (resultSet.next()) {
                ProfessionalAccomplishmentResult professionalAccomplishmentResult = new ProfessionalAccomplishmentResult();
                professionalAccomplishmentResult.setStaticIp(resultSet.getString(1));
                professionalAccomplishmentResult.setTime(resultSet.getString(2));
                professionalAccomplishmentResult.setWorkingAttitude(FormulaUtil.calculateComprehensiveScore(new BigDecimal(resultSet.getString(3)),
                        new BigDecimal(resultSet.getString(4)),new BigDecimal(resultSet.getString(5))).toString());
                professionalAccomplishmentResults.add(professionalAccomplishmentResult);
            }
        } catch (SQLException e) {
             throw new UnifiedException(e);
        }
        return professionalAccomplishmentResults;
    }


    @Override
    public List<String> getDepIpsByNo(String no) {
        List<String> listIp = new ArrayList<>();
        String preSql = "SELECT\n" +
                "\tdev.static_ip\n" +
                "FROM\n" +
                "\tentity_employee_information_basic emp\n" +
                "JOIN auth_account cou ON cou.employee_id = emp.id\n" +
                "JOIN entity_device_basic dev ON dev.account_id = cou.id\n" +
                "WHERE\n" +
                "\temp.department_id = (\n" +
                "\tSELECT\n" +
                "\tdepartment_id\n" +
                "\tFROM\n" +
                "\tentity_employee_information_basic\n" +
                "\tWHERE\n" +
                "\t\tnumber = #{no}\n" +
                "\t)";
        String sql = SQLBuilder.sql(preSql).setParamter("no", no).toString();
        ResultSet resultSet = mySQLProxy.queryBySQL(sql);
        try {
            while (resultSet.next()) {
                listIp.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
             throw new UnifiedException(e);
        }
        return listIp;
    }

    @Override
    public String getEmployeeNameByNumber(String number) {
        String name = "";
        String preSql = "SELECT name FROM entity_employee_information_basic e WHERE number = #{number}";
        String sql = SQLBuilder.sql(preSql).setParamter("number", number).toString();
        ResultSet resultSet = mySQLProxy.queryBySQL(sql);
        try {
            while (resultSet.next()) {
                name = resultSet.getString(1);
            }
        } catch (SQLException e) {
             throw new UnifiedException(e);
        }
        return name;
    }

    @Override
    public List<String> getNumbersByDepName(String departmentName) {
        List<String> list = new ArrayList<>();
        String preSql = "SELECT e.number FROM entity_department_information_basic d INNER JOIN entity_employee_information_basic e\n" +
                " ON e.department_id = d.department_id where d.department_name = #{departmentName}";
        String sql = SQLBuilder.sql(preSql).setParamter("departmentName", departmentName).toString();
        ResultSet resultSet = mySQLProxy.queryBySQL(sql);
        try {
            while (resultSet.next()) {
                list.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
             throw new UnifiedException(e);
        }
        return list;
    }

    @Override
    public boolean isExistInLib(ArrayList<String> ips,String month) {
        String preSql = "select * from lib_professional_accomplishment WHERE static_ip in #{ips} AND time = #{month}";
        String sql = SQLBuilder.sql(preSql).setParamter("ips", ips).setParamter("month",month.replace("-","")).toString();
        ResultSet resultSet = mySQLProxy.queryBySQL(sql);
        try {
            while (resultSet.next()) {
               return true;
            }
        } catch (SQLException e) {
             throw new UnifiedException(e);
        }
        return false;
    }




}

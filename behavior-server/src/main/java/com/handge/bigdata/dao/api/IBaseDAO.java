package com.handge.bigdata.dao.api;

import com.handge.bigdata.dao.model.AppIdentify;
import com.handge.bigdata.resource.models.response.monitor.Alarm;
import com.handge.bigdata.resource.models.response.monitor.Illegal;
import com.handge.bigdata.resource.models.response.professional.ProfessionalAccomplishmentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IBaseDAO {
    /**
     * 查询公司实时总人数(在岗)
     *
     * @return
     */
    public Integer totalNumberOfEmployeesOnGuard();

    /**
     * 根据时间点查询公司总人数(在岗)
     *
     * @return
     */
    public Integer totalNumberOfEmployeesOnGuard(String time);

    /**
     * 查询公司总人数(包含离职)
     *
     * @return
     */
    public Integer totalNumberOfEmployees();

    /**
     * 查询各个部门人数(在岗)
     *
     * @return
     */
    public HashMap<String, Integer> numberOfEmployeesGroupByDep();

    /**
     * 获取工作【无关】应用标签列表
     *
     * @return
     */
    public List<String> listTagsOfNonWorking();

    /**
     * 获取工作【有关】应用标签列表
     * @return
     */
    public List<String> listTagsOfWorking();

    /**
     * 根据sourceIP获取职工编号（number）
     */
    public String getEmployeeNumberBySourceIP(String sourceIP);

    /**
     * 获取所有员工ip及对应的职工编号
     */
    public HashMap<String, String> getAllEmployeeIpAndNumber();

    /**
     * 获取所有员工编号和对应的姓名
     *
     * @return
     */
    public Map<String, String> getAllEmployeeNumberAndName();

    /**
     * 查询基础标签与抽象标签
     *
     * @return
     */
    public HashMap<String, String> getJobClass();

    /**
     * 获取部门所有ip 传null获取所有部门员工ip（在岗）
     *
     * @param departmentName 部门名称
     * @return
     */
    public HashMap<String, ArrayList<String>> getEmployeeIps(String departmentName);

    /**
     * 获取国家起始IP为key的映射表
     * 数据结构{startIP: [endIP, countryName]}
     *
     * @return
     */
    Map<Long, Object[]> getIpRegionOfCountry();

    /**
     * 获取国家基本信息的映射表
     * 数据结构{name:[nickName, geo]}
     *
     * @return
     */
    Map<String, Object[]> getInfoOfCountry();

    /**
     * 获取基础标签信息
     *
     * @return
     */
    public ArrayList<AppIdentify> getTagInfo();

    /**
     * 获取配置参数的值
     *
     * @return
     */
    Map<String, Object> getConfigParam();

    /**
     * 通过员工工号查找对应的IP
     */
    public List<String> getIpsByNo(String no);

    /**
     * 通过员工工号查找部门名称及部门人数
     */
    public String[] getDepInfoByNo(String no);

    /**
     * 查询模型结果
     *
     * @param yearMonth
     * @param flag      是否查询其他月数据
     * @return
     */
    public List<ProfessionalAccomplishmentResult> getProfessionalAccomplishmentResults(String yearMonth, boolean flag);

    /**
     * 通过员工工号查找该部门的所有ip
     */
    public List<String> getDepIpsByNo(String no);


    /**
     * 通过员工工号获得员工姓名
     */
    public String getEmployeeNameByNumber(String number);

    /**
     * 通过部门名称获取部门员工工号
     */
    public List<String> getNumbersByDepName(String departmentName);

    /**
     * ip是否存在在模型库中
     * @param ips
     * @return
     */
    public boolean isExistInLib(ArrayList<String> ips,String month);

}

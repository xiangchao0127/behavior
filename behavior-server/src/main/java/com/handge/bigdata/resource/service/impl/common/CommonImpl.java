package com.handge.bigdata.resource.service.impl.common;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.SQLBuilder;
import com.handge.bigdata.dao.proxy.MySQLProxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.resource.models.request.common.IpsByNumberParam;
import com.handge.bigdata.resource.models.request.common.UserInfoByNameParam;
import com.handge.bigdata.resource.models.response.common.UserInfo;
import com.handge.bigdata.resource.service.api.common.ICommon;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DaLu Guo on 2018/5/29.
 */
@Component
public class CommonImpl implements ICommon {

    private static String RESP_SUCCESS = "success";

    MySQLProxy mySQLProxy = ProxyFactory.createProxy(DAOProxyEnum.MySQL);

    @Override
    public Object listUserInfoByName(UserInfoByNameParam userInfoByNameParam) {
        List<UserInfo> userInfoList = new ArrayList<>();
        String sql = "SELECT\n" +
                "\te.`name` AS employeeName,\n" +
                "\tde.department_name AS departmentName,\n" +
                "\te.number\n" +
                "FROM\n" +
                "\tentity_employee_information_basic e \n" +
                "INNER JOIN entity_department_information_basic de ON de.department_id = e.department_id\n" +
                "WHERE\n" +
                " e.`name` LIKE #{user}\n" +
                "OR e.number LIKE #{user}\n"+
                "OR de.department_name LIKE #{user}";
        String excuteSql = SQLBuilder.sql(sql)
                .setParamter("user", "%" + userInfoByNameParam.getName() + "%")
                .toString();
        ResultSet resultSet = mySQLProxy.queryBySQL(excuteSql);
        try {
            while (resultSet.next()) {
                UserInfo userInfo = new UserInfo();
                userInfo.setEmployeeName(resultSet.getString(1));
                userInfo.setDepartment(resultSet.getString(2));
                userInfo.setNumber(resultSet.getString(3));
                userInfoList.add(userInfo);
            }
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        return userInfoList;
    }

    @Override
    public Object getIpsByNumber(IpsByNumberParam ipsByNumberParam) {
        List<String> ipList = new ArrayList<>();
        String sql = "SELECT\n" +
                "\ten.static_ip as ip\n" +
                "FROM\n" +
                "\tentity_employee_information_basic e \n" +
                "INNER JOIN auth_account acct ON acct.employee_id = e.id " +
                "INNER JOIN entity_device_basic en ON acct.id = en.account_id\n" +
                "WHERE\n" +
                " e.number = #{number}";
        String excuteSql = SQLBuilder.sql(sql)
                .setParamter("number", ipsByNumberParam.getNumber())
                .toString();
        ResultSet resultSet = mySQLProxy.queryBySQL(excuteSql);
        try {
            while (resultSet.next()) {
                ipList.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        return ipList;
    }
}

package com.handge.bigdata.resource.service.impl.professional;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.proxy.Proxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.resource.models.request.professional.ProfessionalAccomplishmentByDepartmentManagerDetailParam;
import com.handge.bigdata.resource.models.request.professional.TopOfProfessionalAccomplishmentByDepartmentManagerParam;
import com.handge.bigdata.resource.models.response.professional.ProfessionalAccomplishmentByDepartmentManager;
import com.handge.bigdata.resource.service.api.professional.IGoodOrBad;
import com.handge.bigdata.resource.service.api.professional.IProfessionalAccomplishmentByDepartmentManager;
import com.handge.bigdata.utils.CollectionUtils;
import com.handge.bigdata.utils.PageResults;
import com.handge.bigdata.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by MaJianfu on 2018/6/13.
 */
@Component
public class ProfessionalAccomplishmentByDepartmentManagerImpl implements IProfessionalAccomplishmentByDepartmentManager {
    @Autowired
    private IGoodOrBad iGoodOrBad;
    /**
     * 部门经理职业素养 TOP5
     *
     * @return
     */
    @Override
    public Object listTopOfProfessionalAccomplishmentByDepartmentManager(TopOfProfessionalAccomplishmentByDepartmentManagerParam topOfProfessionalAccomplishmentByDepartmentManagerParam) {
            List<ProfessionalAccomplishmentByDepartmentManager> result = result(topOfProfessionalAccomplishmentByDepartmentManagerParam.getStartTime());
            List<ProfessionalAccomplishmentByDepartmentManager> numlist = numlist(result);
            List<ProfessionalAccomplishmentByDepartmentManager> professionalAccomplishmentByDepartmentManagers = topNumlist(topOfProfessionalAccomplishmentByDepartmentManagerParam.getN(), numlist);
            if(professionalAccomplishmentByDepartmentManagers == null || professionalAccomplishmentByDepartmentManagers.size() == 0){
                List<ProfessionalAccomplishmentByDepartmentManager> resultNew = new ArrayList<>();
                ProfessionalAccomplishmentByDepartmentManager pro=new ProfessionalAccomplishmentByDepartmentManager();
                resultNew.add(pro);
                return resultNew;
            }else{
                return professionalAccomplishmentByDepartmentManagers;
            }
    }
    /**
     * 部门经理职业素养排名
     *
     * @return
     */
    @Override
    public Object listProfessionalAccomplishmentByDepartmentManagerDetail(ProfessionalAccomplishmentByDepartmentManagerDetailParam professionalAccomplishmentByDepartmentManagerDetailParam) {
            List<ProfessionalAccomplishmentByDepartmentManager> result = result(professionalAccomplishmentByDepartmentManagerDetailParam.getStartTime());
            List<ProfessionalAccomplishmentByDepartmentManager> numlist = numlist(result);
            if(numlist == null || numlist.size() == 0){
                List<ProfessionalAccomplishmentByDepartmentManager> resultNew = new ArrayList<>();
                ProfessionalAccomplishmentByDepartmentManager pro=new ProfessionalAccomplishmentByDepartmentManager();
                resultNew.add(pro);
                return resultNew;
            }else{
                PageResults<ProfessionalAccomplishmentByDepartmentManager> pageResult = CollectionUtils.getPageResult(numlist, professionalAccomplishmentByDepartmentManagerDetailParam.getPageNo(), professionalAccomplishmentByDepartmentManagerDetailParam.getPageSize());
                return pageResult;
            }

        }

    public List<ProfessionalAccomplishmentByDepartmentManager> result(String date){
        HashMap<String, String> numScoreMap = numberDep(date);
        Proxy proxyMysql = ProxyFactory.createProxy(DAOProxyEnum.MySQL);
        String sqlName="select department_name,department_header,header_number\n" +
                        "from entity_department_information_basic";
        ResultSet resultSet = (ResultSet) proxyMysql.queryBySQL(sqlName);
        List<ProfessionalAccomplishmentByDepartmentManager> result = new ArrayList<>();
        try {
            while (resultSet.next()){
                ProfessionalAccomplishmentByDepartmentManager pro=new ProfessionalAccomplishmentByDepartmentManager();
                String name=resultSet.getString("department_header");
                String department_name=resultSet.getString("department_name");
                String number=resultSet.getString("header_number");
                String score=numScoreMap.get(number);
                if(StringUtils.notEmpty(score)){
                    pro.setNumber(number);
                    pro.setDepartment(department_name);
                    pro.setName(name);
                    pro.setScore(score);
                    result.add(pro);
                }
            }
            return result;
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
    }

    public HashMap<String,String> numberDep(String date){
        HashMap<String, String> numberMap = iGoodOrBad.scoreStaff(date,null);
        HashMap<String, String> numScoreMap = new HashMap<>();
        for (Map.Entry<String,String> entry :numberMap.entrySet()) {
            numScoreMap.put(entry.getKey().split("\\|")[0],entry.getValue().split("\\|")[3]);
        }
        return numScoreMap;
    }

    public List<ProfessionalAccomplishmentByDepartmentManager> numlist(List<ProfessionalAccomplishmentByDepartmentManager> result){
        List<ProfessionalAccomplishmentByDepartmentManager> resultTotal = new LinkedList<>();
        Collections.sort(result, new Comparator<ProfessionalAccomplishmentByDepartmentManager>() {
            @Override
            public int compare(ProfessionalAccomplishmentByDepartmentManager o1, ProfessionalAccomplishmentByDepartmentManager o2) {
                return -Double.compare(Double.parseDouble(o1.getScore()),
                        Double.parseDouble(o2.getScore()));
            }
        });
        int i=1;
        for(ProfessionalAccomplishmentByDepartmentManager pr:result){
            ProfessionalAccomplishmentByDepartmentManager pro=new ProfessionalAccomplishmentByDepartmentManager();
            pro.setNum(String.valueOf(i));
            pro.setDepartment(pr.getDepartment());
            pro.setNumber(pr.getNumber());
            pro.setName(pr.getName());
            pro.setScore(pr.getScore());
            resultTotal.add(pro);
            i++;
        }
        return resultTotal;
    }

    public List<ProfessionalAccomplishmentByDepartmentManager> topNumlist(int n,List<ProfessionalAccomplishmentByDepartmentManager> resultTotal){
        int i = 0;
        List<ProfessionalAccomplishmentByDepartmentManager> resultTopNum = new LinkedList<>();
        for(ProfessionalAccomplishmentByDepartmentManager mapping:resultTotal){
            ProfessionalAccomplishmentByDepartmentManager profession=new ProfessionalAccomplishmentByDepartmentManager();
            profession.setDepartment(mapping.getDepartment());
            profession.setName(mapping.getName());
            profession.setNumber(mapping.getNumber());
            profession.setScore(mapping.getScore());
            resultTopNum.add(profession);
            i++;
            if (i == n) {
                break;
            }
        }
        return resultTopNum;
    }
}

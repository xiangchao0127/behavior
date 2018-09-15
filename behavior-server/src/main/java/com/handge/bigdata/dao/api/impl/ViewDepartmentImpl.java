package com.handge.bigdata.dao.api.impl;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.api.RepositoryDepartment;
import com.handge.bigdata.dao.api.RepositoryEmployee;
import com.handge.bigdata.dao.api.ViewDepartment;
import com.handge.bigdata.dao.model.TableEntityDepartment;
import com.handge.bigdata.dao.model.TableEntityEmployee;
import com.handge.bigdata.enumeration.ExceptionWrapperEnum;
import com.handge.bigdata.resource.models.request.organization.DeleteDepartmentParam;
import com.handge.bigdata.resource.models.request.organization.InsertDepartmentParam;
import com.handge.bigdata.resource.models.request.organization.ListMemberParam;
import com.handge.bigdata.resource.models.request.organization.UpdateDepartmentParam;
import com.handge.bigdata.resource.models.response.organization.ListDepartment;
import com.handge.bigdata.resource.models.response.organization.ListMember;
import com.handge.bigdata.resource.models.response.organization.MoveEmployee;
import com.handge.bigdata.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Liujuhao
 * @date 2018/6/13.
 */
@Component
public class ViewDepartmentImpl implements ViewDepartment {

    @Autowired
    RepositoryDepartment repositoryDepartment;

    @Autowired
    RepositoryEmployee repositoryEmployee;

    @Override
    public Object selectDepartmentStructure() {
        return null;
    }

    @Override
    public List<ListDepartment> listDepartment() {
        List<ListDepartment> listDepartments = new ArrayList<>();
        Iterable<TableEntityDepartment> tableEntityDepartments = repositoryDepartment.findAll();
        for (TableEntityDepartment tableEntityDepartment : tableEntityDepartments
                ) {
            ListDepartment listDepartment = new ListDepartment();
            //部门id
            listDepartment.setId(String.valueOf(tableEntityDepartment.getId()));
            //部门名称
            listDepartment.setDepartmentName(tableEntityDepartment.getDepartmentName());
            //部门负责人
            listDepartment.setDepartmentHeader(tableEntityDepartment.getDepartmentHeader());
            Set<TableEntityDepartment> higherDepartmentList = tableEntityDepartment.getHigherDepartmentList();
            if (higherDepartmentList.size() == 1) {
                Iterator<TableEntityDepartment> iterator = higherDepartmentList.iterator();
                //上级部门
                listDepartment.setHigherDepartment(iterator.next().getDepartmentName());
            }
            //职能描述
            listDepartment.setDepartmentDesc(tableEntityDepartment.getDescription());
            listDepartments.add(listDepartment);
        }
        return listDepartments;
    }

    @Override
    public void insertDepartment(InsertDepartmentParam insertDepartmentParam) {
        TableEntityDepartment tableEntityDepartment = new TableEntityDepartment();
        Set<TableEntityDepartment> set = new HashSet<>();
        set.add(repositoryDepartment.findByDepartmentName(insertDepartmentParam.getHigherDepartment()));
        //上级部门
        tableEntityDepartment.setHigherDepartmentList(set);
        //部门名称
        tableEntityDepartment.setDepartmentName(insertDepartmentParam.getDepartmentName());
        //部门负责人
        tableEntityDepartment.setDepartmentHeader(insertDepartmentParam.getDepartmentHeader());
        //职能描述
        tableEntityDepartment.setDescription(insertDepartmentParam.getDepartmentDesc());
        //创建时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            tableEntityDepartment.setCreateAt(sdf.parse(insertDepartmentParam.getCreateDateTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        repositoryDepartment.save(tableEntityDepartment);
    }

    @Override
    public void updateDepartment(UpdateDepartmentParam updateDepartmentParam) {
        Optional<TableEntityDepartment> department = repositoryDepartment.findById(Long.valueOf(updateDepartmentParam.getId()));
        TableEntityDepartment tableEntityDepartment = department.get();
        if(StringUtils.notEmpty(updateDepartmentParam.getDepartmentName())) {
            //部门名称
            tableEntityDepartment.setDepartmentName(updateDepartmentParam.getDepartmentName());
        }
        if(StringUtils.notEmpty(updateDepartmentParam.getDepartmentHeader())) {
            //部门负责人
            tableEntityDepartment.setDepartmentHeader(updateDepartmentParam.getDepartmentHeader());
        }
        if(StringUtils.notEmpty(updateDepartmentParam.getDepartmentDesc())) {
            //职能描述
            tableEntityDepartment.setDescription(updateDepartmentParam.getDepartmentDesc());
        }
        repositoryDepartment.save(tableEntityDepartment);
    }

    @Override
    public List<ListMember> listMember(ListMemberParam departmentIdParam) {
        Optional<TableEntityDepartment> department = repositoryDepartment.findById(Long.valueOf(departmentIdParam.getId()));
        TableEntityDepartment tableEntityDepartment = department.get();
        List<ListMember> listMembers = new ArrayList<>();
        Set<TableEntityEmployee> employeeList = tableEntityDepartment.getEmployeeList();
        for (TableEntityEmployee tableEntityEmployee : employeeList) {
            ListMember listMember = new ListMember();
            //姓名
            listMember.setName(tableEntityEmployee.getName());
            //工号
            listMember.setNumber(tableEntityEmployee.getNumber());
            listMembers.add(listMember);
        }
        return listMembers;
    }

    @Override
    public void deleteDepartment(DeleteDepartmentParam deleteDepartmentParam) {
        List<TableEntityDepartment> tableEntityDepartments = new ArrayList<>();
        for (String id : deleteDepartmentParam.getIds()
                ) {
            Optional<TableEntityDepartment> department = repositoryDepartment.findById(Long.valueOf(id));
            //获取该部门员工
            Set<TableEntityEmployee> employeeList = department.get().getEmployeeList();
            //获取下级部门
            Set<TableEntityDepartment> lowerDepartmentList = department.get().getLowerDepartmentList();
            boolean flag = judgeLowerDepartmentMember(lowerDepartmentList);
            if(employeeList.isEmpty() && flag){
                tableEntityDepartments.add(department.get());
            }else{
                throw new UnifiedException(department.get().getDepartmentName(), ExceptionWrapperEnum.Member_Exist_NOT_Delete);
            }
        }
        repositoryDepartment.deleteAll(tableEntityDepartments);
    }

    @Override
    public Object selectDepartmentAndMembers() {
        return repositoryDepartment.findAll();
    }

    @Override
    public void moveEmployee(MoveEmployee moveEmployee) {
        deleteMembers(moveEmployee);
        addMembers(moveEmployee);
    }

    /**
     * 递归判断某部门的所有下级部门是否为空
     * @param tableEntityDepartments
     * @return true:为空 false:不为空
     */
    private boolean judgeLowerDepartmentMember(Set<TableEntityDepartment> tableEntityDepartments){
        boolean flag = false;
        if(tableEntityDepartments.isEmpty()){
            flag=true;
        }else {
            for (TableEntityDepartment t : tableEntityDepartments) {
                Set<TableEntityEmployee> employeeList = t.getEmployeeList();
                if (employeeList.isEmpty()) {
                    flag = t.getLowerDepartmentList().isEmpty() || judgeLowerDepartmentMember(t.getLowerDepartmentList());
                } else {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * 添加成员到部门
     * @param moveEmployee
     */
    private void addMembers(MoveEmployee moveEmployee){
        //获取要添加的成员集合
        Map<String, List<String>> outDepartmentMembers = moveEmployee.getOutDepartmentMembers();
        Set<TableEntityEmployee> tableEntityEmployees = new HashSet<>();
        outDepartmentMembers.values().forEach(o ->{
            o.forEach(id -> {
                tableEntityEmployees.add(repositoryEmployee.findById(Long.valueOf(id)).get());
            });
        });
        //获取要添加成员的部门
        TableEntityDepartment tableEntityDepartment = repositoryDepartment.findById(Long.valueOf(moveEmployee.getInDepartmentId())).get();
        //获取部门已存在成员
        Set<TableEntityEmployee> employeeList = tableEntityDepartment.getEmployeeList();
        //添加新成员
        employeeList.forEach(e -> {
            tableEntityEmployees.add(e);
        });
        tableEntityDepartment.setEmployeeList(tableEntityEmployees);
        repositoryDepartment.save(tableEntityDepartment);
    }

    /**
     * 删除部门成员
     * @param moveEmployee
     */
    private void deleteMembers(MoveEmployee moveEmployee){
        //获取要删除的成员id集合及其部门id
        Map<String, List<String>> outDepartmentMembers = moveEmployee.getOutDepartmentMembers();
        for (Map.Entry<String, List<String>> entry : outDepartmentMembers.entrySet()) {
            //获取要删除成员的部门
            TableEntityDepartment tableEntityDepartment = repositoryDepartment.findById(Long.valueOf(entry.getKey())).get();
            //部门成员
            Iterator<TableEntityEmployee> iterator = tableEntityDepartment.getEmployeeList().iterator();
            Set<TableEntityEmployee> employeeList = new HashSet<>();
            while(iterator.hasNext()){
                if(entry.getValue().contains(String.valueOf(iterator.next().getId()))){
                    iterator.remove();
                }
            }
            while(iterator.hasNext()){
                employeeList.add(iterator.next());
            }
            tableEntityDepartment.setEmployeeList(employeeList);
            repositoryDepartment.save(tableEntityDepartment);
        }
    }
}

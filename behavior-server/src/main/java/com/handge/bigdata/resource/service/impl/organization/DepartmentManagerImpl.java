package com.handge.bigdata.resource.service.impl.organization;

import com.handge.bigdata.dao.api.ViewDepartment;
import com.handge.bigdata.dao.model.TableEntityDepartment;
import com.handge.bigdata.resource.models.request.organization.DeleteDepartmentParam;
import com.handge.bigdata.resource.models.request.organization.InsertDepartmentParam;
import com.handge.bigdata.resource.models.request.organization.ListMemberParam;
import com.handge.bigdata.resource.models.request.organization.UpdateDepartmentParam;
import com.handge.bigdata.resource.models.response.organization.FindDepartmentAndMembers;
import com.handge.bigdata.resource.models.response.organization.ListDepartment;
import com.handge.bigdata.resource.models.response.organization.ListMember;
import com.handge.bigdata.resource.models.response.organization.MoveEmployee;
import com.handge.bigdata.resource.service.api.organization.IDepartmentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Liujuhao
 * @date 2018/6/13.
 */
@Component
public class DepartmentManagerImpl implements IDepartmentManager {

    @Autowired
    ViewDepartment viewDepartment;

    @Override
    public Object showDepartmentStructure() {
        return null;
    }

    @Override
    public List<ListDepartment> listDepartment() {
        return (List<ListDepartment>)viewDepartment.listDepartment();
    }

    @Override
    public Object addDepartment(InsertDepartmentParam insertDepartmentParam) {
        viewDepartment.insertDepartment(insertDepartmentParam);
        return 1;
    }

    @Override
    public Object alterDepartment(UpdateDepartmentParam updateDepartmentParam) {
        viewDepartment.updateDepartment(updateDepartmentParam);
        return 1;
    }

    @Override
    public List<ListMember> listMember(ListMemberParam listMemberParam) {
        List<ListMember> listMembers = (List<ListMember>) viewDepartment.listMember(listMemberParam);
        Collections.sort(listMembers,new Comparator<ListMember>() {

            @Override
            public int compare(ListMember o1, ListMember o2) {
                return Integer.valueOf(o1.getNumber())-Integer.valueOf(o2.getNumber());
            }
        });
        return listMembers;
    }

    @Override
    public Object deleteDepartment(DeleteDepartmentParam deleteDepartmentParam) {
        viewDepartment.deleteDepartment(deleteDepartmentParam);
        return 1;
    }

    @Override
    public List<FindDepartmentAndMembers> findDepartmentAndMembers() {
        List<TableEntityDepartment> tableEntityDepartments = (List<TableEntityDepartment>) viewDepartment.selectDepartmentAndMembers();
        List<FindDepartmentAndMembers> findDepartmentAndMemberParams = new ArrayList<>();
        tableEntityDepartments.forEach(o -> {
           findDepartmentAndMemberParams.add(new FindDepartmentAndMembers(){
               {
                   this.setDepartmentId(String.valueOf(o.getId()));
                   this.setDepartmentName(o.getDepartmentName());
                   Map<String,String> map = new HashMap<>();
                   o.getEmployeeList().forEach(e -> {
                       map.put(String.valueOf(e.getId()),e.getName());
                   });
                   this.setEmployeeIdName(map);
               }
            });
        });
        return findDepartmentAndMemberParams;
    }

    @Override
    public Object moveEmployee(MoveEmployee moveEmployee) {
        viewDepartment.moveEmployee(moveEmployee);
        return 1;
    }

}

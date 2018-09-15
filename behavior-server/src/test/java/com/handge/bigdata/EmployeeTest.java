package com.handge.bigdata;

import com.handge.bigdata.dao.api.ViewDepartment;
import com.handge.bigdata.dao.api.ViewEmployee;
import com.handge.bigdata.dao.model.TableEntityEmployee;
import com.handge.bigdata.resource.models.request.organization.ListEmployeeParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/** 
* ViewEmployeeImpl Tester.
* @author <Authors name> 
* @since <pre>06/14/2018</pre> 
* @version 1.0 
*/
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:application_DontUse.properties"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeTest {
    @Autowired
    ViewEmployee viewEmployee;

    @Autowired
    ViewDepartment viewDepartment;

    @Test
    public void testEmployee() throws Exception {

        //按条件分页查询
        ListEmployeeParam listEmployeeParam = new ListEmployeeParam();
        listEmployeeParam.setDepartmentName("技术");
        Page<TableEntityEmployee> tableEntityEmployees = viewEmployee.pageQuery(listEmployeeParam);
        tableEntityEmployees.forEach(o -> {
            System.out.println(o.toString());
        });

        /*//新增员工
        EmployeeParam employeeParam = new EmployeeParam();
        employeeParam.setEmployeeName("李四");
        employeeParam.setEmployeeNumber("678");
        employeeParam.setStatus("实习");
        employeeParam.setDepartmentName("技术部");
        employeeParam.setRoles(Arrays.asList("开发经理","开发工程师"));
        employeeParam.setPostAge("4");
        employeeParam.setSeniority("1");
        employeeParam.setPosition("大数据工程师");
        employeeParam.setPhone("13585462584");
        employeeParam.setEmail("ls@163.mail");
        employeeParam.setHomeAddress("street 7");
        employeeParam.setCreateTime("2018-03-01 13:45:00");
        viewEmployee.insertEmployee(employeeParam);*/

       /* //修改员工
        EmployeeParam employeeParam = new EmployeeParam();
        employeeParam.setAccountId("1");
        viewEmployee.updateEmployee(employeeParam);*/

        /*//显示员工信息详情
        SelectEmployeeParam selectEmployeeParam = new SelectEmployeeParam();
        selectEmployeeParam.setAccountId("2");
        SelectEmployee selectEmployee = (SelectEmployee) viewEmployee.selectEmployee(selectEmployeeParam);
        System.out.println(selectEmployee);*/

        /*//删除员工
        List<String> list = Arrays.asList("75","76");
        DeleteEmployeeParam deleteEmployeeParam = new DeleteEmployeeParam();
        deleteEmployeeParam.setAccountIds(list);
        viewEmployee.deleteEmployee(deleteEmployeeParam);*/
    }

    @Test
    public void testDepartment() throws Exception{

        /* //部门列表
        List<ListDepartment> listDepartments = (List<ListDepartment>)viewDepartment.listDepartment();
        for (ListDepartment listDepartment:
                listDepartments
             ) {
            System.out.println(listDepartment);
        }*/

        /*//添加部门
        InsertDepartmentParam insertDepartmentParam = new InsertDepartmentParam();
        insertDepartmentParam.setId("18");
        insertDepartmentParam.setDepartmentName("test");
        insertDepartmentParam.setDepartmentHeader("张三");
        insertDepartmentParam.setHigherDepartment("技术部");
        insertDepartmentParam.setDepartmentDesc("测试");
        insertDepartmentParam.setCreateDateTime("2018-06-15 09:44:46");
        viewDepartment.insertDepartment(insertDepartmentParam);*/

        /*//修改部门信息
        UpdateDepartmentParam updateDepartmentParam = new UpdateDepartmentParam();
        updateDepartmentParam.setId("18");
        updateDepartmentParam.setDepartmentName("Test");
        updateDepartmentParam.setDepartmentHeader("张三");
        updateDepartmentParam.setDepartmentDesc("测试测试测试");
        viewDepartment.updateDepartment(updateDepartmentParam);*/

        /*//部门成员列表
        ListMemberParam listMemberParam = new ListMemberParam();
        listMemberParam.setId("3");
        List<ListMember> listMembers = (List<ListMember>)viewDepartment.listMenber(listMemberParam);
        for (ListMember listMember : listMembers
                ) {
            System.out.println(listMember);
        }*/

        /*//删除部门
        DeleteDepartmentParam deleteDepartmentParam = new DeleteDepartmentParam();
        deleteDepartmentParam.setIds(Arrays.asList("18"));
        viewDepartment.deleteDepartment(deleteDepartmentParam);*/

    }

}

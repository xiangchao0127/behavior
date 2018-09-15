package com.handge.bigdata.resource;

import com.handge.bigdata.resource.models.request.organization.*;
import com.handge.bigdata.resource.models.response.organization.MoveEmployee;
import com.handge.bigdata.resource.service.api.organization.IDepartmentManager;
import com.handge.bigdata.resource.service.api.organization.IDeviceManager;
import com.handge.bigdata.resource.service.api.organization.IEmployeeManager;
import com.handge.bigdata.resource.service.api.organization.IRoleManager;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Liujuhao
 * @date 2018/6/20.
 */
@RestController
@RequestMapping(value = "/organization", produces = {"application/json", "application/xml"}, consumes = {"application/json", "application/xml"})
public class OrganizationResource {

    @Autowired
    IRoleManager roleManager;

    @Autowired
    IDepartmentManager departmentManager;

    @Autowired
    IEmployeeManager employeeManager;

    @Autowired
    IDeviceManager deviceManager;

    @RequiresPermissions(value = "")
    @PostMapping("/add_role")
    public ResponseEntity addRole(@Valid @RequestBody RoleParam roleParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(roleManager.addRole(roleParam));
    }

    @RequiresPermissions(value = "")
    @PutMapping("/edit_role")
    public ResponseEntity editRole(@Valid @RequestBody RoleParam roleParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(roleManager.alterRole(roleParam));
    }

    @RequiresPermissions(value = "")
    @DeleteMapping("/delete_role")
    public ResponseEntity deleteRole(@Valid @RequestBody DeleteRoleParam deleteRoleParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(roleManager.deleteRole(deleteRoleParam));
    }

    @RequiresPermissions(value = "")
    @PutMapping("/member_role")
    public ResponseEntity editRoleMember(@Valid @RequestBody RoleMemberParam roleMemberParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(roleManager.alterMember(roleMemberParam));
    }

    @RequiresPermissions(value = "")
    @GetMapping("/search_role")
    public ResponseEntity searchRole(@Valid ListRoleParam listRoleParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(roleManager.listRole(listRoleParam));
    }

    @RequiresPermissions(value = "")
    @GetMapping("/list_member_by_role")
    public ResponseEntity listMemberByRole(@Valid ListMemberByRoleParam listMemberByRoleParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(roleManager.listMemberByRole(listMemberByRoleParam));
    }

    /**
     * 显示部门结构
     * @return
     */
    @RequiresPermissions(value = "")
    @GetMapping("/show_department_structure")
    public ResponseEntity showDepartmentStructure() {
        return ResponseEntity.ok().body(departmentManager.showDepartmentStructure());
    }

    /**
     * 部门列表
     * @return
     */
    @RequiresPermissions(value = "")
    @GetMapping("/list_department")
    public ResponseEntity listDepartment() {
        return ResponseEntity.ok().body(departmentManager.listDepartment());
    }

    /**
     * 新增部门
     * @param insertDepartmentParam
     * @return
     */
    @RequiresPermissions(value = "")
    @PostMapping("/add_department")
    public ResponseEntity addDepartment(@Valid @RequestBody InsertDepartmentParam insertDepartmentParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(departmentManager.addDepartment(insertDepartmentParam));
    }

    /**
     * 编辑部门
     * @param updateDepartmentParam
     * @return
     */
    @RequiresPermissions(value = "")
    @PostMapping("/alter_department")
    public ResponseEntity alterDepartment(@Valid @RequestBody UpdateDepartmentParam updateDepartmentParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(departmentManager.alterDepartment(updateDepartmentParam));
    }

    /**
     * 部门成员列表
     * @param listMemberParam
     * @return
     */
    @RequiresPermissions(value = "")
    @GetMapping("/list_department_member")
    public ResponseEntity listDepartmentMember(@Valid ListMemberParam listMemberParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(departmentManager.listMember(listMemberParam));
    }

    /**
     * 删除部门
     * @param deleteDepartmentParam
     * @return
     */
    @RequiresPermissions(value = "")
    @DeleteMapping("/delete_department")
    public ResponseEntity deleteDepartment(@Valid @RequestBody DeleteDepartmentParam deleteDepartmentParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(departmentManager.deleteDepartment(deleteDepartmentParam));
    }

    /**
     * 获取部门及各部门成员
     * @return
     */
    @RequiresPermissions(value = "")
    @GetMapping("/find_department_and_members")
    public ResponseEntity findDepartmentAndMembers() {
        return ResponseEntity.ok().body(departmentManager.findDepartmentAndMembers());
    }

    @RequiresPermissions(value = "")
    @PostMapping("/move_employee")
    public ResponseEntity moveEmployee(@Valid @RequestBody MoveEmployee moveEmployee, BindingResult bindingResult) {
        return ResponseEntity.ok().body(departmentManager.moveEmployee(moveEmployee));
    }

    /**
     * 员工列表（模糊查询）
     * @param listEmployeeParam
     * @return
     */
    @RequiresPermissions(value = "")
    @GetMapping("/list_employee")
    public ResponseEntity listEmployee(@Valid ListEmployeeParam listEmployeeParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(employeeManager.listEmployee(listEmployeeParam));
    }

    /**
     * 新增员工
     * @param employeeParam
     * @return
     */
    @RequiresPermissions(value = "")
    @PostMapping("/add_employee")
    public ResponseEntity addEmployee(@Valid @RequestBody EmployeeParam employeeParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(employeeManager.addEmployee(employeeParam));
    }

    /**
     * 编辑员工
     * @param employeeParam
     * @return
     */
    @RequiresPermissions(value = "")
    @PostMapping("/alter_employee")
    public ResponseEntity alterEmployee(@Valid @RequestBody EmployeeParam employeeParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(employeeManager.alterEmployee(employeeParam));
    }

    /**
     * 查看员工详情
     * @param selectEmployeeParam
     * @return
     */
    @RequiresPermissions(value = "")
    @GetMapping("/show_employee_detail")
    public ResponseEntity showEmployeeDetail(@Valid SelectEmployeeParam selectEmployeeParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(employeeManager.showEmployeeDetail(selectEmployeeParam));
    }

    /**
     * 删除员工
     * @param deleteEmployeeParam
     * @return
     */
    @RequiresPermissions(value = "")
    @DeleteMapping("/delete_employee")
    public ResponseEntity deleteEmployee(@Valid @RequestBody DeleteEmployeeParam deleteEmployeeParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(employeeManager.deleteEmployee(deleteEmployeeParam));
    }

    /**
     * 设备列表（模糊查询）
     * @param listDeviceParam
     * @param bindingResult
     * @return
     */
    @RequiresPermissions(value = "")
    @GetMapping("/list_device")
    public ResponseEntity listDevice(@Valid ListDeviceParam listDeviceParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(deviceManager.listDevice(listDeviceParam));
    }

    /**
     * 新增设备
     * @param addDeviceParam
     * @param bindingResult
     * @return
     */
    @RequiresPermissions(value = "")
    @PostMapping("/add_device")
    public ResponseEntity addDevice(@Valid @RequestBody AddDeviceParam addDeviceParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(deviceManager.addDevice(addDeviceParam));
    }

    /**
     * 编辑设备
     * @param editDeviceParam
     * @param bindingResult
     * @return
     */
    @RequiresPermissions(value = "")
    @PostMapping("/edit_device")
    public ResponseEntity editDevice(@Valid @RequestBody EditDeviceParam editDeviceParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(deviceManager.editDevice(editDeviceParam));
    }

    /**
     * 删除设备
     * @param deleteDeviceParam
     * @param bindingResult
     * @return
     */
    @RequiresPermissions(value = "")
    @DeleteMapping("/delete_device")
    public ResponseEntity deleteDevice(@Valid @RequestBody DeleteDeviceParam deleteDeviceParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(deviceManager.deleteDevice(deleteDeviceParam));
    }

    /**
     * 根据员工姓名模糊查询获取姓名工号账号id集合
     * @param findNameAndNumberParam
     * @param bindingResult
     * @return
     */
    @RequiresPermissions(value = "")
    @GetMapping("/find_name_and_number")
    public ResponseEntity findNameAndNumber(@Valid FindNameAndNumberParam findNameAndNumberParam, BindingResult bindingResult) {
        return ResponseEntity.ok().body(deviceManager.findNameAndNumber(findNameAndNumberParam));
    }
}

package com.handge.bigdata.dao.api;

import com.handge.bigdata.dao.model.TableEntityDepartment;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Liujuhao
 * @date 2018/6/14.
 */
public interface RepositoryDepartment extends CrudRepository<TableEntityDepartment, Long> {
    /**
     * 根据部门名称查询部门信息
     *
     * @param departmentName 部门名称
     * @return
     */
    TableEntityDepartment findByDepartmentName(String departmentName);

    /**
     * 根据id查询部门信息
     *
     * @param id 部门id
     * @return
     */
    TableEntityDepartment findById(long id);

}

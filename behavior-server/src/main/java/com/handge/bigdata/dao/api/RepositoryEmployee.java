package com.handge.bigdata.dao.api;

import com.handge.bigdata.dao.model.TableEntityEmployee;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Liujuhao
 * @date 2018/6/4.
 */

public interface RepositoryEmployee extends CrudRepository<TableEntityEmployee, Long>, JpaSpecificationExecutor<TableEntityEmployee> {

    TableEntityEmployee findByName(String name);

    @Query(value = "SELECT mrp.permission_id FROM middle_role_permission mrp " +
            "INNER JOIN middle_employee_role mer ON mrp.role_id = mer.role_id " +
            "INNER JOIN entity_employee_information_basic eeib ON eeib.id = mer.employee_id " +
            "WHERE eeib.name = :name", nativeQuery = true)
    List<Long> findPermissionByName(@Param("name") String name);

}

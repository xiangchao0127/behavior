package com.handge.bigdata.dao.api;

import com.handge.bigdata.dao.model.TableAuthAccount;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Liujuhao
 * @date 2018/6/6.
 */
public interface RepositoryAccount extends CrudRepository<TableAuthAccount, Long>, JpaSpecificationExecutor<TableAuthAccount> {

    TableAuthAccount findByUsername(String username);

    @Query(value = "SELECT t2.id as id, t3.name as name " +
            "FROM middle_account_role t1 " +
            "INNER JOIN auth_account t2 ON t1.account_id = t2.id  " +
            "INNER JOIN entity_employee_information_basic t3 ON t2.employee_id = t3.id " +
            "WHERE t1.role_id = :roleId", nativeQuery = true)
    List<Map> findByRole(@Param("roleId") String roleId);

    @Query(value = "SELECT t2.id as id, t3.name as name " +
            "FROM middle_account_role t1 " +
            "INNER JOIN auth_account t2 ON t1.account_id = t2.id  " +
            "INNER JOIN entity_employee_information_basic t3 ON t2.employee_id = t3.id " +
            "WHERE t1.role_id != :roleId", nativeQuery = true)
    List<Map> findByRoleNot(@Param("roleId") String roleId);

    TableAuthAccount findById(long id);
    
}

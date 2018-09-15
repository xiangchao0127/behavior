package com.handge.bigdata.dao.api;

import com.handge.bigdata.dao.model.TableAuthRoleBasic;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Liujuhao
 * @date 2018/6/4.
 */
public interface RepositoryRole extends CrudRepository<TableAuthRoleBasic, Long>, JpaSpecificationExecutor<TableAuthRoleBasic> {

    TableAuthRoleBasic findByRoleName(String roleName);

    void deleteById(Long id);

}

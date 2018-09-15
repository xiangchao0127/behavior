package com.handge.bigdata.dao.api;

import com.handge.bigdata.dao.model.TableAuthPermission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author Liujuhao
 * @date 2018/6/4.
 */
public interface RepositoryPermission extends CrudRepository<TableAuthPermission, Long> {

    @Query(value = "select t from TableAuthPermission t")
    List<TableAuthPermission> getAll();

    @Query(value = "select t.api_id from auth_permission t", nativeQuery = true)
    List<Long> getAllApiId();
}

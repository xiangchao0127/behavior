package com.handge.bigdata.dao.api;

import com.handge.bigdata.dao.model.TableAuthApi;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author Liujuhao
 * @date 2018/6/6.
 */
public interface RepositoryApi extends CrudRepository<TableAuthApi, Long> {

    @Query(value = "select t from TableAuthApi t")
    List<TableAuthApi> getALL();

    @Query(value = "select t.id from TableAuthApi t")
    List<Long> getAllId();
}

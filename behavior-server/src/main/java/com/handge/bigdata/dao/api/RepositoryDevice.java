package com.handge.bigdata.dao.api;

import com.handge.bigdata.dao.model.TableEntityDevice;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * @author liuqian
 * @date 2018/6/21
 * @Description:
 */
public interface RepositoryDevice extends CrudRepository<TableEntityDevice, Long>, JpaSpecificationExecutor<TableEntityDevice> {
    TableEntityDevice findById(long id);
}

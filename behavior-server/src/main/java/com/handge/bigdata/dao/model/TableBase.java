package com.handge.bigdata.dao.model;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author Liujuhao
 * @date 2018/6/21.
 */
@MappedSuperclass
public class TableBase implements Persistable<String> {

    @Id
    @GeneratedValue(generator = "db-uuid")
    @GenericGenerator(name = "db-uuid", strategy = "uuid")
    private String id;

    @Nullable
    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return this.id == null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        TableBase rhs = (TableBase) obj;
        return this.id != null && this.id.equals(rhs.id);
    }
}

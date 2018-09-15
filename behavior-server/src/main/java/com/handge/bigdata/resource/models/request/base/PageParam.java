package com.handge.bigdata.resource.models.request.base;

import javax.validation.constraints.Digits;
import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/6/1.
 */
public class PageParam extends UserContextParam implements Serializable {

    /**
     * 页码
     */
    @Digits(integer = 4, fraction = 0)
    private int pageNo = 1;

    /**
     * 页数
     */
    @Digits(integer = 4, fraction = 0)
    private int pageSize = 10;

    /**
     * 排序规则：ASC=升序，NULL（空值）=降序
     */
    private String order;

    /**
     * 按某字段排序
     */
    private String orderBy = "id";

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public String toString() {
        return "PageParam{" +
                "pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                '}';
    }
}

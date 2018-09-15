package com.handge.bigdata.utils;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Liujuhao
 * @date 2018/6/21.
 */
public class PageUtil {

    /**
     * 从JPA PAGE中获取分页分析并封装为前端所需Model
     * @param results
     * @param page
     * @param <T>
     * @return
     */
    public static <T> PageResults<T> fromJpaPage(List<T> results, Page page) {
        return new PageResults<T>(){
            {
                this.setCurrentPage(page.getNumber());
                this.setNextPageNo(page.getNumber()+1);
                this.setPageSize(page.getSize());
                this.setPageCount(page.getTotalPages());
                this.setTotalCount((int)page.getTotalElements());
                this.setResults(results);
            }
        };
    }
}

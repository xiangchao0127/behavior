package com.handge.bigdata.utils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 集合工具类
 *
 * @param
 * @author XiangChao
 * @date 2018/5/7 16:05
 * @return
 **/
public class CollectionUtils extends org.apache.commons.collections.CollectionUtils {

    /**
     * 根据实体中的某个属性分组
     *
     * @param colls
     * @param gb
     * @param <T>
     * @param <D>
     * @return
     */
    public static final <T extends Comparable<T>, D> Map<T, List<D>> group(Collection<D> colls, GroupBy<T> gb) {
        if (colls == null || colls.isEmpty()) {
            System.out.println("分组集合不能为空!");
            return null;
        }
        if (gb == null) {
            System.out.println("分组依据接口不能为Null!");
            return null;
        }
        Iterator<D> iter = colls.iterator();
        Map<T, List<D>> map = new HashMap<T, List<D>>();
        while (iter.hasNext()) {
            D d = iter.next();
            T t = gb.groupby(d);
            if (map.containsKey(t)) {
                map.get(t).add(d);
            } else {
                List<D> list = new ArrayList<D>();
                list.add(d);
                map.put(t, list);
            }
        }
        return map;
    }

    /**
     * HashMap K V交换位置
     *
     * @param hashMap
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> HashMap<V, K> hashMapInterchange(HashMap<K, V> hashMap) {
        if (hashMap == null) {
            throw new RuntimeException("hashMap为null");
        }
        Set<K> ks = hashMap.keySet();
        HashMap<V, K> hashMapChange = new HashMap<>();
        for (K k : ks) {
            hashMapChange.put(hashMap.get(k), k);
        }
        return hashMapChange;
    }

    /**
     * 集合reduce操作
     *
     * @param collection
     * @param start
     * @param reducer
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T, K> K reduce(Collection<T> collection, K start, Reducer<T, K> reducer) {

        HashMap<String, K> map = new HashMap<String, K>();

        map.put("key", start);

        collection.forEach(new Consumer<T>() {
            @Override
            public void accept(T t) {

                map.put("key", (K) reducer.reducer(map.get("key"), t));
            }
        });

        return map.get("key");
    }

    /**
     * 集合map操作
     *
     * @param collection
     * @param function
     * @return
     */
    public static Collection map(Collection collection, Function function) {
        return (Collection) collection.stream().map(function).collect(Collectors.toList());
    }

    /**
     * Map reduce 操作
     *
     * @param map
     * @param r
     * @param <K>
     * @param <V>
     * @param <R>
     * @return
     */
    public static <K, V, R> Map<K, R> hashMapReduce(Map<K, V> map, R r, Reducer<K, V> reducer) {
        Map<K, R> hashMap = new HashMap<>(16);
        map.forEach((k, v) ->
                hashMap.put(k, (R) reducer.reducer(v, k))
        );
        return hashMap;
    }

    /**
     * 对list分页
     *
     * @param results  要进行分页的list
     * @param pageNo   页码
     * @param pageSize 一页的记录数
     * @author DaLu Guo
     */
    public static PageResults getPageResult(List results, int pageNo, int pageSize) {
        PageResults pageResults = new PageResults();
        pageResults.setCurrentPage(pageNo);
        pageResults.setTotalCount(results.size());
        //页码的数量 = ((总数-1) / 每页数量取整) + 1
        pageResults.setPageCount(((results.size() - 1) / pageSize) + 1);
        pageResults.setNextPageNo(pageNo + 1 < pageResults.getPageCount() ? pageNo + 1 : pageResults.getPageCount());
        pageResults.setPageSize(pageSize);
        int startIndex = pageSize * (pageNo - 1);
        int endIndex = pageSize * pageNo > results.size() ? results.size() : pageNo * pageSize;
        pageResults.setResults(results.subList(startIndex, endIndex));
        return pageResults;
    }

    public interface GroupBy<T> {
        T groupby(Object obj);
    }

    public interface Reducer<T, K> {

        public Object reducer(K sum, T obj);
    }
}

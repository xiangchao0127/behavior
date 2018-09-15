package com.handge.bigdata.dao;

import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.utils.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Liujuhao on 2018/5/2.
 */
public class SQLBuilder {

    /**
     * 预编译匹配预写SQL占位参数的正则
     */
    static Log logger = LogFactory.getLog(SQLBuilder.class);

    static final private Pattern REGEX_IS_PLACE_HOLDER_1 = Pattern.compile("#\\{(\\S*)\\}");

    private String sql;

    private SQLBuilder() {
    }

    private static SQLBuilder init() {
        return new SQLBuilder();
    }

    /**
     * 返回将预写的SQL和参数封装为完整可执行的SQL语句
     * 该API不推荐使用，推荐使用函数式风格接口sql(String preSql)
     *
     * @param [preSql, params]
     * @return java.lang.String
     * @author LiuJihao
     * @date 2018/5/2 16:02
     **/
    @Deprecated
    public static String sql(String preSql, Map<String, Object> params) {
        if (params == null || params.size() == 0) {
            throw new Error("传入参数异常");
        }
        final String[] sql = {preSql};
        String prefix = "#{";
        String pofix = "}";
        listPlaceHolder(preSql).forEach(
                s -> {
                    if (params.containsKey(s)) {
                        Object param = params.get(s);
                        String replaced = object2Str(param);
                        sql[0] = sql[0].replace(prefix + s + pofix, replaced);
                    } else {
                        throw new Error("参数缺失：" + s);
                    }
                });

        return sql[0];
    }

    /**
     * 目前推荐的构建SQL的API
     *
     * @param preSql
     * @return
     */
    public static SQLBuilder sql(String preSql) {
        SQLBuilder builder = init();
        builder.sql = preSql;
        return builder;
    }

    /**
     * 打印出预写SQL语句中的占位参数，异常时用该接口检查是否符合预期
     *
     * @param [sql]
     * @return void
     * @author LiuJihao
     * @date 2018/5/2 16:04
     **/
    public static void printPlaceHolders(String sql) {
        listPlaceHolder(sql).forEach(
                s -> logger.debug("holder: " + s)
        );
    }

    private static List<String> listPlaceHolder(String sql) {
        List<String> holders = new ArrayList();
        Matcher matcher = REGEX_IS_PLACE_HOLDER_1.matcher(sql);
        while (matcher.find()) {
            holders.add(matcher.group(1));
        }
        return holders;
    }

    private static String object2Str(Object o) {
        String str;
        if (o instanceof String) {
            str = str2String((String) o);
        } else if (o instanceof Number) {
            str = o.toString();
        } else if (o instanceof Collection) {
            str = collection2String((Collection) o);
        } else if (o instanceof Date) {
            str = str2String(DateUtil.date2Str((Date) o, DateFormatEnum.SECONDS));
        } else {
            throw new Error("不支持的数据类型：" + o.getClass().getTypeName());
        }
        return str;
    }

    private static String str2String(String str) {
        return "'" + str + "'";
    }

    private static String collection2String(Collection list) {
        final String[] str = {"("};
        if (list instanceof List) {
            list.forEach(
                    s -> {
                        String replaced = object2Str(s);
                        str[0] = str[0] + replaced + ",";
                    }
            );
        } else {
            throw new Error("参数为集合类时，目前只支持List类型");
        }
        str[0] = str[0].substring(0, str[0].length() - 1) + ")";
        return str[0];
    }

    /**
     * 基于sql(String preSql)方法后，调用绑定参数
     *
     * @param param
     * @param value
     * @return
     */
    public SQLBuilder setParamter(String param, Object value) {
        String old = "#{" + param + "}";
        if (this.sql.contains(old)) {
            this.sql = this.sql.replace(old, object2Str(value));
        } else {
            throw new Error("占位参数缺失：" + param);
        }
        return this;
    }

    @Override
    public String toString() {
        Matcher matcher = REGEX_IS_PLACE_HOLDER_1.matcher(this.sql);
        if (matcher.find()) {
            throw new Error("存在未绑定的占位参数：" + matcher.group(0));
        }
        return this.sql.trim();
    }
}

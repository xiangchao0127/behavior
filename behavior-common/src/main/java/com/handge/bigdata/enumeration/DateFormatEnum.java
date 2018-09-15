package com.handge.bigdata.enumeration;

/**
 * Created by DaLu Guo on 2018/5/22.
 */
public enum DateFormatEnum {
    YEAR("yyyy"),
    MONTH("yyyy-MM"),
    MONTHNEW("yyyyMM"),
    DAY("yyyy-MM-dd"),
    HOUR("yyyy-MM-dd HH"),
    MINUTES("yyyy-MM-dd HH:mm"),
    SECONDS("yyyy-MM-dd HH:mm:ss");

    private String format;

    DateFormatEnum(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}

package com.handge.bigdata.enumeration;

/**
 * @author
 * @date
 */
public enum TimeSectionEnum {

    /**
     * 小时
     */
    HOUR_SECTION("h"),
    /**
     * 天
     */
    DAY_SECTION("d"),
    /**
     * 周
     */
    WEEK_SECTION("w"),
    /**
     * 月
     */
    MONTH_SECTION("m");

    private String mark;

    TimeSectionEnum(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }

}

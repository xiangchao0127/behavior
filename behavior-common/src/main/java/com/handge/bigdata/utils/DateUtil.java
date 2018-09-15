package com.handge.bigdata.utils;

/**
 * <p>Title: 时间和日期的工具类</p>
 * <p>Description: DateUtil类包含了标准的时间和日期格式，以及这些格式在字符串及日期之间转换的方法</p>
 * <p>Copyright: Copyright (c) 2018 WuZhou. All Rights Reserved</p>
 * <p>Company: WuZhou.</p>
 *
 * @author 郭大露
 * @version 1.0
 */

import com.handge.bigdata.enumeration.DateFormatEnum;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class DateUtil {
    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");//注意格式化的表达式
    /**
     * 计算是否是季度末
     *
     * @param date eg:2002-01-01
     * @return
     */
    public static boolean isSeasonEnd(String date) {
        int getMonth = Integer.parseInt(date.substring(5, 7));
        boolean sign = false;
        List seasonEnds = Arrays.asList(3, 6, 9, 12);
        if (seasonEnds.contains(getMonth)) {
            sign = true;
        }
        return sign;
    }

    /**
     * 格式化时间
     *
     * @param strDate 字符串日期格式为秒级
     * @param format  指定格式
     * @return newStrDate 指定格式的字符串时间
     * @throws ParseException
     */
    public static String str2Str(String strDate, DateFormatEnum format) throws ParseException {
        String newStrDate = null;
        SimpleDateFormat inSim = new SimpleDateFormat(DateFormatEnum.SECONDS.getFormat());
        SimpleDateFormat outSim = new SimpleDateFormat(format.getFormat());
        newStrDate = outSim.format(inSim.parse(strDate));
        return newStrDate;
    }

    /**
     * 计算从现在开始几天后的时间
     *
     * @param afterDay eg:1
     * @param format   格式
     * @return
     */
    public static String getStrDateFromNow(int afterDay, DateFormatEnum format) {
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat(format.getFormat());
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + afterDay);
        Date date = calendar.getTime();
        return df.format(date);
    }

    /**
     * 得到当前时间，用于文件名，没有特殊字符，使用yyyyMMddHHmmss格式
     *
     * @param afterDay eg:30
     * @return by time
     */
    public static String getNowForFileName(int afterDay) {
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + afterDay);
        Date date = calendar.getTime();
        return df.format(date);
    }

    /**
     * 判断两个时间的间隔
     *
     * @param start
     * @param end
     * @param dateFormat 时间格式（注意：同一时间使用不同的格式（"yyyy-MM-dd" 和 "yyyy-MM-dd HH:mm:ss"） 进行比较，可能会得出不同的结果）
     * @return 相隔的天数
     */
    public static int differentDays(String start, String end, DateFormatEnum dateFormat) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat.getFormat());
        Date startDate = df.parse(start);
        Date endDate = df.parse(end);
        long a = endDate.getTime() - startDate.getTime();
        long b = a / (24 * 60 * 60 * 1000);
        return (int) b;
    }

    /**
     * 形如"2015-12-7T16:00:00.000Z" 转化为中国时间
     * @param dateStr
     * @return yyyy-MM-dd HH:mm:ss
     * @throws Exception
     */
    public static String transformTimeZone(String dateStr){
        dateStr = dateStr.replace("Z", " UTC");//注意是空格+UTC
        Date d = null;
        try {
            d = format.parse(dateStr );
        } catch (ParseException e) {
           e.printStackTrace();
        }
        return date2Str(d,DateFormatEnum.SECONDS);
    }


    /**
     * 日期转指定格式字符串
     *
     * @param date       Date
     * @param dateFormat 格式
     * @return
     */
    public static String date2Str(Date date, DateFormatEnum dateFormat) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat.getFormat());
        String returnValue = df.format(date);
        return (returnValue);
    }

    /**
     * 日期转自定义字符串
     *
     * @param date      Date
     * @param strFormat 格式
     * @return
     */
    public static String date2Str(Date date, String strFormat) {
        SimpleDateFormat df = new SimpleDateFormat(strFormat);
        String returnValue = df.format(date);
        return (returnValue);
    }

    /**
     * 字符串转指定格式的时间
     *
     * @param dateFormat 格式
     * @param strDate    eg:"2017-02-11 12:12:12"
     * @return
     * @throws ParseException
     */
    public static Date str2Date(DateFormatEnum dateFormat, String strDate) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat.getFormat());
        Date date = df.parse(strDate);
        return (date);
    }

    /**
     * 字符串是否符合指定的时间格式
     *
     * @param format  eg:"yyyy-MM-dd HH:mm:ss"
     * @param strDate eg:"2017-02-11 12:12:12"
     * @return
     */
    public static boolean isFollowFormat(String strDate, DateFormatEnum format) {
        SimpleDateFormat df = new SimpleDateFormat(format.getFormat());
        try {
            df.parse(strDate);
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    /**
     * 将字符串数组使用指定的分隔符合并成一个字符串。
     *
     * @param array 字符串数组
     * @param delim 分隔符，为null的时候使用""作为分隔符（即没有分隔符）
     * @return 合并后的字符串
     * @since 0.4
     */
    public static String combineStringArray(String[] array, String delim) {
        int length = array.length - 1;
        if (delim == null) {
            delim = "";
        }
        StringBuffer result = new StringBuffer(length * 8);
        for (int i = 0; i < length; i++) {
            result.append(array[i]);
            result.append(delim);
        }
        result.append(array[length]);
        return result.toString();
    }

    /**
     * 获取周对应数字
     *
     * @param strWeek
     * @return
     */
    public static int getWeekNum(String strWeek) {
        int returnValue = 0;
        if ("Mon".equals(strWeek)) {
            returnValue = 1;
        } else if ("Tue".equals(strWeek)) {
            returnValue = 2;
        } else if ("Wed".equals(strWeek)) {
            returnValue = 3;
        } else if ("Thu".equals(strWeek)) {
            returnValue = 4;
        } else if ("Fri".equals(strWeek)) {
            returnValue = 5;
        } else if ("Sat".equals(strWeek)) {
            returnValue = 6;
        } else if ("Sun".equals(strWeek)) {
            returnValue = 0;
        } else if (strWeek == null) {
            returnValue = 0;
        }
        return returnValue;
    }

    /**
     * 获取日期
     *
     * @param timeType 时间类型，譬如：Calendar.DAY_OF_YEAR
     * @param timenum  时间数字，譬如：-1 昨天，0 今天，1 明天
     * @param format   时间格式，譬如："yyyy-MM-dd HH:mm:ss"
     * @return 字符串
     */
    public static final String getDateFromNow(int timeType, int timenum, DateFormatEnum format) {
        Calendar cld = Calendar.getInstance();
        Date date = null;
        DateFormat df = new SimpleDateFormat(format.getFormat());
        cld.set(timeType, cld.get(timeType) + timenum);
        date = cld.getTime();
        return df.format(date);
    }

    /**
     * 计算当前时间相差指定月份后的的时间
     *
     * @param date       时间格式，譬如："2017-07-02"
     * @param afterMouth 相差的月份 如：1
     * @return 字符串 譬如："2017-08-03"
     */
    public static String getNextMonth(String date, int afterMouth) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(str2Date(DateFormatEnum.MONTH, date));
        calendar.add(Calendar.MONTH, afterMouth);
        Date date1 = calendar.getTime();
        return date2Str(date1, DateFormatEnum.MONTH);
    }

    /**
     * 计算当前时间相差指定月份和天数后的的时间
     *
     * @param date       时间格式，譬如："2017-07-02"
     * @param afterMouth 相差的月份 如：1
     * @param afterDays  相差的天数 如：1
     * @return 字符串 譬如："2017-08-03"
     */
    public static String getNextDate(String date, int afterMouth, int afterDays) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(str2Date(DateFormatEnum.DAY, date));
        calendar.add(Calendar.MONTH, afterMouth);
        calendar.add(Calendar.DAY_OF_MONTH, afterDays);
        Date date1 = calendar.getTime();
        return date2Str(date1, DateFormatEnum.DAY);
    }


    /**
     * 获取当前时间的日期,时间数组, 譬如：Array{"20170707","050505"}
     *
     * @return
     */
    public static String[] getDatetime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmmss");
        String datetime = format.format(new Date());
        String[] datetimes = datetime.split("\\s+");
        return datetimes;
    }

    /**
     * 根据出生日期获取当前的年龄
     * @param birthDay
     * @return
     */
    public static int getAge(Date birthDay) {
        Calendar cal = Calendar.getInstance();

        if (birthDay == null || cal.before(birthDay)) {
            return 0;
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age;
    }

    /**
     * 1.去掉前端传回数据的“-”，int参数填0或任意负数；
     * 2.填充“-”到前端，int参数填6或8，如199306就填6，会变成1993-06；8位长度的同理；
     * 3.8位或更长的格式，也可以传6，那么就只会获得前6位的数据；同理超过8位的也可以传8。
     *
     * @param date
     * @param flag
     * @return
     */
    public static String str2Str(String date, int flag) {

        if (StringUtils.isEmpty(date)) {
            return null;
        }

        try {
            if (flag <= 0) {
                date = date.replace("-", "");
            } else {
                if (flag == 6 || date.length() == 6) {
                    date = date.substring(0, 4) + "-" + date.substring(4, 6);
                } else if (flag == 8 || date.length() == 8) {
                    date = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
                }
            }
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 比较两个日期的前后
     */
    public static boolean compareDate(String date1, String date2) {
        DateFormat df = new SimpleDateFormat(DateFormatEnum.DAY.getFormat());
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = df.parse(date1);
            dt2 = df.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() > dt2.getTime()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSameDate(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.getTime() == date2.getTime();
    }

    /**
     * @param minute
     * @return
     * @author Guo Dalu
     * 获取当前时间前后几分钟的时间戳
     */
    public static Long getTimeByMinute(double minute) {

        Calendar calendar = Calendar.getInstance();
        int second = (int) (minute * 60);
        calendar.add(Calendar.SECOND, second);

        return calendar.getTimeInMillis();

    }

    public static String dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat(DateFormatEnum.DAY.getFormat());
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 时间戳13位转10位
     *
     * @param origin
     * @return long
     * @author XiangChao
     * @date 2018/5/7 11:42
     **/
    public static long timestamp13To10(long origin) {
        return Long.valueOf(String.valueOf(origin).substring(0, 10));
    }

    /**
     * 判断字符串是否为yyyy-MM或者yyyy-MM-dd
     *
     * @param dateStr
     * @return
     */
    public static boolean checkDateStr(String dateStr) {
        return Pattern.compile("\\d{4}-\\d{2}|\\d{4}-\\d{2}-\\d{2}").matcher(dateStr).matches();
    }

    /**
     * 日期时间转时间戳
     *
     * @param date 日期，格式形如"2018-05-01 00:00:00"
     * @return String
     * @author liuqian
     */
    public static long dateToTimeStamp(String date, DateFormatEnum dateFormat) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat.getFormat());
        Date startDate = simpleDateFormat.parse(date);
        long stamp = startDate.getTime();
        return stamp;
    }

    /**
     * 将传入的日期转化成该日凌晨或结束的时间戳
     *
     * @param date 时间 yyyy-MM-dd 00:00:00
     * @return
     * @flag 0 返回yyyy-MM-dd 00:00:00日期<br>
     * 1 返回yyyy-MM-dd 23:59:59日期
     */
    public static long dateToStartOrEndStamp(String date, int flag) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(str2Date(DateFormatEnum.SECONDS, date));
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        //时分秒（毫秒数）
        long millisecond = hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000;
        //凌晨00:00:00
        cal.setTimeInMillis(cal.getTimeInMillis() - millisecond);

        if (flag == 0) {
            return cal.getTime().getTime();
        } else if (flag == 1) {
            //凌晨23:59:59
            cal.setTimeInMillis(cal.getTimeInMillis() + 23 * 60 * 60 * 1000 + 59 * 60 * 1000 + 59 * 1000);
        }
        return cal.getTime().getTime();
    }

    /**
     * 时间戳转指定格式的字符串
     *
     * @param stamp
     * @param format 格式
     * @return
     * @author Guo Dalu
     */
    public static String timeStampToStrDate(long stamp, DateFormatEnum format) {
        Date date = new Date(stamp);
        String dateStr = date2Str(date, format);
        return dateStr;
    }

    /**
     * 根据传入的时间的年份，获取该年对应的起始日期和结束日期
     * 例如：传入"2018-11-20"，返回["2018-01-01", "2018-12-31"]
     *
     * @return
     */
    public static Date[] getStartAndEndByYear(String date) throws ParseException {
        String year = null;
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        year = DateUtil.str2Str(date, DateFormatEnum.YEAR);
        startCal.set(Calendar.YEAR, Integer.parseInt(year));
        startCal.set(Calendar.MONTH, 0);
        startCal.set(Calendar.DATE, 1);
        endCal.set(Calendar.YEAR, Integer.parseInt(year));
        endCal.set(Calendar.MONTH, 11);
        endCal.set(Calendar.DATE, 31);
        return new Date[]{startCal.getTime(), endCal.getTime()};
    }

    /**
     * 获取当前日期时间前几天的日期时间
     *
     * @param n 前1,2,3,4...天
     * @return
     * @author liuqian
     */
    public static String getDateTimeSeveralDaysAgo(int n) {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormatEnum.SECONDS.getFormat());
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -n);
        date = calendar.getTime();
        String format = sdf.format(date);
        return format;
    }

    /**
     * 返回去年的当前时刻
     * @return
     */
    public static long getTimeByLastYear() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(System.currentTimeMillis());
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, -1);
        date = calendar.getTime();
        return date.getTime();
    }

    /**
     * 如果是当月或者大于当月,返回上一个月
     *
     * @param month (yyyyMM)
     * @return (yyyyMM)
     * @author xc
     */
    public static String getLastMonthIfNow(String month) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateFormatEnum.MONTH.getFormat());
        String nowMonth = simpleDateFormat.format(new Date());
        Calendar calendar = Calendar.getInstance();
        if (month.compareTo(nowMonth) >= 0) {
                calendar.setTime(new Date());
                calendar.add(Calendar.MONTH, -1);
                return simpleDateFormat.format(calendar.getTime());

        } else {
            return month;
        }
    }


    /**
     * 计算当前时间距离上几个月的时间
     *
     * @param date       时间格式，譬如："201806"
     * @param Mouth      相差的月份 如：-1
     * @param dateFormat 如：- yyyyMM
     * @return 字符串 譬如："201805"
     * @author MaJianfu
     */
    public static String getPreviousMonth(String date, int Mouth, DateFormatEnum dateFormat) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(str2Date(dateFormat, date));
        calendar.add(Calendar.MONTH, Mouth);
        Date date1 = calendar.getTime();
        return date2Str(date1, dateFormat);
    }

    /**
     * 获取当月的开始时间戳
     *
     * @return
     */
    public static long getMonthStartTimeStamp() throws ParseException {

        String strDate = date2Str(new Date(), DateFormatEnum.MONTH);
        strDate += "-01 00:00:00";
        Date date = null;
        date = str2Date(DateFormatEnum.SECONDS, strDate);
        long day_first = date.getTime();
        return day_first;
    }
}


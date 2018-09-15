package com.handge.bigdata.utils;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by DaLu Guo on 2018/4/28.
 */
public class NumberUtil {

    /**
     * 将小数转换成百分数
     *
     * @param input
     * @return
     * @author guodalu
     */
    public static String decimalToPercentage(double input) {
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(2);
        return percent.format(input);
    }

    /**
     * 把ip地址转化为数字（便于比较大小）
     *
     * @param [ip]
     * @return long
     * @author LiuJihao
     * @date 2018/5/18 13:45
     **/
    public static long transferIp2Number(String ip) {
        String[] ips = ip.split("\\.");

        List<String> collects = Arrays.stream(ips).map(s -> {
            int ok = 3;
            if (s.length() == ok) {
                return s;
            } else {
                return s.length() == 1 ? "00" + s : "0" + s;
            }
        }).collect(Collectors.toList());
        final String[] str = {""};
        collects.forEach(s -> str[0] += s);

        long number = Long.valueOf(str[0]);
        return number;
    }
}

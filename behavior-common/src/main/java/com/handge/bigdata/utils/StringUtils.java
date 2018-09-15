package com.handge.bigdata.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Liujuhao latest edit
 * @date 2018/04/23
 */
public class StringUtils {

    /**
     * 此处对正则表达式预编译，不允许写在方法内！
     */
    static final private Pattern REGEX_IS_NUM = Pattern.compile("[0-9]*");
    static final private Pattern REGEX_IS_DATE = Pattern
            .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
    static final private Pattern REGEX_IS_MOIBLE = Pattern.compile("^[1][0-9]{10}$");
    static final private Pattern REGEX_IS_PHONE_ZONE = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");
    static final private Pattern REGEX_IS_PHONE = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");
    static final private Pattern REGEX_IS_EMAIL = Pattern.compile("^[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?");
    static final private Pattern REGEX_IS_BANKCARD = Pattern.compile("^[0-9]{0,30}$");
    static final private Pattern REGEX_IS_POSTAL = Pattern.compile("^[1-9]\\d{5}$");
    static final private Pattern REGEX_IS_MONEY = Pattern.compile("(^[1-9]([0-9]+)?(\\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\\.[0-9]([0-9])?$)");
    static final private Pattern REGEX_IS_ALL_NUMBER = Pattern.compile("^\\d*$");
    static final private Pattern ALLOWABLE_IP_REGEX = Pattern.compile("(127[.]0[.]0[.]1)|" + "(localhost)|" + "(^10(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){3}$)|" + "(^172\\.([1][6-9]|[2]\\d|3[01])(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}$)|" + "(^192\\.168(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}$)");
    static final private Pattern ALLOWABLE_URL_REGEX = Pattern.compile(".*(\\.jpg|\\.png|\\.gif|\\.js|\\.css).*");

    /**
     * 字符串不为空
     *
     * @param str
     * @return
     */
    public static boolean notEmpty(String str) {
        return str != null && !"".equals(str.trim());
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        int strLenth;
        if (null == str || (strLenth = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLenth; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean notEmpty(String str, boolean allowNull) {

        if (!notEmpty(str)) {
            return allowNull;
        }

        return notEmpty(str);
    }

    /**
     * 判定字符串是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNum(String str) {

        if (str == null) {
            return false;
        }
        Matcher isNum = REGEX_IS_NUM.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isDigits(String str) {

        try {
            Double.parseDouble(str);
        } catch (Exception e) {

            return false;
        }

        return true;
    }

    public static String notNullValue(String s, String defaultvalue) {

        return s == null ? "" : defaultvalue;
    }

    public static boolean isDigits(String str, boolean allowNull) {

        if (!StringUtils.notEmpty(str)) {

            return allowNull;
        }

        try {
            Double.parseDouble(str);
        } catch (Exception e) {

            return false;
        }

        return true;
    }

    public static BigDecimal safeBigDecimal(String string) {

        if (!StringUtils.notEmpty(string) || !StringUtils.isDigits(string)) {
            return new BigDecimal(0);
        }

        return new BigDecimal(string);
    }

    public static boolean safeBoolean(String string) {

        if (!StringUtils.notEmpty(string) || !StringUtils.isDigits(string)) {
            return false;
        }

        return new BigDecimal(string).intValue() == 1;
    }

    public static BigDecimal safeRatioBigDecimal(String string) {

        if (!StringUtils.notEmpty(string) || !StringUtils.isDigits(string)) {
            return new BigDecimal(0);
        }

        return new BigDecimal(string).divide(new BigDecimal("100"), 55, RoundingMode.HALF_UP);
    }

    public static boolean isContainedIn(String string, boolean allowNull, String... types) {

        if (!StringUtils.notEmpty(string)) {

            return allowNull;
        }

        return types == null || Arrays.asList(types).contains(string);
    }

    public static String addPercent(String string) {

        if (string == null) {
            return "%";
        }

        String result = "%";

        for (char a : string.toCharArray()) {

            result += (a + "" + "%");
        }

        return result;
    }

    /**
     * 判断是否为时间字符串
     *
     * @param strDate
     * @return
     */
    public static boolean isDate(String strDate) {
        Matcher m = REGEX_IS_DATE.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static String digitUppercase(double n) {

        String[] fraction = {"角", "分"};
        String[] digit = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        String[][] unit = {{"元", "万", "亿"}, {"", "拾", "佰", "仟"}};

        String head = n < 0 ? "负" : "";
        n = Math.abs(n);

        String s = "";
        for (int i = 0; i < fraction.length; i++) {
            s += (digit[(int) (Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", "");
        }
        if (s.length() < 1) {
            s = "整";
        }
        int integerPart = (int) Math.floor(n);

        for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
            String p = "";
            for (int j = 0; j < unit[1].length && n > 0; j++) {
                p = digit[integerPart % 10] + unit[1][j] + p;
                integerPart = integerPart / 10;
            }
            s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
        }
        return head + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
    }

    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     * @author ：xc
     */
    public static boolean isMobile(final String str) {
        Matcher m;
        boolean b;

        m = REGEX_IS_MOIBLE.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 电话号码验证
     *
     * @param str
     * @return 验证通过返回true
     * @author ：xc
     */
    public static boolean isPhone(final String str) {
        Matcher m;
        boolean b;

        int lengthOfContainsZone = 9;
        if (str.length() > lengthOfContainsZone) {
            m = REGEX_IS_PHONE_ZONE.matcher(str);
            b = m.matches();
        } else {
            m = REGEX_IS_PHONE.matcher(str);
            b = m.matches();
        }
        return b;
    }

    /**
     * 电子邮箱验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isEmail(final String str) {
        Matcher m;
        boolean b;
        if (str != null) {
            m = REGEX_IS_EMAIL.matcher(str);
            b = m.matches();
        } else {
            b = true;
        }
        return b;
    }

    /**
     * 银行卡号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isBankCard(final String str) {
        Matcher m;
        boolean b;
        m = REGEX_IS_BANKCARD.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 邮政编码验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isPostalcode(final String str) {
        Matcher m;
        boolean b;
        m = REGEX_IS_POSTAL.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 金额类验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMoney(final String str) {
        Matcher m;
        boolean b;
        m = REGEX_IS_MONEY.matcher(str);
        b = m.matches();
        return b;
    }

    public static Date[] timeTransform(String startTime, String endTime) {
        Date kssj = null;
        Date jssj = null;
        try {
            if (StringUtils.notEmpty(startTime)) {
                kssj = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(startTime);
            }
            if (StringUtils.notEmpty(endTime)) {
                jssj = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(endTime);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Date[] date = {kssj, jssj};
        return date;
    }

    public static boolean stringEquals(Object obj1, Object obj2) {
        if (obj1 == null && !(obj2 instanceof String)) {
            return obj2 == null;
        }
        if (obj1 == null && obj2 instanceof String) {
            return obj2 == null || "".equals(((String) obj2).trim());
        }
        if (obj1 instanceof Date && obj2 != null && obj2 instanceof Date) {
            return ((Date) obj1).compareTo((Date) obj2) == 0;
        }
        if (obj1 instanceof BigDecimal && obj2 != null && obj2 instanceof BigDecimal) {
            return ((BigDecimal) obj1).compareTo((BigDecimal) obj2) == 0;
        }
        if (obj1 instanceof String && "".equals(((String) obj1).trim())) {
            return obj1.equals(obj2) || obj2 == null;
        }
        return obj1.equals(obj2);
    }

    public static boolean matchRegex(String string, String regex) {

        if (!StringUtils.notEmpty(string)) {
            return true;
        }

        Pattern pattern = Pattern.compile(regex);
        Matcher isMatch = pattern.matcher(string);

        return isMatch.matches();
    }

    /**
     * 全角转半角
     *
     * @param str
     * @return
     */
    public static String toDBC(String str) {
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);
        return returnString;
    }

    /**
     * 验证字符串是否为纯数字
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isAllNum(final String str) {

        Matcher m;
        boolean b;
        m = REGEX_IS_ALL_NUMBER.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 判断是否为内网ip     *
     *
     * @param ip
     * @return
     */
    public static boolean isInnerIp(String ip) {
        return ALLOWABLE_IP_REGEX.matcher(ip).matches();
    }

    /**
     * 获取第一个15位的数字子字符串，匹配不到则返回原字符串
     *
     * @param s
     * @return String
     */
    public static String subStr(String s) {
        String[] strs = s.split("[^\\d]+");
        for (String str : strs) {
            if (str.length() == 15) {
                return str;
            }
        }

        return s;
    }

    /**
     * 月份去零
     * 例如 01--》1  12 --》12
     *
     * @param str
     * @return
     */
    public static String splitO(String str) {
        String removeStr = "0";
        if (str.startsWith(removeStr)) {
            return str.substring(1);
        }
        return str;
    }

    /**
     * 个位数字加零
     *
     * @param i
     * @return
     */
    public static String addO(int i) {
        String appendStr = "0";
        int lengthOfMinStr = 10;
        if (i < lengthOfMinStr) {
            return appendStr + i;
        }
        return i + "";
    }

    /**
     * 判断是否超过阈值
     *
     * @param time
     * @param threshold
     * @return
     */
    public static boolean isOverTime(double time, double threshold) {
        if (time >= threshold) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否包含无用的URL
     *
     * @return
     */
    public static boolean filterUrl(String url) {
        return ALLOWABLE_URL_REGEX.matcher(url).find();
    }
}

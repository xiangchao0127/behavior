package com.handge.bigdata.utils;


import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TopDomainUtil implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(TopDomainUtil.class);
    // 定义正则表达式，域名的根需要自定义，这里不全
    private static final String RE_TOP1 = "[\\w-]+\\.(ac.cn|bj.cn|sh.cn|tj.cn|cq.cn|he.cn|sn.cn|sx.cn|nm.cn|ln.cn|jl.cn|hl.cn|js.cn|zj.cn|ah.cn|fj.cn|jx.cn|sd.cn|ha.cn|hb.cn|hn.cn|gd.cn|gx.cn|hi.cn|sc.cn|gz.cn|yn.cn|gs.cn|qh.cn|nx.cn|xj.cn|tw.cn|hk.cn|mo.cn|xz.cn" +
            "|com.cn|net.cn|org.cn|gov.cn|.com.hk|我爱你|在线|中国|网址|网店|中文网|公司|网络|集团" +
            "|com|cn|cc|org|net|xin|xyz|vip|shop|top|club|wang|fun|info|online|tech|store|site|ltd|ink|biz|group|link|work|pro|mobi|ren|kim|name|tv|red" +
            "|cool|team|live|pub|company|zone|today|video|art|chat|gold|guru|show|life|love|email|fund|city|plus|design|social|center|world|auto|.rip|.ceo|.sale|.hk|.io|.gg|.tm|.gs|.us)";
    private static final String RE_TOP2 = "[\\w-]+\\.[\\w-]+\\.(ac.cn|bj.cn|sh.cn|tj.cn|cq.cn|he.cn|sn.cn|sx.cn|nm.cn|ln.cn|jl.cn|hl.cn|js.cn|zj.cn|ah.cn|fj.cn|jx.cn|sd.cn|ha.cn|hb.cn|hn.cn|gd.cn|gx.cn|hi.cn|sc.cn|gz.cn|yn.cn|gs.cn|qh.cn|nx.cn|xj.cn|tw.cn|hk.cn|mo.cn|xz.cn" +
            "|com.cn|net.cn|org.cn|gov.cn|.com.hk|我爱你|在线|中国|网址|网店|中文网|公司|网络|集团" +
            "|com|cn|cc|org|net|xin|xyz|vip|shop|top|club|wang|fun|info|online|tech|store|site|ltd|ink|biz|group|link|work|pro|mobi|ren|kim|name|tv|red" +
            "|cool|team|live|pub|company|zone|today|video|art|chat|gold|guru|show|life|love|email|fund|city|plus|design|social|center|world|auto|.rip|.ceo|.sale|.hk|.io|.gg|.tm|.gs|.us)";
    private static final String RE_TOP3 = "[\\w-]+\\.[\\w-]+\\.[\\w-]+\\.(ac.cn|bj.cn|sh.cn|tj.cn|cq.cn|he.cn|sn.cn|sx.cn|nm.cn|ln.cn|jl.cn|hl.cn|js.cn|zj.cn|ah.cn|fj.cn|jx.cn|sd.cn|ha.cn|hb.cn|hn.cn|gd.cn|gx.cn|hi.cn|sc.cn|gz.cn|yn.cn|gs.cn|qh.cn|nx.cn|xj.cn|tw.cn|hk.cn|mo.cn|xz.cn" +
            "|com.cn|net.cn|org.cn|gov.cn|.com.hk|我爱你|在线|中国|网址|网店|中文网|公司|网络|集团" +
            "|com|cn|cc|org|net|xin|xyz|vip|shop|top|club|wang|fun|info|online|tech|store|site|ltd|ink|biz|group|link|work|pro|mobi|ren|kim|name|tv|red" +
            "|cool|team|live|pub|company|zone|today|video|art|chat|gold|guru|show|life|love|email|fund|city|plus|design|social|center|world|auto|.rip|.ceo|.sale|.hk|.io|.gg|.tm|.gs|.us)";
    private Pattern pattern;

    // 构造函数
    public TopDomainUtil() {
    }

    public static void main(String[] args) {
        TopDomainUtil obj = new TopDomainUtil();
        // 示例
        String[] res1 = obj.getTopDomain("short.weixin.qq.com");
        for (String ll : res1) {
            System.out.print(" ==> " + ll);
        }
    }

    public String[] getTopDomain(String url) {
        boolean res = true;
        String[] str = new String[3];
        String result = url.replaceAll("(www.)|(http://)|(https://)", "");
        int match = 0;
        try {
            pattern = Pattern.compile(RE_TOP3, Pattern.CASE_INSENSITIVE);
            Matcher matcher = this.pattern.matcher(result);
            matcher.find();
            result = matcher.group();
            match = 3;
        } catch (Exception e) {
            try {
                pattern = Pattern.compile(RE_TOP2, Pattern.CASE_INSENSITIVE);
                Matcher matcher = this.pattern.matcher(result);
                matcher.find();
                result = matcher.group();
                match = 2;
            } catch (Exception e2) {
                try {
                    pattern = Pattern.compile(RE_TOP1, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = this.pattern.matcher(result);
                    matcher.find();
                    result = matcher.group();
                    match = 1;
                } catch (Exception e3) {

                }
            }
        }
        String[] host = result.split("\\.");
        if (match == 3) {
            str[0] = host[2];
            str[1] = host[1];
            str[2] = host[0];
            return str;
        } else if (match == 2) {
            str[0] = host[1];
            str[1] = host[0];
            str[2] = "";
            return str;
        } else if (match == 1) {
            str[0] = host[0];
            str[1] = "";
            str[2] = "";
            return str;
        } else {
            str[0] = "";
            str[1] = "";
            str[2] = "";
            return str;
        }
    }
}
package info.doushen.utils;

/**
 * DateUtil
 *
 * @author huangdou
 * @date 2019/5/11
 */
public class DateUtil {

    public static String formatChDate(String date) {
        if (!StringUtil.isEmpty(date)) {
            return date.replace("年", "-").replace("月", "-").replace("日", "");
        }
        return "";
    }

}

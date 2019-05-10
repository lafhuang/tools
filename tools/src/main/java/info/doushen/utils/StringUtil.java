package info.doushen.utils;

/**
 * StringUtil
 *
 * @author huangdou
 * @date 2019/5/9
 */
public class StringUtil {

    public static boolean isEmpty(String s) {
        if (null == s || "".equals(s.trim())) {
            return true;
        }
        return false;
    }

}

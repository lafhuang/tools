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

    public static boolean equals(String s1, String s2) {
        if (null == s1 || null == s2) {
            return false;
        }
        return s1.equals(s2);
    }

}

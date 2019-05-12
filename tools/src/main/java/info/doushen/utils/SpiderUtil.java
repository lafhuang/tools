package info.doushen.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * SpiderUtil
 *
 * @author huangdou
 * @date 2019/5/11
 */
public class SpiderUtil {

    public static Document getDocument(String xmPath) throws IOException {
        return Jsoup.connect(xmPath)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .timeout(1000 * 30)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
                .header("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .header("accept-encoding","gzip, deflate, br")
                .header("accept-language","zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                .get();
    }

}

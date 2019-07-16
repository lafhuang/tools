package info.doushen.spider.lyrics;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.request.HttpGetRequest;

/**
 * LyricsSpider
 *
 * @author huangdou
 * @date 2019/7/16
 */
public class LyricsSpider {

    public static void main(String[] rags) {
        System.out.println("=======start========");
        HttpGetRequest startUrl = new HttpGetRequest("https://www.azlyrics.com/l/lavigne.html");
        GeccoEngine.create()
                //Gecco搜索的包路径
                .classpath("info.doushen.spider.lyrics")
                //开始抓取的页面地址
                .start(startUrl)
                //开启几个爬虫线程
                .thread(1)
                //单个爬虫每次抓取完一个请求后的间隔时间
                .interval(10000)
                .start();
    }

}

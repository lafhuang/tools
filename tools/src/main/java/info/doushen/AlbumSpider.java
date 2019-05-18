package info.doushen;

import info.doushen.utils.DateUtil;
import info.doushen.utils.SpiderUtil;
import info.doushen.utils.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * AlbumSpider
 *
 * @author huangdou
 * @date 2019/5/9
 */
public class AlbumSpider {

    public static final String XIAMI_COM = "https://emumo.xiami.com";
    public static final String ISSUE_DATE = "发行时间：";

    public static void doSpider(String albumPath, String singerUrl) {
        Set<String> xmUrlSet = new HashSet<>();
        extractXmUrl(xmUrlSet, singerUrl, true);

        generateAlbumFolder(xmUrlSet, albumPath);
    }

    /**
     * 抓取虾米专辑信息
     *
     * @param xmUrlSet
     * @param albumPath
     */
    private static void generateAlbumFolder(Set<String> xmUrlSet, String albumPath) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            for (String url : xmUrlSet) {
                Document albumPage = SpiderUtil.getDocument(url);

                Element span = albumPage.selectFirst("#title span");
                if (null != span) {
                    span.remove();
                }
                Element title = albumPage.selectFirst("#title h1");
                title.text();

                Elements tds = albumPage.select("td.item");
                for (Element td : tds) {
                    if (StringUtil.equals(ISSUE_DATE, td.text())) {
                        Element issueDate = td.parent().selectFirst("td:eq(1)");

                        String path = albumPath + File.separator + DateUtil.formatChDate(issueDate.text()) + " [" + title.text() + "]";

                        /*
                        if (path.indexOf("\\?") >= 0) {
                            path.replace("\\?", "？");
                        }
                         */

                        Path albumDir = Paths.get(path);
                        if (!Files.exists(albumDir)) {
                            try {
                                System.out.println("album==" + albumDir);
                                Files.createDirectory(albumDir);
                            } catch (IOException e) {

                            }
                        }

                        break;
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 抓取歌手录音室专辑url
     *
     * @param albumSet
     * @param url
     * @param first
     */
    public static void extractXmUrl(Set<String> albumSet, String url, boolean first) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Document albumDoc = SpiderUtil.getDocument(url);

            Elements albums = albumDoc.select("p.name");

            for (Element element : albums) {
                Node node = element.childNode(0);
                String albumPage = node.attr("href");
                albumSet.add(XIAMI_COM + albumPage);
            }

            if (first) {
                // 获取分页
                Elements page = albumDoc.select(".p_num");
                for (Element element : page) {
                    if (StringUtil.equals("1", element.text())) {
                        continue;
                    }

                    String pageAlbum = element.attr("href");
                    extractXmUrl(albumSet, XIAMI_COM + pageAlbum, false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        doSpider("/Volumes/Macintosh HD/doudou/music", "https://emumo.xiami.com/artist/album-O9fc383?spm=0.0.0.0.wsQ7uE&p=&d=&c=Cd");
    }

}

package info.doushen;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * LyricSpider
 *
 * @author huangdou
 * @date 2019/3/11
 */
public class LyricSpider {

    public static void doSpider(String dirPath, String singer, String album, String albumUrl) {
        try {
            Document albumDocument = Jsoup.connect(albumUrl)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .timeout(1000 * 30)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
                    .header("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("accept-encoding","gzip, deflate, br")
                    .header("accept-language","zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                    .get();
            
            Elements albumElements = albumDocument.select("a[href]");

            for (Element albumElement : albumElements) {
                String lyricLink = albumElement.attr("href");

                if (lyricLink.startsWith("/lyrics")) {
                    Document lyricDocument = Jsoup.connect("https://www.musixmatch.com" + lyricLink).get();
                    Elements lyricElements = lyricDocument.select(".mxm-lyrics__content");

                    StringBuffer lyric = new StringBuffer();

                    for (Element lyricEmelent : lyricElements) {
                        lyric.append(lyricEmelent.html());
                    }

                    String song = lyricLink.substring(lyricLink.lastIndexOf("/") + 1);

                    if (lyric.length() > 0) {
                        writeLyric(dirPath, singer, album, song, lyric.toString());
                    }
                    Thread.sleep(3000);
                }
            }

        } catch (IOException e) {

        } catch (InterruptedException e) {

        }
    }

    public static void writeLyric(String dirPath, String singer, String album, String song, String lyrics) {

        Path lyricRoot = Paths.get(dirPath);
        if (!Files.exists(lyricRoot)) {
            try {
                Files.createDirectory(lyricRoot);
            } catch (IOException e) {

            }
        }

        Path singerDir = Paths.get(dirPath + "\\" + singer);
        if (!Files.exists(singerDir)) {
            try {
                Files.createDirectory(singerDir);
            } catch (IOException e) {

            }
        }

        Path albumDir = Paths.get(dirPath + "\\" + singer + "\\" + album);
        if (!Files.exists(albumDir)) {
            try {
                Files.createDirectory(albumDir);
            } catch (IOException e) {

            }
        }

        Path lyric = Paths.get(dirPath + "\\" + singer + "\\" + album + "\\" + song + ".txt");

        if(!Files.exists(lyric)) {
            try {
                Files.createFile(lyric);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //创建BufferedWriter
        try {
            BufferedWriter writer = Files.newBufferedWriter(lyric);
            writer.write(lyrics);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

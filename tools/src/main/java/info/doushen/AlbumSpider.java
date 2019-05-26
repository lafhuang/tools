package info.doushen;

import info.doushen.music.Album;
import info.doushen.music.common.MusicConstant;
import info.doushen.utils.DateUtil;
import info.doushen.utils.SpiderUtil;
import info.doushen.utils.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    public static void doSpider(String singerName, String albumPath, String xmUrl, String wyUrl) {
        Set<String> xmUrlSet = new HashSet<>();
        extractXmUrl(xmUrlSet, xmUrl, true);
        List<Album> albumList = extractXmAlbum(xmUrlSet);

        extractWyUrl(wyUrl, true);
        generateAlbumTemplate(singerName, albumList, albumPath);
    }

    /**
     * 抓取网易歌手专辑信息
     *
     * @param url
     * @param first
     */
    private static void extractWyUrl(String url, boolean first) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Document albumDoc = SpiderUtil.getDocument(url);

            Elements albums = albumDoc.select("a.s-fc0");
            Elements elements = albumDoc.select("#m-song-module");
            for (Element album : albums) {
                album.attr("href");
            }

            if (first) {
                albumDoc.select("a.zpgi");
            }

        } catch (IOException e) {

        }
    }

    /**
     * 生成歌手专辑模板
     *
     * @param singerName
     * @param albumList
     * @param albumPath
     */
    private static void generateAlbumTemplate(String singerName, List<Album> albumList, String albumPath) {

        // 模板
        XSSFWorkbook template = new XSSFWorkbook();
        // 歌手页
        XSSFSheet singer = template.createSheet(singerName);
        // 表头
        XSSFRow title = singer.createRow(0);

        // 专辑名	发行日期	语言	类型	风格	封面url
        XSSFCell titleCell = title.createCell(0);
        titleCell.setCellValue(MusicConstant.ALBUM_NAME);
        titleCell = title.createCell(1);
        titleCell.setCellValue(MusicConstant.ISSUE_DATE);
        titleCell = title.createCell(2);
        titleCell.setCellValue(MusicConstant.ALBUM_LANGUAGE);
        titleCell = title.createCell(3);
        titleCell.setCellValue(MusicConstant.ALBUM_TYPE);
        titleCell = title.createCell(4);
        titleCell.setCellValue(MusicConstant.ALBUM_STYLE);
        titleCell = title.createCell(5);
        titleCell.setCellValue(MusicConstant.ALBUM_COVER);

        int index = 1;
        for (Album album : albumList) {
            XSSFRow albumRow = singer.createRow(index);
            XSSFCell albumCell = albumRow.createCell(0);
            albumCell.setCellValue(album.getName());
            albumCell = albumRow.createCell(1);
            albumCell.setCellValue(album.getDate());
            index++;
        }

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(albumPath + File.separator + "专辑.xlsx");
            template.write(out);
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {

                }
            }
        }

    }

    /**
     * 抓取虾米专辑信息
     *
     * @param xmUrlSet
     */
    private static List<Album> extractXmAlbum(Set<String> xmUrlSet) {

        List<Album> albumList = new ArrayList<>();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            return albumList;
        }

        try {
            for (String url : xmUrlSet) {
                Album album = new Album();

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
                        album.setName(title.text());
                        album.setDate(DateUtil.formatChDate(issueDate.text()));
                        break;
                    }
                }
            }
        } catch (IOException e) {
            return albumList;
        }

        return albumList;

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
        // doSpider("陈奕迅", "D:\\", "https://emumo.xiami.com/artist/album-O9fc383?spm=0.0.0.0.wsQ7uE&p=&d=&c=Cd", "https://music.163.com/#/artist/album?id=2116");
        extractWyUrl("https://music.163.com/#/artist/album?id=2116", true);
    }

}

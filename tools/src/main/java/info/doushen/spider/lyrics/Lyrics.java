package info.doushen.spider.lyrics;

import com.geccocrawler.gecco.annotation.Gecco;
import com.geccocrawler.gecco.annotation.HtmlField;
import com.geccocrawler.gecco.annotation.RequestParameter;
import com.geccocrawler.gecco.spider.HtmlBean;
import lombok.Data;

/**
 * Lyrics
 *
 * @author huangdou
 * @date 2019/7/16
 */
@Data
@Gecco(matchUrl = "https://www.azlyrics.com/lyrics/avrillavigne/{song}.html", pipelines = {"consolePipeline", "lyricsPipeline"})
public class Lyrics implements HtmlBean {

    @HtmlField(cssPath = "body > div.container.main-page > div > div.col-xs-12.col-lg-8.text-center > div:nth-child(8)")
    private String lyrics;

    @HtmlField(cssPath = "body > div.container.main-page > div > div.col-xs-12.col-lg-8.text-center > div:nth-child(10)")
    private String bakLyrics;

    @HtmlField(cssPath = "body > div.container.main-page > div > div.col-xs-12.col-lg-8.text-center > div.lyricsh > h2 > b")
    private String artistName;

    @HtmlField(cssPath = "body > div.container.main-page > div > div.col-xs-12.col-lg-8.text-center > div.panel.songlist-panel.noprint > b")
    private String albumName;

    @HtmlField(cssPath = "body > div.container.main-page > div > div.col-xs-12.col-lg-8.text-center > b")
    private String songName;

    @RequestParameter
    private String song;

}

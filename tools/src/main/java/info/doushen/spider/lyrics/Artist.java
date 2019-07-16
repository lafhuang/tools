package info.doushen.spider.lyrics;

import com.geccocrawler.gecco.annotation.Gecco;
import com.geccocrawler.gecco.annotation.HtmlField;
import com.geccocrawler.gecco.annotation.Request;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.spider.HrefBean;
import com.geccocrawler.gecco.spider.HtmlBean;
import lombok.Data;

import java.util.List;

/**
 * Artist
 *
 * @author huangdou
 * @date 2019/7/16
 */
@Data
@Gecco(matchUrl = "https://www.azlyrics.com/l/lavigne.html", pipelines = {"consolePipeline", "artistPipeline"})
public class Artist implements HtmlBean {

    @Request
    private HttpRequest request;

    @HtmlField(cssPath = "#listAlbum > div > b")
    private String album;

    @HtmlField(cssPath = "#listAlbum > a")
    private List<HrefBean> songList;

}

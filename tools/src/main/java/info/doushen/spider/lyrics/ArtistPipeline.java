package info.doushen.spider.lyrics;

import com.geccocrawler.gecco.annotation.PipelineName;
import com.geccocrawler.gecco.pipeline.Pipeline;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.scheduler.SchedulerContext;
import com.geccocrawler.gecco.spider.HrefBean;

import java.util.List;

/**
 * ArtistPipeline
 *
 * @author huangdou
 * @date 2019/7/16
 */
@PipelineName("artistPipeline")
public class ArtistPipeline implements Pipeline<Artist> {

    @Override
    public void process(Artist artist) {
        List<HrefBean> hrefs = artist.getSongList();
        for(HrefBean href : hrefs) {
            String url = href.getUrl();
            HttpRequest currRequest = artist.getRequest();
            SchedulerContext.into(currRequest.subRequest(url));
        }
    }

}

package info.doushen.music;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * Album
 *
 * @author huangdou
 * @date 2019/3/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Album {

    private String name;
    private Date date;

    private List<Song> songList;

}

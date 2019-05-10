package info.doushen.music;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Song
 *
 * @author huangdou
 * @date 2019/3/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Song {

    private String name;
    private int trackNum;
    private String length;
    private String size;

}

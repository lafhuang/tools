package info.doushen.music;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Singer
 *
 * @author huangdou
 * @date 2019/3/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Singer {

    private String name;

    private List<Album> albumList;

}

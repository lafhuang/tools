package info.doushen.spider.lyrics;

import com.geccocrawler.gecco.annotation.PipelineName;
import com.geccocrawler.gecco.pipeline.Pipeline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * LyricsPipeline
 *
 * @author huangdou
 * @date 2019/7/16
 */
@PipelineName("lyricsPipeline")
public class LyricsPipeline implements Pipeline<Lyrics> {

    @Override
    public void process(Lyrics lyrics) {
        String artist = lyrics.getArtistName().replace(" Lyrics", "");
        String album = lyrics.getAlbumName();
        album = album.substring(1, album.length() -1);
        String song = lyrics.getSongName();
        song = song.substring(1, song.length() -1);

        String lyricPath = "D:\\Lyrics";

        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac OS")) {
            lyricPath = "/Volumes/L.A.F./lyrics";
        }

        Path lyricRoot = Paths.get(lyricPath);
        if (!Files.exists(lyricRoot)) {
            try {
                Files.createDirectory(lyricRoot);
            } catch (IOException e) {

            }
        }

        lyricPath += File.separator + artist;
        Path singerDir = Paths.get(lyricPath);
        if (!Files.exists(singerDir)) {
            try {
                Files.createDirectory(singerDir);
            } catch (IOException e) {

            }
        }

        lyricPath += File.separator +  album;
        Path albumDir = Paths.get(lyricPath);
        if (!Files.exists(albumDir)) {
            try {
                Files.createDirectory(albumDir);
            } catch (IOException e) {

            }
        }

        lyricPath += File.separator + song + ".txt";
        Path lyric = Paths.get(lyricPath);
        if(!Files.exists(lyric)) {
            try {
                Files.createFile(lyric);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(lyric);
            writer.write(lyrics.getLyrics().replaceFirst(" ", "").replace("<!-- Usage of azlyrics.com content by any third-party lyrics provider is prohibited by our licensing agreement. Sorry about that. -->", "").replaceAll("<br> ", ""));
            writer.flush();
        } catch (IOException e) {

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {

                }
            }
        }

    }

}

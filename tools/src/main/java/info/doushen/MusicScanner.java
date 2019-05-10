package info.doushen;

import info.doushen.music.Song;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.flac.FlacFileReader;
import org.jaudiotagger.tag.flac.FlacTag;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.*;

/**
 * MusicScanner
 *
 * @author huangdou
 * @date 2019/3/11
 */
public class MusicScanner {

    public static void main(String[] args) throws IOException {
        scanMusic("E:\\MUSIC");
    }

    public static Map<String, Set<String>> scanMusic(String path) throws IOException {
        Path dir = Paths.get(path);
        Map<String, Set<String>> flacMap = new HashMap<>();
        Files.walkFileTree(dir, new FlacVisitor(flacMap));
        return flacMap;
    }

    private static class FlacVisitor extends SimpleFileVisitor<Path> {

        private Map<String, Set<String>> result = new HashMap<>();

        public FlacVisitor(Map<String, Set<String>> result) {
            this.result = result;
        }

        public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) {
            String fileName = filePath.toString();
            if (fileName.endsWith(".m4a")) {

                try {
                    Files.delete(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                FlacFileReader flacFileReader = new FlacFileReader();
//                File audioFile = new File(fileName);
//                AudioFile flacFile = null;
//                try {
//                    flacFile = flacFileReader.read(audioFile);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                FlacTag tag = (FlacTag) flacFile.getTag();
//                String artist = tag.getFirst("ARTIST");
//                String album = tag.getFirst("ALBUM");
//                String song = tag.getFirst("TITLE");
//
//                Set<String> artistList = result.get("ARTIST");
//                if (artistList == null) {
//                    artistList = new HashSet<>();
//                }
//                artistList.add(artist);
//
//                Set<String> albumList = result.get("ALBUM");
//                if (albumList == null) {
//                    albumList = new HashSet<>();
//                }
//                albumList.add(artist + "_" + album);
//
//                Set<String> songList = result.get("SONG");
//                if (songList == null) {
//                    songList = new HashSet<>();
//                }
//                songList.add(artist + "_" + album + "_" + song);
//
//                result.put("ARTIST", artistList);
//                result.put("ALBUM", albumList);
//                result.put("SONG", songList);
            }
            return FileVisitResult.CONTINUE;
        }

    }

    public static Song readFlac(String fileName) throws Exception {
        FlacFileReader flacFileReader = new FlacFileReader();
        File audioFile = new File(fileName);
        AudioFile flacFile = flacFileReader.read(audioFile);
        FlacTag tag = (FlacTag) flacFile.getTag();

        Song song = new Song();
        song.setName(tag.getFirst("TITLE"));
        song.setTrackNum(Integer.parseInt(tag.getFirst("TRACKNUMBER")));
        song.setSize(getSize(audioFile.length()));
        AudioHeader audioHeader = flacFile.getAudioHeader();
        song.setLength(getLength(audioHeader.getTrackLength()));

        return song;
    }

    private static String getLength(int length) {
        return length / 60 + ":" + length % 60;
    }

    private static String getSize(long size) {
        double tmp = (double) size;
        DecimalFormat format = new DecimalFormat("#.0");
        return format.format(tmp / 1024 / 1024);
    }

}

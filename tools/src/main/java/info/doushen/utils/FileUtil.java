package info.doushen.utils;

import com.hankcs.hanlp.HanLP;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.flac.FlacFileReader;
import org.jaudiotagger.audio.flac.FlacFileWriter;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.flac.FlacTag;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

public class FileUtil {

    public static void main(String[] args) throws Exception {

        Path startingDir = Paths.get("D:\\music\\OVER\\蔡健雅");
        List<String> flacFilePathList = new LinkedList<>();
        Files.walkFileTree(startingDir, new FlacVisitor(flacFilePathList));

        convertAndRename(flacFilePathList);

//         printFlacAttr(flacFilePathList);

    }

    private static void convertAndRename(List<String> flacFilePathList) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException, CannotWriteException {
        for (String flacFilePath : flacFilePathList) {
            FlacFileReader flacFileReader = new FlacFileReader();
            File file = new File(flacFilePath);

            String parentPath = file.getParent();

            String albumPath = parentPath.substring(parentPath.lastIndexOf("\\") + 1);

            String artist = parentPath.substring(0, parentPath.lastIndexOf("\\"));
            artist = artist.substring(artist.lastIndexOf("\\") + 1);
            albumPath = parentPath.substring(parentPath.lastIndexOf("\\") + 1);
            String album = albumPath.substring(albumPath.indexOf(" ") + 2, albumPath.length() - 1);
            String date = albumPath.substring(0, albumPath.indexOf(" "));

            int trackTotal = 0;
            File parent = new File(parentPath);

            String[] subFiles = parent.list();
            for (String subFile : subFiles) {
                if (subFile.endsWith("flac")) {
                    trackTotal ++;
                }
            }

            AudioFile flacFile = flacFileReader.read(file);

            FlacTag tag = (FlacTag) flacFile.getTag();

            String title = HanLP.convertToSimplifiedChinese(tag.getFirst("TITLE")).replace("（", "(").replace("）",")");

            tag.setField("VENDOR", "");
            tag.setField("ALBUM", album);
            tag.setField("ALBUMARTIST", artist);
            tag.setField("ARTIST", artist);
            tag.setField("COMMENT", "huangdou flac");
            tag.setField("DATE", date);
            tag.setField("GENRE", "");
            tag.setField("TITLE", title);
            tag.setField("TRACKTOTAL", String.valueOf(trackTotal));

            String trackIdx = tag.getFirst("TRACKNUMBER");
            int trackNum = 0;
            if (trackIdx.indexOf("/") >= 0) {
                trackNum = Integer.parseInt(trackIdx.substring(0, trackIdx.indexOf("/")));
            } else {
                trackNum = Integer.parseInt(trackIdx);
            }

            tag.setField("TRACKNUMBER", trackNum+"");

            flacFile.setTag(tag);

            FlacFileWriter flacFileWriter = new FlacFileWriter();
            flacFileWriter.write(flacFile);

            String simpleFileName = parentPath + "\\" + "[" + (trackNum > 9 ? trackNum : "0" + trackNum) + "] " + title + ".flac";

            File simpleFile = new File(HanLP.convertToSimplifiedChinese(simpleFileName));
            try {
                file.renameTo(simpleFile);
            } catch (Exception e) {
                System.out.println("====" + simpleFileName);
            }

        }

    }

    private static class FlacVisitor extends SimpleFileVisitor<Path> {
        private List<String> result = new LinkedList<>();

        public FlacVisitor(List<String> result) {
            this.result = result;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            String fileName = file.toString();
            if (fileName.endsWith(".flac")) {
                result.add(fileName);
            }
            return FileVisitResult.CONTINUE;
        }
    }

    private static void printFlacAttr(List<String> flacFilePathList) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        for (String flacFilePath : flacFilePathList) {
            FlacFileReader flacFileReader = new FlacFileReader();
            File file = new File(flacFilePath);

            AudioFile flacFile = flacFileReader.read(file);

            FlacTag tag = (FlacTag) flacFile.getTag();

            System.out.println("VENDOR===" + tag.getFirst("VENDOR") + "===");
            System.out.println("ALBUM===" + tag.getFirst("ALBUM") + "===");
            System.out.println("ALBUMARTIST===" + tag.getFirst("ALBUMARTIST") + "===");
            System.out.println("ARTIST===" + tag.getFirst("ARTIST") + "===");
            System.out.println("COMMENT===" + tag.getFirst("COMMENT") + "===");
            System.out.println("DATE===" + tag.getFirst("DATE") + "===");
            System.out.println("GENRE===" + tag.getFirst("GENRE") + "===");
            System.out.println("TITLE===" + tag.getFirst("TITLE") + "===");
            System.out.println("TRACKTOTAL===" + tag.getFirst("TRACKTOTAL") + "===");
            System.out.println("TRACKNUMBER===" + tag.getFirst("TRACKNUMBER") + "===");

            System.out.println("=========================================================");
        }

    }

}

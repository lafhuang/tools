package info.doushen.utils;

import com.hankcs.hanlp.HanLP;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.flac.FlacFileReader;
import org.jaudiotagger.audio.flac.FlacFileWriter;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.flac.FlacTag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileUtil {

    private static final String PRINT = "print";
    private static final String TAG = "tag";
    private static final String CONVERT = "convert";

    static List<String> dsfList = new ArrayList<>();
    static List<String> errorList = new ArrayList<>();

    public static void main(String[] args) {

        String path = "/Volumes/Music/music";
        String exportPath = "/Volumes/Music";

         printAndConvert(path, CONVERT);

//        exportMusic(path, exportPath);
    }

    private static void printAndConvert(String path, String opr) {
        Path startingDir = Paths.get(path);
        List<String> flacFilePathList = new LinkedList<>();
        try {
            Files.walkFileTree(startingDir, new FlacVisitor(flacFilePathList));

            if (CollectionUtils.isNotEmpty(errorList)) {
                System.out.println("===============error start================");
                for (String error : errorList) {
                    System.out.println(error);
                }
                System.out.println("===============error end================");
            }

            if (StringUtil.equals(TAG, opr)) {
                printFlacAttr(flacFilePathList);
            } else if (StringUtil.equals(CONVERT, opr)) {
                convertAndRename(flacFilePathList);

                if (CollectionUtils.isNotEmpty(dsfList)) {
                    System.out.println("===============error start================");
                    for (String dsf : dsfList) {
                        System.out.println(dsf);
                    }
                    System.out.println("===============error end================");
                }
            } if (StringUtil.equals(PRINT, opr)) {
                System.out.println("===============file start================");
                for (String name : flacFilePathList) {
                    System.out.println(name.replace(path, ""));
                }
                System.out.println("===============file end================");
            }

        } catch (Exception e) {

        }

    }

    private static void convertAndRename(List<String> flacFilePathList)  {
        for (String flacFilePath : flacFilePathList) {
            FlacFileReader flacFileReader = new FlacFileReader();
            File file = new File(flacFilePath);

            String parentPath = file.getParent();

            String albumPath = parentPath.substring(parentPath.lastIndexOf(File.separator) + 1);

            String artist = parentPath.substring(0, parentPath.lastIndexOf(File.separator));
            artist = artist.substring(artist.lastIndexOf(File.separator) + 1);
            albumPath = parentPath.substring(parentPath.lastIndexOf(File.separator) + 1);
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

            AudioFile flacFile = null;

            try {
                flacFile = flacFileReader.read(file);
            } catch (Exception e) {
                System.out.println(flacFilePath + "====读取flac失败");
                errorList.add(flacFilePath);
            }

            FlacTag tag = (FlacTag) flacFile.getTag();

            String title = HanLP.convertToSimplifiedChinese(tag.getFirst("TITLE")).replace("（", "(").replace("）",")");
            String trackIdx = tag.getFirst("TRACKNUMBER");
            int trackNum = 0;
            if (trackIdx.indexOf("/") >= 0) {
                trackNum = Integer.parseInt(trackIdx.substring(0, trackIdx.indexOf("/")));
            } else {
                trackNum = Integer.parseInt(trackIdx);
            }

            try {
                tag.setField("VENDOR", "");
                tag.setField("ALBUM", album);
                tag.setField("ALBUMARTIST", artist);
                tag.setField("ARTIST", artist);
                tag.setField("COMMENT", "huangdou flac");
                tag.setField("DATE", date);
                tag.setField("GENRE", tag.getFirst("GENRE"));
                tag.setField("TITLE", title);
                tag.setField("TRACKTOTAL", String.valueOf(trackTotal));
                tag.setField("TRACKNUMBER", trackNum+"");
            } catch (Exception e) {
                System.out.println(flacFilePath + "====设置tag失败");
                errorList.add(flacFilePath);
            }

            Artwork artwork = new Artwork();
            File cover = new File(parentPath + File.separator + "cover.jpg");

            try {
                artwork.setFromFile(cover);
                tag.setField(artwork);
            } catch (Exception e) {
                System.out.println(flacFilePath + "====设置封面失败");
                errorList.add(flacFilePath);
            }

            flacFile.setTag(tag);

            FlacFileWriter flacFileWriter = new FlacFileWriter();
            try {
                flacFileWriter.write(flacFile);
            } catch (Exception e) {
                System.out.println(flacFilePath + "====写入tag失败");
                errorList.add(flacFilePath);
            }

            String simpleFileName = parentPath + File.separator + "[" + (trackNum > 9 ? trackNum : "0" + trackNum) + "] " + title + ".flac";

            File simpleFile = new File(HanLP.convertToSimplifiedChinese(simpleFileName));
            try {
                file.renameTo(simpleFile);
            } catch (Exception e) {
                System.out.println("=======" + simpleFileName);
                errorList.add(flacFilePath);
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
            } else if(fileName.endsWith("dsf")) {
                dsfList.add(fileName);
            }
            return FileVisitResult.CONTINUE;
        }
    }

    private static void printFlacAttr(List<String> flacFilePathList) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        System.out.println("=========================================================");
        for (String flacFilePath : flacFilePathList) {
            FlacFileReader flacFileReader = new FlacFileReader();
            File file = new File(flacFilePath);

            AudioFile flacFile = flacFileReader.read(file);

            FlacTag tag = (FlacTag) flacFile.getTag();

            /*
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
            */

            if (StringUtil.isEmpty(tag.getFirst("TRACKNUMBER"))) {
                System.out.println(flacFilePath);
            }

        }
        System.out.println("=========================================================");

    }

    private static void exportMusic(String path, String exportPath) {
        Path musicPath = Paths.get(path);
        List<String> flacList = new LinkedList<>();
        try {
            Files.walkFileTree(musicPath, new FlacVisitor(flacList));
        } catch (IOException e) {
            e.printStackTrace();
        }

        XSSFWorkbook book = new XSSFWorkbook();
        XSSFSheet flacSheet = book.createSheet("flac");
        XSSFRow head = flacSheet.createRow(0);
        XSSFCell headArtist = head.createCell(0);
        headArtist.setCellValue("歌手");
        XSSFCell headAlbum = head.createCell(1);
        headAlbum.setCellValue("专辑");
        XSSFCell headSong = head.createCell(2);
        headSong.setCellValue("歌曲名");

        for (int idx = 1; idx < flacList.size(); idx++) {
            String flac = flacList.get(idx);

            String[] flacMeta = flac.split(File.separator);
            XSSFRow row = flacSheet.createRow(idx);

            String artistName = flacMeta[flacMeta.length - 3];
            XSSFCell artistCell = row.createCell(0);
            artistCell.setCellValue(artistName);

            String albumName = flacMeta[flacMeta.length - 2];
            if (albumName.contains("[")) {
                albumName = albumName.substring(albumName.indexOf("[") + 1, albumName.indexOf("]"));
            }
            XSSFCell albumCell = row.createCell(1);
            albumCell.setCellValue(albumName);

            String songName = flacMeta[flacMeta.length - 1];
            XSSFCell songCell = row.createCell(2);
            songCell.setCellValue(songName);

            if (songName.contains("[")) {
                boolean flg = false;
                if (songName.contains("[]")) {
                    flg = true;
                } else {
                    String track = songName.substring(songName.indexOf("[") + 1, songName.indexOf("]"));
                    if (songName.replace("["+track+"]", "").contains(track)) {
                        flg = true;
                    }
                }

                if (flg) {
                    XSSFCell cell = row.createCell(3);
                    cell.setCellValue(songName);
                }
            }

        }

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(exportPath + File.separator + "flac.xlsx");
            book.write(out);
        } catch (Exception e) {

        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {

                }
            }
        }

    }

}

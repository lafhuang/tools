package info.doushen.utils;

import com.hankcs.hanlp.HanLP;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * @author huangdou
 */
public class FileUtil {

    private static final String PRINT = "print";
    private static final String TAG = "tag";
    private static final String CONVERT = "convert";
    private static final String FLAC = "flac";
    private static final String FLAC_UP = "FLAC";
    private static final String DSF = "dsf";
    private static final String DSF_UP = "dsf";

    public static void main(String[] args) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException, CannotWriteException {

        String path = "E:\\music";
//        String exportPath = "D:\\";

        printAndConvert(path, CONVERT);

//        exportMusic(path, exportPath);
    }

    /**
     * 转换flac
     *
     * @param path
     * @param opr
     * @throws IOException
     * @throws ReadOnlyFileException
     * @throws TagException
     * @throws InvalidAudioFrameException
     * @throws CannotReadException
     * @throws CannotWriteException
     */
    private static void printAndConvert(String path, String opr) throws IOException, ReadOnlyFileException, TagException, InvalidAudioFrameException, CannotReadException, CannotWriteException {
        Path startingDir = Paths.get(path);
        List<String> flacFilePathList = new LinkedList<>();
        Files.walkFileTree(startingDir, new FlacVisitor(flacFilePathList));

        if (StringUtil.equals(TAG, opr)) {
            printFlacAttr(flacFilePathList);
        } else if (StringUtil.equals(CONVERT, opr)) {
            convertAndRename(flacFilePathList);
        } if (StringUtil.equals(PRINT, opr)) {
            System.out.println("===============file start================");
            for (String name : flacFilePathList) {
                System.out.println(name.replace(path, ""));
            }
            System.out.println("===============file end================");
        }
    }

    private static void convertAndRename(List<String> flacFilePathList) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException, CannotWriteException {
        for (String flacFilePath : flacFilePathList) {
            FlacFileReader flacFileReader = new FlacFileReader();
            File file = new File(flacFilePath);

            String parent = file.getParent();

            String artist = parent.substring(0, parent.lastIndexOf(File.separator));
            artist = artist.substring(artist.lastIndexOf(File.separator) + 1);

            String album = parent.substring(parent.lastIndexOf(File.separator) + 1);

            String date = album.substring(0, album.indexOf(" "));
            album = album.substring(album.indexOf(" ") + 2, album.length() - 1);

            int trackTotal = 0;
            File parentFolder = new File(parent);

            String[] subFiles = parentFolder.list();
            assert subFiles != null;
            for (String subFile : subFiles) {
                if (subFile.endsWith(FLAC)) {
                    trackTotal ++;
                }
            }

            AudioFile flacFile = flacFileReader.read(file);

            FlacTag tag = (FlacTag) flacFile.getTag();

            String title = HanLP.convertToSimplifiedChinese(tag.getFirst("TITLE")).replace("（", "(").replace("）",")");
            String trackIdx = tag.getFirst("TRACKNUMBER");
            int trackNum;
            if (trackIdx.contains("/")) {
                trackNum = Integer.parseInt(trackIdx.substring(0, trackIdx.indexOf("/")));
            } else {
                trackNum = Integer.parseInt(trackIdx);
            }

            tag.setField("VENDOR", "");
            tag.setField("ALBUM", album);
            tag.setField("ALBUMARTIST", artist);
            tag.setField("ARTIST", artist);
            tag.setField("COMMENT", "huangdou flac");
            tag.setField("DATE", date);
            tag.setField("GENRE", tag.getFirst("GENRE"));
            tag.setField("TITLE", title);
            tag.setField("TRACKTOTAL", String.valueOf(trackTotal));
            tag.setField("TRACKNUMBER", String.valueOf(trackNum));

            Artwork artwork = new Artwork();
            File cover = new File(parent + File.separator + "cover.jpg");

            artwork.setFromFile(cover);
            tag.setField(artwork);

            flacFile.setTag(tag);

            FlacFileWriter flacFileWriter = new FlacFileWriter();
            flacFileWriter.write(flacFile);

            String simpleFileName = parent + File.separator + "[" + (trackNum > 9 ? trackNum : "0" + trackNum) + "] " + title + ".flac";

            File simpleFile = new File(HanLP.convertToSimplifiedChinese(simpleFileName));
            boolean rename = file.renameTo(simpleFile);
            System.out.println(flacFilePath + "===重命名===" + rename);
        }

    }

    private static class FlacVisitor extends SimpleFileVisitor<Path> {
        private List<String> result;

        FlacVisitor(List<String> result) {
            this.result = result;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            String fileName = file.toString();
            if (fileName.endsWith(FLAC) || fileName.endsWith(FLAC_UP)) {
                result.add(fileName);
            }

            /*
            if (fileName.endsWith(DSF) || fileName.endsWith(DSF_UP)) {
                result.add(fileName);
            }

             */
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

    private static void exportMusic(String path, String exportPath) throws IOException {
        Path musicPath = Paths.get(path);
        List<String> flacList = new LinkedList<>();
        Files.walkFileTree(musicPath, new FlacVisitor(flacList));

        XSSFWorkbook book = new XSSFWorkbook();
        XSSFSheet flacSheet = book.createSheet("flac");
        XSSFRow head = flacSheet.createRow(0);
        XSSFCell headArtist = head.createCell(0);
        headArtist.setCellValue("歌手");
        XSSFCell headAlbum = head.createCell(1);
        headAlbum.setCellValue("专辑");
        XSSFCell headSong = head.createCell(2);
        headSong.setCellValue("歌曲名");
        XSSFCell unStand = head.createCell(3);
        unStand.setCellValue("非标准歌曲名");

        Properties props = System.getProperties();
        String osName = props.getProperty("os.name");

        System.out.println(osName);

        String operate = File.separator;
        if (osName.contains("Windows")) {
            operate = "\\\\";
        }

        for (int idx = 0; idx < flacList.size(); idx++) {
            String flac = flacList.get(idx);
            String[] flacMeta = flac.split(operate);
            XSSFRow row = flacSheet.createRow(idx+1);

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

        FileOutputStream out = new FileOutputStream(exportPath + File.separator + "flac.xlsx");
        book.write(out);
        out.close();

    }

}

package info.doushen;

import info.doushen.utils.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * ToolsFrame
 *
 * @Author: huangdou
 * @Date: 2019-03-12
 */
public class ToolsFrame extends JFrame {

    public static final Font TOOLS_FONT = new Font(null, Font.PLAIN, 20);
    public static final String SINGER_ALBUM = "歌手专辑";
    public static final String LYRIC_DOWNLOAD = "Lyric下载";

    private Map<String, Set<String>> flacMap;

    JFrame mainFrame;
    JTabbedPane tabPane;

    JPanel singerPanel;
    JLabel xmSingerLabel = new JLabel("虾米专辑页：");
    JTextField xmSingerText = new JTextField();
    JLabel qqSingerLabel = new JLabel("QQ音乐：");
    JTextField qqSingerText = new JTextField();
    JLabel wySingerLabel = new JLabel("网易音乐：");
    JTextField wySingerText = new JTextField();
    JLabel albumSaveLabel = new JLabel("保存地址：");
    JTextField albumPathText = new JTextField();
    JButton albumBrowseBtn = new JButton("浏览");
    JButton singerFetchBtn = new JButton("抓取");

    JPanel scanPanel;
    JTextField musicPath = new JTextField();
    JButton musicBrowseBtn = new JButton("浏览");
    JButton musicAnalysisBtn = new JButton("分析");
    JButton musicExportBtn = new JButton("导出");

    JPanel lyricPanel;
    JLabel singerLabel = new JLabel("歌手：");
    JTextField singerText = new JTextField();
    JLabel urlLabel = new JLabel("Lyric网址：");
    JTextField urlText = new JTextField();
    JLabel lyricPathLabel = new JLabel("下载目录：");
    JTextField lyricPathText = new JTextField();
    JButton lyricBrowseBtn = new JButton("浏览");
    JButton downloadBtn = new JButton("下载");

    public void showFrame() {
        mainFrame = new JFrame("豆豆小工具");
        mainFrame.setSize(800, 300);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        initTab();
        mainFrame.add(tabPane);
        mainFrame.setVisible(true);
    }

    private void initTab() {
        tabPane = new JTabbedPane();
        createSingerPanel();
        createScannerPanel();
        createLyricPanel();
    }

    private void createSingerPanel() {
        singerPanel = new JPanel();
        singerPanel.setLayout(null);

        xmSingerLabel.setFont(TOOLS_FONT);
        xmSingerLabel.setBounds(10, 20, 120, 35);
        xmSingerText.setFont(TOOLS_FONT);
        xmSingerText.setBounds(140, 20, 350, 35);

        qqSingerLabel.setFont(TOOLS_FONT);
        qqSingerLabel.setBounds(10, 60, 120, 35);
        qqSingerText.setFont(TOOLS_FONT);
        qqSingerText.setBounds(140, 60, 350, 35);

        wySingerLabel.setFont(TOOLS_FONT);
        wySingerLabel.setBounds(10, 100, 120, 35);
        wySingerText.setFont(TOOLS_FONT);
        wySingerText.setBounds(140, 100, 350, 35);

        albumSaveLabel.setFont(TOOLS_FONT);
        albumSaveLabel.setBounds(10, 140, 120, 35);
        albumPathText.setFont(TOOLS_FONT);
        albumPathText.setBounds(140, 140, 350, 35);
        albumBrowseBtn.setFont(TOOLS_FONT);
        albumBrowseBtn.setBounds(500, 140, 80, 35);

        albumBrowseBtn.addActionListener(event -> {
            File file = openChoseWindow(JFileChooser.DIRECTORIES_ONLY);
            if (file == null) {
                return;
            }

            albumPathText.setText(file.getPath());
        });

        singerFetchBtn.setFont(TOOLS_FONT);
        singerFetchBtn.setBounds(590, 140, 80, 35);

        singerFetchBtn.addActionListener(event -> {
            String xmPath = xmSingerText.getText();
            if (StringUtil.isEmpty(xmPath)) {
                popWindow("请填写歌手虾米专辑页", SINGER_ALBUM, JOptionPane.WARNING_MESSAGE);
                return;
            }

            String qqPath = qqSingerText.getText();
            if (StringUtil.isEmpty(qqPath)) {
                popWindow("请填写歌手QQ音乐主页", SINGER_ALBUM, JOptionPane.WARNING_MESSAGE);
                return;
            }

            String albumPath = albumPathText.getText();
            if (StringUtil.isEmpty(albumPath)) {
                popWindow("请选择下载路径", SINGER_ALBUM, JOptionPane.WARNING_MESSAGE);
                return;
            }
        });

        singerPanel.add(xmSingerLabel);
        singerPanel.add(xmSingerText);
        singerPanel.add(qqSingerLabel);
        singerPanel.add(qqSingerText);
        singerPanel.add(wySingerLabel);
        singerPanel.add(wySingerText);

        singerPanel.add(albumSaveLabel);
        singerPanel.add(albumPathText);

        singerPanel.add(albumBrowseBtn);
        singerPanel.add(singerFetchBtn);

        tabPane.add(SINGER_ALBUM, singerPanel);
    }

    private void createLyricPanel() {
        lyricPanel = new JPanel();
        lyricPanel.setLayout(null);

        singerLabel.setFont(TOOLS_FONT);
        singerLabel.setBounds(10, 20, 120, 35);
        singerText.setFont(TOOLS_FONT);
        singerText.setBounds(140, 20, 350, 35);

        urlLabel.setFont(TOOLS_FONT);
        urlLabel.setBounds(10, 60, 120, 35);
        urlText.setFont(TOOLS_FONT);
        urlText.setBounds(140, 60, 350, 35);

        lyricPathLabel.setFont(TOOLS_FONT);
        lyricPathLabel.setBounds(10, 100, 120, 35);
        lyricPathText.setFont(TOOLS_FONT);
        lyricPathText.setBounds(140, 100, 350, 35);

        lyricBrowseBtn.setFont(TOOLS_FONT);
        lyricBrowseBtn.setBounds(500, 100, 80, 35);

        lyricBrowseBtn.addActionListener(e -> {
            File file = openChoseWindow(JFileChooser.DIRECTORIES_ONLY);
            if (file == null) {
                return;
            }

            lyricPathText.setText(file.getPath());
        });

        downloadBtn.setFont(TOOLS_FONT);
        downloadBtn.setBounds(590, 100, 80, 35);

        downloadBtn.addActionListener(e -> {
            String singer = singerText.getText();
            if (StringUtil.isEmpty(singer)) {
                popWindow("请填写歌手", LYRIC_DOWNLOAD, JOptionPane.WARNING_MESSAGE);
                return;
            }

            String lyricUrl = urlText.getText();
            if (StringUtil.isEmpty(lyricUrl)) {
                popWindow("请填写歌手专辑歌词地址", LYRIC_DOWNLOAD, JOptionPane.WARNING_MESSAGE);
                return;
            }

            String lyricPath = lyricPathText.getText();
            /*if (StringUtil.isEmpty(lyricPath)) {
                popWindow("请选择歌词下载路径", LYRIC_DOWNLOAD, JOptionPane.WARNING_MESSAGE);
                return;
            }*/

            LyricSpider.doSpider(lyricPath, singer, lyricUrl);
        });

        lyricPanel.add(singerLabel);
        lyricPanel.add(singerText);
        lyricPanel.add(urlLabel);
        lyricPanel.add(urlText);
        lyricPanel.add(lyricPathLabel);
        lyricPanel.add(lyricPathText);
        lyricPanel.add(lyricBrowseBtn);
        lyricPanel.add(downloadBtn);

        tabPane.add("Lyric下载", lyricPanel);
    }

    private void createScannerPanel() {
        scanPanel = new JPanel();
        scanPanel.setLayout(null);

        musicPath.setFont(TOOLS_FONT);
        musicPath.setBounds(10, 20, 300, 35);

        musicBrowseBtn.setFont(TOOLS_FONT);
        musicBrowseBtn.setBounds(320, 20, 80, 35);

        musicBrowseBtn.addActionListener(event -> {
            File file = openChoseWindow(JFileChooser.DIRECTORIES_ONLY);
            if (file == null) {
                musicPath.setText("");
                return;
            }
            String musicDir = file.getPath();
            musicPath.setText(musicDir);

            try {
                flacMap = MusicScanner.scanMusic(musicDir);

            } catch (IOException e) {

            }

        });

        musicAnalysisBtn.addActionListener(event -> {

        });

        musicAnalysisBtn.setFont(TOOLS_FONT);
        musicAnalysisBtn.setBounds(410,20,80,35);
        musicExportBtn.setFont(TOOLS_FONT);
        musicExportBtn.setBounds(500,20,80,35);

        scanPanel.add(musicPath);
        scanPanel.add(musicBrowseBtn);
        scanPanel.add(musicAnalysisBtn);
        scanPanel.add(musicExportBtn);

        tabPane.add("扫描音乐", scanPanel);
    }

    public File openChoseWindow(int type){
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(type);//选择的文件类型(文件夹or文件)
        jfc.showDialog(new JLabel(), "选择");
        File file = jfc.getSelectedFile();
        return file;
    }

    public static void popWindow(String msg, String title, int type) {
        JOptionPane.showMessageDialog(null, msg, title, type);
    }

    public static void main(String[] args) {
        ToolsFrame frame = new ToolsFrame();
        frame.showFrame();
    }

}

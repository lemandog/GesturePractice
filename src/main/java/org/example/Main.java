package org.example;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class Main {
    static Logger logger = LogManager.getLogger(Main.class);
    static SaveDTO currentLoad;
    @Getter @Setter
    static String lastFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
    static JButton folderSelect = new JButton("Select a folder with references!");
    static JButton start = new JButton("PLAY");
    static JButton exit = new JButton("EXIT");
    @Getter @Setter
    static JCheckBox soundCheck = new JCheckBox("Play sounds");
    @Getter @Setter
    static JCheckBox subfoldersCheck = new JCheckBox("Include subfolders");
    @Getter @Setter
    static JCheckBox alwaysOnTop = new JCheckBox("Always on top");
    @Getter @Setter
    static JTextField secondsEach = new JTextField("30");
    @Getter @Setter
    static JTextField minutesWhole = new JTextField("30");

    public static void main(String[] args) {
        BasicConfigurator.configure();
        JFrame main = new JFrame("GesturePractice!");
        main.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                defaultExit();
            }
        });
        currentLoad = SettingManager.loadSettings();
        set(currentLoad);
        main.setSize(250,300);
        GridLayout layout = new GridLayout();
        layout.setColumns(1);
        layout.setRows(7);
        JPanel controls = new JPanel(layout);
        main.add(setStandardLayout(controls));
        main.setVisible(true);
        logger.debug("program launched");
    }

    private static void defaultExit() {
        currentLoad = SettingManager.loadCurrentState();
        SettingManager.saveSettings(currentLoad);
        System.exit(0);
    }

    private static void set(SaveDTO currentLoad) {
        secondsEach.setText(String.valueOf(currentLoad.getSecondsEach()));
        minutesWhole.setText(String.valueOf(currentLoad.getMinutesWhole()));
        soundCheck.setSelected(currentLoad.isSoundCheck());
        subfoldersCheck.setSelected(currentLoad.isSubfoldersCheck());
        alwaysOnTop.setSelected(currentLoad.isAlwaysOnTop());
        PictureViewer.main.setSize(currentLoad.getPWidth(),currentLoad.getPHeight());
        lastFolder = currentLoad.getLastFolder();
    }

    private static Component setStandardLayout(JPanel controls) {
        controls.add(folderSelect);

        JPanel timerControl = new JPanel();
        timerControl.add(new JLabel("Next picture each seconds"));
        timerControl.add(secondsEach);
        controls.add(timerControl);

        JPanel sessionControl = new JPanel();
        sessionControl.add(new JLabel("Session timer - minutes"));
        sessionControl.add(minutesWhole);
        controls.add(sessionControl);

        controls.add(soundCheck);
        controls.add(subfoldersCheck);
        controls.add(alwaysOnTop);

        JPanel programControl = new JPanel();
        start.addActionListener(e -> {
            logger.debug("PLAY PRESSED, starting...");
            List<String> imagesList = Util.checkImagesList(lastFolder);
            if (imagesList.size() != 0){
                PictureViewer.startSession(imagesList,SettingManager.loadCurrentState());
            };
        });
        folderSelect.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File(lastFolder));
            chooser.setDialogTitle("Select folder");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.showDialog(controls, "Select folder");
            lastFolder = chooser.getSelectedFile().getAbsolutePath();
            logger.debug("selected folder path: " + lastFolder);
        });
        exit.addActionListener(e -> {
            defaultExit();
        });
        programControl.add(start);
        programControl.add(exit);
        controls.add(programControl);

        return controls;
    }
}
package org.example;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.*;

public class SettingManager {
    static Logger logger = LogManager.getLogger(SettingManager.class);

    static final String defaultPathForSaves = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + File.separator + "GesturePractice.txt";
    public static SaveDTO loadSettings() {
            File settings = new File(defaultPathForSaves);
            if (!settings.exists()) {
                loadDefaultsAndCreateSave();
            }
            return deserialize(defaultPathForSaves);
    }
    private static void loadDefaultsAndCreateSave(){
        try {
            File saveFile = new File(defaultPathForSaves);
            saveFile.createNewFile();
            SaveDTO save = new SaveDTO(
                    Main.getLastFolder(),
                    Main.getSoundCheck().isEnabled(),
                    Main.getSubfoldersCheck().isEnabled(),
                    Main.getAlwaysOnTop().isEnabled(),
                    Integer.parseInt(Main.getSecondsEach().getText()),
                    Integer.parseInt(Main.getMinutesWhole().getText()),
                    PictureViewer.getHeigth(),
                    PictureViewer.getWidth());
            logger.debug("new save created, loading...");
            saveSettings(save);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveSettings(SaveDTO settings) {
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        try {
            fout = new FileOutputStream(defaultPathForSaves);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(settings);
            logger.debug(settings.toString());
            logger.debug("save written");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if(oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public static SaveDTO loadCurrentState() {
        return new SaveDTO(
                Main.getLastFolder(),
                Main.getSoundCheck().isSelected(),
                Main.getSubfoldersCheck().isSelected(),
                Main.alwaysOnTop.isSelected(),
                Integer.parseInt(Main.getSecondsEach().getText()),
                Integer.parseInt(Main.getMinutesWhole().getText()),
                PictureViewer.getHeigth(),
                PictureViewer.getWidth()
        );
    }
    static SaveDTO deserialize(String path){
        try {
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (SaveDTO) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            if (e.getClass().equals(InvalidClassException.class)){
                logger.debug("Old version of save! Rewriting...");
                new File(path).delete();
                return new SaveDTO();
            }
            if (e.getClass().equals(EOFException.class)){
                logger.debug("reqached end of file before something could be read!");
                return new SaveDTO();
            }
            throw new RuntimeException(e);
        }
    }

    static SaveDTO deserialize(File target){
        try {
            FileInputStream fis = new FileInputStream(target);
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (SaveDTO) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

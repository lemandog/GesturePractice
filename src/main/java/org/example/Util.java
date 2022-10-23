package org.example;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class Util {
    static Logger logger = LogManager.getLogger(Util.class);
    public static List<String> checkImagesList(String lastFolder) {
        File dir = new File(lastFolder);
        List<String> filesList;
        if (!Main.getSubfoldersCheck().isSelected()){
            filesList = new ArrayList<>();
            for (File e : dir.listFiles()) {
                filesList.add(e.getAbsolutePath());
            }
        } else{
            filesList = listf(lastFolder);
        }
        List<String> images = new ArrayList<>(0);

        for(String f : filesList){
            try {
                File file = new File(f);
                String mimetype = Files.probeContentType(file.toPath());
                if(file.isFile() && (mimetype != null && mimetype.split("/")[0].equals("image"))){
                    images.add(file.getAbsolutePath());
                    logger.debug("found file: " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        logger.debug("returning list: " + images.size());
        return images;
    }
    public static List<String> listf(String directoryName) {
        File directory = new File(directoryName);
        List<String> resultList = new ArrayList<String>();
        // get all the files from a directory
        File[] fList = directory.listFiles();
        try {
        resultList.addAll(Arrays.asList(fList).stream().map(File::getAbsolutePath).toList());
        for (File file : fList) {
            if (file.isDirectory()) {
                resultList.addAll(listf(file.getAbsolutePath()));
            }
        }
        }catch (java.lang.NullPointerException e ){
            logger.debug("no images found at path " + directoryName);
        }
        return resultList;
    }
}

package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class SaveDTO implements Serializable {
    @Getter
    String lastFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
    @Getter
    boolean soundCheck = true;
    @Getter
    boolean subfoldersCheck = true;
    @Getter
    boolean alwaysOnTop = true;
    @Getter
    int secondsEach = 30;
    @Getter
    int minutesWhole = 15;
    @Getter
    int pHeight = 400;
    @Getter
    int pWidth = 400;

    @Override
    public String toString(){
        return "LASTPATH" + lastFolder + "\n"
        +"SOUNDCHECK " + soundCheck + "\n"
        +"SUBFOLCHECK " + subfoldersCheck + "\n"
        +"AONTOPCHECK " + alwaysOnTop + "\n"
        +"EACHSECONDS " + secondsEach + "\n"
        +"WHOLETLIMIT " + minutesWhole + "\n"
        +"PICSHOWHEIG " + pHeight + "\n"
        +"PICSHOWWIDT " + pWidth + "\n";
    }
}

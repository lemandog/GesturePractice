package org.example;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PictureViewer {
    static JFrame main = new JFrame("Session in progress");
    static Logger logger = LogManager.getLogger(PictureViewer.class);
    static JLabel picture = new JLabel();
    static long startTime;
    static long endTime;
    static JButton pauseB;
    static JButton next;
    static JButton exit;
    static Label label = new Label();
    static Thread session;
    static boolean pause = false;
    static ImageIcon currentPic;
    static Long savedState = null;
    public static void startSession(List<String> imagesList, SaveDTO saveDTO) {

        pauseB = new JButton("PAUSE");
        exit = new JButton("STOP");
        exit.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.dispose();
                session.interrupt();
            }
        });
        next = new JButton("NEXT");
        Queue<String> queue = new LinkedList<>();
        queue.addAll(imagesList);

        if (saveDTO.getPWidth() == 0 || saveDTO.getPHeight() == 0){
            main.setSize(600,600);
        } else {
            main.setSize(saveDTO.getPWidth(),saveDTO.getPHeight());
        }
        main.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sessionStop();
            }
        });
        main.addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e) {
                if (!Objects.isNull(currentPic)) {
                    picture.setIcon(imageToSize(currentPic));
                }
            }
        });
        logger.debug("session started!");
        session = new Thread(() -> {
            startTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            endTime = LocalDateTime.now().plusMinutes(saveDTO.getMinutesWhole()).toEpochSecond(ZoneOffset.UTC);
            JPanel mainPanel = setStandardLayout(new JPanel());
            LocalDateTime endTimeRaw = LocalDateTime.now().plusMinutes(saveDTO.getMinutesWhole());
            AtomicReference<LocalDateTime> currentPictureTime = new AtomicReference<>(LocalDateTime.now().plusSeconds(saveDTO.secondsEach));
            LocalDateTime finalEndTimeRaw = endTimeRaw;
            pauseB.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pause(Duration.between(finalEndTimeRaw, LocalDateTime.now()).toSeconds());
                }
            });
            logger.debug("frame dimensions W/H" + saveDTO.getPWidth() + " " + saveDTO.getPHeight());
            main.add(mainPanel);
            main.setVisible(true);
            main.setAlwaysOnTop(saveDTO.alwaysOnTop);

            next.addActionListener(e ->{
                currentPic = new ImageIcon(queue.poll());
                currentPictureTime.set(LocalDateTime.now().plusSeconds(saveDTO.secondsEach));
                picture.setIcon(imageToSize(currentPic));
            });

            while(!queue.isEmpty() && LocalDateTime.now().isBefore(endTimeRaw)){
                if (!pause) {
                    label.setText("Pictures left: " + queue.size() + " Time left: " + Duration.between(endTimeRaw, LocalDateTime.now()).toSeconds() + " sec");
                    while (currentPictureTime.get().isBefore(LocalDateTime.now())) {
                        if (!Objects.isNull(savedState)){endTimeRaw = LocalDateTime.now().plusSeconds(savedState); savedState = null;}
                        logger.debug("queue is not empty - size " + queue.size());
                        currentPictureTime.set(LocalDateTime.now().plusSeconds(saveDTO.secondsEach));
                        currentPic = new ImageIcon(queue.poll());
                        picture.setIcon(imageToSize(currentPic));
                    }
                }
            }
            main.dispose();
        });
        session.start();
    }

    private static Icon imageToSize(ImageIcon imageIcon) {
        Image image = imageIcon.getImage(); // transform it
        double smallest = Arrays.stream(new double[]{picture.getHeight(),picture.getWidth()}).min().getAsDouble();
        Image newimg = image.getScaledInstance((int) ((smallest/picture.getWidth())*picture.getWidth()), (int) ((smallest/picture.getHeight())*picture.getHeight()),java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        return new ImageIcon(newimg);
    }

    private static void pause(long currentPictureTimeLeft) {
        savedState = currentPictureTimeLeft;
        pause = !pause;
    }

    private static int sessionStop() {
        session.interrupt();
        Main.exit.doClick();
        return 0;
    }

    private static JPanel setStandardLayout(JPanel jPanel) {
        BorderLayout bord = new BorderLayout();
        jPanel.setLayout(bord);
        jPanel.setBackground(Color.WHITE);
        jPanel.add(picture, BorderLayout.CENTER);
        jPanel.add(label, BorderLayout.SOUTH);
        JPanel buttonCenter = new JPanel(new GridLayout(2,1));
        JPanel buttonBar = new JPanel(new GridLayout(1,3));
        buttonBar.add(exit);
        buttonBar.add(pauseB);
        buttonBar.add(next);
        buttonCenter.add(label);
        buttonCenter.add(buttonBar);
        jPanel.add(buttonCenter, BorderLayout.SOUTH);
        return jPanel;
    }

    public static int getHeigth() {
        return main.getHeight();
    }

    public static int getWidth() {
        return main.getWidth();
    }


}

import org.opencv.videoio.VideoCapture;

import javax.management.remote.JMXConnectorFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Aakash on 1/14/2017.
 */
public class MainFrame {

    static VideoCapture capture;
    static JLabel videoDisplay;
    JFrame mainFrame;
    JButton startVideo;
    JButton stopVideo;
    JPanel buttonHolder;
    VideoCapturer vc = new VideoCapturer();
    MainFrame() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        mainFrame = new JFrame("Compter Vision");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        videoDisplay = new JLabel();
        buttonHolder = new JPanel();
        startVideo = new JButton("Start Video");
        stopVideo = new JButton("Stop Video");
        buttonHolder.add(startVideo);
        buttonHolder.add(stopVideo);
        mainFrame.add(videoDisplay, BorderLayout.NORTH);
        mainFrame.add(buttonHolder, BorderLayout.SOUTH);
        mainFrame.setVisible(true);
        mainFrame.setSize(658, 560);
        mainFrame.setLocationRelativeTo(null);




        startVideo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                videoDisplay.setVisible(true);
                vc.execute();
            }
        });


        stopVideo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                videoDisplay.setVisible(false);
                capture.release();
            }
        });

    }
}

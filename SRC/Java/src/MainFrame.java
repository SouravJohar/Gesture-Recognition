import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aakash on 1/14/2017.
 */
public class MainFrame {

    static VideoCapture capture; //The capture element of OpenCv(gets the webcam Image)
    static JLabel videoDisplay, binary;//
    JFrame mainFrame;
    JButton startVideo, stopVideo, histoCapture;
    JPanel buttonHolder;//the bottom part of the GUI holding the three buttons

     MainFrame() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");//Setting the windows look and feel
        mainFrame = new JFrame("Compter Vision");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        videoDisplay = new JLabel();
        buttonHolder = new JPanel();
        binary=new JLabel();
        startVideo = new JButton("Start Video");
        stopVideo = new JButton("Stop Video");
        histoCapture = new JButton("Capture Histogram");
        buttonHolder.add(startVideo);
        buttonHolder.add(histoCapture);
        buttonHolder.add(stopVideo);
        mainFrame.add(videoDisplay, BorderLayout.WEST);
        mainFrame.add(binary, BorderLayout.EAST);
        mainFrame.add(buttonHolder, BorderLayout.SOUTH);
        mainFrame.setVisible(true);
        mainFrame.setSize(1316, 560);//perfect for a 1080p screen
        mainFrame.setLocationRelativeTo(null);




        startVideo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VideoCapturer vc = new VideoCapturer();
                videoDisplay.setVisible(true);
                binary.setVisible(true);
                vc.execute();//getting the video feed, entire thing runs in the background(multithreading)
            }
        });


        stopVideo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VideoCapturer.clicked = false;
                File file = new File("check.txt");                
                try(FileWriter writer = new FileWriter(file.getAbsolutePath(), true)){
                	String a = "blahblah";                	
                	writer.write(a);                	               	//Have to create a file called check, so that the background scripts know when to stop
                } catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                System.out.println(file.getAbsolutePath());
                VideoCapturer.webCamImage=null;
                VideoCapturer.toShow = null;
                VideoCapturer.toShowContour = null;
                videoDisplay.setVisible(false);
                binary.setVisible(false);
                capture.release();
                try {
					TimeUnit.SECONDS.sleep(1);//Sleeping for 1 second to ensure the scripts catch up, and terminate
					file.delete(); //deletes the file, as it is no longer needed
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                //file.delete();
            }
        });

        histoCapture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VideoCapturer.clicked=true;
                ImageProcessor a = new ImageProcessor();
                Mat histogram = a.captureHistogram(VideoCapturer.webCamImage);
                VideoCapturer.histogram = histogram;
                binary.setVisible(true);
                File bri = new File("BrightnessScript.ps1");
                File vol = new File("VolumeScript.ps1");
                try {
					Runtime.getRuntime().exec("cmd /c powershell -noexit \"& \"\""+bri.getAbsolutePath() +"\"\"\"");//initiates the brightness script
					Runtime.getRuntime().exec("cmd /c powershell -noexit \"& \"\""+vol.getAbsolutePath() +"\"\"\"");//initiates the volume script
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
            }
        });

    }
}

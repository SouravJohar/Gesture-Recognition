import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;

/**
 * Created by Aakash on 1/14/2017.
 */
public class VideoCapturer extends SwingWorker<Void, Void> {
    public static Mat webCamImage = new Mat();

    @Override
    protected Void doInBackground() throws Exception {

        ImageProcessor imageProcessor = new ImageProcessor();
        Image tempImage;
        Image path;
        MainFrame.capture = new VideoCapture(0);
        MainFrame.capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        MainFrame.capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
        if(MainFrame.capture.isOpened()){
            while(true){
                MainFrame.capture.read(webCamImage);

                if(!webCamImage.empty()){
                    //webCamImage=webCamImage.inv();
                    Core.flip(webCamImage, webCamImage, 180);
                    imageProcessor.addTemplate();
                    tempImage = imageProcessor.toBufferedImage(webCamImage);
                    ImageIcon icon = new ImageIcon(tempImage, "captured image");
                    MainFrame.videoDisplay.setIcon(icon);
                    //mainFrame.pack();
                }
                else{
                    break;
                }
            }
        }
        else{
            System.out.println("Couldn't open");
        }
        return null;
    }

}

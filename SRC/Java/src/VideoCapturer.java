import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by Aakash on 1/14/2017.
 */
public class VideoCapturer extends SwingWorker<Void, Void> {
    public static Mat webCamImage = new Mat();
    static Mat histogram = new Mat();
    static boolean clicked=false;
    static Mat toShow = new Mat();
    static Mat toShowContour = new Mat();
    static boolean button1Click=false, button2Click=false;
    static Mat webCamImage2, webCamImage3;
    

    @Override
    protected Void doInBackground() throws Exception {


        ImageProcessor imageProcessor = new ImageProcessor();
        Image tempImage = null;
        Image tempImage2;
        MainFrame.capture = new VideoCapture(0);
        MainFrame.capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        MainFrame.capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
        if(MainFrame.capture.isOpened()){
            while(true){
                MainFrame.capture.read(webCamImage);

                if(!webCamImage.empty()){
                    Core.flip(webCamImage, webCamImage, 180);
                    Mat histoRect = webCamImage.clone();
                    Imgproc.rectangle(histoRect, ImageProcessor.histoRect.tl(), ImageProcessor.histoRect.br(),
                            new Scalar(0, 255, 255), 2);
                    
                    webCamImage2=webCamImage.clone();
                    webCamImage3=webCamImage.clone();
                    toShowContour = webCamImage.clone();
                    toShow = histoRect;
                    if(clicked ==true){
                       // toShow=imageProcessor.addTemplate(webCamImage);
                        Mat tImage = webCamImage.clone();
                        //Imgproc.GaussianBlur(tImage, tImage, new Size(7, 7), 0);
                        Imgproc.cvtColor(tImage,tImage, Imgproc.COLOR_BGR2HSV);
                        ArrayList<Mat> matList = new ArrayList();
                        matList.add(tImage);
                        Mat probImage = new Mat();
                        Imgproc.calcBackProject(matList, new MatOfInt(0, 1), histogram, probImage, new MatOfFloat(0, 180, 0, 256), 1);
                        Mat ellipse = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
                        Imgproc.filter2D(probImage, probImage, -1, ellipse);
                        Imgproc.threshold(probImage, probImage, 70, 255, Imgproc.THRESH_BINARY);
                        ArrayList<Mat> merge = new ArrayList<>();
                        merge.add(probImage);
                        merge.add(probImage);
                        merge.add(probImage);
                        Core.merge(merge, probImage);
                        Core.bitwise_and(probImage, webCamImage, probImage);
                        //toShow=probImage;
                        Mat temp = new Mat();
                        temp=probImage;
                        toShowContour = imageProcessor.convertToBinary(temp);
                        toShowContour = imageProcessor.findAndDrawContours(toShowContour);
                        //Imgproc.threshold(to, converted, 70, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
                        toShow = imageProcessor.addTemplate(webCamImage2);
                    }
                    /*toShowContour=imageProcessor.convertToBinary(webCamImage);
                    toShowContour=imageProcessor.findAndDrawContours(toShowContour);*/                               
                   
                    if(button1Click==false&&button2Click==false){
                    	tempImage = imageProcessor.toBufferedImage(toShow);
                    }
                    else{
                    	tempImage=imageProcessor.toBufferedImage(webCamImage);
                    }
                    
                    tempImage2 = imageProcessor.toBufferedImage(toShowContour);
                    ImageIcon icon = new ImageIcon(tempImage, "captured image");
                    ImageIcon icon2 = new ImageIcon(tempImage2, "processed image");
                    MainFrame.videoDisplay.setIcon(icon);
                    MainFrame.binary.setIcon(icon2);
                    try(FileWriter writer = new FileWriter("NirBrightness.txt", true)){
                    	String a = Double.toString(ImageProcessor.brightness);                    	
                        //System.out.println(a);
                    	if(!a.equals("0.0")){
                    	writer.write(a);
                    	writer.write(System.getProperty("line.separator"));
                    }
                    	
                    }
                    try(FileWriter writer = new FileWriter("NirVolume.txt", true)){
                    	String a = Double.toString(ImageProcessor.volume);
                    	//System.out.println(a);
                    	if(!a.equals("0.0")){
                    	writer.write(a);
                    	writer.write(System.getProperty("line.separator"));
                    	}
                    	
                    }
                    
                    //writer.write((int)ImageProcessor.brightness);
                    
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

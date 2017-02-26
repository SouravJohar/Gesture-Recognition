import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Import;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import javafx.scene.transform.Scale;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.*;

/**
 * Created by Aakash on 1/9/2017.
 */
public class ImageProcessor {
	public static Mat clone = VideoCapturer.webCamImage.clone();
    public static Rect histoRect = new Rect(new Point(295, 215), new Point(345, 265));
    CascadeClassifier faceDetector = new CascadeClassifier("Cascades/haarcascade_frontalface_default.xml");
    boolean fingerClicked = false;
    Point thumb = new Point(), index = new Point();
    static double volume, brightness;
    

    public Mat drawFace(Mat image){
        MatOfRect faceRect = new MatOfRect();
        faceDetector.detectMultiScale(image, faceRect);
        for(Rect rect : faceRect.toArray()){
            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x
                    + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 2);
        }
        return image;
    }


    public Image toBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] buffer = new byte[bufferSize];
        matrix.get(0, 0, buffer); // get all the pixels
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.
                rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().
                getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }

    public Mat addTemplate(Mat image) {
        //Imgproc.circle(image, new org.opencv.core.Point(320, 240), 3, new Scalar(0, 255, 255), 3);
    	Imgproc.circle(image, new org.opencv.core.Point(320, 60), 30, new Scalar(0, 255, 255), 2);
    	Imgproc.circle(image, new org.opencv.core.Point(320, 140), 30, new Scalar(0, 255, 255), 2);
        //Imgproc.rectangle(image, new org.opencv.core.Point(200, 120), new org.opencv.core.Point(300, 220), new Scalar(0, 255, 255), 2);
        //Imgproc.rectangle(image, new org.opencv.core.Point(340, 120), new org.opencv.core.Point(440, 220), new Scalar(0, 255, 255), 2);
        //Imgproc.rectangle(image, new org.opencv.core.Point(200, 260), new org.opencv.core.Point(300, 360), new Scalar(0, 255, 255), 2);
       //Imgproc.rectangle(image, new org.opencv.core.Point(340, 260), new org.opencv.core.Point(440, 360), new Scalar(0, 255, 255), 2);
        return image;
    }

    public Mat convertToBinary(Mat image) {
        Mat converted = new Mat();

        Imgproc.cvtColor(image, converted, Imgproc.COLOR_BGRA2GRAY);
        //Imgproc.GaussianBlur(converted, converted, new Size(5, 5), 0);
        Imgproc.medianBlur(converted, converted, 7);
        //Imgproc.adaptiveThreshold(converted, converted, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 15, 40);
        Imgproc.threshold(converted, converted, 70, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU );

        return converted;

    }
    Mat captureHistogram(Mat image){
        Mat hisArea = image.submat(histoRect);
        Imgproc.cvtColor(hisArea, hisArea, Imgproc.COLOR_BGR2HSV);
        ArrayList<Mat> matList = new ArrayList<>();
        matList.add(hisArea);
        Mat histogram = new Mat();
        Imgproc.calcHist(matList, new MatOfInt(0, 1), new Mat(), histogram, new MatOfInt(12, 150), new MatOfFloat(0, 180, 0, 256));
        Core.normalize(histogram, histogram, 0, 255, Core.NORM_MINMAX);
        return histogram;
    }

    public Mat findAndDrawContours(Mat image) throws IOException {
        Mat temp = VideoCapturer.webCamImage.clone();
        Mat contourMat = image.clone();
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(contourMat, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        int flag = 0;
        double maxArea = 7000;
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint currentContour = contours.get(i);

            double area = Imgproc.contourArea(currentContour);
            if (area>maxArea) {
                flag = i;
                maxArea = area;
            }
            currentContour = contours.get(flag);

            MatOfInt hull = new MatOfInt();
            Imgproc.convexHull(currentContour, hull);
            ArrayList<MatOfPoint> hullContours = new ArrayList<>();
            MatOfPoint hullMat = new MatOfPoint();
            hullMat.create((int) hull.size().height, 1, CvType.CV_32SC2);
            for(int j=0; j<hull.size().height; j++){
                int index =(int)hull.get(j, 0)[0];
                double[] point = new double[] {
                        currentContour.get(index, 0)[0], currentContour.get(index, 0)[1]
                };
                hullMat.put(j, 0, point);
            }
            hullContours.add(hullMat);
            ArrayList<MatOfPoint> printContour = new ArrayList<>();
            printContour.add(currentContour);


            Imgproc.drawContours(temp, printContour, 0, new Scalar(0, 255, 0), 2);
            //Imgproc.drawContours(temp, hullContours, 0, new Scalar(255,255,255),2);


            /*Moments moments = Imgproc.moments(contourMat, true);
            double cx=0;
            double cy=0;

            if(moments.get_m00()!=0){
                 cx =  (moments.get_m10()/moments.get_m00());
                cy =  (moments.get_m01()/moments.get_m00());

            }
            Point center = new Point(cx, cy);*/

            MatOfInt4 hullDefects = new MatOfInt4();


            Imgproc.convexityDefects(currentContour, hull, hullDefects);



            Point startp = new Point();
            Point endp = new Point();
            //Point farp = new Point();
            

            double miny=300;
            double minx=600;
            int checker=1;
            double check1=0, check2=0;
            double[] checkEnd=null;
            
            for(int k=0; k<hullDefects.size().height; k++){
                double[] sefd = hullDefects.get(k, 0);

                double[] start = currentContour.get((int) sefd[0], 0);
                startp.set(start);
                double[] end = currentContour.get((int) sefd[1], 0);
                endp.set(end);
                /*double[] far = currentContour.get((int) sefd[2], 0);
                farp.set(far);*/

                if(endp.y>350){
	                    continue;
	                }
                if((k>0)){

                    double[] tempsefd = hullDefects.get(k-1, 0);
                    double[] tempEnd = currentContour.get((int) tempsefd[1], 0);


                    if((Math.abs((tempEnd[0] - end[0])) < 15)||(Math.abs(tempEnd[1] - end[1]) < 15)){
                        continue;
                    }
                }
                if(k==0){
                    minx=endp.x;
                    miny=endp.y;
                }
                if(endp.x<10||endp.y<10){
                	continue;
                }
                               
                if(checker==1){
                	if(endp.y<miny&&endp.y!=(double)0.0&&(Math.abs(thumb.x-endp.x)>10)){
                    index.set(end);
                    
                	}
                check1=  index.y;
                   	if((endp.x<minx&&endp.x!=(double)0.0&&endp.y!=(double)0.0)){
               		thumb.set(end);
               		checkEnd=end;                    
                   	}
                        	
                check2=thumb.x;
                checker++;
                }
                if(checker==2){
                	if(endp.y<miny&&endp.y!=(double)0.0&&endp.x!=(double)0.0){
                        index.set(end);
                        
                    }
                	double xi=index.x, yi=index.y;
                    if(Math.abs(index.y-check1)>50)
                    {	end[0]=xi;
                    	end[1]=check1;
                    	index.set(end);
                    }
                    if((endp.x<minx&&endp.x!=(double)0.0&&endp.y!=(double)0.0)){
                        thumb.set(end);
                        
                    }
                    if(Math.abs(thumb.y-end[1])>50){
                    	thumb.set(checkEnd);
                    	//continue;
                    }
                    if(Math.abs(thumb.x-end[0])>50){
                    	thumb.set(checkEnd);
                    }
                                        
                    checker--;
                }
                if(endp.y>thumb.y){
                	continue;
                }
                if(endp.x>thumb.x){
                continue;
                }
                
                
                
                Imgproc.circle(temp, endp, 3, new Scalar(255, 0, 0), 2);
                
                

            }
            
            
            if(thumb.x !=(double)0.0||thumb.y !=(double)0.0){
            Imgproc.putText(temp, "Thumb", thumb,
                    Core.FONT_HERSHEY_COMPLEX_SMALL, 3, new Scalar(0, 0, 0), 2);
            Imgproc.circle(temp, thumb, 10, new Scalar(0, 0, 0), 4);
            double a =Math.sqrt((index.x-thumb.x)*(index.x-thumb.x)+(index.y-thumb.y)*(index.y-thumb.y))/Math.abs(index.x-thumb.x);
            if(a>0)
            //System.out.println(a);
            if(a> 2.5){
            	fingerClicked=true;
            }
            else
            	fingerClicked=false;
            //System.out.println(fingerClicked);
            //System.out.println(index.x+" "+index.y);
            }         
            
            if(index.x !=(double)0.0||index.y !=(double)0.0){
            Imgproc.putText(temp, "Index", index,
                    Core.FONT_HERSHEY_COMPLEX_SMALL, 3, new Scalar(0, 0, 0), 2);
            Imgproc.circle(temp, index, 10, new Scalar(0, 0, 0), 4);
            
            }
            
            
            //double a=350.0, b=290.0, c=70.0, d=570.0, e=350.0, f=290.0;
            if(fingerClicked==true&&index.x<470.0&&index.x>170.0&&index.y<100.0&&index.y>30.0){
            	
            	//Imgproc.circle(VideoCapturer.webCamImage2, new org.opencv.core.Point(320, 60), 30, new Scalar(0, 255, 255), -1);
            	Imgproc.line(VideoCapturer.webCamImage, new org.opencv.core.Point(170, 60 ), new org.opencv.core.Point(470, 60), new Scalar(0, 255, 255), 3);
            	Imgproc.circle(VideoCapturer.webCamImage, new org.opencv.core.Point(index.x, 60), 30, new Scalar(0, 255, 255), -1);
            	volume = (index.x-170)*218.5;
            	/*String command ="cmd /c nircmd.exe setvolume 0 "+a+" "+a;
            	Runtime.getRuntime().exec(command);*/
            	
            	//this.addSlider1(new org.opencv.core.Point(320, 60));
            	VideoCapturer.button1Click=true;
            }
            else{
            	VideoCapturer.button1Click=false;
            	
            }
            
            if(fingerClicked==true&&index.x<470.0&&index.x>170.0&&index.y<180.0&&index.y>110.0){
            	//this.addSlider2(new org.opencv.core.Point(320, 140));
            	Imgproc.line(VideoCapturer.webCamImage, new org.opencv.core.Point(170, 140 ), new org.opencv.core.Point(470, 140), new Scalar(0, 255, 255), 3);
            	Imgproc.circle(VideoCapturer.webCamImage, new org.opencv.core.Point(index.x, 140), 30, new Scalar(0, 255, 255), -1);
            	brightness = (index.x-170.0)/3;
            	/*String command = "cmd /c nircmd.exe setbrightness "+a;
            	Runtime.getRuntime().exec(command);*/
            	
            	VideoCapturer.button2Click=true;
            	//Imgproc.circle(VideoCapturer.webCamImage2, new org.opencv.core.Point(320, 140), 30, new Scalar(0, 255, 255), -1);
            }
            else{
            	VideoCapturer.button2Click=false;
            	
            }
            

        }

        return temp;

    }
    
    
    
    
    




}

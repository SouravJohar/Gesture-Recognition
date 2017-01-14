import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by Aakash on 1/9/2017.
 */
public class ImageProcessor {
    private CascadeClassifier faceDetector;
    public Image toBufferedImage(Mat matrix){
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( matrix.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = matrix.channels()*matrix.cols()*matrix.rows();
        byte [] buffer = new byte[bufferSize];
        matrix.get(0,0,buffer); // get all the pixels
        BufferedImage image = new BufferedImage(matrix.cols(),matrix.
                rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().
                getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }
   
}

package com.example.fn;

import com.example.fn.cloudevents.OCIEventBinding;
import com.example.fn.cloudevents.ObjectStorageObjectEvent;
import com.fnproject.fn.api.InputBinding;
import com.fnproject.fn.api.RuntimeContext;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FacesFunctions {
    static {
        OpenCVInit.loadLibrary();
        System.setProperty("java.awt.headless", "true");
    }

    final CascadeClassifier faceClassifier;
    final BufferedImage sombrero;
    public static ObjectStore store;

    public FacesFunctions(RuntimeContext rtc) throws IOException {
        faceClassifier = new CascadeClassifier();
        if (!faceClassifier.load("data/haarcascade_frontalface_alt.xml")) {
            throw new RuntimeException("Failed to load face classifier ");
        }

        sombrero = ImageIO.read(new File("data/sombrero.png"));

//        if (!faceClassifier.load("data/haarcascade_eye_tree_eyeglasses.xml")) {
//            throw new RuntimeException("Failed to load eye classifier ");
//        }

    }


    public List<Rect> detectFaces(BufferedImage bi) {

        Mat frame = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        try {
            byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
            frame.put(0, 0, data);

            MatOfRect faces = new MatOfRect();
            try {

                Mat grayFrame = new Mat();
                try {

                    Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
                    Imgproc.equalizeHist(grayFrame, grayFrame);

                    int height = grayFrame.rows();
                    double absoluteFaceSize;
                    absoluteFaceSize = Math.round(height * 0.2f);
                    this.faceClassifier.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                      new Size(absoluteFaceSize, absoluteFaceSize), new Size());
                } finally {
                    grayFrame.release();

                }
                List<Rect> f = faces.toList();
                return f;
            } finally {
                faces.release();
            }


        } finally {
            frame.release();
        }


    }

    public BufferedImage loadImage(String filename) {
        try {
            return ImageIO.read(new File(filename));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image");
        }
    }


    public String handleRequest(@InputBinding(coercion = OCIEventBinding.class) ObjectStorageObjectEvent event) throws Exception {

        BufferedImage img = loadImage("testdata/face3.jpg");

        List<Rect> rects = detectFaces(img);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(3));

        Collections.sort(rects, Comparator.<Rect>comparingInt(a -> a.width));
        for (Rect r : rects) {
            System.err.println("Found rect " + r);
           // g.drawRect(r.x, r.y, r.width, r.height);
            double sombreroRatio = (double) sombrero.getWidth() / (double) sombrero.getHeight();
            double hatWidth = r.width * 2;
            double hatHeight = hatWidth / sombreroRatio;

            g.drawImage(sombrero, (int)((double)r.x - ((hatWidth - r.width)/2.0)), r.y - (int)hatHeight +(r.height/5), (int)hatWidth,(int)hatHeight, null);
        }
        g.dispose();
        ImageIO.write(img, "jpg", new File("output.jpg"));

        return "found " + rects.size() + " faces on object " + event.getObjectName();

    }

}
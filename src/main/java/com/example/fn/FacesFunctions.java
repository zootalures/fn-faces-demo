package com.example.fn;

import com.example.fn.cloudevents.OCIEventBinding;
import com.example.fn.cloudevents.ObjectStorageObjectEvent;
import com.fnproject.fn.api.InputBinding;
import com.fnproject.fn.api.RuntimeContext;
import com.oracle.bmc.auth.*;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.util.Comparator;
import java.util.List;

public class FacesFunctions {
    static {
        OpenCVInit.loadLibrary();
        // TODO : move this to the FDK startup
        System.setProperty("java.awt.headless", "true");
    }

    private final CascadeClassifier faceClassifier;
    private final BufferedImage sombrero;
    private final BufferedImage moustache;
    private ObjectStorage objectStoreClient;
    private final String outputBucketName;

    public FacesFunctions(RuntimeContext ctx) throws IOException {
        faceClassifier = new CascadeClassifier();
        if (!faceClassifier.load("data/haarcascade_frontalface_alt.xml")) {
            System.err.println("can't load config xml");
            throw new RuntimeException("Failed to load face classifier ");
        }
        sombrero = ImageIO.read(new File("data/sombrero.png"));
        moustache = ImageIO.read(new File("data/moustache.png"));
        outputBucketName = reqEnv(ctx, "OUTPUT_BUCKET");
    }

    private String reqEnv(RuntimeContext ctx, String key) {
        return ctx.getConfigurationByKey(key).orElseThrow(() -> new RuntimeException("Missing required config " + key));

    }

    private ObjectStorage createObjectStoreClient() {
        System.out.println("Inside createObjectStoreClient");
        ObjectStorage objStoreClient = null;

        try {
            ResourcePrincipalAuthenticationDetailsProvider provider
                    = ResourcePrincipalAuthenticationDetailsProvider.builder()
                    .build();
            System.err.println("ResourcePrincipalAuthenticationDetailsProvider setup");

            objStoreClient = new ObjectStorageClient(provider);
            System.out.println("ObjectStorage client setup");

        } catch (Exception ex) {
            System.err.println("Exception in FDK " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("failed to create client", ex);
        }
        return objStoreClient;
    }

    private List<Rect> detectFaces(BufferedImage bi) {

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
                    double absoluteFaceSize = Math.round(height * 0.2f);
                    this.faceClassifier.detectMultiScale(grayFrame, faces, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE,
                      new Size(absoluteFaceSize, absoluteFaceSize), new Size());
                } finally {
                    grayFrame.release();

                }
                return faces.toList();
            } finally {
                faces.release();
            }

        } finally {
            frame.release();
        }

    }

    private BufferedImage loadImage(String namespace, String bucketName, String objectName) {
        try {
            GetObjectResponse resp = objectStoreClient.getObject(GetObjectRequest.builder().namespaceName(namespace).bucketName(bucketName).objectName(objectName).build());
            return ImageIO.read(resp.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image");
        }
    }


    private void writeImage(BufferedImage bi, String namespace, String bucketName, String objectName) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", bos);
            objectStoreClient.putObject(PutObjectRequest.builder()
              .bucketName(bucketName)
              .objectName(objectName)
              .contentType("image/jpeg")
              .namespaceName(namespace)
              .putObjectBody(new ByteArrayInputStream(bos.toByteArray()))
              .build());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image");
        }
    }


    public String handleRequest(@InputBinding(coercion = OCIEventBinding.class) ObjectStorageObjectEvent event) {
        System.err.println("Got a new event: " + event.toString());

        try {
            objectStoreClient = createObjectStoreClient();
            String namespace = event.additionalDetails.namespace;
            String bucketName = event.additionalDetails.bucketName;
            String resourceName = event.resourceName;

            BufferedImage img = loadImage(namespace, bucketName, resourceName);
            List<Rect> faceRectangles = detectFaces(img);
            // Sort the face boxes by size smallest to largest (for Z-order)
            faceRectangles.sort(Comparator.comparingInt(a -> a.width));

            Graphics2D g = img.createGraphics();
            for (Rect r : faceRectangles) {
                System.err.println("Found rect " + r);
                double sombreroRatio = (double) sombrero.getWidth() / (double) sombrero.getHeight();
                double hatWidth = r.width * 2.3;
                double hatHeight = hatWidth / sombreroRatio;

                double moustacheRatio = (double) moustache.getWidth() / (double) moustache.getHeight();
                double stacheWidth = r.width;
                double stacheHeight = stacheWidth / moustacheRatio;

                g.drawImage(sombrero, (int) ((double) r.x - ((hatWidth - r.width) / 2.0)), r.y - (int) hatHeight + (int) (r.height * .3), (int) hatWidth, (int) hatHeight, null);
                g.drawImage(moustache, (int) ((double) r.x - ((stacheWidth - r.width) / 2.0)), r.y - (int) stacheHeight + (int) (r.height * 1.4), (int) stacheWidth, (int) stacheHeight, null);
            }
            g.dispose();
            writeImage(img, namespace, outputBucketName, resourceName);
            System.err.println("found " + faceRectangles.size() + " faces on object " + resourceName);
            return "ok";
        } catch (Exception ex) {
            System.err.println("Exception in FDK " + ex.getMessage());
            ex.printStackTrace();
            return "oops";
        }
    }

}
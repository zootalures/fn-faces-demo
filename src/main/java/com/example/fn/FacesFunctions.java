package com.example.fn;

import com.example.fn.cloudevents.OCIEventBinding;
import com.example.fn.cloudevents.ObjectStorageObjectEvent;
import com.fnproject.fn.api.InputBinding;
import com.fnproject.fn.api.RuntimeContext;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
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
import java.util.*;
import java.util.List;

public class FacesFunctions {
    static {
        OpenCVInit.loadLibrary();
        // TODO : move this to the FDK startup
        System.setProperty("java.awt.headless", "true");
    }

    private final CascadeClassifier faceClassifier;
    private final BufferedImage sombrero;
    private final ObjectStorage objectStoreClient;
    private final String objectStoreNamespace;
    private final String outputBucketName;

    public FacesFunctions(RuntimeContext ctx) throws IOException {
        faceClassifier = new CascadeClassifier();
        if (!faceClassifier.load("data/haarcascade_frontalface_alt.xml")) {
            throw new RuntimeException("Failed to load face classifier ");
        }

        sombrero = ImageIO.read(new File("data/sombrero.png"));

//        if (!faceClassifier.load("data/haarcascade_eye_tree_eyeglasses.xml")) {
//            throw new RuntimeException("Failed to load eye classifier ");
//        }

        objectStoreClient = createObjectStoreClient(ctx);

        objectStoreNamespace = reqEnv(ctx,"OBJECT_NAMESPACE");
        outputBucketName = reqEnv(ctx,"OUTPUT_BUCKET");

    }

    public String reqEnv(RuntimeContext ctx,String key) {
        return  ctx.getConfigurationByKey(key).orElseThrow(() -> new RuntimeException("Missing required config " + key));

    }

    public ObjectStorage createObjectStoreClient(RuntimeContext ctx) {
        System.out.println("Inside createObjectStoreClient");

        ObjectStorage objStoreClient = null;

        try {
            String privateKey = reqEnv(ctx,"OCI_PRIVATE_KEY");
            byte[] decoded = Base64.getDecoder().decode(privateKey);
            com.google.common.base.Supplier<InputStream> privateKeyFromEnv = () -> new ByteArrayInputStream(decoded);

            AuthenticationDetailsProvider provider
              = SimpleAuthenticationDetailsProvider.builder()
              .tenantId(reqEnv(ctx,"OCI_TENANCY"))
              .userId(reqEnv(ctx,"OCI_USER"))
              .fingerprint(reqEnv(ctx,"OCI_KEY_FINGERPRINT"))
              .privateKeySupplier(privateKeyFromEnv)
              .build();

            System.err.println("AuthenticationDetailsProvider setup");

            objStoreClient = new ObjectStorageClient(provider);
            objStoreClient.setRegion(reqEnv(ctx,"OCI_REGION"));
            //objStoreClient.setRegion(reqEnv("REGION"));


            System.out.println("ObjectStorage client setup");
        } catch (Exception ex) {
            System.err.println("Exception in FDK "+ex.getMessage());
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

    private BufferedImage loadImage(String bucketName, String objectName) {
        try {
            GetObjectResponse resp = objectStoreClient.getObject(GetObjectRequest.builder().namespaceName(objectStoreNamespace).bucketName(bucketName).objectName(objectName).build());
            return ImageIO.read(resp.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image");
        }
    }


    private void writeImage(BufferedImage bi, String bucketName, String objectName) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", bos);

            objectStoreClient.putObject(PutObjectRequest.builder().bucketName(bucketName).objectName(objectName).contentType("image/jpeg").namespaceName(objectStoreNamespace).putObjectBody(new ByteArrayInputStream(bos.toByteArray())).build());


        } catch (IOException e) {
            throw new RuntimeException("Failed to load image");
        }
    }


    public String handleRequest(@InputBinding(coercion = OCIEventBinding.class) ObjectStorageObjectEvent event) throws Exception {

        BufferedImage img = loadImage(event.getBucketName(), event.getObjectName());

        List<Rect> faceRectangles = detectFaces(img);
        Graphics2D g = img.createGraphics();
        // Sort the face boxes by size smallest to largest (for Z-order)
        Collections.sort(faceRectangles, Comparator.comparingInt(a -> a.width));
        for (Rect r : faceRectangles) {
            System.err.println("Found rect " + r);
            // g.drawRect(r.x, r.y, r.width, r.height);
            double sombreroRatio = (double) sombrero.getWidth() / (double) sombrero.getHeight();
            double hatWidth = r.width * 2;
            double hatHeight = hatWidth / sombreroRatio;

            g.drawImage(sombrero, (int) ((double) r.x - ((hatWidth - r.width) / 2.0)), r.y - (int) hatHeight + (int) (r.height * .2), (int) hatWidth, (int) hatHeight, null);
        }
        g.dispose();
        writeImage(img, outputBucketName, event.getObjectName());
        System.err.println("found " + faceRectangles.size() + " faces on object " + event.getObjectName());
        return "ok";
    }

}
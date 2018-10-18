package com.example.fn;

import com.fnproject.fn.testing.*;
import org.junit.*;

import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.Assert.*;

public class FacesFunctionsTest {

    @Rule
    public final FnTestingRule testing = FnTestingRule.createDefault();


    String sampleEvent = "{\n" +
      "  \"eventType\": \"com.oraclecloud.objectstorage.object.create\",\n" +
      "  \"eventTypeVersion\": \"1.0\",\n" +
      "  \"cloudEventsVersion\": \"0.1\",\n" +
      "  \"source\": \"/service/objectstorage/resourceType/object\",\n" +
      "  \"eventID\": \"dead-beef-abcd-1234\",\n" +
      "  \"eventTime\": \"2018-04-12T23:20:50.52Z\",\n" +
      "  \"extensions\": {\n" +
      "    \"compartmentId\": \"ocidv1.customerfoo.compartment.abcd\"\n" +
      "  },\n" +
      "  \"data\": {\n" +
      "    \"tenantId\": \"ocid1.tenancy.oc1..aaaaaaaaltbr5bobenjcbaa3qsuvds6lowqokqzdjllfbwxk5ypjj2e7d23a\",\n" +
      "    \"bucketOcid\": \"ocid1.bucket.oc1.phx.aaaaaaaa7mbpdfjfi6rzz4ef2pu7hhb5vhyf4tmt73d6l4lfa3qkhmyjlljq\",\n" +
      "    \"bucketName\": \"facedetection-incoming\",\n" +
      "    \"api\": \"v2\",\n" +
      "    \"objectName\": \"033f268c-8bd6-9f9c-d881-90dcb71ed522.jpg\",\n" +
      "    \"objectEtag\": \"7746B06277BD3795E053824310ACA1F6\",\n" +
      "    \"resourceType\": \"OBJECT\",\n" +
      "    \"action\": \"CREATE\",\n" +
      "    \"creationTime\": \"2018-10-02T21:58:30.070Z\"\n" +
      "  }\n" +
      "}\n";

    String sampleEvent2 = "{\n" +
      "  \"eventType\": \"com.oraclecloud.objectstorage.object.create\",\n" +
      "  \"eventTypeVersion\": \"1.0\",\n" +
      "  \"cloudEventsVersion\": \"0.1\",\n" +
      "  \"source\": \"/service/objectstorage/resourceType/object\",\n" +
      "  \"eventID\": \"dead-beef-abcd-1234\",\n" +
      "  \"eventTime\": \"2018-04-12T23:20:50.52Z\",\n" +
      "  \"extensions\": {\n" +
      "    \"compartmentId\": \"ocidv1.customerfoo.compartment.abcd\"\n" +
      "  },\n" +
      "  \"data\": \"{\\\"tenantId\\\":\\\"ocid1.tenancy.oc1..aaaaaaaaltbr5bobenjcbaa3qsuvds6lowqokqzdjllfbwxk5ypjj2e7d23a\\\",\\\"bucketOcid\\\":\\\"ocid1.bucket.oc1.phx.aaaaaaaa7mbpdfjfi6rzz4ef2pu7hhb5vhyf4tmt73d6l4lfa3qkhmyjlljq\\\",\\\"bucketName\\\":\\\"facedetection-incoming\\\",\\\"api\\\":\\\"v2\\\",\\\"objectName\\\":\\\"033f268c-8bd6-9f9c-d881-90dcb71ed522.jpg\\\",\\\"objectEtag\\\":\\\"7746B06277BD3795E053824310ACA1F6\\\",\\\"resourceType\\\":\\\"OBJECT\\\",\\\"action\\\":\\\"CREATE\\\",\\\"creationTime\\\":\\\"2018-10-02T21:58:30.070Z\\\"}\"\n" +
      "}\n";


    @Test
    @Ignore
    public void shouldFindFacesInBasicImage()  throws  Exception{
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("test.properties"));
        }catch(Exception e){
            Assume.assumeFalse("No test properties skipping test",true);
        }
        testing.addSharedClass(OpenCVInit.class);
        testing.addSharedClassPrefix("org.opencv.");

        testing.setConfig("OBJECT_NAMESPACE",p.getProperty("oci.object_namespace"));
        testing.setConfig("OCI_KEY_FINGERPRINT",p.getProperty("oci.key_fingerprint"));
        testing.setConfig("OCI_PRIVATE_KEY",p.getProperty("oci.private_key"));
        testing.setConfig("OCI_REGION",p.getProperty("oci.region"));
        testing.setConfig("OCI_TENANCY",p.getProperty("oci.tenancy"));
        testing.setConfig("OCI_USER",p.getProperty("oci.user"));
        testing.setConfig("OUTPUT_BUCKET",p.getProperty("oci.out_bucket"));


        testing.givenEvent()
          .withHeader("Content-Type","application/cloudevents+json")
          .withBody(sampleEvent2)
          .enqueue();

        testing.thenRun(FacesFunctions.class, "handleRequest");

        FnResult result = testing.getOnlyResult();
        assertEquals("ok", result.getBodyAsString());
    }


}
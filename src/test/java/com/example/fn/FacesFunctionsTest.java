package com.example.fn;

import com.fnproject.fn.testing.*;
import org.junit.*;

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
      "    \"resourceName\": \"resource-name.jpg\",\n" +
      "    \"additionalDetails\": {\n" +
      "      \"bucketId\": \"ocid1.bucket.oc1.phx.aaa...jlljq\",\n" +
      "      \"bucketName\": \"facedetection-incoming\",\n" +
      "      \"namespace\": \"tenant-name\"\n" +
      "    }\n" +
      "  }\n" +
      "}\n";

    @Ignore
    @Test
    public void shouldFindFacesInBasicImage() {

        testing.addSharedClass(OpenCVInit.class);
        testing.addSharedClassPrefix("org.opencv.");
        testing.setConfig("OUTPUT_BUCKET", "facedetection-result");
        testing.setConfig("OCI_REGION", "us-phoenix-1");

        testing.givenEvent()
          .withHeader("Content-Type","application/cloudevents+json")
          .withBody(sampleEvent)
          .enqueue();

        testing.thenRun(FacesFunctions.class, "handleRequest");

        FnResult result = testing.getOnlyResult();
        assertEquals("ok", result.getBodyAsString());
    }

}

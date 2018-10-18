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
      "    \"tenantId\": \"ocid1.tenancy.oc1..aaaaaaaaltbr5bobenjcbaa3qsuvds6lowqokqzdjllfbwxk5ypjj2e7d23a\",\n" +
      "    \"bucketOcid\": \"ocid1.bucket.oc1.phx.aaaaaaaa7mbpdfjfi6rzz4ef2pu7hhb5vhyf4tmt73d6l4lfa3qkhmyjlljq\",\n" +
      "    \"bucketName\": \"oow-demo\",\n" +
      "    \"api\": \"v2\",\n" +
      "    \"objectName\": \"image2.png\",\n" +
      "    \"objectEtag\": \"7746B06277BD3795E053824310ACA1F6\",\n" +
      "    \"resourceType\": \"OBJECT\",\n" +
      "    \"action\": \"CREATE\",\n" +
      "    \"creationTime\": \"2018-10-02T21:58:30.070Z\"\n" +
      "  }\n" +
      "}";

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
      "  \"data\": \"{\\\"tenantId\\\":\\\"ocid1.tenancy.oc1..aaaaaaaaltbr5bobenjcbaa3qsuvds6lowqokqzdjllfbwxk5ypjj2e7d23a\\\",\\\"bucketOcid\\\":\\\"ocid1.bucket.oc1.phx.aaaaaaaa7mbpdfjfi6rzz4ef2pu7hhb5vhyf4tmt73d6l4lfa3qkhmyjlljq\\\",\\\"bucketName\\\":\\\"sriks-casper-oow-demo\\\",\\\"api\\\":\\\"v2\\\",\\\"objectName\\\":\\\"image4.png\\\",\\\"objectEtag\\\":\\\"7871ED25DC03D86AE053824310AC5EA7\\\",\\\"resourceType\\\":\\\"OBJECT\\\",\\\"action\\\":\\\"CREATE\\\",\\\"creationTime\\\":\\\"2018-10-17T18:43:33.506Z\\\"}\"\n" +
      "}\n";

    @Test
    public void shouldFindFacesInBasicImage() {
        testing.addSharedClass(OpenCVInit.class);
        testing.addSharedClassPrefix("org.opencv.");

        testing.givenEvent()
          .withHeader("Content-Type","application/cloudevents+json")
          .withBody(sampleEvent)
          .enqueue();

        testing.thenRun(FacesFunctions.class, "handleRequest");

        FnResult result = testing.getOnlyResult();
        assertEquals("found 3 faces on object image2.png", result.getBodyAsString());
    }

}
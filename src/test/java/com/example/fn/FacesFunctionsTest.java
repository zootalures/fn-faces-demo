package com.example.fn;

import com.fnproject.fn.testing.*;
import org.junit.*;

import static org.junit.Assert.*;

public class FacesFunctionsTest {

    @Rule
    public final FnTestingRule testing = FnTestingRule.createDefault();


    String sampleEvent = "{\"data\":{\"objectName\":\"foo\"}}";

    @Test
    public void shouldReturnGreeting() {
        testing.addSharedClass(OpenCVInit.class);
        testing.addSharedClassPrefix("org.opencv.");

        testing.givenEvent()
          .withHeader("Content-Type","application/cloudevents+json")
          .withBody(sampleEvent)
          .enqueue();

        testing.thenRun(FacesFunctions.class, "handleRequest");

        FnResult result = testing.getOnlyResult();
        assertEquals("found 1 faces on object foo", result.getBodyAsString());
    }

}
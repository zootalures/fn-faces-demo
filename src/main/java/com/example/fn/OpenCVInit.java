package com.example.fn;


/**
 * Surrogate that loads OpenCV to enable the library to be shared with testing
 * This is a separate class to allow it to span the testing class loader and the function class loader
 * Created on 17/10/2018.
 * <p>
 * (c) 2018 Oracle Corporation
 */
public class OpenCVInit {

    public static void loadLibrary() {
        nu.pattern.OpenCV.loadShared();
    }
}

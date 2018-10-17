package com.example.fn;

import org.opencv.core.Core;

/**
 * Surrogate that loads OpenCV to enable the library to be shared with testing
 * Created on 17/10/2018.
 * <p>
 * (c) 2018 Oracle Corporation
 */
public class OpenCVInit {

    public static void loadLibrary() {
        nu.pattern.OpenCV.loadShared();
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}

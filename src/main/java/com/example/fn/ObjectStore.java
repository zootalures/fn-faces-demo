package com.example.fn;

import java.io.InputStream;

/**
 * Created on 17/10/2018.
 * <p>
 * (c) 2018 Oracle Corporation
 */
public interface ObjectStore {
    InputStream getObject(String url);

    void putObject(String url, InputStream source);
}

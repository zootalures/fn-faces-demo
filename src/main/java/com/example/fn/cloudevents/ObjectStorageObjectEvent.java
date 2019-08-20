package com.example.fn.cloudevents;

import java.util.Map;

/**
 * Created on 16/10/2018.
 * <p>
 * (c) 2018 Oracle Corporation
 */
public final class ObjectStorageObjectEvent {

    private String archivalState;

    private Map<String, Map<String, String>> bucketDefinedTags;

    private Map<String, String> bucketFreeformTags;

    private String bucketId;

    private String bucketName;

    private String displayName;

    private String eTag;

    private String namespace;

    public String getArchivalState() {
        return this.archivalState;
    }

    public Map<String, Map<String, String>> getBucketDefinedTags() {
        return this.bucketDefinedTags;
    }

    public Map<String, String> getBucketFreeformTags() {
        return this.bucketFreeformTags;
    }

    public String getBucketId() {
        return this.bucketId;
    }

    public String getBucketName() {
        return this.bucketName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String geteTag() {
        return this.eTag;
    }

    public String getNamespace() {
        return this.namespace;
    }
}

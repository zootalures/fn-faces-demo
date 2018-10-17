package com.example.fn.cloudevents;

/**
 * Created on 16/10/2018.
 * <p>
 * (c) 2018 Oracle Corporation
 */
public final class ObjectStorageObjectEvent {

    private String tenantId;

    private String bucketOcid;

    private String bucketName;

    private String api;

    private String objectName;

    private String objectEtag;

    private String resourceType;

    private String action;

    private String creationTime;


    public String getTenantId() {
        return this.tenantId;
    }

    public String getBucketOcid() {
        return this.bucketOcid;
    }

    public String getBucketName() {
        return this.bucketName;
    }

    public String getApi() {
        return this.api;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public String getObjectEtag() {
        return this.objectEtag;
    }

    public String getResourceType() {
        return this.resourceType;
    }

    public String getAction() {
        return this.action;
    }

    public String getCreationTime() {
        return this.creationTime;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setBucketOcid(String bucketOcid) {
        this.bucketOcid = bucketOcid;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public void setObjectEtag(String objectEtag) {
        this.objectEtag = objectEtag;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }
}

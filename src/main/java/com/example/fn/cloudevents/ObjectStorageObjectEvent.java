package com.example.fn.cloudevents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Optional;

/**
 * Created on 16/10/2018.
 * <p>
 * (c) 2018 Oracle Corporation
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ObjectStorageObjectEvent {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public final class AdditionalDetails {
        @JsonProperty("eTag")
        public String eTag;
        @JsonProperty("archivalState")
        public String archivalState;
        @JsonProperty("bucketName")
        public String bucketName;
        @JsonProperty("bucketId")
        public String bucketId;
        @JsonProperty("namespace")
        public String namespace;
    }
    @JsonProperty("additionalDetails")
    public AdditionalDetails additionalDetails;
    @JsonProperty("definedTags")
    public Optional<Map<String, Map<String, String>>> definedTags;
    @JsonProperty("freeformTags")
    public Optional<Map<String, String>> freeformTags;
    @JsonProperty("resourceName")
    public String resourceName;
    @JsonProperty("resourceId")
    public String resourceId;
    @JsonProperty("compartmentId")
    public String compartmentId;
    @JsonProperty("compartmentName")
    public String compartmentName;
    @JsonProperty("availabilityDomain")
    public String availabilityDomain;
}

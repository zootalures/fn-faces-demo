package com.example.fn.cloudevents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;
import java.util.Map;

/**
 * Basic data model for cloud event
 * Created on 16/10/2018.
 * <p>
 * (c) 2018 Oracle Corporation
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CloudEvent {
    @JsonProperty("eventID")
    public String eventID;
    @JsonProperty("eventType")
    public String eventType;
    @JsonProperty("source")
    public String source;
    @JsonProperty("eventTypeVersion")
    public String eventTypeVersion;
    @JsonProperty("eventTime")
    public Date eventTime;
    @JsonProperty("schemaURL")
    public String schemaURL;
    @JsonProperty("contentType")
    public String contentType;
    @JsonProperty("extensions")
    public Map<String, Object> extensions;
    @JsonProperty("data")
    public JsonNode data;
}

package com.example.fn.cloudevents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Basic data model for cloud event
 * Created on 16/10/2018.
 * <p>
 * (c) 2018 Oracle Corporation
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CloudEvent {


    public JsonNode data;
}

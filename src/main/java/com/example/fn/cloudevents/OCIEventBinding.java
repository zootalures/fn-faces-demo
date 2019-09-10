package com.example.fn.cloudevents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnproject.fn.api.*;

import java.io.IOException;
import java.util.Optional;

/**
 * // TODO just an interface taster,
 * Created on 17/10/2018.
 * <p>
 * (c) 2018 Oracle Corporation
 */
public class OCIEventBinding implements InputCoercion<Object> {
    private static final ObjectMapper myObjectMapper = new ObjectMapper();

    @Override
    public Optional<Object> tryCoerceParam(InvocationContext invocationContext, int i, InputEvent inputEvent, MethodWrapper methodWrapper) {

        TypeWrapper paramType = methodWrapper.getParamType(i);
        if (CloudEvent.class.equals(paramType.getParameterClass()) || ObjectStorageObjectEvent.class.equals(paramType.getParameterClass())) {
            // TODO, verify content type etc.
            CloudEvent ce = inputEvent.consumeBody((is) -> {
                    try {
                        return myObjectMapper.readValue(is, CloudEvent.class);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to extract cloud event", e);
                    }
                }
            );

            if (CloudEvent.class.equals(paramType.getParameterClass())) {
                return Optional.of(ce);
            } else if (ObjectStorageObjectEvent.class.equals(paramType.getParameterClass())) {
                try {
                    return Optional.of(myObjectMapper.treeToValue(ce.data, ObjectStorageObjectEvent.class));
                } catch (JsonProcessingException e) {
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }
}

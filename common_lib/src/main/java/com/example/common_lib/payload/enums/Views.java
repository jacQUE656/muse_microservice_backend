package com.example.common_lib.payload.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Views {
    PUBLIC,
    PRIVATE;

    @JsonCreator
    public static Views fromString(String value) {
        if (value == null) return null;
        try {
            return Views.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown view type: " + value);
        }
    }
}

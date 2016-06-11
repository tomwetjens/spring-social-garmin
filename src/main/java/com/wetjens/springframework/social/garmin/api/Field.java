package com.wetjens.springframework.social.garmin.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Field<T> {

    private T value;

    @JsonProperty("uom")
    private String unitOfMeasurement;

    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public T getValue() {
        return value;
    }
}

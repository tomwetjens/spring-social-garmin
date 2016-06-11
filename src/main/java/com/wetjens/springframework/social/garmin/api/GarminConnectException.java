package com.wetjens.springframework.social.garmin.api;

public class GarminConnectException extends RuntimeException {

    public GarminConnectException(String message) {
        super(message);
    }

    public GarminConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}

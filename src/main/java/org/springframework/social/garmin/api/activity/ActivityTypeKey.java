package org.springframework.social.garmin.api.activity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ActivityTypeKey {

    RUNNING,
    WALKING,
    CYCLING,
    OTHER;

    @JsonCreator
    public static ActivityTypeKey fromString(String str) {
        if (str != null) {
            ActivityTypeKey activityTypeKey;
            try {
                activityTypeKey = ActivityTypeKey.valueOf(str.toUpperCase());
            } catch (IllegalArgumentException e) {
                activityTypeKey = ActivityTypeKey.OTHER;
            }
            return activityTypeKey;
        }
        return null;
    }

}

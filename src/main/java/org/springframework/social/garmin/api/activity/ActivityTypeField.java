package org.springframework.social.garmin.api.activity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.social.garmin.api.activity.ActivityTypeKey;
import org.springframework.social.garmin.api.Field;

public class ActivityTypeField extends Field<ActivityTypeKey> {

    private ActivityTypeKey key;

    @JsonCreator
    public ActivityTypeField(@JsonProperty("key") String key) {
        this.key = ActivityTypeKey.fromString(key);
    }

    public ActivityTypeKey getKey() {
        return key;
    }
}

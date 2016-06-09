package org.springframework.social.garmin.api.activity.internal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.social.garmin.api.activity.Activity;
import org.springframework.social.garmin.api.internal.PageResult;

class ActivityResult extends PageResult<Activity> {

    @JsonCreator
    public ActivityResult(@JsonProperty("activity") Activity activity) {
        super(activity);
    }
}

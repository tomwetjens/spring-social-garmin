package com.wetjens.springframework.social.garmin.api.activity.internal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wetjens.springframework.social.garmin.api.internal.PageResult;
import com.wetjens.springframework.social.garmin.api.activity.Activity;

class ActivityResult extends PageResult<Activity> {

    @JsonCreator
    public ActivityResult(@JsonProperty("activity") Activity activity) {
        super(activity);
    }
}

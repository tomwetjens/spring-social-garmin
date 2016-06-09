package org.springframework.social.garmin.api.activity.internal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.social.garmin.api.activity.Activity;
import org.springframework.social.garmin.api.internal.PageResults;

import java.util.List;

class ActivityResults extends PageResults<Activity> {

    @JsonCreator
    public ActivityResults(@JsonProperty("activities") List<ActivityResult> activities) {
        super(activities);
    }
}

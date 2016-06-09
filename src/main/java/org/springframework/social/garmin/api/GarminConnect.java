package org.springframework.social.garmin.api;

import org.springframework.social.ApiBinding;
import org.springframework.social.garmin.api.activity.ActivityOperations;
import org.springframework.social.garmin.api.activity.ActivitySearchOperations;

public interface GarminConnect extends ApiBinding {

    void authenticate();

    ActivityOperations activity();

    ActivitySearchOperations activitySearch();

}

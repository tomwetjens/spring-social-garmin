package com.wetjens.springframework.social.garmin.api;

import com.wetjens.springframework.social.garmin.api.activity.ActivityOperations;
import com.wetjens.springframework.social.garmin.api.activity.ActivitySearchOperations;
import org.springframework.social.ApiBinding;

public interface GarminConnect extends ApiBinding {

    void authenticate();

    ActivityOperations activity();

    ActivitySearchOperations activitySearch();

}

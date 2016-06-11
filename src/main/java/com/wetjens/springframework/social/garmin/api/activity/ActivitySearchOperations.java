package com.wetjens.springframework.social.garmin.api.activity;

import com.wetjens.springframework.social.garmin.api.Paging;

public interface ActivitySearchOperations {

    Paging<Activity> getActivities();

}

package org.springframework.social.garmin.api.activity;

import org.springframework.social.garmin.api.Paging;

public interface ActivitySearchOperations {

    Paging<Activity> getActivities();

}

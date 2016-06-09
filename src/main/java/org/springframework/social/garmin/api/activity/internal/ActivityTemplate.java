package org.springframework.social.garmin.api.activity.internal;

import org.springframework.social.garmin.api.activity.ActivityOperations;
import org.springframework.social.garmin.api.activity.ActivityType;
import org.springframework.social.garmin.api.internal.AbstractGarminClientTemplate;
import org.springframework.social.garmin.api.internal.GarminConnectClient;

import java.util.Arrays;
import java.util.List;

public class ActivityTemplate extends AbstractGarminClientTemplate implements ActivityOperations {

    public ActivityTemplate(GarminConnectClient client) {
        super(client);
    }

    public List<ActivityType> getActivityTypes() {
        ActivityType[] activityTypes = this.getClient().callService("https://connect.garmin.com/modern/proxy/activity-service/activity/activityTypes", ActivityType[].class);
        return Arrays.asList(activityTypes);
    }
}

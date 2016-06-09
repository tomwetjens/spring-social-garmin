package org.springframework.social.garmin.api.internal;

import org.springframework.social.garmin.api.GarminConnect;
import org.springframework.social.garmin.api.GarminConnectException;
import org.springframework.social.garmin.api.activity.ActivityOperations;
import org.springframework.social.garmin.api.activity.ActivitySearchOperations;
import org.springframework.social.garmin.api.activity.internal.ActivitySearchTemplate;
import org.springframework.social.garmin.api.activity.internal.ActivityTemplate;

public class GarminConnectTemplate implements GarminConnect {

    private final GarminConnectClient client;

    public GarminConnectTemplate(String username, String password) {
        this.client = new GarminConnectClient(username, password);
    }

    public void authenticate() {
        this.client.authenticate();
    }

    public ActivityOperations activity() {
        return new ActivityTemplate(this.client);
    }

    public ActivitySearchOperations activitySearch() {
        return new ActivitySearchTemplate(this.client);
    }

    public boolean isAuthorized() {
        // credentials are always set
        return true;
    }

    public boolean test() {
        try {
            this.activity().getActivityTypes();
            return true;
        } catch (GarminConnectException e) {
            return false;
        }
    }
}

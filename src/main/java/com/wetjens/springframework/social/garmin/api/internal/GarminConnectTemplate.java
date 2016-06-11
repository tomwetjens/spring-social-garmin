package com.wetjens.springframework.social.garmin.api.internal;

import com.wetjens.springframework.social.garmin.api.activity.ActivityOperations;
import com.wetjens.springframework.social.garmin.api.activity.ActivitySearchOperations;
import com.wetjens.springframework.social.garmin.api.GarminConnect;
import com.wetjens.springframework.social.garmin.api.GarminConnectException;
import com.wetjens.springframework.social.garmin.api.activity.internal.ActivitySearchTemplate;
import com.wetjens.springframework.social.garmin.api.activity.internal.ActivityTemplate;

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

package org.springframework.social.garmin.api.activity;

public class Activity {

    private String activityName;

    private ActivityTypeField activityType;

    private ActivitySummary activitySummary;

    public String getActivityName() {
        return this.activityName;
    }

    public ActivityTypeField getActivityType() {
        return activityType;
    }

    public ActivitySummary getActivitySummary() {
        return this.activitySummary;
    }
}

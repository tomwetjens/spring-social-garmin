package org.springframework.social.garmin.api.activity.internal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.social.garmin.api.Paging;
import org.springframework.social.garmin.api.activity.Activity;
import org.springframework.social.garmin.api.activity.ActivitySearchOperations;
import org.springframework.social.garmin.api.internal.AbstractGarminClientTemplate;
import org.springframework.social.garmin.api.internal.AbstractPaging;
import org.springframework.social.garmin.api.internal.GarminConnectClient;
import org.springframework.social.garmin.api.internal.PageResults;

public class ActivitySearchTemplate extends AbstractGarminClientTemplate implements ActivitySearchOperations {

    public ActivitySearchTemplate(GarminConnectClient client) {
        super(client);
    }

    public Paging<Activity> getActivities() {
        return new ActivityPaging(this.getClient(), 0, 10);
    }

    static final class ActivityPaging extends AbstractPaging<Activity> {

        ActivityPaging(GarminConnectClient client, int start, int limit) {
            super(client, start, limit);
        }

        @Override
        protected PageResults<Activity> retrievePage(int start, int limit) {
            ActivitySearchResponse response = this.getClient().callService("https://connect.garmin.com/proxy/activity-search-service-1.2/json/activities?start=" + start + "&limit=" + limit, ActivitySearchResponse.class);
            return response.getResults();
        }

        static final class ActivitySearchResponse {

            private final ActivityResults results;

            @JsonCreator
            public ActivitySearchResponse(@JsonProperty("results") ActivityResults results) {
                this.results = results;
            }

            ActivityResults getResults() {
                return this.results;
            }
        }

    }


}

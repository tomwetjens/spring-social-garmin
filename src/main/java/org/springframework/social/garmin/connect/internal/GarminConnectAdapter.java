package org.springframework.social.garmin.connect.internal;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.garmin.api.GarminConnect;
import org.springframework.social.garmin.api.internal.GarminConnectTemplate;

public class GarminConnectAdapter implements ApiAdapter<GarminConnect> {

    private GarminConnect api;

    public boolean test(GarminConnect garminConnect) {
        return ((GarminConnectTemplate) garminConnect).test();
    }

    public void setConnectionValues(GarminConnect garminConnect, ConnectionValues connectionValues) {
        // TODO Fetch profile and set values
    }

    public UserProfile fetchUserProfile(GarminConnect garminConnect) {
        // TODO Fetch the profile
        return new UserProfile(null, null, null, null, null, null);
    }

    public void updateStatus(GarminConnect garminConnect, String statusMessage) {
        throw new UnsupportedOperationException();
    }

    public GarminConnect getApi(String username, String password) {
        if (api == null) {
            api = new GarminConnectTemplate(username, password);
        }
        return api;
    }
}

package org.springframework.social.garmin.connect;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.garmin.api.GarminConnect;
import org.springframework.social.garmin.connect.internal.GarminConnectAdapter;
import org.springframework.social.garmin.connect.internal.GarminConnectServiceProvider;

public class GarminConnectConnectionFactory extends ConnectionFactory<GarminConnect> {

    public GarminConnectConnectionFactory() {
        super(GarminConnectConnection.PROVIDER_ID, new GarminConnectServiceProvider(), new GarminConnectAdapter());
    }

    public Connection<GarminConnect> createConnection(ConnectionData data) {
        return new GarminConnectConnection(data, (GarminConnectAdapter) this.getApiAdapter());
    }

    public Connection<GarminConnect> createConnection(String username, String password) {
        return new GarminConnectConnection(username, password, (GarminConnectAdapter) this.getApiAdapter());
    }
}

package com.wetjens.springframework.social.garmin.connect;

import com.wetjens.springframework.social.garmin.connect.internal.GarminConnectServiceProvider;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import com.wetjens.springframework.social.garmin.api.GarminConnect;
import com.wetjens.springframework.social.garmin.connect.internal.GarminConnectAdapter;

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

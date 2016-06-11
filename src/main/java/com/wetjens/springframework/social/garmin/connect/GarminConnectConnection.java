package com.wetjens.springframework.social.garmin.connect;

import com.wetjens.springframework.social.garmin.connect.internal.GarminConnectAdapter;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.AbstractConnection;
import com.wetjens.springframework.social.garmin.api.GarminConnect;

public class GarminConnectConnection extends AbstractConnection<GarminConnect> {

    public static final String PROVIDER_ID = "garmin";

    private final String password;

    private final GarminConnectAdapter apiAdapter;

    public GarminConnectConnection(String username, String password, GarminConnectAdapter apiAdapter) {
        super(apiAdapter);

        this.password = password;
        this.apiAdapter = apiAdapter;

        this.initKey(PROVIDER_ID, username);
    }

    public GarminConnectConnection(ConnectionData connectionData, GarminConnectAdapter apiAdapter) {
        super(apiAdapter);

        this.password = connectionData.getSecret();
        this.apiAdapter = apiAdapter;

        this.initKey(PROVIDER_ID, connectionData.getProviderUserId());
    }

    public GarminConnect getApi() {
        return this.apiAdapter.getApi(this.getKey().getProviderUserId(), this.password);
    }

    public ConnectionData createData() {
        return new ConnectionData(PROVIDER_ID, this.getKey().getProviderUserId(), null, null, null, null, this.password, null, null);
    }
}

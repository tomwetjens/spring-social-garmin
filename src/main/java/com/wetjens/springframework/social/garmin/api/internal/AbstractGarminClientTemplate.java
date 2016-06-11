package com.wetjens.springframework.social.garmin.api.internal;

public abstract class AbstractGarminClientTemplate {

    private final GarminConnectClient client;

    public AbstractGarminClientTemplate(GarminConnectClient client) {
        this.client = client;
    }

    public GarminConnectClient getClient() {
        return client;
    }
}

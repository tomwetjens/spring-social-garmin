package org.springframework.social.garmin.api.activity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.social.garmin.api.Field;
import org.springframework.social.garmin.api.internal.jackson.InstantFieldDeserializer;

import java.time.Instant;

public class ActivitySummary {

    @JsonProperty("BeginTimestamp")
    @JsonDeserialize(using = InstantFieldDeserializer.class)
    private Instant beginTimestamp;

    @JsonProperty("EndTimestamp")
    @JsonDeserialize(using = InstantFieldDeserializer.class)
    private Instant endTimestamp;

    @JsonProperty("SumDistance")
    private Field<Double> sumDistance;

    @JsonProperty("SumDuration")
    private Field<Double> sumDuration;

    @JsonProperty("SumEnergy")
    private Field<Double> sumEnergy;

    @JsonProperty("WeightedMeanHeartRate")
    private Field<Double> weightedMeanHeartRate;

    public Instant getBeginTimestamp() {
        return beginTimestamp;
    }

    public Instant getEndTimestamp() {
        return endTimestamp;
    }

    public Field<Double> getSumDistance() {
        return sumDistance;
    }

    public Field<Double> getSumDuration() {
        return sumDuration;
    }

    public Field<Double> getSumEnergy() {
        return sumEnergy;
    }

    public Field<Double> getWeightedMeanHeartRate() {
        return weightedMeanHeartRate;
    }
}

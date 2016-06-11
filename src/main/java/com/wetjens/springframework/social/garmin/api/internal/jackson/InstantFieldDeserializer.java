package com.wetjens.springframework.social.garmin.api.internal.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wetjens.springframework.social.garmin.api.Field;

import java.io.IOException;
import java.time.Instant;

public class InstantFieldDeserializer extends JsonDeserializer<Instant> {

    private static final TypeReference<Field<String>> FIELD_TYPE_REFERENCE = new TypeReference<Field<String>>() {
    };

    public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Field<String> field = jsonParser.readValueAs(FIELD_TYPE_REFERENCE);
        return field != null && field.getValue() != null ? Instant.parse(field.getValue()) : null;
    }
}

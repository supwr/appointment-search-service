package com.appointmentsearch.api.application.dto.event;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FlexibleOffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

    private static final DateTimeFormatter[] FORMATTERS = new DateTimeFormatter[] {
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z")
    };

    @Override
    public OffsetDateTime deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        final String value = parser.getValueAsString();
        if (value == null || value.isBlank()) {
            return null;
        }

        for (final DateTimeFormatter formatter : FORMATTERS) {
            try {
                return OffsetDateTime.parse(value, formatter);
            } catch (final DateTimeParseException ignored) {
                // try the next supported format
            }
        }

        throw context.weirdStringException(value, OffsetDateTime.class, "Unsupported appointmentDateTime format");
    }
}

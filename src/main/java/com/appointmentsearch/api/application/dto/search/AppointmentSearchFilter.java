package com.appointmentsearch.api.application.dto.search;

import java.time.OffsetDateTime;

public record AppointmentSearchFilter(
    OffsetDateTime from,
    OffsetDateTime to,
    Integer page,
    Integer size
) {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    public AppointmentSearchFilter {
        page = page == null || page < 0 ? DEFAULT_PAGE : page;
        size = size == null || size < 1 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
    }

    public static AppointmentSearchFilter empty() {
        return new AppointmentSearchFilter(null, null, DEFAULT_PAGE, DEFAULT_SIZE);
    }
}

package com.appointmentsearch.api.application.dto.search;

import java.util.List;

public record AppointmentPage(
    List<AppointmentView> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext
) {
}

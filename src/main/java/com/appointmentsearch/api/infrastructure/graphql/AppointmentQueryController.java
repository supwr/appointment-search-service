package com.appointmentsearch.api.infrastructure.graphql;

import com.appointmentsearch.api.application.dto.search.AppointmentPage;
import com.appointmentsearch.api.application.dto.search.AppointmentSearchFilter;
import com.appointmentsearch.api.application.usecase.AppointmentSearchUseCase;
import com.appointmentsearch.api.infrastructure.security.AppointmentAccessGuard;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@Profile("api")
public class AppointmentQueryController {

    private final AppointmentSearchUseCase searchUseCase;
    private final AppointmentAccessGuard accessGuard;

    public AppointmentQueryController(
        final AppointmentSearchUseCase searchUseCase,
        final AppointmentAccessGuard accessGuard
    ) {
        this.searchUseCase = searchUseCase;
        this.accessGuard = accessGuard;
    }

    @QueryMapping
//    @PreAuthorize("@appointmentAccessGuard.canAccessPatient(#patientId, authentication)")
    public AppointmentPage appointmentsByPatient(
        @Argument final UUID patientId,
        @Argument final AppointmentSearchFilter filter
    ) {
        return searchUseCase.execute(patientId, normalizeFilter(filter));
    }

    @QueryMapping
//    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'NURSE', 'ADMIN')")
    public AppointmentPage myAppointments(@Argument final AppointmentSearchFilter filter) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return searchUseCase.execute(accessGuard.requireCustomerId(authentication), normalizeFilter(filter));
    }

    private AppointmentSearchFilter normalizeFilter(final AppointmentSearchFilter filter) {
        return filter == null ? AppointmentSearchFilter.empty() : filter;
    }
}

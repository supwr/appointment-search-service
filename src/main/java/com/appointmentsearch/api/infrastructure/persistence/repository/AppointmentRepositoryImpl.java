package com.appointmentsearch.api.infrastructure.persistence.repository;

import com.appointmentsearch.api.infrastructure.persistence.document.AppointmentDocument;
import com.appointmentsearch.api.domain.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AppointmentRepositoryImpl implements AppointmentRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public AppointmentRepositoryImpl(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<AppointmentDocument> search(
        final UUID patientId,
        final OffsetDateTime from,
        final OffsetDateTime to,
        final AppointmentStatus status,
        final Pageable pageable
    ) {
        final List<Criteria> criteria = new ArrayList<>();
        criteria.add(Criteria.where("patientId").is(patientId));
        if (from != null) {
            criteria.add(Criteria.where("appointmentDateTime").gte(from));
        }
        if (to != null) {
            criteria.add(Criteria.where("appointmentDateTime").lte(to));
        }
        if (status != null) {
            criteria.add(Criteria.where("status").is(status));
        }

        final Query countQuery = buildQuery(criteria);
        final long total = mongoTemplate.count(countQuery, AppointmentDocument.class);

        final Query pageQuery = buildQuery(criteria).with(pageable);
        if (pageable.getSort().isUnsorted()) {
            pageQuery.with(Sort.by(Sort.Direction.DESC, "appointmentDateTime"));
        }

        final List<AppointmentDocument> content = mongoTemplate.find(pageQuery, AppointmentDocument.class);
        return new PageImpl<>(content, pageable, total);
    }

    private Query buildQuery(final List<Criteria> criteria) {
        if (CollectionUtils.isEmpty(criteria)) {
            return new Query();
        }
        return new Query(new Criteria().andOperator(criteria.toArray(Criteria[]::new)));
    }
}

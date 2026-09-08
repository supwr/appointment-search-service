package com.appointmentsearch.api.infrastructure.graphql;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Profile("api")
public class GraphQlExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(final Throwable ex, final DataFetchingEnvironment environment) {
        if (ex instanceof AccessDeniedException) {
            return buildError("FORBIDDEN", "Access denied", environment);
        }
        if (ex instanceof IllegalArgumentException) {
            return buildError("BAD_REQUEST", ex.getMessage(), environment);
        }
        return buildError("INTERNAL_SERVER_ERROR", "Unexpected error", environment);
    }

    private GraphQLError buildError(
        final String code,
        final String message,
        final DataFetchingEnvironment environment
    ) {
        return GraphqlErrorBuilder.newError(environment)
            .message(message)
            .extensions(Map.of("code", code))
            .build();
    }
}

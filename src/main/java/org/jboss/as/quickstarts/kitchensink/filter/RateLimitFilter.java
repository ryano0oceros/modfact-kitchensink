package org.jboss.as.quickstarts.kitchensink.filter;

import io.quarkiverse.bucket4j.runtime.RateLimited;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@PreMatching
@ApplicationScoped
@RateLimited(bucket = "api")
public class RateLimitFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // The rate limiting is handled automatically by the Quarkus extension
    }

    private record ErrorResponse(
        String code,
        String message,
        String details
    ) {}
} 
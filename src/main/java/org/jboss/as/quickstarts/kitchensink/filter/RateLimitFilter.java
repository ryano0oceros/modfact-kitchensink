package org.jboss.as.quickstarts.kitchensink.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Provider
@PreMatching
@ApplicationScoped
public class RateLimitFilter implements ContainerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private static final int CAPACITY = 10;
    private static final int REFILL_TOKENS = 10;
    private static final int REFILL_MINUTES = 1;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String clientIp = requestContext.getHeaderString("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = "unknown";
        }

        Bucket bucket = buckets.computeIfAbsent(clientIp, this::createNewBucket);

        if (!bucket.tryConsume(1)) {
            requestContext.abortWith(
                Response.status(Response.Status.TOO_MANY_REQUESTS)
                    .entity(new ErrorResponse(
                        "RATE_LIMIT_EXCEEDED",
                        "Too many requests",
                        "Please try again in " + REFILL_MINUTES + " minute(s)"
                    ))
                    .build()
            );
        }
    }

    private Bucket createNewBucket(String clientIp) {
        return Bucket4j.builder()
            .addLimit(Bandwidth.classic(CAPACITY, Refill.intervally(REFILL_TOKENS, Duration.ofMinutes(REFILL_MINUTES))))
            .build();
    }

    private record ErrorResponse(
        String code,
        String message,
        String details
    ) {}
} 
package org.jboss.as.quickstarts.kitchensink.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;

@Readiness
@ApplicationScoped
public class MongoHealthCheck implements HealthCheck {

    private final MemberService memberService;

    @Inject
    public MongoHealthCheck(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("MongoDB connection health check");
        
        try {
            // Try to count members to verify MongoDB connection
            long count = memberService.count();
            return responseBuilder
                .withData("memberCount", count)
                .up()
                .build();
        } catch (Exception e) {
            return responseBuilder
                .withData("error", e.getMessage())
                .down()
                .build();
        }
    }
} 
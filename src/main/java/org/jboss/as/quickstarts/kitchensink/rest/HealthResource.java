package org.jboss.as.quickstarts.kitchensink.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/health")
@ApplicationScoped
public class HealthResource {

    @GET
    public Response checkHealth() {
        return Response.ok().build();
    }
} 
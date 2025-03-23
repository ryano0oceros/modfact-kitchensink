package org.jboss.as.quickstarts.kitchensink.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/health")
@ApplicationScoped
@Tag(name = "Health", description = "Health check endpoint")
public class HealthResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Check health status", description = "Returns the health status of the application")
    @APIResponse(
        responseCode = "200",
        description = "Application is healthy",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = String.class)
        )
    )
    public Response checkHealth() {
        return Response.ok().entity("{\"status\":\"UP\"}").build();
    }
} 
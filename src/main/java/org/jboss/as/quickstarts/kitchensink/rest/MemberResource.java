package org.jboss.as.quickstarts.kitchensink.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;

import java.util.List;

@Path("/v1/members")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Member Resource", description = "Operations for managing members")
public class MemberResource {

    private final MemberService memberService;

    @Inject
    public MemberResource(MemberService memberService) {
        this.memberService = memberService;
    }

    @GET
    @Operation(
        summary = "List all members",
        description = "Returns a paginated list of all registered members"
    )
    @APIResponse(
        responseCode = "200",
        description = "List of members retrieved successfully",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Member.class)
        )
    )
    @APIResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
        )
    )
    public Response listAllMembers() {
        var members = memberService.findAllMembers();
        return Response.ok(members).build();
    }

    @GET
    @Path("/{id}")
    @Operation(
        summary = "Get member by ID",
        description = "Returns a specific member by their ID"
    )
    @APIResponse(
        responseCode = "200",
        description = "Member found and returned successfully",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Member.class)
        )
    )
    @APIResponse(
        responseCode = "404",
        description = "Member not found",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
        )
    )
    @APIResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
        )
    )
    public Response getMemberById(
        @Parameter(
            description = "ID of the member to retrieve",
            required = true,
            example = "507f1f77bcf86cd799439011"
        )
        @PathParam("id") String id
    ) {
        return memberService.findById(id)
            .map(member -> Response.ok(member).build())
            .orElse(Response.status(Response.Status.NOT_FOUND)
                .entity(new GlobalExceptionHandler.ErrorResponse(
                    "NOT_FOUND",
                    "Member not found",
                    List.of("No member found with ID: " + id)
                ))
                .build());
    }

    @POST
    @Operation(
        summary = "Create new member",
        description = "Creates a new member with the provided information"
    )
    @APIResponse(
        responseCode = "201",
        description = "Member created successfully",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Member.class)
        )
    )
    @APIResponse(
        responseCode = "400",
        description = "Invalid input",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
        )
    )
    @APIResponse(
        responseCode = "409",
        description = "Member with this email already exists",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
        )
    )
    @APIResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
        )
    )
    public Response createMember(
        @Parameter(
            description = "Member to create",
            required = true,
            schema = @Schema(implementation = Member.class)
        )
        @Valid Member member
    ) {
        try {
            var createdMember = memberService.register(member);
            return Response.status(Response.Status.CREATED)
                .entity(createdMember)
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new GlobalExceptionHandler.ErrorResponse(
                    "INVALID_INPUT",
                    "Invalid input provided",
                    List.of(e.getMessage())
                ))
                .build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                .entity(new GlobalExceptionHandler.ErrorResponse(
                    "DUPLICATE_EMAIL",
                    "Email already exists",
                    List.of(e.getMessage())
                ))
                .build();
        }
    }
} 
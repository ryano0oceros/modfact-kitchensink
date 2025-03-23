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
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;

import java.util.List;

@Path("/members")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Member Resource", description = "Operations for managing members")
public class MemberResource {

    @Inject
    private MemberService memberService;

    @GET
    @Operation(summary = "List all members", description = "Returns a list of all registered members")
    @APIResponse(
        responseCode = "200",
        description = "List of members",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Member.class)
        )
    )
    public List<Member> listAllMembers() {
        return memberService.findAllMembers();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get member by ID", description = "Returns a specific member by their ID")
    @APIResponse(
        responseCode = "200",
        description = "The member",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Member.class)
        )
    )
    @APIResponse(
        responseCode = "404",
        description = "Member not found"
    )
    public Response getMemberById(@PathParam("id") String id) {
        Member member = memberService.findById(id);
        if (member == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(member).build();
    }

    @POST
    @Operation(summary = "Create new member", description = "Creates a new member")
    @APIResponse(
        responseCode = "201",
        description = "Member created",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Member.class)
        )
    )
    @APIResponse(
        responseCode = "400",
        description = "Invalid input"
    )
    public Response createMember(@Valid Member member) {
        try {
            memberService.register(member);
            return Response.status(Response.Status.CREATED).entity(member).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
} 
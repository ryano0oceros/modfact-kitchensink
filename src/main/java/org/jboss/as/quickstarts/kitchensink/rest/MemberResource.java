package org.jboss.as.quickstarts.kitchensink.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;

import java.util.List;

@Path("/api/members")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MemberResource {

    @Inject
    private MemberService memberService;

    @GET
    public List<Member> listAllMembers() {
        return memberService.findAllMembers();
    }

    @GET
    @Path("/{id}")
    public Response getMemberById(@PathParam("id") String id) {
        Member member = memberService.findById(id);
        if (member == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(member).build();
    }

    @POST
    public Response createMember(@Valid Member member) {
        try {
            memberService.register(member);
            return Response.status(Response.Status.CREATED).entity(member).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
} 
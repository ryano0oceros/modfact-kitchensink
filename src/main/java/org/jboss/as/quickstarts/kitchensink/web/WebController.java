package org.jboss.as.quickstarts.kitchensink.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
public class WebController {

    @Inject
    Template base;

    @Inject
    Template members;

    @Inject
    MemberService memberService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance index() {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "Welcome to Kitchensink");
        return base.data(data);
    }

    @GET
    @Path("/members")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getMembers() {
        List<Member> membersList = memberService.findAllMembers();
        Map<String, Object> data = new HashMap<>();
        data.put("members", membersList);
        return members.data(data);
    }

    @POST
    @Path("/members")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response createMember(
            @FormParam("name") String name,
            @FormParam("email") String email,
            @FormParam("phoneNumber") String phoneNumber,
            @Context HttpHeaders headers) {
        try {
            Member member = new Member();
            member.setName(name);
            member.setEmail(email);
            member.setPhoneNumber(phoneNumber);
            
            memberService.register(member);
            
            // If the request is HTMX, return just the updated table content
            String htmxRequest = headers.getHeaderString("HX-Request");
            if (htmxRequest != null && htmxRequest.equals("true")) {
                List<Member> updatedMembers = memberService.findAllMembers();
                Map<String, Object> data = new HashMap<>();
                data.put("members", updatedMembers);
                return Response.ok(members.data(data)
                        .getAttribute("content"))
                        .header("HX-Trigger", "{\"showMessage\": {\"type\": \"success\", \"message\": \"Member registered successfully!\"}}")
                        .build();
            }
            
            // Otherwise redirect to the members page with a flash message
            Map<String, Object> data = new HashMap<>();
            data.put("flash", Map.of(
                "type", "success",
                "message", "Member registered successfully!"
            ));
            data.put("members", memberService.findAllMembers());
            return Response.ok(members.data(data)).build();
        } catch (Exception e) {
            Map<String, Object> data = new HashMap<>();
            data.put("flash", Map.of(
                "type", "error",
                "message", "Registration failed: " + e.getMessage()
            ));
            data.put("members", memberService.findAllMembers());
            return Response.ok(members.data(data)).build();
        }
    }
} 
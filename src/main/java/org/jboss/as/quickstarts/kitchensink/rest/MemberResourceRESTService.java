/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;

/**
 * REST API for managing members
 */
@Path("/api/v1/members")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Member Resource", description = "Operations for managing members")
public class MemberResourceRESTService {

    @Inject
    Logger log;

    @Inject
    Validator validator;

    @Inject
    MemberRepository repository;

    @Inject
    MemberRegistration registration;

    @GET
    @Operation(summary = "Get all members", description = "Returns a list of all registered members")
    @APIResponse(
        responseCode = "200",
        description = "List of members",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(type = SchemaType.ARRAY, implementation = Member.class)
        )
    )
    public List<Member> listAllMembers() {
        return repository.findAll().list();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get member by ID", description = "Returns a member by their ID")
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
    public Member getMember(
            @Parameter(description = "Member ID", required = true)
            @PathParam("id") String id) {
        Member member = repository.findById(new ObjectId(id));
        if (member == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return member;
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
    @APIResponse(
        responseCode = "409",
        description = "Email already exists"
    )
    public Response createMember(
            @Parameter(description = "Member to create", required = true)
            Member member) {
        try {
            validateMember(member);
            registration.register(member);
            return Response.status(Status.CREATED)
                    .entity(member)
                    .build();
        } catch (ConstraintViolationException ce) {
            return createViolationResponse(ce.getConstraintViolations()).build();
        } catch (ValidationException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Email already exists");
            return Response.status(Status.CONFLICT)
                    .entity(responseObj)
                    .build();
        } catch (Exception e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            return Response.status(Status.BAD_REQUEST)
                    .entity(responseObj)
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update member", description = "Updates an existing member")
    @APIResponse(
        responseCode = "200",
        description = "Member updated",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Member.class)
        )
    )
    @APIResponse(
        responseCode = "400",
        description = "Invalid input"
    )
    @APIResponse(
        responseCode = "404",
        description = "Member not found"
    )
    @APIResponse(
        responseCode = "409",
        description = "Email already exists"
    )
    public Response updateMember(
            @Parameter(description = "Member ID", required = true)
            @PathParam("id") String id,
            @Parameter(description = "Updated member details", required = true)
            Member member) {
        Member existingMember = repository.findById(new ObjectId(id));
        if (existingMember == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        try {
            // Only validate email uniqueness if it changed
            if (!existingMember.getEmail().equals(member.getEmail())) {
                validateMember(member);
            } else {
                Set<ConstraintViolation<Member>> violations = validator.validate(member);
                if (!violations.isEmpty()) {
                    throw new ConstraintViolationException(new HashSet<>(violations));
                }
            }

            member.setId(existingMember.getId());
            repository.update(member);
            return Response.ok(member).build();
        } catch (ConstraintViolationException ce) {
            return createViolationResponse(ce.getConstraintViolations()).build();
        } catch (ValidationException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Email already exists");
            return Response.status(Status.CONFLICT)
                    .entity(responseObj)
                    .build();
        } catch (Exception e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            return Response.status(Status.BAD_REQUEST)
                    .entity(responseObj)
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete member", description = "Deletes a member")
    @APIResponse(
        responseCode = "204",
        description = "Member deleted"
    )
    @APIResponse(
        responseCode = "404",
        description = "Member not found"
    )
    public Response deleteMember(
            @Parameter(description = "Member ID", required = true)
            @PathParam("id") String id) {
        Member member = repository.findById(new ObjectId(id));
        if (member == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        repository.delete(member);
        return Response.noContent().build();
    }

    private void validateMember(Member member) throws ConstraintViolationException, ValidationException {
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<>(violations));
        }

        if (emailAlreadyExists(member.getEmail())) {
            throw new ValidationException("Email already exists");
        }
    }

    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<>();
        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Status.BAD_REQUEST).entity(responseObj);
    }

    private boolean emailAlreadyExists(String email) {
        Member member = null;
        try {
            member = repository.findByEmail(email);
        } catch (Exception e) {
            // ignore
        }
        return member != null;
    }
}

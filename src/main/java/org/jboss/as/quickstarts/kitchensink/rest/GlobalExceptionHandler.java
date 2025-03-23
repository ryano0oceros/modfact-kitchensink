package org.jboss.as.quickstarts.kitchensink.rest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof ConstraintViolationException) {
            return handleConstraintViolation((ConstraintViolationException) exception);
        } else if (exception instanceof IllegalArgumentException) {
            return handleIllegalArgument((IllegalArgumentException) exception);
        } else if (exception instanceof IllegalStateException) {
            return handleIllegalState((IllegalStateException) exception);
        }
        
        // Default error handling
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(new ErrorResponse(
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                List.of(exception.getMessage())
            ))
            .build();
    }

    private Response handleConstraintViolation(ConstraintViolationException e) {
        List<String> violations = e.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toList());

        return Response.status(Response.Status.BAD_REQUEST)
            .entity(new ErrorResponse(
                "VALIDATION_ERROR",
                "Validation failed",
                violations
            ))
            .build();
    }

    private Response handleIllegalArgument(IllegalArgumentException e) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(new ErrorResponse(
                "INVALID_INPUT",
                "Invalid input provided",
                List.of(e.getMessage())
            ))
            .build();
    }

    private Response handleIllegalState(IllegalStateException e) {
        return Response.status(Response.Status.CONFLICT)
            .entity(new ErrorResponse(
                "INVALID_STATE",
                "Operation cannot be performed in current state",
                List.of(e.getMessage())
            ))
            .build();
    }

    public record ErrorResponse(
        String code,
        String message,
        List<String> details
    ) {}
} 
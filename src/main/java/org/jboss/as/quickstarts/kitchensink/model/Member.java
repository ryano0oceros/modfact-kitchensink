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
package org.jboss.as.quickstarts.kitchensink.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.bson.types.ObjectId;

@MongoEntity(collection = "members")
public record Member(
    ObjectId id,

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 50)
    String name,

    @NotNull
    @NotEmpty
    @Pattern(regexp = "[^@]+@[^@]+\\.[^@]+", message = "Invalid email format")
    String email,

    @NotNull
    @NotEmpty
    @Size(min = 10, max = 12)
    @Digits(fraction = 0, integer = 12, message = "Invalid phone number")
    String phoneNumber
) {
    // Constructor with validation
    public Member {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (email == null || !email.matches("[^@]+@[^@]+\\.[^@]+")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (phoneNumber == null || !phoneNumber.matches("\\d{10,12}")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }

    // Factory method for creating a new member
    public static Member createMember(String name, String email, String phoneNumber) {
        return new Member(null, name, email, phoneNumber);
    }

    // Utility method to create a copy with a new ID
    public Member withId(ObjectId newId) {
        return new Member(newId, name(), email(), phoneNumber());
    }
}

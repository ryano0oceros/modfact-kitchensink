package org.jboss.as.quickstarts.kitchensink.rest;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class MemberResourceTest {

    @Inject
    MemberService memberService;

    @Test
    public void testCreateMember() throws Exception {
        Member member = Member.createMember(
            "John Doe",
            "john@example.com",
            "1234567890"
        );

        given()
            .contentType("application/json")
            .body(member)
            .when()
            .post("/api/members")
            .then()
            .statusCode(201)
            .body("name", is("John Doe"))
            .body("email", is("john@example.com"))
            .body("phoneNumber", is("1234567890"))
            .body("id", notNullValue());
    }

    @Test
    public void testGetMember() throws Exception {
        Member member = Member.createMember(
            "Jane Doe",
            "jane@example.com",
            "0987654321"
        );

        memberService.register(member);
        String id = member.id().toString();

        given()
            .when()
            .get("/api/members/" + id)
            .then()
            .statusCode(200)
            .body("name", is("Jane Doe"))
            .body("email", is("jane@example.com"))
            .body("phoneNumber", is("0987654321"));
    }

    @Test
    public void testListMembers() throws Exception {
        Member member1 = Member.createMember(
            "Test User 1",
            "test1@example.com",
            "1111111111"
        );

        Member member2 = Member.createMember(
            "Test User 2",
            "test2@example.com",
            "2222222222"
        );

        memberService.register(member1);
        memberService.register(member2);

        given()
            .when()
            .get("/api/members")
            .then()
            .statusCode(200)
            .body("size()", is(2));
    }
} 
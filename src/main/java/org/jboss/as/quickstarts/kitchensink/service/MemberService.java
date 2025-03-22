package org.jboss.as.quickstarts.kitchensink.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.jboss.as.quickstarts.kitchensink.model.Member;

import java.util.List;

@ApplicationScoped
public class MemberService {

    @Transactional
    public void register(Member member) throws Exception {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }

        try {
            member.persist();
        } catch (ConstraintViolationException e) {
            throw new Exception("Invalid member data", e);
        }
    }

    public List<Member> findAllMembers() {
        return Member.listAll();
    }

    public Member findById(String id) {
        return Member.findById(id);
    }

    public Member findByEmail(String email) {
        return Member.find("email", email).firstResult();
    }
} 
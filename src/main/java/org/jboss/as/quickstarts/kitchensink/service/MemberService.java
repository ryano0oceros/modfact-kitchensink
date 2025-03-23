package org.jboss.as.quickstarts.kitchensink.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.bson.types.ObjectId;
import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;

import java.util.List;

@ApplicationScoped
public class MemberService {

    @Inject
    MemberRepository repository;

    @Transactional
    public void register(Member member) throws Exception {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }

        try {
            repository.persist(member);
        } catch (ConstraintViolationException e) {
            throw new Exception("Invalid member data", e);
        }
    }

    public List<Member> findAllMembers() {
        return repository.listAll();
    }

    public Member findById(String id) {
        return repository.findById(new ObjectId(id));
    }

    public Member findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public long count() {
        return repository.count();
    }
} 
package org.jboss.as.quickstarts.kitchensink.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.bson.types.ObjectId;
import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MemberService {

    private final MemberRepository repository;

    @Inject
    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Member register(Member member) {
        return Optional.ofNullable(member)
            .map(m -> {
                try {
                    repository.persist(m);
                    return m;
                } catch (ConstraintViolationException e) {
                    throw new IllegalArgumentException("Invalid member data: " + e.getMessage(), e);
                }
            })
            .orElseThrow(() -> new IllegalArgumentException("Member cannot be null"));
    }

    public List<Member> findAllMembers() {
        return repository.listAll();
    }

    public Optional<Member> findById(String id) {
        try {
            return Optional.ofNullable(repository.findById(new ObjectId(id)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Optional<Member> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public long count() {
        return repository.count();
    }
} 
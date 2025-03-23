package org.jboss.as.quickstarts.kitchensink.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.bson.types.ObjectId;
import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class MemberService {

    private final MemberRepository repository;
    private final MeterRegistry registry;
    private final Timer findAllTimer;
    private final Timer registerTimer;

    @Inject
    public MemberService(MemberRepository repository, MeterRegistry registry) {
        this.repository = repository;
        this.registry = registry;
        
        // Initialize timers
        this.findAllTimer = Timer.builder("member.findAll")
            .description("Time taken to find all members")
            .register(registry);
        this.registerTimer = Timer.builder("member.register")
            .description("Time taken to register a new member")
            .register(registry);
            
        // Initialize counters
        registry.counter("member.total").increment(0); // Initialize the counter
    }

    @Transactional
    public Member register(Member member) {
        long start = System.nanoTime();
        try {
            var result = Optional.ofNullable(member)
                .map(m -> {
                    try {
                        repository.persist(m);
                        registry.counter("member.total").increment();
                        return m;
                    } catch (ConstraintViolationException e) {
                        registry.counter("member.registration.error", "type", "validation").increment();
                        throw new IllegalArgumentException("Invalid member data: " + e.getMessage(), e);
                    }
                })
                .orElseThrow(() -> {
                    registry.counter("member.registration.error", "type", "null").increment();
                    return new IllegalArgumentException("Member cannot be null");
                });
            registry.counter("member.registration.success").increment();
            return result;
        } catch (Exception e) {
            registry.counter("member.registration.error", "type", "unexpected").increment();
            throw e;
        } finally {
            registerTimer.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        }
    }

    public List<Member> findAllMembers() {
        long start = System.nanoTime();
        try {
            var members = repository.listAll();
            registry.counter("member.findAll.success").increment();
            return members;
        } catch (Exception e) {
            registry.counter("member.findAll.error").increment();
            throw e;
        } finally {
            findAllTimer.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        }
    }

    public Optional<Member> findById(String id) {
        try {
            var member = Optional.ofNullable(repository.findById(new ObjectId(id)));
            registry.counter("member.findById.success").increment();
            return member;
        } catch (IllegalArgumentException e) {
            registry.counter("member.findById.error", "type", "invalid_id").increment();
            return Optional.empty();
        } catch (Exception e) {
            registry.counter("member.findById.error", "type", "unexpected").increment();
            throw e;
        }
    }

    public Optional<Member> findByEmail(String email) {
        try {
            var member = repository.findByEmail(email);
            registry.counter("member.findByEmail.success").increment();
            return member;
        } catch (Exception e) {
            registry.counter("member.findByEmail.error").increment();
            throw e;
        }
    }

    public long count() {
        try {
            var count = repository.count();
            registry.gauge("member.count", count);
            return count;
        } catch (Exception e) {
            registry.counter("member.count.error").increment();
            throw e;
        }
    }
} 
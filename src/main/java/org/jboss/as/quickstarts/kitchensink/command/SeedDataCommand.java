package org.jboss.as.quickstarts.kitchensink.command;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;

@QuarkusMain
@ApplicationScoped
public class SeedDataCommand implements QuarkusApplication {

    @Inject
    MemberRegistration memberRegistration;

    @Inject
    MemberService memberService;

    @Override
    public int run(String... args) throws Exception {
        // Check if we already have data
        if (memberService.count() == 0) {
            // Create seed data
            Member seedMember = new Member();
            seedMember.setName("John Smith");
            seedMember.setEmail("john.smith@mailinator.com");
            seedMember.setPhoneNumber("2125551212");
            
            memberRegistration.register(seedMember);
            System.out.println("Seed data loaded successfully");
        } else {
            System.out.println("Database already contains data, skipping seed data");
        }
        
        // Keep the application running
        Thread.currentThread().join();
        return 0;
    }
} 
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
package org.jboss.as.quickstarts.kitchensink.service;

import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.logging.Logger;

@ApplicationScoped
public class MemberRegistration {

    private final Logger log;
    private final Event<Member> memberEventSrc;
    private final MemberRepository repository;

    @Inject
    public MemberRegistration(Logger log, Event<Member> memberEventSrc, MemberRepository repository) {
        this.log = log;
        this.memberEventSrc = memberEventSrc;
        this.repository = repository;
    }

    @Transactional
    public void register(Member member) throws Exception {
        log.info("Registering " + member.name());
        repository.persist(member);
        memberEventSrc.fire(member);
    }
}

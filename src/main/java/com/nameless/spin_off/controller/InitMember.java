package com.nameless.spin_off.controller;

import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitMember {

    private final InitMemberService initMemberService;

    @PostConstruct
    public void init() {
        initMemberService.init();
    }

    @Component
    static class InitMemberService {

        @PersistenceContext
        EntityManager em;

        @Transactional
        public void init() {
            Member member1 = Member.createMember("", "", "a", LocalDateTime.now(), "", "");
            Member member2 = Member.createMember("", "", "b", LocalDateTime.now(), "", "");
            Member member3 = Member.createMember("", "", "c", LocalDateTime.now(), "", "");
            Member member4 = Member.createMember("", "", "d", LocalDateTime.now(), "", "");
            Member member5 = Member.createMember("", "", "e", LocalDateTime.now(), "", "");

            em.persist(member1);
            em.persist(member2);
            em.persist(member3);
            em.persist(member4);
            em.persist(member5);

            for (int i = 0; i < 100; i++) {
                Member selectedMember;
                if(i % 5 == 0)
                    selectedMember = member1;
                else if(i % 5 == 1)
                    selectedMember = member2;
                else if(i % 5 == 2)
                    selectedMember = member3;
                else if(i % 5 == 3)
                    selectedMember = member4;
                else
                    selectedMember = member5;
                Post post = Post.createPost(selectedMember, "" + i, "" + i, new ArrayList<>(), new ArrayList<>(), PostPublicStatus.PUBLIC);
                em.persist(post);
            }
        }
    }
}
package com.nameless.spin_off.controller;

import com.nameless.spin_off.dto.MemberDto;
import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
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

            Member member1 = Member.buildMember()
                    .setAccountId("1")
                    .setAccountPw("1")
                    .setNickname("SooAh")
                    .setName("a")
                    .setBirth(LocalDateTime.now())
                    .setPhoneNumber("010-3959-3232")
                    .setEmail("@google.com")
                    .build();

            Member member2 = Member.buildMember()
                    .setAccountId("2")
                    .setAccountPw("2")
                    .setNickname("HyeongYoon")
                    .setName("b")
                    .setBirth(LocalDateTime.now())
                    .setPhoneNumber("010-3959-2522")
                    .setEmail("@naver.com")
                    .build();

            Member member3 = Member.buildMember()
                    .setAccountId("3")
                    .setAccountPw("3")
                    .setNickname("GoldSky")
                    .setName("c")
                    .setBirth(LocalDateTime.now())
                    .setPhoneNumber("010-3959-7676")
                    .setEmail("@kakao.com")
                    .build();
            Member member4 = Member.buildMember()
                    .setAccountId("4")
                    .setAccountPw("4")
                    .setNickname("Toongri")
                    .setName("d")
                    .setBirth(LocalDateTime.now())
                    .setPhoneNumber("010-3959-5195")
                    .setEmail("@hotmail.com")
                    .build();
            Member member5 = Member.buildMember()
                    .setAccountId("5")
                    .setAccountPw("5")
                    .setName("e")
                    .setBirth(LocalDateTime.now())
                    .setPhoneNumber("010-r334-ng")
                    .setEmail("@nethian.com")
                    .build();

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
                Post post = Post.buildPost()
                        .setMember(selectedMember)
                        .setTitle("" + i)
                        .setContent("" + i)
                        .build();
                em.persist(Movie.createMovie((long) i, "" + i, "" + i));
                em.persist(post);
            }

        }
    }
}
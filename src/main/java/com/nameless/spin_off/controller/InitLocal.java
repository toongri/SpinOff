package com.nameless.spin_off.controller;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.collections.CollectedPost;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.movie.PostedMovie;
import com.nameless.spin_off.entity.post.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitLocal {

    private final InitLocalService initLocalService;

    @PostConstruct
    public void init() {
        initLocalService.init();
    }

    @Component
    static class InitLocalService {

        @PersistenceContext
        EntityManager em;

        @Transactional
        public void init() {

            Member member1 = Member.buildMember()
                    .setAccountId("1")
                    .setAccountPw("1")
                    .setNickname("SooAh")
                    .setName("a")
                    .setBirth(LocalDate.now())
                    .setPhoneNumber("010-3959-3232")
                    .setEmail("@google.com")
                    .build();

            Member member2 = Member.buildMember()
                    .setAccountId("2")
                    .setAccountPw("2")
                    .setNickname("HyeongYoon")
                    .setName("b")
                    .setBirth(LocalDate.now())
                    .setPhoneNumber("010-3959-2522")
                    .setEmail("@naver.com")
                    .build();

            Member member3 = Member.buildMember()
                    .setAccountId("3")
                    .setAccountPw("3")
                    .setNickname("GoldSky")
                    .setName("c")
                    .setBirth(LocalDate.now())
                    .setPhoneNumber("010-3959-7676")
                    .setEmail("@kakao.com")
                    .build();
            Member member4 = Member.buildMember()
                    .setAccountId("4")
                    .setAccountPw("4")
                    .setNickname("Toongri")
                    .setName("d")
                    .setBirth(LocalDate.now())
                    .setPhoneNumber("010-3959-5195")
                    .setEmail("@hotmail.com")
                    .build();
            Member member5 = Member.buildMember()
                    .setAccountId("5")
                    .setAccountPw("5")
                    .setName("e")
                    .setBirth(LocalDate.now())
                    .setPhoneNumber("010-r334-ng")
                    .setEmail("@nethian.com")
                    .build();
            Collection collection1 = Collection.createDefaultCollection(member1);
            Collection collection2 = Collection.createDefaultCollection(member2);
            Collection collection3 = Collection.createDefaultCollection(member3);
            Collection collection4 = Collection.createDefaultCollection(member4);
            Collection collection5 = Collection.createDefaultCollection(member5);

            em.persist(member1);
            em.persist(collection1);
            em.persist(member2);
            em.persist(collection2);
            em.persist(member3);
            em.persist(collection3);
            em.persist(member4);
            em.persist(collection4);
            em.persist(member5);
            em.persist(collection5);

            for (int i = 0; i < 100; i++) {
                Member selectedMember;
                Collection selectedCollection;
                if (i % 5 == 0) {
                    selectedMember = member1;
                    selectedCollection = collection1;
                } else if (i % 5 == 1) {
                    selectedMember = member2;
                    selectedCollection = collection2;
                } else if (i % 5 == 2) {
                    selectedMember = member3;
                    selectedCollection = collection3;
                } else if (i % 5 == 3) {
                    selectedMember = member4;
                    selectedCollection = collection4;
                } else{
                    selectedMember = member5;
                    selectedCollection = collection5;
                }
                PostDto.PostBuilder postBuilder = Post.buildPost()
                        .setMember(selectedMember)
                        .setTitle("" + i)
                        .setContent("" + i);

                Movie movie = Movie.createMovie((long) i, "" + i, "" + i);
                em.persist(movie);

                PostedMovie postedMovie = PostedMovie.createPostedMovie(movie);

                Post post = postBuilder.build();
                if (i % 4 == 0) {
                    selectedCollection.addCollectedPostByPost(post);
                }
                em.persist(selectedCollection);
                em.persist(post);
            }
        }
    }
}
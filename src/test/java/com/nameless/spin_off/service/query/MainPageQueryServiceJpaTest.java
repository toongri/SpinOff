package com.nameless.spin_off.service.query;

import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class MainPageQueryServiceJpaTest {

    @Autowired MainPageQueryService mainPageQueryService;
    @Autowired MemberRepository memberRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired CollectionService collectionService;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired PostRepository postRepository;
    @Autowired EntityManager em;
    @Autowired MovieRepository movieRepository;
    @Autowired MemberService memberService;

}
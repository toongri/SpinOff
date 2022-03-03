package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.movie.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
public class PostQueryRepositoryTest {

    @Autowired
    PostRepository postRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired CollectionService collectionService;
    @Autowired MovieRepository movieRepository;
    @Autowired MovieService movieService;
    @Autowired MovieQueryRepository movieQueryRepository;
    @Autowired MemberService memberService;
    @Autowired MemberQueryRepository memberQueryRepository;
    @Autowired PostQueryRepository postQueryRepository;
    @Autowired EntityManager em;

}

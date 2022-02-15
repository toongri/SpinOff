package com.nameless.spin_off.repository.post;

import com.nameless.spin_off.entity.collections.CollectedPost;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.collections.CollectedPostRepository;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.PostQueryRepository;
import com.nameless.spin_off.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class PostRepositoryTest {

    @Autowired PostQueryRepository postQueryRepository;
    @Autowired PostRepository postRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired CollectedPostRepository collectedPostRepository;
    @Autowired
    HashtagRepository hashtagRepository;
    @Autowired PostService postService;

    @Test
    public void 컬렉션추가_조회확인() throws Exception{

        //given

        //when

        List<Collection> collections = collectionRepository.findAll();
        List<Post> posts = postRepository.findAll();
        for (Post post : posts) {
            for (Collection collection : collections) {
                int i = (int) (Math.random() * 10);

                if (i % 3 == 0) {
                    collection.addCollectedPostByPost(post);
                }
            }
        }
        //then
        for (Post post : posts) {
            List<CollectedPost> collectedPosts = collectedPostRepository.findAllByPost(post);

            assertThat(post.getCollectionCount()).isEqualTo(collectedPosts.size());

        }
    }
}

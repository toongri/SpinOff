package com.nameless.spin_off.repository.post;

import com.nameless.spin_off.entity.collection.CollectedPost;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.collection.CollectedPostRepository;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.nameless.spin_off.enums.post.PostScoreEnum.POST_COLLECT;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class PostRepositoryTest {

    @Autowired PostRepository postRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired CollectedPostRepository collectedPostRepository;

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
                    post.insertCollectedPostByCollections(List.of(collection));
                }
            }
        }
        //then
        for (Post post : posts) {
            List<CollectedPost> collectedPosts = collectedPostRepository.findAllByPost(post);

            assertThat(post.getPopularity())
                    .isEqualTo(collectedPosts.size() * POST_COLLECT.getLatestScore() * POST_COLLECT.getRate());

        }
    }
}

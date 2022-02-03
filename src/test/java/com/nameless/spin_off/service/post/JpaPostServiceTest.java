package com.nameless.spin_off.service.post;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.collections.CollectedPost;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.post.*;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.post.PostedMediaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JpaPostServiceTest {

    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired PostedMediaRepository postedMediaRepository;
    @Autowired CollectionRepository collectionRepository;

    @Test
    public void 포스트_생성_테스트() throws Exception{
        //given
        PostDto.CreatePostVO createPostVO = new PostDto.CreatePostVO(1L,
                "알라리숑", "얄라리얄라", PublicOfPostStatus.PUBLIC,
                List.of("abc", "bdc"), List.of("형윤이", "형윤이?"), List.of(), List.of(5L, 12L, 15L));

        List<Post> preparePosts = postRepository.findAll();
        List<PostedMedia> preparePostedMedias = postedMediaRepository.findAll();
        List<Hashtag> prepareHashtags = hashtagRepository.findAll();
        List<Collection> prepareCollections = collectionRepository.findAllById(List.of(5L, 12L, 15L));


        int preparePostsSize = preparePosts.size();
        int preparePostedMediasSize = preparePostedMedias.size();
        int prepareHashtagsSize = prepareHashtags.size();
        List<CollectedPost> prepareCollectedPosts1 = prepareCollections.get(0).getCollectedPosts();
        List<CollectedPost> prepareCollectedPosts2 = prepareCollections.get(1).getCollectedPosts();
        List<CollectedPost> prepareCollectedPosts3 = prepareCollections.get(2).getCollectedPosts();

        int prepareCollectedPosts1Size = prepareCollectedPosts1.size();
        int prepareCollectedPosts2Size = prepareCollectedPosts2.size();
        int prepareCollectedPosts3Size = prepareCollectedPosts3.size();
        //when
        Long aLong = postService.savePostByPostVO(createPostVO);
        Post post1 = postRepository.findById(aLong).orElseThrow(Exception::new);

        Long postCollectionCount = post1.getCollectionCount();
        List<PostedHashtag> postedHashtags = post1.getPostedHashtags();
        int postPostedHashtagSize = postedHashtags.size();

        List<Post> postPosts = postRepository.findAll();
        List<PostedMedia> postPostedMedias = postedMediaRepository.findAll();
        List<Hashtag> postHashtags = hashtagRepository.findAll();
        List<Collection> postCollections = collectionRepository.findAllById(List.of(5L, 12L, 15L));

        int postPostsSize = postPosts.size();
        int postPostedMediasSize = postPostedMedias.size();
        int postHashtagsSize = postHashtags.size();
        List<CollectedPost> postCollectedPosts1 = postCollections.get(0).getCollectedPosts();
        List<CollectedPost> postCollectedPosts2 = postCollections.get(1).getCollectedPosts();
        List<CollectedPost> postCollectedPosts3 = postCollections.get(2).getCollectedPosts();

        int postCollectedPosts1Size = postCollectedPosts1.size();
        int postCollectedPosts2Size = postCollectedPosts2.size();
        int postCollectedPosts3Size = postCollectedPosts3.size();

        //then

        Assertions.assertThat(preparePostsSize).isEqualTo(postPostsSize - 1);
        Assertions.assertThat(preparePostedMediasSize).isEqualTo(postPostedMediasSize - 2);
        Assertions.assertThat(prepareHashtagsSize).isEqualTo(postHashtagsSize - 2);
        Assertions.assertThat(prepareCollectedPosts1Size).isEqualTo(postCollectedPosts1Size - 1);
        Assertions.assertThat(prepareCollectedPosts2Size).isEqualTo(postCollectedPosts2Size - 1);
        Assertions.assertThat(prepareCollectedPosts3Size).isEqualTo(postCollectedPosts3Size - 1);
        Assertions.assertThat(postCollectionCount).isEqualTo(3);
        Assertions.assertThat(postPostedHashtagSize).isEqualTo(2);

    }
}
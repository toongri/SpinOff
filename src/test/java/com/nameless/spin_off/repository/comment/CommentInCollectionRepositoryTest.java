package com.nameless.spin_off.repository.comment;

import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.entity.collections.CollectedPost;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.collections.CollectedPostRepository;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.post.query.PostQueryRepository;
import com.nameless.spin_off.service.comment.CommentInCollectionService;
import com.nameless.spin_off.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentInCollectionRepositoryTest {
    @Autowired PostQueryRepository postQueryRepository;
    @Autowired PostRepository postRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired CollectedPostRepository collectedPostRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired PostService postService;
    @Autowired CommentInCollectionRepository commentInCollectionRepository;
    @Autowired EntityManager em;
    @Autowired CommentInCollectionService commentInCollectionService;


    @Test
    public void 대댓글_지연로딩_테스트() throws Exception{
        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        Collection collection = Collection.createDefaultCollection(member);
        collectionRepository.save(collection);
        CommentInCollection parentComment = CommentInCollection.createCommentInCollection(member, "야스히로 라할살", null);
        collection.addCommentInCollection(parentComment);
        commentInCollectionRepository.save(parentComment);
        CommentInCollection childComment1 = commentInCollectionService.saveCommentInCollectionByCommentVO(new CommentDto.CreateCommentInCollectionVO(member.getId(), collection.getId(), parentComment.getId(), "요지스타 라할살"));
        CommentInCollection childComment2 = commentInCollectionService.saveCommentInCollectionByCommentVO(new CommentDto.CreateCommentInCollectionVO(member.getId(), collection.getId(), parentComment.getId(), "슈퍼스타검흰 라할살"));

        //when
        em.flush();
        em.clear();
        CommentInCollection comments = commentInCollectionRepository.findParentByCollectionIncludeChildrenOrderByDesc(collection).get(0);

        //then
        assertThat(comments.getChildren().get(0).getClass()).isEqualTo(CommentInCollection.class);

    }

}
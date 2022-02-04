package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.dto.CommentDto.CreateCommentInCollectionVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInCollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.LikedPostRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.post.PostedMediaRepository;
import com.nameless.spin_off.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class JpaCommentInCollectionServiceTest {
    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired PostedMediaRepository postedMediaRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired LikedPostRepository likedPostRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CommentInCollectionRepository commentInCollectionRepository;
    @Autowired CommentInCollectionService commentInCollectionService;

    @Test
    public void 컬렉션_댓글_체크() throws Exception{
        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        Collection collection = Collection.createDefaultCollection(member);
        collectionRepository.save(collection);

        //when
        CommentInCollection comment = commentInCollectionService.saveCommentInCollectionByCommentVO(new CreateCommentInCollectionVO(member.getId(), collection.getId(), null, "야스히로 라할살"));

        //then
        assertThat(collection.getCommentCount()).isEqualTo(collection.getCommentInCollections().size());
        assertThat(collection.getCommentInCollections().get(collection.getCommentInCollections().size() - 1)).isEqualTo(comment);
    }

    @Test
    public void 컬렉션_대댓글_체크() throws Exception{
        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        Collection collection = Collection.createDefaultCollection(member);
        collectionRepository.save(collection);
        CommentInCollection parentComment = CommentInCollection.createCommentInCollection(member, "야스히로 라할살", null);
        collection.addCommentInCollection(parentComment);
        commentInCollectionRepository.save(parentComment);

        //when
        CommentInCollection childComment1 = commentInCollectionService.saveCommentInCollectionByCommentVO(new CreateCommentInCollectionVO(member.getId(), collection.getId(), parentComment.getId(), "요지스타 라할살"));
        CommentInCollection childComment2 = commentInCollectionService.saveCommentInCollectionByCommentVO(new CreateCommentInCollectionVO(member.getId(), collection.getId(), parentComment.getId(), "슈퍼스타검흰 라할살"));

        //then
        assertThat(collection.getCommentCount()).isEqualTo(collection.getCommentInCollections().size());
        assertThat(collection.getCommentInCollections().size()).isEqualTo(3);
        assertThat(parentComment.getChildren().size()).isEqualTo(2);
        assertThat(parentComment.getChildren().get(0)).isEqualTo(childComment1);
        assertThat(parentComment.getChildren().get(1)).isEqualTo(childComment2);
        assertThat(childComment1.getParent()).isEqualTo(parentComment);
        assertThat(childComment2.getParent()).isEqualTo(parentComment);

    }
}
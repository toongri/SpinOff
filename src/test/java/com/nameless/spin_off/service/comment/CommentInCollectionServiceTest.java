package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.StaticVariable;
import com.nameless.spin_off.dto.CommentDto.CreateCommentInCollectionVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInCollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.post.LikedPostRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.post.PostedMediaRepository;
import com.nameless.spin_off.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.nameless.spin_off.StaticVariable.COLLECTION_COMMENT_COUNT_SCORES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class CommentInCollectionServiceTest {

    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired PostedMediaRepository postedMediaRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired LikedPostRepository likedPostRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CommentInCollectionRepository commentInCollectionRepository;
    @Autowired CommentInCollectionService commentInCollectionService;
    @Autowired EntityManager em;

    @Test
    public void saveCommentInCollectionByCommentVO() throws Exception{

        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        Collection collection = Collection.createDefaultCollection(member);
        collectionRepository.save(collection);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        CommentInCollection comment = commentInCollectionRepository.getById(commentInCollectionService.insertCommentInCollectionByCommentVO(new CreateCommentInCollectionVO(member.getId(), collection.getId(), null, "야스히로 라할살")));
        System.out.println("컬렉션업로드");
        Collection newCollection = collectionRepository.getById(collection.getId());

        //then
        assertThat(newCollection.getCommentScore()).isEqualTo(newCollection.getCommentInCollections().size());
        assertThat(newCollection.getCommentInCollections().get(newCollection.getCommentInCollections().size() - 1)).isEqualTo(comment);
    }

    @Test
    public void 대댓글_테스트() throws Exception{
        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);
        CommentInCollection parent = CommentInCollection.createCommentInCollection(mem, "야스히로 라할살", null);
        col.addCommentInCollection(parent);
        commentInCollectionRepository.save(parent);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수1");
        CommentInCollection childComment1 = commentInCollectionRepository.getById(commentInCollectionService.insertCommentInCollectionByCommentVO(new CreateCommentInCollectionVO(mem.getId(), col.getId(), parent.getId(), "요지스타 라할살")));

        System.out.println("서비스함수2");
        CommentInCollection childComment2 = commentInCollectionRepository.getById(commentInCollectionService.insertCommentInCollectionByCommentVO(new CreateCommentInCollectionVO(mem.getId(), col.getId(), parent.getId(), "슈퍼스타검흰 라할살")));

        System.out.println("부모댓글업로드");
        CommentInCollection parentComment = commentInCollectionRepository.findById(parent.getId()).get();

        System.out.println("컬렉션업로드");
        Collection collection = collectionRepository.getById(col.getId());

        //then
        assertThat(collection.getCommentScore()).isEqualTo(collection.getCommentInCollections().size());
        assertThat(collection.getCommentInCollections().size()).isEqualTo(3);
        assertThat(parentComment.getChildren().size()).isEqualTo(2);
        assertThat(parentComment.getChildren().get(0)).isEqualTo(childComment1);
        assertThat(parentComment.getChildren().get(1)).isEqualTo(childComment2);
        assertThat(childComment1.getParent()).isEqualTo(parentComment);
        assertThat(childComment2.getParent()).isEqualTo(parentComment);
        assertThat(collection.getCommentScore()).isEqualTo(collection.getPopularity());
        assertThat(collection.getCommentScore())
                .isEqualTo(collection.getCommentInCollections().size() * COLLECTION_COMMENT_COUNT_SCORES.get(0));
    }

    @Test
    public void 댓글_저장시_파라미터_예외처리() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);

        CreateCommentInCollectionVO commentInCollectionVO1 =
                new CreateCommentInCollectionVO(0L, 0L, 0L, "");

        CreateCommentInCollectionVO commentInCollectionVO2 =
                new CreateCommentInCollectionVO(mem.getId(), 0L, 0L, "");

        CreateCommentInCollectionVO commentInCollectionVO3 =
                new CreateCommentInCollectionVO(mem.getId(), col.getId(), 0L, "");

        //when

        //then
        assertThatThrownBy(() -> commentInCollectionService.insertCommentInCollectionByCommentVO(commentInCollectionVO1))
                .isInstanceOf(NotExistMemberException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> commentInCollectionService.insertCommentInCollectionByCommentVO(commentInCollectionVO2))
                .isInstanceOf(NotExistCollectionException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> commentInCollectionService.insertCommentInCollectionByCommentVO(commentInCollectionVO3))
                .isInstanceOf(NotExistCommentInCollectionException.class);//.hasMessageContaining("")

    }

    @Test
    public void 좋아요_테스트() throws Exception{
        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);
        col.addCommentInCollection(CommentInCollection.createCommentInCollection(mem, " ", null));

        em.flush();
        em.clear();

        //when

        System.out.println("서비스함수");
        commentInCollectionService.insertLikedCommentByMemberId(member.getId(), col.getCommentInCollections().get(0).getId());

        System.out.println("댓글함수");
        CommentInCollection comment = commentInCollectionRepository.getById(col.getCommentInCollections().get(0).getId());

        //then
        assertThat(comment.getId()).isEqualTo(col.getCommentInCollections().get(0).getId());
        assertThat(comment.getCollection().getId()).isEqualTo(col.getId());
        assertThat(comment.getLikedCommentInCollections().size()).isEqualTo(1);
        assertThat(comment.getLikedCommentInCollections().get(0).getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    public void 좋아요_테스트_예외처리() throws Exception{
        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);
        col.addCommentInCollection(CommentInCollection.createCommentInCollection(mem, " ", null));

        em.flush();
        em.clear();

        //when

        System.out.println("서비스함수");
        commentInCollectionService.insertLikedCommentByMemberId(member.getId(), col.getCommentInCollections().get(0).getId());

        System.out.println("댓글함수");
        CommentInCollection comment = commentInCollectionRepository.getById(col.getCommentInCollections().get(0).getId());

        //then

        assertThatThrownBy(() -> commentInCollectionService.insertLikedCommentByMemberId(-1L, col.getCommentInCollections().get(0).getId()))
                .isInstanceOf(NotExistMemberException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> commentInCollectionService.insertLikedCommentByMemberId(member.getId(), -1L))
                .isInstanceOf(NotExistCommentInCollectionException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> commentInCollectionService.insertLikedCommentByMemberId(member.getId(), col.getCommentInCollections().get(0).getId()))
                .isInstanceOf(AlreadyLikedCommentInCollectionException.class);//.hasMessageContaining("")

    }

}
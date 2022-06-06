package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CreateCommentInCollectionVO;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInCollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.LikedPostRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.post.PostedMediaRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static com.nameless.spin_off.enums.collection.CollectionScoreEnum.COLLECTION_COMMENT;
import static com.nameless.spin_off.enums.collection.CollectionScoreEnum.COLLECTION_VIEW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class CommentInCollectionServiceJpaTest {

    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired PostedMediaRepository postedMediaRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired LikedPostRepository likedPostRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CommentInCollectionRepository commentInCollectionRepository;
    @Autowired CommentInCollectionService commentInCollectionService;
    @Autowired CollectionService collectionService;
    @Autowired EntityManager em;
    @Autowired MemberService memberService;

    @Test
    public void saveCommentInCollectionByCommentVO() throws Exception{

        //given
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(member);
        Collection collection = Collection.createDefaultCollection(member);
        collectionRepository.save(collection);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        CommentInCollection comment = commentInCollectionRepository
                .getById(
                        commentInCollectionService.insertCommentInCollectionByCommentVO(
                                new CreateCommentInCollectionVO(
                                        null, "야스히로 라할살"), member.getId(), collection.getId()));
        System.out.println("컬렉션업로드");
        collectionService.insertViewedCollectionByIp("22", collection.getId());
        collectionService.updateAllPopularity();
        Collection newCollection = collectionRepository.getById(collection.getId());

        //then
        assertThat(newCollection.getPopularity())
                .isEqualTo(COLLECTION_VIEW.getRate() * COLLECTION_VIEW.getLatestScore() +
                        newCollection.getCommentInCollections().size() *
                        COLLECTION_COMMENT.getRate() * COLLECTION_COMMENT.getLatestScore());
        assertThat(newCollection.getCommentInCollections().get(newCollection.getCommentInCollections().size() - 1)).isEqualTo(comment);
    }

    @Test
    public void 대댓글_테스트() throws Exception{
        //given
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);
        CommentInCollection parent = CommentInCollection.createCommentInCollection(mem, "야스히로 라할살", null, col);
        commentInCollectionRepository.save(parent);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수1");
        CommentInCollection childComment1 =
                commentInCollectionRepository.getById(
                        commentInCollectionService.insertCommentInCollectionByCommentVO(
                                new CreateCommentInCollectionVO(
                                        parent.getId(), "요지스타 라할살"), mem.getId(), col.getId()));

        System.out.println("서비스함수2");
        CommentInCollection childComment2 =
                commentInCollectionRepository.getById(
                        commentInCollectionService.insertCommentInCollectionByCommentVO(
                                new CreateCommentInCollectionVO(
                                        parent.getId(), "슈퍼스타검흰 라할살"), mem.getId(), col.getId()));

        em.flush();
        em.clear();

        System.out.println("부모댓글업로드");
        collectionService.insertViewedCollectionByIp("22", col.getId());
        collectionService.updateAllPopularity();
        CommentInCollection parentComment = commentInCollectionRepository.findById(parent.getId()).get();

        System.out.println("컬렉션업로드");
        Collection collection = collectionRepository.getById(col.getId());
        //then
        assertThat(collection.getPopularity())
                .isEqualTo(
                        COLLECTION_VIEW.getLatestScore() * COLLECTION_VIEW.getRate() +
                        COLLECTION_COMMENT.getLatestScore() * COLLECTION_COMMENT.getRate() *
                                collection.getCommentInCollections().size());
        assertThat(collection.getCommentInCollections().size()).isEqualTo(3);
        assertThat(parentComment.getChildren().size()).isEqualTo(2);
        assertThat(parentComment.getChildren().get(0).getId()).isEqualTo(childComment1.getId());
        assertThat(parentComment.getChildren().get(1).getId()).isEqualTo(childComment2.getId());
        assertThat(childComment1.getParent().getId()).isEqualTo(parentComment.getId());
        assertThat(childComment2.getParent().getId()).isEqualTo(parentComment.getId());
    }

    @Test
    public void 댓글_저장시_파라미터_예외처리() throws Exception{

        //given
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Collection col = Collection.createCollection(mem, "", "", PublicOfCollectionStatus.A);
        collectionRepository.save(col);
        Member mem2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem2);
        Collection col2 = Collection.createCollection(mem2, "", "", PublicOfCollectionStatus.A);
        collectionRepository.save(col2);

        CreateCommentInCollectionVO commentInCollectionVO1 =
                new CreateCommentInCollectionVO(-1L, "");

        CreateCommentInCollectionVO commentInCollectionVO2 =
                new CreateCommentInCollectionVO(0L, "");

        CreateCommentInCollectionVO commentInCollectionVO3 =
                new CreateCommentInCollectionVO(-1L, "");

        //when

        //then
        assertThatThrownBy(() -> commentInCollectionService
                .insertCommentInCollectionByCommentVO(commentInCollectionVO2, mem.getId(), 0L))
                .isInstanceOf(NotExistCollectionException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> commentInCollectionService
                .insertCommentInCollectionByCommentVO(commentInCollectionVO3, mem.getId(), col.getId()))
                .isInstanceOf(NotExistCommentInCollectionException.class);//.hasMessageContaining("")

        Long aLong = commentInCollectionService.insertCommentInCollectionByCommentVO(
                new CreateCommentInCollectionVO(null, ""), mem2.getId(), col.getId());
        memberService.insertBlockedMemberByMemberId(mem.getId(), mem2.getId(), BlockedMemberStatus.A);
        em.flush();
        assertThatThrownBy(() -> commentInCollectionService.insertCommentInCollectionByCommentVO(
                new CreateCommentInCollectionVO(null, ""), mem.getId(), col2.getId()))
                .isInstanceOf(DontHaveAuthorityException.class);//.hasMessageContaining("")

        assertThatThrownBy(() -> commentInCollectionService.insertCommentInCollectionByCommentVO(
                new CreateCommentInCollectionVO(aLong, ""), mem.getId(), col.getId()))
                .isInstanceOf(DontHaveAuthorityException.class);//.hasMessageContaining("")
    }

    @Test
    public void 좋아요_테스트() throws Exception{
        //given
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(member);
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Collection col = Collection.createCollection(mem, "", "", PublicOfCollectionStatus.A);
        collectionRepository.save(col);
        CommentInCollection commentInCollection =
                CommentInCollection.createCommentInCollection(mem, " ", null, col);

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
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(member);
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Collection col = Collection.createCollection(mem, "", "", PublicOfCollectionStatus.A);
        collectionRepository.save(col);
        CommentInCollection commentInCollection =
                CommentInCollection.createCommentInCollection(mem, " ", null, col);

        em.flush();
        em.clear();

        //when

        System.out.println("서비스함수");
        commentInCollectionService.insertLikedCommentByMemberId(member.getId(), col.getCommentInCollections().get(0).getId());

        System.out.println("댓글함수");
        CommentInCollection comment = commentInCollectionRepository.getById(col.getCommentInCollections().get(0).getId());

        //then
        assertThatThrownBy(() -> commentInCollectionService.insertLikedCommentByMemberId(member.getId(), -1L))
                .isInstanceOf(NotExistCommentInCollectionException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> commentInCollectionService.insertLikedCommentByMemberId(member.getId(), col.getCommentInCollections().get(0).getId()))
                .isInstanceOf(AlreadyLikedCommentInCollectionException.class);//.hasMessageContaining("")

        memberService.insertBlockedMemberByMemberId(member.getId(), mem.getId(), BlockedMemberStatus.A);
        em.flush();
        assertThatThrownBy(() -> commentInCollectionService.insertLikedCommentByMemberId(member.getId(), col.getCommentInCollections().get(0).getId()))
                .isInstanceOf(DontHaveAuthorityException.class);//.hasMessageContaining("")
    }

}
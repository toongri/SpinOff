package com.nameless.spin_off.service.hashtag;

import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.hashtag.NotExistHashtagException;
import com.nameless.spin_off.exception.member.AlreadyFollowedHashtagException;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static com.nameless.spin_off.enums.hashtag.HashtagScoreEnum.HASHTAG_FOLLOW;
import static com.nameless.spin_off.enums.hashtag.HashtagScoreEnum.HASHTAG_VIEW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class HashtagServiceJpaTest {

    @Autowired HashtagService hashtagService;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired EntityManager em;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 해시태그_조회수_증가() throws Exception{

        //given
        Hashtag hashtag = hashtagRepository.save(Hashtag.createHashtag(""));
        Hashtag hashtag2 = hashtagRepository.save(Hashtag.createHashtag(""));
        Hashtag hashtag3 = hashtagRepository.save(Hashtag.createHashtag(""));

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        for (int i = 0; i < 10; i++) {
            hashtagService.insertViewedHashtagByIp("" + i, hashtag.getId());
            hashtagService.insertViewedHashtagByIp("" + i % 2, hashtag2.getId());
            hashtagService.insertViewedHashtagByIp("" + i % 3, hashtag3.getId());
        }
        System.out.println("멤버함수");
        hashtagService.updateAllPopularity();
        em.flush();

        Hashtag tag = hashtagRepository.getById(hashtag.getId());
        Hashtag tag2 = hashtagRepository.getById(hashtag2.getId());
        Hashtag tag3 = hashtagRepository.getById(hashtag3.getId());

        //then
        assertThat(tag.getPopularity())
                .isEqualTo(tag.getViewedHashtagByIps().size() * HASHTAG_VIEW.getRate() * HASHTAG_VIEW.getLatestScore());
        assertThat(tag2.getPopularity())
                .isEqualTo(tag2.getViewedHashtagByIps().size() * HASHTAG_VIEW.getRate() * HASHTAG_VIEW.getLatestScore());
        assertThat(tag3.getPopularity())
                .isEqualTo(tag3.getViewedHashtagByIps().size() * HASHTAG_VIEW.getRate() * HASHTAG_VIEW.getLatestScore());
    }



    @Test
    public void 멤버_팔로우_해시태그() throws Exception{

        //given
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        Long memberId = memberRepository.save(member).getId();
        Hashtag hashtag = Hashtag.createHashtag("팔로우_해시태그");
        Long hashtagId = hashtagRepository.save(hashtag).getId();

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        hashtagService.insertFollowedHashtagByHashtagId(memberId, hashtagId);

        System.out.println("멤버함수");
        hashtagService.insertViewedHashtagByIp("33", hashtagId);
        em.flush();
        hashtagService.updateAllPopularity();
        em.flush();
        Member newMember = memberRepository.getById(memberId);
        Hashtag newHashtag = hashtagRepository.getById(hashtagId);
        //then
        assertThat(newMember.getId()).isEqualTo(memberId);
        assertThat(newMember.getFollowedHashtags().size()).isEqualTo(1);
        assertThat(newMember.getFollowedHashtags().iterator().next().getHashtag().getId()).isEqualTo(hashtagId);
        assertThat(newHashtag.getFollowingMembers().size()).isEqualTo(1);
        assertThat(newHashtag.getPopularity())
                .isEqualTo(HASHTAG_VIEW.getLatestScore() * HASHTAG_VIEW.getRate() +
                        HASHTAG_FOLLOW.getLatestScore() * HASHTAG_FOLLOW.getRate());
    }

    @Test
    public void 멤버_팔로우_해시태그_예외처리() throws Exception{

        //given

        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        Long memberId = memberRepository.save(member).getId();
        Hashtag hashtag = Hashtag.createHashtag("팔로우_해시태그");
        Long hashtagId = hashtagRepository.save(hashtag).getId();

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        Long aLong = hashtagService.insertFollowedHashtagByHashtagId(memberId, hashtagId);

        System.out.println("멤버함수");

        //then
        assertThatThrownBy(() -> hashtagService.insertFollowedHashtagByHashtagId(memberId, hashtagId))
                .isInstanceOf(AlreadyFollowedHashtagException.class);
        assertThatThrownBy(() -> hashtagService.insertFollowedHashtagByHashtagId(memberId, -1L))
                .isInstanceOf(NotExistHashtagException.class);
    }
}